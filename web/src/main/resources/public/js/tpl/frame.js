"use strict";

function resizeCentral(selector, minus) {
	$(selector).height($("#main").height() - minus);
}

function xhrErrorHandler(xhr) {
	if (xhr.status == 401) {
		window.location.replace(window.location.href);
	}
}

$(document).ready(function() {
	$('.ui.menu .ui.dropdown').dropdown({
		on : 'hover'
	});
	$('.ui.menu a.item').on('click', function() {
		$(this).addClass('active').siblings().removeClass('active');
	});

	$('.ui.search').search({
		apiSettings : {
			url : '/search/?q={query}'
		},
		type : 'category'
	});

});