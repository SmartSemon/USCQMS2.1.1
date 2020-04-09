package com.usc.interceptor.a;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.usc.conf.cf.i18n.MessageSourceUtil;

public abstract class APPHandlerInterceptor extends HandlerInterceptorAdapter {
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	@Autowired
	protected MessageSourceUtil messageSourceUtil;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		return beforeHandle(request, response, handler);
	}

	public abstract boolean beforeHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception;

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		afterHandle(request, response, handler, modelAndView);
	}

	public abstract void afterHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception;

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		afterReaderCompletion(request, response, handler, ex);
	}

	protected abstract void afterReaderCompletion(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) throws Exception;
}
