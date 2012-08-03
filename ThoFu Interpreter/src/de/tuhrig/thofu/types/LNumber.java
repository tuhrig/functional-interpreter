package de.tuhrig.thofu.types;

import java.math.BigDecimal;

import de.tuhrig.thofu.Environment;

/**
 * Represents a number.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LNumber extends LObject { // LLeaf {

	/**
	 * 
	 */
	private BigDecimal value;

	/**
	 * @param value to store
	 */
	public LNumber(BigDecimal value) {

		this.value = value;
	}

	/**
	 * @param value to parse into a number
	 */
	public LNumber(String value) {

		this(new BigDecimal(value));
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#toString()
	 */
	@Override
	public String toString() {

		return value.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {

		if (object instanceof LNumber)
			return value.equals(((LNumber) object).value);

		return false;
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
	 * @see de.tuhrig.thofu.types.LObject#evaluate(de.tuhrig.thofu.Environment, de.tuhrig.thofu.types.LObject)
	 */
	@Override
	public LObject evaluate(Environment environment, LObject tokens) {

		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#sum(de.tuhrig.thofu.types.LObject)
	 */
	@Override
	public LObject sum(LObject object) {

		if(object instanceof LNumber)
			return new LNumber(value.add(((LNumber) object).value));
		
		else if(object instanceof LString)
			return new LString(value + object.toString());
		
		throw new LException("Can't sum " + this + " and " + object);
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#subtract(de.tuhrig.thofu.types.LObject)
	 */
	@Override
	public LObject subtract(LObject number) {

		if(number instanceof LNumber)
			return new LNumber(value.subtract(((LNumber) number).value));
		
		throw new LException("Can't subtract " + this + " and " + number);
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#multiply(de.tuhrig.thofu.types.LObject)
	 */
	@Override
	public LObject multiply(LObject number) {
		
		if(number instanceof LNumber)
			return new LNumber(value.multiply(((LNumber) number).value));
		
		throw new LException("Can't multiply " + this + " and " + number);
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#divide(de.tuhrig.thofu.types.LObject)
	 */
	@Override
	public LObject divide(LObject number) {

		if(number instanceof LNumber)
			return new LNumber(value.divide(((LNumber) number).value));
		
		throw new LException("Can't divide " + this + " and " + number);
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#compareTo(de.tuhrig.thofu.types.LObject)
	 */
	@Override
	public int compareTo(LObject number) {

		if(number instanceof LNumber)
			return value.compareTo(((LNumber) number).value);
		
		throw new LException("Can't compare " + this + " and " + number);
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