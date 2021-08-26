package micro.commons.util;

import org.apache.commons.lang3.StringUtils;

import micro.commons.annotation.ThreadSafe;
import micro.commons.exception.NumberConvertException;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 字符串操作辅助类
 * 
 * @author gewx
 **/
@ThreadSafe
public final class StringUtil {

	/**
	 * 获取对象的字符串
	 *
	 * @param obj 需要转换的对象
	 * @return string
	 * @author gewx
	 **/
	public static String getString(Object obj) {
		if (obj == null) {
			return StringUtils.EMPTY;
		} else {
			return obj.toString();
		}
	}

	/**
	 * 获取Int数值
	 *
	 * @param obj 需要转换的对象
	 * @return Integer
	 * @throws IllegalAccessException
	 * @author gewx
	 **/
	public static Integer getInt(Object obj) {
		String val = getString(obj);
		if (NumberUtils.isCreatable(val)) {
			return Integer.parseInt(val);
		} else {
			throw new NumberConvertException("转换数值类型失败~");
		}
	}

	/**
	 * 获取Int数值
	 *
	 * @param obj        需要转换的对象
	 * @param defaultInt 当对象为null时的默认值
	 * @return Integer
	 * @throws IllegalAccessException
	 * @author luyy
	 **/
	public static Integer getInt(Object obj, Integer defaultInt) {
		if (obj == null) {
			return defaultInt;
		} else {
			String val = getString(obj);
			if (NumberUtils.isCreatable(val)) {
				return Integer.parseInt(val);
			} else {
				throw new NumberConvertException("转换数值类型失败~");
			}
		}
	}

	/**
	 * 获取Long数值
	 *
	 * @param obj 需要转换的对象
	 * @return Long
	 * @throws IllegalAccessException
	 * @author gewx
	 **/
	public static Long getLong(Object obj) {
		String val = getString(obj);
		if (NumberUtils.isCreatable(val)) {
			return Long.parseLong(val);
		} else {
			throw new NumberConvertException("转换数值类型失败~");
		}
	}

	/**
	 * 获取Long数值
	 *
	 * @param obj        需要转换的对象
	 * @param defaultInt 当对象为null时的默认值
	 * @return Long
	 * @throws IllegalAccessException
	 * @author luyy
	 **/
	public static Long getLong(Object obj, Long defaultInt) {
		if (obj == null) {
			return defaultInt;
		} else {
			String val = getString(obj);
			if (NumberUtils.isCreatable(val)) {
				return Long.parseLong(val);
			} else {
				throw new NumberConvertException("转换数值类型失败~");
			}
		}
	}

	/**
	 * 异常栈字符串输出
	 *
	 * @param throwable 异常对象
	 * @return String
	 * @author gewx
	 **/
	public static String getErrorText(Throwable throwable) {
		if (throwable == null) {
			return "ERROR,throwable is NULL!";
		}

		try (StringWriter strWriter = new StringWriter(512); PrintWriter writer = new PrintWriter(strWriter)) {
			throwable.printStackTrace(writer);
			StringBuffer sb = strWriter.getBuffer();
			return sb.toString();
		} catch (Exception ex) {
			return "ERROR!";
		}
	}

	/**
	 * 首字符大写(基于ascii表)
	 * 
	 * @author gewx
	 * @param val 字符
	 * @return 大写字符串
	 **/
	public static String firstStrUpperCase(String val) {
		char[] cs = val.toCharArray();
		cs[0] -= 32;
		return String.valueOf(cs);
	}

	/**
	 * 首字符小写(基于ascii表)
	 * 
	 * @author gewx
	 * @param val 字符
	 * @return 大写字符串
	 **/
	public static String firstStrLowerCase(String val) {
		char[] cs = val.toCharArray();
		cs[0] += 32;
		return String.valueOf(cs);
	}
}
