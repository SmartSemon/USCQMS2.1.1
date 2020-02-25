package com.usc.app.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.usc.util.ObjectHelperUtils;
import com.usc.util.SystemResourceUtil;

public class PropertyFileUtil
{
	public static Map<Object, Object> propertice(File propertiesFile)
	{
		if (propertiesFile != null && propertiesFile.exists() && propertiesFile.isFile())
		{
			Properties properties = new Properties();
			try (BufferedReader br = new BufferedReader(new FileReader(propertiesFile)))
			{
				properties.load(br);
				Map<Object, Object> map = properties;
				return map;
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return null;
	}

	public static Map application = new HashMap();

	public static String getApplicationPropertyValue(String key)
	{
		if (key == null)
		{
			return null;
		}
		if (application.containsKey(key))
		{
			return (String) application.get(key);
		}
		File file = SystemResourceUtil.getResourcesFile("/config/application.properties");
		if (file == null)
		{
			return null;
		}
		Map classMap = propertice(file);
		if (!ObjectHelperUtils.isEmpty(classMap))
		{
			application.putAll(classMap);
			if (application.containsKey(key))
			{
				return (String) application.get(key);
			}
		}

		return null;

	}

	public static String getResourcePropertyValue(String filePath, String key)
	{
		if (key == null || filePath == null)
		{
			return null;
		}
		if (application.containsKey(key))
		{
			return (String) application.get(key);
		}
		File file = SystemResourceUtil.getResourcesFile(filePath);
		if (file == null)
		{
			return null;
		}
		Map classMap = propertice(file);
		if (!ObjectHelperUtils.isEmpty(classMap))
		{
			application.putAll(classMap);
			if (application.containsKey(key))
			{
				return (String) application.get(key);
			}
		}

		return null;

	}

}
