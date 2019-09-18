package com.hs.dsch.launch;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.env.Environment;

public abstract class DistributedSchedulerSpringFactoryImportSelector<T> implements DeferredImportSelector, BeanClassLoaderAware, EnvironmentAware {
	
	private Class<T> annotationClass;
	
	@SuppressWarnings("unchecked")
	protected DistributedSchedulerSpringFactoryImportSelector() {
	this.annotationClass = (Class<T>) GenericTypeResolver
			.resolveTypeArgument(this.getClass(), DistributedSchedulerSpringFactoryImportSelector.class);
	}
	
	protected Class<T> getAnnotationClass() {
		return this.annotationClass;
	}
	
	@Override
	public void setEnvironment(Environment environment) {
	}
	
	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
	}
}

