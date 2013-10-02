package com.uslunchbox.restaurant.user;

import java.util.Map;

import javax.mail.MessagingException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class UserNotificationJob implements Job{
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub

		Map dataMap = context.getJobDetail().getJobDataMap();
		UserNotificationTask task = (UserNotificationTask) dataMap.get("userNotificationTask");
		try {
			task.sendEmail();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
