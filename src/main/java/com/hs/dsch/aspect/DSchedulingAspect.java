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
import com.hs.dsch.vo.DSchJobData;

@Aspect
@Component
@Order(3)
public class DSchedulingAspect {
	private static Logger logger = LoggerFactory.getLogger(DSchedulingAspect.class);

	@Pointcut("@annotation(com.hs.dsch.annotation.DScheduled)")
    public void schedulePointCut() {
    }
	
	private DSchJobData handleRegJob(DScheduled dscheduled) {
		DSchJobData jobData = DSchContext.getInstance().getJob(dscheduled.job());
		if (jobData != null) {
			return jobData;
		}
		
		DSchJobContext jobRegContext = new DSchJobContext();
		jobRegContext.setNodeId(DSchContext.getInstance().getNodeId());
		jobRegContext.setJobName(dscheduled.job());
		jobRegContext.setCron(dscheduled.cron());
		jobRegContext.setDesc(dscheduled.desc());
		jobRegContext.setFixDelay(dscheduled.fixedDelay());
		jobRegContext.setFixRate(dscheduled.fixedRate());
		jobRegContext.setInitialDelay(dscheduled.initialDelay());
		
		DSchJobHandlerMgr.getInstance().handle(DSchHandlerType.DSCH_JOB_HANDLER_TYPE_REG , jobRegContext);
		
		return DSchContext.getInstance().getJob(dscheduled.job());
	}
	
	private void handleCommands(DSchJobData jobData) {
		DSchJobContext commandGetContext = new DSchJobContext();
		commandGetContext.setNodeId(DSchContext.getInstance().getNodeId());
		commandGetContext.setJobId(jobData.getJobId());
		
		DSchJobHandlerMgr.getInstance().handle(DSchHandlerType.DSCH_JOB_HANDLER_TYPE_COMMAND , commandGetContext);		
	}
	
	@Around("schedulePointCut() && @annotation(dscheduled)")
    public Object around(ProceedingJoinPoint point , DScheduled dscheduled) throws Throwable {
		if (DSchContext.getInstance().isNodeShutdown()) {
			logger.error("节点已经下线，JVM进程即将退出。{}" , DSchContext.getInstance().getNodeId());
			
			System.exit(0);
		}
		
		DSchJobData jobData = handleRegJob(dscheduled);
		if (jobData == null) {
			logger.error("任务注册失败，服务启动失败.{}" , dscheduled.job());
			
			System.exit(0);
		}
		
		handleCommands(jobData);
		
		if (DSchContext.getInstance().getJobStatus(jobData.getJobId()) == DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE) {
			logger.error("任务状态已停止，同服务器失联.{}" , dscheduled.job() , DSchContext.getInstance().getJobStatus(jobData.getJobId()));
			
			return null;
		}
		
		DSchContext.getInstance().updateJobStatus(jobData.getJobId(), DSchJobStatus.DSCH_JOB_ST_RUNNING_VALUE);
		
		DSchJobContext healthCheckContext = new DSchJobContext();
		
		healthCheckContext.setBeginTime(System.currentTimeMillis());
		
		Object returnObj = point.proceed();
		
		healthCheckContext.setEndTime(System.currentTimeMillis());
		
		DSchContext.getInstance().updateJobStatus(jobData.getJobId() , DSchJobStatus.DSCH_JOB_ST_IDLING_VALUE);

		handleHealthCheck(healthCheckContext , jobData , dscheduled);

		return returnObj;
	}
	
	private void handleHealthCheck(DSchJobContext healthCheckContext , DSchJobData jobData , DScheduled dscheduled) {
		healthCheckContext.setJobId(jobData.getJobId());
		healthCheckContext.setJobName(dscheduled.job());
		healthCheckContext.setNodeId(DSchContext.getInstance().getNodeId());
		healthCheckContext.setCron(dscheduled.cron());
		healthCheckContext.setFixDelay(dscheduled.fixedDelay());
		healthCheckContext.setFixRate(dscheduled.fixedRate());
		healthCheckContext.setInitialDelay(dscheduled.initialDelay());
		
		DSchJobHandlerMgr.getInstance().handle(DSchHandlerType.DSCH_JOB_HANDLER_TYPE_JOB_HC , healthCheckContext);
	}
}
