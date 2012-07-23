package de.tuhrig.thofu.types;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.Interpreter;
import de.tuhrig.thofu.gui.Debugger;

public abstract class LObject { // implements TreeNode {

	public LObject run(Environment environment, LObject tokens) {
		
		if(Interpreter.isDebugg()) {
			
			Debugger.call(this, environment, tokens);
			
			while(Interpreter.next() == false) {
			
				try {
					
					Thread.sleep(100);
				}
				catch (InterruptedException e) { }
			}
		}
		
		LObject result = evaluate(environment, tokens);
		
		if(Interpreter.isDebugg()) {
			
			Debugger.result(result);
		}
		
		return result;
	}

	public abstract LObject evaluate(Environment environment, LObject tokens);

	public abstract String toString();

	public abstract boolean equals(Object o);
	
	public abstract int hashCode();
}