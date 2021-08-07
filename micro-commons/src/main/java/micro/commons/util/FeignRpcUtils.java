package micro.commons.util;

import static micro.commons.util.StringUtil.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.type.TypeReference;
import micro.commons.annotation.ThreadSafe;
import micro.commons.enums.ThreadContextEnum;
import micro.commons.exception.BusinessRuntimeException;
import micro.commons.log.MicroLogger;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Feign RPC调用工具类
 *
 * @author gewx
 **/
@ThreadSafe
public final class FeignRpcUtils {

	/**
	 * 日志组件
	 */
	private static final MicroLogger LOGGER = new MicroLogger(FeignRpcUtils.class);

	/**
	 * token
	 **/
	private static final String TOKEN = "token";

	/**
	 * success
	 **/
	private static final String SUCCESS = "0000";

	/**
	 * 响应消息体枚举
	 **/
	@Getter
	enum Response {

		SUCCESS("success", "服务调用成功/失败"),

		RESP_CODE("code", "响应码"),

		RESP_MSG("msg", "响应消息"),

		RESP_DATA("data", "响应数据");

		Response(String code, String comment) {
			this.code = code;
			this.comment = comment;
		}

		/**
		 * 编码
		 **/
		private String code;

		/**
		 * 注释
		 **/
		private String comment;
	}

	/**
	 * 解析响应数据
	 *
	 * @author gewx
	 * @param responseMap 响应结果集
	 * @return 解析后的数据
	 **/
	@SuppressWarnings("unchecked")
	public static Result getResult(Map<String, Object> respMap) {
		Boolean success = Boolean.valueOf(getString(respMap.get(Response.SUCCESS.getCode())));
		String code = getString(respMap.get(Response.RESP_CODE.getCode()));
		String msg = getString(respMap.get(Response.RESP_MSG.getCode()));
		Object data = respMap.get(Response.RESP_DATA.getCode());
		StringBuilder token = new StringBuilder();
		Object headers = ThreadContextEnum.RESP_HEADER.removeAndGetVal();
		if (headers instanceof Map) {
			Map<String, Collection<String>> headerMap = (Map<String, Collection<String>>) headers;
			Collection<String> list = headerMap.get(TOKEN);
			token.append(CollectionUtils.isNotEmpty(list) ? list.stream().findFirst().get() : StringUtils.EMPTY);
		}
		Result result = new Result(success, code, msg, data, token.toString());
		return result;
	}

	/**
	 * 获取RPC结果集
	 * 
	 * @author liangd
	 * @param methodName    方法名
	 * @param map           RPC响应结果
	 * @param typeReference 转换
	 * @return 响应结果集
	 **/
	public static <T> T handleRpcResult(String methodName, Map<String, Object> map, TypeReference<T> typeReference) {
		LOGGER.enter(methodName, StringUtils.EMPTY);
		FeignRpcUtils.Result rpcResult = FeignRpcUtils.getResult(map);
		LOGGER.info(methodName, "RPC调用响应, rpcResult: " + rpcResult);
		if (rpcResult.isAllSuccess()) {
			Object data = rpcResult.getData();
			if (null != data) {
				String rpcResultDataJson = JSONUtils.NON_NULL.toJSONString(data);
				T t = JSONUtils.NON_NULL.toJavaObject(rpcResultDataJson, typeReference);
				LOGGER.info(methodName, "RPC调用响应, rpcData: " + t);
				LOGGER.exit(methodName, StringUtils.EMPTY);
				return t;
			}
			return null;
		} else {
			throw new BusinessRuntimeException("RPC调用失败或未查询到相关信息~");
		}
	}

	/**
	 * 获取RPC结果集
	 * 
	 * @author gewx
	 * @param execute       操作体
	 * @param typeReference 响应转换类型
	 * @return 解析后的数据
	 **/
	public static <T, R> T handleRpcResult(Supplier<FeignRpcUtils.Result> execute, TypeReference<T> typeReference) {
		FeignRpcUtils.Result rpcResult = execute.get();
		if (!rpcResult.isAllSuccess()) {
			throw new BusinessRuntimeException("RPC调用失败或未查询到相关信息~");
		}
		return JSONUtils.NON_NULL.toJavaObject(JSONUtils.NON_NULL.toJSONString(rpcResult.getData()), typeReference);
	}

	/**
	 * 获取RPC结果集
	 * 
	 * @author gewx
	 * @param execute  操作体
	 * @param function 响应转换类型
	 * @return 解析后的数据
	 **/
	public static <R> R handleRpcResult(Supplier<FeignRpcUtils.Result> execute,
			Function<FeignRpcUtils.Result, R> function) {
		FeignRpcUtils.Result rpcResult = execute.get();
		if (!rpcResult.isAllSuccess()) {
			throw new BusinessRuntimeException("RPC调用失败或未查询到相关信息~");
		}
		return function.apply(rpcResult);
	}

	/**
	 * 设置token
	 * 
	 * @author gewx
	 * @param token 认证token
	 * @return void
	 **/
	public static void setToken(String token) {
		Collection<String> collections = new ArrayList<>();
		collections.add(token);
		Map<String, Collection<String>> headers = new HashMap<>();
		headers.put(TOKEN, collections);
		ThreadContextEnum.REQ_HEADER.setVal(headers);
	}

	/**
	 * RPC响应结果集对象
	 **/
	@Getter
	@ToString
	public static class Result {

		@Getter(value = AccessLevel.NONE)
		private final Boolean success;

		private final String code;

		private final String msg;

		private final Object data;

		private final String token;

		public Result(Boolean success, String code, String msg, Object data, String token) {
			this.success = success;
			this.code = code;
			this.msg = msg;
			this.data = data;
			this.token = token;
		}

		public Boolean isSuccess() {
			return this.success;
		}

		/**
		 * 服务调用响应与业务响应同时成功
		 *
		 * @author gewx
		 **/
		public Boolean isAllSuccess() {
			return this.success && SUCCESS.equals(this.code);
		}
	}
}
