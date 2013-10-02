<%@ page language="java" import="java.util.*,com.uslunchbox.restaurant.order.*"  contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>

<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml">
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
        <link href="../formCSS/css_master.css" rel="stylesheet" type="text/css">
        <title>Google Maps JavaScript API Example</title>
        <link href="http://www.google.com/uds/css/gsearch.css" type="text/css" rel="stylesheet"/>
        
        <script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAA67zgbZxG9mFB78rv5MIvohTNxxXlEpbR5xXiBs0PpQ1QipzGUhQtizyNSqWk4tdwsN2dQXHbEyWrxg" type="text/javascript"></script>
        <script type="text/javascript" src="../js/jquery-1.6.4.js"></script>
        
        <script type="text/javascript">
		
         
        <%//String test=request.getParameter("test")+"1";
        String ha=(String)request.getAttribute("ha"); %>
        
        var theorders=eval("("+"<%=ha %>"+")")
        //alert("test2");
        var therestaurants = { "Restaurants" : 
                				[
                 					{"id" : 1, "name" : "South Garden Chinese Restaurant", "place" : "10855 S.W. 72nd Street <br> miami, fl 33173", "longitude" : -80.369325, "latitude" : 25.70223, "status" : "open" }
                 				]  }

        
        </script>
        
        <script type="text/javascript" language="javascript">
                var map = null;
                var geocoder1 = new GClientGeocoder();
                
                function initialize()
                {
                   if (GBrowserIsCompatible()) 
                   {
                        map = new GMap2(document.getElementById("map"));
                        map.addControl(new GSmallMapControl());
                        map.addControl(new GMapTypeControl());
                        map.setCenter(new GLatLng(25.70223,-80.369325), 11);
						map.enableScrollWheelZoom();
						map.enableContinuousZoom();
                        
                        plotRestaurantJason(therestaurants);
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
        </script>

        <script type="text/javascript" src="../js/map/mapUtil.js"></script>
        
    </head>
    
    <body onLoad="initialize()" onUnload="GUnload()" style="overflow:hidden">
        <!-- input type="button" value="test" onClick="plotOrders('11,22,33');" / -->
        <input type="button" value="Show all locations" onClick="plotOrdersJASON(theorders);" />
        <div style="height:5%;backGround-color:#80A6DF; padding:2px; width:100%; border-bottom:2px solid black">
            <select id="time">
                <option value="-1">Select Time</option>
                <option value="0">All</option>
                <option value="1">Yesterday</option>
                <option value="2">Today</option>
                <option value="3">Tomorrow</option>
                <option value="4">Next 7 days</option>
                <option value="5">Next Month</option>
            </select>
            
            <select id="status">
                <option value="-1">Select Status</option>
                <option value="0">All</option>
                <option value="1">Delivered</option>
                <option value="2">Delivering</option>
                <option value="3">Preparing</option>
                <option value="4">Cancelled</option>
            </select>
            <input type="button" value="Show" onClick="loadInfo(theorders);" />
        </div>

        <div id="map" style="overflow:hidden;width: 100%; height: 94%"></div>
    </body>
</html>