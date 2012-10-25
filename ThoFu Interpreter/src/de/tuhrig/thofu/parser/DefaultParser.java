package de.tuhrig.thofu.parser;

import static de.tuhrig.thofu.Literal.BLANK;
import static de.tuhrig.thofu.Literal.DOUBLE_QUOTE;
import static de.tuhrig.thofu.Literal.EMPTY;
import static de.tuhrig.thofu.Literal.FALSE;
import static de.tuhrig.thofu.Literal.LEFT_PARENTHESIS;
import static de.tuhrig.thofu.Literal.LEFT_PARENTHESIS_BLANKED;
import static de.tuhrig.thofu.Literal.NL;
import static de.tuhrig.thofu.Literal.NULL;
import static de.tuhrig.thofu.Literal.QUOTED_LEFT_PARENTHESIS;
import static de.tuhrig.thofu.Literal.RIGHT_PARENTHESIS;
import static de.tuhrig.thofu.Literal.RIGHT_PARENTHESIS_BLANKED;
import static de.tuhrig.thofu.Literal.SINGLE_QUOTE;
import static de.tuhrig.thofu.Literal.TRUE;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.interfaces.Parser;
import de.tuhrig.thofu.types.LBoolean;
import de.tuhrig.thofu.types.LException;
import de.tuhrig.thofu.types.LLambda;
import de.tuhrig.thofu.types.LList;
import de.tuhrig.thofu.types.LNull;
import de.tuhrig.thofu.types.LNumber;
import de.tuhrig.thofu.types.LObject;
import de.tuhrig.thofu.types.LOperation;
import de.tuhrig.thofu.types.LQuoted;
import de.tuhrig.thofu.types.LString;
import de.tuhrig.thofu.types.LSymbol;
import de.tuhrig.thofu.types.LTupel;

