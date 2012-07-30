package de.tuhrig.thofu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import de.tuhrig.thofu.interfaces.EnvironmentListener;
import de.tuhrig.thofu.interfaces.HistoryListener;
import de.tuhrig.thofu.interfaces.IInterpreter;
import de.tuhrig.thofu.interfaces.IJava;
import de.tuhrig.thofu.java.LJClass;
import de.tuhrig.thofu.java.LJava;
import de.tuhrig.thofu.types.LBoolean;
import de.tuhrig.thofu.types.LException;
import de.tuhrig.thofu.types.LLambda;
import de.tuhrig.thofu.types.LList;
import de.tuhrig.thofu.types.LNull;
import de.tuhrig.thofu.types.LNumber;
import de.tuhrig.thofu.types.LObject;
import de.tuhrig.thofu.types.LOperation;
import de.tuhrig.thofu.types.LString;
import de.tuhrig.thofu.types.LSymbol;
import de.tuhrig.thofu.types.LTupel;

/**
 * The interpreter class itself. This class...
 * 
 * 		- ...adds all build-in operations and vairables
 * 		- ...holds the root environment
 * 		- ...can take a command and evaluate it
 * 		- ...provides methods for the GUI (e.g. for the debugger)
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class Interpreter implements IInterpreter, IJava {

	private static Logger logger = Logger.getLogger(Interpreter.class);

	private final Environment root = new Environment(null);

	private final List<EnvironmentListener> environmentListeners = new ArrayList<>();
	
	private final List<HistoryListener> historyListeners = new ArrayList<>();

	private final Parser parser = new Parser();

	private String print = "";

	private static boolean debugg;

	private static boolean next;

	private static boolean resume;

	/**
	 * Creates a new interpreter. Also, all built-in operations and
	 * variables will be created.
	 */
	public Interpreter() {

		/**
		 * Build-in operations
		 */
		logger.info("adding operations");

		// (load file)
		root.put(LSymbol.get("load"), new LOperation("load") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

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

					throw new LException("[file not found] - file " + path + " can't be resolved", e);
				}
			}
		});
		
		// (resource name)
		root.put(LSymbol.get("resource"), new LOperation("resource") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LObject path = ((LList) tokens).get(0);

				String rs = path.toString().replaceAll("\"", "");

				InputStream is = getClass().getResourceAsStream(rs);
				
				@SuppressWarnings("resource")
				Scanner cns = new Scanner(is);
				
				StringBuilder content = new StringBuilder();
				
				String nl = "\n";
				
				while(cns.hasNext()) {
					
					content.append(cns.next());
					content.append(nl);
				}

				String commands = parser.format(content.toString());

				List<LObject> objects = parser.parseAll(commands);
				
				LObject result = LNull.NULL;
				
				for(LObject object: objects) {

					result = execute(object, environment);
				}
				
				return result;
			}
		});
		
		// (pair? '(pair))
		root.put(LSymbol.get("pair?"), new LOperation("pair?") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				tokens = tokens.run(environment, tokens);

				return LBoolean.get(tokens instanceof LTupel);
			}
		});

		// (beginne (expression1) (expression2) ...)
		root.put(LSymbol.get("begin"), new LOperation("begin") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LObject result = LNull.NULL;

				LList list = (LList) tokens;

				for (LObject object: list) {
				
					result = object.run(environment, tokens);
				}

				return result;
			}
		});
		
		// (inspect object)
		root.put(LSymbol.get("inspect"), new LOperation("inspect") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LList list = (LList) tokens;
				
//				Test implementation
//				if(list.getFirst() instanceof LList) {
//					
//					tokens = list.getFirst().run(environment, tokens);
//					
//					return new LString(tokens.inspect());
//				}
//
//				return new LString(list.getFirst().inspect());
				
				return new LString(list.getFirst().run(environment, tokens).inspect());
			}
		});

		// (let (parameters) body)
		root.put(LSymbol.get("let"), new LOperation("let") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LList parameters = (LList) ((LList) tokens).getFirst();
				LObject body = ((LList) tokens).getRest();

				Environment innerEnvironment = new Environment(environment);

				for (LObject tmp: parameters) {

					LList parameter = (LList) tmp;

					LSymbol symbol = LSymbol.get(parameter.get(0));
					
					LObject object = parameter.get(1).run(innerEnvironment, tokens);

					innerEnvironment.put(symbol, object);
				}

				return body.run(innerEnvironment, tokens);
			}
		});

		// (|| first second)
		root.put(LSymbol.get("||"), new LOperation("||") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LObject first = ((LList) tokens).getFirst();
				LObject second = ((LList) tokens).getRest();

				first = first.run(environment, tokens);
				second = second.run(environment, tokens);

				return LBoolean.get(first.equals(LBoolean.TRUE) || second.equals(LBoolean.TRUE));
			}
		});

		// (&& first second)
		root.put(LSymbol.get("&&"), new LOperation("&&") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LObject first = ((LList) tokens).getFirst();
				LObject second = ((LList) tokens).getRest();

				first = first.run(environment, tokens);
				second = second.run(environment, tokens);

				return LBoolean.get(first.equals(LBoolean.TRUE) && second.equals(LBoolean.TRUE));
			}
		});

		// (+ token..)
		root.put(LSymbol.get("+"), new LOperation("+") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LNumber n = null;

				for (LObject token : (LList) tokens) {

					if (n == null) {

						n = (LNumber) token.run(environment, token);
					}
					else {

						n = n.add((LNumber) token.run(environment, token));
					}
				}

				return n;
			}
		});

		// (- token..)
		root.put(LSymbol.get("-"), new LOperation("-") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LNumber n = null;

				for (LObject token : (LList) tokens) {

					if (n == null) {

						n = (LNumber) token.run(environment, token);
					}
					else {

						n = n.subtract((LNumber) token.run(environment, token));
					}
				}

				return n;
			}
		});

		// (* token..)
		root.put(LSymbol.get("*"), new LOperation("*") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LNumber n = null;

				for (LObject token : (LList) tokens) {

					if (n == null) {

						n = (LNumber) token.run(environment, token);
					}
					else {

						n = n.multiply((LNumber) token.run(environment, token));
					}
				}

				return n;
			}
		});

		// (/ token..)
		root.put(LSymbol.get("/"), new LOperation("/") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LNumber n = null;

				for (LObject token : (LList) tokens) {

					if (n == null) {

						n = (LNumber) token.run(environment, token);
					}
					else {

						n = n.divide((LNumber) token.run(environment, token));
					}
				}

				return n;
			}
		});

		// (> first second)
		root.put(LSymbol.get(">"), new LOperation(">") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LObject first = ((LList) tokens).getFirst();
				LObject second = ((LList) tokens).getRest();

				LNumber f = (LNumber) first.run(environment, tokens);
				LNumber s = (LNumber) second.run(environment, tokens);

				boolean result = (1 == f.compareTo(s));

				return LBoolean.get(result);
			}
		});
		
		// (< first second)
		root.put(LSymbol.get("<"), new LOperation("<") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LObject first = ((LList) tokens).getFirst();
				LObject second = ((LList) tokens).getRest();

				LNumber f = (LNumber) first.run(environment, tokens);
				LNumber s = (LNumber) second.run(environment, tokens);

				boolean result = (-1 == f.compareTo(s));

				return LBoolean.get(result);
			}
		});

		// (eq? first second)
		root.put(LSymbol.get("eq?"), new LOperation("eq?") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LObject first = ((LList) tokens).get(0);
				LObject second = ((LList) tokens).get(1);

				if(first instanceof LList || first instanceof LSymbol)
					first = first.run(environment, tokens);

				if(second instanceof LList || second instanceof LSymbol)
					second = second.run(environment, tokens);

				boolean result = first.equals(second);

				return LBoolean.get(result);
			}
		});

		// (define name expression)
		root.put(LSymbol.get("define"), new LOperation("define") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

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

					expression = lambdaList.run(environment, lambdaList);
				}
				else {

					LObject second = ((LList) tokens).get(1);

					if(second instanceof LOperation)
						expression = second; //.run(environment, tokens);
					else
						expression = second.run(environment, tokens);
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
			public LObject evaluate(Environment environment, LObject tokens) {

				LObject name = ((LList) tokens).get(0);

				if (name instanceof List) {

					name = ((LList) name).get(0);
				}

				if (!environment.contains(LSymbol.get(name)))
					throw new LException(name + " is undefined");

				LObject second = ((LList) tokens).get(1);

				second = second.run(environment, tokens);

				environment.set(LSymbol.get(name), second);
	
				return second;
			}
		});

		// (print value)
		root.put(LSymbol.get("print"), new LOperation("print") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LObject object = ((LList) tokens).get(0);

				if(!(object instanceof LOperation))
					object = object.run(environment, tokens);

				print(object);

				return object;
			}
		});

		// (lambda (parameters) body)
		root.put(LSymbol.get("lambda"), new LOperation("lambda") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LList parameters = (LList) ((LList) tokens).getFirst();

				LList body = (LList) ((LList) tokens).getRest();

				return new LLambda(parameters, body, environment);
			}
		});

		// (if condition ifExpression elseExpression)
		root.put(LSymbol.get("if"), new LOperation("if") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LObject condition = ((LList) tokens).get(0);

				LObject result = condition.run(environment, tokens);

				if (result.equals(LBoolean.TRUE)) {

					LObject ifExpression = ((LList) tokens).get(1);
					return ifExpression.run(environment, tokens);
				}

				else {

					LObject elseExpression = ((LList) tokens).get(2);
					return elseExpression.run(environment, tokens);
				}
			}
		});

		// (cons value1 value2)
		root.put(LSymbol.get("cons"), new LOperation("cons") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LObject value1 = ((LList) tokens).get(0);
				LObject value2 = ((LList) tokens).get(1);

				LTupel tupel = new LTupel();

				tupel.setFirst(value1.run(environment, tokens));
				tupel.setLast(value2.run(environment, tokens));

				return tupel;
			}
		});

		// (first value)
		root.put(LSymbol.get("first"), new LOperation("first") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				if (tokens instanceof List)
					tokens = tokens.run(environment, ((LList) tokens).getRest());

				tokens = tokens.run(environment, tokens);

				return ((LTupel) tokens).getFirst();
			}
		});

		// (rest value)
		root.put(LSymbol.get("rest"), new LOperation("rest") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				if (tokens instanceof List)
					tokens = tokens.run(environment, ((LList) tokens).getRest());

				tokens = tokens.run(environment, tokens);

				return ((LTupel) tokens).getRest();
			}
		});

		/**
		 * Java functions
		 */
	
		// (import "name")
		root.put(LSymbol.get("import"), new LOperation("import") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

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
			public LObject evaluate(Environment environment, LObject tokens) {
				
				LList list = (LList) tokens;
							
				LObject c = list.get(0).run(environment, tokens);
				LLambda lambda = (LLambda) list.get(1).run(environment, tokens);
				
				LJClass cl = (LJClass) c.run(environment, tokens);

				return LJava.createInterface((Class<?>) cl.getJObject(), lambda, environment);
			}
		});
		
		// (class Class.class object)
		root.put(LSymbol.get("class"), new LOperation("class") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {
				
				LList list = (LList) tokens;
							
				LObject c = list.get(0).run(environment, tokens);
				LLambda lambda = (LLambda) list.get(1).run(environment, tokens);
				
				LJClass cl = (LJClass) c.run(environment, tokens);

				return LJava.createClass((Class<?>) cl.getJObject(), lambda, environment);
			}
		});
		
		/**
		 * Build-in lambdas
		 */
		logger.info("adding lambdas");

		execute("(resource \"init.txt\")");

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

			logger.warn(e.getMessage(), e);

			return e.getMessage();
		}
		catch (Exception e) {

			logger.error("[interpreter exception] - " + e.getMessage(), e);

			return "[interpreter exception] - " + e.getMessage();
		}
	}
	
	@Override
	public String execute(LObject tokens) {

		print = "";

		try {

			LObject result = execute(tokens, root);

			if (print.length() == 0) {

				return result.toString();
			}

			return print;
		}
		catch (LException e) {

			logger.warn(e.getMessage(), e);

			return e.getMessage();
		}
		catch (Exception e) {

			logger.error("[interpreter exception] - " + e.getMessage(), e);

			return "[interpreter exception] - " + e.getMessage();
		}
	}

	private LObject execute(LObject tokens, Environment environment) {

		// This method is an experiment, it doesn't work. Therefore,
		// it's commented our. It can be commented in and tested with
		// the JUnit tests.
		// tokens = parser.replace(tokens, environment);

		Date before = new Date();

		LObject result = tokens.run(environment, ((LList) tokens).getRest());
		
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

	/**
	 * Activates or de-activates the debugging mode.
	 * 
	 * @param b
	 */
	public static void setDebugg(boolean b) {

		debugg = b;
	}

	/**
	 * @return whether debugging is on or off
	 */
	public static boolean isDebugg() {

		return debugg;
	}

	/**
	 * This method returns true if the debugger should make another step.
	 * It return false if the debugger should still stop at the current 
	 * point.
	 * 
	 * If the method returns true, it will return false immediately after.
	 * This makes the debugger stop at the next step until the user clicks
	 * "next".
	 * 
	 * @return true if the next step should be made.
	 */
	public static boolean next() {

		if(next) {
			
			next = false;
			return true;
		}
		
		return false;
	}
	
	/**
	 * @param b
	 */
	public static void setNext(boolean b) {

		next = b;
	}

	/**
	 * @return true if the debugger should resume (all steps)
	 */
	public static boolean resume() {

		return resume;
	}
	
	/**
	 * Sets the resume-value to the different value as the current.
	 * If resume is false, it will be true after the call. If resume
	 * if true, it will be false after the call.
	 */
	public static void setResume() {

		if(resume)
			resume = false;
		else
			resume = true;
	}
}