package de.tuhrig.fujas.interfaces;

import de.tuhrig.fujas.Environment;

public interface EnvironmentListener {

	public void update(Environment environment);
	
	public void reset(Environment environment);
}