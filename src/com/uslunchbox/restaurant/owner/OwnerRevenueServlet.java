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
 * Servlet implementation class OwnerMonthlyStatementServlet
 */
@WebServlet("/OwnerRevenueServlet")
public class OwnerRevenueServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final static String CURRENT_USER_ATTRIBUTE = "currentOwner";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OwnerRevenueServlet() {
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
			long ownerId = ((Owner)session.getAttribute(CURRENT_USER_ATTRIBUTE)).getOwnerId();
			if(action.equals("queryprepaid")){
				JSONArray retJson = new JSONArray();
				OwnerRevenueManagement o = new OwnerRevenueManagement(ownerId);
				retJson = o.queryPrepaidUsers();
				
				ret.put("isvalid", "true");
				ret.put("result", retJson);
			} else if(action.equals("queryrevenuestat")){
				JSONObject retJson = new JSONObject();
				OwnerRevenueManagement o = new OwnerRevenueManagement(ownerId);
				retJson = o.queryOwnerRevenueStatistics();
				
				ret.put("isvalid", "true");
				ret.put("result", retJson);
			} else if(action.equals("querymonthlyusage")){
				JSONObject retJson = new JSONObject();
				OwnerRevenueManagement o = new OwnerRevenueManagement(ownerId);
				retJson = o.queryOwnerRevenueStatement();
				
				ret.put("isvalid", "true");
				ret.put("result", retJson);
			}else{
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
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
