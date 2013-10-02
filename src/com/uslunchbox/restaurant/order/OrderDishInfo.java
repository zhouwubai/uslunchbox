package com.uslunchbox.restaurant.order;

public class OrderDishInfo {

	private int quantity;
	private int dishId;
	private int staple_food_id;
	private int side_dish_id;
	private int restaurantId;
	private String restaurantName;
	private String dishName;
	private String stapleFoodName;
	private String sideDishName;
	private double price;
	private String comment;
	private String location;
	private String deliverTime;
	
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDeliverTime() {
		return deliverTime;
	}

	public void setDeliverTime(String deliverTime) {
		this.deliverTime = deliverTime;
	}

	
	
	public OrderDishInfo(){}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDishName() {
		return dishName;
	}

	public void setDishName(String dishName) {
		this.dishName = dishName;
	}

	public OrderDishInfo(int quantity,int dishId, double price,String dishName, int staple_food_id, 
			String stapleFoodName, int side_dish_id, String sideDishName, String comment){
		this.quantity=quantity;
		this.dishId=dishId;
		this.price=price;
		this.dishName=dishName;
		this.stapleFoodName = stapleFoodName;
		this.comment=comment;
		this.staple_food_id = staple_food_id;
		this.side_dish_id = side_dish_id;
		this.sideDishName = sideDishName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getDishId() {
		return dishId;
	}

	public void setDishId(int dishId) {
		this.dishId = dishId;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(int restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public int getStapleFoodId() {
		return staple_food_id;
	}

	public void setStapleFoodId(int staple_food_id) {
		this.staple_food_id = staple_food_id;
	}

	public String getStapleFoodName() {
		return stapleFoodName;
	}

	public void setStapleFoodName(String stapleFoodName) {
		this.stapleFoodName = stapleFoodName;
	}

	public int getSideDishId() {
		return side_dish_id;
	}

	public void setSideDishId(int side_dish_id) {
		this.side_dish_id = side_dish_id;
	}

	public String getSideDishName() {
		return sideDishName;
	}

	public void setSideDishName(String sideDishName) {
		this.sideDishName = sideDishName;
	}

}
