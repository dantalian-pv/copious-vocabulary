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

			function Item(data) {
				this.id = ko.observable(data.id);
				this.link = ko.observable("/vocabularies/" + data.id)
				this.name = ko.observable(data.name)
				this.description = ko.observable(data.description);
				this.sourceId = ko.observable(data.sourceId);
				this.targetId = ko.observable(data.targetId);
				this.sourceName = ko.observable(data.sourceName);
				this.targetName = ko.observable(data.targetName);
			}

			function ItemForm(data) {
				this.id = ko.observable(data.id);
				this.name = ko.observable(data.name)
				this.description = ko.observable(data.description);
				this.sourceId = ko.observable(data.sourceId);
				this.targetId = ko.observable(data.targetId);
			}

			function ItemGroupListViewModel() {
				// Data
				var self = this;

				self.items = ko.observableArray([]);

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
						$('.languages').dropdown('activate');
					},
					setEmpty : function(data) {
						this.id(null);
						this.name('');
						this.description('');
						this.sourceId('');
						this.targetId('');
						$('.languages').dropdown('clear');
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
					}
				});

				// Operations
				self.addItem = function(data) {
					self.items.splice(0, 0, new Item(data));
				};
				self.removeItem = function(item) {
					self.items.remove(item);
				};

				self.showItemForm = function() {
					self.itemForm.setEmpty();
					self.itemForm.show(function(data) {
						self.addItem(data);
						$('#add_voc').modal('hide');
					});
				};

				self.showEditItemForm = function() {
					var selectedItem = self.selectedItems()[0];
					$.getJSON("/v1/api/vocabularies", function(itemForm) {
						self.itemForm.setItem(new ItemForm(itemForm));
						self.itemForm.show(function(data) {
							self.items.replace(self.selectedItem, new Item(data));
							$('#add_voc').modal('hide');
						});
					});
				};

				self.showDeleteItems = function() {
					self.deleteUrl = '/v1/api/vocabularies';

					self.itemsToDelete.removeAll();

					self.errorHeader('');
					self.errorMessage('');

					self.itemsToDelete([].concat(self.itemsToDelete()).concat(
							self.selectedItems()));

					$('#delete_items').modal('show');
				};

				self.deleteItems = function(model, evt) {
					self.deleteInProgress(true);
					self.errorHeader('');
					self.errorMessage('');

					var initialSize = self.itemsToDelete().length;

					var deffers = [];

					self.itemsToDelete().forEach(function(item) {
						deffers.push($.ajax({
							url : self.deleteUrl + item.id(),
							contentType : "application/json; charset=utf-8",
							method : 'DELETE',
							headers : csrf,
							dataType : 'json'
						}));
					});

					$.when.apply(self, deffers).done(function() {
						self.itemsToDelete().forEach(function(item) {
							if (self.deleteUrl.indexOf('group') != -1) {
								self.groups.remove(item);
							} else {
								self.items.remove(item);
							}
						});
						$('#delete_items').modal('hide');
					}).fail(function(deffer, type, message) {
						self.errorHeader(type);
						self.errorMessage(message);
					}).always(function() {
						self.deleteInProgress(false);
					});

				};

				self.updateData = function() {
					self.errorHeader('');
					self.errorMessage('');
					// Load initial state from server
					$.getJSON('/v1/api/vocabularies', function(allData) {
						var mappedItems = $.map(allData, function(item) {
							return new Item(item)
						});
						self.items(mappedItems);
					})
					.fail(function(error) {
						var e = error.responseJSON;
						self.errorHeader('Failed to receive vocabulary list');
						self.errorMessage(e.message);
					});
				};

				self.updateData();

			}

			ko.applyBindings(new ItemGroupListViewModel());
		});
