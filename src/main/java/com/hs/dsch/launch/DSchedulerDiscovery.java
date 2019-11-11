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
	private DSchedulerNodeHealthChecker nodeHealthChecker = new DSchedulerNodeHealthChecker();
	
	@Override
	public String[] selectImports(AnnotationMetadata metadata) {
		AnnotationAttributes attributes = AnnotationAttributes.fromMap(
				metadata.getAnnotationAttributes(getAnnotationClass().getName(), true));
		
		initApplication(attributes);

		return new String[0];
	}

	private void initApplication(AnnotationAttributes attributes) {
		String service = attributes.getString("service");
		String desc = attributes.getString("desc");
		
		DSchRegisterNodeRequest.Builder request = DSchRegisterNodeRequest.newBuilder();
		request.setHost(addressConvertor.getLocalIPList().get(0));
		request.setServiceName(service);
		request.setDesc(desc);
		
		try {
			HttpResponse httpResponse = httpClient.post(dschConfiguration.getHost() , dschConfiguration.getPort() ,
					DSchClientConsts.DSCH_SERVICE_REG_NODE_INF_NAME , request.build().toByteArray());
			DSchRegisterNodeResponse response = DSchRegisterNodeResponse.parseFrom(httpResponse.getEntity().getContent());
			if (response.getResCode() == DSchResponseCode.RESP_CODE_FAILED) {
				logger.error("注册节点失败,{}/{}" , service , addressConvertor.getLocalIPList().get(0));
				
				System.exit(0);
			} else {
				logger.info("注册节点成功,{}/{}/{}" , service , addressConvertor.getLocalIPList().get(0) , 
						response.getNodeId());
				DSchContext.getInstance().setNodeId(response.getNodeId());
				
				nodeHealthChecker.scheduleTimer();
			}
		} catch (Exception e) {
			logger.error("注册节点失败，异常：{}" , e);
			
			System.exit(0);
		}
	}
}
