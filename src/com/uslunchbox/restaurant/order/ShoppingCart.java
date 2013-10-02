package com.uslunchbox.restaurant.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.uslunchbox.restaurant.dish.Dish;
import com.uslunchbox.restaurant.dish.SideDish;
import com.uslunchbox.restaurant.dish.StapleFood;

public class ShoppingCart {
	
	private int totalNumItems = 0;
	
	private List<OrderDishInfo> dishes = new ArrayList<OrderDishInfo>();
	
	private final static String SESSION_ATTRIBUTE = "ShoppingCart";
	
	public ShoppingCart() {
		
	}
	
	public void addItem(int siteId, int dishId, int staple_food_id, int side_dish_id, int quantity, String location, String deliverTime) {
		boolean bAlreadyExist = false;
		for (int i=0; i<dishes.size(); i++) {
			OrderDishInfo dishInfo = dishes.get(i);
			if (dishInfo.getDishId() == dishId && dishInfo.getStapleFoodId() == staple_food_id) {
				dishInfo.setQuantity(dishInfo.getQuantity()+quantity);
				bAlreadyExist = true;
				break;
			}
		}
		if (!bAlreadyExist) {
			Dish dish = Dish.findActivatedDish(dishId, siteId);
			if (dish == null) {
				throw new Error("Cannot find the dish_id : "+dishId);
			}
			StapleFood stapleFood = StapleFood.findStapleFood(staple_food_id);
			if (stapleFood == null) {
				throw new Error("Cannot find the Staple Food : "+staple_food_id);
			}
			SideDish sideDish = SideDish.findSideDish(side_dish_id);
			if (sideDish == null) {
				throw new Error("Cannot find the side dish: "+side_dish_id);
			}
			OrderDishInfo dishInfo = new OrderDishInfo(quantity, dishId, dish.lunch_price, dish.english_name, 
 					staple_food_id, stapleFood.name, sideDish.id, sideDish.english_name, "");
			dishInfo.setRestaurantName(dish.restaurant_name);
			dishInfo.setRestaurantId(dish.restaurant_id);
			dishInfo.setLocation(location);
			dishInfo.setDeliverTime(deliverTime);
			dishes.add(dishInfo);
		}
		totalNumItems += quantity;
	}
	
	public void removeItem(int dishId, int staple_food_id, int quantity) {
		for (int i=0; i<dishes.size(); i++) {
			OrderDishInfo dishInfo = dishes.get(i);
			if (dishInfo.getDishId() == dishId && dishInfo.getStapleFoodId() == staple_food_id) {
				int originQuantity = dishInfo.getQuantity();
				dishInfo.setQuantity(originQuantity - quantity);
				if (dishInfo.getQuantity() <= 0) {
					dishes.remove(i);
					totalNumItems -= originQuantity;
				}
				else {
					totalNumItems -= quantity;
				}
				break;
			}
		}		
	}
	
	public void removeItem(int dishId, int staple_food_id) {
		removeItem(dishId, staple_food_id, Integer.MAX_VALUE);
	}
	
	public Collection<OrderDishInfo> getAllDishes() {
		return dishes;
	}
	
	public int getTotalItems() {
		return totalNumItems;
	}
	
	/**
	 * 
	 * @return The key is the restaurant_id. Each order can only be for one restaurant
	 */
	public Map<Integer, List<OrderDishInfo>> getOrdersByRestaurant() {
		Map<Integer, List<OrderDishInfo>> orders = new HashMap<Integer, List<OrderDishInfo>>();
		for (OrderDishInfo dishInfo : dishes) {
			List<OrderDishInfo> orderList = orders.get(dishInfo.getRestaurantId());
			if (orderList == null) {
				orderList = new ArrayList<OrderDishInfo>();
				orders.put(dishInfo.getRestaurantId(), orderList);
			}
			orderList.add(dishInfo);
		}
		return orders;
	}
	
	public static void addToSession(ShoppingCart cart, HttpSession session) {
		session.setAttribute(SESSION_ATTRIBUTE, cart);
	}
	
	public static ShoppingCart getFromSession(HttpSession session) {
		Object obj = session.getAttribute(SESSION_ATTRIBUTE);
		if (obj == null) {
			return null;
		}
		else {
			return (ShoppingCart)obj;
		}
	}
	
	public static void clearFromSession(HttpSession session) {
		session.removeAttribute(SESSION_ATTRIBUTE);
	}
	
	
	
	
}
