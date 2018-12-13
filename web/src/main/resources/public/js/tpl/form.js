"use strict";

function resetForm(form) {
	var messageBox = form.children('.message');
	form.children('.ui.red.label').addClass('hidden');
	form.children('.field').removeClass('error');
	form.find('input').parents('.field').children('.red.label').addClass('hidden');
	messageBox.addClass('hidden');
}

function setFormAction(params) {

	$(params.formSelector)
			.submit(
					function(evt) {
						evt.preventDefault();

						var form = $(evt.target);
						var messageBox = form.children('.message');

						// internal functions
						var showInputError = function(inputName, inputText) {
							var field = form.find('input[name="' + inputName + '"]').parents(
									'.field');
							field.addClass('error');
							var errorLabel = field.children('.red.label');
							errorLabel.text(inputText);
							errorLabel.removeClass('hidden');
						}
						var showMessage = function(status, message, statusTitle) {
							if (status == 'success') {
								messageBox.removeClass('negative message').addClass('positive message');
								messageBox.children('div.header').text(
										(statusTitle) ? statusTitle : 'Success');
							} else if (status == 'error') {
								messageBox.removeClass('positive message').addClass('negative message');
								messageBox.children('div.header').text(
										(statusTitle) ? statusTitle : 'Error');
							} else {
								console.error('undefined status', status);
							}
							messageBox.children('p.info').text(message);
							messageBox.removeClass('hidden');
						}
						// END internal functions

						var csrf = {};
						var csrf_name = $("meta[name='_csrf_header']").attr("content");
						csrf[csrf_name] = $("meta[name='_csrf']").attr("content");

						var data = {};

						form.serializeArray().map(function(x) {
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

						resetForm(form);

						form.addClass('loading');

						$.ajax({
							url : params.uri,
							method : params.method,
							headers : csrf,
							contentType : "application/json; charset=utf-8",
							data : JSON.stringify(data),
							dataType : 'json'

						}).done(function(data) {
							if (data.message) {
								showMessage('success', data.message);
							} else {
								showMessage('success', "");
							}
							if (params.doneAction) {
								params.doneAction(data);
							}
						}).fail(
								function(jqXHR, textStatus, errorThrown) {

									if (jqXHR.responseJSON) {

										var json = jqXHR.responseJSON;
										
										if(json.status == 403 && json.message.indexOf('CSRF')) {
											window.location.replace(window.location.href);
										}

										var message = "Error code: " + jqXHR.status + " " + json.message;
										showMessage('error', message, json.error);

										if (json.fieldErrors && Array.isArray(json.fieldErrors)) {
											for ( var i in json.fieldErrors) {
												var fieldError = json.fieldErrors[i];
												showInputError(fieldError.field, fieldError.defaultMessage);
											}
										}
									} else {
										var message = "Error code: " + jqXHR.status + " "
												+ ((jqXHR.status == 0) ? "Lost connection" : jqXHR.statusText);
										showMessage('error', message);
									}

									if (params.failAction) {
										params.failAction();
									}

								}).always(function() {
							form.removeClass('loading');

							if (params.alwaysAction) {
								params.alwaysAction();
							}
						});
					});

}
