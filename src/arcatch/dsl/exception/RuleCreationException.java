package arcatch.dsl.exception;

public class RuleCreationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RuleCreationException() {
		super();
	}

	public RuleCreationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RuleCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public RuleCreationException(String message) {
		super(message);
	}

	public RuleCreationException(Throwable cause) {
		super(cause);
	}

}
