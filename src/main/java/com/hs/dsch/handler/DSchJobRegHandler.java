package com.hs.dsch.handler;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.dsch.conf.DSchConfiguration;
import com.hs.dsch.conf.DSchContext;
import com.hs.dsch.consts.DSchClientConsts;
import com.hs.dsch.proto.DSchAdminProto.DSchJobExecStrategy;
import com.hs.dsch.proto.DSchAdminProto.DSchRegisterJobRequest;
import com.hs.dsch.proto.DSchAdminProto.DSchRegisterJobResponse;
import com.hs.dsch.proto.DSchAdminProto.DSchResponseCode;
import com.hs.dsch.util.HttpClient;

public class DSchJobRegHandler implements DSchJobHandler {
	private static Logger logger = LoggerFactory.getLogger(DSchJobRegHandler.class);
			
	private DSchConfiguration dschConfiguration = DSchContext.getInstance().getDSchConfiguration();
	private HttpClient httpClient = DSchContext.getInstance().getHttpClient();
	
	@Override
	public void handle(DSchJobContext context) {
		logger.info("任务注册：{} {}" , context.getJobId() , context.getJobName());
		
		DSchRegisterJobRequest.Builder request = DSchRegisterJobRequest.newBuilder();
		request.setJobName(context.getJobName());
		request.setNodeId(context.getNodeId());
		
		DSchJobExecStrategy.Builder strategy = DSchJobExecStrategy.newBuilder();
		strategy.setCron(context.getCron());
		strategy.setFixedDelay(context.getFixDelay());
		strategy.setFixedRate(context.getFixRate());
		strategy.setInitialDelay(context.getInitialDelay());
		
		request.setStrategy(strategy);
		
		try {
			HttpResponse httpResponse = httpClient.post(dschConfiguration.getHost() , dschConfiguration.getPort() ,
					DSchClientConsts.DSCH_SERVICE_REG_JOB_INF_NAME , request.build().toByteArray());
			DSchRegisterJobResponse response = DSchRegisterJobResponse.parseFrom(httpResponse.getEntity().getContent());
			if (response.getResCode() == DSchResponseCode.RESP_CODE_SUCCESS) {
				logger.info("任务注册成功，job：{}" , response.getJobId());
				
				DSchContext.getInstance().addJob(context.getJobName() , response.getJobId() , context.getDesc());
			} else {
				logger.info("任务注册失败，job-name:{}" , context.getJobName());
			}
		} catch (Exception e) {
			logger.error("注册任务异常发生：{}" , e);
		}
	}
}
