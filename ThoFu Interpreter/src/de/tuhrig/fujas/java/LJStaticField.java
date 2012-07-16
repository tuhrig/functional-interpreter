package de.tuhrig.fujas.java;

import org.apache.commons.lang3.reflect.FieldUtils;

import de.tuhrig.fujas.Environment;
import de.tuhrig.fujas.types.LException;
import de.tuhrig.fujas.types.LObject;
import de.tuhrig.fujas.types.LSymbol;

public class LJStaticField extends LJava {

	private String fieldName;
	
	private String className;

	public LJStaticField(LSymbol key) {

		className = key.toString().substring(0, key.toString().lastIndexOf("."));
		fieldName = key.toString().substring(key.toString().lastIndexOf(".") + 1, key.toString().length()).replace("$", "");
	}

	@Override
	public LObject eval(Environment environment, LObject tokens) {

		try {

			Class<?> c = LJava.getClass(className);

			this.result = FieldUtils.readDeclaredStaticField(c, fieldName);
		}
		catch (Exception e) {

			throw new LException("[" + e.getClass() + "] - " + e.getMessage());
		}
		
		return this;
	}
}