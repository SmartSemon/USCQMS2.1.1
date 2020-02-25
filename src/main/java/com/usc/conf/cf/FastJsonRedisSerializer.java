package com.usc.conf.cf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.IOUtils;
import com.usc.util.ObjectHelperUtils;
import com.usc.util.SystemProperties;

public class FastJsonRedisSerializer<T> implements RedisSerializer<Object>
{
	private Class<T> clazz;
	private static ParserConfig defaultRedisConfig = new ParserConfig();
	static
	{
		defaultRedisConfig.setAutoTypeSupport(true);
		String autoTypeAccept = null;
		try
		{
			Properties properties = PropertiesLoaderUtils.loadAllProperties("config/application.properties");
			autoTypeAccept = properties.getProperty("fastjson.parser.autoTypeAccept");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		if (autoTypeAccept != null)
		{
			String[] types = autoTypeAccept.split(",");
			for (String string : types)
			{
				String path = SystemProperties.getUSER_DIR() + "\\src\\main\\java\\" + (string.replace(".", "\\"));
				File file = new File(path);
				if (file.isDirectory())
				{
					getAllFileName(string, path, new ArrayList<String>());
				} else
				{
					defaultRedisConfig.addAccept(string);
				}
			}

		}

	}

	public FastJsonRedisSerializer(Class<T> clazz)
	{
		super();
		this.clazz = clazz;
	}

	public byte[] serialize(Object object) throws SerializationException
	{
		if (object == null)
		{
			return new byte[0];
		}
		try
		{
			return JSON.toJSONBytes(object, SerializerFeature.WriteClassName);
		} catch (Exception ex)
		{
			throw new SerializationException("Could not serialize: " + ex.getMessage(), ex);
		}
	}

	public T deserialize(byte[] bytes) throws SerializationException
	{
		if (bytes == null || bytes.length == 0)
		{
			return null;
		}
		try
		{
			String string = new String(bytes, IOUtils.UTF8);
			return JSON.parseObject(string, this.clazz, defaultRedisConfig, Feature.OrderedField);
		} catch (Exception ex)
		{
			throw new SerializationException("Could not deserialize: " + ex.getMessage(), ex);
		}
	}

	public static void getAllFileName(String classDirPath, String path, ArrayList<String> fileNameList)
	{
		File file = new File(path);
		File[] tempList = file.listFiles();
		if (ObjectHelperUtils.isEmpty(tempList))
		{
			return;
		}
		for (int i = 0; i < tempList.length; i++)
		{
			File f = tempList[i];
			if (tempList[i].isFile())
			{
				String fileName = f.getName();
				String classSamplName = fileName.substring(0, fileName.lastIndexOf("."));
				String className = classDirPath + "." + classSamplName;
				defaultRedisConfig.addAccept(className);
				fileNameList.add(f.getName());
			}
			if (f.isDirectory())
			{
				getAllFileName(classDirPath + "." + f.getName(), f.getAbsolutePath(), fileNameList);
			}
		}
	}
}
