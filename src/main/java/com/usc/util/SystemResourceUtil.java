package com.usc.util;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.alibaba.fastjson.JSONObject;

public final class SystemResourceUtil
{
	/**
	 * 项目根目录物理地址
	 */
	public static final String dir = SystemProperties.getUSER_DIR();
	public static final String FILE_SEPARATOR = SystemProperties.getFILE_SEPARATOR();

	/**
	 * 项目resource资源文件夹物理地址
	 */
	public static final String resourcePath = dir + FILE_SEPARATOR + "src" + FILE_SEPARATOR + "main" + FILE_SEPARATOR
			+ "resources";

	/**
	 * @param localPath resource路径下的文件相对路径
	 * @return
	 */
	public static File getResourcesFile(String localPath)
	{
		if (ObjectHelperUtils.isNotEmpty(localPath))
		{
			if (localPath.contains(Symbols.SPOT))
			{

			}
			String path = resourcePath;
			if (localPath.startsWith(Symbols.BACKSLASH) || localPath.startsWith(Symbols.EXCEPT))
			{
				path = resourcePath + localPath;
			} else
			{
				path = SystemProperties.FILE_SEPARATOR + resourcePath + localPath;
			}
			File source = new File(path);
			if (source.exists())
			{
				return source;
			}
		}
		return null;
	}

	public static JSONObject getProperties(String... propNames)
	{
		if (ObjectHelperUtils.isEmpty(propNames))
		{
			return null;
		}

		JSONObject map = null;
		try
		{
			for (String propName : propNames)
			{

				Properties properties = PropertiesLoaderUtils.loadAllProperties(propName);
				if (properties != null)
				{
					map = new JSONObject();
					Enumeration<Object> keys = properties.keys();
					while (keys.hasMoreElements())
					{
						String key = (String) keys.nextElement();
						String value = properties.getProperty(key);
						map.put(key, value);
					}
				}

			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return map;
	}
}
