package de.tuhrig.fujas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.tuhrig.fujas.java.LJClass;
import de.tuhrig.fujas.java.LJInstanceField;
import de.tuhrig.fujas.java.LJInstanceMember;
import de.tuhrig.fujas.java.LJObject;
import de.tuhrig.fujas.java.LJStaticField;
import de.tuhrig.fujas.java.LJStaticMember;
import de.tuhrig.fujas.types.LException;
import de.tuhrig.fujas.types.LObject;
import de.tuhrig.fujas.types.LSymbol;

/**
 * @author Thomas Uhrig (tuhrig.de)
 */
public class Environment {

	private final Environment parent;

	private final Map<LSymbol, LObject> objects = new HashMap<LSymbol, LObject>();

	private static int count = 0;

	private final int serial;

	public Environment(Environment environment) {

		count++;

		serial = count;

		this.parent = environment;
	}

	public boolean contains(LSymbol key) {

		if (objects.containsKey(key))
			return true;

		else if (parent != null)
			return parent.contains(key);
		
		return false;
	}

	public LObject get(LSymbol key) {

		if (objects.containsKey(key))
			return objects.get(key);

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

			// "$" in the middle inner class java.awt.geom.Point2D$Double.class
			// "$" at the beginning packageless class $ParseDemo.class
			// "#" at the end allow private access (.name$# (Symbol.# "abc"))
		}

		throw new LException("[symbol not found] - symbol " + key + " can't be resolved");
	}

	public void put(LSymbol symbole, LObject object) {

		objects.put(symbole, object);
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

		String formated = "";

		String place = "";
		
		for(int i = 0; i < envs.size(); i++) {
			
			formated += place + envs.get(i) + "\n";
			place += "\t";
		}
		
		return formated;
	}

	public Set<Entry<LSymbol, LObject>> entrySet() {

		return objects.entrySet();
	}

	public void set(LSymbol key, LObject value) {

		if (objects.containsKey(key)) {

			objects.put(key, value);
		}

		else if (parent != null) {
			
			parent.set(key, value);
		}
	}

//	public Environment copy() {
//
//		Environment copy = new Environment(parent);
//
//		for (Entry<LSymbol, LObject> e : objects.entrySet()) {
//
//			copy.put(e.getKey(), e.getValue());
//		}
//
//		return copy;
//	}
}