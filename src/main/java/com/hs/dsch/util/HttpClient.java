package com.hs.dsch.util;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.context.annotation.Configuration;

@Configuration("httpClient")
public class HttpClient {
	private CloseableHttpClient httpClient = null;
	
	private static final Integer HTTP_CODE_SUCCESS = 200;
	private static final Integer CONNECT_TIMEOUT = 250;
	private static final Integer READ_TIMEOUT = 100;
	private static final Integer MAX_TOTAL_CONN = 100;
	private static final Integer MAX_PER_ROUTE = 25;
	private static final Integer MAX_RETRY_TIMES = 2;
	private static final Integer DEFAULT_KA_TIME = 60000;
	
	private DefaultConnectionKeepAliveStrategy keepAliveStrategy = new DefaultConnectionKeepAliveStrategy() {
		@Override
		public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
			long keepAlive = super.getKeepAliveDuration(response, context);
			if (keepAlive == -1) {
				keepAlive = DEFAULT_KA_TIME;
			}
			
			return keepAlive;
		}
	};
	
	public HttpClient() {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();  
	    cm.setMaxTotal(MAX_TOTAL_CONN);  
	    cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);
	    SocketConfig socketConfig = SocketConfig.custom()
                .setSoKeepAlive(true)
                .build();
        cm.setDefaultSocketConfig(socketConfig);
				    
		httpClient = HttpClients.custom()  
	            .setConnectionManager(cm)
	            .setRetryHandler(new DefaultHttpRequestRetryHandler(MAX_RETRY_TIMES , true)).setKeepAliveStrategy(keepAliveStrategy)  
	            .build();  
	}
	
	public HttpResponse post(String host , Integer port , String url , byte[] content) throws Exception {
		try {
			URI uri = new URI("http", null, host , port , url, "", null);
			HttpPost post = new HttpPost(uri);
	
	        post.setEntity(new ByteArrayEntity(content));
	        post.addHeader("Content-Type", "application/x-protobuf;charset=UTF-8");
	        post.addHeader("Connection", "keep-alive");
	        
	        RequestConfig rconfig = RequestConfig.custom().
	        		setSocketTimeout(READ_TIMEOUT).
	        		setConnectTimeout(CONNECT_TIMEOUT).build();
	        post.setConfig(rconfig);
	        
	        HttpResponse response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() == HTTP_CODE_SUCCESS) {				
				return response;
			} else {
				throw new IOException("HTTP请求失败，返回空句柄");
			}
		} catch (Exception e) {
			throw e;
		}
	}	
}
