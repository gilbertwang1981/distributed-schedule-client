package com.hs.dsch;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.hs.dsch.launch.DistributedSchedulerLauncher;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableScheduling
@Import(DistributedSchedulerLauncher.class)
public @interface DistributedScheduler {
	/**
	 * 名字空间
	 * @return
	 */
	String namespace();
	
	/**
	 * 服务名字
	 * @return
	 */
	String service();
	
	/**
	 * 分组名
	 * @return
	 */
	String group();
}
