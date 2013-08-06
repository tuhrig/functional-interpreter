package de.tuhrig.thofu.java;

import org.apache.commons.lang3.reflect.FieldUtils;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.types.LException;
import de.tuhrig.thofu.types.LObject;
import de.tuhrig.thofu.types.LSymbol;

/**
 * Represents a static field.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LJStaticField extends LJava {

	private String fieldName;
	
	private String className;

	public LJStaticField(LSymbol key) {

		className = key.toString().substring(0, key.toString().lastIndexOf("."));
		fieldName = key.toString().substring(key.toString().lastIndexOf(".") + 1, key.toString().length()).replace("$", "");
	}

	@Override
	public LObject evaluate(Environment environment, LObject tokens) {

		try {

			Class<?> c = LJava.getClass(className);

			this.result = FieldUtils.readDeclaredStaticField(c, fieldName);
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