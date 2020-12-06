package com.yuweix.assist4j.sequence.exception;


/**
 * @author yuwei
 */
public class SequenceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SequenceException() {

	}

	public SequenceException(String message) {
		super(message);
	}

	public SequenceException(String message, Throwable cause) {
		super(message, cause);
	}

	public SequenceException(Throwable cause) {
		super(cause);
	}
}
