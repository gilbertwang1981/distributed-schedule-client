package com.hs.dsch.conf;

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
	
	private static DSchContext instance = null;
	
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
