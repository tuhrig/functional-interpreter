package de.tuhrig.thofu.interfaces;

import de.tuhrig.thofu.Environment;

/**
 * An interface implemented by UI components that
 * visualize the environment.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public interface EnvironmentListener {

	public void update(Environment environment);
}