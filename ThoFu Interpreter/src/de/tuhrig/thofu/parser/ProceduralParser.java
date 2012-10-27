package de.tuhrig.thofu.parser;

import static de.tuhrig.thofu.Literal.BLANK;
import static de.tuhrig.thofu.Literal.DOUBLE_QUOTE;
import static de.tuhrig.thofu.Literal.FALSE;
import static de.tuhrig.thofu.Literal.LEFT_PARENTHESIS;
import static de.tuhrig.thofu.Literal.LEFT_PARENTHESIS_BLANKED;
import static de.tuhrig.thofu.Literal.NL;
import static de.tuhrig.thofu.Literal.NULL;
import static de.tuhrig.thofu.Literal.RIGHT_PARENTHESIS;
import static de.tuhrig.thofu.Literal.RIGHT_PARENTHESIS_BLANKED;
import static de.tuhrig.thofu.Literal.TRUE;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tuhrig.thofu.interfaces.Parser;
import de.tuhrig.thofu.types.LBoolean;
import de.tuhrig.thofu.types.LList;
import de.tuhrig.thofu.types.LNull;
import de.tuhrig.thofu.types.LNumber;
import de.tuhrig.thofu.types.LObject;
import de.tuhrig.thofu.types.LString;
import de.tuhrig.thofu.types.LSymbol;

/**
 * @author Thomas Uhrig (tuhrig.de)
 */
public class ProceduralParser implements Parser {
	
	private static final Pattern SPLIT = Pattern.compile("[^\\s\"]+|\"([^\"]*)\"");

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.Parser#parse(java.lang.String)
	 */
	@Override
	public LList parse(String expression) {

		expression = format(expression);
		
		List<Object> tokens = new ArrayList<>();

		Matcher matcher = SPLIT.matcher(expression);
		
		while (matcher.find()) {
			
			tokens.add(matcher.group());
		}

		LList result = parse(tokens);
		
		System.out.println("result: " + result);
		
		return result;
	}
	
	private LList parse(List<Object> tokens) {

		System.out.println("parse " + tokens);
		
		LList list = new LList();
		
		while(tokens.size() > 0) {
			
			Object token = tokens.remove(0);
			
			if(token.equals(";")) {

				return strip(list);
			}
			else if(token.toString().startsWith("for")) {

				return forLoop(list, tokens);
			}
			else if(token.toString().startsWith("while")) {
				
				return whileLoop(list, tokens);
			}
			else if(token.toString().startsWith("do")) {
				
				return doLoop(list, tokens);
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

				return function(list, tokens);
			}
			else if(token.equals("{")) {		// create new let
			
				return block(list, tokens);
			}			
			else if(token.equals("}")) {		// close let

				return strip(list);
			}
			else if(token.equals("=")) {
				
				return define(list, tokens);
			}
			else if(isBinaryOperator(token)) {		// any look up for binary operators? 
													// maybe dynamically editable?
				return binary(list, tokens, token);
			}
			else if(token.equals("(")) {

				parenthesis(list, tokens);
			}
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

	private LList forLoop(LList list, List<Object> tokens) {

		tokens.remove(0);		// remove ( before (i; i <...
		
		List<Object> define = getSubListAndClear(tokens, 0, tokens.indexOf(";") + 1);
		List<Object> condition = getSubListAndClear(tokens, 0, tokens.indexOf(";") + 1);
		List<Object> increment = getSubListAndClear(tokens, 0, tokens.indexOf(")"));
		
		tokens.remove(0);		// remove ) after (i; i <...
		
		increment.add(";");		// add a ; to the final increment instruction

		list.add(type("for"));
		list.add(parse(define));
		list.add(parse(condition));
		list.add(parse(increment));
		list.add(parse(tokens));
		
		return list;
	}
	
	private LList whileLoop(LList list, List<Object> tokens) {

		tokens.remove(0);		// remove ( before (i; i <...

		List<Object> condition = getSubListAndClear(tokens, 0, tokens.indexOf(")"));
		
		tokens.remove(0);		// remove ) after (i; i <...
		
		condition.add(";");		// add a ; to the final increment instruction

		list.add(type("while"));
		list.add(parse(condition));
		list.add(parse(tokens));
		
		return list;
	}
	
	private LList doLoop(LList list, List<Object> tokens) {

		tokens.remove(0);		// remove ( before (i; i <...

		List<Object> condition = getSubListAndClear(tokens, 0, tokens.indexOf(")"));
		
		tokens.remove(0);		// remove ) after (i; i <...
		
		condition.add(";");		// add a ; to the final increment instruction

		list.add(type("do"));
		list.add(parse(condition));
		list.add(parse(tokens));
		
		return list;
	}

	private void parenthesis(LList list, List<Object> tokens) {

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
	}

	private LList block(LList list, List<Object> tokens) {

		List<Object> functionTokens = getSubListAndClear(tokens, 0, tokens.indexOf("}"));
		
		List<List<Object>> instructions = new ArrayList<List<Object>>();
		
		List<Object> current = new ArrayList<Object>();
		
		for(Object tmp: functionTokens) {
			
			current.add(tmp);
			
			if(tmp.equals(";")) {
				
				instructions.add(current);
				current = new ArrayList<Object>();
			}
		}
		
		LList inner = new LList();
		
		for(List<Object> instruction: instructions)
			inner.add(parse(instruction));

		LList let = new LList();
		
		let.add(type("begin"));				// let and begin!!!!! TODO
		let.add(inner);						// TODO is begin needed?
		
		list.add(let);

		return strip(list);
	}

	private LList binary(LList list, List<Object> tokens, Object token) {

		list.add(0, type(token));
		
		List<Object> rest = getSubListAndClear(tokens, 0, tokens.size());
		
		list.add(parse(rest));
		
		return list;
	}

	private LList define(LList list, List<Object> tokens) {

		list.add(0, type("define"));
		list.add(parse(tokens));
		
		return list;
	}

	private LList function(LList list, List<Object> tokens) {

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

		LList inner = parse(tokens);

		LList body = new LList();
		body.add(inner.get(0));		// body
		body.add(inner.get(1));		// body

		LList lambda = new LList();
		lambda.add(type("lambda"));
		lambda.add(paras);
		lambda.add(body);
		
		list.add(lambda);

		return strip(list);
	}

	private LList ifBlock(LList list, List<Object> tokens, Object token) {

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
		
		// TODO just for the moment a simple implementation
		
		List<LObject> list = new ArrayList<>();
		
		list.add(parse(commands));
		
		return list; 
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.Parser#validate(java.lang.String)
	 */
	@Override
	public void validate(String expression) {

		// TODO Auto-generated method stub
	}
}
