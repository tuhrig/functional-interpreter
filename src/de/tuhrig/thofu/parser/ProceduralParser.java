package de.tuhrig.thofu.parser;

import static de.tuhrig.thofu.Literal.BLANK;
import static de.tuhrig.thofu.Literal.DOUBLE_QUOTE;
import static de.tuhrig.thofu.Literal.FALSE;
import static de.tuhrig.thofu.Literal.LEFT_BRAKET;
import static de.tuhrig.thofu.Literal.LEFT_BRAKET_BLANKED;
import static de.tuhrig.thofu.Literal.LEFT_PARENTHESIS;
import static de.tuhrig.thofu.Literal.LEFT_PARENTHESIS_BLANKED;
import static de.tuhrig.thofu.Literal.NULL;
import static de.tuhrig.thofu.Literal.RIGHT_BRAKET;
import static de.tuhrig.thofu.Literal.RIGHT_BRAKET_BLANKED;
import static de.tuhrig.thofu.Literal.RIGHT_PARENTHESIS;
import static de.tuhrig.thofu.Literal.RIGHT_PARENTHESIS_BLANKED;
import static de.tuhrig.thofu.Literal.TRUE;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tuhrig.thofu.interfaces.Parser;
import de.tuhrig.thofu.java.LJava;
import de.tuhrig.thofu.types.LBoolean;
import de.tuhrig.thofu.types.LException;
import de.tuhrig.thofu.types.LList;
import de.tuhrig.thofu.types.LNull;
import de.tuhrig.thofu.types.LNumber;
import de.tuhrig.thofu.types.LObject;
import de.tuhrig.thofu.types.LString;
import de.tuhrig.thofu.types.LSymbol;
import de.tuhrig.thofu.types.LTupel;

/**
 * @author Thomas Uhrig (tuhrig.de)
 */
public class ProceduralParser implements Parser {
	
	private static final Pattern SPLIT = Pattern.compile("\n|[^\\s\"]+|\"([^\"]*)\"");

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.Parser#parse(java.lang.String)
	 */
	@Override
	public LList parse(String expression) {

		List<Object> tokens = toTokens(expression);

		LList result = parse(tokens);
		
		//System.out.println("result: " + result);
		
		return result;
	}
	
	private LList parse(List<Object> tokens) {

		//System.out.println("parse " + tokens);
		
		LList list = new LList();

		while(tokens.size() > 0) {
			
			Object token = tokens.remove(0);
			
			/*
			 * THOFU LANGUAGE
			 */
			
			if(token.equals(";")) {

				return strip(list);
			}
			else if(token.equals("[")) {
				
				return list(tokens);
			}
			else if(token.equals("var")) {
				
				return defination(tokens);
			}
			else if(token.equals("=")) {
				 
				return assignment(list, tokens);
			}
			else if(token.equals("for")) {

				return forLoop(tokens);
			}
			else if(token.equals("while")) {
				
				return whileLoop(tokens);
			}
			else if(token.equals("do")) {
				
				return doLoop(tokens);
			}
			else if(token.toString().startsWith("++")) {
				
				return crementBefore(list, token.toString(), "++", "+");
			}
			else if(token.toString().endsWith("++")) {
	
				return crementAfter(list, token.toString(), "++", "+");
			}
			else if(token.toString().startsWith("--")) {
				
				return crementBefore(list, token.toString(), "--", "-");
			}
			else if(token.toString().endsWith("--")) {
					
				return crementAfter(list, token.toString(), "--", "-");
			}
			else if(token.equals(".")) {

				return chain(list, tokens);
			}
			else if(token.equals("if")) {
				
				return ifBlock(list, tokens, token);
			}
			else if(token.equals("function")) {

				return function(tokens);
			}
			else if(token.equals("{")) {		// create new let
			
				return block(tokens);
			}			
			else if(token.equals("}")) {		// close let

				return strip(list);
			}
			else if(isBinaryOperator(token)) {		// any look up for binary operators? 
													// maybe dynamically editable?
				return binary(list, tokens, token);
			}
			else if(token.equals("(")) {

				List<LList> instructions = parenthesis(tokens);
				
				for(LList tmp : instructions) {
					
					if(tmp.size() == 1)
						list.add(tmp.get(0));
					else
						list.add(tmp);
				}
			}
			
			/*
			 * JAVA API
			 */
			
			else if(token.equals("new")) {	

				return constructor(list, tokens, token);
			}
			else if(token.toString().contains(".") && !token.toString().startsWith("\"")) {	

				// look ahead
				Object next = tokens.get(0);
				
				if(next.toString().equals("(")) {
					
					return method(list, tokens, token);
				}
				else {
				
					return field(list, tokens, token);
				}
			}
			
			/*
			 * REST 
			 */
			
			else {

				list.add(type(token));
			}
			
			// close working list
			if(list.size() == 3) {
				
				tokens.add(0, list);

				list = new LList();
			}
		}

		// should never be reached!
		throw new RuntimeException("No ; found at end of statement");
	}

