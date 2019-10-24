package com.hs.dsch.conf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.hs.dsch.util.AddressConvertor;
import com.hs.dsch.util.HttpClient;

public class DSchContext {
	private String nodeId;
	
	private ApplicationContext appCtx = new AnnotationConfigApplicationContext(AddressConvertor.class , DSchConfiguration.class , HttpClient.class);	
	private AddressConvertor addressConvertor = (AddressConvertor) appCtx.getBean("addressConvertor");
	private DSchConfiguration dschConfiguration = (DSchConfiguration) appCtx.getBean("dschConfiguration");
	private HttpClient httpClient = (HttpClient)appCtx.getBean("httpClient");
	
	private Map<String , String> jobs = new ConcurrentHashMap<>();
	
	private Map<String , Integer> jobStatus = new ConcurrentHashMap<>();
	
	private static DSchContext instance = null;
	
	public void addJob(String jobName , String jobId) {
		jobs.put(jobName,  jobId);
	}
	
	public void updateJobStatus(String jobId , Integer status) {
		jobStatus.put(jobId , status);
	}
	
	public Integer getJobStatus(String jobId) {
		return jobStatus.get(jobId) == null? 0 : jobStatus.get(jobId);
	}
	
	public String getJob(String jobName) {
		return jobs.get(jobName);
	}
	
	public AddressConvertor getAddressConvertor() {
		return addressConvertor;
	}
	
	public DSchConfiguration getDSchConfiguration() {
		return dschConfiguration;
	}
	
	public HttpClient getHttpClient() {
		return httpClient;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	
	public static DSchContext getInstance() {
		if (instance == null) {
			synchronized (DSchContext.class) {
				if (instance == null) {
					instance = new DSchContext();
				}
			}
		}
		
		return instance;
	}
}
