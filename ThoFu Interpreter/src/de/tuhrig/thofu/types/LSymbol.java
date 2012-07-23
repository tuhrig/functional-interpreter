package de.tuhrig.thofu.types;

import java.util.ArrayList;
import java.util.List;

import de.tuhrig.thofu.Environment;

public class LSymbol extends LObject {

	private final String value;

	private final static List<LSymbol> symbols = new ArrayList<LSymbol>();

	public static LSymbol get(Object value) {

		for (LSymbol tmp : symbols) {

			if (tmp.value.equals(value.toString())) {
				
				return tmp;
			}
		}

		LSymbol symbol = new LSymbol(value);
		
		symbols.add(symbol);

		return symbol;
	}

	private LSymbol(Object value) {

		this.value = value.toString();
	}

	public String toString() {

		return value;
	}

	public boolean equals(Object object) {

		return this == object;
	}

	public int hashCode() {
		
		return value.hashCode();
	}

	public LObject evaluate(Environment environment, LObject tokens) {

		return environment.get(this);
	}
}