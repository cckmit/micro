package micro.commons.support;

import java.util.function.Supplier;

import lombok.Getter;
import lombok.Setter;

/**
 * 内部类，保存lambda计算结果
 * 
 * @author gewx
 **/

@Setter
@Getter
public class InnerResult<T> {

	public static <T> InnerResult<T> withInitial(Supplier<T> supplier) {
		InnerResult<T> result = new InnerResult<>();
		result.val = supplier.get();
		return result;
	}

	private T val;
}