	private LList field(LList list, List<Object> tokens, Object token) {

		int lastDot = token.toString().lastIndexOf(".");

		String first = token.toString().substring(0, lastDot);
		String last = token.toString().substring(lastDot, token.toString().length());

		try {
			
			// check class before object
			
			LJava.getClass(first);
			
			list.add(type(first + last + "$"));
		}
		catch(LException e) {
			
			// no class found, so take a object
		
			list.add(type(last + "$"));
			list.add(type(first));
		}
		
		return list;
	}

	private LList constructor(LList list, List<Object> tokens, Object token) {

//		System.out.println(list);
//		System.out.println(tokens);
//		System.out.println(token);
		
		Object object = tokens.remove(0);

		list.add(type(object + "."));

		LList parameters = parse(tokens);
		
		if(parameters.size() == 1) {
			
			list.add(parameters.get(0));
		}
		else {
			
			for(int i = 0; i < parameters.size(); i++)
				list.add(parameters.get(i));
		}
			
		return list;
	}

	private LList method(LList list, List<Object> tokens, Object token) {

//		System.out.println(list);
//		System.out.println(tokens);
//		System.out.println(token);

		LList parameters = parse(tokens);
		
		//System.out.println(parameters);
		
		String[] parts = token.toString().split("\\.");

		list.add(type("." + parts[1]));
		list.add(type(parts[0]));
		
		if(parameters.size() == 1) {
			
			list.add(parameters.get(0));
		}
		else {
			
			for(int i = 0; i < parameters.size(); i++)
				list.add(parameters.get(i));
		}

		return list;
	}

	private LList list(List<Object> tokens) {

		LTupel list = new LTupel();

		int balance = getParenthesisBalance(tokens, 1, "[", "]");

		List<Object> sub = getSubListAndClear(tokens, 0, balance);
		
		tokens.remove(0);

		for(Object obj : sub) {
			
			if(!obj.toString().equals(",")) {

				List<Object> tmp = new ArrayList<>();
				tmp.add(obj);
				tmp.add(";");
				
				LList element = parse(tmp);
				
				list.add(element.get(0));
			}
		}
		
		tokens.add(0, list);

		return list;
	}

	private LList defination(List<Object> tokens) {

		Object name = tokens.remove(0);
					  tokens.remove(0);		// =

		LList defination = new LList();
		
		defination.add(type("define"));
		defination.add(type(name));
		
		if(tokens.size() > 0)
			defination.add(parse(tokens));
		else
			defination.add(type("null"));
		
		return defination;
	}

	private LList assignment(LList list, List<Object> tokens) {

		list.add(0, type("set!"));
		list.add(parse(tokens));
		
		return list;
	}
	
	private LList forLoop(List<Object> tokens) {

		System.out.println("tokens in for " + tokens);
		
		tokens.remove(0);		// remove ( before (i; i <...
		
		List<Object> define = getSubListAndClear(tokens, 0, tokens.indexOf(new Token(";")) + 1);
		List<Object> condition = getSubListAndClear(tokens, 0, tokens.indexOf(new Token(";")) + 1);
		List<Object> increment = getSubListAndClear(tokens, 0, tokens.indexOf(new Token(")")));
		
		tokens.remove(0);		// remove ) after (i; i <...
		
		increment.add(new Token(";"));		// add a ; to the final increment instruction

		LList loop = new LList();
		
		loop.add(type("for"));
		loop.add(parse(define));
		loop.add(parse(condition));
		loop.add(parse(increment));
		
		
		LList begin = new LList(type("begin"));
		
		getSubListAndClear(tokens, 0, tokens.indexOf(new Token("{")));
		
		tokens = tokens.subList(1, tokens.size() - 1);
		
		for(List<Object> list: split(tokens)) {

			begin.add(parse(list));
		}
		
		loop.add(begin);
		
		
		//loop.add(parse(tokens));
		
		return loop;
	}
	
