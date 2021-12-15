package micro.commons.util;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.util.Map;
import java.util.function.Supplier;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import micro.commons.exception.BusinessRuntimeException;
import micro.commons.page.PageParameter;
import micro.commons.page.Pages;

/**
 * 通用分页工具类,基于PageHelper
 * 
 * @author gewx
 **/
public final class PageHelperUtils {

	/**
	 * 通用分页方法
	 * 
	 * @author gewx
	 * @param parameter  分页参数
	 * @param pageResult 分页数据集
	 * @return 分页结果集
	 **/
	public static <T> Pages<T> limit(PageParameter parameter, Supplier<Page<T>> pageResult) {
		if (parameter.getStartpage() == 0 || parameter.getPagesize() == 0) {
			throw new BusinessRuntimeException("页码、页行必须大于0");
		}

		PageHelper.startPage(parameter.getStartpage(), parameter.getPagesize());

		Page<T> page = pageResult.get();
		Pages<T> pages = new Pages<T>();
		pages.setTotalPageNum(page.getPages());
		pages.setTotalNum(page.getTotal());
		pages.setPageNum(page.getPageNum());
		pages.setPageSize(page.getPageSize());
		pages.setPages(page.getResult());
		return pages;
	}

	/**
	 * 通用分页方法
	 * 
	 * @author gewx
	 * @param parameter  分页参数
	 * @param pageResult 分页数据集
	 * @param extraData  附属数据
	 * @return 分页结果集
	 **/
	public static <T> Pages<T> limit(PageParameter parameter, Supplier<Page<T>> pageResult,
			Supplier<Map<String, Object>> extraData) {
		Pages<T> pages = limit(parameter, pageResult);
		pages.setExtraData(extraData.get());
		return pages;
	}

	/**
	 * 判断查询数据是否存在
	 * 
	 * @author gewx
	 * @param pageResult 分页数据集
	 * @return boolean
	 **/
	public static <T> boolean isExists(Supplier<Page<T>> pageResult) {
		PageParameter parameter = new PageParameter();
		parameter.setStartpage(1);
		parameter.setPagesize(1);

		PageHelper.startPage(parameter.getStartpage(), parameter.getPagesize(), false);

		return isNotEmpty(pageResult.get());
	}
}
