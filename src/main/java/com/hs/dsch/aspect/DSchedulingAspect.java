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
import com.hs.dsch.proto.DSchAdminProto.DSchJobHealthStatus;
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
		logger.info("注册任务 {}" , dscheduled.job());
		
		DSchJobData jobData = DSchContext.getInstance().getJob(dscheduled.job());
		if (jobData != null) {
			logger.info("找到注册任务 {}" , dscheduled.job());
			
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
		
		logger.info("处理任务命令 {}" , commandGetContext);
		
		DSchJobHandlerMgr.getInstance().handle(DSchHandlerType.DSCH_JOB_HANDLER_TYPE_COMMAND , commandGetContext);		
	}
	
	@Around("schedulePointCut() && @annotation(dscheduled)")
    public Object around(ProceedingJoinPoint point , DScheduled dscheduled) throws Throwable {
		logger.info("进入任务切面 {}" , dscheduled.job());
		
		if (DSchContext.getInstance().isNodeShutdown()) {
			logger.error("节点关闭，任务停止执行 {}" , DSchContext.getInstance().getNodeId());
			
			return null;
		}
		
		DSchJobData jobData = handleRegJob(dscheduled);
		if (jobData == null) {
			logger.error("任务注册失败，服务启动失败 {}" , dscheduled.job());
			
			System.exit(0);
		}
		
		handleCommands(jobData);
		
		if (DSchContext.getInstance().getJobStatus(jobData.getJobId()) == DSchJobStatus.DSCH_JOB_ST_STOPPED_VALUE) {
			logger.error("任务状态已停止，同服务器失联 {}" , dscheduled.job() , DSchContext.getInstance().getJobStatus(jobData.getJobId()));
			
			return null;
		}
		
		DSchContext.getInstance().updateJobStatus(jobData.getJobId(), DSchJobStatus.DSCH_JOB_ST_RUNNING_VALUE);
		
		DSchJobContext healthCheckContext = new DSchJobContext();
		
		healthCheckContext.setBeginTime(System.currentTimeMillis());
		
		Object returnObj = null;
		DSchJobHealthStatus jobStatus = DSchJobHealthStatus.DSCH_JOB_ST_GREEN;
		try {
			returnObj = point.proceed();
		} catch (Exception e) {
			jobStatus = DSchJobHealthStatus.DSCH_JOB_ST_RED;
		}
		
		healthCheckContext.setEndTime(System.currentTimeMillis());
		
		if (jobStatus == DSchJobHealthStatus.DSCH_JOB_ST_GREEN) {
			if (dscheduled.fixedRate() > 0L && dscheduled.fixedRate() < (healthCheckContext.getEndTime() - healthCheckContext.getBeginTime())) {
				jobStatus = DSchJobHealthStatus.DSCH_JOB_ST_YELLOW;
			}
		}
		
		healthCheckContext.setJobStatus(jobStatus);
		
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
