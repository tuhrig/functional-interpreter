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
	
	@Override
	public LObject evaluate(Environment environment, LObject tokens) {

		return this;
	}

	@Override
	public String toString() {

		return Literal.SINGLE_QUOTE + value;
	}

	@Override
	public boolean equals(Object o) {

		if (o instanceof LQuoted) {
			
			return value.equals(((LQuoted) o).value);
		}
		
		return false;
	}

	@Override
	public int hashCode() {

		return System.identityHashCode(this);
	}

	@Override
	public int argrumentSize(LObject object) {

		return 1;
	}
}