	private LList whileLoop(List<Object> tokens) {

		tokens.remove(0);		// remove ( before (i; i <...

		List<Object> condition = getSubListAndClear(tokens, 0, tokens.indexOf(new Token(")")));
		
		tokens.remove(0);		// remove ) after (i; i <...
		
		condition.add(";");		// add a ; to the final increment instruction

		LList loop = new LList();
		
		loop.add(type("while"));
		loop.add(parse(condition));
		loop.add(parse(tokens));
		
		return loop;
	}
	
	private LList doLoop(List<Object> tokens) {

		tokens.remove(0);		// remove ( before (i; i <...

		List<Object> condition = getSubListAndClear(tokens, 0, tokens.indexOf(new Token(")")));
		
		tokens.remove(0);		// remove ) after (i; i <...
		
		condition.add(";");		// add a ; to the final increment instruction

		LList loop = new LList();
		
		loop.add(type("do"));
		loop.add(parse(condition));
		loop.add(parse(tokens));
		
		return loop;
	}

	private List<LList> parenthesis(List<Object> tokens) {

		List<LList> list = new ArrayList<LList>();
		
		int position = getParenthesisBalance(tokens, 1, "(", ")");

		List<Object> parameterTokens = getSubListAndClear(tokens, 0, position);

		tokens.remove(0);

		List<List<Object>> instructions = new ArrayList<List<Object>>();
		
		List<Object> current = null;

		int paranthesisCount = 0;
		
		for(Object tmp: parameterTokens) {

			if(current == null)
				current = new ArrayList<Object>();
			
			if(tmp.equals("(")) {
				
				current.add(tmp);
				paranthesisCount++;
				continue;
			}	
			else if(tmp.equals(")")) {
				
				current.add(tmp);
				paranthesisCount--;
				
				if(paranthesisCount == 0) {

					instructions.add(current);		
					current = null;
					continue;
				}
			}	
			else if(tmp.equals(",") && paranthesisCount == 0) {

				if(current.size() != 0)
					instructions.add(current);	
				current = null;		
				continue;
			}
			else {

				
				
				current.add(tmp);
			}
		}

		if(current != null)
			instructions.add(current);

		for(List<Object> instruction: instructions) {

			instruction.add(";");
			list.add(parse(instruction));
		}
		
		return list;
	}

	private LList block(List<Object> tokens) {

		List<Object> functionTokens = getSubListAndClear(tokens, 0, tokens.indexOf(new Token("}")));
		
		List<List<Object>> instructions = new ArrayList<List<Object>>();
		
		List<Object> current = new ArrayList<Object>();
		
		for(Object tmp: functionTokens) {
			
			current.add(tmp);
			
			if(tmp.equals(";")) {
				
				instructions.add(current);
				current = new ArrayList<Object>();
			}
		}

		LList let = new LList();
		
		let.add(type("begin"));				// let and begin!!!!! TODO

		for(List<Object> instruction: instructions) {

			let.add(parse(instruction));
		}

		return let;
	}

	private LList binary(LList list, List<Object> tokens, Object token) {

		list.add(0, type(token));
		
		List<Object> rest = getSubListAndClear(tokens, 0, tokens.size());
		
		list.add(parse(rest));
		
		return list;
	}

	private LList function(List<Object> tokens) {

		System.out.println("tokens in function " + tokens);
		
		int position = getParenthesisBalance(tokens, 0, "(", ")");
		
		List<Object> parameterTokens = getSubListAndClear(tokens, 1, position);

		LList paras = new LList();

		for(Object t: parameterTokens) {
			
			if(!t.equals(",")) {
				
				List<Object> current = new ArrayList<Object>();
				current.add(t);
				current.add(";");
				LList inner = parse(current);
				paras.add(inner.get(0));
			}
		}

		LList lambda = new LList();
		lambda.add(type("lambda"));
		lambda.add(paras);
		
		LList begin = new LList(type("begin"));
		
		getSubListAndClear(tokens, 0, tokens.indexOf(new Token("{")));
		
		tokens = tokens.subList(1, tokens.size() - 1);
		
		for(List<Object> list: split(tokens)) {

			begin.add(parse(list));
		}
		
		lambda.add(begin);

		return lambda;
	}

