"use strict";

$(document).ready(
		function() {

			var csrf = {};
			var csrf_name = $("meta[name='_csrf_header']").attr("content");
			csrf[csrf_name] = $("meta[name='_csrf']").attr("content");
			
			function convert(data) {
				var card = {};
				card['content'] = [];
				for (var key in data) {
			    if (data.hasOwnProperty(key)) {
			    	if (key == 'vocabularyId') {
			    		card['vocabularyId'] = data[key];
			    	} else {
			    		card.content.push({
			    			name: key,
			    			text: data[key]
			    		});
			    	}
			    }
				}
				return card;
			};

			function Item(data) {
				for (var key in data) {
			    if (data.hasOwnProperty(key)) {
			    	this[key] = ko.observable(data[key]);
			    }
				}
			}

			function ItemForm(data) {
				for (var key in data) {
					if (data.hasOwnProperty(key)) {
						this[key] = ko.observable(data[key]);
					}
				}
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
					convert: convert,
					setItem : function(data) {
						for (var key in data) {
					    if (data.hasOwnProperty(key)) {
					    	this[key](data[key]());
					    }
						}
					},
					setEmpty : function(data) {
						this.id(null);
						this.vocabularyId(null);
						for (var key in document.cardFields) {
					    if (document.cardFields.hasOwnProperty(key)) {
					    	this[document.cardFields[key].name]('');
					    }
						}
					},
					url : '/v1/api/cards',
					formSelector : '#add_card_form',
					modalSelector : '#add_card',
					initItem : function() {
						this.id = ko.observable();
						this.vocabularyId = ko.observable();
						for (var key in document.cardFields) {
					    if (document.cardFields.hasOwnProperty(key)) {
					    	this[document.cardFields[key].name] = ko.observable();
					    }
						}
					}
				});

				// Suggester
				$('.suggester').search({
					fields: {
						description: 'key',
						title: 'value'
					},
					apiSettings : {
						url : '/v1/api/suggester?key={key}&value={query}&type=string',
						cache : 'none',
						beforeSend: function(settings) {
							var fieldName = $(this).find('.prompt').attr('name');
				      settings.urlData['key'] = fieldName;
				      return settings;
				    },
						onResponse: function(data) {
							var results = [];
							$.each(data, function( i, l ) {
								results[i] = l;
							});
							return {"success": true, "results": results};
						}
					},
					onSelect: function(result, response) {
						if (this.className.indexOf('first') == -1) {
							return;
						}
						$.getJSON("https://localhost:8443/v1/api/retrieval?uri=" + result.source, function(data) {
							data['id'] = null;
							data['vocabularyId'] = document.vocabularyId;

							var itemForm = new ItemForm(data);
							self.itemForm.setItem(itemForm);
						});
					},
					cache: false,
					maxResults: 10,
					selectFirstResult: true
				});

				// Operations
				self.addItem = function(data) {
					self.items.splice(0, 0, new Item(self.convertItem(data)));
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
				
				self.convertItem = function(data) {
					var flatItem = {};
					flatItem['id'] = data.id;
					flatItem['vocabularyId'] = data.vocabularyId;
					for (var cnt in data.content) {
						flatItem[data.content[cnt].name] = data.content[cnt].text;
					}
					return flatItem;
				}

				self.updateData = function() {
					// Load initial state from server
					$.getJSON('/v1/api/cards/' + document.vocabularyId, function(allData) {
						var mappedItems = $.map(allData, function(item) {
							return new Item(self.convertItem(item));
						});
						self.items(mappedItems);
					});
				};

				self.updateData();

			}

			ko.applyBindings(new ItemGroupListViewModel());
		});
