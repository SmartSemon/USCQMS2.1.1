package com.usc.app.util.tran;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.usc.conf.cf.i18n.MessageSourceUtil;
import com.usc.util.SpringContextUtil;

/**
 * 国际化语言
 * 
 * @author Semon
 *
 */
public class InternationalFormat {
	/**
	 * @param code 翻译编码
	 * @return
	 */
	public static String getFormatMessage(String code) {
		return getMessageSourceUtil().getMessage(code);
	}

	/**
	 * @param code   翻译编码
	 * @param locale 语言环境
	 * @return
	 */
	public static String getFormatMessage(String code, Locale locale) {
		return getMessageSourceUtil().getMessage(code, locale);
	}

	/**
	 * @param code    翻译编码
	 * @param request HttpServletRequest
	 * @return
	 */
	public static String getFormatMessage(String code, HttpServletRequest request) {
		return getFormatMessage(code, request.getLocale());
	}

	private static MessageSourceUtil getMessageSourceUtil() {
		return SpringContextUtil.getBean(MessageSourceUtil.class);
	}
}
