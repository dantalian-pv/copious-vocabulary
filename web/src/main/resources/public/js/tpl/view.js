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

			function ItemGroupListViewModel() {
				// Data
				var self = this;

				self.errorHeader = ko.observable();
				self.errorMessage = ko.observable();

				$('#view_form').submit(function() {
					self.errorHeader('');
    			self.errorMessage('');
    			$.ajax({
	           url: $('#view_form').attr('action'),
	           type : "PUT",
	           headers : csrf,
	           dataType : 'json',
	           contentType: "application/json; charset=utf-8",
	           data: JSON.stringify($("#view_form").serializeFormJSON()),
 						 statusCode: {
								401: function() {
									window.location.replace(window.location.href);
								}
						 },
	           success: function(result) {
	        	   window.location.href = '/vocabularies/' + document.vocabularyId;
	           },
	           error: function(xhr, resp, text) {
	        	   if (!xhr.responseJSON) {
				    			self.errorHeader(xhr.status);
				    			self.errorMessage(xhr.statusText);
				    	 } else {
				    		 var e = xhr.responseJSON;
				    		 self.errorHeader('Failed to Save');
				    		 self.errorMessage(e.message);
				    	 }
	           }
	        });
			    return false;
				});
			};
			ko.applyBindings(new ItemGroupListViewModel());
		});
