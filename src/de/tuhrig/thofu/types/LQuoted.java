package de.tuhrig.thofu.types;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.Literal;

/**
 * Represents a quoted value that won't be evaluated.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LQuoted extends LObject { // LLeaf {

	/**
	 * 
	 */
	public LObject value;

	/**
	 * @param value to wrap
	 */
	public LQuoted(LObject value) {

		this.value = value;
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
	 * @see de.tuhrig.thofu.types.LObject#toString()
	 */
	@Override
	public String toString() {

		return Literal.SINGLE_QUOTE + value;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {

		if (o instanceof LQuoted) {
			
			return value.equals(((LQuoted) o).value);
		}
		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#hashCode()
	 */
	@Override
	public int hashCode() {

		return System.identityHashCode(this);
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