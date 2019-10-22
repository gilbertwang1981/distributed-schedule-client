package com.hs.dsch.conf;

import org.springframework.context.annotation.Configuration;

import com.hs.dsch.consts.DSchClientConsts;

@Configuration("dschConfiguration")
public class DSchConfiguration {	
	public String getHost() {
		String host = System.getenv("DSCH_SERVICE_HOST");
		if (host == null) {
			return DSchClientConsts.DSCH_SERVICE_DEFAULT_HOST;
		} 
		return host;
	}

	public Integer getPort() {
		String port = System.getenv("DSCH_SERVICE_PORT");
		if (port == null) {
			return DSchClientConsts.DSCH_SERVICE_DEFUALT_PORT;
		} 
		return Integer.parseInt(port);
	}
}
