package de.tuhrig.fujas.java;

import de.tuhrig.fujas.Environment;
import de.tuhrig.fujas.types.LException;
import de.tuhrig.fujas.types.LObject;
import de.tuhrig.fujas.types.LSymbol;

public class LJClass extends LJava {

	private String className;

	public LJClass(LSymbol key) {

		className = key.toString().replace(".class", "");
	}

	@Override
	public LObject eval(Environment environment, LObject tokens) {

		if(result == null) {
			
			try {
				
				result = LJava.getClass(className);
			}
			catch (Exception e) {

				throw new LException("[" + e.getClass() + "] - " + e.getMessage());
			}
		}
		
		return this;
	}
}