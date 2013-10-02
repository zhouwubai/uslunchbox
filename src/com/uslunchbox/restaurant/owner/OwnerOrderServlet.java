package com.uslunchbox.restaurant.owner;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * 
 * @author lli003
 * 
 */
@WebServlet("/OwnerOrderServlet")
public class OwnerOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final static String CURRENT_USER_ATTRIBUTE = "currentOwner";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OwnerOrderServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JSONObject ret = new JSONObject();

		HttpSession session = request.getSession();
		if (session.getAttribute(CURRENT_USER_ATTRIBUTE) == null) {
			ret.put("isvalid", "false");
		} else {
			String action = request.getParameter("action").trim();
			long ownerId = ((Owner) session
					.getAttribute(CURRENT_USER_ATTRIBUTE)).getOwnerId();
			if (action.equals("ownerinfo")) {
				String nickname = ((Owner) session
						.getAttribute(CURRENT_USER_ATTRIBUTE)).getNickName();
				OwnerOrderManagement o = new OwnerOrderManagement(ownerId);
				JSONArray sites = o.retrieveOwnerSites();

				ret.put("isvalid", "true");
				ret.put("nickname", nickname);
				ret.put("sites", sites);
			} else if (action.equals("orderquery")) {
				ret.put("isvalid", "true");
				OwnerOrderManagement o = new OwnerOrderManagement(ownerId);
				int siteId = Integer.valueOf(
						request.getParameter("siteid").trim()).intValue();
				if (o.retrieveOwnerOrders(siteId).size() == 0) {
					ret.put("order", "no");
				} else {
					ret.put("order", "yes");
					ret.put("orderdata", o.retrieveOwnerOrders(siteId));
					ret.put("orderstat", o.queryOrderStatistics(siteId));
					ret.put("orderrevenue", o.queryDailyRevenue(siteId));
				}
			} else {
				throw new Error("Unknown action : " + action);
			}
		}

		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		response.getWriter().write(ret.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		JSONObject ret = new JSONObject();

		HttpSession session = request.getSession();
		if (session.getAttribute(CURRENT_USER_ATTRIBUTE) == null) {
			ret.put("isvalid", "false");
		} else {
			String action = request.getParameter("action").trim();
			long ownerId = ((Owner) session
					.getAttribute(CURRENT_USER_ATTRIBUTE)).getOwnerId();
			if (action.equals("singleorderchange")) {
				int orderId = Integer.valueOf(request.getParameter("orderId"));
				String status = request.getParameter("status");
				OwnerOrderManagement o = new OwnerOrderManagement(ownerId);
				ret.put("result", o.changeOrderStatus(orderId, status));
			}else if(action.equals("multipleorderchange")){
				String[] orderIds = request.getParameter("orderIds").split(",");
				String status = request.getParameter("status");
				OwnerOrderManagement o = new OwnerOrderManagement(ownerId);
				ret.put("result", o.changeSetofOrderStatus(orderIds, status));
			}else{
				throw new Error("Unknown action : " + action);
			}
		}

		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		response.getWriter().write(ret.toString());
	}

}
