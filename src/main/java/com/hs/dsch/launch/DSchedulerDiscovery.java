package com.hs.dsch.launch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import com.hs.dsch.annotation.EnableDScheduling;

public class DSchedulerDiscovery extends DSchedulerSpringFactoryImportSelector<EnableDScheduling> {
	private static Logger logger = LoggerFactory.getLogger(DSchedulerDiscovery.class);
	
	@Override
	public String[] selectImports(AnnotationMetadata metadata) {
		AnnotationAttributes attributes = AnnotationAttributes.fromMap(
				metadata.getAnnotationAttributes(getAnnotationClass().getName(), true));
		
		initApplication(attributes);

		return new String[0];
	}

	private void initApplication(AnnotationAttributes attributes) {
		String namespace = attributes.getString("namespace");
		String service = attributes.getString("service");
		String group = attributes.getString("group");
		
		logger.info("分布式计划任务调度客户端启动，namespace:{} service:{} group:{}" , namespace , service , group);
	}
}
