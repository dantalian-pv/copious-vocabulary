"use strict";

$(document).ready(
		function() {

			var csrf = {};
			var csrf_name = $("meta[name='_csrf_header']").attr("content");
			csrf[csrf_name] = $("meta[name='_csrf']").attr("content");

			$('.languages').dropdown({
				apiSettings : {
					url : '/v1/api/langs?lang={query}',
					cache : 'none',
					onResponse: function(data) {
						var results = [];
						$.each(data, function( i, l ) {
							results[i] = {"name": l.text, "value": l.id};
						});
						return {"success": true, "results": results};
					}
				},
				fullTextSearch: true,
				filterRemoteData: true
			});
			
			$('.ui.checkbox').checkbox();
			
			var csrf = {};
			var csrf_name = $("meta[name='_csrf_header']").attr("content");
			csrf[csrf_name] = $("meta[name='_csrf']").attr("content");
			$('#import_voc_form').on('submit', function(env) {
				env.preventDefault();
				var form = $(this);
				var messageBox = form.children('.message');
				var data = new FormData(form[0]);
		    var url = form.attr('action');
		    
		    var showMessage = function(status, message, statusTitle) {
					if (status == 'success') {
						messageBox.removeClass('negative message').addClass('positive message');
						messageBox.children('div.header').text(
								(statusTitle) ? statusTitle : 'Success');
					} else if (status == 'error') {
						messageBox.removeClass('positive message').addClass('negative message');
						messageBox.children('div.header').text(
								(statusTitle) ? statusTitle : 'Error');
					} else {
						console.error('undefined status', status);
					}
					messageBox.children('p.info').text(message);
					messageBox.removeClass('hidden');
				}
		    
		    $.ajax({
		    	headers : csrf,
          type: "POST",
          url: url,
          data: data,
          contentType: 'multipart/form-data',
          processData: false,
          contentType: false,
					statusCode: {
						401: function() {
							window.location.replace(window.location.href);
						}
					},
          success: function(data) {
          	$('#import_voc').modal('hide');
          },
          fail: function(jqXHR, textStatus, errorThrown) {
          	if (jqXHR.responseJSON) {

							var json = jqXHR.responseJSON;
							
							if(json.status == 403 && json.message.indexOf('CSRF')) {
								window.location.replace(window.location.href);
							}

							var message = "Error code: " + jqXHR.status + " " + json.message;
							showMessage('error', message, json.error);

							if (json.fieldErrors && Array.isArray(json.fieldErrors)) {
								for ( var i in json.fieldErrors) {
									var fieldError = json.fieldErrors[i];
									showInputError(fieldError.field, fieldError.defaultMessage);
								}
							}
						} else {
							var message = "Error code: " + jqXHR.status + " "
									+ ((jqXHR.status == 0) ? "Lost connection" : jqXHR.statusText);
							showMessage('error', message);
						}
          }
        });
			});

			function ItemForm(data) {
				this.id = ko.observable(data.id);
				this.name = ko.observable(data.name)
				this.description = ko.observable(data.description);
				this.sourceId = ko.observable(data.sourceId);
				this.targetId = ko.observable(data.targetId);
				this.sourceName = ko.observable(data.sourceName);
				this.targetName = ko.observable(data.targetName);
			}

			function ItemGroupListViewModel() {
				// Data
				var self = this;

				self.deleteUrl = null;
				self.deleteInProgress = ko.observable(false);
				self.errorHeader = ko.observable();
				self.errorMessage = ko.observable();
				
				self.shared = ko.observable(document.vocabularyShared);
				
				self.sharedText = ko.computed(function() {
					return self.shared() ? "Shared" : "Share";
				});
				
				self.share = function() {
					var shared = self.shared() == true;
					$.ajax({
						url : '/v1/api/vocabularies/' + document.vocabularyId + '/_share?share=' + !shared,
						contentType : "application/json; charset=utf-8",
						method : 'PUT',
						headers : csrf,
						dataType : 'json',
						statusCode: {
							401: function() {
								window.location.replace(window.location.href);
							}
						}
					}).done(function() {
						self.shared(!shared);
					}).fail(function(xhr, type, message) {
						xhrErrorHandler(xhr);
						self.errorHeader(type);
						self.errorMessage(message);
					});
				}

				// Form Data
				self.itemForm = new Form({
					setItem : function(data) {
						this.id(data.id());
						this.name(data.name());
						this.description(data.description());
						this.sourceId(data.sourceId());
						this.targetId(data.targetId());
						this.sourceName(data.sourceName());
						this.targetName(data.targetName());
					},
					setEmpty : function(data) {
						this.id(null);
						this.name('');
						this.description('');
						this.sourceId('');
						this.targetId('');
						this.sourceName('');
						this.targetName('');
					},
					url : '/v1/api/vocabularies',
					formSelector : '#add_voc_form',
					modalSelector : '#add_voc',
					initItem : function() {
						this.id = ko.observable();
						this.name = ko.observable();
						this.description = ko.observable();
						this.sourceId = ko.observable();
						this.targetId = ko.observable();
						this.sourceName = ko.observable();
						this.targetName = ko.observable();
					}
				});

				// Operations
				self.showEditVoc = function() {
					$.getJSON("/v1/api/vocabularies/" + document.vocabularyId, function(itemForm) {
						self.itemForm.setItem(new ItemForm(itemForm));
						self.itemForm.show(function(data) {
							self.items.replace(self.selectedItem, new Item(data));
							$('#add_voc').modal('hide');
						});
					}).fail(xhrErrorHandler);
				};
				self.deleteItem = function() {
					$('.ui.basic.yesno.modal')
				  .modal({
				    closable  : false,
				    onDeny    : function(){
				    },
				    onApprove : function() {
				    	$.ajax({
								url : '/v1/api/vocabularies/' + document.vocabularyId,
								contentType : "application/json; charset=utf-8",
								method : 'DELETE',
								headers : csrf,
								dataType : 'json',
								statusCode: {
									401: function() {
										window.location.replace(window.location.href);
									}
								}
							}).done(function() {
								window.location.href = '/';
							}).fail(function(xhr, type, message) {
								xhrErrorHandler(xhr);
								self.errorHeader(type);
								self.errorMessage(message);
							});
				    }
				  })
				  .modal('show');
				};
				self.showImport = function() {
					$('#import_voc').modal('show');
				}

			}

			ko.applyBindings(new ItemGroupListViewModel());
		});
