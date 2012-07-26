package de.tuhrig.thofu;

import de.tuhrig.thofu.types.LObject;

/**
 * A container wraps a LObject. Different environments can hold
 * the same container. If the value of a container is changed (in 
 * one environment), only the wrapped object will be changed, not
 * the container itself. Therefore, all other environments holding
 * the same container, will have the change too.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class Container {

	private LObject object;
		
	/**
	 * @param object to wrap
	 */
	public Container(LObject object) {

		this.setObject(object);
	}

	/**
	 * @return wrapped object
	 */
	public LObject getObject() {

		return object;
	}

	/**
	 * @param object to wrap
	 */
	public void setObject(LObject object) {

		this.object = object;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		return object.toString();
	}
}