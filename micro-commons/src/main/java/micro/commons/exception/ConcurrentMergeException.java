package micro.commons.exception;

/**
 * 归并计算异常
 * 
 * @author gewx
 **/
public final class ConcurrentMergeException extends RuntimeException {

	private static final long serialVersionUID = -86247454114144939L;

	public ConcurrentMergeException() {

	}

	public ConcurrentMergeException(String message) {
		super(message);
	}

	public ConcurrentMergeException(Throwable cause) {
		super(cause);
	}

	public ConcurrentMergeException(String message, Throwable cause) {
		super(message, cause);
	}
}
