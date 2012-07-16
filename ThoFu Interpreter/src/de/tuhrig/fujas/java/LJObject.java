package de.tuhrig.fujas.java;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import de.tuhrig.fujas.Environment;
import de.tuhrig.fujas.types.LException;
import de.tuhrig.fujas.types.LObject;
import de.tuhrig.fujas.types.LSymbol;

public class LJObject extends LJava {

	private String className;

	public LJObject(LSymbol key) {

		className = key.toString();
		
		className = className.substring(0, className.length() - 1);
	}

	public LJObject(Object object) {

		result = object;
	}

	@Override
	public LObject eval(Environment environment, LObject tokens) {

		if(result == null) {
			
			try {

				Class<?> c = LJava.getClass(className);

				Class<?>[] types = LJava.getTypes(tokens, environment);

				Object[] args = LJava.getObjects(tokens, environment);
				
				this.result = ConstructorUtils.invokeConstructor(c, args, types);
			}
			catch (Exception e) {
	
				throw new LException("[" + e.getClass() + "] - " + e.getMessage());
			}
		}
		
		return this;
	}
}