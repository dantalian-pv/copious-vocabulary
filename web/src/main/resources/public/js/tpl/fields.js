"use strict";

$(document).ready(
		function() {

			var csrf = {};
			var csrf_name = $("meta[name='_csrf_header']").attr("content");
			csrf[csrf_name] = $("meta[name='_csrf']").attr("content");
			
			function Item(data) {
				this.id = ko.observable(data.id);
				this.name = ko.observable(data.name)
				this.type = ko.observable(data.type);
			}

			function ItemForm(data) {
				this.id = ko.observable(data.id);
				this.name = ko.observable(data.name)
				this.type = ko.observable(data.type);
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
						this.type(data.type());
					},
					setEmpty : function(data) {
						this.id(null);
						this.name('');
						this.type('');
					},
					url : '/v1/api/fields',
					formSelector : '#add_field_form',
					modalSelector : '#add_field',
					initItem : function() {
						this.id = ko.observable();
						this.name = ko.observable();
						this.type = ko.observable();
					}
				});
				
				// Suggester
				$('.suggester').search({
					apiSettings : {
						url : '/v1/api/suggester?value={query}&type=field&notKey=vocabulary_id&notValue=' + document.vocabularyId + "&source=" + document.source + "&target=" + document.target,
						cache : 'none',
						onResponse: function(data) {
							var results = [];
							$.each(data, function( i, l ) {
								results[i] = {"title": l.value, "description": l.key, "source": l.source};
							});
							return {"success": true, "results": results};
						}
					},
					onSelect: function(result, response) {
						var item = new ItemForm({
							id: null,
							name: result.title,
							type: result.description
						});
						self.itemForm.setItem(item);
					},
					cache: false,
					maxResults: 10,
					selectFirstResult: true
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
						$('#add_field').modal('hide');
					});
				};

				self.showDeleteItems = function() {
					self.deleteUrl = '/v1/api/fields';

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
					$.getJSON('/v1/api/fields/' + document.vocabularyId, function(allData) {
						var mappedItems = $.map(allData, function(item) {
							return new Item(item)
						});
						self.items(mappedItems);
					})
					.fail(function(error) {
						var e = error.responseJSON;
						self.errorHeader('Failed to receive fields list');
						self.errorMessage(e.message);
					});
				};

				self.updateData();

			}

			ko.applyBindings(new ItemGroupListViewModel());
		});
