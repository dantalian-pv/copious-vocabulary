"use strict";

function xhrErrorHandler(xhr) {
	if (xhr.status == 401) {
		window.location.replace(window.location.href);
	}
}

function Form(params) {

	var self = this;

	var formParams = {
		formSelector : params.formSelector,
		uri : params.url,
		method : 'POST',
		doneAction : null,
		convert: params.convert
	};

	setFormAction(formParams);

	params.initItem.call(self);

	self.resetForm = function() {
		var form = $(params.formSelector);
		resetForm(form);
	}

	self.setEmpty = function() {
		self.resetForm();

		params.setEmpty.call(self);

		formParams.uri = params.url;
		formParams.method = "POST";
	}

	self.setItem = function(item) {
		self.resetForm();

		params.setItem.call(self, item);

		if (self.id()) {
			if (!params.noUrlChange) {
				formParams.uri = params.url + "/" + self.id();
			}
			formParams.method = "PUT";
		}
	}
	
	self.getData = function(noConvert) {
		var data = {};

		$(params.formSelector).serializeArray().map(function(x) {
			if (typeof (data[x.name]) != 'undefined') {
				if (!Array.isArray(data[x.name])) {
					var val = data[x.name];
					data[x.name] = [ val ];
				}
				data[x.name].push(x.value);
			} else {
				data[x.name] = x.value;
			}
		});
		
		if (params.convert && !noConvert) {
			data = params.convert(data);
		}
		return data;
	}

	self.show = function(callback) {
		$(params.modalSelector).modal('show');
		formParams.doneAction = callback;
	}

}