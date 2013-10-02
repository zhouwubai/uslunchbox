package com.uslunchbox.restaurant.review.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.uslunchbox.restaurant.review.Rating;


/**
 * Servlet implementation class RatingServlet
 */
@WebServlet("/ReviewRatingServlet")
public class ReviewRatingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReviewRatingServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			JSONArray jarray;
			if (request.getParameter("id") == null)
				jarray = new JSONArray();
			else {
				String id = request.getParameter("id").toString();
				if (id.equals("")) {
					jarray = new JSONArray();
				} else {
					try {
						jarray = Rating.getRatingByID(Integer.parseInt(id));
						
					} catch (NumberFormatException nFE) {
						jarray = new JSONArray();
						//response.sendRedirect("error.jsp?number=4");
					}
				}
			}
			response.getWriter().write(jarray.toString());
		} catch (Throwable theException) {
			System.out.println(theException);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("hello world!");
	}

}
