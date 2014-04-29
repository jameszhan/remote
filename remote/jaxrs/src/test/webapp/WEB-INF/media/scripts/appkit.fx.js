(function($){	
	
	$.extend({
		appkit: {			
			size: function(size){
				var ret = size;
				var suffix = 'B';
				if(size > 1024 * 1024 * 1024){
					ret = size / (1024 * 1024 * 1024);
					suffix = 'G';
				}else if(size > 1024 * 1024){
					ret = size / (1024 * 1024);
					suffix = 'M';
				}else if(size > 1024){
					ret = size / 1024;
					suffix = 'K';
				}
				return Math.round(ret * 100) / 100 + suffix;
			},		
			pagingtip: function(max, mid, gap){
				var gap = gap || 5, ret = [];
				(mid < 2) && (mid = 2);
				(mid > max - 1) && (mid = max - 1);
				ret.push(mid);
				for(var i = 0, l = mid, r = mid; i < gap; i++){
					(--l > 1) && ret.unshift(l);
					(++r < max) && ret.push(r);
				}	
				while(l <= 1 && r < max){
					l++, r++;
					ret.push(r);
				}
				while(r >= max && l > 1){
					r--, l--;
					ret.unshift(l);
				}
				(l > 2) && ret.unshift('...');
				(r < max - 1) && ret.push('...');
				ret.unshift(1);
				ret.push(max);
				return ret;
			}, 
			initTabview: init_tabs,
			initSortable: init_sortable,
			ratingStar: rating_star
		}	
	});		
	
	jQuery.fn.alternateRowColors = function(){	
		$('tbody tr:odd', this).removeClass('even').addClass('odd');
		$('tbody tr:even', this).removeClass('odd').addClass('even');		
	};
	
	//Tab Menu
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
	
	//Sort table.
	function init_sortable(){		
		$('table.sortable').each(function(){
			var $table = $(this);
			$('thead>tr>*', $table).each(function(column){
				var $header = $(this);
				var findSortKey;
				if($header.is('.sort-alpha')){
					findSortKey = function($cell){
						return $cell.find('.sort-key').text().toUpperCase() + ' ' + $cell.text().toUpperCase();
					};
				}else if($header.is('.sort-numeric')){
					findSortKey = function($cell){
						var key = parseFloat($cell.text().replace(/^[^\d.]*/, ''));
						return isNaN(key) ? 0 : key;
					};
				}else if($header.is('.sort-date')){
					findSortKey = function($cell){					
						return Date.parse($cell.text());
					};
				}
				
				if(findSortKey){
					$header.addClass('clickable').hover(function(){
						$header.addClass('hover');
					}, function(){
						$header.removeClass('hover');
					}).click(function(){
						var sortDirection = -1;
						if($header.is(".sorted-desc")){
							sortDirection = 1;
						}
						var rows = $table.find('tbody>tr').get();
						$.each(rows, function(index, row){
							var $cell = $(row).children('td').eq(column);
							row.sortKey = findSortKey($cell);
						});
						rows.sort(function(a, b){						
							if(a.sortKey < b.sortKey){
								return -sortDirection;
							}else if(a.sortKey > b.sortKey){
								return sortDirection;
							}else{
								return 0;
							}
						});
						$.each(rows, function(index, row){
							$table.children('tbody').append(row);
							row.sortkey = null;
						});
						$table.find('thead>tr>*').removeClass('sorted-asc').removeClass('sorted-desc');
						if(sortDirection == 1){
							$header.addClass('sorted-asc');
						}else{
							$header.addClass('sorted-desc');
						}						
						$table.alternateRowColors();
					});				
				}				
			});
			$table.alternateRowColors();
		});
	}
	
	function rating_star(v){
		var s = '0.0';
		if(v > 32768){
			s = '5.0';
		}else if(v > 16384){
			s = '4.5';
		}else if(v > 8192){
			s = '3.5';
		}else if(v > 4096){
			s = '3.0';
		}else if(v > 2048){
			s = '2.5';
		}else if(v > 1024){
			s = '2.0';
		}
		return $('<img>').attr('src', '/media/images/icons/star' + s + '.gif');
	}
	
	function fx(selector){	
		return $(selector).hover(function(){
			$(this).removeClass('mouseout').addClass('mouseover');
		}, function(){
			$(this).removeClass('mouseover').addClass('mouseout');
		});			
	}

	$(function(){			
		var $btn = fx('.ajaxButton').addClass('mouseout');			
		$('.ajaxStatus').ajaxStart(function(e) {		
			$(this).show();			
		}).bind('ajaxSend', function(e, xhr){			
			$btn.attr('disabled', true);
		}).bind('ajaxSuccess', function(e, xhr, options, resp){				
		}).bind('ajaxError', function(e, xhr, options, resp){				
		}).bind('ajaxComplete', function(e, xhr, options){
			$btn.removeAttr('disabled');		
		}).ajaxStop(function(e, xhr, options, o) {			
			$(this).hide();
		});	
	});
		
})(jQuery);



Date.prototype.format = function(format){ 
	var o = { 
		"M+" : this.getMonth()+1, 
		"d+" : this.getDate(),
		"h+" : this.getHours(), 
		"m+" : this.getMinutes(), 
		"s+" : this.getSeconds(),
		"q+" : Math.floor((this.getMonth()+3)/3), 
		"S" : this.getMilliseconds() 
	} 
	if(/(y+)/.test(format)){
		format = format.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 		
	} 
	for(var k in o){
		if(new RegExp("(" + k + ")").test(format)){
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length)); 
		}
	}
	return format; 
};


// ajaxStart (Global Event)	
// beforeSend (Local Event)		
// ajaxSend (Global Event)	
// success (Local Event)	
// ajaxSuccess (Global Event)	
// error (Local Event)	
// ajaxError (Global Event)	
// complete (Local Event)	
// ajaxComplete (Global Event)
// ajaxStop (Global Event)	
//beforeSend: function(xhr, options){					
//	xhr.setRequestHeader("Content-Type", "utf-8");	
//}	

/*
function paging(pagesize, curr, func){
	var start_ellipsis = false, end_ellipsis = false, dist = 5;
	var start = curr - dist, end = curr + dist;
	func(1);
	if(start > 2){
		start_ellipsis = true;
	}else{
		start = 2;
		end = start + 2 * dist;
		if(end > pagesize - 1){
			end_ellipsis = false;	
			end = pagesize - 1;
		}
	}
	if(end < pagesize - 1){
		end_ellipsis = true;			
	}else{
		end = pagesize - 1;
		start = end - 2 * dist;
		if(start < 2){
			start_ellipsis = false;
			start = 2;
		}
	}
	if(start_ellipsis){
		func('...');
	}

	for(i = start; i <= end; i++){
		func(i);
	}		
	
	if(end_ellipsis){
		func('...');
	}
	func(pagesize);
}
*/
