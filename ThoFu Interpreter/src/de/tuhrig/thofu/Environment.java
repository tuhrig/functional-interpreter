package de.tuhrig.thofu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.tuhrig.thofu.java.LJClass;
import de.tuhrig.thofu.java.LJInstanceField;
import de.tuhrig.thofu.java.LJInstanceMember;
import de.tuhrig.thofu.java.LJObject;
import de.tuhrig.thofu.java.LJStaticField;
import de.tuhrig.thofu.java.LJStaticMember;
import de.tuhrig.thofu.types.LException;
import de.tuhrig.thofu.types.LObject;
import de.tuhrig.thofu.types.LSymbol;

/**
 * The implementation of the environment. The environment holds all
 * operations, lambdas and variables in a single list. The list contains
 * a key and a corresponding value. 
 * 
 * An environment can have a parent environment.  
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class Environment {

	private final Environment parent;

	/**
	 * Mapping of variables to objects
	 */
	private final Map<LSymbol, Container> objects = new HashMap<>();

	/**
	 * Counts all created environments
	 */
	private static int count = 0;

	/**
	 * serial
	 */
	private final int serial;

	/**
	 * The lambda that holds the environment
	 */
	private String lambda = null;

	/**
	 * Creates a new environment with a parent.
	 * 
	 * @param environment to use as parent
	 */
	public Environment(Environment environment) {

		count++;

		serial = count;

		this.parent = environment;
	}

	/**
	 * Creates a new environment with a parent and a surrounding lambda
	 * (so the created environment is probably an inner environment).
	 * 
	 * @param environment to use as parent
	 * @param lambda holding the environment
	 */
	public Environment(Environment environment, String lambda) {

		this(environment);
		
		this.lambda  = lambda;
	}

	/**
	 * This method will look-up the given key. It will first look
	 * in its own list. If it doesn't contain the key, the environemnt
	 * will ask it parent (and so on).
	 * 
	 * @param key to look-up
	 * @return true if the environment or its parent contain the key
	 */
	public boolean contains(LSymbol key) {

		if (objects.containsKey(key))
			return true;

		else if (parent != null)
			return parent.contains(key);
		
		return false;
	}

	/**
	 * This method will look-up the given key. It will first look
	 * in its own list. If it doesn't contain the key, the environemnt
	 * will ask it parent (and so on).
	 * 
	 * @param key to look-up
	 * @return the corresponding value to the key
	 */
	public LObject get(LSymbol key) {

		if (objects.containsKey(key))
			return objects.get(key).getObject();

		else if (parent != null)
			return parent.get(key);

		/**
		 * Get Java Methods (etc.) here
		 */
		{
			if (key.toString().endsWith(".")) {

				return new LJObject(key);
			}
			else if (key.toString().endsWith(".class")) {

				return new LJClass(key);
			}
			else if (key.toString().startsWith(".") && key.toString().endsWith("$")) {

				return new LJInstanceField(key);
			}
			else if (key.toString().startsWith(".")) {

				return new LJInstanceMember(key);
			}
			else if (key.toString().endsWith("$")) {

				return new LJStaticField(key);
			}
			else if (key.toString().contains(".")) {

				return new LJStaticMember(key);
			}

			// not implemented:
			// "$" in the middle inner class java.awt.geom.Point2D$Double.class
			// "$" at the beginning packageless class $ParseDemo.class
			// "#" at the end allow private access (.name$# (Symbol.# "abc"))
		}

		throw new LException("[symbol not found] - symbol " + key + " can't be resolved");
	}

	/**
	 * @param key for the value
	 * @param value for the key
	 */
	public void put(LSymbol key, LObject value) {

		objects.put(key, new Container(value));
	}
	

	/**
	 * @param key for the value
	 * @param value for the key
	 */
	public void put(LSymbol key, Container value) {

		objects.put(key, value);
	}

	@Override
	public String toString() {

		List<String> envs = new ArrayList<>();
		
		envs.add(serial + " - " + objects.toString());

		Environment tmp = parent;
		
		while(tmp != null)
		
		if (parent != null) {
			
			envs.add(tmp.serial + " - " + tmp.objects);
			
			tmp = tmp.parent;
		}

		StringBuilder formated = new StringBuilder();

		if(lambda != null)
			formated.append("Holded by " + lambda + Literal.NL);
		
		String place = "";
		
		for(int i = 0; i < envs.size(); i++) {
			
			formated.append(place + envs.get(i) + Literal.NL);
			place += Literal.TP;
		}
		
		return formated.toString().trim();
	}

	/**
	 * @return an entrySet for the current key-value-pairs
	 */
	public Set<Entry<LSymbol, Container>> entrySet() {

		return objects.entrySet();
	}

	/**
	 * This method will replace the current value of the given key
	 * with the new given value. If the key doesn't exist, the 
	 * method will do nothing. 
	 * 
	 * @param key to set a new value for
	 * @param value to replace the old value with
	 */
	public void set(LSymbol key, LObject value) {

		if (objects.containsKey(key)) {

			Container tmp = objects.get(key);
			
			tmp.setObject(value);
		}

		else if (parent != null) {
			
			parent.set(key, value);
		}
	}
}