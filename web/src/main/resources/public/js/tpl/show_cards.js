"use strict";

$(document).ready(
		function() {

			var csrf = {};
			var csrf_name = $("meta[name='_csrf_header']").attr("content");
			csrf[csrf_name] = $("meta[name='_csrf']").attr("content");

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
				
				self.item = ko.observable();
				self.current = ko.observable(0);

				self.items = ko.observableArray([]);
				
				self.isShowAnswer = ko.observable(false);
				
				self.showAnswer = function() {
					self.isShowAnswer(!self.isShowAnswer());
				}

				self.errorHeader = ko.observable();
				self.errorMessage = ko.observable();
				
				self.showPrevious = function() {
					var i = self.current() - 1;
					if (i < 0) {
						self.current(0);
					} else {
						self.current(i);
					}
					self.item(self.items()[self.current()]);
					self.isShowAnswer(false);
				};

				self.showNext = function() {
					var i = self.current() + 1;
					if (i >= self.items().length) {
						self.current(self.items().length - 1);
					} else {
						self.current(i);
					}
					self.item(self.items()[self.current()]);
					self.isShowAnswer(false);
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
						self.item(self.items()[0]);
					});
				};

				self.updateData();

			}

			ko.applyBindings(new ItemGroupListViewModel());
		});
