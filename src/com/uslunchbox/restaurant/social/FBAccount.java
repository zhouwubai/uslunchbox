package com.uslunchbox.restaurant.social;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.*;
import com.uslunchbox.restaurant.database.ConnectionManager;

public class FBAccount {

	public FBAccount(User profile) {
		this.profile = profile;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private User profile;
	
	public User getProfile() {
		return profile;
	}

	public static FBAccount getFromAccessToken(String accesstoken) {
		FacebookClient fbClient = new DefaultFacebookClient(accesstoken);
		User profile = fbClient.fetchObject("me", User.class);
		FBAccount account = new FBAccount(profile);
		return account;
	}
	
	public void updateAccessExp(String accesstoken, Calendar expiration) {
		Connection conn = ConnectionManager.getInstance().getConnection();
		try {
			PreparedStatement pstmt = conn.prepareStatement("UPDATE users_fb_profile SET access_token=?, " +
					"expiration=? WHERE fb_id=?");
			pstmt.setString(1, accesstoken);
			pstmt.setTimestamp(2, new java.sql.Timestamp(expiration.getTimeInMillis()));
			pstmt.setString(3, profile.getId());
			pstmt.executeUpdate();
		} catch (Exception e) {
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
