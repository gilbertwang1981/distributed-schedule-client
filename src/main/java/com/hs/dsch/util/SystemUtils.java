package com.hs.dsch.util;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemUtils {
	private static Logger logger = LoggerFactory.getLogger(SystemUtils.class);
	
	private static Sigar sigar = new Sigar();
	
	public static int getThreadCount() {
		ThreadGroup parentThread = null;
		for (parentThread = Thread.currentThread().getThreadGroup(); parentThread.getParent() != null; parentThread = parentThread.getParent());
		
		return parentThread.activeCount();
	}
	
	public static double getMemUtil() {
		try {
			return sigar.getMem().getUsedPercent();
		} catch (Exception e) {
			logger.error("获取系统内存信息失败，{}" , e);
			
			return 0D;
		}
	}
	
	public static double getCpuUtil() {
		try {
			CpuPerc cpuList[] = sigar.getCpuPercList();
			double cpuUtil = 0;
			for (int i = 0 ; i < cpuList.length ; i++) {
				cpuUtil += cpuList[i].getCombined();
			}
			
			return cpuUtil / cpuList.length;
		} catch (Exception e) {
			logger.error("获取系统CPU信息失败，{}" , e);
			
			return 0D;
		}
	}
}
