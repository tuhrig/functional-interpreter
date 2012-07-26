package de.tuhrig.thofu.types;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.Interpreter;
import de.tuhrig.thofu.gui.Debugger;

/**
 * Represents an abstract object. It's the super class of 
 * every other object.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public abstract class LObject { // implements TreeNode {

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
		
		// stop before the call
		if(Interpreter.isDebugg()) {
			
			if(this instanceof LOperation) {
	
				Debugger.getInstance().pushCall(this, environment, tokens, 1);
			}
			else {

				Debugger.getInstance().pushCall(this, environment, tokens, 0);
			}

			while(Interpreter.resume() == false && Interpreter.next() == false) {
			
				try {
					
					Thread.sleep(100);
				}
				catch (InterruptedException e) { }
			}
		}
		
		LObject result = evaluate(environment, tokens);
		
		// stop after the call
		if(Interpreter.isDebugg()) {

			Debugger.getInstance().pushResult(result, environment, tokens, argrumentSize(tokens));
		
			while(Interpreter.resume() == false && Interpreter.next() == false) {
				
				try {
					
					Thread.sleep(100);
				}
				catch (InterruptedException e) { }
			}
		}
		
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
	 * @param environment to evaluate the object with
	 * @param tokens to evaluate the object with
	 * @return evaluation result
	 */
	public abstract LObject evaluate(Environment environment, LObject tokens);

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