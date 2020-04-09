package com.usc.conf.cf.i18n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageSourceUtil {

	@Autowired
	private MessageSource messageSource;

	public String getMessage(String code) {
		return getMessage(code, new String[] {});
	}

	public String getMessage(String code, Locale locale) {
		return messageSource.getMessage(code, null, locale);
	}

	public String getMessage(String code, HttpServletRequest request) {
		return getMessage(code, request.getLocale());
	}

	public String getMessage(String code, Object[] args) {
		return getMessage(code, args, "");
	}

	public String getMessage(String code, Object[] args, String defaultMsg) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(code, args, defaultMsg, locale);
	}

}
