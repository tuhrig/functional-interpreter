package de.tuhrig.thofu.types;

import de.tuhrig.thofu.Environment;

public class LString extends LObject { // LLeaf {

	public String value;

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
	public LObject evaluate(Environment environment, LObject tokens) {

		return this;
	}

	@Override
	public int hashCode() {

		return value.hashCode();
	}
}