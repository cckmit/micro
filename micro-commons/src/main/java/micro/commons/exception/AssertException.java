package micro.commons.exception;

/**
 * 断言校验异常
 * 
 * @author gewx
 **/
public final class AssertException extends RuntimeException {

	private static final long serialVersionUID = 1501409425705427239L;

	public AssertException() {
		super();
	}

	public AssertException(String msg) {
		super(msg);
	}
}
