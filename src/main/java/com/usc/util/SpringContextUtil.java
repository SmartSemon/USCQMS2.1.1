package com.usc.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware
{
	protected static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		if (context == null)
		{
			context = applicationContext;
		}

	}

	public static ApplicationContext getApplicationContext()
	{
		return context;
	}

	public static <T> T getBean(Class<T> clazz)
	{
		return context.getBean(clazz);
	}

	public static <T> T getBean(String name, Class<T> beanClass)
	{
		return context.getBean(name, beanClass);
	}

	public static final Object getBean(String beanName)
	{
		return getApplicationContext().getBean(beanName);
	}

	public static final Object getBean(String beanName, String className) throws ClassNotFoundException
	{
		Class<?> clz = Class.forName(className);
		return getApplicationContext().getBean(beanName, clz.getClass());
	}

	public static boolean containsBean(String name)
	{
		return getApplicationContext().containsBean(name);
	}

	public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException
	{
		return getApplicationContext().isSingleton(name);
	}

	public static Class<?> getType(String name) throws NoSuchBeanDefinitionException
	{
		return getApplicationContext().getType(name);
	}

	public static String[] getAliases(String name) throws NoSuchBeanDefinitionException
	{
		return getApplicationContext().getAliases(name);
	}

}
