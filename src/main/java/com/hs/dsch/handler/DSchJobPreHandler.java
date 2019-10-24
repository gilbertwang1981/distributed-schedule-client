package com.hs.dsch.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.dsch.conf.DSchConfiguration;
import com.hs.dsch.conf.DSchContext;
import com.hs.dsch.proto.DSchAdminProto.DSchJobStatus;
import com.hs.dsch.util.HttpClient;

public class DSchJobPreHandler implements DSchJobHandler {
	
	private DSchConfiguration dschConfiguration = DSchContext.getInstance().getDSchConfiguration();
	private HttpClient httpClient = DSchContext.getInstance().getHttpClient();
	
	private static Logger logger = LoggerFactory.getLogger(DSchJobPreHandler.class);

	@Override
	public void handle(DSchJobContext context) {
		logger.info("从服务端拉取命令:{}" , context);
		
		if (DSchContext.getInstance().getJobStatus(context.getJobId()) == DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE) {
			// 当前任务状态为停止状态并且获取的命令为启动
			DSchContext.getInstance().updateJobStatus(context.getJobId() , DSchJobStatus.DSCH_JOB_ST_STARTED_VALUE);
		} else if (DSchContext.getInstance().getJobStatus(context.getJobId()) != DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE) {
			// 当前任务为运行状态并且获取命令为停止
			DSchContext.getInstance().updateJobStatus(context.getJobId() , DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE);
		}
	}
}
