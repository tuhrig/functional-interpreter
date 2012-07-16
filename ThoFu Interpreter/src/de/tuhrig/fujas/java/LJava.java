package de.tuhrig.fujas.java;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import de.tuhrig.fujas.Environment;
import de.tuhrig.fujas.types.LBoolean;
import de.tuhrig.fujas.types.LException;
import de.tuhrig.fujas.types.LLambda;
import de.tuhrig.fujas.types.LList;
import de.tuhrig.fujas.types.LNumber;
import de.tuhrig.fujas.types.LObject;
import de.tuhrig.fujas.types.LQuoted;
import de.tuhrig.fujas.types.LString;
import de.tuhrig.fujas.types.LSymbol;

public abstract class LJava extends LObject {

	protected Object result;

	private final static List<String> importedClasses = new ArrayList<>();
	
	private final static List<String> importedPackages = new ArrayList<>();
	
	public Object getJObject() {

		return result;
	}

	public Class<? extends Object> getJClass() {

		return result.getClass();
	}
	
	@Override
	public String toString() {

		return result.toString();
	}
	
	@Override
	public int hashCode() {

		return result.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {

		return result.equals(o);
	}
	
	public static Class<?>[] getTypes(LObject tokens, Environment environment) {
		
		LList list = (LList) tokens;
		
		Class<?>[] types = new Class[list.size()];
		
		for(int i = 0; i < list.size(); i ++) {
			
			Object o = list.get(i);
			
			if(o instanceof LSymbol) {
				
				o = environment.get((LSymbol) o);
			}
			
			if(o instanceof LString) {
				
				types[i] = (String.class);
			}
			else if(o instanceof LNumber) {
				
				types[i] = (String.class);
			}
			else if(o instanceof LBoolean) {
				
				types[i] = (Boolean.class);
			}
			else if(o instanceof LJava) {

				o = ((LJava) o).eval(environment, tokens);
				
				types[i] = (((LJava) o).getJClass());
			}
			else {
				
				throw new LException("[unsupported type] - " + o.getClass());
			}
		}
		
		return types;
	}
	
	public static Object[] getObjects(LObject tokens, Environment environment) {
		
		LList list = (LList) tokens;
		
		Object[] args = new Object[list.size()];
		
		for(int i = 0; i < list.size(); i ++) {
			
			Object o = list.get(i);
			
			if(o instanceof LSymbol) {
				
				o = environment.get((LSymbol) o);
			}
			
			if(o instanceof LString) {
				
				args[i] = ((LString) o).getValue(); 
			}
			else if(o instanceof LNumber) {
				
				args[i] = o.toString(); 
			}
			else if(o instanceof LBoolean) {
				
				args[i] = ((LBoolean) o).getValue();
			}
			else if(o instanceof LJava) {
				
				o = ((LJava) o).eval(environment, tokens);
				
				args[i] = (((LJava) o).getJObject());
			}
			else {
				
				throw new LException("[unsupported object] - " + o.getClass());
			}
		}
		
		return args;
	}

	public static Class<?> getClass(String className) {

		try {
			
			return Class.forName(className);
		}
		catch (ClassNotFoundException e) {

			for(String tmp: importedClasses) {
				
				String importedClassName = tmp.substring(tmp.lastIndexOf(".") + 1, tmp.length());
				
				if(importedClassName.equals(className)) {
					
					try {
						
						return Class.forName(tmp);
					}
					catch (ClassNotFoundException e1) {

						throw new LException("[" + e.getClass() + "] - " + e.getMessage());
					}
				}
			}
			
			for(String tmp: importedPackages) {

				try {
					
					return Class.forName(tmp + "." + className);
				}
				catch (ClassNotFoundException e1) {

					//
				}
			}
			
			throw new LException("[" + e.getClass() + "] - " + e.getMessage());
		}
	}
	
	public static void importClass(String className) {
		
		importedClasses.add(className);
	}
	
	public static void importPackage(String packageName) {
	
		importedPackages.add(packageName);
	}
	
	public static LJObject createInterface(final Class<?> c, final LLambda lambda, final Environment environment) {

		try {

			ProxyFactory factory = new ProxyFactory();

			factory.setInterfaces(new Class[] { c });

			MethodHandler handler = handler(c, lambda, environment);

			Class<?> interfaceClass = factory.createClass();

			Object object = interfaceClass.newInstance();

			((ProxyObject) object).setHandler(handler);

			return new LJObject(object);
		}
		catch (Exception e) {

			throw new LException("[" + e.getClass() + "] - " + e.getMessage());
		}
	}

	public static LObject createClass(final Class<?> c, final LLambda lambda, final Environment environment) {

		try {

			ProxyFactory factory = new ProxyFactory();

			factory.setSuperclass(c);

			MethodHandler handler = handler(c, lambda, environment);

			Class<?> interfaceClass = factory.createClass();

			Object object = interfaceClass.newInstance();

			((ProxyObject) object).setHandler(handler);

			return new LJObject(object);
		}
		catch (Exception e) {

			throw new LException("[" + e.getClass() + "] - " + e.getMessage());
		}
	}

	private static MethodHandler handler(final Class<?> c, final LLambda lambda, final Environment environment) {

		return new MethodHandler() {

			@Override
			public Object invoke(Object arg0, Method method, Method arg2, Object[] args) throws Throwable {

				String name = method.getName();

				Class<?>[] types = method.getParameterTypes();

				if (name.equals("toString") && types.length == 0) {

					return "<Proxy: " + c.getName() + ">";
				}

				LSymbol str = LSymbol.get(name);
				LQuoted qut = new LQuoted(str);

				LList list = new LList();
				list.add(qut);

				LObject result = lambda.eval(environment, list);

				list = new LList();

				for (Object o : args)
					list.add(new LJObject(o));

				return result.eval(environment, list);
			}
		};
	}
}