package com.uslunchbox.restaurant.utils;

public class TimeModifier {

	public static String modify(String dateStr) {
		
		String dateStrM = "";
		String[] subStrs = dateStr.split(" ");

		if (subStrs[1].equals("12:00AM")) {
			dateStr = subStrs[0] + " " + "00:00AM";
		} else if (!subStrs[1].startsWith("12") && subStrs[1].endsWith("PM")) {
			String[] subSubStrs = subStrs[1].split(":");
			int realHour = Integer.parseInt(subSubStrs[0]) + 12;
			dateStr = subStrs[0] + " " + realHour + ":" + subSubStrs[1];
		}
		
		if (dateStr.length() == 17) {
			String[] newSubStrs=dateStr.split(" ");
			dateStrM=newSubStrs[0]+" 0"+newSubStrs[1];
		}else{
			dateStrM=dateStr;
		}

		return dateStrM;
	}

	public static void main(String[] args) {
		String teString = "08/12/2011 12:00PM";
		System.out.println(teString.length());
		System.out.println(TimeModifier.modify(teString));
	}

}
