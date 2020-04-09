package com.usc.conf.cf.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.usc.app.action.retmsg.ActionMessage;

//@ControllerAdvice(value = { "com.usc.app.entry" })
public class LocaleResponseBodyAdvice implements ResponseBodyAdvice<ActionMessage> {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public ActionMessage beforeBodyWrite(ActionMessage body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		System.out.println(body);
		return body;
	}

}
