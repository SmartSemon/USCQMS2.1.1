package com.usc.app.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.usc.conf.cf.i18n.MessageSourceUtil;

@ControllerAdvice
public class ExceptionHandlerUtil {
	@Autowired
	private MessageSourceUtil messageSourceUtil;

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleBussinessException(Exception ex) {
		Map<String, Object> result = new HashMap<>();
		result.put("code", 400);
		result.put("info", messageSourceUtil.getMessage(ex.getMessage()));
		return ResponseEntity.badRequest().body(result);
	}
}
