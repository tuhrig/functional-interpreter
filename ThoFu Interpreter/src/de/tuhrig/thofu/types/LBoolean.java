package de.tuhrig.thofu.types;

import de.tuhrig.thofu.Environment;

public class LBoolean extends LObject { // LLeaf {

	public static final LBoolean TRUE = new LBoolean();
	
	public static final LBoolean FALSE = new LBoolean();
	
	private LBoolean() {
		
		// singleton
	}
	
	public static LBoolean get(boolean condition) {
		
		if(condition)
			return TRUE;
		
		return FALSE;
	}

	@Override
	public LObject evaluate(Environment environment, LObject tokens) {

		return this;
	}

	@Override
	public String toString() {

		if(this == TRUE)
			return "true";

		return "false";
	}

	@Override
	public boolean equals(Object o) {

		if (o instanceof LBoolean) {
			
			return this == o;
		}
			
		return false;
	}

	@Override
	public int hashCode() {

		return System.identityHashCode(this);
	}

	public Object getValue() {

		if(this == TRUE)
			return Boolean.valueOf(true);
		
		return Boolean.valueOf(false);
	}

	@Override
	public int argrumentSize(LObject object) {

		return 1;
	}
}