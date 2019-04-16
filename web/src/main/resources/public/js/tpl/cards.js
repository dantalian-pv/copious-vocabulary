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
			    	} else if (key == 'id') {
			    		card['id'] = data[key];
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
				self.itemsTotal = ko.observable(0);
				self.itemsFrom = ko.observable(0);
				self.itemsLimit = ko.observable(0);

				self.deleteUrl = null;
				self.deleteInProgress = ko.observable(false);
				self.errorHeader = ko.observable();
				self.errorMessage = ko.observable();

				// Form Data
				self.itemForm = new Form({
					noUrlChange: true,
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

				// Operations
				self.addItem = function(data) {
					self.items.splice(self.items().length, 0, new Item(self.convertItem(data)));
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

				self.showEditItemForm = function(item) {
					$.getJSON("/v1/api/cards/" + item.vocabularyId() + '/' + item.id(), function(itemForm) {
						self.itemForm.setItem(new Item(self.convertItem(itemForm)));
						self.itemForm.show(function(data) {
							self.items.replace(item, new Item(self.convertItem(data)));
							$('#add_card').modal('hide');
						});
					});
				};

				self.deleteItem = function(item) {
					$.ajax({
						url : '/v1/api/cards/' + item.vocabularyId() + '/' + item.id(),
						contentType : "application/json; charset=utf-8",
						method : 'DELETE',
						headers : csrf,
						dataType : 'json'
					}).done(function() {
						self.items.remove(item);
					}).fail(function(deffer, type, message) {
						self.errorHeader(type);
						self.errorMessage(message);
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
				
				self.suggests = ko.observableArray([]);
				
				self.suggest = function(name, value) {
					if (!value) {
						self.suggests([]);
						return;
					}
					var src = name == 'word' ? document.source : document.target;
					var tgt = name == 'word' ? document.target : document.source;
					$.getJSON('/v1/api/suggester?key=' + name + '&value=' + value 
							+ '&type=string&notKey=vocabulary_id&notValue=' 
							+ document.vocabularyId + "&source=" + 
							src + "&target=" + tgt, function(data) {
						var currGroup = data.length > 0 ? data[0].group : null;
						var group = {
							group: null,
							items: []
						};
						var allGroups = [];
						for (var i in data) {
							var item = data[i];
							if (item.group != currGroup) {
								currGroup = item.group;
								allGroups.push(group);
								group = {
									group: null,
									items: []
								};
							}
							group.group = item.group;
							group.items.push(item);
						}
						if ((allGroups.length == 0 && group.items.length > 0) 
								|| (allGroups.length > 0 
										&& allGroups[allGroups.length - 1].group != group.group && group.items.length > 0)) {
							allGroups.push(group);
						}
						self.suggests(allGroups);
					});
				}
				
				self.oldFormData = null;
				
				self.hideSuggest = function(item, evt) {
					$(evt.target).css('font-style', 'normal');
					//console.log(item, evt);
					if (self.oldFormData) {
						self.itemForm.setItem(new Item(self.convertItem(self.oldFormData)));
					}
				};
				
				self.showSuggest = function(item, evt) {
					$(evt.target).css('font-style', 'italic');
					//console.log(item, evt);
					if (!self.oldFormData) {
						self.oldFormData = self.itemForm.getData();
					}
					if (item.key == 'word') {
						// Fill up full form only for word 
						$.getJSON("/v1/api/retrieval?uri=" + encodeURIComponent(btoa(item.source)), function(data) {
							data['id'] = null;
							data['vocabularyId'] = document.vocabularyId;
	
							self.itemForm.setItem(new ItemForm(data));
						});
					} else {
						// Set value only for particular field
						if (self.oldFormData) {
							var data = jQuery.extend(true, {}, self.oldFormData);
							for (var i in data.content) {
								if (data.content[i].name == item.key) {
									data.content[i].text = item.value;
								}
							}
							self.itemForm.setItem(new Item(self.convertItem(data)));
						}
					}
				};
				
				self.selectSuggest = function(item, evt) {
					$(".suggest.item").css('font-weight', 'normal');
					$(evt.target).css('font-weight', 'bold');
					//console.log(item, evt);
					self.oldFormData = null;
				};
				
				var suggestTimer;
				$('#add_card_form .prompt').on('keyup paste mouseup', function(evt) {
					clearTimeout(suggestTimer);
					suggestTimer = setTimeout(function() {
						var name = evt.target.name;
						var val = evt.target.type == 'textarea' ? $('#add_card_form input[name="translation"]').val() : evt.target.value;
						self.suggest(name, val);
				  }, 500);
				});
				
				self.showPrevPage = function() {
					if (self.itemsFrom() <= 0) {
						return;
					}
					var from = self.itemsFrom() - self.itemsLimit();
					from = from < 0 ? 0 : from;
					self.updateData(from, self.itemsLimit());
				}
				
				self.showNextPage = function() {
					if (self.itemsFrom() + self.itemsLimit() >= self.itemsTotal()) {
						return;
					}
					var from = self.itemsFrom() + self.itemsLimit();
					self.updateData(from, self.itemsLimit());
				}

				self.updateData = function(aFrom, aLimit) {
					var from = aFrom ? aFrom : 0;
					var limit = aLimit ? aLimit : 30;
					// Load initial state from server
					$.getJSON('/v1/api/cards/' + document.vocabularyId + '?from=' + from + "&limit=" + limit,
					function(allData) {
						self.itemsTotal(allData.total);
						self.itemsFrom(allData.from);
						self.itemsLimit(allData.limit);
						var mappedItems = $.map(allData.items, function(item) {
							return new Item(self.convertItem(item));
						});
						self.items(mappedItems);
					});
				};

				self.updateData();

			}

			ko.applyBindings(new ItemGroupListViewModel());
		});
