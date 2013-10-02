function checkFacebook() {
  var queryObj = getQueryParameters();
  var code = queryObj["code"];
  var state = queryObj["state"];
  if (code != undefined && code != "") { // For facebook redirect login
	$('body').mask('Loading...', 100);
	var ret = $.parseJSON($.ajax({type: "post",
		url: 'UpdateFbServlet',
		async: false,
		data: {"code": code, "state": state},
		dataType: 'json'
	}).responseText);
	$('body').unmask();
	if (ret.result == "error") {
		alert("fail to used the Facebook account! " + ret.message);
	}
	var loc = window.location;
	var newurl = loc.protocol + "//" + loc.host + loc.pathname;
	
    var queryObj = getQueryParameters();
    var search = "";
    for (var k in queryObj) {
    	if (k != "code" && k != "state") {
    		search += (k + "=" + queryObj[k]); 
    	}
    }
    if (search != "") {
    	newurl += "?" + search;
    }
    window.location.href = newurl;
  }
}



Object.size = function(obj) {
    var size = 0, key;
    for (key in obj) {
        if (obj.hasOwnProperty(key)) size++;
    }
    return size;
};

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

function LoginFB() {
	var curUrl = window.location.href;
	curUrl = curUrl.split("#")[0];
	var url = 'LoginFbServlet?redirect_uri='
			+ encodeURIComponent(curUrl);

	$.getJSON(url, function(data) {
		if (data.login != "success") {
			window.location.href = data.redirect;
		}
	});
}

function SignOut() {
	$.post('UserLogOutServlet', function(data){
		location.reload();
	}, 'json');
}

function SignIn() {
	$('#sign-in-dialog').dialog({ minWidth: 410, minHeight: 130,
		open: function() { 
        	$(this).bind("keypress.ui-dialog", function(event) { 
	        	if (event.keyCode == $.ui.keyCode.ENTER) { 
	        		$( this ).dialog( "close" );
					var password = $('#sign-in-password').val();
			    	var email = $('#sign-in-email').val();
			    	$.post('MemberLoginServlet', {'email':email, 'password':SHA1(password)}, function(data){
			    		if (data.result == 'true') {
			    			location.reload();
			    		}
			    		else if (data.result == 'inactivated') {
			    			alert("Your account has not been activated. "+
			    			 "An email has been sent to you email address when you registered the account. " +
			    			 "Please click the email to activate your the account.");
			    		}
			    		else {
			    			alert('The email or password is wrong');
			    		}
			    	}, 'json');
	        	} 
        	});
        	$("#fb-login-button").click(function(){
        		LoginFB();
        	});
        	_wt_click('signin');
        	//$("#fb-login-button").css('background-image', 'url(newresources/images/fb-login-button.png)');
        	//$("#fb-login-button").css('width', '154px').css('height','22px');
        },
        
		buttons: [
		    {text:"Sign in", click:function() {
				$( this ).dialog( "close" );
				var password = $('#sign-in-password').val();
		    	var email = $('#sign-in-email').val();
		    	$.post('MemberLoginServlet', {'email':email, 'password':SHA1(password)}, function(data){
		    		if (data.result == 'true') {
		    			location.reload();
		    		}
		    		else if (data.result == 'inactivated') {
		    			alert("Your account has not been activated. "+
		    			 "An email has been sent to you email address when you registered the account. " +
		    			 "Please click the email to activate your the account.");
		    		}
		    		else {
		    			alert('The email or password is wrong');
		    		}
		    	}, 'json');
				
			}},{
			text: "Cancel", click: function() {
				$( this ).dialog( "close" );
			}}
		]
	});    	
}

function checkSite() {
	var queryParams = getQueryParameters();
	if (queryParams['site'] != undefined) {
		var site_id = queryParams['site'];
		var ret = $.parseJSON(
				$.ajax({type: "GET",
					url: 'SiteResetServlet',
					async: false,
					data: {site:site_id},
					dataType: 'json'
				}).responseText
		);
		if (ret.ret == 'false') {
			alert('Please select a valid site!');
			window.location='index.html';
			return;
		}
	}
	else {
		var ret = $.parseJSON(
				$.ajax({type: "GET",
					url: 'SiteCheckServlet',
					async: false,
					dataType: 'json'
				}).responseText
		);
		if(ret.ret == 'false') {
			alert('Please select the site first');
			window.location='index.html';
		}
	}
}



function validateEmail(email) {
	var emailReg = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	return emailReg.test(email);
}

function validateEmailDomain(email) {
	return true;
//	var domainRegx = /fiu\.edu$/;
//	if (!domainRegx.test(email)) {
//		return false;
//	}
//	else {
//		return true;
//	}
}

function validateContactName(name) {
	var nameReg = /^[\w.\.,]+(\s+[\w.\.,]+)*$/;
	return nameReg.test(name);
}

function validateContactPhone(phoneNumber) {
	var phoneReg1 = /^(\d){4,12}$/;
	var phoneReg2 = /^(\d){3}-(\d){3}-(\d){4}$/;
	return phoneReg1.test(phoneNumber) || phoneReg2.test(phoneNumber);
}

function isBlank(str) {
	return (!str || /^\s*$/.test(str));
}

function adsInit(parentElemId) {
	var params = {
	   account: 'uslunchbox2',
	   tracker: 'adsview',
	   pageurl: location.href,
	};
	
	$.get('http://23.23.192.51/WebTracker/AdsServlet', params, function(data){
		var adsArray = data.ads;
		var tracker = "adsview";
		var adsDiv = "<div><h2 style='font-size:13px;padding:1px 0 4px;text-align:left;margin:0;'>News</h2><ol>";	
		for (var i=0; i<adsArray.length; i++) {
			var ads = adsArray[i];
			var adsId = ads.id;
			adsDiv += "<li style='margin-bottom:10px;'><h3><a href='"+ads.url+"' onclick='_wt_click_sync2(\""+tracker+"\", \""+adsId+"\");'>"+ads.title+"</a></h3>";
			adsDiv += "<div style='color:#093;text-align:left;margin-bottom:1px;display:inline-block;'>"+ads.sponsor+"</div>";
			adsDiv += "<div>"+ads.description+"</div>";
			adsDiv += "</li>";			
		}
		adsDiv += "</ol></div>";
		$('#'+parentElemId).append(adsDiv);
	}, 'json');
	
}

