package de.tuhrig.thofu.types;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.Literal;
import de.tuhrig.thofu.gui.ThoFuUi;
import de.tuhrig.thofu.interfaces.IDebugger;
import de.tuhrig.thofu.java.LJObject;

/**
 * Represents an abstract object. It's the super class of 
 * every other object.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public abstract class LObject {

	/**
	 * The environment passed to the object in the run method. It's
	 * only stored for debugging and object inspection.
	 */
	private Environment environment;
	
	/**
	 * The tokens passed to the object in the run method. It's
	 * only stored for debugging and object inspection.
	 */
	private LObject tokens;
	
	/**
	 * The debugger instance, a singleton created in the UI
	 */
	private final IDebugger debugger = ThoFuUi.getDebugger();

	/**
	 * This method wraps the evaluate method that each object must
	 * override. It enables a simple debugging mechanism. It can stop
	 * before and after the evaluate call. Therefore, it's not intended
	 * to be overwritten.
	 * 
	 * @param environment to evaluate the object with
	 * @param tokens to evaluate the object with
	 * @return evaluation result
	 */
	public final LObject run(Environment environment, LObject tokens) {
		
		// store for inspection
		this.environment = environment;
		this.tokens = tokens;
		
		// stop before the call
		if(debugger.debugg()) {
			
			if(this instanceof LOperation) {
	
				debugger.pushCall(this, environment, tokens, 1);
			}
			else {

				debugger.pushCall(this, environment, tokens, 0);
			}

			while(debugger.resume() == false && debugger.next() == false) {
			
				try {
					
					Thread.sleep(100);
				}
				catch (InterruptedException e) { }
			}
		}
		
		LObject result = evaluate(environment, tokens);
		
		if(result instanceof LJObject) {
			
			if(((LJObject) result).getJObject() instanceof LObject)
				result = (LObject) ((LJObject) result).getJObject();
		}
		
		// stop after the call
		if(debugger.debugg()) {

			debugger.pushResult(result, environment, tokens, argrumentSize(tokens));
		
			while(debugger.resume() == false && debugger.next() == false) {
				
				try {
					
					Thread.sleep(100);
				}
				catch (InterruptedException e) { }
			}
		}
		
		// store for inspection
		this.environment = environment;
		this.tokens = tokens;
		
		return result;
	}

	/**
	 * The argument size tells the debugger how many parameters
	 * this object will remove from the stack, e.g. 1 means that 
	 * 1 frame will be removed by the debugger, 2 means that 2
	 * frames will be removed.
	 * 
	 * @param tokens to calculate the argument size with
	 * @return argument size
	 */
	public abstract int argrumentSize(LObject tokens);

	/**
	 * This method does the actual evaluation of the object. It must
	 * be overwritten be every implementation. It should never be called
	 * directly! It's only called by the run-method of the object itself.
	 * 
	 * @param environment to evaluate the object with
	 * @param tokens to evaluate the object with
	 * @return evaluation result
	 */
	public abstract LObject evaluate(Environment environment, LObject tokens);

	/**
	 * Basic arithmetic operation. A subtype can override this method
	 * if suitable. If not, the standard implementation will throw a
	 * LException.
	 * 
	 * @param object to operate
	 * @return result depending on the subtype implementation
	 */
	public LObject sum(LObject object) {
		
		throw new LException("Not implemented for " + getClass());
	}

	/**
	 * Basic arithmetic operation. A subtype can override this method
	 * if suitable. If not, the standard implementation will throw a
	 * LException.
	 * 
	 * @param object to operate
	 * @return result depending on the subtype implementation
	 */
	public LObject subtract(LObject object) {
		
		throw new LException("Not implemented for " + getClass());
	}

	/**
	 * Basic arithmetic operation. A subtype can override this method
	 * if suitable. If not, the standard implementation will throw a
	 * LException.
	 * 
	 * @param object to operate
	 * @return result depending on the subtype implementation
	 */
	public LObject multiply(LObject object) {
		
		throw new LException("Not implemented for " + getClass());
	}

	/**
	 * Basic arithmetic operation. A subtype can override this method
	 * if suitable. If not, the standard implementation will throw a
	 * LException.
	 * 
	 * @param object to operate
	 * @return result depending on the subtype implementation
	 */
	public LObject divide(LObject object) {
		
		throw new LException("Not implemented for " + getClass());
	}

	/**
	 * Basic arithmetic operation. A subtype can override this method
	 * if suitable. If not, the standard implementation will throw a
	 * LException.
	 * 
	 * @param object to operate
	 * @return result depending on the subtype implementation
	 */
	public int compareTo(LObject object) {
		
		throw new LException("Not implemented for " + getClass());
	}

	/**
	 * @return a panel with information about the current object
	 */
	public String inspect() {

		StringBuilder builder = new StringBuilder();

		builder.append(
				"toString():\t" 	+ this + Literal.NL + 
			    "Class:\t\t" 		+ this.getClass() + Literal.NL +
			    "Tokens:\t\t" 		+ tokens + Literal.NL +
				"Environment:\t" 	+ environment
				);
		
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public abstract boolean equals(Object o);
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public abstract int hashCode();
}