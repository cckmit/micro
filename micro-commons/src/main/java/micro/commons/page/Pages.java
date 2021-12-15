package micro.commons.page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 分页数据结构
 * 
 * @author gewx
 **/

@Setter
@Getter
@ToString
public final class Pages<T> implements Serializable {

	private static final long serialVersionUID = 6677852537002542077L;

	/**
	 * 总行数
	 **/
	private long totalNum;

	/**
	 * 总页数
	 **/
	private int totalPageNum;

	/**
	 * 页行
	 **/
	private int pageSize;

	/**
	 * 页码
	 **/
	private int pageNum;

	/**
	 * 当前页数据集
	 **/
	private List<T> pages;

	/**
	 * 额外数据(譬如：统计汇总等)
	 **/
	private Map<String, Object> extraData;
}
