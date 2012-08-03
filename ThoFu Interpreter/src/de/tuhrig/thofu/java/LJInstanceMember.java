package de.tuhrig.thofu.java;

import org.apache.commons.lang3.reflect.MethodUtils;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.types.LException;
import de.tuhrig.thofu.types.LList;
import de.tuhrig.thofu.types.LObject;
import de.tuhrig.thofu.types.LSymbol;

/**
 * Represents an instance member.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LJInstanceMember extends LJava {

//	private LJObject object;
	
	private String methodName;

	public LJInstanceMember(LSymbol key) {
		
		methodName = key.toString();
		
		methodName = methodName.substring(1, methodName.length());
	}
	
	@Override
	public LObject evaluate(Environment environment, LObject tokens) {

//		if(object == null) {
			
			try {
				
				LList tmp = (LList) tokens;

				LObject obj = (LObject) tmp.get(0).run(environment, tokens);
				
				if(obj instanceof LJava) {
	
					Class<?>[] types = LJava.getConvertedTypes(tmp.getRest(), environment);

					Object[] args = LJava.getConvertedObjects(tmp.getRest(), environment);
	
					this.result = MethodUtils.invokeMethod(((LJava) obj).getJObject(), methodName, args, types);
				}
				else {	
					
					Class<?>[] types = LJava.getTypes(tmp.getRest(), environment);

					Object[] args = LJava.getObjects(tmp.getRest(), environment);

					this.result = MethodUtils.invokeMethod(obj, methodName, args, types);
				}
				
				if(result == null)
					result = "void";
			}
			catch (Exception e) {
				
				throw new LException("[" + e.getClass() + "] - " + e.getMessage(), e);
			}
//		}
		
		return new LJObject(result);
	}
	
	@Override
	public int argrumentSize(LObject tokens) {

		return -1;
	}
}