package com.usc.conf.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Properties;

import org.springframework.boot.ApplicationArguments;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.usc.cache.redis.RedisUtil;
import com.usc.server.AppRunner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(30)
public class TranslateData2RedisRunner extends AppRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try
		{
			Long st = System.currentTimeMillis();
			initTranslateData();
			log.info("Initialize language transfer successfully, in " + (System.currentTimeMillis() - st) + " ms");
		} catch (Exception e)
		{
			log.error("Initialize language transfer failded", e);
		}

	}

	private void propertice(File file) {
		RedisUtil redisUtil = RedisUtil.getInstanceOfObject();
		Properties properties = new Properties();
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
			properties.load(br);
			br.close();
			Map<Object, Object> map = properties;
			redisUtil.set("ACTIONTRANSLATEMSG", map);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void initTranslateData() {
		File f2 = new File(this.getClass().getResource("").getPath());
		File t = new File(f2.getPath() + "\\res\\actiontranslatemsg.properties");
		propertice(t);
	}

}
