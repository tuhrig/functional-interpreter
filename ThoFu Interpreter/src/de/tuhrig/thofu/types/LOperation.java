package de.tuhrig.thofu.types;

public abstract class LOperation extends LObject { // extends LLeaf {

	protected String name;

	public LOperation(String name) {

		this.name = name;
	}

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
		
		return list.size();
	}
}