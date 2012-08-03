package de.tuhrig.thofu.java;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.types.LException;
import de.tuhrig.thofu.types.LObject;
import de.tuhrig.thofu.types.LSymbol;

/**
 * Represents a Java class.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LJClass extends LJava {

	private String className;

	public LJClass(LSymbol key) {

		className = key.toString().replace(".class", "");
		
		if(result == null) {
			
			try {
				
				result = LJava.getClass(className);
			}
			catch (Exception e) {

				throw new LException("[" + e.getClass() + "] - " + e.getMessage(), e);
			}
		}
	}

	@Override
	public LObject evaluate(Environment environment, LObject tokens) {
		
		return this;
	}

	@Override
	public int argrumentSize(LObject tokens) {

		return -1;
	}
}