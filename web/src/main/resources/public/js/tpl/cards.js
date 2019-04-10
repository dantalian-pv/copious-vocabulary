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
					$.getJSON('/v1/api/suggester?key=' + name + '&value=' + value 
							+ '&type=string&notKey=vocabulary_id&notValue=' 
							+ document.vocabularyId + "&source=" + 
							document.source + "&target=" + document.target, function(data) {
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
								|| (allGroups[allGroups.length - 1].group != group.group && group.items.length > 0)) {
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
					$.getJSON("/v1/api/retrieval?uri=" + encodeURIComponent(btoa(item.source)), function(data) {
						data['id'] = null;
						data['vocabularyId'] = document.vocabularyId;

						self.itemForm.setItem(new ItemForm(data));
					});
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
						var val = evt.target.value;
						self.suggest(name, val);
				  }, 500);
				});

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
