package de.tuhrig.fujas.java;

import org.apache.commons.lang3.reflect.MethodUtils;

import de.tuhrig.fujas.Environment;
import de.tuhrig.fujas.types.LException;
import de.tuhrig.fujas.types.LObject;
import de.tuhrig.fujas.types.LSymbol;

public class LJStaticMember extends LJava {

	private String methodName;
	
	private String className;

	public LJStaticMember(LSymbol key) {

		className = key.toString().substring(0, key.toString().lastIndexOf("."));
		methodName = key.toString().substring(key.toString().lastIndexOf(".") + 1, key.toString().length());
	}

	@Override
	public LObject eval(Environment environment, LObject tokens) {

		try {

			Class<?> c = LJava.getClass(className);

			Class<?>[] types = LJava.getTypes(tokens, environment);

			Object[] args = LJava.getObjects(tokens, environment);

			this.result = MethodUtils.invokeStaticMethod(c, methodName, args, types);
		}
		catch (Exception e) {

			throw new LException("[" + e.getClass() + "] - " + e.getMessage());
		}
		
		return this;
	}
}