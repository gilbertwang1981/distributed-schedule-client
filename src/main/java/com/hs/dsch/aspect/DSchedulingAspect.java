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
import com.hs.dsch.handler.DSchJobHandlerType;
import com.hs.dsch.proto.DSchAdminProto.DSchJobStatus;

@Aspect
@Component
@Order(3)
public class DSchedulingAspect {
	private static Logger logger = LoggerFactory.getLogger(DSchedulingAspect.class);

	@Pointcut("@annotation(com.hs.dsch.annotation.DScheduled)")
    public void schedulePointCut() {
    }
	
	@Around("schedulePointCut() && @annotation(dsechduled)")
    public Object around(ProceedingJoinPoint point , DScheduled dsechduled) throws Throwable {
		if (DSchContext.getInstance().isNodeShutdown()) {
			logger.error("节点已经下线,{}" , DSchContext.getInstance().getNodeId());
			
			System.exit(0);
		}
		
		String jobId = DSchContext.getInstance().getJob(dsechduled.job());
		if (jobId == null) {
			logger.error("找不到任务，同服务器失联.{}" , dsechduled.job());
			
			DSchContext.getInstance().updateJobStatus(jobId, DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE);
			
			return point.proceed();
		}
		
		DSchJobContext preContext = new DSchJobContext();
		preContext.setNodeId(DSchContext.getInstance().getNodeId());
		preContext.setJobId(jobId);
		
		DSchJobHandlerMgr.getInstance().handle(DSchJobHandlerType.DSCH_JOB_HANDLER_TYPE_PRE , preContext);
		
		if (DSchContext.getInstance().getJobStatus(jobId) == DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE) {
			logger.error("任务状态已停止，同服务器失联.{}" , dsechduled.job() , DSchContext.getInstance().getJobStatus(jobId));
			
			return null;
		}
		
		DSchContext.getInstance().updateJobStatus(jobId, DSchJobStatus.DSCH_JOB_ST_RUNNING_VALUE);
		
		DSchJobContext postContext = new DSchJobContext();
		
		postContext.setBeginTime(System.currentTimeMillis());
		
		Object returnObj = point.proceed();
		
		postContext.setEndTime(System.currentTimeMillis());
		
		DSchContext.getInstance().updateJobStatus(jobId, DSchJobStatus.DSCH_JOB_ST_IDLING_VALUE);

		postContext.setJobId(jobId);
		postContext.setJobName(dsechduled.job());
		postContext.setNodeId(DSchContext.getInstance().getNodeId());
		
		DSchJobHandlerMgr.getInstance().handle(DSchJobHandlerType.DSCH_JOB_HANDLER_TYPE_POST , postContext);
		
		return returnObj;
	}
}
