"use strict";

function resizeCentral(selector, minus) {
	$(selector).height($("#main").height() - minus);
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
			url : '/page/search/?q={query}'
		},
		type : 'category'
	});

});