package de.tuhrig.thofu.types;

import java.util.ArrayList;
import java.util.List;

import de.tuhrig.thofu.Environment;

/**
 * Represents a symbol (e.g. a variable).
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LSymbol extends LObject {

	private final String value;

	private final static List<LSymbol> symbols = new ArrayList<LSymbol>();

	/**
	 * every symbol can only exist once. This method will return
	 * the existing symbol for the given object. If no symbol 
	 * exists, it will create a new one.
	 * 
	 * @param value to resolve to a symbol
	 * @return symbol for the object
	 */
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
	
	public String inspect() {

		StringBuilder builder = new StringBuilder();

		builder.append(super.inspect());

		builder.append(
				"\n" +
				"Value:\t\t" 	+ value
				);
		
		return builder.toString();
	}

	@Override
	public int argrumentSize(LObject object) {

		return 1;
	}
}