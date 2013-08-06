package de.tuhrig.thofu.interfaces;

import java.util.List;
import java.util.regex.Pattern;

import de.tuhrig.thofu.types.LList;
import de.tuhrig.thofu.types.LObject;

public interface Parser {

	static final Pattern SINGLE_LINE_COMMENT = Pattern.compile("(;+)(.*)(\\n+)");

	static final Pattern MULTI_LINE_COMMENT = Pattern.compile("(#\\|+)(.*)(\\|#+)", Pattern.MULTILINE | Pattern.DOTALL);

	/**
	 * @param expression to parse (e.g. (+ 1 2 3))
	 * @return a list of parsed objects
	 */
	public LList parse(String expression);

	/**
	 * This method removes single and multi-line comments as well
	 * as line breaks. It also inserts a space between every token,
	 * e.g. between ")(" to ") (".
	 * 
	 * @param expression to format
	 * @return a formated string without comments in one line
	 */
	public String format(String expression);

	/**
	 * This method parses a string with more then one command.
	 * Each command will be parsed separately. The returned list
	 * will contained the parsed commands in the same order.
	 * 
	 * @param commands to parse
	 * @return a list of parsed objects
	 */
	public List<LObject> parseAll(String commands);
	
	/**
	 * This method validated an expression due to its parenthesis. If
	 * a parenthesis is wrong (missing, too much, wrong place) it will
	 * throw a LException.
	 * 
	 * @param expression to validate
	 */
	public void validate(final String expression);
}
