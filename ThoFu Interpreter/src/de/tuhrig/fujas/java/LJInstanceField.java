package de.tuhrig.fujas.java;

import org.apache.commons.lang3.reflect.FieldUtils;

import de.tuhrig.fujas.Environment;
import de.tuhrig.fujas.types.LException;
import de.tuhrig.fujas.types.LList;
import de.tuhrig.fujas.types.LObject;
import de.tuhrig.fujas.types.LSymbol;

public class LJInstanceField extends LJava {

	private LJObject object;
	
	private String fieldName;
	
	public LJInstanceField(LSymbol key) {

		fieldName = key.toString();
		
		fieldName = fieldName.substring(1, fieldName.length()).replace("$", "");
	}

	@Override
	public LObject eval(Environment environment, LObject tokens) {

		if(object == null) {
			
			try {
				
				LList tmp = (LList) tokens;

				object = (LJObject) tmp.get(0).eval(environment, tokens);

				this.result = FieldUtils.readDeclaredField(object.getJObject(), fieldName);
			}
			catch (Exception e) {

				throw new LException("[" + e.getClass() + "] - " + e.getMessage());
			}
		}
		
		return this;
	}
}