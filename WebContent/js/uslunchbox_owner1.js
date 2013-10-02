	function loadtabs(tabcontent){
		//When page loads...
		$('.'+tabcontent).hide(); //Hide all content
		$('#' + tabcontent + '.tabs li:first').addClass("active").show(); //Activate first tab
		$('.'+tabcontent+':first').show(); //Show first tab content

		//On Click Event
		$('#' + tabcontent + '.tabs li').click(function() {

			$('#' + tabcontent + '.tabs li').removeClass("active"); //Remove any "active" class
			$(this).addClass("active"); //Add "active" class to selected tab
			$('.'+tabcontent).hide(); //Hide all tab content

			var activeTab = $(this).find("a").attr("href"); //Find the href attribute value to identify the active tab + content
			$(activeTab).fadeIn(); //Fade in the active ID content
			return false;
		});
	}

	function getAllDishes(){
    	$('#dish_info').empty();
    	$('#dishdiv').remove();
    	$.get('OwnerDishServlet', {'action':'readdishlist'}, function(data){
    		if(data.isvalid == 'false')
				popup('popUpDiv');
			else{
				//$('#report').append('<tr><th>Dish</th><th>Chinese Name</th><th>Desc</th><th>Lunch Price</th><th>Dinner Price</th><th>Change</th></tr><tbody id="info"></tbody>');
				disheslist(data['result']);
			}
    	},'json');
    	$('#dish_manage').append('<div id=\"dishdiv\" style=\"float:right\"><input type=\"button\" class=\"button\" value =\"add dish\" id=\"adddish\"/></div>');
    	var rnd = Math.floor(Math.random()*10000);
    	$('#adddish').click(function(){
    		$("table#dish_table tr:last").after(
    				'<tr style=\"border-top-style:solid\">'
    				+ '<td><input type=text value=\"\"/></td>'
    				+ '<td><input type=text maxlength=\"100\" size=\"10\" value=\"\"/></td>'
    				+ '<td><textarea cols=12 rows=2></textarea></td>'
    				+ '<td><input type=text value="5" maxlength=\"5\" style="width:70px" value=\"\" onkeyup="this.value=this.value.replace(/[^0-9.]/g,\'\')"/></td>'
    				+ '<td><input type=text value="5" maxlength=\"5\" style="width:70px" value=\"\" onkeyup="this.value=this.value.replace(/[^0-9.]/g,\'\')"/></td>'
    				+ '<td><input class=\"button\" type=button value=\"add\" id=\"addnewdish'+ rnd +'\" onclick=addnewdish('+rnd+')></td></tr>');
    	});
    }
    
    function addnewdish(rnd){
    	var trNode = $('#addnewdish'+rnd).parent().parent();
    	var dishname =  trNode.children().eq(0).children().val();
    	var dishcnname = trNode.children().eq(1).children().val();
    	var dishdesc = trNode.children().eq(2).children().val();
    	var lunchprice =  trNode.children().eq(3).children().val();
    	var dinnerprice =  trNode.children().eq(4).children().val();
    	if(dishname=="" || lunchprice=="" || dinnerprice==""){
    		alert_mes("Please fill in Dish Name, Lunch Price and Dinner Price for adding new dishes!","info");
    		//alert("Please fill in Dish Name, Lunch Price and Dinner Price for adding new dishes!");
    		return;
    	}
    	$.post('OwnerDishServlet', {'action':'addnewdish', 'dishname':dishname, 'dishcnname':dishcnname, 'dishdesc':dishdesc, 'lunchprice':lunchprice, 'dinnerprice':dinnerprice},
    		function(data){
    			if(data.isvalid == 'false')
					popup('popUpDiv');
				else{
					if(data.result == 'false')
						alert("Error when adding new dishes! Please contact with administrators!");
					else
						alert_mes("Add New Dish Success!","success");
						getAllDishes();
				}
    	},'json');
    }
    
    function disheslist(dishes){
    	$.each(dishes, function(i){
			$('#dish_info').append(
				'<tr style=\"border-top-style:solid\">'
				+ '<td><input class=\"dishname\" type=text maxlength=\"100\" size=\"20\" value="' + dishes[i]['dishname'] 
				+ '" dishid=' + dishes[i]['dishid'] + ' /></td>'
				+ '<td><input class=\"dishcnname\" type=text maxlength=\"100\" size=\"10\" value="' + dishes[i]['dishcnname'] 
				+ '" dishid=' + dishes[i]['dishid'] + ' /></td>'
				+ '<td><textarea class=\"dishdesc\" cols=12, rows=2 descid=' + dishes[i]['dishid'] + '>'
				+ dishes[i]['dishdesc'] + '</textarea></td>'
				+ '<td><input class=\"lunchprice\" type=text maxlength=\"5\" style="width:70px" value="' + dishes[i]['lunchprice'] 
				+ '" priceid=' + dishes[i]['dishid'] + ' onkeyup="this.value=this.value.replace(/[^0-9.]/g,\"\")"/></td>'
				+ '<td><input class=\"dinnerprice\" type=text maxlength=\"5\" style="width:70px" value="' + dishes[i]['dinnerprice'] 
				+ '" priceid=' + dishes[i]['dishid'] + ' onkeyup="this.value=this.value.replace(/[^0-9.]/g,\"\")"/></td>'
				+ '<td><img id=dishlimit' + dishes[i]['dishid'] + ' src=\"newresources/images/ok.png\" style=\"display:none\"/></td></tr>');
		});
    	
/*    	var sorting = [[2,1],[0,0]]; 
        // sort on the first column 
        $("dish_table").trigger("sorton",[sorting]); */
    	
		$('.dishname').change(function(event){
    		$.post('OwnerDishServlet', {'action':'updatedishname','dishid':$(this).attr("dishid"), 'dishname':$(this).val(), 'nametype':'EN'},
    	    	function(data){
    				if(data.isvalid == 'false')
						popup('popUpDiv');
					else
    					$('#dishlimit'+data['dishid']).css('display', 'inline').hide().fadeIn(500);
    			},'json');
    	});
		$('.dishdesc').change(function(event){
    		$.post('OwnerDishServlet', {'action':'updatedishdesc','dishid':$(this).attr("descid"), 'dishdesc':$(this).val()},
    	    	function(data){
    				if(data.isvalid == 'false')
						popup('popUpDiv');
					else
    					$('#dishlimit'+data['dishid']).css('display', 'inline').hide().fadeIn(500);
    			},'json');
    	});
		$('.dishcnname').change(function(event){
    		$.post('OwnerDishServlet', {'action':'updatedishname','dishid':$(this).attr("dishid"), 'dishname':$(this).val(), 'nametype':'CN'},
    	    	function(data){
    				if(data.isvalid == 'false')
						popup('popUpDiv');
					else
    					$('#dishlimit'+data['dishid']).css('display', 'inline').hide().fadeIn(500);
    			},'json');
    	});
		$('.lunchprice').change(function(event){
    		$.post('OwnerDishServlet', {'action':'updatedishprice','dishid':$(this).attr("priceid"), 'pricetype':'lunch', 'price':$(this).val()},
    	    	function(data){
    				if(data.isvalid == 'false')
						popup('popUpDiv');
					else
    					$('#dishlimit'+data['dishid']).css('display', 'inline').hide().fadeIn(500);
    			},'json');
    	});
		$('.dinnerprice').change(function(event){
    		$.post('OwnerDishServlet', {'action':'updatedishprice','dishid':$(this).attr("priceid"), 'pricetype':'dinner', 'price':$(this).val()},
    	    	function(data){
    				if(data.isvalid == 'false')
						popup('popUpDiv');
					else
    					$('#dishlimit'+data['dishid']).css('display', 'inline').hide().fadeIn(500);
    			},'json');
    	});
		$('.dishlimit').focus(function(event){
			$('#dishlimit'+$(this).attr("limitid")).fadeOut(500);
		});
	}
    
    function getRevenueStatistics(){    	
    	$('#randu').empty();
    	$.post('OwnerRevenueServlet', {'action':'queryrevenuestat'}, function(data){
    		
    		if(data['isvalid'] == 'false'){
				alert('you need to login!');
			} else {
		var ret = data['result'];
    	var colors = Highcharts.getOptions().colors,
        categories = ret['categories'],
        name = ret['name'],
        data = ret['data'];
    	
    	function setChart(name, categories, data, color) {
            chart.xAxis[0].setCategories(categories, false);
            chart.series[0].remove(false);
            chart.addSeries({
                name: name,
                data: data,
                color: color || 'white'
            }, false);
            chart.redraw();
        }
    	var chart = new Highcharts.Chart({
        chart: {
        	renderTo: 'randu',
            type: 'column'
        },
        title: {
            text: 'Revenue Statistics'
        },
        subtitle: {
            text: 'Click the columns to view daily revenue. Click again to view monthly revenue.'
        },
        xAxis: {
            categories: categories
        },
        yAxis: {
            title: {
                text: 'Total revenue'
            }
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                point: {
                    events: {
                        click: function () {
                            var drilldown = this.drilldown;
                            if (drilldown) { // drill down
                                setChart(drilldown.name, drilldown.categories, drilldown.data, drilldown.color);
                            } else { // restore
                                setChart(name, categories, data);
                            }
                        }
                    }
                },
                dataLabels: {
                    enabled: true,
                    color: colors[0],
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function () {
                        return '$' + this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function () {
                var point = this.point,
                    s = this.x + ':<b>$' + this.y + '</b><br/>';
                if (point.drilldown) {
                    s += 'Click to view ' + point.category + ' revenue';
                } else {
                    s += 'Click to return to browser monthly revenue';
                }
                return s;
            }
        },
        series: [{
            name: name,
            data: data,
            color: 'white'
        }],
        exporting: {
            enabled: false
        },
        credits: {
            enabled: false
        }
    }); // return chart
			}
    	}, 'json');
    }
    
    function getUsageStatistics(){
    	$('#randu').empty();
    	$.post('OwnerRevenueServlet', {'action':'querymonthlyusage'},function(data){
    		
    		if(data['isvalid'] == 'false'){
				alert('you need to login!');
			} else {
		var ret = data['result'];
    	var colors = Highcharts.getOptions().colors,
        categories = ret['categories'],
        name = ret['name'],
        data = ret['data'];
    	
    	function setChart(name, categories, data, color) {
            chart.xAxis[0].setCategories(categories, false);
            chart.series[0].remove(false);
            chart.addSeries({
                name: name,
                data: data,
                color: color || 'white'
            }, false);
            chart.redraw();
        }

    var chart = new Highcharts.Chart({
        chart: {
        	renderTo: 'randu',
            type: 'column',
        },
        title: {
            text: 'Usage Statistics'
        },
        subtitle: {
            text: 'Click the columns to view daily usage. Click again to view monthly usage.'
        },
        xAxis: {
            categories: categories
        },
        yAxis: {
            title: {
                text: 'Total revenue'
            }
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                point: {
                    events: {
                        click: function () {
                            var drilldown = this.drilldown;
                            if (drilldown) { // drill down
                                setChart(drilldown.name, drilldown.categories, drilldown.data, drilldown.color);
                            } else { // restore
                                setChart(name, categories, data);
                            }
                        }
                    }
                },
                dataLabels: {
                    enabled: true,
                    color: colors[0],
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function () {
                        return '$' + this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function () {
                var point = this.point,
                    s = this.x + ':<b>$' + this.y + '</b><br/>';
                if (point.drilldown) {
                    s += 'Click to view ' + point.category + ' usage';
                } else {
                    s += 'Click to return to browser monthly usage';
                }
                return s;
            }
        },
        series: [{
            name: name,
            data: data,
            color: 'white'
        }],
        exporting: {
            enabled: false
        },
        credits: {
            enabled: false
        }
    }); // return chart
			}
    	}, 'json');
    }

    function ownerLogin() {
    	var password = $('#ownerlogin-dialog-password').val();
    	var nickname = $('#ownerlogin-dialog-nickname').val();
    	$.post('OwnerLoginServlet', {'nickname':nickname, 'password':password}, function(data){
    		if (data.result == 'true') {
    			$('#popUpDiv').hide();
    			$('#blanket').hide();
    	    	$.get('OwnerOrderServlet', {'action': 'ownerinfo'},
    	    		function(data){
    					if(data['isvalid'] == 'false'){
    						alert('you need to login!');
    					} else {
    						$('#login-link').text('Welcome '+data.nickname);
    						loadLocation(data.sites);
    					}
    	            },'json');
    		}
    		else {
    			alert('The user name or password is wrong');
    		}
    	}, 'json');
    }
   
   function alert_mes(mes,status){
	   if(status=="err"){
		   //setDiv(status_div);
		   $('#status_div').show();
		   $('#status_div').empty();
		   $('#status_div').append('<h4 class="alert_error">'+mes+'</h4>');
	   }else if(status=="info"){
		   $('#status_div').show();
		   $('#status_div').empty();
		   $('#status_div').append('<h4 class="alert_info">'+mes+'</h4>');
	   }else if(status=="warning"){
		   $('#status_div').show();
		   $('#status_div').empty();
		   $('#status_div').append('<h4 class="alert_warning">'+mes+'</h4>');
	   }else if(status=="success"){
		   $('#status_div').show();
		   $('#status_div').empty();
		   $('#status_div').append('<h4 class="alert_success">'+mes+'</h4>');
	   }else{
		   
	   }
   }
    
    
   function initSchedule(){
	   $('#dish_manage').hide();
	   $('#scheduleinfo').empty();
	  // var dishschedule=new Array();
	   $.getJSON('OwnerDishScheduleServlet',{'action':'readdishschedule'},function(data){
		   
			 //  alert(data['isvalid']);
			   if(data['isvalid']=='true'){
				  
				   schedulelist(data['result']);
			   }else{
				   ownerLogin();
			   }
	   });
	   
		 $( "#datepicker").datepicker();
		 $( "#datepicker" ).change(function() {
			 $( "#datepicker" ).datepicker( "option","dateFormat","yy-mm-dd");
			 });
	   
	     $.getJSON('OwnerDishScheduleServlet',{'action':'readdishlist'},function(data){
			 //  alert(data['isvalid']);
			   if(data['isvalid']=='true'){
				   loadDishes(data['result']);
			   }else{
				   ownerLogin();
			   }
	   });
	   
	   $.getJSON('OwnerDishScheduleServlet',{'action':'readownersite'},function(data){
			 //  alert(data['isvalid']);
			   if(data['isvalid']=='true'){
				//   alert(data['result']);
				   loadSites(data['result']);
			   }else{
				   ownerLogin();
			   }
	   });
   }
   
   function loadSites(sites){
	   $('#site_select').empty();
	   $.each(sites, function(i){
		   var o=new Option(sites[i]['sitename'],sites[i]['siteid']);
		   $(o).html();
		   $('#site_select').append(o);
		});
   }
   
   function loadLocation(sites){
	   $('#locations').empty();	   
	   $.each(sites, function(i){
		   $('#locations').append('<li class="icn_site"><a href="#" id="'+sites[i]['siteid']+'">'+sites[i]['sitearea']+'</a></li>');
	   });
	   $('.icn_site a').click(function(event) {
		   $('#ordertime').remove();
		   $('#stattime').remove();
		   $('#revenuetime').remove();
		   $('#ownerorders').empty();
		   $('#orderstats').empty();
		   $('#revenue_view').empty();
			var site_id=this.id;
			var site_name=this.text;
			setDiv('ownerinfo');
			$('#orderstat').show();
			$('#orderrevenue').show();
			addsuperControler(site_name);
			addNewControler('orderstat');
			addNewControler('orderrevenue');
			addNewControler('ownerinfo');
			$('#owner_header').text("Order Detail Info");
			$('#orderstat_header').text("Order Stat");
			$('#orderrevenue_header').text("Order Revenue");
			/*$('#owner_header').text(site_name);
			$('#orderstat_header').text(site_name);
			$('#orderrevenue_header').text(site_name);*/
			$.get('OwnerOrderServlet', {'action':'orderquery', 'siteid':site_id},
			   	function(data){
			   		if(data.isvalid == 'false')
			   			alert("Please login!");
					else{
						if(data.order == 'no')
							alert("No order available!");
						else{
							loadOrders(data.orderdata);
							loadOrderStats(data.orderstat);
							loadOrderRevenue(data.orderrevenue);
						}
					}
			},'json'); 
	   });
   }
   
   function addsuperControler(data){
	   $('#control_panne').empty();
	   $('#control_panne').append(
			   '<a href="#" id="'+data+'_panne">'+data+'</a>' );
	 /*  $('#'+data+'_panne').click(function(event){
			pannecontrol(data);
		});*/
   }
   
   function addNewControler(data){
	   $('#control_panne').append('<div class="breadcrumb_divider"></div>'+
			   '<a href="#" id="'+data+'_panne">'+data+'</a>' );
	   $('#'+data+'_panne').click(function(event){
			pannecontrol(data);
		});
   }
   
   
   function loadOrderRevenue(data){
	  // $('#orderstat_header').after('<ul id="stattime" class="tabs"></ul>');
	   var tabcontent = "stattime";
	   $.each(data,function(i){
		   $('#revenue_view').append(' <div class="overview_days" id="revenue'+i+'">');
		   $('#revenue'+i).append('<p class="overview_day">'+data[i]['time']+'</p>');
		   var time = '#revenue'+i;
		   revenuelist(data[i]['data'], time);
	   });
   }
   
   function revenuelist(revenue,time){
		$.each(revenue, function(i){
			var trElement = 
			'<p class="overview_count">$'+ revenue[i]['revenue'] +'</p>'
			+'<p class="overview_type">'+ revenue[i]['name'] +'</p>';
			trElement += '</tr>';
			$(time).append(trElement);
		});
	}
   
   function loadOrderStats(data){
	   $('#orderstat_header').after('<ul id="stattime" class="tabs"></ul>');
	   var tabcontent = "stattime";
	   $.each(data,function(i){
		   $('#stattime').append('<li><a href="#stattab'+i+'">'+data[i]['time']+'</a></li>');
		   var content = "";
		   content += '<div id="stattab'+i+'" class="' + tabcontent + '">';
		   content += '<table class="tablesorter" cellspacing="0">';
		   content += '<thead><tr><th>Dish</th><th>Staple</th><th>Quantity</th></tr></thead>';
		   content += '<tbody id = "stattable' + i + '"></tbody>';
		   var time = '#stattable'+i;
		   content += '</table></div>';
		   $('#orderstats').append(content);
		   statlist(data[i]['data'], time);
	   });
	   loadtabs(tabcontent);
   }
   
   function statlist(stats,time){
		$.each(stats, function(i){
			var trElement = '<tr style=\"border-top-style:solid\"><td>'+ stats[i]['dishname'] +'</td>'
			+ '<td>'+ stats[i]['staplename'] + '</td>'
			+ '<td>'+ stats[i]['quantity'] + '</td>';
			trElement += '</tr>';
			$(time).append(trElement);
		});
	}
   
   function loadOrders(data){
	   $('#owner_header').after('<ul id="ordertime" class="tabs"></ul>');
	   var tabcontent = "ordertime";
	   $.each(data,function(i){
		   $('#ordertime').append('<li><a href="#ordertab'+i+'">'+data[i]['time']+'</a></li>');
		   var content = "";
		   content += '<div id="ordertab'+i+'" class="' + tabcontent + '">';
		   content += '<table class="tablesorter" cellspacing="0">';
		   content += '<thead><tr><th>OrderID</th><th>Address</th><th>Deliver Time</th><th>Contact</th><th>Phone</th><th>Status</th><th>Payment</th></tr></thead>';
		   content += '<tbody id = "ownertable' + i + '"></tbody>';
		   var time = '#ownertable'+i;
		   content += '</table></div>';
		   $('#ownerorders').append(content);
		   orderlist(data[i]['data'], time);
	   });
	   loadtabs(tabcontent);
   }
   
   function orderlist(orders,time){
		$.each(orders, function(i){
			var trElement = '<tr style=\"border-top-style:solid\"><td>'+ orders[i]['order_id'] +'</td><td>'+ orders[i]['location']
			+'</td><td id=order' + orders[i]['order_id'] + 'time>'+orders[i]['deliver_time']
			+'</td><td>'+ orders[i]['contact_name'] + '</td><td>' + orders[i]['contact_phone'] +'</td>'
			+ '<td><input class=\"button\"type=button id=order'+ orders[i]['order_id']+' value='
					+ orders[i]['status']+' onclick=orderchange("'
					+ orders[i]['order_id']+ '","' + orders[i]['status'] +'")></td>';
			if(orders[i]['payment'] != 0)
				trElement += '<td >$&nbsp;' + orders[i]['payment'] +'</td>';
			else
				trElement += '<td >0</td>';
			trElement += '</tr>';
			$(time).append(trElement);
			dishlist(orders[i]['order_id'],orders[i]['detail'],time);
		});
	}
   
   function orderchange(order_id,status){
   	if(status == "placed")
   		status = "delivered";
   	else if(status == "delivered")
   		status = "placed";
   	else
   		status = "unplaced";
   	$.post('OwnerOrderServlet', {'action':'singleorderchange','orderId':order_id, 'status':status}, function(data){
   		if (data['result'] == true) {
   			if(status == "delivered"){
   				$("#order"+order_id).val("delivered");
   				$("#order"+order_id).attr('onclick', 'orderchange("' + order_id + '","delivered")');
   			}else if(status == "placed"){
   				$("#order"+order_id).val("placed");
   				$("#order"+order_id).attr('onclick', 'orderchange("' + order_id + '","placed")');
   			}else{
   				$("#order"+order_id).val("unplaced");
   			}
   		} else {
   			alert('Wrong order information');
   		}
   	}, 'json');
   }
   
   function dishlist(orderid,dishes,time){
   	$(time).append('<tr><td></td><td><strong>Dish</strong></td><td><strong>Staple</strong></td><td><strong>Quantity</strong></td><td></td><td></td><td></td></tr>');
   	$.each(dishes, function(i){
   		$(time).append('<tr><td></td><td>' + dishes[i]['dish_english'] + '</td>'
   			+ '<td>' + dishes[i]['staple_food_name'] + '</td>'
   			+ '<td>' + dishes[i]['quantity'] + '</td><td></td><td></td><td></td>'
   			+ '</tr>');
   	});
   }
   
   
   function removeSchedule(trNode){
	   var date =  trNode.children().eq(0).children().eq(0).val();
	   var dishid =  trNode.children().eq(2).children().eq(0).val();
	   var siteid =  trNode.children().eq(4).children().eq(0).val();
   	$.post('OwnerDishScheduleServlet', {'action':'deletedishschedule','date':date, 'dishid':dishid, 'siteid':siteid},
   		function(data){
   			if(data.isvalid == 'false')
   				alert_mes("Error when delete a dishe schedule! Please contact with administrators!",'err');
				else{
					if(data.result == 'false')
						alert_mes("Error when delete a dishe schedule! Please contact with administrators!",'err');
					else
						alert_mes('Dish Schedule has been removed !','success');
						initSchedule();
				}
   	},'json'); 
   }
   
   
   function schedulelist(schedules){
		$.each(schedules, function(i){
			$("#scheduleinfo").append(
				'<tr style="border-top-style:solid">'
				+'<td><input type="hidden" class="scheduledate" value="' +schedules[i]['time']+'" />'+schedules[i]['time']+'</td>' 
				+'<td><input type="hidden" class="scheduledate" value="' +schedules[i]['time']+'" />'+schedules[i]['weekday']+'</td>' 
				+'<td ><input type="hidden" class="scheduledish" value="' +schedules[i]['dishid']+'" />'+schedules[i]['dishname']+'</td>' 
				+'<td><a class="remove" href="#" "><img src="newresources/images/foodimages/food_'+schedules[i]['dishid']+'.jpg" style="width:60px;height:60px"/></a></td>'
				+'<td><input type="hidden" class="schedulesite" value="' +schedules[i]['siteid']+'" /><img src="newresources/images/lunchbox/site_'+schedules[i]['siteid']+'.jpg" style="width:60px;height:60px"/></a></td>'
				+'<td><a class="remove" href="#" "><img src="newresources/images/remove.png" style="width:40px;height:40px"/></a></td></tr>');
		});
		$('.remove').click(function(){
	  		 removeSchedule($(this).parent().parent());
		});
	};
   
   function loadDishes(data) {
	   
	   $('#dish').click(function(event){
		   popupDiv();
		   updateDishItems(data);
		});
	   
	   };
	   
	   function updateDishItems(data) {
	    	$('#items').empty();
	    	$.each(data, function(index, dishJSON) {
	        	var itemImage = 'newresources/images/foodimages/food_'+dishJSON.dishid+'.jpg';
	        	var item = '<div class="item"><a href="#" id='+dishJSON.dishid+'><img src="'+itemImage+
				'" width=182px height=109px /><br /><p style="height:36px;line-height:12px">'+
				dishJSON.dishname+"</a></p><span class='price' style='height:36px'> </span><br style='margin-bottom:10px'/>";
	        	item += '<input type="checkbox" name="dish" id="check'+dishJSON.dishid+'" value="'+dishJSON.dishname+'"/>';		
				
	        	var review= dishJSON.count;
			
				item += '</a>';
							
	        	item += "</div>";
	        	
	    		$('#items').append(item);
			});
	    	
	    	
	    	$('.item a').click(function(event){
	    		var dish=this.id;
	    		var status=$('#check'+dish).is(':checked');
	    		
	    		//alert(status);
	    		$('#check'+dish ).prop('checked', !status);
	    	});
	    	//initDishRating();
	    	
	    }
	   
	   function setDish(){
		   var selectedDishID="";
		   var selectedDishName="";
		   $("input:checkbox[name=dish]:checked").each(function()
				   {
			   			var str=this.id;
			   			var id=str.replace("check","");
			   			var dish_name=$(this).val();
			   			selectedDishID += id+';';
			   			selectedDishName += dish_name+';';
				       // add $(this).val() to your array
				   });
		   $('#dish').val(selectedDishName);
		   $('#dish-id').val(selectedDishID);
		   
		   hideDiv();
		   //alert(selectedDish);
	   }
	   
	   function popupDiv() {   
		    
		    //add and show mask div
		    $("<div id='mask'></div>").addClass("mask")   
		                               .width(1600)   
		                               .height(1600)   
		                               .click(function() {hideDiv(); })   
		                               .appendTo("body")   
		                              .fadeIn(200);   
		    
		    //$('input[name=id]').val(id);
		    
		    $("#dishes").css({"position": "absolute"})   
		           .animate({left: 300,    
		                     top: 200, opacity: "show" }, "slow");   
		                    
		}   
		  
		 function hideDiv() {   
		     $("#mask").remove();   
		     $("#dishes").animate({left: 0, top: 0, opacity: "hide" }, "slow");   
		 }    
	   
   function addASchedule(){
   	
   	var date=$('#datepicker').val();
   	var dishid=$('#dish-id').val();
   	var siteid=$('#site_select').val();
   	if(date=="" || dishid=="" || siteid==""){
		alert_mes("Please select a Date, a Dish and a Location for adding a new schedule!","info");
		//alert("Please fill in Dish Name, Lunch Price and Dinner Price for adding new dishes!");
		return;
	}
   	
	$.post('OwnerDishScheduleServlet', {'action':'adddishschedule','date':date, 'dishid':dishid, 'siteid':siteid},
  	   		function(data){
  	   			if(data.isvalid == 'false')
  	   			alert_mes("Error when delete a dishe schedule! Please contact with administrators!","err");
  					else{
  						if(data.result == 'false')
  							alert_mes("Error when delete a dishe schedule! Please contact with administrators!","err");
  						else
  							alert_mes("Add a new dish schedule success!",'success');
  							initSchedule();
  					}
  	   	},'json');
   }
    