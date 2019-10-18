package com.hs.dsch.launch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import com.hs.dsch.annotation.DScheduledJob;

public class DSchedulerJob extends DSchedulerSpringFactoryImportSelector<DScheduledJob> {
	
	private static Logger logger = LoggerFactory.getLogger(DSchedulerJob.class);

	@Override
	public String[] selectImports(AnnotationMetadata metadata) {
		AnnotationAttributes attributes = AnnotationAttributes.fromMap(
				metadata.getAnnotationAttributes(getAnnotationClass().getName(), true));
		
		initApplication(attributes);

		return new String[0];
	}
	
	private void initApplication(AnnotationAttributes attributes) {
		String job = attributes.getString("job");
		String desc = attributes.getString("desc");
		
		logger.info("任务：job:{} desc:{}" , job , desc);
	}
}
