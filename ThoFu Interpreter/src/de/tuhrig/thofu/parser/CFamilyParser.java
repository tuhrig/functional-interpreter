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
public class CFamilyParser implements Parser {
	
	private static final Pattern SPLIT = Pattern.compile("[^\\s\"]+|\"([^\"]*)\"");

	private Object last = null;
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.Parser#parse(java.lang.String)
	 */
	@Override
	public LList parse(String expression) {

		expression = format(expression);
		
		List<Object> tokens = new ArrayList<>();

		// reset!
		last = null;
		
		Matcher matcher = SPLIT.matcher(expression);
		
		while (matcher.find()) {
			
			tokens.add(matcher.group());
		}

		LList result = parse(tokens);
		
		System.out.println(result);
		
		return result;
	}
	
	private LList parse(List<Object> tokens) {

		LList list = new LList();
		
		while(tokens.size() > 0) {
			
			Object token = tokens.remove(0);
			
			if(token.equals(";")) {

				return strip(list);
			}
			else if(token.equals(",")) {
				
				// we skipt it ;)
			}
			else if(token.equals("function")) {

				int position = getParenthesisBalance(tokens, 0);
	
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
				lambda.add(LSymbol.get("lambda"));
				lambda.add(paras);
				lambda.add(body);
				
				list.add(lambda);

				return strip(list);
			}
			else if(token.equals("{")) {		// create new let
			
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
				
				let.add(LSymbol.get("let"));
				let.add(inner);
				
				list.add(let);
			}			
			else if(token.equals("}")) {		// close let

				return strip(list);
			}
			else if(token.equals("(")) {

				// -----------------------------------
				//	Parse method parameter list
				// -----------------------------------
				if(getType(token) instanceof LSymbol) {

					int position = getParenthesisBalance(tokens, 1);

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
				
				// -----------------------------------
				//	Parse common (...)
				// -----------------------------------
				else if(last == null || last instanceof LList || isBinaryOperator(last)) {
					
					LList inner = parse(tokens);

					tokens.add(0, inner);
				}
			}
			else if(token.equals(")")) {

				return strip(list);
			}
			else if(token.equals("=")) {
				
				list.add(0, LSymbol.get("define"));
			}
			else if(isBinaryOperator(token)) {		// any look up for binary operators? 
													// maybe dynamically editable?
				list.add(0, getType(token));
			}
			else {
				
				list.add(getType(token));
			}
			
			// close working list
			if(list.size() == 3) {
				
				tokens.add(0, list);

				list = new LList();
			}
			
			last = token;
		}

		// should never be reached!
		throw new RuntimeException("No ;!");
	}

	private boolean isBinaryOperator(Object token) {

		return 	
				token.equals("+")  || token.equals("-") || token.equals("*")  || token.equals("/") || 
				token.equals("%")  || token.equals("<") || token.equals("<=") || token.equals(">") || 
				token.equals(">=") || token.equals("=");
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
	private int getParenthesisBalance(List<Object> tokens, int parenthesis) {

		for(int i = 0; i < tokens.size(); i++) {
			
			if(tokens.get(i).equals("("))
				parenthesis++;
			if(tokens.get(i).equals(")"))
				parenthesis--;
			if(parenthesis == 0)
				return i;
		}
		
		throw new RuntimeException("No balance of parenthesis");
	}

	private LObject getType(Object token) {

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

				return LSymbol.get(token);
			}
		}
	}
	
	private LList strip(LList list) {

		// strip
		while(list.size() == 1 && list.getFirst() instanceof LList)
			list = (LList) list.getFirst();
		
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.Parser#format(java.lang.String)
	 */
	@Override
	public String format(String expression) {

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
