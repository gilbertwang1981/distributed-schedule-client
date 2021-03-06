package com.hs.dsch.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemUtils {
	private static Logger logger = LoggerFactory.getLogger(SystemUtils.class);
	
	public static final int jvmPid() {  
        try {  
            RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();  
            Field jvm = runtime.getClass().getDeclaredField("jvm");  
            jvm.setAccessible(true);   
            Method pidMethod = jvm.get(runtime).getClass().getDeclaredMethod("getProcessId");  
            pidMethod.setAccessible(true);  
            int pid = (Integer) pidMethod.invoke(jvm.get(runtime));  
            return pid;  
        } catch (Exception e) {  
            return -1;  
        }  
    }
	
	public static long getThreadCount() {
		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		String[] command = {"/bin/sh" , "-c" , "top -b -n 1 -H -p " + jvmPid() + " | grep Threads | awk -F, \'{print $1}\' | awk -F: \'{print $2}\' | awk \'{print $1}\'"};  
		try {
			process = runtime.exec(command);
			process.waitFor();
			
			is = process.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			
			String line;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			
			logger.info("线程数:{}/{}/{}" , sb.toString() , jvmPid() , command);
			
			return Double.valueOf(sb.toString()).longValue();
		} catch (Exception e) {
			logger.error("获取线程数发生异常, {}" , e.getMessage());
			
			return 0L;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				
				if (isr != null) {
					isr.close();
				}
				
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				logger.error("关闭流发生异常, {}" , e.getMessage());
			}
		}
	}
	
	public static long getMemUtil() {
		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		String[] command = {"/bin/sh" , "-c" , "top -b -n 1 -p " + jvmPid() + " | tail -n 1 | awk \'{print $10}\'"};
		try {
			process = runtime.exec(command);
			process.waitFor();
			
			is = process.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
 
			String line;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			
			logger.info("内存使用率:{}/{}/{}" , sb.toString() , jvmPid() , command);
			
			return Double.valueOf(sb.toString()).longValue();
		} catch (Exception e) {
			logger.error("获取内存利用率发生异常, {}/{}" , e.getMessage() , command);
			
			return 0L;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				
				if (isr != null) {
					isr.close();
				}
				
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				logger.error("关闭流发生异常, {}" , e.getMessage());
			}
		}
	}
	
	public static long getCpuUtil() {
		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		String[] command = {"/bin/sh" , "-c" , "top -b -n 1 -p " + jvmPid() + " | tail -n 1 | awk \'{print $9}\'"};
		try {
			process = runtime.exec(command);
			process.waitFor();
			
			is = process.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
 
			String line;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {			
				sb.append(line);
			}
			
			logger.info("CPU使用率:{}/{}/{}" , sb.toString() , jvmPid() , command);
			
			return Double.valueOf(sb.toString()).longValue();
		} catch (Exception e) {
			logger.error("获取CPU利用率发生异常, {}/{}" , command , e.getMessage());
			
			return 0L;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				
				if (isr != null) {
					isr.close();
				}
				
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				logger.error("关闭流发生异常, {}" , e.getMessage());
			}
		}
	}
}
