package com.usc.app.util.http;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpURLConnectionUtils
{
	private static final String GET = "GET";
	private static final String POST = "POST";
	private static final String CHARSET = "utf-8";

	public static final String CONTENT_TYPE = "Content-Type";
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String BOUNDARY = "----WebKitFormBoundaryzdoJf6rglgYbKLBw";

	public static Object sendPostRequest(String urlString, String param, MultipartFile[] files)
	{
		try
		{
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(POST);
			conn.setReadTimeout(3000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			conn.setRequestProperty("Charsert", "UTF-8");

		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return param;

	}

	public static String sendPostRequest(String urlString, Object jsonString, String clientID)
	{
		HttpURLConnection conn = null;
		PrintWriter writer = null;
		BufferedReader reader = null;
		try
		{
			conn = createHttpURLConnection(urlString, POST, null, clientID);
			writer = new PrintWriter(conn.getOutputStream());
			writer.print(jsonString);
			writer.flush();

			StringBuffer result = new StringBuffer();
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null)
			{
				result.append(line);
			}
			return result.toString();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			closeIOStram(writer);
			closeIOStram(reader);
			conn.disconnect();
		}
		return null;

	}

	public static JSONObject sendPostRequest(String urlString, String jsonString)
	{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(urlString);
		httpPost.setHeader(CONTENT_TYPE, "application/json");
		httpPost.setEntity(new StringEntity(jsonString, CHARSET));
		CloseableHttpResponse response = null;
		String resp = null;
		try
		{
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			resp = EntityUtils.toString(entity, CHARSET);
			EntityUtils.consume(entity);
		} catch (ClientProtocolException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			closeIOStram(response);
		}
		return JSONObject.parseObject(resp);

	}

	public static Object sendGetRequest(String urlString, String clientID)
	{
		HttpURLConnection conn = null;
		InputStream is = null;
		BufferedReader br = null;
		try
		{
			conn = createHttpURLConnection(urlString, GET, null, clientID);
			conn.connect();
//			Map<String, List<String>> map = conn.getHeaderFields();
//			map.forEach((k, fl) -> {
//				System.out.println(k + "---------->" + fl);
//			});

			if (conn.getResponseCode() == 200)
			{
				is = conn.getInputStream();
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				StringBuffer buffer = new StringBuffer();
				String line = null;
				while ((line = br.readLine()) != null)
				{
					buffer.append(line);
				}
				return buffer.toString();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			closeIOStram(is);
			closeIOStram(br);
			conn.disconnect();
		}

		return null;

	}

	public static JSONObject sendGetHttpRequest(String urlString)
	{
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(urlString);
		CloseableHttpResponse response = null;
		String resp = null;
		try
		{
			response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			System.out.println(response.getAllHeaders());
			resp = EntityUtils.toString(entity, "utf-8");
			EntityUtils.consume(entity);
		} catch (ClientProtocolException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			closeIOStram(response);
		}

		return JSONObject.parseObject(resp);

	}

	public static String httpRequestUpload(String requestUrl, File file, String clientID)
	{
		StringBuffer buffer = new StringBuffer();
		HttpURLConnection httpUrlConn = null;

		DataInputStream dataInputStream = null;

		OutputStream outputStream = null;

		InputStream inputStream = null;

		InputStreamReader inputStreamReader = null;

		BufferedReader bufferedReader = null;
		try
		{
			httpUrlConn = createHttpURLConnection(requestUrl, POST, null, clientID);
			// 1.3设置边界
			String BOUNDARY = "----------" + System.currentTimeMillis();
			httpUrlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

			// 请求正文信息
			// 第一部分：
			// 2.将文件头输出到微信服务器
			StringBuilder sb = new StringBuilder();
			sb.append("--"); // 必须多两道线
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"media\";filelength=\"" + file.length() + "\";filename=\""
					+ file.getName() + "\"\r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");
			byte[] head = sb.toString().getBytes("utf-8");
			// 获得输出流
			outputStream = new DataOutputStream(httpUrlConn.getOutputStream());
			// 将表头写入输出流中：输出表头
			outputStream.write(head);

			// 3.将文件正文部分输出到微信服务器
			// 把文件以流文件的方式 写入到微信服务器中
			dataInputStream = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = dataInputStream.read(bufferOut)) != -1)
			{
				outputStream.write(bufferOut, 0, bytes);
			}
			// 4.将结尾部分输出到微信服务器
			byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
			outputStream.write(foot);
			outputStream.flush();

			// 5.将微信服务器返回的输入流转换成字符串
			inputStream = httpUrlConn.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null)
			{
				buffer.append(str);
			}

			bufferedReader.close();

		} catch (IOException e)
		{
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
		} finally
		{
			closeIOStram(dataInputStream);
			closeIOStram(outputStream);
			closeIOStram(inputStream);
			closeIOStram(inputStreamReader);
			closeIOStram(bufferedReader);
			httpUrlConn.disconnect();
		}
		return buffer.toString();
	}

	/**
	 * @param urlString   请求url地址
	 * @param method      http的请求方式方法GET/POST
	 * @param contentType http请求头文字内容(可携带边界以分号‘;’隔开)
	 * @return
	 * @throws IOException
	 */
	public static HttpURLConnection createHttpURLConnection(String urlString, String method, String contentType,
			String clientID) throws IOException
	{
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setReadTimeout(3000);
		conn.setReadTimeout(60000);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Connection", "keep-alive");
		if (contentType == null)
		{
			conn.setRequestProperty(CONTENT_TYPE, "application/json");
		} else
		{
			conn.setRequestProperty(CONTENT_TYPE, contentType);
		}
		if (clientID != null)
		{
			conn.setRequestProperty("clientID", clientID);
		}

		conn.setRequestProperty("Charsert", "UTF-8");

		return conn;
	}

	public static void closeIOStram(Closeable closeable)
	{
		try
		{
			if (closeable != null)
			{
				closeable.close();
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
