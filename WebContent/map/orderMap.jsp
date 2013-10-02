<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0); //prevents caching at the proxy
%>
<%@ page import="java.io.*"%> 
<%@ page import="java.util.*"%> 
<%@ page import="org.json.*"%>

<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml">
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
        <link href="../formCSS/css_master.css" rel="stylesheet" type="text/css">
        <title>Google Maps JavaScript API Example</title>
        <link href="http://www.google.com/uds/css/gsearch.css" type="text/css" rel="stylesheet"/>
        
        <!--  ABQIAAAAL9qmO5lfEGs93cfYznb5MxTwM0brOpm-All5BF6PoaKBxRWWERS4hrYR0crB6gfXYU_SK-qrIwjkLA -->
        
        <script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAA67zgbZxG9mFB78rv5MIvohTNxxXlEpbR5xXiBs0PpQ1QipzGUhQtizyNSqWk4tdwsN2dQXHbEyWrxg" type="text/javascript"></script>
        <script type="text/javascript" src="../js/jquery-1.6.4.js"></script>
        
        <script type="text/javascript" language="javascript">
                var ordersArr = new Array();
                var map = null;
                var geocoder1 = new GClientGeocoder();
                
                function initialize()
                {
<%String test=(String)request.getAttribute("test");
String yesman=(String)request.getAttribute("yesman");
%>
                    alert("<%=test %>");
  //                  
  alert("<%=yesman %>");
                    alert("haha");
                   if (GBrowserIsCompatible()) 
                   {
                        map = new GMap2(document.getElementById("map"));
                        map.addControl(new GSmallMapControl());
                        map.addControl(new GMapTypeControl());
                        map.setCenter(new GLatLng(25.70223,-80.369325), 11);
                        
                        //geocoder1 = new GClientGeocoder();
                        
						/*
                        var county = "Florida";
                        var zoom = 10;
                       
                        geocoder1.getLatLng(county, 
                            function(point) {
                                if (!point) {
                                    //alert(address + " not found");      
                                }else {
                                    map.setCenter(point, zoom); 
                                    map.addControl(new GSmallMapControl());
                                    map.addControl(new GOverviewMapControl());						
                                }
                            }
                        );
                        */ 
                   }
                }

                function myloadInfo()
                {   
                    var county = "";
                    var zoom = 10;
             
                    if(document.getElementById("county").value == "miami")
                    {
                        county = "Miami, Fl";
                        zoom = 11;
                    }
                    else 
                    {
                        county = "Florida";
                        zoom = 6;
                    }    
                    geocoder1.getLatLng(county, 
                        function(point) {
                            if (!point) {//alert(address + " not found");      
                            }else {
                                map.setCenter(point, zoom); 
                                map.addControl(new GSmallMapControl());
                                map.addControl(new GOverviewMapControl());						
                            }
                        }
                    );   
                    
                } 
                
                function showAddress(name, address, status, latlng)
                {

                    var icon = new GIcon();

                    //if(status == "Closed" || status == "N")
                    //{
                    //    icon.image = "../images/redmarker.png";
                    //}
                    //else
                    //{
                        icon.image = "../images/map/restaurant.png";
                    //}

                    icon.shadow = "http://www.google.com/mapfiles/shadow50.png";
                    icon.iconSize = new GSize(32, 37);
                    icon.shadowSize = new GSize(37, 34);
                    icon.iconAnchor = new GPoint(9, 34);
                    icon.infoWindowAnchor = new GPoint(9, 2);
                    
                    //map.setCenter(latlng, 10);        
                    var marker = new GMarker(latlng, icon);        
                    map.addOverlay(marker);
                    GEvent.addListener(marker, "click", 
                        function() {
                            marker.openInfoWindowHtml(name+ "<br>" + address + "<br>" + "Status: " + status);  
                        }
                    );  

                }
        </script>
        
        <script type="text/javascript">
        //var theorders = { "Orders" : 
        //    				[ {"id" : 1, "place" : "11200 SW 8th ST", "time" : "12:30pm 2011-12-06", "longitude" : -80.211652, "latitude" : 25.898572, "status" : "delivered" },
        //    					  {"id" : 2, "place" : "4755 NW 97th PL", "time" : "16:30pm 2011-12-06", "longitude" : -80.212308, "latitude" : 25.914623, "status" : "delivering" },
       //     					  {"id" : 3, "place" : "11770 SW 16th ST", "time" : "18:30pm 2011-12-06", "longitude" : -80.241413, "latitude" : 25.834909, "status" : "preparing" }
       //     				]	}

		<%String ordersjson=(String)session.getAttribute("ordersjson"); %>
		//var theorders=eval("("+<%=ordersjson%>+")");
        </script>
        
        <script type="text/javascript">
		function plotOrders(ids){
			if(ids != null){
	  			$.getJSON("http://localhost:8080/miami-restaurant/OrderMap?oids="+ids, function(list) {
		  	    	//alert("eee: " + list.length);
		  	    	$.each(list, function(index, data) {
		  	        	//$('<tr>').appendTo(table)
		  	            //.append($('<td>').text(data.id))
		  	            //.append($('<td>').text(data.name))
		  	            //.append($('<td>').text(data.value));
		  	            //.append($('<td>').text(data.name))
		  	            //.append($('<td>').text(data.value));
		  	            //data.longitude, data.latitude
		  	            showAddress(data.name, data.description, data.status, new GLatLng(data.latitude,data.longitude));
		  	    	});
		  		});
			}
		}

		function plotOrdersJASON(os){
			if(os != null){
				for(i=0;i<os.Orders.length;i++){
	  	  			showAddress(os.Orders[i].time, os.Orders[i].place, os.Orders[i].status, new GLatLng(os.Orders[i].latitude,os.Orders[i].longitude));
	  			}
			}
		}

		function loadInfo(){
			var select_time = document.getElementById("time").value;
			alert(select_time);
		}
  		</script>

    </head>
    <body onLoad="initialize()" onUnload="GUnload()" style="overflow:hidden">
        <input type="button" value="test" onclick="plotOrders('11,22,33');" />
        <input type="button" value="jason" onclick="plotOrdersJASON(theorders);" />
        <div style="height:5%;backGround-color:#80A6DF; padding:2px; width:100%; border-bottom:2px solid black">
            <select id="time">
                <option value="none">Select Time</option>
                <option value="-1">Yesterday</option>
                <option value="0">Today</option>
                <option value="1">Tomorrow</option>
                <option value="2">Next 7 days</option>
            </select>
            <input type="button" value="Show" onClick="loadInfo();" />
        </div>

        <div id="map" style="overflow:hidden;width: 100%; height: 94%"></div>
    </body>
</html>