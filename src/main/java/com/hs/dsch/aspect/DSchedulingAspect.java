package com.hs.dsch.aspect;

import org.apache.http.HttpResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.hs.dsch.annotation.DScheduled;
import com.hs.dsch.conf.DSchConfiguration;
import com.hs.dsch.conf.DSchContext;
import com.hs.dsch.consts.DSchClientConsts;
import com.hs.dsch.proto.DSchAdminProto.AdminResponseCode;
import com.hs.dsch.proto.DSchAdminProto.DSchAdminHealthCheckRequest;
import com.hs.dsch.proto.DSchAdminProto.DSchAdminHealthCheckResponse;
import com.hs.dsch.proto.DSchAdminProto.DSchAdminJob;
import com.hs.dsch.util.HttpClient;

@Aspect
@Component
@Order(3)
public class DSchedulingAspect {
	private static Logger logger = LoggerFactory.getLogger(DSchedulingAspect.class);
	
	private DSchConfiguration dschConfiguration = DSchContext.getInstance().getDSchConfiguration();
	private HttpClient httpClient = DSchContext.getInstance().getHttpClient();
	
	@Pointcut("@annotation(com.hs.dsch.annotation.DScheduled)")
    public void schedulePointCut() {
    }
	
	@Around("schedulePointCut() && @annotation(dsechduled)")
    public Object around(ProceedingJoinPoint point , DScheduled dsechduled) throws Throwable {
		long begin = System.currentTimeMillis();
		
		Object returnObj = point.proceed();
		
		long end = System.currentTimeMillis();
		
		long duration = end - begin;
		
		DSchAdminHealthCheckRequest.Builder request = DSchAdminHealthCheckRequest.newBuilder();
		request.setNodeId(DSchContext.getInstance().getNodeId());
		DSchAdminJob.Builder job = DSchAdminJob.newBuilder();
		job.setExecTime(duration == 0?1L:duration);
		
		if (DSchContext.getInstance().getJob(dsechduled.job()) == null) {
			logger.error("找不到任务，和服务器失联.{}" , dsechduled.job());
			
			return returnObj;
		}
		
		job.setJobId(DSchContext.getInstance().getJob(dsechduled.job()));
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
		
		return returnObj;
	}
}
