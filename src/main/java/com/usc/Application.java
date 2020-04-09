package com.usc;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: Application <br/>
 * Class description: Application Entry. <br/>
 * date: 2019年7月31日 下午4:50:35 <br/>
 * 
 * @version 2.2.2
 * @author PuTianXiong
 * @since JDK 1.8
 */
@Slf4j
@EnableCaching
@EnableAsync
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		log.info("QMS-Service Started Successfully");
	}

}
