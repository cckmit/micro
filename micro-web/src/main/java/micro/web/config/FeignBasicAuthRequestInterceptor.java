package micro.web.config;

import java.util.Collection;
import java.util.Map;

import org.slf4j.MDC;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import micro.commons.annotation.ThreadSafe;
import micro.commons.enums.ThreadContextEnum;

/**
 * Feign请求拦截器
 * 
 * @author gewx
 */
@ThreadSafe
public class FeignBasicAuthRequestInterceptor implements RequestInterceptor {

	/**
	 * 日志链路追踪
	 **/
	private static final String X_B3_TRACEID = "X-B3-TraceId";

	/**
	 * 账号
	 **/
	private static final String ACC_NO = "accNo";

	/**
	 * 标记请求源
	 **/
	private static final String HTTP_REQUEST_SOURCE = "source";

	@SuppressWarnings("unchecked")
	@Override
	public void apply(RequestTemplate template) {
		Object val = ThreadContextEnum.REQ_HEADER.removeAndGetVal();
		if (val instanceof Map) {
			Map<String, Collection<String>> collections = (Map<String, Collection<String>>) val;
			template.headers(collections);
		}
		template.header(X_B3_TRACEID, MDC.get(X_B3_TRACEID));
		template.header(ACC_NO, MDC.get(ACC_NO));
		template.header(HTTP_REQUEST_SOURCE, "feign");
	}
}
