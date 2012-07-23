package de.tuhrig.thofu.types;

import de.tuhrig.thofu.Environment;

public class LNull extends LObject {

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
	
//	@Override
//	public boolean contains(Object o) {
//		
//		return false;
//	}
	
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