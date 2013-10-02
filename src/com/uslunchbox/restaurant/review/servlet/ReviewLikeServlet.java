package com.uslunchbox.restaurant.review.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.uslunchbox.restaurant.review.LikeReview;
import com.uslunchbox.restaurant.user.User;

/**
 * Servlet implementation class LikeReviewServlet
 */
@WebServlet("/ReviewLikeServlet")
public class ReviewLikeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReviewLikeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			User user = User.getFromSession(request.getSession());		
			if (user!=null) {
					String opration = request.getParameter("opration");
					Boolean isSuccess = false;
					if (opration.equals("like")) {
						isSuccess = LikeReview.newLikeReview(Integer
								.parseInt(request.getParameter("review_id")),
								(int) user.getUserId());

					} else if (opration.equals("unlike")) {
						isSuccess = LikeReview.unLikeReview(Integer
								.parseInt(request.getParameter("review_id")),
								(int) user.getUserId());
					} else {
					//	response.sendRedirect("invalid.html");
					}
					JSONObject jobject = new JSONObject();
					jobject.put("result", isSuccess);
					response.setContentType("application/json");
					response.getWriter().write(jobject.toString());
				} else {
					
				}
		} catch (Throwable theException) {
			System.out.println("Not working");
			System.out.println(theException);
		}
	}

}
