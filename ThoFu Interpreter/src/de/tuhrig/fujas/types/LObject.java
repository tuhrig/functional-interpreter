package de.tuhrig.fujas.types;

import de.tuhrig.fujas.Environment;

public abstract class LObject { // implements TreeNode {

	public abstract LObject eval(Environment environment, LObject tokens);

	public abstract String toString();

	public abstract boolean equals(Object o);
	
	public abstract int hashCode();
}