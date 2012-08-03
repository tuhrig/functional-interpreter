package de.tuhrig.thofu.java;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.Literal;
import de.tuhrig.thofu.types.LBoolean;
import de.tuhrig.thofu.types.LException;
import de.tuhrig.thofu.types.LLambda;
import de.tuhrig.thofu.types.LList;
import de.tuhrig.thofu.types.LNumber;
import de.tuhrig.thofu.types.LObject;
import de.tuhrig.thofu.types.LQuoted;
import de.tuhrig.thofu.types.LString;
import de.tuhrig.thofu.types.LSymbol;

/**
 * Represents an abstract Java object. It's the super class of 
 * all Java objects. It provides also some utility methods.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public abstract class LJava extends LObject {

	protected Object result;

	private final static List<String> importedClasses = new ArrayList<>();
	
	private final static List<String> importedPackages = new ArrayList<>();
	
	/**
	 * @return wrapped object
	 */
	public Object getJObject() {

		return result;
	}

	/**
	 * @return Java class of the wrapped object
	 */
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

		if(o instanceof LJObject)
			return result.equals(((LJObject) o).result);
		
		return result.equals(o);
	}

	public static Class<?>[] getConvertedTypes(LObject tokens, Environment environment) {
		
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

				o = ((LJava) o).run(environment, tokens);
				
				types[i] = (((LJava) o).getJClass());
			}
			else {
				
				throw new LException(Literal.LEFT_BRAKET + "unsupported type] - " + o.getClass());
			}
		}
		
		return types;
	}
	
	public static Object[] getConvertedObjects(LObject tokens, Environment environment) {
		
		LList list = (LList) tokens;
		
		Object[] args = new Object[list.size()];
		
		for(int i = 0; i < list.size(); i ++) {
			
			Object o = list.get(i);
			
			if(o instanceof LSymbol) {
				
				o = environment.get((LSymbol) o);
			}
			
			if(o instanceof LString) {
				
				args[i] = o.toString();
			}
			else if(o instanceof LNumber) {
				
				args[i] = o.toString();
			}
			else if(o instanceof LBoolean) {
				
				args[i] = ((LBoolean) o).getValue();
			}
			else if(o instanceof LJava) {
				
				o = ((LJava) o).run(environment, tokens);
				
				args[i] = (((LJava) o).getJObject());
			}
			else {
				
				throw new LException(Literal.LEFT_BRAKET + "unsupported object] - " + o.getClass());
			}
		}
		
		return args;
	}
	
	public static Class<?>[] getTypes(LObject tokens, Environment environment) {
		
		LList list = (LList) tokens;
		
		Class<?>[] types = new Class[list.size()];
		
		for(int i = 0; i < list.size(); i ++) {
			
			Object o = list.get(i);
			
			if(o instanceof LSymbol) {
				
				o = environment.get((LSymbol) o);
			}
			
			if(o instanceof LJava) {

				o = ((LJava) o).run(environment, tokens);
			}
				
			types[i] = o.getClass();
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
			
			if(o instanceof LJava) {
				
				o = ((LJava) o).run(environment, tokens);
			}
				
			args[i] = o;
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

						throw new LException(Literal.LEFT_BRAKET + e.getClass() + "] - " + e.getMessage());
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
			
			throw new LException(Literal.LEFT_BRAKET + e.getClass() + "] - " + e.getMessage());
		}
	}
	
	/**
	 * @param className to import
	 */
	public static void importClass(String className) {
		
		importedClasses.add(className);
	}
	
	/**
	 * @param packageName to import
	 */
	public static void importPackage(String packageName) {
	
		importedPackages.add(packageName);
	}
	
	/**
	 * @param c
	 * @param lambda
	 * @param environment
	 * @return new Java object wrapper
	 */
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

			throw new LException(Literal.LEFT_BRAKET + e.getClass() + "] - " + e.getMessage());
		}
	}

	/**
	 * @param c
	 * @param lambda
	 * @param environment
	 * @return new Java class
	 */
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

			throw new LException(Literal.LEFT_BRAKET + e.getClass() + "] - " + e.getMessage());
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

				LObject result = lambda.run(environment, list);

				list = new LList();

				for (Object o : args)
					list.add(new LJObject(o));

				return result.run(environment, list);
			}
		};
	}
}