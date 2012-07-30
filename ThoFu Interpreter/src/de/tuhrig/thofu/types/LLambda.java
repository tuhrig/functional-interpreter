package de.tuhrig.thofu.types;

import java.util.Map.Entry;

import de.tuhrig.thofu.Container;
import de.tuhrig.thofu.Environment;

/**
 * Represents a user-defined lambda.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LLambda extends LOperation {

	private final LList parameters;

	private final LList closureParameters = new LList();
	
	private final LList definitions;

	private Environment closure;

	private int parametersSize;
	
	/**
	 * @param parameters
	 * @param definitions
	 * @param closure
	 */
	public LLambda(LList parameters, LList definitions, Environment closure) {

		super("lambda");

		this.closure = closure;
		this.parameters = parameters;
		this.parametersSize = parameters.size();
		this.definitions = definitions;
	}

	@Override
	public LObject evaluate(Environment environment, LObject tokens) {

		// error checking
		if (parametersSize != ((LList) tokens).size())
			throw new LException("[wrong number of arguments] - expected " + parameters.size() + ", but were " + ((LList) tokens).size() + " [args = " + ((LList) tokens) + "]");

		// we make a new and empty inner environment
		Environment inner = new Environment(environment, name);

		for (Entry<LSymbol, Container> current : closure.entrySet()) {

			if(!parameters.contains(current.getKey()) && !inner.contains(current.getKey())) {
	
				inner.put(current.getKey(), current.getValue());
			}
			
			if(closureParameters.contains(current.getKey())) {
				
				inner.put(current.getKey(), current.getValue());
			}
		}

		for (int i = 0; i < parametersSize; i++) {

			LSymbol key = LSymbol.get(parameters.get(i));

			LObject object = ((LList) tokens).get(i);

			if(!(object instanceof LOperation))
				object = object.run(inner, null);

			inner.put(key, object);
		}	

		LObject result = null;

		for(LObject definition : definitions) {

			result = definition.run(inner, tokens);
		}

		if(result instanceof LLambda) {

			((LLambda) result).addClosureParameters(parameters);
		}
		
		return result;
	}

	private void addClosureParameters(LList cp) {

		for(int i = 0; i < cp.size(); i++) {
			
			if(!parameters.contains(cp.get(i))) {
			
				this.closureParameters.add(cp.get(i));
			}
		}
	}

	@Override
	public boolean equals(Object o) {

		if (o instanceof LLambda)
			return 
					parameters.equals(((LLambda) o).parameters) && 
					definitions.equals(((LLambda) o).definitions);

		return false;
	}
	
	public String inspect() {

		StringBuilder builder = new StringBuilder();

		builder.append(super.inspect());

		builder.append(
				"\n" +
				"Parameters:\t" 	+ getParameters() + "\n" +
				"Definitions:\t" 	+ getDefinitions() + "\n" +
				"Closure:\t\t" 		+ getClosure()
				);
		
		return builder.toString();
	}

	/**
	 * @param name for the lambda
	 */
	public void setName(String name) {

		this.name = name;
	}

	public String toString() {

		return "<Lambda: " + name + ">";
	}

	@Override
	public int argrumentSize(LObject object) {

		LList list = (LList) object;
		
		return list.size() + 1;
	}
	
	/**
	 * @return parameters of the lambda
	 */
	public LList getParameters() {
		
		return parameters;
	}

	/**
	 * @return definitions of the lambda
	 */
	public LList getDefinitions() {
	
		return definitions;
	}
	
	/**
	 * @return closed environment of the lambda
	 */
	public Environment getClosure() {
	
		return closure;
	}
}