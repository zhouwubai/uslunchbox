package com.uslunchbox.restaurant.order;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.uslunchbox.restaurant.database.ConnectionManager;
import com.uslunchbox.restaurant.site.Site;

/**
 * Servlet implementation class AnnouncementServlet
 */
@WebServlet("/AnnouncementServlet")
public class AnnouncementServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AnnouncementServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		
		Site site = Site.getSiteFromSession(request.getSession());
		response.getWriter().write(getAnnouncement(site.getSiteId()));		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
	}
	
	private String getAnnouncement(int siteId) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		String content = "";
		try {			
			PreparedStatement pstmt = conn.prepareStatement("SELECT content from announcement where site_id = ?");
			pstmt.setInt(1,siteId);
			ResultSet rets = pstmt.executeQuery();
			while(rets.next()) {
				content = rets.getString(1);
				break;
			}
			rets.close();
			pstmt.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return content;
	}

}
