package com.uslunchbox.restaurant.owner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import com.uslunchbox.restaurant.database.ConnectionManager;

/**
 * 
 * @author lli003
 * 
 */
public class Owner {

	private long ownerId;

	private String nickName;

	private final static String CURRENT_OWNER_ATTRIBUTE = "currentOwner";

	private Owner(long ownerId, String nickName) {
		this.ownerId = ownerId;
		this.nickName = nickName;
	}

	public static boolean validate(String nickName, String password,
			HttpSession session) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet retOwner = null;
		try {
			pstmt = conn.prepareStatement("SELECT m.owner_id"
					+ " FROM restaurant_owners m"
					+ " WHERE m.nick_name = ? and m.password = ?");
			pstmt.setString(1, nickName);
			pstmt.setString(2, password);
			retOwner = pstmt.executeQuery();
			if (retOwner.next()) {
				Owner owner = new Owner(retOwner.getLong("owner_id"), nickName);
				session.setAttribute(CURRENT_OWNER_ATTRIBUTE, owner);
				pstmt.close();
				retOwner.close();
				conn.close();
				return true;
			} else {
				pstmt.close();
				retOwner.close();
				conn.close();
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				pstmt.close();
				retOwner.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static Owner getFromSession(HttpSession session) {
		if (session.getAttribute(CURRENT_OWNER_ATTRIBUTE) == null) {
			return null;
		} else {
			return (Owner) session.getAttribute(CURRENT_OWNER_ATTRIBUTE);
		}
	}
	
	public static void removeFromSession(HttpSession session) {
		if (getFromSession(session) != null) {
			session.removeAttribute(CURRENT_OWNER_ATTRIBUTE);
		}
	}

	public long getOwnerId() {
		return ownerId;
	}

	public String getNickName() {
		return nickName;
	}

}
