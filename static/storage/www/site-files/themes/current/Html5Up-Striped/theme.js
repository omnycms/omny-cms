define(['jquery'],
    function($) {

	var theme = {};
	theme.load = function() {
		$(function() {
			$('[data-toggle="offcanvas"]').click(function () {
				$('.row-offcanvas, .sidebar-offcanvas').toggleClass('active');
			});
		});
	};

	return theme;
});