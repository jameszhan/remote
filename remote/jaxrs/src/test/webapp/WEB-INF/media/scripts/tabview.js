$(function(){
	function init_tabs(){
		$(".tabs > ul").addClass("tab-title");
		$(".tabs > div").addClass("tab-content");			
		$(".tab-title > li > a").click(function(e) {
			if (e.target == this) {
				var titles = $(this).parentsUntil('.tabs').children('li');			
				var contents = $(this).parentsUntil('.tabs').next('.tab-content').children('div');	
				var index = $.inArray(this, titles.children('a'));	 			
				if(titles.length <= contents.length){
					titles.removeClass("selected").eq(index).addClass("selected");
					contents.addClass("hide").eq(index).removeClass("hide");
				}	               
			}
		});
		
		$('.tab-title').each(function(i, e){
			$(e).find('li >  a').eq(0).click();
		});		
	}
	init_tabs();	
});

