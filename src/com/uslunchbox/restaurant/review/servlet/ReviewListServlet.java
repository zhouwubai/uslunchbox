package com.uslunchbox.restaurant.review.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;

import com.uslunchbox.restaurant.review.Review;


/**
 * Servlet implementation class ReviewListServlet
 */
@WebServlet("/ReviewListServlet")
public class ReviewListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReviewListServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			JSONArray jarray;
			if (request.getParameter("id") == null)
				jarray = new JSONArray();
			else {
				String id = request.getParameter("id").toString();
				if (id.equals("")) {
					jarray = new JSONArray();
//				} else if (id.equals("all")){
//					jarray = Review.getAllReviews();
				} else {
					try {
						jarray = Review.getAllReviewsForDish(Integer.parseInt(id));
					} catch (NumberFormatException nFE) {
						jarray = new JSONArray();
					}
				}
			}
			response.setContentType("text/json;charset=UTF-8");
			response.getWriter().write(jarray.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
