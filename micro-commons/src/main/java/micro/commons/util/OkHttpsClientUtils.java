package micro.commons.util;

import okhttp3.*;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import micro.commons.exception.BusinessRuntimeException;

/**
 * 基于okHttp3 工具类
 * 
 * @author gewx
 **/
public final class OkHttpsClientUtils {

	/**
	 * 池化okHttp客户端组件,读取超时设置30秒
	 **/
	private static final OkHttpClient client = initSSLClient();

	public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

	public static final MediaType X_WWW_FORM_URLENCODED = MediaType
			.get("application/x-www-form-urlencoded; charset=utf-8");

	/**
	 * 发送GET请求
	 * 
	 * @author gewx
	 * @param url 请求地址
	 * @throws IOException 抛出异常
	 * @return 响应值
	 **/
	public static String get(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}

	/**
	 * 发送POST请求
	 * 
	 * @author gewx
	 * @param url  请求地址
	 * @param json 请求数据
	 * @throws IOException 抛出异常
	 * @return 响应值
	 **/
	public static String postJson(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder().url(url).post(body).build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}

	/**
	 * 发送POST请求
	 * 
	 * @author gewx
	 * @param url     请求地址
	 * @param reqBody 请求数据
	 * @throws IOException 抛出异常
	 * @return 响应值
	 **/
	public static String post(String url, String reqBody) throws IOException {
		RequestBody body = RequestBody.create(X_WWW_FORM_URLENCODED, reqBody);
		Request request = new Request.Builder().url(url).post(body).build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}

	// 构建https连接
	private static OkHttpClient initSSLClient() {
		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			
			// 使用 X509TrustManager 初始化 SSLContext
			X509TrustManagerVerify x509TrustManagerVerify = new X509TrustManagerVerify();
			sslContext.init(null, new X509TrustManagerVerify[] { x509TrustManagerVerify }, null);
			
			// 使用 SSLContext 创建 SSLSocketFactory
			SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			
			OkHttpClient client = new OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, x509TrustManagerVerify)
					.readTimeout(30, TimeUnit.SECONDS).build();
			return client;
		} catch (Exception e) {
			throw new BusinessRuntimeException("OkHttp构建异常，ex: " + e.getMessage());
		}
	}
	
	// 自定义使用X509TrustManager信任所有证书
	private static class X509TrustManagerVerify implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	}
	
}
