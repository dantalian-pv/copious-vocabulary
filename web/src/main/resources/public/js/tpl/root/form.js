"use strict";

function Form(params) {

	var self = this;

	var formParams = {
		formSelector : params.formSelector,
		uri : params.url,
		method : 'POST',
		doneAction : null
	};

	setFormAction(formParams);

	params.initItem.call(self);

	self.resetForm = function() {
		var form = $(params.formSelector);
		resetForm(form);
	}

	self.setEmpty = function() {
		self.resetForm();

		params.setEmpty.call(self)

		formParams.uri = params.url;
		formParams.method = "POST";
	}

	self.setItem = function(item) {
		self.resetForm();

		params.setItem.call(self,item);

		formParams.uri = params.url + "/" + self.id();
		formParams.method = "PUT";
	}

	self.show = function(callback) {
		$(params.modalSelector).modal('show');
		formParams.doneAction = callback;
	}

}