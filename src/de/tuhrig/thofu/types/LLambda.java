package de.tuhrig.thofu.types;

import java.util.Map.Entry;

import de.tuhrig.thofu.Container;
import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.Literal;

/**
 * Represents a user-defined lambda.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LLambda extends LOperation {

	private final LList parameters;

	private final LList closureParameters = new LList();
	
	private final LList definitions;

	private final Environment closure;

	/**
	 * @param parameters
	 * @param definitions
	 * @param closure
	 */
	public LLambda(LList parameters, LList definitions, Environment closure) {

		// call super to set the name
		super("unnamed lambda");

		this.closure = closure;
		this.parameters = parameters;
		this.definitions = definitions;
	}

	@Override
	public LObject evaluate(Environment environment, LObject tokens) {

		// error checking
		if (parameters.size() != ((LList) tokens).size())
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

		for (int i = 0; i < parameters.size(); i++) {

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

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LOperation#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {

		if (o instanceof LLambda)
			return 
					parameters.equals(((LLambda) o).parameters) && 
					definitions.equals(((LLambda) o).definitions);

		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LOperation#inspect()
	 */
	@Override
	public String inspect() {

		StringBuilder builder = new StringBuilder();

		builder.append(super.inspect());

		builder.append(
				Literal.NL +
				"Parameters:" + Literal.TP + parameters + Literal.NL +
				"Definitions:" + Literal.TP + definitions + Literal.NL +
				"Closure:" + Literal.TP + Literal.TP + closure
				);
		
		return builder.toString();
	}

	/**
	 * @param name for the lambda
	 */
	public void setName(String name) {

		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LOperation#toString()
	 */
	@Override
	public String toString() {

		return "<Lambda: " + name + ">";
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LOperation#argrumentSize(de.tuhrig.thofu.types.LObject)
	 */
	@Override
	public int argrumentSize(LObject object) {

		LList list = (LList) object;
		
		return list.size() + 1;
	}
}