	private LList ifBlock(LList list, List<Object> tokens, Object token) {

		System.out.println(tokens);
		
		// --------------------------------
		// Add the key-word
		// --------------------------------
		
		list.add(type("if"));
		
		// --------------------------------
		// Parse the boolean expression
		// --------------------------------
		int position = getParenthesisBalance(tokens, 0, "(", ")");
		
		List<Object> booleanExpression = getSubListAndClear(tokens, 0, position + 1);
		
		booleanExpression = getSubListAndClear(booleanExpression, 1, booleanExpression.size() - 1);
		booleanExpression.add(";");
		
		list.add(parse(booleanExpression));

		// --------------------------------
		// Parse the if expression
		// --------------------------------
		
		System.out.println(tokens);
		
		position = getParenthesisBalance(tokens, 0, "{", "}");
		
		List<Object> ifExpression = getSubListAndClear(tokens, 0, position + 1);

		list.add(parse(ifExpression));

		// ------------------------------------------
		// Parse the else and if else expressions
		// ------------------------------------------
		if(tokens.size() > 0) {
			
			while(tokens.get(0).equals("else") || tokens.get(0).equals("elseif")) {
				
				token = tokens.remove(0);
				
				if(token.equals("else")) {

					position = getParenthesisBalance(tokens, 0, "{", "}");
					
					List<Object> elseExpression = getSubListAndClear(tokens, 0, position + 1);

					list.add(parse(elseExpression));

					break;
				}
				else if(token.equals("elseif")) {
				
					// TODO
				}
			}
		}
		else {
			
			list.add(new LList());
		}
		
		// ------------------------------------------
		// Return the parsed if-else block
		// ------------------------------------------
		return list;
	}

	private LList chain(LList list, List<Object> tokens) {

		LList first = parse(tokens);
		
		first.add(1, strip(list));
		
		return first;
	}

	private LList crementAfter(LList list, String token, String symbol, String replacement) {

		/*
			(lambda () 
				(begin
					(define tmp i) 
					(set! i (+ i 1))
					(tmp)
				)	
			)
		 */
		
		token = token.toString().replace(symbol, "");
		
		LList define = new LList();
		define.add(type("define"));
		define.add(type("tmp"));
		define.add(type(token));
		
		LList set = new LList();
		set.add(type("set!"));
		set.add(type(token));
		set.add(new LList(type(replacement), type(token), type("1")));
		
		LList result = new LList(type("tmp"));
		
		LList begin = new LList(type("begin"), define, set, result);
		
		list.add(type("lambda"));
		list.add(new LList());
		list.add(begin);
		
		return new LList(list);
	}

	private LList crementBefore(LList list, String token, String symbol, String replacement) {

		/*
			(lambda () 
				(begin
					(set! i (+ i 1))
					(i)
				)	
			)
		 */
		
		token = token.replace(symbol, "");

		LList set = new LList();
		set.add(type("set!"));
		set.add(type(token));
		set.add(new LList(type(replacement), type(token), type("1")));
		
		LList result = new LList(type(token));
		
		LList begin = new LList(type("begin"), set, result);
		
		list.add(type("lambda"));
		list.add(new LList());
		list.add(begin);
		
		return new LList(list);
	}

	private LObject symbol(Object token) {

		return LSymbol.get(token);
	}

	private boolean isBinaryOperator(Object token) {

		return 	
				token.equals("+")  || token.equals("-") || token.equals("*")  || token.equals("/") || 
				token.equals("%")  || token.equals("<") || token.equals("<=") || token.equals(">") || 
				token.equals(">=");
	}

	/**
	 * @param tokens to get the sublist from
	 * @param from bound
	 * @param to bound
	 * @return a sublist as a new list
	 */
	private List<Object> getSubListAndClear(List<Object> tokens, int from, int to) {

		List<Object> subTokens = new ArrayList<Object>(tokens.subList(from, to));
		
		tokens.subList(from, to).clear();
		
		return subTokens;
	}

