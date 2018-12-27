"use strict";

$(document).ready(
		function() {

			var csrf = {};
			var csrf_name = $("meta[name='_csrf_header']").attr("content");
			csrf[csrf_name] = $("meta[name='_csrf']").attr("content");

			function Item(data) {
				this.id = ko.observable(data.id);
				this.content = ko.observableArray(data.content)
			}

			function ItemForm(data) {
				this.id = ko.observable(data.id);
				this.content = ko.observableArray(data.content)
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
						this.content(data.content());
					},
					setEmpty : function(data) {
						this.id(null);
						this.content([]);
					},
					url : '/v1/api/cards',
					formSelector : '#add_card_form',
					modalSelector : '#add_card',
					initItem : function() {
						this.id = ko.observable();
						this.content = ko.observableArray();
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
						$('#add_card').modal('hide');
					});
				};

				self.showEditItemForm = function() {
					var selectedItem = self.selectedItems()[0];
					$.getJSON("/v1/api/cards", function(itemForm) {
						self.itemForm.setItem(new ItemForm(itemForm));
						self.itemForm.show(function(data) {
							self.items.replace(self.selectedItem, new Item(data));
							$('#add_card').modal('hide');
						});
					});
				};

				self.showDeleteItems = function() {
					self.deleteUrl = '/v1/api/cards';

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
					// Load initial state from server
					$.getJSON('/v1/api/cards/' + document.vocabularyId, function(allData) {
						var mappedItems = $.map(allData, function(item) {
							return new Item(item)
						});
						self.items(mappedItems);
					});
				};

				self.updateData();

			}

			ko.applyBindings(new ItemGroupListViewModel());
		});
