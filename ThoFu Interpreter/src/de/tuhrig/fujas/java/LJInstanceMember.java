package de.tuhrig.fujas.java;

import org.apache.commons.lang3.reflect.MethodUtils;

import de.tuhrig.fujas.Environment;
import de.tuhrig.fujas.types.LException;
import de.tuhrig.fujas.types.LList;
import de.tuhrig.fujas.types.LObject;
import de.tuhrig.fujas.types.LSymbol;

public class LJInstanceMember extends LJava {

	private LJObject object;
	
	private String methodName;

	public LJInstanceMember(LSymbol key) {
		
		methodName = key.toString();
		
		methodName = methodName.substring(1, methodName.length());
	}
	
	@Override
	public LObject eval(Environment environment, LObject tokens) {

		if(object == null) {
			
			try {
				
				LList tmp = (LList) tokens;

				object = (LJObject) tmp.get(0).eval(environment, tokens);

				Class<?>[] types = LJava.getTypes(tmp.getRest(), environment);

				Object[] args = LJava.getObjects(tmp.getRest(), environment);
	
				this.result = MethodUtils.invokeMethod(object.getJObject(), methodName, args, types);

				if(result == null)
					result = "void";
			}
			catch (Exception e) {

				e.printStackTrace();
				
				throw new LException("[" + e.getClass() + "] - " + e.getMessage());
			}
		}
		
		return this;
	}
}