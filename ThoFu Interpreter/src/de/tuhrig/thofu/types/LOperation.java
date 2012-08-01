package de.tuhrig.thofu.types;

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
	
	public String toString() {

		return "<Operation: " + name + ">";
	}

	public boolean equals(Object o) {

		if (o instanceof LOperation) {

			return name.equals(((LOperation) o).name);
		}

		return false;
	}
	
	public int hashCode() {
		
		return name.hashCode();
	}
	
	public int argrumentSize(LObject object) {
		
		LList list = (LList) object;
		
		return list.size() + 1;
	}
	
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