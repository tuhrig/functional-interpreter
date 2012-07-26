package de.tuhrig.thofu.types;

import de.tuhrig.thofu.Environment;

/**
 * Represents null.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LNull extends LObject {

	/**
	 * The null instance
	 */
	public final static LNull NULL = new LNull();

	private LNull() {
		
		// singleton
	}
	
	public String toString() {

		return "'(null)";
	}

	@Override
	public LObject evaluate(Environment environment, LObject tokens) {

		return this;
	}
	
	public boolean equals(Object o) {

		if (o == NULL)
			return true;

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