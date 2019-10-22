package com.hs.dsch.launch;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import com.hs.dsch.annotation.EnableDScheduling;
import com.hs.dsch.conf.DSchConfiguration;
import com.hs.dsch.consts.DSchClientConsts;
import com.hs.dsch.proto.DSchAdminProto.AdminResponseCode;
import com.hs.dsch.proto.DSchAdminProto.DSchAdminRegisterNodeRequest;
import com.hs.dsch.proto.DSchAdminProto.DSchAdminRegisterNodeResponse;
import com.hs.dsch.util.AddressConvertor;
import com.hs.dsch.util.HttpClient;

public class DSchedulerDiscovery extends DSchedulerSpringFactoryImportSelector<EnableDScheduling> {
	private static Logger logger = LoggerFactory.getLogger(DSchedulerDiscovery.class);
	
	private HttpClient httpClient = new HttpClient();
	
	private ApplicationContext appCtx = new AnnotationConfigApplicationContext(AddressConvertor.class , DSchConfiguration.class);	
	private AddressConvertor addressConvertor = (AddressConvertor) appCtx.getBean("addressConvertor");
	private DSchConfiguration dschConfiguration = (DSchConfiguration) appCtx.getBean("dschConfiguration");
	
	@Override
	public String[] selectImports(AnnotationMetadata metadata) {
		AnnotationAttributes attributes = AnnotationAttributes.fromMap(
				metadata.getAnnotationAttributes(getAnnotationClass().getName(), true));
		
		initApplication(attributes);

		return new String[0];
	}

	private void initApplication(AnnotationAttributes attributes) {
		String namespace = attributes.getString("namespace");
		String service = attributes.getString("service");
		
		DSchAdminRegisterNodeRequest.Builder request = DSchAdminRegisterNodeRequest.newBuilder();
		request.setHost(addressConvertor.getLocalIPList().get(0));
		request.setNamespace(namespace);
		request.setServiceName(service);
		
		try {
			HttpResponse httpResponse = httpClient.post(dschConfiguration.getHost() , dschConfiguration.getPort() ,
					DSchClientConsts.DSCH_SERVICE_REG_NODE_INF_NAME , request.build().toByteArray());
			DSchAdminRegisterNodeResponse response = DSchAdminRegisterNodeResponse.parseFrom(httpResponse.getEntity().getContent());
			if (response.getResCode() == AdminResponseCode.RESP_CODE_FAILED) {
				logger.error("注册节点失败,{}/{}/{}" , namespace , service , addressConvertor.getLocalIPList().get(0));
			} else {
				logger.info("注册节点成功,{}/{}/{}" , namespace , service , addressConvertor.getLocalIPList().get(0));
			}
		} catch (Exception e) {
			logger.error("注册节点失败，异常：{}" , e);
		}
	}
}
