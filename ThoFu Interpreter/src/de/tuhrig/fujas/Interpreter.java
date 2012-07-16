package de.tuhrig.fujas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import de.tuhrig.fujas.interfaces.EnvironmentListener;
import de.tuhrig.fujas.interfaces.HistoryListener;
import de.tuhrig.fujas.interfaces.IInterpreter;
import de.tuhrig.fujas.interfaces.IJava;
import de.tuhrig.fujas.java.LJClass;
import de.tuhrig.fujas.java.LJava;
import de.tuhrig.fujas.types.LBoolean;
import de.tuhrig.fujas.types.LException;
import de.tuhrig.fujas.types.LLambda;
import de.tuhrig.fujas.types.LList;
import de.tuhrig.fujas.types.LNull;
import de.tuhrig.fujas.types.LNumber;
import de.tuhrig.fujas.types.LObject;
import de.tuhrig.fujas.types.LOperation;
import de.tuhrig.fujas.types.LString;
import de.tuhrig.fujas.types.LSymbol;
import de.tuhrig.fujas.types.LTupel;

/**
 * @author Thomas Uhrig (tuhrig.de)
 */
public class Interpreter implements IInterpreter, IJava {

	private static Logger logger = Logger.getLogger(Interpreter.class);

	private final Environment root = new Environment(null);

	private final List<EnvironmentListener> environmentListeners = new ArrayList<>();
	
	private final List<HistoryListener> historyListeners = new ArrayList<>();

	private final Parser parser = new Parser();

	private String print = "";

