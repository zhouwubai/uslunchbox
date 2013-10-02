package com.uslunchbox.restaurant.user;

import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;


//@WebListener
public class UserNotificationListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("user notification listener is stopped!");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("user notification listener starts!");

		try {
			UserNotificationTask task = new UserNotificationTask();

			// specify your sceduler task details
			JobDetail job = new JobDetail();
			job.setName("userNotifiationJob");
			job.setJobClass(UserNotificationJob.class);

			Map dataMap = job.getJobDataMap();
			dataMap.put("userNotificationTask", task);

			CronTrigger trigger = new CronTrigger();
			trigger.setName("userNotificationTrigger");
			trigger.setCronExpression("0 1 13,19 ? * MON-FRI");

			// schedule it
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(job, trigger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
