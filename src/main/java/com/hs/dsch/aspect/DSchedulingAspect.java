package com.hs.dsch.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.hs.dsch.annotation.DScheduled;
import com.hs.dsch.conf.DSchContext;
import com.hs.dsch.handler.DSchJobContext;
import com.hs.dsch.handler.DSchJobHandlerMgr;
import com.hs.dsch.handler.DSchHandlerType;
import com.hs.dsch.proto.DSchAdminProto.DSchJobStatus;

@Aspect
@Component
@Order(3)
public class DSchedulingAspect {
	private static Logger logger = LoggerFactory.getLogger(DSchedulingAspect.class);

	@Pointcut("@annotation(com.hs.dsch.annotation.DScheduled)")
    public void schedulePointCut() {
    }
	
	@Around("schedulePointCut() && @annotation(dscheduled)")
    public Object around(ProceedingJoinPoint point , DScheduled dscheduled) throws Throwable {
		if (DSchContext.getInstance().isNodeShutdown()) {
			logger.error("节点已经下线,{}" , DSchContext.getInstance().getNodeId());
			
			System.exit(0);
		}
		
		String jobId = DSchContext.getInstance().getJob(dscheduled.job());
		if (jobId == null) {
			logger.error("找不到任务，同服务器失联.{}" , dscheduled.job());
			
			DSchContext.getInstance().updateJobStatus(jobId, DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE);
			
			return point.proceed();
		}
		
		DSchJobContext preContext = new DSchJobContext();
		preContext.setNodeId(DSchContext.getInstance().getNodeId());
		preContext.setJobId(jobId);
		
		DSchJobHandlerMgr.getInstance().handle(DSchHandlerType.DSCH_JOB_HANDLER_TYPE_COMMAND , preContext);
		
		if (DSchContext.getInstance().getJobStatus(jobId) == DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE) {
			logger.error("任务状态已停止，同服务器失联.{}" , dscheduled.job() , DSchContext.getInstance().getJobStatus(jobId));
			
			return null;
		}
		
		DSchContext.getInstance().updateJobStatus(jobId, DSchJobStatus.DSCH_JOB_ST_RUNNING_VALUE);
		
		DSchJobContext postContext = new DSchJobContext();
		
		postContext.setBeginTime(System.currentTimeMillis());
		
		Object returnObj = point.proceed();
		
		postContext.setEndTime(System.currentTimeMillis());
		
		DSchContext.getInstance().updateJobStatus(jobId, DSchJobStatus.DSCH_JOB_ST_IDLING_VALUE);

		postContext.setJobId(jobId);
		postContext.setJobName(dscheduled.job());
		postContext.setNodeId(DSchContext.getInstance().getNodeId());
		postContext.setCron(dscheduled.cron());
		postContext.setFixDelay(dscheduled.fixedDelay());
		postContext.setFixRate(dscheduled.fixedRate());
		postContext.setInitialDelay(dscheduled.initialDelay());
		
		DSchJobHandlerMgr.getInstance().handle(DSchHandlerType.DSCH_JOB_HANDLER_TYPE_JOB_HC , postContext);
		
		return returnObj;
	}
}
