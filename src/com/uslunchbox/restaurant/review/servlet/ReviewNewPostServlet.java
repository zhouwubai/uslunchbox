package com.uslunchbox.restaurant.review.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.uslunchbox.restaurant.review.RestaurantReview;
import com.uslunchbox.restaurant.review.Review;
import com.uslunchbox.restaurant.user.User;

/**
 * Servlet implementation class ReviewServlet
 */
@WebServlet("/ReviewNewPostServlet")
public class ReviewNewPostServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReviewNewPostServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			
			System.out.println("ReviewNewPostServlet");
			//encode
			String content=new String(request.getParameter("content").getBytes("ISO-8859-1"),"utf-8");
			System.out.print(content);
			User user = User.getFromSession(request.getSession());		
			if (user!=null) {
				if (request.getParameter("type").equalsIgnoreCase("review_dish")){
					System.out.println("UserID: " + user.getUserId());
					Review.insertReview( Integer.parseInt(request.getParameter("rating")), content, (int) user.getUserId(), Integer.parseInt(request.getParameter("id")));
					response.sendRedirect("dishreview.html?id=" + request.getParameter("id"));
				}
				else if(request.getParameter("type").equalsIgnoreCase("review_restaurant")){
					System.out.println("UserID: " + user.getUserId());
					String restaurant_id=request.getParameter("id").toString();
					RestaurantReview.insertReview( Integer.parseInt(request.getParameter("food_rating")), 
							Integer.parseInt(request.getParameter("price_rating")),
							Integer.parseInt(request.getParameter("serv_rating")),
							Integer.parseInt(request.getParameter("atmo_rating")),
							content, 
							(int) user.getUserId() ,
							Integer.parseInt(restaurant_id)
							);
					response.sendRedirect("restaurantreview.html?restaurant="+restaurant_id);
				}else{
					response.sendRedirect("index.html");
				}
			}
		
			else
			{
				response.sendRedirect("index.html");
			}
				
		} catch (Throwable theException) {
			System.out.println("Not working");
			System.out.println(theException);
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
