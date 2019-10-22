package com.hs.dsch.launch;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;

import com.hs.dsch.annotation.DScheduledJob;
import com.hs.dsch.conf.DSchConfiguration;
import com.hs.dsch.conf.DSchContext;
import com.hs.dsch.consts.DSchClientConsts;
import com.hs.dsch.proto.DSchAdminProto.AdminResponseCode;
import com.hs.dsch.proto.DSchAdminProto.DSchAdminRegisterJobRequest;
import com.hs.dsch.proto.DSchAdminProto.DSchAdminRegisterJobResponse;
import com.hs.dsch.util.HttpClient;

@Order(2)
public class DSchedulerJob extends DSchedulerSpringFactoryImportSelector<DScheduledJob> {
	
	private static Logger logger = LoggerFactory.getLogger(DSchedulerJob.class);
	
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
		String job = attributes.getString("job");
		
		DSchAdminRegisterJobRequest.Builder request = DSchAdminRegisterJobRequest.newBuilder();
		request.setJobName(job);
		request.setNodeId(DSchContext.getInstance().getNodeId());
		
		try {
			HttpResponse httpResponse = httpClient.post(dschConfiguration.getHost() , dschConfiguration.getPort() ,
					DSchClientConsts.DSCH_SERVICE_REG_JOB_INF_NAME , request.build().toByteArray());
			DSchAdminRegisterJobResponse response = DSchAdminRegisterJobResponse.parseFrom(httpResponse.getEntity().getContent());
			if (response.getResCode() == AdminResponseCode.RESP_CODE_FAILED) {
				logger.error("注册任务失败,{}" , job);
			} else {
				DSchContext.getInstance().addJob(job , response.getJobId());
				logger.info("注册任务成功,{}/{}" , DSchContext.getInstance().getNodeId() , response.getJobId());
			}
		} catch (Exception e) {
			logger.error("注册任务失败：{}" , e);
		}
	}
}
