package com.uslunchbox.restaurant.owner;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.uslunchbox.restaurant.dish.Dish;


/**
 * 
 * @author lli003
 *
 */
@WebServlet("/OwnerDishServlet")
public class OwnerDishServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private final static String CURRENT_USER_ATTRIBUTE = "currentOwner";
	
	public OwnerDishServlet() {
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
				OwnerDishManagement o = new OwnerDishManagement(ownerId);
				ret.put("isvalid", "true");
				ret.put("result", o.queryAllDishes());
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
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JSONObject ret = new JSONObject();
		
		HttpSession session = request.getSession();
		if (session.getAttribute(CURRENT_USER_ATTRIBUTE) == null) {
			ret.put("isvalid", "false");
		} else {
			request.setCharacterEncoding("UTF-8"); 
			
			String action = request.getParameter("action").trim();
			long ownerId = ((Owner)session.getAttribute(CURRENT_USER_ATTRIBUTE)).getOwnerId();
			
			if(action.equals("updatedishname")){
				int dishId = Integer.valueOf(request.getParameter("dishid").trim()).intValue();
				String dishName = request.getParameter("dishname").trim();
				String nameType = request.getParameter("nametype").trim();
				OwnerDishManagement o = new OwnerDishManagement(ownerId);
				
				ret.put("isvalid", "true");
				ret.put("result", o.manageDishName(dishId, nameType, dishName));
				ret.put("dishid", dishId);
			}else if(action.equals("updatedishdesc")){
				int dishId = Integer.valueOf(request.getParameter("dishid").trim()).intValue();
				String dishdesc = request.getParameter("dishdesc").trim();
				OwnerDishManagement o = new OwnerDishManagement(ownerId);
				
				ret.put("isvalid", "true");
				ret.put("result", o.manageDishesDesc(dishId, dishdesc));
				ret.put("dishid", dishId);
			}else if(action.equals("updatedishprice")){
				int dishId = Integer.valueOf(request.getParameter("dishid").trim()).intValue();
				String priceType = request.getParameter("pricetype").trim();
				String price = request.getParameter("price").trim();
				OwnerDishManagement o = new OwnerDishManagement(ownerId);
				
				ret.put("isvalid", "true");
				ret.put("result", o.manageDishPrice(dishId, priceType, price));
				ret.put("dishid", dishId);
			}else if(action.equals("addnewdish")){
				Dish dish = new Dish();
				dish.english_name = request.getParameter("dishname").trim();
				dish.chinese_name = request.getParameter("dishcnname").trim();
				dish.desc = request.getParameter("dishdesc").trim();
				dish.lunch_price = Double.valueOf(request.getParameter("lunchprice").trim());
				dish.dinner_price = Double.valueOf(request.getParameter("dinnerprice").trim());
				
				OwnerDishManagement o = new OwnerDishManagement(ownerId);
				ret.put("isvalid", "true");
				ret.put("result", o.addNewDish(dish));
			}else{
				throw new Error("Unknown action : " + action);
			}
		}
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		response.getWriter().write(ret.toString());
	}


}
