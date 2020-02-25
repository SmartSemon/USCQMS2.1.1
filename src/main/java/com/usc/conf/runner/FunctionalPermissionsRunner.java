package com.usc.conf.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.usc.server.AppRunner;
import com.usc.server.init.InitJurisdictionData;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>
 * Description: 授权信息初始化
 * </P>
 *
 * @date 2019年9月17日
 * @author SEMON
 */
@Slf4j
@Component
@Order(4)
public class FunctionalPermissionsRunner extends AppRunner
{

	@Override
	public void run(ApplicationArguments args) throws Exception
	{
		Long st = System.currentTimeMillis();
		InitJurisdictionData.init();
		log.info("Authorization initialized successfully, in " + (System.currentTimeMillis() - st) + " ms");
	}

}
