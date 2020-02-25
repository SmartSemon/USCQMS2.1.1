package com.usc.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfigurer implements WebMvcConfigurer
{
	@Autowired
	private OnlineInterceptor onlineInterceptor;
	@Autowired
	private LoginInterceptor loginInterceptor;
	@Autowired
	private LogoutInterceptor logoutInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry)
	{
		registry.addInterceptor(loginInterceptor).addPathPatterns("/login");
		registry.addInterceptor(logoutInterceptor).addPathPatterns("/logout");

		registry.addInterceptor(onlineInterceptor).addPathPatterns("/**").excludePathPatterns("/login", "/logout",
				"/src/**","/act/**");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry)
	{
		registry.addMapping("/**").allowedOrigins("*")
				.allowedMethods("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE")
				.allowCredentials(true);
	}
}
