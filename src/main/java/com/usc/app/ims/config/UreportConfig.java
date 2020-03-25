package com.usc.app.ims.config;

import javax.servlet.Servlet;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.bstek.ureport.console.UReportServlet;

/**
 * Ureport2 配置类
 * 
 */

//@ImportResource("classpath:ureport-console-context.xml")
//@EnableAutoConfiguration
//@Configuration
public class UreportConfig {

	@Bean
	public ServletRegistrationBean<Servlet> buildUreportServlet() {
		return new ServletRegistrationBean<Servlet>(new UReportServlet(), "/ureport/*");
	}

}
