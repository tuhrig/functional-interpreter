package de.tuhrig.thofu.types;

import de.tuhrig.thofu.Environment;

public abstract class LObject { // implements TreeNode {

	public abstract LObject eval(Environment environment, LObject tokens);

	public abstract String toString();

	public abstract boolean equals(Object o);
	
	public abstract int hashCode();
}