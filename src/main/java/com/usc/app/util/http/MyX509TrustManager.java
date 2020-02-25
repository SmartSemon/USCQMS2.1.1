package com.usc.app.util.http;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * <p>
 * 证书信任管理器（用于https请求）
 * </p>
 *
 * @author semon
 * @date 2020-01-07
 */
public class MyX509TrustManager implements X509TrustManager
{

	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
	{
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
	{
	}

	public X509Certificate[] getAcceptedIssuers()
	{
		return null;
	}
}