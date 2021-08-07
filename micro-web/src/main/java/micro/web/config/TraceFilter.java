package micro.web.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 散杂货云平台自定义过滤器,支持traceId、accNo追踪
 * 
 * @author gewx
 **/
public class TraceFilter extends OncePerRequestFilter {

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

	/**
	 * 请求源数值
	 **/
	private static final String HTTP_REQUEST_SOURCE_VALUE = "feign";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// Fix: 如果过滤器拦截的http请求是内部feign调用,则账号从请求头里获取否则不予处理
		String source = request.getHeader(HTTP_REQUEST_SOURCE);
		if (HTTP_REQUEST_SOURCE_VALUE.equals(source)) {
			MDC.put(ACC_NO, request.getHeader(ACC_NO));
			MDC.put(X_B3_TRACEID, request.getHeader(X_B3_TRACEID));
		}
		try {
			filterChain.doFilter(request, response);
		} finally {
			MDC.remove(ACC_NO);
			MDC.remove(X_B3_TRACEID);
		}
	}
}
