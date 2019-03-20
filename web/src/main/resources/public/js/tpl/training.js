"use strict";

(function ($) {
  $.fn.serializeFormJSON = function () {

      var o = {};
      var a = this.serializeArray();
      $.each(a, function () {
          if (o[this.name]) {
              if (!o[this.name].push) {
                  o[this.name] = [o[this.name]];
              }
              o[this.name].push(this.value || '');
          } else {
              o[this.name] = this.value || '';
          }
      });
      return o;
  };
})(jQuery);

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

				self.isShowAnswer = ko.observable(false);
				
				self.showAnswer = function() {
					
				}

				self.errorHeader = ko.observable();
				self.errorMessage = ko.observable();

				self.showNext = function() {
					if (document.nextCardId) {
						window.location.href = '/cards/' + document.vocabularyId + '/' + document.nextCardId;
					} else {
						window.location.href = '/vocabularies/' + document.vocabularyId;
					}
				};
				
				self.errorHeader = ko.observable();
				self.errorMessage = ko.observable();

				$('#check_card_form').submit(function() {
					self.errorHeader('');
    			self.errorMessage('');
    			$.ajax({
    	           url: $('#check_card_form').attr('action'),
    	           type : "POST",
    	           headers : csrf,
    	           dataType : 'json',
    	           contentType: "application/json; charset=utf-8",
    	           data : JSON.stringify($("#check_card_form").serializeFormJSON()),
    	           success : function(result) {
    	          	 if (result.valid) {
    	          		 window.location.href = '/cards/' + document.vocabularyId + '/' + document.nextCardId;
    	          	 } else {
    	          		 self.errorHeader('Wrong answer');
    	          		 self.errorMessage(result.message);
    	          	 }
    	           },
    	           error: function(xhr, resp, text) {
    	        	   if (!xhr.responseJSON) {
    	        	  	 self.errorHeader(xhr.status);
    	        	  	 self.errorMessage(xhr.statusText);
    	        	   } else {
    	        	  	 var e = xhr.responseJSON;
    	        	  	 self.errorHeader('Failed to Check');
    	        	  	 self.errorMessage(e.message);
    	        	   }
    	           }
    	        });
			    return false;
				});
				
				self.convertItem = function(data) {
					var flatItem = {};
					flatItem['id'] = data.id;
					flatItem['vocabularyId'] = data.vocabularyId;
					for (var cnt in data.content) {
						flatItem[data.content[cnt].name] = data.content[cnt].text;
					}
					return flatItem;
				};
				
				self.updateData = function() {
					// Load initial state from server
					$.getJSON('/v1/api/validate/' + document.vocabularyId + '/' + document.cardId, function(allData) {
						self.item(new Item(self.convertItem(allData)));
					});
				};

				self.updateData();

			};

			ko.applyBindings(new ItemGroupListViewModel());
		});
