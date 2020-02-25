package com.usc.app.util.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.usc.server.util.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerletUtils
{
	public static void returnResponseJson(ServletResponse response, String json)
	{
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		try (PrintWriter writer = response.getWriter())
		{
			writer.print(json);
		} catch (IOException e)
		{
			log.error("JSON err:" + json);
		}
	}

	public static void setFileResponse(HttpServletResponse response, String outFileName)
	{
		response.reset();
		response.setHeader("content-type", "application/octet-stream");
		response.setContentType("application/octet-stream;charset=utf-8");
		try
		{
			response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(outFileName, "UTF-8"));
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	public static String getRequestJsonParam(HttpServletRequest request)
	{
		String jsonParam = "";
		int contentLength = request.getContentLength();
		if (!(contentLength < 0))
		{
			try (ServletInputStream inputStream = request.getInputStream())
			{
				byte[] buffer = new byte[contentLength];
				for (int i = 0; i < contentLength;)
				{
					int len = inputStream.read(buffer, i, contentLength);
					if (len == -1)
					{
						break;
					}
					i += len;
				}
				jsonParam = new String(buffer, "utf-8");
			} catch (IOException e)
			{
				LoggerFactory.logError("参数转换成json异常g{}", e);
			}
		}

		return jsonParam;
	}

	public static byte[] InputStreamToByte(InputStream is, int n)
	{

		byte[] buffer = new byte[n];
		int ch;
		/**
		 *
		 * */
		try (ByteArrayOutputStream bytestream = new ByteArrayOutputStream();)
		{
			while ((ch = is.read(buffer)) != -1)
			{
				bytestream.write(buffer, 0, ch);
			}
			byte data[] = bytestream.toByteArray();
			bytestream.close();
			return data;
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] InputStreamToByte(InputStream is)
	{

		byte[] buffer = new byte[1024];
		int ch;
		/**
		 *
		 * */
		try (ByteArrayOutputStream bytestream = new ByteArrayOutputStream();)
		{
			while ((ch = is.read(buffer)) != -1)
			{
				bytestream.write(buffer, 0, ch);
			}
			byte data[] = bytestream.toByteArray();
			bytestream.close();
			return data;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;

	}

	public static ServletRequest getServletRequestWapper(HttpServletRequest httpServletRequest)
	{
		try
		{
			return new ServletRequestWrapper(httpServletRequest);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return httpServletRequest;
	}

	public static ServletRequest getServletRequestWapper(ServletRequest request)
	{
		return getServletRequestWapper((HttpServletRequest) request);
	}

	public static ServletResponse getServletResponseWapper(HttpServletResponse httpServletResponse) throws IOException
	{
		return new ServletResponseWrapper(httpServletResponse);
	}

	public static ServletResponse getServletResponseWapper(ServletResponse response)
	{
		try
		{
			return getServletResponseWapper((HttpServletResponse) response);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return response;
	}

}
