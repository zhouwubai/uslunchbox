package com.uslunchbox.restaurant.order;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.uslunchbox.restaurant.utils.DataEncryption;

/**
 * Servlet implementation class UserConfirmationServlet
 */
@WebServlet("/OrderConfirmationServlet")
public class OrderConfirmationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrderConfirmationServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		JSONObject json = new JSONObject();
		try {
			json.put("result", "failed");
			String orderIdsStr = DataEncryption.decryptStringData(java.net.URLDecoder.decode(request.getParameter("orderid")));
			String[] orderIdStrs = orderIdsStr.split(",");
			long[] orderIds = new long[orderIdStrs.length];
			for (int orderIndex=0; orderIndex<orderIdStrs.length; orderIndex++) {
				orderIds[orderIndex] = Long.parseLong(orderIdStrs[orderIndex]);
			}
			boolean isValid = OrderInfo.validateOrders(orderIds);
			if(isValid == false){
				json.put("result", "failed");
			}else{
				if(OrderInfo.updateOrderStatus(orderIds, "placed")) {
					json.put("result", "success");
				}
			}
			
			json.put("order_id", orderIdsStr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		response.getWriter().write(json.toString());

	}

}
