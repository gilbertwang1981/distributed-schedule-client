package com.hs.dsch.handler;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.dsch.conf.DSchConfiguration;
import com.hs.dsch.conf.DSchContext;
import com.hs.dsch.consts.DSchClientConsts;
import com.hs.dsch.proto.DSchAdminProto.AdminResponseCode;
import com.hs.dsch.proto.DSchAdminProto.DSchAdminHealthCheckRequest;
import com.hs.dsch.proto.DSchAdminProto.DSchAdminHealthCheckResponse;
import com.hs.dsch.proto.DSchAdminProto.DSchAdminJob;
import com.hs.dsch.util.HttpClient;

public class DSchJobPostHandler implements DSchJobHandler {
	private static Logger logger = LoggerFactory.getLogger(DSchJobPostHandler.class);
	
	private DSchConfiguration dschConfiguration = DSchContext.getInstance().getDSchConfiguration();
	private HttpClient httpClient = DSchContext.getInstance().getHttpClient();

	@Override
	public void handle(DSchJobContext context) {
		DSchAdminHealthCheckRequest.Builder request = DSchAdminHealthCheckRequest.newBuilder();
		request.setNodeId(DSchContext.getInstance().getNodeId());
		DSchAdminJob.Builder job = DSchAdminJob.newBuilder();
		job.setExecTime(context.getDuration());
		job.setStatus(DSchContext.getInstance().getJobStatus(context.getJobId()));
		
		job.setJobId(context.getJobId());
		request.addJobs(job);
		
		try {
			HttpResponse httpResponse = httpClient.post(dschConfiguration.getHost() , dschConfiguration.getPort() ,
					DSchClientConsts.DSCH_SERVICE_HEALTH_CHECK_INF_NAME , request.build().toByteArray());
			DSchAdminHealthCheckResponse response = DSchAdminHealthCheckResponse.parseFrom(httpResponse.getEntity().getContent());
			if (response.getResCode() == AdminResponseCode.RESP_CODE_FAILED) {
				logger.error("任务健康检查失败,{}" , job.getJobId());
			} else {
				logger.info("任务健康检查成功,{}/{}" , DSchContext.getInstance().getNodeId() , job.getJobId());
			}
		} catch (Exception e) {
			logger.error("任务健康检查失败：{}" , e);
		}
	}
}
