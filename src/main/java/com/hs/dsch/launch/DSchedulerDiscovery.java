package com.hs.dsch.launch;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import com.hs.dsch.annotation.EnableDScheduling;
import com.hs.dsch.conf.DSchConfiguration;
import com.hs.dsch.conf.DSchContext;
import com.hs.dsch.consts.DSchClientConsts;
import com.hs.dsch.proto.DSchAdminProto.DSchRegisterNodeRequest;
import com.hs.dsch.proto.DSchAdminProto.DSchRegisterNodeResponse;
import com.hs.dsch.proto.DSchAdminProto.DSchResponseCode;
import com.hs.dsch.util.AddressConvertor;
import com.hs.dsch.util.HttpClient;

@Order(1)
public class DSchedulerDiscovery extends DSchedulerSpringFactoryImportSelector<EnableDScheduling> {
	private static Logger logger = LoggerFactory.getLogger(DSchedulerDiscovery.class);
		
	private AddressConvertor addressConvertor = DSchContext.getInstance().getAddressConvertor();
	private DSchConfiguration dschConfiguration = DSchContext.getInstance().getDSchConfiguration();
	private HttpClient httpClient = DSchContext.getInstance().getHttpClient();
	
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
		
		DSchRegisterNodeRequest.Builder request = DSchRegisterNodeRequest.newBuilder();
		request.setHost(addressConvertor.getLocalIPList().get(0));
		request.setNamespace(namespace);
		request.setServiceName(service);
		
		try {
			HttpResponse httpResponse = httpClient.post(dschConfiguration.getHost() , dschConfiguration.getPort() ,
					DSchClientConsts.DSCH_SERVICE_REG_NODE_INF_NAME , request.build().toByteArray());
			DSchRegisterNodeResponse response = DSchRegisterNodeResponse.parseFrom(httpResponse.getEntity().getContent());
			if (response.getResCode() == DSchResponseCode.RESP_CODE_FAILED) {
				logger.error("注册节点失败,{}/{}/{}" , namespace , service , addressConvertor.getLocalIPList().get(0));
			} else {
				logger.info("注册节点成功,{}/{}/{}/{}" , namespace , service , addressConvertor.getLocalIPList().get(0) , 
						response.getNodeId());
				DSchContext.getInstance().setNodeId(response.getNodeId());
			}
		} catch (Exception e) {
			logger.error("注册节点失败，异常：{}" , e);
		}
	}
}
