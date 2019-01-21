"use strict";

$(document).ready(
		function() {

			var csrf = {};
			var csrf_name = $("meta[name='_csrf_header']").attr("content");
			csrf[csrf_name] = $("meta[name='_csrf']").attr("content");

			$('.languages').dropdown({
				apiSettings : {
					url : '/v1/api/langs',
					cache : 'none',
					onResponse: function(data) {
						var results = [];
						$.each(data, function( i, l ) {
							results[i] = {"name": l.text, "value": l.id};
						});
						return {"success": true, "results": results};
					}
				}
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
					});
				};
				self.showEditFields = function() {
					
				};
				self.showEditCards = function() {
					
				};
				self.showListCards = function() {
					
				};
				self.showTraining = function() {
					
				};

			}

			ko.applyBindings(new ItemGroupListViewModel());
		});
