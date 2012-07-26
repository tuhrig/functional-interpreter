package de.tuhrig.thofu.types;

import de.tuhrig.thofu.Environment;

/**
 * Represents string.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LString extends LObject { // LLeaf {

	/**
	 * 
	 */
	public String value;

	/**
	 * The given token will be stored in its string representation
	 * by calling token.toString(). Also, all quotes will be removed.
	 * 
	 * @param token to store as a string
	 */
	public LString(Object token) {

		this.value = token.toString().replaceAll("\"", "");
	}
	
	public String toString() {

		return "\"" + value + "\"";
	}

	/**
	 * @return string value
	 */
	public String getValue() {

		return value;
	}
	
	public boolean equals(Object o) {

		if (o instanceof LString)
			return value.equals(((LString) o).value);

		return false;
	}

	@Override
	public LObject evaluate(Environment environment, LObject tokens) {

		return this;
	}

	@Override
	public int hashCode() {

		return value.hashCode();
	}

	@Override
	public int argrumentSize(LObject object) {

		return 1;
	}
}