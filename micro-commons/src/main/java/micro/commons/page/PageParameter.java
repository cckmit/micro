package micro.commons.page;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 分页参数
 **/
@Setter
@Getter
@ToString
public class PageParameter implements Serializable {

	private static final long serialVersionUID = -7696440725170038746L;

	/**
	 * 页码
	 **/
	private int startpage;

	/**
	 * 页行
	 **/
	private int pagesize;
}
