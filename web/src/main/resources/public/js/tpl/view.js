"use strict";

$(document).ready(
		function() {

			var csrf = {};
			var csrf_name = $("meta[name='_csrf_header']").attr("content");
			csrf[csrf_name] = $("meta[name='_csrf']").attr("content");

			function ItemGroupListViewModel() {
				// Data
				var self = this;
				
				self.errorHeader = ko.observable();
				self.errorMessage = ko.observable();
				
				$('#view_form').submit(function() {
					self.errorHeader('');
	    			self.errorMessage('');
				    $(this).ajaxSubmit({
				    	error: function(error) {
				    		if (!error.responseJSON) {
				    			self.errorHeader(error.status);
				    			self.errorMessage(error.statusText);
				    		} else {
								var e = error.responseJSON;
								self.errorHeader('Failed to Save');
								self.errorMessage(e.message);
				    		}
						}
				    })
				    return false;
				});
			};
			ko.applyBindings(new ItemGroupListViewModel());
		});
