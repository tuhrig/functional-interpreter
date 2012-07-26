package de.tuhrig.thofu.java;

import org.apache.commons.lang3.reflect.FieldUtils;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.types.LException;
import de.tuhrig.thofu.types.LList;
import de.tuhrig.thofu.types.LObject;
import de.tuhrig.thofu.types.LSymbol;

/**
 * Represents an instance field.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LJInstanceField extends LJava {

	private LJObject object;
	
	private String fieldName;
	
	public LJInstanceField(LSymbol key) {

		fieldName = key.toString();
		
		fieldName = fieldName.substring(1, fieldName.length()).replace("$", "");
	}

	@Override
	public LObject evaluate(Environment environment, LObject tokens) {

		if(object == null) {
			
			try {
				
				LList tmp = (LList) tokens;

				object = (LJObject) tmp.get(0).run(environment, tokens);

				this.result = FieldUtils.readDeclaredField(object.getJObject(), fieldName);
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