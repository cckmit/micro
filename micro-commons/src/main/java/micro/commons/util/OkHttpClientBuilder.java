package micro.commons.util;

import okhttp3.*;

import java.io.IOException;
import java.net.Proxy;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.tuple.Pair;

import lombok.Getter;

/**
 * 基于okHttp4 工具类
 *
 * @author gewx
 **/

public final class OkHttpClientBuilder {

	private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

	private final OkHttpClient client;

	private OkHttpClientBuilder(Builder builder) {
		OkHttpClient.Builder okBuilder = new OkHttpClient().newBuilder()
				.writeTimeout(builder.getWriteTimeout(), TimeUnit.SECONDS)
				.connectTimeout(builder.getConnectTimeout(), TimeUnit.SECONDS)
				.readTimeout(builder.getReadTimeout(), TimeUnit.SECONDS);
		if (builder.isSsl()) {
			try {
				SSLContext sslContext = SSLContext.getInstance("TLS");
				X509TrustManagerVerify x509TrustManagerVerify = new X509TrustManagerVerify();
				sslContext.init(null, new X509TrustManagerVerify[] { x509TrustManagerVerify }, null);
				SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
				okBuilder = okBuilder.sslSocketFactory(sslSocketFactory, x509TrustManagerVerify);
			} catch (Exception e) {
				throw new RuntimeException("OkHttp构建异常，ex: " + e.getMessage());
			}
		}

		if (builder.getProxy() != null) {
			okBuilder = okBuilder.proxy(builder.getProxy());
			if (builder.getAuthenticator() != null) {
				okBuilder = okBuilder.authenticator(builder.getAuthenticator());
			}
		}

		client = okBuilder.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * 发送GET请求
	 *
	 * @param url    请求地址
	 * @param header 请求头列表
	 * @return 响应值
	 * @throws IOException 抛出异常
	 * @author gewx
	 **/
	@SuppressWarnings("unchecked")
	public String get(String url, Pair<String, String>... header) throws IOException {
		Request.Builder builder = new Request.Builder();
		for (Pair<String, String> pair : header) {
			builder = builder.addHeader(pair.getKey(), pair.getValue());
		}
		Request request = builder.url(url).build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}

	/**
	 * 发送POST请求
	 *
	 * @param url     请求地址
	 * @param reqBody 请求数据
	 * @return 响应值
	 * @throws IOException 抛出异常
	 * @author gewx
	 **/
	@SuppressWarnings("unchecked")
	public String post(String url, Pair<String, String>... reqBody) throws IOException {
		return post(url, Arrays.asList(reqBody));
	}

	/**
	 * 发送POST请求
	 *
	 * @param url     请求地址
	 * @param reqBody 请求数据
	 * @return 响应值
	 * @throws IOException 抛出异常
	 * @author gewx
	 **/
	@SuppressWarnings("unchecked")
	public String post(String url, List<Pair<String, String>> reqBody, Pair<String, String>... header)
			throws IOException {
		FormBody.Builder formBuilder = new FormBody.Builder();
		for (Pair<String, String> pair : reqBody) {
			formBuilder.add(pair.getKey(), pair.getValue());
		}
		Request.Builder builder = new Request.Builder();
		for (Pair<String, String> pair : header) {
			builder = builder.addHeader(pair.getKey(), pair.getValue());
		}
		Request request = builder.url(url).post(formBuilder.build()).build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}

	/**
	 * 发送POST请求
	 *
	 * @param url    请求地址
	 * @param json   请求数据
	 * @param header 请求头列表
	 * @return 响应值
	 * @throws IOException 抛出异常
	 * @author gewx
	 **/
	@SuppressWarnings("unchecked")
	public String postJson(String url, String json, Pair<String, String>... header) throws IOException {
		Request.Builder builder = new Request.Builder();
		for (Pair<String, String> pair : header) {
			builder = builder.addHeader(pair.getKey(), pair.getValue());
		}
		RequestBody body = RequestBody.create(json, JSON);
		Request request = builder.url(url).post(body).build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}

	/**
	 * 发送HEAD请求
	 *
	 * @param url    请求地址
	 * @param header 请求头列表
	 * @return 响应值
	 * @throws IOException 抛出异常
	 * @author gewx
	 **/
	@SuppressWarnings("unchecked")
	public Map<String, List<String>> head(String url, Pair<String, String>... header) throws IOException {
		Request.Builder builder = new Request.Builder();
		for (Pair<String, String> pair : header) {
			builder = builder.addHeader(pair.getKey(), pair.getValue());
		}
		Request request = builder.url(url).build();
		try (Response response = client.newCall(request).execute()) {
			Headers headers = response.headers();
			return headers.toMultimap();
		}
	}

	/**
	 * 发送PUT请求
	 *
	 * @param url     请求地址
	 * @param reqBody 请求数据
	 * @return 响应值
	 * @throws IOException 抛出异常
	 * @author gewx
	 **/
	@SuppressWarnings("unchecked")
	public String put(String url, Pair<String, String>... reqBody) throws IOException {
		FormBody.Builder formBuilder = new FormBody.Builder();
		for (Pair<String, String> pair : reqBody) {
			formBuilder.add(pair.getKey(), pair.getValue());
		}
		Request request = new Request.Builder().url(url).put(formBuilder.build()).build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}

	/**
	 * 发送PUT请求
	 *
	 * @param url    请求地址
	 * @param json   请求数据
	 * @param header 请求头列表
	 * @return 响应值
	 * @throws IOException 抛出异常
	 * @author gewx
	 **/
	@SuppressWarnings("unchecked")
	public String putJson(String url, String json, Pair<String, String>... header) throws IOException {
		Request.Builder builder = new Request.Builder();
		for (Pair<String, String> pair : header) {
			builder = builder.addHeader(pair.getKey(), pair.getValue());
		}
		RequestBody body = RequestBody.create(json, JSON);
		Request request = builder.url(url).put(body).build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}

	@Getter
	public static class Builder {

		private int writeTimeout;

		private int connectTimeout;

		private int readTimeout;

		private boolean ssl;

		private Proxy proxy;

		private Authenticator authenticator;

		public Builder() {
			writeTimeout = 15;
			connectTimeout = 15;
			readTimeout = 15;
			ssl = false;
		}

		public Builder writeTimeout(int writeTimeout) {
			this.writeTimeout = writeTimeout;
			return this;
		}

		public Builder connectTimeout(int connectTimeout) {
			this.connectTimeout = connectTimeout;
			return this;
		}

		public Builder readTimeout(int readTimeout) {
			this.readTimeout = readTimeout;
			return this;
		}

		public Builder ssl(boolean ssl) {
			this.ssl = ssl;
			return this;
		}

		public Builder proxy(Proxy proxy) {
			this.proxy = proxy;
			return this;
		}

		public Builder authenticator(Authenticator authenticator) {
			this.authenticator = authenticator;
			return this;
		}

		public OkHttpClientBuilder build() {
			return new OkHttpClientBuilder(this);
		}
	}

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
