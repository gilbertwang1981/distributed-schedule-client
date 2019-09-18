package com.hs.dsch.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hs.dsch.annotation.DScheduled;

@Aspect
@Component
public class DSchedulingAspect {
	private static Logger logger = LoggerFactory.getLogger(DSchedulingAspect.class);
	
	@Pointcut("@annotation(com.hs.dsch.annotation.DScheduled)")
    public void schedulePointCut() {
    }
	
	@Around("schedulePointCut() && @annotation(dsechduled)")
    public Object around(ProceedingJoinPoint point , DScheduled dsechduled) throws Throwable {
		logger.info("执行调度方法, {} {}" , dsechduled.fixedRate() , dsechduled.initialDelay());
		
		return point.proceed();
	}
}
