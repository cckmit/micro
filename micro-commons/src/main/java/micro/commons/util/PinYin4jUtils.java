package micro.commons.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import org.apache.commons.lang3.StringUtils;

import micro.commons.annotation.ThreadSafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 拼音转换工具类
 * 
 * @author gewx
 **/
@ThreadSafe
public final class PinYin4jUtils {

	public static final String SPLIT_SYMBOL = " / ";

	private static final Pattern PATTERN_CN_ZH = Pattern.compile("[\\u4E00-\\u9FA5]+");

	private static final Pattern PATTERN_NUMBER = Pattern.compile("\\d");

	private static final Pattern PATTERN_BLANK = Pattern.compile("\\s*|\t|\r|\n");

	/**
	 * 中文转拼音数组
	 * 
	 * @author gewx
	 * @param hyVal 中文
	 * @return 拼音数值数组
	 **/
	public static String[] convertCnzhToPinYinArray(String hyVal) {
		if (StringUtils.isBlank(hyVal)) {
			return Collections.emptyList().toArray(new String[] {});
		}

		List<String> dataList = new ArrayList<>(16);
		char[] charArray = hyVal.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			String tempVal = Character.toString(charArray[i]);
			if (PATTERN_CN_ZH.matcher(tempVal).matches()) {
				String[] pinyingArray = PinyinHelper.toHanyuPinyinStringArray(charArray[i]);
				if (pinyingArray == null) {
					continue;
				}
				String val = pinyingArray[0];
				val = PATTERN_NUMBER.matcher(val).replaceAll(StringUtils.EMPTY);
				dataList.add(val);
			} else {
				dataList.add(tempVal);
			}
		}
		return dataList.toArray(new String[] {});
	}

	/**
	 * 中文转拼音首字符大写
	 * 
	 * @author gewx
	 * @param zh 中文
	 * @return 拼音数值
	 **/
	public static String convertCnzhToPinYinVal(String zh) {
		Matcher matcher = PATTERN_BLANK.matcher(zh);
		String[] valArray = convertCnzhToPinYinArray(matcher.replaceAll(StringUtils.EMPTY));
		return Stream.of(valArray).map(val -> {
			return Character.toString(val.charAt(0));
		}).collect(Collectors.joining());
	}

	/**
	 * 包装字符
	 * 
	 * @param zh 中文数据, splitSymbol 分隔符
	 * @return 包装后数据
	 **/
	public static String wrap(String zh, String splitSymbol) {
		String pyVal = convertCnzhToPinYinVal(zh);
		if (StringUtils.isNotBlank(pyVal)) {
			StringBuilder sb = new StringBuilder(32);
			sb.append(zh + splitSymbol + pyVal.toUpperCase());
			return sb.toString();
		} else {
			return zh;
		}
	}

	/**
	 * 判断字符串是否拼音
	 *
	 * @param string
	 * @return
	 */
	public static boolean isPinyin(String string) {
		if (StringUtils.isBlank(string)) {
			return false;
		}
		return string.toUpperCase().chars().noneMatch(val -> val < 65 || val > 90);
	}
}
