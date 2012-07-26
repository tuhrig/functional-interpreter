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
	public LNumber add(LNumber number) {

		return new LNumber(value.add(number.value));
	}

	/**
	 * @param number to subtract
	 * @return calculation result as a new number instance
	 */
	public LNumber subtract(LNumber number) {

		return new LNumber(value.subtract(number.value));
	}

	/**
	 * @param number to multiply
	 * @return calculation result as a new number instance
	 */
	public LNumber multiply(LNumber number) {

		return new LNumber(value.multiply(number.value));
	}

	/**
	 * @param number to divide
	 * @return calculation result as a new number instance
	 */
	public LNumber divide(LNumber number) {

		return new LNumber(value.divide(number.value));
	}

	/**
	 * @param number to compare
	 * @return calculation result as a new number instance
	 */
	public int compareTo(LNumber number) {

		return value.compareTo(number.value);
	}

	@Override
	public int argrumentSize(LObject object) {

		return 1;
	}
}