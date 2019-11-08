package com.hs.dsch.handler;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.dsch.conf.DSchConfiguration;
import com.hs.dsch.conf.DSchContext;
import com.hs.dsch.consts.DSchClientConsts;
import com.hs.dsch.proto.DSchAdminProto.DSchJob;
import com.hs.dsch.proto.DSchAdminProto.DSchJobHealthCheckRequest;
import com.hs.dsch.proto.DSchAdminProto.DSchJobHealthCheckResponse;
import com.hs.dsch.proto.DSchAdminProto.DSchResponseCode;
import com.hs.dsch.util.HttpClient;

public class DSchJobHealthCheckHandler implements DSchJobHandler {
	private static Logger logger = LoggerFactory.getLogger(DSchJobHealthCheckHandler.class);
	
	private DSchConfiguration dschConfiguration = DSchContext.getInstance().getDSchConfiguration();
	private HttpClient httpClient = DSchContext.getInstance().getHttpClient();

	@Override
	public void handle(DSchJobContext context) {
		DSchJobHealthCheckRequest.Builder request = DSchJobHealthCheckRequest.newBuilder();
		request.setNodeId(DSchContext.getInstance().getNodeId());
		DSchJob.Builder job = DSchJob.newBuilder();
		job.setBeginTime(context.getBeginTime());
		job.setEndTime(context.getEndTime());
		
		job.setJobId(context.getJobId());
		request.setJob(job);
		
		try {
			HttpResponse httpResponse = httpClient.post(dschConfiguration.getHost() , dschConfiguration.getPort() ,
					DSchClientConsts.DSCH_SERVICE_HEALTH_CHECK_INF_NAME , request.build().toByteArray());
			DSchJobHealthCheckResponse response = DSchJobHealthCheckResponse.parseFrom(httpResponse.getEntity().getContent());
			if (response.getResCode() == DSchResponseCode.RESP_CODE_FAILED) {
				logger.error("任务健康检查失败,{}" , job.getJobId());
			} else {
				logger.info("任务健康检查成功,node:{} job:{}" , DSchContext.getInstance().getNodeId() , job.getJobId());
			}
		} catch (Exception e) {
			logger.error("任务健康检查失败：{}" , e.getMessage());
		}
	}
}
