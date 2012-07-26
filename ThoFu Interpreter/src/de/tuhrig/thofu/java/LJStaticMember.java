package de.tuhrig.thofu.java;

import org.apache.commons.lang3.reflect.MethodUtils;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.types.LException;
import de.tuhrig.thofu.types.LObject;
import de.tuhrig.thofu.types.LSymbol;

/**
 * Represents a static member.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LJStaticMember extends LJava {

	private String methodName;
	
	private String className;

	public LJStaticMember(LSymbol key) {

		className = key.toString().substring(0, key.toString().lastIndexOf("."));
		methodName = key.toString().substring(key.toString().lastIndexOf(".") + 1, key.toString().length());
	}

	@Override
	public LObject evaluate(Environment environment, LObject tokens) {

		try {

			Class<?> c = LJava.getClass(className);

			Class<?>[] types = LJava.getTypes(tokens, environment);

			Object[] args = LJava.getObjects(tokens, environment);

			this.result = MethodUtils.invokeStaticMethod(c, methodName, args, types);
		}
		catch (Exception e) {

			throw new LException("[" + e.getClass() + "] - " + e.getMessage(), e);
		}
		
		return this;
	}

	@Override
	public int argrumentSize(LObject tokens) {

		return -1;
	}
}