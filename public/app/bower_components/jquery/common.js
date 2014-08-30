/*Designer Check box function Begin*/
function designercheckbox(){
	$('body').on("click", '.cbox, .cbox-selected', function () {
			if ($(this).attr('class') == 'cbox') {
				$(this).children('input').attr('checked', true);
				$(this).removeClass().addClass('cbox-selected');
				$(this).children('input').trigger('change');
			}
			else {
				$(this).children('input').attr('checked', false);
				$(this).removeClass().addClass('cbox');
				$(this).children('input').trigger('change');
			}
		});
		//browse button function begin
		$('.browse input[type=file]').css('opacity','0');
		$('.browse input[type=file]').change(function(){
			$(this).prev().val($(this).val().substr(12));
		});
		//browse button function end
}
/*Designer Check box function End*/

/*Designer radio button function Begin*/
jQuery.fn.customInput = function(){
	$(this).each(function(i){	
		if($(this).is('[type=checkbox],[type=radio]')){
			var input = $(this);
			
			// get the associated label using the input's id
			var label = $('label[for='+input.attr('id')+']');
			
			//get type, for classname suffix 
			var inputType = (input.is('[type=checkbox]')) ? 'checkbox' : 'radio';
			
			// wrap the input + label in a div 
			$('<div class="custom-'+ inputType +'"></div>').insertBefore(input).append(input, label);
			
			// find all inputs in this set using the shared name attribute
			var allInputs = $('input[name='+input.attr('name')+']');
			
			// necessary for browsers that don't support the :hover pseudo class on labels
			label.hover(
				function(){ 
					$(this).addClass('hover'); 
					if(inputType == 'checkbox' && input.is(':checked')){ 
						$(this).addClass('checkedHover'); 
					} 
				},
				function(){ $(this).removeClass('hover checkedHover'); }
			);
			
			//bind custom event, trigger it, bind click,focus,blur events					
			input.bind('updateState', function(){	
				if (input.is(':checked')) {
					if (input.is(':radio')) {				
						allInputs.each(function(){
							$('label[for='+$(this).attr('id')+']').removeClass('checked');
						});		
					};
					label.addClass('checked');
				}
				else { label.removeClass('checked checkedHover checkedFocus'); }
										
			})
			.trigger('updateState')
			.click(function(){ 
				$(this).trigger('updateState'); 
			})
			.focus(function(){ 
				label.addClass('focus'); 
				if(inputType == 'checkbox' && input.is(':checked')){ 
					$(this).addClass('checkedFocus'); 
				} 
			})
			.blur(function(){ label.removeClass('focus checkedFocus'); });
		}
	});
};
function designerradiobutton(){
	$(".designer").customInput();
}
/*Designer radio button function End*/

/*Designer  Select dropdown function Begin*/
function designerdropdownselect(){
	designerSelect(); 
	$('select.designer').css({'opacity':'0'});
}
function designerSelect(){
	$("select.designer").change(function () {
		var ds1 = "";
		var deId = this.id;
		$("#"+ deId +" option:selected").each(function () {
			ds1 = $(this).text();
		});
		$(this).prev().text(ds1);
	}).change();
}
/*Designer  Select dropdown function End*/

/*Function for hide outline of html element begin*/
function oulineandbordernone(){
	$('a, input, button, label').each(function() {
		$(this).attr("hideFocus", "true").css("outline", "none");
	});
}
/*Function for hide outline of html element bend*/

	/*clear text javascript Begin*/
	function clearText(field){
		if (field.defaultValue == field.value) field.value = '';
		else if (field.value == '') field.value = field.defaultValue;
		}
	/*clear text javascript End*/
	
	/*main navigation active function start*/
	function mainTnb(index){
	 var allLinks=$('.mainnav').children('ul').children('li');
	 allLinks.removeClass('selective');
	 $(allLinks[index-1]).addClass('selective');
		}
	/*main navigation active function End*/
	
	/*Sub navigation active function start*/
	function subTnb(index){
	 var allLinks=$('.mainnav').children('ul').children('li').children('ul').children('li');
	 allLinks.removeClass('selective');
	 $(allLinks[index-1]).addClass('selective');
		}
	/*Sub navigation active function End*/

/*Close Popup window function begin*/
function popupwindowclose(){
	$('#fade, .d-close').click(function(){
		$('.dpopupwindow, #fade').fadeOut();
	});
}
/*Close Popup window function End*/

/*Normal Data Grid action drop down function Starts*/
function datagridactiondropdown() {
	$('body').on('click', '.dactionarrow', function(){
		if($('.drplist').is(":visible"))
		{
			$('.drplist').fadeOut();
		}
		else{
			$('.drplist').fadeIn();
			$('.drplist').css({top:$(this).offset().top+21,left:$(this).offset().left-$('.drplist').width()/1+16}).fadeIn("fast");
		}
	});
	/*$('.dactionarrow').click(function(){
		if($('.drplist').is(":visible"))
		{
			$('.drplist').fadeOut();
		}
		else{
			$('.drplist').fadeIn();
			$('.drplist').css({top:$(this).offset().top+21,left:$(this).offset().left-$('.drplist').width()/1+16}).fadeIn("fast");
		}
	});*/
	$(".drplist, .dactionarrow").mouseup(function() {
		return true
	});
	$(document).mouseup(function() {
		$(".drplist").hide();
	});
}
/*Normal Data Grid action drop down function End*/


$(document).ready(function(){
	designercheckbox(); //Function for Designer Checkbox
	designerradiobutton(); //Function for Designer Radio Button
	designerdropdownselect(); //Function for Designer Select Drop Down
	oulineandbordernone(); //Outline none of html Element
	popupwindowclose(); // Close popup Window
	datagridactiondropdown(); //Normal Data Grid action drop down function
});