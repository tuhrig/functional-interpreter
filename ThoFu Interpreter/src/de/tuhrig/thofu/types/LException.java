package de.tuhrig.thofu.types;

/**
 * Represents an exception.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static final String PARANTHESIS_EXCEPTION = "[parenthesis exception] - ";

	/**
	 * Exception text for a missing initial parenthesis
	 */
	public static final String MISSING_INITIAL_OPENING = PARANTHESIS_EXCEPTION + "missing initial opening parenthesis: ";

	/**
	 * Exception text for a missing closing parenthesis at the end of the command
	 */
	public static final String MISSING_FINAL_CLOSING = PARANTHESIS_EXCEPTION + "missing final closing parenthesis: ";

	/**
	 * Exception text for a missing closing parenthesis in the command
	 */
	public static final String MISSING_CLOSING = PARANTHESIS_EXCEPTION + "missing closing parenthesis";

	/**
	 * Exception text for a missing parenthesis
	 */
	public static final String MISSING_OPENING_NEAR_INDEX = PARANTHESIS_EXCEPTION + "missing opening parenthesis near index ";

	/**
	 * @param message of the exception
	 */
	public LException(String message) {

		super(message);
	}
	
	/**
	 * @param message of the exception
	 * @param parent of the exception
	 */
	public LException(String message, Exception parent) {

		super(message, parent);
	}
}