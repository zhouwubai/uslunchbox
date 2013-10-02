package edu.fiu.cs.tomcatcollector;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.MBeanServerConnection;

import org.dom4j.Element;

public class JVMEventTimerTask extends TimerTask {
	
	private TomcatCollector collector = null;
	
	private Map<Long,Long> prevThreadCPUTimes = new HashMap<Long,Long>();
	
	private long lastRunTime = -1;

	public JVMEventTimerTask(TomcatCollector collector) {
		this.collector = collector;
		System.out.println("JVMEventTimerTask is started.");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			writeTomcatEvent();
			lastRunTime = System.nanoTime();
		}catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void destroy() {
		
	}
	
	private void writeTomcatEvent() throws IOException {
		Element event = collector.createEvent("JVM");
		event.addElement("jvm_total_memory").setText(Runtime.getRuntime().totalMemory()+"");
		event.addElement("jvm_max_use_memory").setText(Runtime.getRuntime().maxMemory()+"");
		event.addElement("jvm_free_memory").setText(Runtime.getRuntime().freeMemory()+"");
		event.addElement("system_load_avg").setText(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()+"");
		event.addElement("loaded_class_count").setText(ManagementFactory.getClassLoadingMXBean().getLoadedClassCount()+"");
		event.addElement("max_heap_memory").setText(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()+"");
		event.addElement("used_heap_memory").setText(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed()+"");
		event.addElement("session_count").setText(SessionLogListener.getActiveSessionCount()+"");
		event.addElement("thread_count").setText(ManagementFactory.getThreadMXBean().getThreadCount()+"");	    	
    	long[] threadIds = ManagementFactory.getThreadMXBean().getAllThreadIds();
    	Element threadsElem = event.addElement("threads");
		long now = System.nanoTime();
		long elapsedTime = now - lastRunTime;
		double overall_cpu_util = 0;
		Map<Long,Long> newThreadCPUTimes = new HashMap<Long,Long>();
    	for (long threadid : threadIds) {
    		Element threadElem = threadsElem.addElement("thread");
    		threadElem.addAttribute("id", threadid+"");
    		long totalCpuTime = ManagementFactory.getThreadMXBean().getThreadCpuTime(threadid);
    		long totalUserTime = ManagementFactory.getThreadMXBean().getThreadUserTime(threadid);
    		threadElem.addAttribute("cpu_time", totalCpuTime+"");
    		threadElem.addAttribute("user_time",totalUserTime+"");
    		newThreadCPUTimes.put(threadid, totalCpuTime);
    		if (lastRunTime < 0 ) {
    			threadElem.addAttribute("cpu_utilization", "0");
    		}
    		else {    		
	    		long elapsedCpu;
	    		if (prevThreadCPUTimes.containsKey(threadid)) {
	    			long prevTotalCpuTime = prevThreadCPUTimes.get(threadid);
	    			elapsedCpu = totalCpuTime - prevTotalCpuTime;
	    		}
	    		else {
	    			elapsedCpu = totalCpuTime;
	    		}
	    		double cpu_util = elapsedCpu / (double)elapsedTime;
				overall_cpu_util += cpu_util;
				threadElem.addAttribute("cpu_utilization", cpu_util+"");
    		}
    	}
    	this.prevThreadCPUTimes = newThreadCPUTimes;
    	event.addElement("cpu_utilization").setText(overall_cpu_util+"");
    	collector.writeEvent(event);
	}
	

}
