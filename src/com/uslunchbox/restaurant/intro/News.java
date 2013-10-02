package com.uslunchbox.restaurant.intro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.uslunchbox.restaurant.database.ConnectionManager;

public class News {
	
	private int newsId;
	private int restaurantId;
	private String newsContent;
	private String newsDate;
	private String newsType;
	
	public News(){
	}
	
	public static List<News> getNewsForRestaurant(int restaurantId){
		List<News> news = new ArrayList<News>();
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rets = null;
		try {
			pstmt = conn.prepareStatement("select n.news_id, n.news_content, n.news_date, n.news_type " +
					"from restaurants_news n where n.restaurant_id = ? order by n.news_date desc");
			pstmt.setInt(1, restaurantId);
			rets = pstmt.executeQuery();
			int count = 0;
			while (rets.next() && count < 3) {
				News n = new News();
				n.setNewsId(rets.getInt(1));
				n.setRestaurantId(restaurantId);
				n.setNewsContent(rets.getString(2));
				n.setNewsDate(rets.getTimestamp(3).toString());
				n.setNewsType(rets.getString(4));
				news.add(n);
				count++;
			}
			rets.close();
			pstmt.close();
			conn.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				rets.close();
				pstmt.close();
				conn.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return news;
	}

	public int getNewsId() {
		return newsId;
	}

	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}

	public int getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(int restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getNewsContent() {
		return newsContent;
	}

	public void setNewsContent(String newsContent) {
		this.newsContent = newsContent;
	}

	public String getNewsDate() {
		return newsDate;
	}

	public void setNewsDate(String newsDate) {
		this.newsDate = newsDate;
	}

	public String getNewsType() {
		return newsType;
	}

	public void setNewsType(String newsType) {
		this.newsType = newsType;
	}

}
