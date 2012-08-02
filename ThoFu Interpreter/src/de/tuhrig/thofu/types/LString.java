package de.tuhrig.thofu.types;

import de.tuhrig.thofu.Environment;

/**
 * Represents a string.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LString extends LObject { // LLeaf {

	/**
	 * 
	 */
	private String value;

	/**
	 * The given token will be stored in its string representation
	 * by calling token.toString(). Also, all quotes will be removed.
	 * 
	 * @param token to store as a string
	 */
	public LString(Object token) {

		this.value = token.toString().replaceAll("\"", "");
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#sum(de.tuhrig.thofu.types.LObject)
	 */
	public LObject sum(LObject object) {
		
		return new LString(value + object.toString());
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#compareTo(de.tuhrig.thofu.types.LObject)
	 */
	public int compareTo(LObject object) {

		return value.compareTo(object.toString());
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#toString()
	 */
	public String toString() {

		return value;
	}
 
	/**
	 * @return string value
	 */
	public String getValue() {

		return value;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {

		if (o instanceof LString)
			return value.equals(((LString) o).value);

		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#evaluate(de.tuhrig.thofu.Environment, de.tuhrig.thofu.types.LObject)
	 */
	@Override
	public LObject evaluate(Environment environment, LObject tokens) {

		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#hashCode()
	 */
	@Override
	public int hashCode() {

		return value.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#argrumentSize(de.tuhrig.thofu.types.LObject)
	 */
	@Override
	public int argrumentSize(LObject object) {

		return 1;
	}
}