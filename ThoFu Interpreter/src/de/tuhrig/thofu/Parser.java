package de.tuhrig.thofu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.tuhrig.thofu.types.LBoolean;
import de.tuhrig.thofu.types.LException;
import de.tuhrig.thofu.types.LList;
import de.tuhrig.thofu.types.LNull;
import de.tuhrig.thofu.types.LNumber;
import de.tuhrig.thofu.types.LObject;
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
public class Parser {

	private static Logger logger = Logger.getLogger(Parser.class);

	static final Pattern SINGLE_LINE_COMMENT = Pattern.compile("(;+)(.*)(\\n+)");

	static final Pattern MULTI_LINE_COMMENT = Pattern.compile("(#\\|+)(.*)(\\|#+)", Pattern.MULTILINE | Pattern.DOTALL);

	private static final Pattern SPLIT = Pattern.compile("[^\\s\"]+|\"([^\"]*)\"");

	private static final String nl = "\n";
	
	private int i;

	/**
	 * @param file to read
	 * @return content of the file
	 * @throws IOException if the file can't be read
	 */
	public String read(File file) throws IOException {
		
		List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

		StringBuilder content = new StringBuilder();
		
		for (String line : lines) {
			
			content.append(line);
			content.append(nl);
		}
		
		return content.toString();
	}
	
	/**
	 * @param clazz to use for resource loading
	 * @param resource name to load
	 * @return the content of the resource as a String
	 */
	public String read(Class<?> clazz, String resource) {

		InputStream stream = clazz.getResourceAsStream(resource);
		
		Scanner scanner = new Scanner(stream);
	 
		StringBuilder content = new StringBuilder();
		
		while(scanner.hasNext()) {
			
			content.append(scanner.nextLine());
			content.append(nl);
		}
		
		scanner.close();
		
		return content.toString();
	}

	/**
	 * @param expression to parse (e.g. (+ 1 2 3))
	 * @return a list of parsed objects
	 */
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

				matchList.add("\"" + regexMatcher.group(1) + "\"");
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
			Object next = new String("");

			if (tokens.size() > i + 1)
				next = tokens.get(i + 1);

			if (current.equals("'") && next.equals("(")) {

				LTupel tmp = new LTupel();

				if (list == null)
					list = tmp;
				else
					list.add(tmp);

				i++; // increment 2 times because we skip
				i++; // two chars '(

				parse(tmp, tokens);
			}
			else if (current.equals("(")) {

				LList tmp = new LList();

				if (list == null)
					list = tmp;
				else
					list.add((LObject) tmp);

				i++;

				parse(tmp, tokens);
			}
			else if (current.equals(")")) {

				return list;
			}
			else if (current.toString().startsWith("'")) {

				String name = current.toString().replace("'", "");

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

	private LObject getType(String current) {

		try {

			return new LNumber(current.toString());
		}
		catch (NumberFormatException e) {

			if (current.equals("true")) {

				return LBoolean.TRUE;
			}
			else if (current.equals("false")) {

				return LBoolean.FALSE;
			}
			else if (current.equals("null")) {

				return LNull.NULL;
			}
			else if (current.startsWith("\"") && current.endsWith("\"")) {

				return new LString(current);
			}
			else {

				return LSymbol.get(current);
			}
		}
	}

	/**
	 * This method removes single and multi-line comments as well
	 * as line breaks. It also inserts a space between every token,
	 * e.g. between ")(" to ") (".
	 * 
	 * @param expression to format
	 * @return a formated string without comments in one line
	 */
	public String format(String expression) {

		expression = SINGLE_LINE_COMMENT.matcher(expression).replaceAll("");
		expression = MULTI_LINE_COMMENT.matcher(expression).replaceAll("");
		expression = expression.replace("\n", " ");
		expression = expression.replace("(", " ( ");
		expression = expression.replace(")", " ) ");

		return expression;
	}

	/**
	 * This method parses a string with more then one command.
	 * Each command will be parsed separately. The returned list
	 * will contained the parsed commands in the same order.
	 * 
	 * @param commands to parse
	 * @return a list of parsed objects
	 */
	public List<LObject> parseAll(String commands) {

		List<LObject> objects = new ArrayList<>();
		
		int count = 0;

		char[] chars = commands.trim().toCharArray();

		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < chars.length; i++) {

			char currentChar = chars[i];

			if ("'".equals(String.valueOf(currentChar)) && i == 0)
				continue;

			if ("(".equals(String.valueOf(currentChar)))
				count++;

			if (")".equals(String.valueOf(currentChar)))
				count--;

			buffer.append(currentChar);

			if (count == 0) {

				String command = buffer.toString().trim();

				if (!command.trim().equals("")) {

					LList tmp = parse(command);
					
					objects.add(tmp);
				}

				buffer = new StringBuffer();
			}
		}
		
		return objects;
	}
	
	/**
	 * This method validated an expression due to its parenthesis. If
	 * a parenthesis is wrong (missing, too much, wrong place) it will
	 * throw a LException.
	 * 
	 * @param expression to validate
	 */
	public void validate(final String expression) {

		if (!expression.trim().startsWith("(") && !expression.trim().startsWith("'("))
			throw new LException(LException.MISSING_INITIAL_OPENING + expression);

		if (!expression.trim().endsWith(")"))
			throw new LException(LException.MISSING_FINAL_CLOSING + expression);

		int count = 0;

		char[] chars = expression.toCharArray();

		for (int i = 0; i < chars.length; i++) {

			char currentChar = chars[i];

			if ("'".equals(String.valueOf(currentChar)) && i == 0)
				continue;

			if ("(".equals(String.valueOf(currentChar)))
				count++;

			if (")".equals(String.valueOf(currentChar)))
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

		if(tokens instanceof LSymbol) {
			
			if(environment.contains((LSymbol) tokens))
				return tokens.run(environment, null);
		}
		else if(tokens instanceof LList && !(tokens instanceof LTupel)) {
			
			LList tmp = (LList) tokens;
			
			LObject first = replace(tmp.getFirst(), environment);
			
			tmp.set(0, first);
			
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