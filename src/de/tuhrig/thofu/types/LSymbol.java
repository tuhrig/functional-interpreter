package de.tuhrig.thofu.types;

import java.util.ArrayList;
import java.util.List;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.Literal;

/**
 * Represents a symbol (e.g. a variable).
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class LSymbol extends LObject {

	private final String value;

	private final static List<LSymbol> symbols = new ArrayList<LSymbol>();

	/**
	 * Every symbol can only exist once. This method will return
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

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#toString()
	 */
	@Override
	public String toString() {

		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {

		return this == object;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#hashCode()
	 */
	@Override
	public int hashCode() {
		
		return value.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#evaluate(de.tuhrig.thofu.Environment, de.tuhrig.thofu.types.LObject)
	 */
	@Override
	public LObject evaluate(Environment environment, LObject tokens) {

		return environment.get(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#inspect()
	 */
	@Override
	public String inspect() {

		StringBuilder builder = new StringBuilder();

		builder.append(super.inspect());

		builder.append(
				Literal.NL +
				"Value:\t\t" 	+ value
				);
		
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#argrumentSize(de.tuhrig.thofu.types.LObject)
	 */
	@Override
	public int argrumentSize(LObject object) {

		return 1;
	}
}