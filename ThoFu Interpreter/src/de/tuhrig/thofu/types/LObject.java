package de.tuhrig.thofu.types;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.Interpreter;
import de.tuhrig.thofu.gui.Debugger;

public abstract class LObject { // implements TreeNode {

	public LObject run(Environment environment, LObject tokens) {
		
		if(Interpreter.isDebugg()) {
			
			if(this instanceof LOperation) {
				
//				System.out.println(this + " call add " + 1);
//				System.out.println(this + " call remove " + 1 + "\n");
				
				Debugger.getInstance().pushCall(this, environment, tokens, 1);
			}
			else {
				
//				System.out.println(this + " call add " + 1);
//				System.out.println(this + " call remove " + 0 + "\n");
				
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
		
		if(Interpreter.isDebugg()) {

//			System.out.println(this + " result add " + 1);
//			System.out.println(this + " result remove " + argrumentSize(tokens) + "\n");
			
			Debugger.getInstance().pushResult(result, environment, tokens, argrumentSize(tokens));
		}
		
		return result;
	}

	public abstract int argrumentSize(LObject tokens);

	public abstract LObject evaluate(Environment environment, LObject tokens);

	public abstract String toString();

	public abstract boolean equals(Object o);
	
	public abstract int hashCode();
}