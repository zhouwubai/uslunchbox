package com.uslunchbox.restaurant.user;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.mail.MessagingException;

import com.uslunchbox.restaurant.utils.EmailSender;

public class UserNotificationTask {
	
	public void sendEmail() throws MessagingException{
		
		Calendar cal = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String deliverTime = dateFormat.format(cal.getTime());
		if (cal.get(Calendar.HOUR_OF_DAY) < 18) {
			deliverTime += " 12:30:00";
		} else {
			deliverTime += " 18:00:00";
		}
		List<String> emails = User.getUserEmailsForLastOrder(deliverTime);
		String[] recipients = emails.toArray(new String[emails.size()]);
		
		String subject = "Thank you for ordering lunchbox on USLUNCHBOX.com";
		StringBuffer message = new StringBuffer("");
		message.append("Hi Customer,<P>")
			.append("Thank you for ordering lunchbox on USLUNCHBOX.com. We hope you like the flavor and taste of the food. <br/>")
			.append("Your feedback is very important for us. Please post your feelings or suggestions and like us on our Facebook fan page - LunchBoxInUS.<P>")
			.append("Currently, the available delivery location will be at the loading zone ot the west of ECS building. Please make your order before 11:30 AM for lunch and pick it up at 12:30 PM, ")
			.append("or before 5:00 PM for dinner and pick it up at 18:00 PM.<P>")
			.append("We are looking forward to your support in the future.<P>")
			.append("Sincerely,<br/>USLUNCHBOX Team");

		String from = "uslunchbox@gmail.com";

		EmailSender ea = new EmailSender();
		ea.sendSSLMessage(null, new String[]{"uslunchbox@gmail.com"}, recipients, subject, message.toString(), from);
	}

}