	/**
	 * @param tokens to find the position of parenthesis balance
	 * @return the position where the parenthesis are equal
	 */
	private int getParenthesisBalance(List<Object> tokens, int parenthesis, String open, String close) {

		for(int i = 0; i < tokens.size(); i++) {
			
			if(tokens.get(i).equals(open))
				parenthesis++;
			if(tokens.get(i).equals(close))
				parenthesis--;
			if(parenthesis == 0)
				return i;
		}
		
		throw new RuntimeException("No balance of parenthesis");
	}

	private LObject type(Object token) {

		if(token instanceof LObject)
			return (LObject) token;
		
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

				return symbol(token);
			}
		}
	}
	
	private LList strip(LList list) {

		// strip
		if(list.size() == 1 && list.getFirst() instanceof LList) {

			list = (LList) list.getFirst();
		}
		
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.Parser#format(java.lang.String)
	 */
	@Override
	public String format(String expression) {

		expression = expression.replace(").", BLANK + ")" + BLANK + "." + BLANK);
		expression = expression.replace(",", BLANK + "," + BLANK);
		expression = expression.replace(";", BLANK + ";" + BLANK);
		expression = expression.replace(LEFT_PARENTHESIS, LEFT_PARENTHESIS_BLANKED);
		expression = expression.replace(RIGHT_PARENTHESIS, RIGHT_PARENTHESIS_BLANKED);
		
		expression = expression.replace(LEFT_BRAKET, LEFT_BRAKET_BLANKED);
		expression = expression.replace(RIGHT_BRAKET, RIGHT_BRAKET_BLANKED);

		return expression.trim();
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.Parser#parseAll(java.lang.String)
	 */
	@Override
	public List<LObject> parseAll(String commands) {
	
		List<Object> tokens = toTokens(commands);
		
		List<LObject> list = new ArrayList<>();
		
		for(List<Object> tmp: split(tokens))
			list.add(parse(tmp));

		System.out.println("result all " + list);
		
		return list; 
	}
	
	/**
	 * Reduces the list for the first block and return this block
	 * 
	 * @return block of which the list was reduced
	 */
	public List<Object> reduce(List<Object> list) {

		boolean block = false;
		
		boolean parenthesis = false;
		
		int balanceP = 0;
		
		int balanceB = 0;
		
		for(int i = 0; i < list.size(); i++) {
			
			if(list.get(i).equals(")")) {
				
				balanceP--;
			}
			
			if(list.get(i).equals("(")) {
				
				balanceP++;
			}
			
			if(balanceP > 0) {
				
				parenthesis = true;
			}
			else {
				
				parenthesis = false;
			}
			
			if(!parenthesis && !block && list.get(i).equals(";")) {

				return getSubListAndClear(list, 0, i + 1);
			}
			
			if(block && balanceB == 0) {
				
				return getSubListAndClear(list, 0, i);
			}
			
			if(list.get(i).equals("{")) {
				
				block = true;
				
				balanceB++;
			}
			
			if(list.get(i).equals("}")) {
	
				balanceB--;
			}
		}
		
		return getSubListAndClear(list, 0, list.size());
	}

	
	/**
	 * Splits a list into sublist which each is a single block.
	 * 
	 * @return list of sublist which each is a single block
	 */
	public List<List<Object>> split(List<Object> list) {
		
		List<List<Object>> result = new ArrayList<List<Object>>();
		
		while(list.size() > 0) {
			
			result.add(reduce(list));
		}
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.Parser#validate(java.lang.String)
	 */
	@Override
	public void validate(String expression) {

		if(!(expression.endsWith(";") || expression.endsWith("}")))
			throw new LException("Missing termination character");
	}

	public List<Object> toTokens(String string) {

		string = format(string);
		
		List<Object> tokens = new ArrayList<>();

		Matcher matcher = SPLIT.matcher(string);

		int line = 1;
		int position = 0;

		while (matcher.find()) {
			
			position++;
			
			String group = matcher.group();
			
			if(group.equals("\n")){

				line++;
				position = 0;
				continue;
			}
			
			Token token = new Token();
			
			token.setWord(group);
			token.setLine(line);
			token.setPosition(position);
			
			tokens.add(token);
		}

		return tokens;
	}
}
