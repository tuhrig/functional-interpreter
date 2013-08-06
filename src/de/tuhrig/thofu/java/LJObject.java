package de.tuhrig.thofu.java;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.types.LException;
import de.tuhrig.thofu.types.LObject;
import de.tuhrig.thofu.types.LSymbol;

/**
 * Represents a Java object.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
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
	public LObject evaluate(Environment environment, LObject tokens) {

		if(result == null) {
			
			try {

				Class<?> c = LJava.getClass(className);

				if(ClassUtils.getAllSuperclasses(c).contains(LObject.class)) {
				
					Class<?>[] types = LJava.getTypes(tokens, environment);

					Object[] args = LJava.getObjects(tokens, environment);
					
					this.result = ConstructorUtils.invokeConstructor(c, args, types);
				}
				else {
					
					Class<?>[] types = LJava.getConvertedTypes(tokens, environment);

					Object[] args = LJava.getConvertedObjects(tokens, environment);
					
					this.result = ConstructorUtils.invokeConstructor(c, args, types);
				}
			}
			catch (Exception e) {
	
				throw new LException("[" + e.getClass() + "] - " + e.getMessage(), e);
			}
		}
		
		return this;
	}
	
	@Override
	public int argrumentSize(LObject tokens) {

		return -1;
	}
}