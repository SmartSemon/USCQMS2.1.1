package com.usc.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JBeanUtils {
	public final static String PORT = ".";
	public final static String SLASH = "/";
	public final static String BACK_SLASH = "\\";

	public static Class<?> forName(String clazz) {
		try
		{
			return Class.forName(clazz);
		} catch (ClassNotFoundException e)
		{
			log.error(clazz + " >>> Class Not Found Exception", e);
		}
		return null;
	}

	public static Object newInstance(String clazz, Object... paramTypes) {

		if (isClassName(clazz))
		{
			try
			{
				Class<?> clas = forName(clazz);
				if (clas == null)
				{ return null; }
				return newInstance(clas, paramTypes);
			} catch (Exception e)
			{
				log.error(clazz + " Class newInstance Exception", e);
			}
		}
		return null;
	}

	public static Object newInstance(Class<?> clazz, Object... paramTypes) throws Exception {
		Class<?>[] parameterTypes = null;
		if (!ArrayUtils.isEmpty(paramTypes))
		{
			int l = paramTypes.length;
			parameterTypes = new Class<?>[l];
			for (int i = 0; i < l; i++)
			{ parameterTypes[i] = paramTypes[i].getClass(); }
		}

		return newInstance(clazz.getConstructor(parameterTypes), paramTypes);
	}

	public static Object newInstance(Constructor<?> constructor, Object... paramTypes) throws Exception {
		return constructor.newInstance(paramTypes);
	}

	public static boolean isClassName(String clazz) {
		if (ObjectHelperUtils.isNotEmpty(clazz))
		{
			clazz = clazz.replace(SLASH, PORT).replace(BACK_SLASH, PORT);
			if (clazz.contains(PORT) && !clazz.startsWith(PORT) && !clazz.endsWith(PORT))
			{ return true; }
		}
		return false;
	}

	/*
	 * 通过反射执行class的无参（No args）方法
	 * 
	 * @param object ---调到该方法的具体对象
	 * 
	 * @param cclass ---具体对象的class类型
	 * 
	 * @param methodName ---反射方法的名称
	 * 
	 **/
	public static Object reflection(Object object, Class<?> cclass, String methodName) {

		try
		{

			Method method = cclass.getMethod(methodName);
			return method.invoke(object);
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		} catch (SecurityException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}

		return null;

	}

	/*
	 * 通过反射执行class的有参（args）方法
	 * 
	 * @param object ---调到该方法的具体对象
	 * 
	 * @param cclass ---具体对象的class类型
	 * 
	 * @param methodName ---反射方法的名称
	 * 
	 * @param paramClasses ---反射方法中参数class类型
	 * 
	 * @param args ----调用该方法用到的具体参数
	 * 
	 * 
	 **/
	public static Object reflection(Object object, Class<?> cclass, String methodName, Class<?>[] paramclasses,
			Object... args) {

		try
		{

			Method method = cclass.getMethod(methodName, paramclasses);
			return method.invoke(object, args);
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		} catch (SecurityException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}

		return null;

	}
}
