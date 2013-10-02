package com.uslunchbox.restaurant.review.servlet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.uslunchbox.restaurant.review.Comment;
import com.uslunchbox.restaurant.user.User;

/**
 * Servlet implementation class ReviewServlet
 */
@WebServlet("/ReviewCommentForReviewServlet")
public class ReviewCommentForReviewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReviewCommentForReviewServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			System.out.println("CommentForreviewServlet");
			System.out.println(request.getParameter("content"));
			System.out.println(request.getParameter("id"));
			String content=new String(request.getParameter("content").getBytes("ISO-8859-1"),"utf-8");
			User user = User.getFromSession(request.getSession());		
			if (user!=null) {
			
					//System.out.println(" OVER HERE UserID: " + user.getId());

					Comment.insertComment((int) user.getUserId(),
							Integer.parseInt(request.getParameter("id")),
							content);

					response.sendRedirect("reviewdish.html?id="
							+ request.getParameter("id"));
				} else {
					response.sendRedirect("error.jsp?number=1");
					return;
				}

		} catch (Throwable theException) {
			System.out.println(theException);
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			User user = User.getFromSession(request.getSession());		
			if (user!=null) {
					int id = Comment.insertComment((int) user.getUserId(),
							Integer.parseInt(request.getParameter("id")),
							request.getParameter("content"));
					Date date = new Date();
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					JSONObject jobject = new JSONObject();
					jobject.put("id", id);
					jobject.put("time", df.format(date));
					response.setContentType("application/json");
					response.getWriter().write(jobject.toString());
				} else {
					System.out.println("Not working4");
					response.sendRedirect("error.jsp?number=1");
					return;
				}
		} catch (Throwable theException) {
			System.out.println("Not working2");
			System.out.println(theException);
			response.sendRedirect("error.jsp?number=4");
			return;
		}
	}

}
