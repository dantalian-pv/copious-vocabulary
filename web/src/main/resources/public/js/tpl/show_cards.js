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

				self.itemsTotal = ko.observable(0);
				self.itemsFrom = ko.observable(0);
				self.itemsLimit = ko.observable(0);

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
					if (self.current() < self.itemsFrom()) {
						var from = self.itemsFrom() - self.itemsLimit();
						from = from < 0 ? 0 : from;
						self.itemsFrom(from);
						self.updateData(from, self.itemsLimit());
					}
					self.item(self.items()[self.current() - self.itemsFrom()]);
					self.isShowAnswer(false);
				};

				self.showNext = function() {
					var i = self.current() + 1;
					if (i >= self.itemsTotal()) {
						self.current(self.itemsTotal() - 1);
					} else {
						self.current(i);
					}
					if (self.current() > self.itemsFrom() + self.itemsLimit()) {
						var from = self.itemsFrom() + self.itemsLimit();
						self.itemsFrom(from);
						self.updateData(from, self.itemsLimit());
					}
					self.item(self.items()[self.current() - self.itemsFrom()]);
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

				self.updateData = function(aFrom, aLimit) {
					var from = aFrom ? aFrom : 0;
					var limit = aLimit ? aLimit : 30;
					// Load initial state from server
					$.getJSON('/v1/api/cards/' + document.vocabularyId + '?from=' + from + "&limit=" + limit + "&highlight=true",
					function(allData) {
						self.itemsTotal(allData.total);
						self.itemsFrom(allData.from);
						self.itemsLimit(allData.limit);
						var mappedItems = $.map(allData.items, function(item) {
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
