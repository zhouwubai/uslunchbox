/*var headID = document.getElementsByTagName("head")[0];
var scriptObj = document.createElement('script');
scriptObj.type = 'text/javascript';
scriptObj.src = 'js/css-pop.js';
headID.appendChild(scriptObj);

var cssObj = document.createElement('link');
cssObj.type = 'text/css';
cssObj.rel = 'stylesheet';
cssObj.src = 'newresources/css/popup.css';
cssObj.media = 'screen';
headID.appendChild(cssObj);*/

function dialog(title, content, width, height) {

	var temp_float = new String;
	temp_float = "<div id=\"blanket\" style=\"display:none;\"></div>";
	temp_float += "<div id=\"popUpDiv\" style=\"display:none;\">";
	temp_float += "<div id=\"mealdialog\" class=\"dialog widget-header\" style=\"filter:alpha(opacity=0);opacity:100;\">";
	temp_float += "<div id=\"mealdialogtop\" class=\"dialog-titlebar widget-header corner-all helper-clearfix\">";
	temp_float += "<div id=\"mealdialogtitle\" class=\"dialog-title dialog-text\"></div>";
	temp_float += "<a href=\"\" class=\"dialog-close\"></a></div>";

	temp_float += "<div class=\"dialog-content widget-content\">";
	temp_float += "<div id=\"mealtext\" class=\"dialog-text\"></div></div>";
	temp_float += "<div class=\"div-left\">";
	temp_float += "<span><img src=\"newresources/images/logo.png\" alt=\"\" width=120px height=30px /></span></div>";
	temp_float += "</div></div>";

	$("body").append(temp_float);

	$("#mealdialogtitle").html(title);
	$("#mealtext").html(content);

	$("#mealdialog").css({
		display : "block",
		width : width,
		height : height
	});
	$("#mealdialog").show();
	popup('popUpDiv');
}

function dialogYesOrNo(title, content, YesCallBack, NoCallBack) {
	var blanket = $('<div>').attr("id", "blanket").css("display", "none");
	$("body").append(blanket);

	var popUpDiv = $('<div>').attr("id", "popUpDiv").css("display", "none");
	$("body").append(popUpDiv);

	var confirmDialog = $('<div>').attr("id", "confirmdialog").addClass(
			"dialog widget-header").css({
		// filter : alpha(opacity=0),
		opacity : 100
	});
	$("#popUpDiv").append(confirmDialog);

	var confirmDialogTop = $('<div>').attr("id", "confirmdialogtop").addClass(
			"dialog-titlebar widget-header corner-all helper-clearfix");
	$("#confirmdialog").append(confirmDialogTop);
	var confirmDialogTitle = $('<div>').attr("id", "confirmdialogtitle")
			.addClass("dialog-title dialog-text");
	$("#confirmdialogtop").append(confirmDialogTitle);
	$("#confirmdialogtitle").html(title);
	$("#confirmdialogtop").append(
			"<a href=\"\" class=\"dialog-close\"></a></div>");

	var confirmDialogText = $('<div>').attr("id", "confirmtext").addClass(
			"dialog-text").html(content);
	var confirmDialogInfo = $('<div>').attr("id","confirmdialoginfo").addClass("dialog-content widget-content")
			.append(confirmDialogText);
	$("#confirmdialog").append(confirmDialogInfo);
	var confirmDialogYes = $('<div>').attr("id","yes").addClass("button").css({width: "50px", height: "20px", float: "left"});
	$("#confirmdialoginfo").append(confirmDialogYes);
	$("#yes").html("Yes");
	$("#yes").click(YesCallBack);
	
	var confirmDialogNo = $('<div>').attr("id","no").addClass("button").css({width: "50px", height: "20px", float: "right"});
	$("#confirmdialoginfo").append(confirmDialogNo);
	$("#no").html("no");
	$("#no").click(NoCallBack);

	var confirmDialogLogo = $('<div>')
			.addClass("div-left")
			.append(
					"<span><img src=\"newresources/images/logo.png\" alt=\"\" width=120px height=30px /></span>");
	$("#confirmdialog").append(confirmDialogLogo);
	
	$("#confirmdialog").css({
		display : "block",
		width : 400,
		height : "auto"
	});
	$("#confirmdialog").show();
	popup('popUpDiv');

}