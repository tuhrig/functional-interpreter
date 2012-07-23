package de.tuhrig.thofu;

import de.tuhrig.thofu.types.LObject;

public class Container {

	private LObject object;
		
	public Container(LObject object) {

		this.setObject(object);
	}

	public LObject getObject() {

		return object;
	}

	public void setObject(LObject object) {

		this.object = object;
	}
	
	public String toString() {
		
		return object.toString();
	}
}