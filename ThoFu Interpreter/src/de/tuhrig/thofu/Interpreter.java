package de.tuhrig.thofu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import de.tuhrig.thofu.interfaces.EnvironmentListener;
import de.tuhrig.thofu.interfaces.HistoryListener;
import de.tuhrig.thofu.interfaces.IInterpreter;
import de.tuhrig.thofu.interfaces.IJava;
import de.tuhrig.thofu.interfaces.Parser;
import de.tuhrig.thofu.java.LJClass;
import de.tuhrig.thofu.java.LJObject;
import de.tuhrig.thofu.java.LJava;
import de.tuhrig.thofu.parser.DefaultParser;
import de.tuhrig.thofu.types.LBoolean;
import de.tuhrig.thofu.types.LException;
import de.tuhrig.thofu.types.LLambda;
import de.tuhrig.thofu.types.LList;
import de.tuhrig.thofu.types.LNull;
import de.tuhrig.thofu.types.LObject;
import de.tuhrig.thofu.types.LOperation;
import de.tuhrig.thofu.types.LSymbol;

/**
 * The interpreter class itself. This class...
 * 
 * 		- ...adds all build-in operations and variables
 * 		- ...holds the root environment
 * 		- ...can take a command and evaluate it
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class Interpreter implements IInterpreter, IJava {

	private static Logger logger = Logger.getLogger(Interpreter.class);

	private final Environment root = new Environment(null);

	private final List<EnvironmentListener> environmentListeners = new ArrayList<>();
	
	private final List<HistoryListener> historyListeners = new ArrayList<>();

	private Parser parser = new DefaultParser();

	private StringBuilder builder = new StringBuilder();

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

				String path = ((LList) tokens).get(0).toString();

				final File file = new File(path);
				
				try {
					
					String content = new Util().read(file);
					
					String commands = parser.format(content);

					List<LObject> objects = parser.parseAll(commands);
					
					LObject result = LNull.NULL;
					
					for(LObject object: objects) {

						result = execute(object, environment);
					}
					
					return result;
				}
				catch (IOException e) {

					throw new LException("[file not found] - " + path + " can't be resolved", e);
				}
			}
		});
		
		// (resource name)
		root.put(LSymbol.get("resource"), new LOperation("resource") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				String rs = ((LList) tokens).get(0).toString();

				String content = new Util().read(getClass(), rs);
				
				String commands = parser.format(content.toString());

				List<LObject> objects = parser.parseAll(commands);
				
				LObject result = LNull.NULL;
				
				for(LObject object: objects) {

					result = execute(object, environment);
				}
				
				return result;
			}
		});
		
		// (instance? object class)
		root.put(LSymbol.get("instance?"), new LOperation("instance?") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LList list = (LList) tokens;
	
				LObject first = list.get(0).run(environment, tokens);
				LJClass second = (LJClass) list.get(1).run(environment, tokens);
			
				return LBoolean.get(((Class<?>) second.getJObject()).isInstance(first));
			}
		});
		
		// (try (expression) (exception (expression)))
		root.put(LSymbol.get("try"), new LOperation("try") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LList list = (LList) tokens;
				
				LObject first = list.get(0);
				LObject second = list.get(1);
				
				try {
					
					return first.run(environment, first);
				}
				catch(Exception e) {
					
					logger.info("Caught exception", e);
			
					LList tmp = (LList) second;
					LObject name = tmp.getFirst();
					LObject expression = tmp.getRest();
					
					environment.put(LSymbol.get(name), new LJObject(e));
					
					return expression.run(environment, expression);
				}
			}
		});

		// (begin (expression1) (expression2) ...)
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

		// (+ token..)
		root.put(LSymbol.get("+"), new LOperation("+") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LObject n = null;

				for (LObject token : (LList) tokens) {

					if (n == null) {

						n = token.run(environment, token);
					}
					else {

						n = n.sum(token.run(environment, token));
					}
				}

				return n;
			}
		});

		// (- token..)
		root.put(LSymbol.get("-"), new LOperation("-") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LObject n = null;

				for (LObject token : (LList) tokens) {

					if (n == null) {

						n = token.run(environment, token);
					}
					else {

						n = n.subtract(token.run(environment, token));
					}
				}

				return n;
			}
		});

		// (* token..)
		root.put(LSymbol.get("*"), new LOperation("*") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LObject n = null;

				for (LObject token : (LList) tokens) {

					if (n == null) {

						n = token.run(environment, token);
					}
					else {

						n = n.multiply(token.run(environment, token));
					}
				}

				return n;
			}
		});

		// (/ token..)
		root.put(LSymbol.get("/"), new LOperation("/") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LObject n = null;

				for (LObject token : (LList) tokens) {

					if (n == null) {

						n = token.run(environment, token);
					}
					else {

						n = n.divide(token.run(environment, token));
					}
				}

				return n;
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

				builder.append(object.toString());

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
		
		// (for (define i 0) (< i 5) (set! i (+ i 1)) (print i))
		root.put(LSymbol.get("for"), new LOperation("for") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LList define = (LList) ((LList) tokens).get(0);
				LList condition = (LList) ((LList) tokens).get(1);
				LList increment = (LList) ((LList) tokens).get(2);
				LList block = (LList) ((LList) tokens).get(3);

				Environment innerEnvironment = new Environment(environment);

				define.evaluate(innerEnvironment, tokens);
				
				LObject result = LNull.NULL;
				
				while(condition.evaluate(innerEnvironment, tokens) == LBoolean.TRUE) {
					
					result = block.evaluate(innerEnvironment, tokens);
					
					increment.evaluate(innerEnvironment, tokens);
				}

				return result;
			}
		});
		
		// (while (true) (print i))
		root.put(LSymbol.get("while"), new LOperation("while") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LList condition = (LList) ((LList) tokens).get(0);
				LList block = (LList) ((LList) tokens).get(1);
		
				LObject result = LNull.NULL;
				
				while(condition.evaluate(environment, tokens) == LBoolean.TRUE) {
					
					result = block.evaluate(environment, tokens);
				}

				return result;
			}
		});
		
		// (do (true) (print i))
		root.put(LSymbol.get("do"), new LOperation("do") {

			@Override
			public LObject evaluate(Environment environment, LObject tokens) {

				LList condition = (LList) ((LList) tokens).get(0);
				LList block = (LList) ((LList) tokens).get(1);
		
				LObject result = LNull.NULL;
				
				do {
					
					result = block.evaluate(environment, tokens);
					
				}while(condition.evaluate(environment, tokens) == LBoolean.TRUE);

				return result;
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
				
					String tmp = object.toString();
					
					System.out.println("import " + tmp);
					
					if(tmp.endsWith("*")) {
						
						System.out.println("package");
						
						LJava.importPackage(tmp.substring(0, tmp.length()-2));
					}
					else {
						
						System.out.println("class");
						
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
		 * Build-in lambdas from initiation file (init.txt)
		 */
		logger.info("loading initiation file");

		execute("(resource \"init.txt\")");
		
		// TODO 
		//just for the moment a simple implementation
		//this.parser = new ProceduralParser();
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.IInterpreter#execute(java.lang.String)
	 */
	@Override
	public String execute(String expression) {

		try {

			parser.validate(expression);

			LList tokens = parser.parse(expression);

			LObject result = execute(tokens, root);
			
			return result.toString();
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
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.IInterpreter#execute(de.tuhrig.thofu.types.LObject)
	 */
	@Override
	public String execute(LObject tokens) {

		try {

			LObject result = execute(tokens, root);

			return result.toString();
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

		LObject result = tokens.run(environment, tokens);
		
		Date after = new Date();

		if(tokens instanceof LList)
			archive((LList) tokens, before, after);

		return 	result;	
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.IInterpreter#addEnvironmentListener(de.tuhrig.thofu.interfaces.EnvironmentListener)
	 */
	@Override
	public void addEnvironmentListener(EnvironmentListener listener) {

		this.environmentListeners.add(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.IInterpreter#addHistoryListener(de.tuhrig.thofu.interfaces.HistoryListener)
	 */
	@Override
	public void addHistoryListener(HistoryListener listener) {

		this.historyListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.IInterpreter#getEnvironment()
	 */
	@Override
	public Environment getEnvironment() {

		return root;
	}
	
	/**
	 * Calls all registered environment listeners.
	 */
	private void callEnvironmentListeners() {

		for (EnvironmentListener listener : environmentListeners)
			listener.update(root);
	}
	
	/**
	 * Calls all registered history listeners.
	 */
	private void archive(LList tokens, Date started, Date ended) {

		for (HistoryListener listener : historyListeners)
			listener.update(tokens, started, ended);
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.IInterpreter#setParser(de.tuhrig.thofu.interfaces.Parser)
	 */
	@Override
	public void setParser(Parser parser) {

		this.parser = parser;
	}

	@Override
	public Parser getParser() {

		return parser;
	}

	@Override
	public void setStringBuilder(StringBuilder builder) {

		this.builder = builder;
	}

	@Override
	public StringBuilder getStringBuilder() {

		return builder;
	}
}