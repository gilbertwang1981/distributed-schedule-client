package com.hs.dsch.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import com.hs.dsch.launch.DSchedulerJob;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(DSchedulerJob.class)
public @interface DScheduledJob {
	/**
	 * 作业名字
	 * @return
	 */
	String job();
	
	/**
	 * 任务描述
	 * @return
	 */
	String desc() default "";
}
