package de.tuhrig.fujas.types;

public class LException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static final String PARANTHESIS_EXCEPTION = "[parenthesis exception] - ";

	public static final String MISSING_INITIAL_OPENING = PARANTHESIS_EXCEPTION + "missing initial opening parenthesis: ";

	public static final String MISSING_FINAL_CLOSING = PARANTHESIS_EXCEPTION + "missing final closing parenthesis: ";

	public static final String MISSING_CLOSING = PARANTHESIS_EXCEPTION + "missing closing parenthesis";

	public static final String MISSING_OPENING_NEAR_INDEX = PARANTHESIS_EXCEPTION + "missing opening parenthesis near index ";

	public LException(String message) {

		super(message);
	}
}