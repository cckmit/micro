package micro.commons.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * Eolinker API管理工具辅助工具类,主要生成VO对应的JSON
 * 
 * @author gewx
 **/
public final class EolinkerUtils {

	/**
	 * 构建文档
	 * 
	 * @author gewx
	 * @param t 构建对象
	 * @throws Exception
	 * @return JSON字符串
	 **/
	public static <T extends Serializable> String buildDocJson(T t) throws Exception {
		Map<String,Object> json = new LinkedHashMap<>();
		Field[] field = t.getClass().getDeclaredFields();
		for (int j = 0; j < field.length; j++) {
			field[j].setAccessible(true);
			json.put(field[j].getName(), StringUtils.EMPTY);
		}
		return JSONObject.toJSONString(json);
	}

	/**
	 * 构建文档
	 * 
	 * @author gewx
	 * @param t 构建对象
	 * @throws Exception
	 * @return Query字符串
	 **/
	public static <T extends Serializable> String buildDocQuery(T t) throws Exception {
		List<String> list = new ArrayList<>(128);
		Field[] field = t.getClass().getDeclaredFields();
		for (int j = 0; j < field.length; j++) {
			field[j].setAccessible(true);
			list.add(field[j].getName() + "=");
		}
		return list.stream().collect(Collectors.joining("&"));
	}

	/**
	 * 构建文档
	 * 
	 * @author gewx
	 * @param t 构建对象
	 * @throws Exception
	 * @return Query字符串
	 **/
	public static <T extends Map<String, Object>> String buildDocQuery(T t) throws Exception {
		List<String> list = new ArrayList<>(128);
		t.entrySet().stream().forEach(val -> {
			list.add(val.getKey() + "=");
		});
		return list.stream().collect(Collectors.joining("&"));
	}
}
