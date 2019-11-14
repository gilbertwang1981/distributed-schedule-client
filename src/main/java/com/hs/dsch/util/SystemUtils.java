package com.hs.dsch.util;

public class SystemUtils {
	public static int getThreadCount() {
		ThreadGroup parentThread = null;
		for (parentThread = Thread.currentThread().getThreadGroup(); parentThread.getParent() != null; parentThread = parentThread.getParent());
		
		return parentThread.activeCount();
	}
	
	public static long getMemUtil() {
		long free = Runtime.getRuntime().freeMemory();
        long total = Runtime.getRuntime().totalMemory();
        long used = total - free;
        
        return (long) ((used / (float)total) * 100);
	}
	
	public static long getCpuUtil() {
		return 10L;
	}
}