/**
 * A Parser instance can be used to parse a command into
 * a list of tokens. A parser can also remove comments, 
 * read a file/resource or execute a list of several 
 * commands.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class DefaultParser implements Parser {

	private static Logger logger = Logger.getLogger(DefaultParser.class);

	static final Pattern SINGLE_LINE_COMMENT = Pattern.compile("(;+)(.*)(\\n+)");

	static final Pattern MULTI_LINE_COMMENT = Pattern.compile("(#\\|+)(.*)(\\|#+)", Pattern.MULTILINE | Pattern.DOTALL);

	static final Pattern SPLIT = Pattern.compile("[^\\s\"]+|\"([^\"]*)\"");
	
	private int i;

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.Parser#parse(java.lang.String)
	 */
	@Override
	public LList parse(String expression) {

		expression = format(expression);

		logger.debug("parse: " + expression);

		// assumption: the first token is a "(", so we start with the
		// second one. we have to set this back before every call.
		i = 0;

		List<String> matchList = new ArrayList<String>();

		Matcher regexMatcher = SPLIT.matcher(expression);

		while (regexMatcher.find()) {

			if (regexMatcher.group(1) != null) {

				matchList.add(DOUBLE_QUOTE + regexMatcher.group(1) + DOUBLE_QUOTE);
			}
			else {

				matchList.add(regexMatcher.group());
			}
		}

		return parse(null, matchList);
	}

	private LList parse(LList list, List<String> tokens) {

		for (; i < tokens.size(); i++) {

			Object current = tokens.get(i);
			Object next = new String(EMPTY);

			if (tokens.size() > i + 1)
				next = tokens.get(i + 1);

			if (current.equals(SINGLE_QUOTE) && next.equals(LEFT_PARENTHESIS)) {

				LTupel tmp = new LTupel();

				if (list == null)
					list = tmp;
				else
					list.add(tmp);

				i++; // increment 2 times because we skip
				i++; // two chars '(

				parse(tmp, tokens);
			}
			else if (current.equals(LEFT_PARENTHESIS)) {

				LList tmp = new LList();

				if (list == null)
					list = tmp;
				else
					list.add((LObject) tmp);

				i++;

				parse(tmp, tokens);
			}
			else if (current.equals(RIGHT_PARENTHESIS)) {

				return list;
			}
			else if (current.toString().startsWith(SINGLE_QUOTE)) {

				String name = current.toString().replace(SINGLE_QUOTE, EMPTY);

				LSymbol symbol = LSymbol.get(name);

				list.add(new LQuoted(symbol));
			}
			else {

				LObject typed = getType(current.toString());

				list.add(typed);
			}
		}

		return list;
	}

	public LObject getType(Object token) {

		try {

			return new LNumber(token.toString());
		}
		catch (NumberFormatException e) {

			if (token.equals(TRUE)) {

				return LBoolean.TRUE;
			}
			else if (token.equals(FALSE)) {

				return LBoolean.FALSE;
			}
			else if (token.equals(NULL)) {

				return LNull.NULL;
			}
			else if (token.toString().startsWith(DOUBLE_QUOTE) && token.toString().endsWith(DOUBLE_QUOTE)) {
 
				return new LString(token);
			}
			else {

				return LSymbol.get(token);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.Parser#format(java.lang.String)
	 */
	@Override
	public String format(String expression) {

		expression = SINGLE_LINE_COMMENT.matcher(expression).replaceAll(EMPTY);
		expression = MULTI_LINE_COMMENT.matcher(expression).replaceAll(EMPTY);
		expression = expression.replace(NL, BLANK);
		expression = expression.replace(LEFT_PARENTHESIS, LEFT_PARENTHESIS_BLANKED);
		expression = expression.replace(RIGHT_PARENTHESIS, RIGHT_PARENTHESIS_BLANKED);

		return expression.trim();
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.Parser#parseAll(java.lang.String)
	 */
	@Override
	public List<LObject> parseAll(String commands) {

		List<LObject> objects = new ArrayList<>();
		
		int count = 0;

		char[] chars = commands.trim().toCharArray();

		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < chars.length; i++) {

			char currentChar = chars[i];

			if (SINGLE_QUOTE.equals(String.valueOf(currentChar)) && i == 0)
				continue;

			if (LEFT_PARENTHESIS.equals(String.valueOf(currentChar)))
				count++;

			if (RIGHT_PARENTHESIS.equals(String.valueOf(currentChar)))
				count--;

			buffer.append(currentChar);

			if (count == 0) {

				String command = buffer.toString().trim();

				if (!command.trim().equals(EMPTY)) {
					
					LList tmp = parse(command);
					
					objects.add(tmp);
				}

				buffer = new StringBuffer();
			}
		}
		
		return objects;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.Parser#validate(java.lang.String)
	 */
	@Override
	public void validate(final String expression) {

		if (!expression.trim().startsWith(LEFT_PARENTHESIS) && !expression.trim().startsWith(QUOTED_LEFT_PARENTHESIS))
			throw new LException(LException.MISSING_INITIAL_OPENING + expression);

		if (!expression.trim().endsWith(RIGHT_PARENTHESIS))
			throw new LException(LException.MISSING_FINAL_CLOSING + expression);

		int count = 0;

		char[] chars = expression.toCharArray();

		for (int i = 0; i < chars.length; i++) {

			char currentChar = chars[i];

			if (SINGLE_QUOTE.equals(String.valueOf(currentChar)) && i == 0)
				continue;

			if (LEFT_PARENTHESIS.equals(String.valueOf(currentChar)))
				count++;

			if (RIGHT_PARENTHESIS.equals(String.valueOf(currentChar)))
				count--;

			if (i == chars.length - 1 && count > 0)
				throw new LException(LException.MISSING_CLOSING);

			if (i != chars.length - 1 && count <= 0)
				throw new LException(LException.MISSING_OPENING_NEAR_INDEX + i);
		}
	}

	/**
	 * This method is experimental and not used in the interpreter itself.
	 * It can take a LObject and an environment and look for replacements 
	 * for the object in this environment. E.g. it could replace a variable
	 * called "a" with "3" if such a mapping was found in the environment. 
	 * This could improve the performance of the interpreter, but causes 
	 * various errors currently. Therefore the method is not used. 
	 * 
	 * @param tokens to replace
	 * @param environment to search for replacements
	 * @return a replacement for the object or the object itself if no replacement was found
	 */
	public LObject replace(LObject tokens, Environment environment) {

		// if the token is a symbol, we resolve it
		if(tokens instanceof LSymbol) {
			
			if(environment.contains((LSymbol) tokens))
				return tokens.run(environment, null);
		}
		else if(tokens instanceof LList && !(tokens instanceof LTupel)) {
			
			LList tmp = (LList) tokens;
			
			LObject first = replace(tmp.getFirst(), environment);
			
			// only replace in-built operations
			if(first instanceof LOperation && !(first instanceof LLambda)) {
				
				tmp.set(0, first);
			}
			
			ListIterator<LObject> iterator = tmp.listIterator();
			
			while(iterator.hasNext()) {
				
				LObject token = iterator.next();
				
				if(token instanceof LList)
					token = replace(token, environment);
				
				iterator.set(token);
			}
		}
		
		return tokens;
	}
}