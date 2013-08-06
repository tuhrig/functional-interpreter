package de.tuhrig.thofu.types;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.Literal;

/**
 * Represents a boolean.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LBoolean extends LObject {

	/**
	 * The true boolean instance
	 */
	public static final LBoolean TRUE = new LBoolean();
	
	/**
	 * The false boolean instance
	 */
	public static final LBoolean FALSE = new LBoolean();
	
	private LBoolean() {
		
		// singleton
	}
	
	/**
	 * @param condition to convert into a boolean
	 * @return the true boolean instance for true and the false boolean instance for false
	 */
	public static LBoolean get(boolean condition) {
		
		if(condition)
			return TRUE;
		
		return FALSE;
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

		if(this == TRUE)
			return Literal.TRUE;

		return Literal.FALSE;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {

		if (o instanceof LBoolean) {
			
			return this == o;
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

	/**
	 * @return a Java boolean according to the instance
	 */
	public Object getValue() {

		if(this == TRUE)
			return Boolean.valueOf(true);
		
		return Boolean.valueOf(false);
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