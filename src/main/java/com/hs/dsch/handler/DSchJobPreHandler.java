package com.hs.dsch.handler;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.dsch.conf.DSchConfiguration;
import com.hs.dsch.conf.DSchContext;
import com.hs.dsch.consts.DSchClientConsts;
import com.hs.dsch.proto.DSchAdminProto.DSchAdminCmd;
import com.hs.dsch.proto.DSchAdminProto.DSchAdminCommandRequest;
import com.hs.dsch.proto.DSchAdminProto.DSchAdminCommandResponse;
import com.hs.dsch.proto.DSchAdminProto.DSchJobStatus;
import com.hs.dsch.util.HttpClient;

public class DSchJobPreHandler implements DSchJobHandler {
	
	private DSchConfiguration dschConfiguration = DSchContext.getInstance().getDSchConfiguration();
	private HttpClient httpClient = DSchContext.getInstance().getHttpClient();
	
	private static Logger logger = LoggerFactory.getLogger(DSchJobPreHandler.class);

	@Override
	public void handle(DSchJobContext context) {
		logger.info("从服务端拉取命令:{}" , context);
		
		DSchAdminCommandRequest.Builder request = DSchAdminCommandRequest.newBuilder();
		request.setJobId(context.getJobId());
		request.setNodeId(context.getNodeId());
		
		try {
			HttpResponse httpResponse = httpClient.post(dschConfiguration.getHost() , dschConfiguration.getPort() ,
					DSchClientConsts.DSCH_SERVICE_GET_COMMAND_INF_NAME , request.build().toByteArray());
			DSchAdminCommandResponse response = DSchAdminCommandResponse.parseFrom(httpResponse.getEntity().getContent());
			if (response.getCommand().getCmdType() != DSchAdminCmd.DSCH_ADMIN_JOB_START && 
					response.getCommand().getCmdType() != DSchAdminCmd.DSCH_ADMIN_JOB_STOP) {
				logger.error("获取不到远程命令,{}" , request.getJobId());
				
				return;
			}
			
			if (DSchContext.getInstance().getJobStatus(context.getJobId()) == DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE && 
					response.getCommand().getCmdType() == DSchAdminCmd.DSCH_ADMIN_JOB_START) {
				DSchContext.getInstance().updateJobStatus(context.getJobId() , DSchJobStatus.DSCH_JOB_ST_STARTED_VALUE);
			} else if (DSchContext.getInstance().getJobStatus(context.getJobId()) != DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE && 
					response.getCommand().getCmdType() == DSchAdminCmd.DSCH_ADMIN_JOB_STOP) {
				DSchContext.getInstance().updateJobStatus(context.getJobId() , DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE);
			}
		} catch (Exception e) {
			logger.error("获取远程命令失败:{}" , e.getMessage());
			
			return;
		}
	}
}
