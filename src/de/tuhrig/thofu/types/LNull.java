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
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#toString()
	 */
	@Override
	public String toString() {

		return "'(null)";
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
	 * @see de.tuhrig.thofu.types.LObject#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {

		if (o == NULL)
			return true;

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