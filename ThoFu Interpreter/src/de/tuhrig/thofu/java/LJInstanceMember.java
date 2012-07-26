package de.tuhrig.thofu.java;

import org.apache.commons.lang3.reflect.MethodUtils;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.types.LException;
import de.tuhrig.thofu.types.LList;
import de.tuhrig.thofu.types.LObject;
import de.tuhrig.thofu.types.LSymbol;

public class LJInstanceMember extends LJava {

	private LJObject object;
	
	private String methodName;

	public LJInstanceMember(LSymbol key) {
		
		methodName = key.toString();
		
		methodName = methodName.substring(1, methodName.length());
	}
	
	@Override
	public LObject evaluate(Environment environment, LObject tokens) {

		if(object == null) {
			
			try {
				
				LList tmp = (LList) tokens;

				object = (LJObject) tmp.get(0).run(environment, tokens);

				Class<?>[] types = LJava.getTypes(tmp.getRest(), environment);

				Object[] args = LJava.getObjects(tmp.getRest(), environment);
	
				this.result = MethodUtils.invokeMethod(object.getJObject(), methodName, args, types);

				if(result == null)
					result = "void";
			}
			catch (Exception e) {
				
				throw new LException("[" + e.getClass() + "] - " + e.getMessage());
			}
		}
		
		return this;
	}
	
	@Override
	public int argrumentSize(LObject tokens) {

		return -1;
	}
}