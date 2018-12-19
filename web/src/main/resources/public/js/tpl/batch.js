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
				this.link = ko.observable("/batches/" + data.id)
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
					},
					setEmpty : function(data) {
						this.id(null);
						this.name('');
						this.description('');
						this.sourceId('');
						this.targetId('')
					},
					url : '/v1/api/batches',
					formSelector : '#add_card_batch_form',
					modalSelector : '#add_card_batch',
					initItem : function() {
						this.id = ko.observable();
						this.name = ko.observable();
						this.description = ko.observable();
						this.sourceId = ko.observable();
						this.targetId = ko.observable();
					}
				});

				// Operations
				self.showEditBatch = function() {
					
				};
				self.showEditView = function() {
					
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
