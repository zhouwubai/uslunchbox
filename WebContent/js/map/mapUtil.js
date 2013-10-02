function showAddress(name, address, status, latlng)
{

	var icon = new GIcon();
	icon.image = "../images/map/green-dot.png";

	if(status == "delivered")
	{
		icon.image = "../images/map/blue-dot.png";
	}
	else if(status == "delivering"){

	}else if(status == "preparing"){
		icon.image = "../images/map/red-dot.png";
	}else if(status == "open"){
		icon.image = "../images/map/restaurant.png";
	}
	else{
		icon.image = "../images/map/yellow-dot.png";
	}

	icon.shadow = "http://www.google.com/mapfiles/shadow50.png";
	icon.iconSize = new GSize(32, 32);
	icon.shadowSize = new GSize(37, 34);
	icon.iconAnchor = new GPoint(9, 34);
	icon.infoWindowAnchor = new GPoint(9, 2);

	//map.setCenter(latlng, 10);        
	var marker = new GMarker(latlng, icon);        
	map2.addOverlay(marker);
	GEvent.addListener(marker, "click", 
			function() {
		marker.openInfoWindowHtml(name+ "<br>" + address + "<br>" + status);  
	}
	);  
}

function showAddress2(name, address, status, latlng, map)
{

	var icon = new GIcon();
	icon.image = "../images/map/green-dot.png";

	if(status == "delivered")
	{
		icon.image = "../images/map/blue-dot.png";
	}
	else if(status == "delivering"){

	}else if(status == "preparing"){
		icon.image = "../images/map/red-dot.png";
	}else if(status == "open"){
		icon.image = "../images/map/restaurant.png";
	}
	else{
		icon.image = "../images/map/yellow-dot.png";
	}

	icon.shadow = "http://www.google.com/mapfiles/shadow50.png";
	icon.iconSize = new GSize(32, 32);
	icon.shadowSize = new GSize(37, 34);
	icon.iconAnchor = new GPoint(9, 34);
	icon.infoWindowAnchor = new GPoint(9, 2);

	//map.setCenter(latlng, 10);        
	var marker = new GMarker(latlng, icon);        
	map.addOverlay(marker);
	GEvent.addListener(marker, "click", 
			function() {
		marker.openInfoWindowHtml(name+ "<br>" + address + "<br>" + status);  
	}
	);  
}

function plotOrders(ids){
	map.clearOverlays();
	plotRestaurantJason(therestaurants);
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

function plotOrders2(ids,map){
	map.clearOverlays();
	plotRestaurantJason(therestaurants);
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
  	            showAddress2(data.name, data.description, data.status, new GLatLng(data.latitude,data.longitude),map);
  	    	});
  		});
	}
}

function plotOrdersJASON(os){
	map.clearOverlays();
	plotRestaurantJason(therestaurants);
	if(os != null){
		for(i=0;i<os.Orders.length;i++){
	  			showAddress(os.Orders[i].time, os.Orders[i].place, os.Orders[i].status, new GLatLng(os.Orders[i].latitude,os.Orders[i].longitude));
			}
	}
}

function plotOrdersJASON2(os,map){
	map.clearOverlays();
	plotRestaurantJason2(therestaurants,map);
	if(os != null){
		for(i=0;i<os.Orders.length;i++){
	  			showAddress2(os.Orders[i].time, os.Orders[i].place, os.Orders[i].status, new GLatLng(os.Orders[i].latitude,os.Orders[i].longitude),map);
			}
	}
}

function plotOrdersJASON3(os,map,p,q){
	map.clearOverlays();
	plotRestaurantJason2(therestaurants,map);
	if(os != null){
		for(i=p;i<q;i++){
	  			showAddress2(os.Orders[i].time, os.Orders[i].place, os.Orders[i].status, new GLatLng(os.Orders[i].latitude,os.Orders[i].longitude),map);
			}
	}
}

function plotRestaurantJason(rs){
	map.clearOverlays();
	if(rs != null){
		for(i=0;i<rs.Restaurants.length;i++){
	  			showAddress(rs.Restaurants[i].name, rs.Restaurants[i].place, rs.Restaurants[i].status, new GLatLng(rs.Restaurants[i].latitude,rs.Restaurants[i].longitude));
		}
	}
}

function plotRestaurantJason2(rs,map){
	map.clearOverlays();
	if(rs != null){
		for(i=0;i<rs.Restaurants.length;i++){
	  			showAddress2(rs.Restaurants[i].name, rs.Restaurants[i].place, rs.Restaurants[i].status, new GLatLng(rs.Restaurants[i].latitude,rs.Restaurants[i].longitude),map);
		}
	}
}

function loadInfo(os){
	map.clearOverlays()
	var select_time = document.getElementById("time").value;
	var select_status = document.getElementById("status").value;
	alert(select_time + " , " + select_status);

	var county = "";
    var zoom = 10;

    if(document.getElementById("status").value == "miami")
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
