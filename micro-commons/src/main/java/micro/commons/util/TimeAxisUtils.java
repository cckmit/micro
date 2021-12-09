package micro.commons.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import cn.hutool.core.annotation.AnnotationUtil;
import micro.commons.annotation.Comment;
import micro.commons.annotation.ThreadSafe;

/**
 * 时间轴文本工具类
 * 
 * @author gewx
 **/

@ThreadSafe
public final class TimeAxisUtils {

	private static final DateTimeFormatter FORMAT = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");

	/**
	 * 创建时间轴对象
	 * 
	 * @author gewx
	 * @param axisDto 数据dto
	 * @throws IllegalAccessException
	 * @return 时间轴文本
	 **/
	@SuppressWarnings("rawtypes")
	public static String create(Serializable axisDto) throws IllegalAccessException {
		List<Pair> commentList = new ArrayList<>(64);
		for (Field f : axisDto.getClass().getDeclaredFields()) {
			f.setAccessible(true);
			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}

			if (!f.isAnnotationPresent(Comment.class)) {
				continue;
			}

			Object val = FieldUtils.readDeclaredField(axisDto, f.getName(), true);
			if (val == null) {
				continue;
			}

			if (val instanceof BigDecimal) {
				BigDecimal newVal = (BigDecimal) val;
				String comment = AnnotationUtil.getAnnotationValue(f, Comment.class, "message");
				commentList.add(Pair.of(comment, newVal.toPlainString()));
			} else if (val instanceof Date) {
				Date newVal = (Date) val;
				String comment = AnnotationUtil.getAnnotationValue(f, Comment.class, "message");
				commentList.add(Pair.of(comment, new DateTime(newVal).toString(FORMAT)));
			} else if (val instanceof Integer) {
				Integer newVal = (Integer) val;
				String comment = AnnotationUtil.getAnnotationValue(f, Comment.class, "message");
				commentList.add(Pair.of(comment, newVal));
			} else {
				String comment = AnnotationUtil.getAnnotationValue(f, Comment.class, "message");
				commentList.add(Pair.of(comment, val.toString()));
			}
		}

		String axisText = commentList.stream().map(val -> {
			return val.getKey() + "改为：" + val.getValue();
		}).collect(Collectors.joining(","));

		return axisText;
	}
}
