package com.usc.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware {
	protected static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (context == null)
		{ context = applicationContext; }

	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	public static <T> T getBean(Class<T> clazz) {
		return context.getBean(clazz);
	}

	public static <T> T getBean(String name, Class<T> beanClass) {
		return context.getBean(name, beanClass);
	}

	public static final Object getBean(String beanName) {
		return getApplicationContext().getBean(beanName);
	}

	public static final Object getBean(String beanName, String className) throws ClassNotFoundException {
		Class<?> clz = Class.forName(className);
		return getApplicationContext().getBean(beanName, clz.getClass());
	}

	public static boolean containsBean(String name) {
		return getApplicationContext().containsBean(name);
	}

	public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return getApplicationContext().isSingleton(name);
	}

	public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		return getApplicationContext().getType(name);
	}

	public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
		return getApplicationContext().getAliases(name);
	}

	public static <T> T registerBean(String name, Class<T> clazz, Object... args) {
		return registerBean((ConfigurableApplicationContext) context, name, clazz, args);
	}

	/**
	 * 主动向Spring容器中注册bean
	 *
	 * @param context Spring容器
	 * @param name    BeanName
	 * @param clazz   注册的bean的类性
	 * @param args    构造方法的必要参数，顺序和类型要求和clazz中定义的一致
	 * @param <T>
	 * @return 返回注册到容器中的bean对象
	 * 
	 */
	private static <T> T registerBean(ConfigurableApplicationContext context, String name, Class<T> clazz,
			Object... args) {
		if (context.containsBean(name))
		{
			Object bean = context.getBean(name);
			if (bean.getClass().isAssignableFrom(clazz))
			{
				return (T) bean;
			} else
			{
				throw new RuntimeException("BeanName 重复 " + name);
			}
		}

		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
		if (args != null)
		{
			for (Object arg : args)
			{ beanDefinitionBuilder.addConstructorArgValue(arg); }
		}

		BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
		BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) context.getBeanFactory();
		beanFactory.registerBeanDefinition(name, beanDefinition);
		return context.getBean(name, clazz);
	}

}
