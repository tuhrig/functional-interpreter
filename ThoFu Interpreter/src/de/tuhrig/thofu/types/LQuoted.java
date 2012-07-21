package de.tuhrig.thofu.types;

import de.tuhrig.thofu.Environment;

public class LQuoted extends LObject { // LLeaf {

	private final LObject value;

	public LQuoted(LObject value) {

		this.value = value;
	}
	
	@Override
	public LObject eval(Environment environment, LObject tokens) {

		return this;
	}

	@Override
	public String toString() {

		return "'" + value;
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
}