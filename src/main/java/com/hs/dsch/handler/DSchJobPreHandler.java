package com.hs.dsch.handler;

import com.hs.dsch.conf.DSchContext;
import com.hs.dsch.proto.DSchAdminProto.DSchJobStatus;

public class DSchJobPreHandler implements DSchJobHandler {

	@Override
	public void handle(DSchJobContext context) {
		
		if (DSchContext.getInstance().getJobStatus(context.getJobId()) == DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE) {
			// 当前任务状态为停止状态并且获取的命令为启动
			DSchContext.getInstance().updateJobStatus(context.getJobId() , DSchJobStatus.DSCH_JOB_ST_STARTED_VALUE);
		} else if (DSchContext.getInstance().getJobStatus(context.getJobId()) != DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE) {
			// 当前任务为运行状态并且获取命令为停止
			DSchContext.getInstance().updateJobStatus(context.getJobId() , DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE);
		}
	}
}
