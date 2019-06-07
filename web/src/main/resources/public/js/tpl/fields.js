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
				this.order = ko.observable(data.order);
				this.system = ko.observable(data.system);
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
					self.items.splice(self.items().length - 1, 0, new Item(data));
				};

				self.showItemForm = function() {
					self.itemForm.setEmpty();
					self.itemForm.show(function(data) {
						self.addItem(data);
						$('#add_field').modal('hide');
					});
				};
				
				self.deleteItem = function(item) {
					$.ajax({
						url : '/v1/api/fields/' + document.vocabularyId + '/' + item.name(),
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
						self.items.remove(item);
					}).fail(function(xhr, type, message) {
						xhrErrorHandler(xhr);
						self.errorHeader(type);
						self.errorMessage(message);
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
					}).fail(function(error) {
						xhrErrorHandler(error);
						var e = error.responseJSON;
						self.errorHeader('Failed to receive fields list');
						self.errorMessage(e.message);
					});
				};

				self.updateData();

			}

			ko.applyBindings(new ItemGroupListViewModel());
		});
