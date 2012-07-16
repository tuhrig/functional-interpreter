package de.tuhrig.fujas.types;

import de.tuhrig.fujas.Environment;

public class LString extends LObject { // LLeaf {

	private final String value;

	public LString(Object token) {

		this.value = token.toString().replaceAll("\"", "");
	}

	public String toString() {

		return "\"" + value + "\"";
	}

	public String getValue() {

		return value;
	}
	
	public boolean equals(Object o) {

		if (o instanceof LString)
			return value.equals(((LString) o).value);

		return false;
	}

	@Override
	public LObject eval(Environment environment, LObject tokens) {

		return this;
	}

	@Override
	public int hashCode() {

		return value.hashCode();
	}
}