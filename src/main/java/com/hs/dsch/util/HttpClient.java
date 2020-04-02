package com.hs.dsch.util;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.context.annotation.Configuration;

@Configuration("httpClient")
public class HttpClient {
	private CloseableHttpClient httpClient = HttpClientBuilder.create().build();
	
	private static final Integer HTTP_CODE_SUCCESS = 200;
	private static final Integer CONNECT_TIMEOUT = 500;
	private static final Integer READ_TIMEOUT = 500;
	
	public HttpResponse post(String host , Integer port , String url , byte[] content) throws Exception {
		HttpResponse response = null;
		try {
			URI uri = new URI("http", null, host , port , url, "", null);
			HttpPost post = new HttpPost(uri);
	
	        post.setEntity(new ByteArrayEntity(content));
	        post.addHeader("Content-Type", "application/x-protobuf;charset=UTF-8");
	        
	        RequestConfig rconfig = RequestConfig.custom().
	        		setSocketTimeout(READ_TIMEOUT).
	        		setConnectTimeout(CONNECT_TIMEOUT).build();
	        post.setConfig(rconfig);
	        
	        response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() == HTTP_CODE_SUCCESS) {				
				return response;
			} else {
				throw new IOException("HTTP请求失败，返回空句柄");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (response != null) {
				EntityUtils.toString(response.getEntity() , "UTF-8");
			}
		}
	}
}
