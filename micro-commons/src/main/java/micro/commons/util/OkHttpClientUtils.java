package micro.commons.util;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.tuple.Pair;

/**
 * 基于okHttp3 工具类
 * 
 * @author gewx
 **/
public final class OkHttpClientUtils {

	/**
	 * 池化okHttp客户端组件,读取超时设置30秒
	 **/
	private static final OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(30, TimeUnit.SECONDS)
			.build();

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
	 * 发送get请求
	 * 
	 * @param url    请求地址
	 * @param header 请求头列表
	 * @return 响应值
	 * @throws IOException 抛出异常
	 */
	@SuppressWarnings("unchecked")
	public static String getJson(String url, Pair<String, String>... header) throws IOException {
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
	 * @author gewx
	 * @param url    请求地址
	 * @param json   请求数据
	 * @param header 请求头列表
	 * @throws IOException 抛出异常
	 * @return 响应值
	 **/
	@SuppressWarnings("unchecked")
	public static String postJson(String url, String json, Pair<String, String>... header) throws IOException {
		Request.Builder builder = new Request.Builder();
		for (Pair<String, String> pair : header) {
			builder = builder.addHeader(pair.getKey(), pair.getValue());
		}
		RequestBody body = RequestBody.create(JSON, json);
		Request request = builder.url(url).post(body).build();
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
}
