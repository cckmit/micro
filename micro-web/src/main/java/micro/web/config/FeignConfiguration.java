package micro.web.config;

import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.FeignException;
import feign.Logger;
import feign.Response;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import micro.commons.enums.ThreadContextEnum;

/**
 * Feign配置
 * 
 * @author gewx
 **/
@Configuration
public class FeignConfiguration {

	@Autowired
	private ObjectFactory<HttpMessageConverters> messageConverters;

	/**
	 * Feign组件日志输出等级
	 * 
	 * @author gewx
	 **/
	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}

	/**
	 * 重写Fegin响应解码器
	 * 
	 * @author gewx
	 **/
	@Bean
	public Decoder feignDecoder() {
		return new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(this.messageConverters) {
			@Override
			public Object decode(Response response, Type type) throws IOException, FeignException {
				ThreadContextEnum.RESP_HEADER.setVal(response.headers());
				return super.decode(response, type);
			}
		}));
	}

	/**
	 * Feign拦截器
	 * 
	 * @author gewx
	 **/
	@Bean
	public FeignBasicAuthRequestInterceptor basicAuthRequestInterceptor() {
		return new FeignBasicAuthRequestInterceptor();
	}

	/**
	 * traceId 过滤器
	 * 
	 * @author gewx
	 **/
	@Bean
	public TraceFilter traceFilter() {
		return new TraceFilter();
	}

}
