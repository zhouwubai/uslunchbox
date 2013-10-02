package com.uslunchbox.restaurant.user;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.uslunchbox.restaurant.database.ConnectionManager;

/**
 * Servlet implementation class UpdateUserProfileServlet
 */
@WebServlet("/UpdateUserProfileServlet")
public class UpdateUserProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateUserProfileServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		User user = User.getFromSession(request.getSession());
		
		if(user == null){
			JSONObject userInfo = new JSONObject();
			userInfo.put("result", "notsignin");
			response.getWriter().write(userInfo.toJSONString());
			return;
		}
		
		long user_id = user.getUserId(); //= 1;
		
		String pWord1 = request.getParameter("newPassword"),
				pWord2 = request.getParameter("confirmPassword");
		
		//System.out.println("fdaffw"+ pWord1);
		String updateProfile = "update users set password = ? where user_id=?";
		Connection conn = null;
		JSONObject retJson = new JSONObject();
		try {
			conn = ConnectionManager.getInstance().getConnection();
		    PreparedStatement stm = conn.prepareStatement(updateProfile);
		    stm.setString(1, pWord1);
		    stm.setLong(2, user_id);
		    int res = stm.executeUpdate();
		    if(res==1){
		    	retJson.put("result", "true");
		    }else if(res==0){
		    	retJson.put("result", "false");
		    }
		    response.getWriter().write(retJson.toString());
		    
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}

}
