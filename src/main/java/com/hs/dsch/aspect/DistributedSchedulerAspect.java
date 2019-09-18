package com.hs.dsch.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DistributedSchedulerAspect {
	private static Logger logger = LoggerFactory.getLogger(DistributedSchedulerAspect.class);
	
	@Pointcut("@annotation(com.hs.dsch.aspect.DScheduler)")
    public void schedulePointCut() {
    }
	
	@Around("schedulePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
		logger.info("执行调度方法, {}" , point.getArgs());
		
		return point.proceed();
	}
}
