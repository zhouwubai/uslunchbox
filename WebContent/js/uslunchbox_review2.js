	function getQueryParameters() {
		var querystring = location.search.replace( '?', '' ).split( '&' );
		// declare object
		var queryObj = {};
		// loop through each name-value pair and populate object
		for ( var i=0; i<querystring.length; i++ ) {
		      // get name and value
		      var name = querystring[i].split('=')[0];
		      var value = querystring[i].split('=')[1];
		      // populate object
		      queryObj[name] = value;
		}
		return queryObj;
	}

	function checkDishID() {
		var queryParams = getQueryParameters();
		if (queryParams['id'] != undefined) {
			var dish_id = queryParams['id'];
			var ret = $.parseJSON(
					$.ajax({type: "GET",
						url: 'ReviewCheckDishServlet',
						async: false,
						data: {id:dish_id},
						dataType: 'json'
					}).responseText
			);
			if (ret.ret == 'false') {
				alert('Please select a dish');
				window.location='onlineorder.html';
				return;
			}
			id=dish_id;
		}
		else {
				alert('Please select a dish');
				window.location='onlineorder.html';
		}
	}
	
	function checkRestaurantID() {
		var queryParams = getQueryParameters();
		if (queryParams['restaurant'] != undefined) {
			var restaurant_id = queryParams['restaurant'];
			var ret = $.parseJSON(
					$.ajax({type: "GET",
						url: 'ReviewCheckRestaurantServlet',
						async: false,
						data: {id:restaurant_id},
						dataType: 'json'
					}).responseText
			);
			if (ret.ret == 'false') {
				alert('Please select a restaurant');
				window.location='onlineorder.html';
				return;
			}
			id=restaurant_id;
		}
		else {
				alert('Please select a restaurant');
				window.location='onlineorder.html';
		}
	}
	

	function back(){
		window.location='onlineorder.html';    
	} 

	function DrawReviewList(data){
		var reviewTable='<table width="100%">';
		var reviews=new Array();
		var reviewCount=0;
		$.each(data, function(i){
			reviews[reviewCount++] = data[i]['reviewid'];
			reviewTable += '<tr><td valign="top" colspan="2">';
			reviewTable += '<img src="newresources/images/reviews/r' + data[i]['reviewrating'] + '0.png" style:"align:top"/>';
			reviewTable += '</td></tr>';
			reviewTable += '<tr><td class="avatarTd">';
			reviewTable += '<img src=" '+ userDefalutImage +'"></td>';
			reviewTable += '<td><h3>' + data[i]['username'] + '</h3>';
			reviewTable += '<p class="review_content">' + data[i]['reviewcontent'] + '</p>';
			reviewLikesArray[data[i]['reviewid']] = data[i]['likereview'];
			reviewTable +=  '<span class="time">Reviewed on ' + data[i]['reviewdate'] + '</span>';
			reviewTable += getLikeReview(data[i]['reviewid'], (data[i]['likereview'].length == 0));
			reviewTable += '</table></td></tr>';
			reviewTable += '<tr><td colspan="3" class="spliter">&nbsp;</td></tr>';
			reviewTable += '<tr><td colspan="3">&nbsp;</td></tr>';
		});
		reviewTable += '</table>';
		$('#reviews').empty();
		$('#reviews').append(reviewTable);
	  	
	  	$.each(reviewPopupContent, function(i){
	  		SetReviewHover(i);
	  	});
	}
	
	var moveLeft = -20;
  	var moveDown = 20;
	function SetReviewHover(i){
		$("#reviewPop"+i).hover(function(e) {
			var offset = $(this).offset();
			$('div#divPopup').empty();
			$('div#divPopup').append(reviewPopupContent[i]);
			$('div#divPopup').show()
		      .css('top', offset.top + moveDown)
		      .css('left',offset.left + moveLeft)
		      .appendTo('body');
		  	}, function() {
		    	$('div#divPopup').hide();
		  	});
  		if(reviewDialogContent[i]!=null){
  			$("#reviewPop"+i).click(function() {
  				$('#dialog-form').empty();
				$('#dialog-form').append(reviewDialogContent[i]);
		    	$('#dialog-form').dialog( "open" );
		    });
  		}
	}
	
	
	
	function getLikeReview(review_id, isHide){
		var tableStr = "";
		var hideComment = "";
		if(isHide) hideComment = 'style="display:none"';
		tableStr+='<tr id="commentRow' + review_id + '" ' + hideComment + '><td></td>';
		tableStr+='<td><table class="commentList" cellspacing="1px">';
		tableStr+='<tr><td style="background-color:white"><img style="padding-left:20px; margin-bottom:-1px" src="newresources/images/reviews/up.png"/></td></tr>';

		
		tableStr += '<tr id="reviewLikeUnitity'+review_id+'"><td>';
		var tableObj = GetReviewLiker(review_id);
		tableStr += tableObj.str;
		tableStr += '</td></tr>';
		var buttonOption = "";
		var buttonText = "";
		
		if(!userObj['userid']){
			buttonOption = 'onclick="SignIn()"';
			buttonText = "Like";
		}
		else{
			if(tableObj.isLiked){
				buttonOption = 'onclick="LikeReview(' + review_id + ', \'unlike\')"';
				buttonText = "Unlike";
			}
			else{
				buttonOption = 'onclick="LikeReview(' + review_id + ', \'like\')"';
				buttonText = "Like";
			}
		}
		tableStr = '<span id="btnLikeReview' + review_id + '" class="linkButton" ' + buttonOption + '>' + buttonText + '</span>&nbsp<span></span>'  + tableStr;	
		return tableStr;
	}
	
	function GetReviewLiker(review_id){
		var tableObj = new Array();
		tableObj.str = "";
		tableObj.isLiked=false;
		var reviewLikes= reviewLikesArray[review_id];
		if(reviewLikes.length > 0){
			reviewPopupContent[review_id] = '<table width="100%" cellpadding="0" cellspacing="0"><tr><td>' +
				'<table width="100%" cellpadding="0" cellspacing="0"><tr><td><img src="newresources/images/reviews/up_black.png" style="padding-left:20px"></td></tr>';
			reviewDialogContent[review_id] = '<table width="100%">';
			var popupRows = "";
			var dialogRows = "";
			var likeCount = reviewLikes.length;
			var index = 0;
			$.each(reviewLikes, function(j){
				if(reviewLikes[j]['userid'] == userObj['userid']) {
					tableObj.isLiked = true;
					likeCount--;
				}
				else {
					index=j;
					popupRows += '<tr><td style="background-color:black; padding:0 5px 0 5px; color:white">' + reviewLikes[j]["username"] + '</td></tr>';
					dialogRows += '<tr><td class="avatarTd"><img src="'+userDefalutImage +'"></td><td><a style="color:#3B5998; font-size:16px">' + reviewLikes[j]['username'] + '</a></td></tr>';
				}
			});
			
			if(tableObj.isLiked&&likeCount==0){
				tableObj.str = 'You like this.';
			} else if(tableObj.isLiked && reviewLikes.length == 1){
				tableObj.str = 'You and ' + '<a href="user.jsp?id=' + reviewLikes[index]['userid'] + '">' + reviewLikes[0]['username'] + '</a> like this.';
			} else if (tableObj.isLiked && reviewLikes.length >1){
				tableObj.str = 'You and ' + '<span id="reviewPop' + review_id + '" class="linkButton">' + likeCount + ' other people</span> like this.';
			} else if(!tableObj.isLiked && reviewLikes.length == 1){
				tableObj.str = '<a href="user.jsp?id=' + reviewLikes[index]['userid'] + '">' + reviewLikes[0]['username'] + '</a> like this.';
			} else if (!tableObj.isLiked && reviewLikes.length >1){
				tableObj.str = '<span id="reviewPop' + review_id + '" class="linkButton">' + likeCount + ' people</span> like this.';
			}
			
			reviewPopupContent[review_id] += popupRows;
			reviewDialogContent[review_id] += dialogRows;
			reviewPopupContent[review_id] += '</table></td></tr></table>';
			reviewDialogContent[review_id] += '</table>';
			
		}
		return tableObj;
	}
	
	function popupDiv() {   
	    
	    //add and show mask div
	    $("<div id='mask'></div>").addClass("mask")   
	                               .width(1600)   
	                               .height(1600)   
	                               .click(function() {hideDiv(); })   
	                               .appendTo("body")   
	                              .fadeIn(200);   
	    
	    $('input[name=id]').val(id);
	    
	    $("#review").css({"position": "absolute"})   
	           .animate({left: 700,    
	                     top: 350, opacity: "show" }, "slow");   
	                    
	}   
	  
	 function hideDiv() {   
	     $("#mask").remove();   
	     $("#review").animate({left: 0, top: 0, opacity: "hide" }, "slow");   
	 }  
	 

	
	function SearchAndRemove(array, item, value){
		for (var i=0; i<array.length; i++) {
		    if (array[i][item] == value) {
		        array.splice(i, 1);
		        break;
		    }
		}
	}
	
	function InsertToArray(array, obj){
		if(!array) array= new Array();
		array.splice(0, 0, obj);
	}
	
	function LikeReview(id, likeOrUnlike){
		$.post("ReviewLikeServlet", { review_id: id, opration: likeOrUnlike},
			function(data) {
				if(data.result){
		   	 		if(likeOrUnlike == 'like'){
		   		 		InsertToArray(reviewLikesArray[id], userObj);
		   		 		$('#btnLikeReview'+id).html('Unlike');
		   		 		$('#btnLikeReview'+id).attr('onclick', 'LikeReview(' + id + ', \'unlike\')');
		   		 		$('#reviewLikeUnitity'+id).css('display','table-row');
		   		 		//$('#commentRow'+id).css('display','table-row');
			   	 	}
			   	 	else{
		   		 		SearchAndRemove(reviewLikesArray[id], 'userid', userObj['userid']);
		   		 		$('#btnLikeReview'+id).html('Like');
		   		 		$('#btnLikeReview'+id).attr('onclick', 'LikeReview(' + id + ', \'like\')');
		   		 		if(reviewLikesArray[id].length==0) 
		   		 			$('#reviewLikeUnitity'+id).css('display','none');
			   	 	}
		   	 		$('#reviewLikeUnitity'+id).empty().append('<td>'+GetReviewLiker(id).str+'</td>');
		   	 		SetReviewHover(id);
		    	}
	   		});
	}
	
	  
    function initRestaurent(){
    	$.getJSON('ReviewRestaurentPageServlet',{'id':id}, function(data){
    		var restaurent_intro="";
    		$.each(data, function(index, entry) {
    			restaurent_intro += '<div class="info" style="float:left;width:315px;margin-bottom:14px;heigh:200">';
    			restaurent_intro += '<p class="title">'+entry.restaurant_name+'</p>';
    			restaurent_intro += '<img src="newresources/images/restaurantimages/r'+id+'.jpg" width="300" height="180"></div> ';
    			restaurent_intro += '<div class="info" style="float:left;width:340px;margin-right:6px;"> <div><p class="title">INFORMATION</p> ' ;  			
    			restaurent_intro += entry.intro +'</div><div><input type="button" class="button" value="Make Review For Restaurant" onclick="reviewMake()"/></div>'
    			restaurent_intro += '<div id="restaurent_review">';
    			restaurent_intro +='<table id="ReviewRestaurent" style="width:300px"><tr><td style="width:100px">';
    			restaurent_intro +=" Food  " +'<img src="newresources/images/reviews/r'+entry.food_rating +'.png"/></td></tr><tr><td style="width:100px">';
    			restaurent_intro +=" Price  " +'<img src="newresources/images/reviews/r'+entry.price_rating +'.png"/></td></tr><tr><td style="width:100px">';
    			restaurent_intro +=" Service  " +'<img src="newresources/images/reviews/r'+entry.serv_rating +'.png"/></td></tr><tr><td style="width:100px">';
    			restaurent_intro +=" Atmosphere  " +'<img src="newresources/images/reviews/r'+entry.atmo_rating +'.png"/>';
    			restaurent_intro +="</td></tr><table></div>";
    			
    	//		restaurent_intro +=entry.intro;
    			
    		});
    		var rimg='<img style="float:left;" src="newresources/images/restaurantimages/banner_'+id+'.png"/>'
    		$('#intro').append(restaurent_intro);
    		$('#banner').empty();
    		$('#banner').append(rimg);
    	//	$('#restaurant_intro').append(restaurent_intro);
    	})
    	
    	$.get('DishAllServlet',{'id':id}, function(data){
        	
    		//alert(data);
    		updateDishItems(data);
        	
    		$('body').unmask();
    	}, 'json');
    }
    
    
    function updateDishItems(data) {
    	$('#items').empty();
    	$.each(data, function(index, dishJSON) {
        	var itemImage = 'newresources/images/foodimages/food_'+dishJSON.id+'.jpg';
        	var item = '<div class="item"><a href="dishreview.html?id=' + dishJSON.id+'"><img src="'+itemImage+
			'" width=272 height=226 /><br /><p style="height:36px;line-height:12px">'+
			dishJSON.name+"</a></p><span class='price' style='height:36px'> $"+
			dishJSON.price+"</span><br style='margin-bottom:10px'/>";
			
        			
			item += '<img src="newresources/images/reviews/r' + dishJSON.rating + '.png"/>';
			var review= dishJSON.count;
			if (review>0){
				if(review>1){
					item +='<a href="dishreview.html?id='+ dishJSON.id+'">' +dishJSON.count+ ' reviews';
				}else{
					item +='<a href="dishreview.html?id='+ dishJSON.id+'"> 1 review';
				}
			}else{
				item += '<a href="dishreview.html?id=' + dishJSON.id +'" >Be 1st reviewer';
			}
			item += '</a>';
						
        	item += "</div>";		
    		$('#items').append(item);
		});
    	
    	//initDishRating();
    	
    }
    
   
	
