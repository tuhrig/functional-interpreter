package de.tuhrig.fujas.types;

import java.math.BigDecimal;

import de.tuhrig.fujas.Environment;

public class LNumber extends LObject { // LLeaf {

	private final BigDecimal value;

	LNumber(BigDecimal value) {

		this.value = value;
	}

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
	public LObject eval(Environment environment, LObject tokens) {

		return this;
	}

	public LNumber add(LNumber n) {

		return new LNumber(value.add(n.value));
	}

	public LNumber subtract(LNumber n) {

		return new LNumber(value.subtract(n.value));
	}

	public LNumber multiply(LNumber n) {

		return new LNumber(value.multiply(n.value));
	}

	public LNumber divide(LNumber n) {

		return new LNumber(value.divide(n.value));
	}

	public int compareTo(LNumber n) {

		return value.compareTo(n.value);
	}
}