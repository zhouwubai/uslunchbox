package com.uslunchbox.restaurant.owner;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;


/**
 * 
 * @author lli003
 *
 */
@WebServlet("/OwnerDishScheduleServlet")
public class OwnerDishScheduleServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private final static String CURRENT_USER_ATTRIBUTE = "currentOwner";
	
	public OwnerDishScheduleServlet() {
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
			if(action.equals("readdishlist")){
				OwnerDishSchedule schedule = new OwnerDishSchedule(ownerId);
				ret.put("isvalid", "true");
				ret.put("result", schedule.queryDishList());
			}else if(action.equals("readdishschedule")){
				OwnerDishSchedule schedule = new OwnerDishSchedule(ownerId);
				ret.put("isvalid", "true");
				ret.put("result", schedule.queryDishSchedules());
			}else if(action.equals("readownersite")){
				OwnerDishSchedule schedule = new OwnerDishSchedule(ownerId);
				ret.put("isvalid", "true");
				ret.put("result", schedule.queryOwnerSites());
			}else{
				throw new Error("Unknown action : " + action);
			}
		}
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
			long ownerId = ((Owner)session.getAttribute(CURRENT_USER_ATTRIBUTE)).getOwnerId();
			String str=request.getParameter("dishid").trim();
			int siteId = Integer.valueOf(request.getParameter("siteid").trim()).intValue();
			String date = request.getParameter("date").trim();
			String[] dishes=null;
			if(str.contains(";")){
				dishes=str.split(";");
			}
			
			
			if(action.equals("deletedishschedule")){
				int dishId = Integer.valueOf(str).intValue();
				OwnerDishSchedule schedule = new OwnerDishSchedule(ownerId);
				ret.put("isvalid", "true");
				ret.put("result", schedule.deleteDishSchedule(dishId, siteId, date));
			}else if(action.equals("adddishschedule")){
				OwnerDishSchedule schedule = new OwnerDishSchedule(ownerId);
				if(dishes!=null){
					for(String dish:dishes){
						int dishId = Integer.valueOf(dish).intValue();
						ret.put("isvalid", "true");
						ret.put("result", schedule.addDishSchedule(dishId, siteId, date));
					}
				}else{
					int dishId = Integer.valueOf(str).intValue();
					ret.put("isvalid", "true");
					ret.put("result", schedule.addDishSchedule(dishId, siteId, date));
				}
				
				
			}else{
				throw new Error("Unknown action : " + action);
			}
		}
		response.getWriter().write(ret.toString());

	}


}
