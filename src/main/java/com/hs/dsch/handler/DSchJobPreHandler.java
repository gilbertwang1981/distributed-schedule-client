package com.hs.dsch.handler;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.dsch.conf.DSchConfiguration;
import com.hs.dsch.conf.DSchContext;
import com.hs.dsch.consts.DSchClientConsts;
import com.hs.dsch.proto.DSchAdminProto.DSchCmd;
import com.hs.dsch.proto.DSchAdminProto.DSchCommandRequest;
import com.hs.dsch.proto.DSchAdminProto.DSchCommandResponse;
import com.hs.dsch.proto.DSchAdminProto.DSchJobStatus;
import com.hs.dsch.util.HttpClient;

public class DSchJobPreHandler implements DSchJobHandler {
	
	private DSchConfiguration dschConfiguration = DSchContext.getInstance().getDSchConfiguration();
	private HttpClient httpClient = DSchContext.getInstance().getHttpClient();
	
	private static Logger logger = LoggerFactory.getLogger(DSchJobPreHandler.class);

	@Override
	public void handle(DSchJobContext context) {
		logger.info("从服务端拉取命令:job:{} node:{}" , context.getJobId() , context.getNodeId());
		
		DSchCommandRequest.Builder request = DSchCommandRequest.newBuilder();
		request.setJobId(context.getJobId());
		request.setNodeId(context.getNodeId());
		
		try {
			HttpResponse httpResponse = httpClient.post(dschConfiguration.getHost() , dschConfiguration.getPort() ,
					DSchClientConsts.DSCH_SERVICE_GET_COMMAND_INF_NAME , request.build().toByteArray());
			DSchCommandResponse response = DSchCommandResponse.parseFrom(httpResponse.getEntity().getContent());
			if (response.getCommand().getCmdType() != DSchCmd.DSCH_JOB_RESUME && 
					response.getCommand().getCmdType() != DSchCmd.DSCH_JOB_PAUSE) {
				logger.error("获取不到远程命令,job:{} node:{}" , request.getJobId() , request.getNodeId());
						
				return;
			}
			
			if (DSchContext.getInstance().getJobStatus(context.getJobId()) == DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE && 
					response.getCommand().getCmdType() == DSchCmd.DSCH_JOB_RESUME) {
				DSchContext.getInstance().updateJobStatus(context.getJobId() , DSchJobStatus.DSCH_JOB_ST_STARTED_VALUE);
			} else if (DSchContext.getInstance().getJobStatus(context.getJobId()) != DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE && 
					response.getCommand().getCmdType() == DSchCmd.DSCH_JOB_PAUSE) {
				DSchContext.getInstance().updateJobStatus(context.getJobId() , DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE);
			}
		} catch (Exception e) {
			logger.error("获取远程命令失败:{}" , e.getMessage());
			
			return;
		}
	}
}
