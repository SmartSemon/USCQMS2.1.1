package com.usc.server.util;

public final class BrowserAcceptLanguage {
	public final static String zh_CN = "zh-CN";
	public final static String en_US = "en-US";

	public static BrowserAcceptLanguage buid() {
		return new BrowserAcceptLanguage();
	}

	public static String zhCNMethod() {
		return "getName";
	}

	public static String enUSMethod() {
		return "getEnName";
	}
}
