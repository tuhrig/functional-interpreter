package de.tuhrig.fujas.types;

import java.util.Map.Entry;

import de.tuhrig.fujas.Environment;

public class LLambda extends LOperation {

	private final LList parameters;

	private final LList closureParameters = new LList();
	
	private final LList definitions;

	private Environment closure;

	private int parametersSize;
	
	public LLambda(LList parameters, LList definitions, Environment closure) {

		super("lambda");

		this.closure = closure;
		this.parameters = parameters;
		this.parametersSize = parameters.size();
		this.definitions = definitions;
	}

	@Override
	public LObject eval(Environment environment, LObject tokens) {
		
		// error checking
		if (parametersSize != ((LList) tokens).size())
			throw new LException("[wrong number of arguments] - expected " + parameters.size() + ", but were " + ((LList) tokens).size() + " [args = " + ((LList) tokens) + "]");

		// we make a new and empty inner environment
		Environment inner = new Environment(environment);

		for (Entry<LSymbol, LObject> current : closure.entrySet()) {

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
				object = object.eval(inner, null);

			inner.put(key, object);
		}	

		LObject result = null;

		for(LObject definition : definitions) {

			result = definition.eval(inner, tokens);
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

	public void setName(String name) {

		this.name = name;
	}

	public String toString() {

		return "<Lambda: " + name + ">";
	}
}