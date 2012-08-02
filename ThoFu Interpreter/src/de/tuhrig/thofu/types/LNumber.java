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
	public BigDecimal value;

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

	public String toString() {

		return value.toString();
	}

	public boolean equals(Object object) {

		if (object instanceof LNumber)
			return value.equals(((LNumber) object).value);

		return false;
	}
	
	@Override
	public int hashCode() {

		return value.hashCode();
	}

	@Override
	public LObject evaluate(Environment environment, LObject tokens) {

		return this;
	}

	/**
	 * @param number to add
	 * @return calculation result as a new number instance
	 */
	public LObject sum(LObject number) {

		if(number instanceof LNumber)
			return new LNumber(value.add(((LNumber) number).value));
		
		throw new LException("Can't sum " + this + " and " + number);
	}

	/**
	 * @param number to subtract
	 * @return calculation result as a new number instance
	 */
	public LObject subtract(LObject number) {

		if(number instanceof LNumber)
			return new LNumber(value.subtract(((LNumber) number).value));
		
		throw new LException("Can't subtract " + this + " and " + number);
	}

	/**
	 * @param number to multiply
	 * @return calculation result as a new number instance
	 */
	public LObject multiply(LObject number) {
		
		if(number instanceof LNumber)
			return new LNumber(value.multiply(((LNumber) number).value));
		
		throw new LException("Can't multiply " + this + " and " + number);
	}

	/**
	 * @param number to divide
	 * @return calculation result as a new number instance
	 */
	public LObject divide(LObject number) {

		if(number instanceof LNumber)
			return new LNumber(value.divide(((LNumber) number).value));
		
		throw new LException("Can't divide " + this + " and " + number);
	}

	/**
	 * @param number to compare
	 * @return calculation result as a new number instance
	 */
	public int compareTo(LObject number) {

		if(number instanceof LNumber)
			return value.compareTo(((LNumber) number).value);
		
		throw new LException("Can't compare " + this + " and " + number);
	}

	@Override
	public int argrumentSize(LObject object) {

		return 1;
	}
}