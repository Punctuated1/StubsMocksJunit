package myapp;

public class InputException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6596456044540753044L;

	public InputException() {
	}

	public InputException(String message) {
		super(message);
	}

	public InputException(Throwable cause) {
		super(cause);
	}

	public InputException(String message, Throwable cause) {
		super(message, cause);
	}

	public InputException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