	public Interpreter() {

		/**
		 * Build-in operations
		 */
		logger.info("adding operations");

		// (load file)
		root.put(LSymbol.get("load"), new LOperation("load") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LObject path = ((LList) tokens).get(0);

				final File file = new File(path.toString().replace("\"", ""));

				try {
					
					String content = parser.read(file);
					
					String commands = parser.format(content);

					List<LObject> objects = parser.parseAll(commands);
					
					LObject result = LNull.NULL;
					
					for(LObject object: objects) {

						result = execute(object, environment);
					}
					
					return result;
				}
				catch (IOException e) {

					throw new LException("[file not found] - file " + path + " can't be resolved");
				}
			}
		});
		
		// (beginne (expression1) (expression2) ...)
		root.put(LSymbol.get("pair?"), new LOperation("pair?") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				tokens = tokens.eval(environment, tokens);

				return LBoolean.get(tokens instanceof LTupel);
			}
		});

		// (beginne (expression1) (expression2) ...)
		root.put(LSymbol.get("begin"), new LOperation("begin") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LObject result = LNull.NULL;

				LList list = (LList) tokens;

				for (LObject object: list) {
				
					result = object.eval(environment, tokens);
				}

				return result;
			}
		});

		// (let (parameters) body)
		root.put(LSymbol.get("let"), new LOperation("let") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LList parameters = (LList) ((LList) tokens).getFirst();
				LObject body = ((LList) tokens).getRest();

				Environment innerEnvironment = new Environment(environment);

				for (LObject tmp: parameters) {

					LList parameter = (LList) tmp;

					LSymbol symbol = LSymbol.get(parameter.get(0));
					
					LObject object = parameter.get(1).eval(innerEnvironment, tokens);

					innerEnvironment.put(symbol, object);
				}

				return body.eval(innerEnvironment, tokens);
			}
		});

		// (|| first second)
		root.put(LSymbol.get("||"), new LOperation("||") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LObject first = ((LList) tokens).getFirst();
				LObject second = ((LList) tokens).getRest();

				first = first.eval(environment, tokens);
				second = second.eval(environment, tokens);

				return LBoolean.get(first.equals(LBoolean.TRUE) || second.equals(LBoolean.TRUE));
			}
		});

		// (&& first second)
		root.put(LSymbol.get("&&"), new LOperation("&&") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LObject first = ((LList) tokens).getFirst();
				LObject second = ((LList) tokens).getRest();

				first = first.eval(environment, tokens);
				second = second.eval(environment, tokens);

				return LBoolean.get(first.equals(LBoolean.TRUE) && second.equals(LBoolean.TRUE));
			}
		});

		// (+ token..)
		root.put(LSymbol.get("+"), new LOperation("+") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LNumber n = null;

				for (LObject token : (LList) tokens) {

					if (n == null) {

						n = (LNumber) token.eval(environment, token);
					}
					else {

						n = n.add((LNumber) token.eval(environment, token));
					}
				}

				return n;
			}
		});

		// (- token..)
		root.put(LSymbol.get("-"), new LOperation("-") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LNumber n = null;

				for (LObject token : (LList) tokens) {

					if (n == null) {

						n = (LNumber) token.eval(environment, token);
					}
					else {

						n = n.subtract((LNumber) token.eval(environment, token));
					}
				}

				return n;
			}
		});

		// (* token..)
		root.put(LSymbol.get("*"), new LOperation("*") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LNumber n = null;

				for (LObject token : (LList) tokens) {

					if (n == null) {

						n = (LNumber) token.eval(environment, token);
					}
					else {

						n = n.multiply((LNumber) token.eval(environment, token));
					}
				}

				return n;
			}
		});

		// (/ token..)
		root.put(LSymbol.get("/"), new LOperation("/") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LNumber n = null;

				for (LObject token : (LList) tokens) {

					if (n == null) {

						n = (LNumber) token.eval(environment, token);
					}
					else {

						n = n.divide((LNumber) token.eval(environment, token));
					}
				}

				return n;
			}
		});

		// (> first second)
		root.put(LSymbol.get(">"), new LOperation(">") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LObject first = ((LList) tokens).getFirst();
				LObject second = ((LList) tokens).getRest();

				LNumber f = (LNumber) first.eval(environment, tokens);
				LNumber s = (LNumber) second.eval(environment, tokens);

				boolean result = (1 == f.compareTo(s));

				return LBoolean.get(result);
			}
		});
		
		// (< first second)
		root.put(LSymbol.get("<"), new LOperation("<") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LObject first = ((LList) tokens).getFirst();
				LObject second = ((LList) tokens).getRest();

				LNumber f = (LNumber) first.eval(environment, tokens);
				LNumber s = (LNumber) second.eval(environment, tokens);

				boolean result = (-1 == f.compareTo(s));

				return LBoolean.get(result);
			}
		});

		// (eq? first second)
		root.put(LSymbol.get("eq?"), new LOperation("eq?") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LObject first = ((LList) tokens).get(0);
				LObject second = ((LList) tokens).get(1);

				if(first instanceof LList || first instanceof LSymbol)
					first = first.eval(environment, tokens);

				if(second instanceof LList || second instanceof LSymbol)
					second = second.eval(environment, tokens);

				boolean result = first.equals(second);

				return LBoolean.get(result);
			}
		});

		// (define name expression)
		root.put(LSymbol.get("define"), new LOperation("define") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LObject name = ((LList) tokens).get(0);

				LObject expression = LNull.NULL;

				// We have the second version of define, if name is a list.
				// e.g.: (define (dec2 (n)) (- n 1))
				//
				// If so, we create a common define-lambda list. So we have just implemented a 
				// short-cut and the actual definition of a lambda still resists in a single function.
				if (name instanceof List) {

					LList list = (LList) name;

					name = list.get(0);

					if(name instanceof LOperation) {

						name = ((LOperation) name).getName();
					}
					
					LList parameters;

					// enables two ways of specifing the parameters:
					// (1) (define (dec2 (n)) (- n 1))
					// (2) (define (dec2 n) (- n 1))
					if (list.get(1) instanceof LList) {

						parameters = (LList) list.get(1);
					}
					else {

						parameters = new LList();

						parameters.addAll(list.subList(1, list.size()));
					}

					LList body = (LList) ((LList) tokens).getRest();

					LList lambdaList = new LList();
				
					lambdaList.add(LSymbol.get("lambda"));
					lambdaList.add(parameters);

					for (int i = 0; i < body.size(); i++) {

						lambdaList.add(body.get(i));
					}

					expression = lambdaList.eval(environment, lambdaList);
				}
				else {

					LObject second = ((LList) tokens).get(1);

					if(second instanceof LOperation)
						expression = second; //.eval(environment, tokens);
					else
						expression = second.eval(environment, tokens);
				}

				// if we have a lambda (not an other variable) we can name it
				if (expression instanceof LLambda)
					((LLambda) expression).setName(name.toString());

				environment.put(LSymbol.get(name), expression);

				callEnvironmentListeners();

				return expression;
			}
		});

		// (set! value)
		root.put(LSymbol.get("set!"), new LOperation("set!") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LObject name = ((LList) tokens).get(0);

				if (name instanceof List) {

					name = ((LList) name).get(0);
				}

				if (!environment.contains(LSymbol.get(name)))
					throw new LException(name + " is undefined");

				LObject second = ((LList) tokens).get(1);

				second = second.eval(environment, tokens);
				
				environment.set(LSymbol.get(name), second);
	
				return second;
			}
		});

		// (print value)
		root.put(LSymbol.get("print"), new LOperation("print") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LObject object = ((LList) tokens).get(0);

				if(!(object instanceof LOperation))
					object = object.eval(environment, tokens);

				print(object);

				return object;
			}
		});

		// (lambda (parameters) body)
		root.put(LSymbol.get("lambda"), new LOperation("lambda") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LList parameters = (LList) ((LList) tokens).getFirst();

				LList body = (LList) ((LList) tokens).getRest();

				return new LLambda(parameters, body, environment);
			}
		});

		// (if condition ifExpression elseExpression)
		root.put(LSymbol.get("if"), new LOperation("if") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LObject condition = ((LList) tokens).get(0);

				LObject result = condition.eval(environment, tokens);

				if (result.equals(LBoolean.TRUE)) {

					LObject ifExpression = ((LList) tokens).get(1);
					return ifExpression.eval(environment, tokens);
				}

				else {

					LObject elseExpression = ((LList) tokens).get(2);
					return elseExpression.eval(environment, tokens);
				}
			}
		});

		// (cons value1 value2)
		root.put(LSymbol.get("cons"), new LOperation("cons") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LObject value1 = ((LList) tokens).get(0);
				LObject value2 = ((LList) tokens).get(1);

				LTupel tupel = new LTupel();

				tupel.setFirst(value1.eval(environment, tokens));
				tupel.setLast(value2.eval(environment, tokens));

				return tupel;
			}
		});

		// (first value)
		root.put(LSymbol.get("first"), new LOperation("first") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				if (tokens instanceof List)
					tokens = tokens.eval(environment, ((LList) tokens).getRest());

				tokens = tokens.eval(environment, tokens);

				return ((LTupel) tokens).getFirst();
			}
		});

		// (rest value)
		root.put(LSymbol.get("rest"), new LOperation("rest") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				if (tokens instanceof List)
					tokens = tokens.eval(environment, ((LList) tokens).getRest());

				tokens = tokens.eval(environment, tokens);

				return ((LTupel) tokens).getRest();
			}
		});

		/**
		 * Java functions
		 */
	
		// (import "name")
		root.put(LSymbol.get("import"), new LOperation("import") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {

				LList list = (LList) tokens;

				for (LObject object: list) {
				
					String tmp = ((LString) object).getValue();
					
					if(tmp.endsWith("*")) {
						
						LJava.importPackage(tmp.substring(0, tmp.length()-2));
					}
					else {
						
						LJava.importClass(tmp);
					}
				}
				
				return LBoolean.TRUE;
			}
		});
		
		// (interface Interface.class object)
		root.put(LSymbol.get("interface"), new LOperation("interface") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {
				
				LList list = (LList) tokens;
							
				LObject c = list.get(0).eval(environment, tokens);
				LLambda lambda = (LLambda) list.get(1).eval(environment, tokens);
				
				LJClass cl = (LJClass) c.eval(environment, tokens);

				return LJava.createInterface((Class<?>) cl.getJObject(), lambda, environment);
			}
		});
		
		// (class Class.class object)
		root.put(LSymbol.get("class"), new LOperation("class") {

			@Override
			public LObject eval(Environment environment, LObject tokens) {
				
				LList list = (LList) tokens;
							
				LObject c = list.get(0).eval(environment, tokens);
				LLambda lambda = (LLambda) list.get(1).eval(environment, tokens);
				
				LJClass cl = (LJClass) c.eval(environment, tokens);

				return LJava.createClass((Class<?>) cl.getJObject(), lambda, environment);
			}
		});
		
		/**
		 * Build-in lambdas
		 */
		logger.info("adding lambdas");

		execute("(load \"init.txt\")");

		/**
		 * NULL
		 */
		root.put(LSymbol.get("null"), LNull.NULL);
	}

	@Override
	public String execute(String expression) {

		print = "";

		try {

			parser.validate(expression);

			LList tokens = parser.parse(expression);
			
			LObject result = execute(tokens, root);

			if (print.length() == 0) {

				return result.toString();
			}

			return print;
		}
		catch (LException e) {

			logger.warn(e.getMessage());

			e.printStackTrace();

			return e.getMessage();
		}
		catch (Exception e) {

			logger.error("[interpreter exception] - " + e.getMessage());

			e.printStackTrace();

			return "[interpreter exception] - " + e.getMessage();
		}
	}

	private LObject execute(LObject tokens, Environment environment) {
		
//		tokens = parser.replace(tokens, environment);

		Date before = new Date();

		LObject result = tokens.eval(environment, ((LList) tokens).getRest());
		
		Date after = new Date();
		
		if(tokens instanceof LList)
			archive((LList) tokens, before, after);
		
		return 	result;	
	}

	@Override
	public void addEnvironmentListener(EnvironmentListener listener) {

		this.environmentListeners.add(listener);
	}
	
	@Override
	public void addHistoryListener(HistoryListener listener) {

		this.historyListeners.add(listener);
	}

	@Override
	public Environment getEnvironment() {

		return root;
	}
	
	private void callEnvironmentListeners() {

		for (EnvironmentListener listener : environmentListeners)
			listener.update(root);
	}
	
	private void archive(LList tokens, Date started, Date ended) {

		for (HistoryListener listener : historyListeners)
			listener.update(tokens, started, ended);
	}

	private void print(LObject object) {

		print += object;
	}
}