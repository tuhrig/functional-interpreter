package de.tuhrig.thofu.types;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.Literal;


/**
 * Represents an abstract operation. It's the super class
 * of a user-defined lambda and of the build-in operations.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public abstract class LOperation extends LObject { // extends LLeaf {

	protected String name;

	/**
	 * @param name for the operation, e.g. "plus"
	 */
	public LOperation(String name) {

		this.name = name;
	}

	/**
	 * @return name of the operation
	 */
	public LSymbol getName() {
		
		return LSymbol.get(name);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#sum(de.tuhrig.thofu.types.LObject)
	 */
	public LObject sum(final LObject object) {

		// return a new operation that contains the original two
		return new LOperation(this.toString() + object.toString()) {
			
			@Override
			public LObject evaluate(Environment environment, LObject tokens) {
			
				// call itself first
				LOperation.this.evaluate(environment, tokens);
				
				// return the argument last
				return object.evaluate(environment, tokens);
			}
		};
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#toString()
	 */
	public String toString() {

		return "<Operation: " + name + ">";
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {

		if (o instanceof LOperation) {

			return name.equals(((LOperation) o).name);
		}

		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#hashCode()
	 */
	public int hashCode() {
		
		return name.hashCode();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#argrumentSize(de.tuhrig.thofu.types.LObject)
	 */
	public int argrumentSize(LObject object) {
		
		LList list = (LList) object;
		
		return list.size() + 1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.types.LObject#inspect()
	 */
	public String inspect() {

		StringBuilder builder = new StringBuilder();

		builder.append(super.inspect());

		builder.append(
				Literal.NL +
				"Name:\t\t" 	+ getName()
				);
		
		return builder.toString();
	}
}