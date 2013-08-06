package de.tuhrig.thofu.interfaces;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.types.LObject;

/**
 * A skeleton for a debugger.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public interface IDebugger {

	/**
	 * Pushes a method call.
	 * 
	 * @param lObject to push
	 * @param environment that is currently used
	 * @param tokens that are currently used
	 * @param argrumentSize to remove from the stack-view
	 */
	public void pushCall(LObject lObject, Environment environment, LObject tokens, int argrumentSize);

	/**
	 * Pushes a result.
	 * 
	 * @param result to push
	 * @param environment that is currently used
	 * @param tokens that are currently used
	 * @param argrumentSize to remove from the stack-view
	 */
	public void pushResult(LObject result, Environment environment, LObject tokens, int argrumentSize);

	/**
	 * @return true if debugging is activated
	 */
	public boolean debugg();

	/**
	 * @param true to activate debugging
	 */
	public void setDebugg(boolean b);
	
	/**
	 * @return true if the debugger should resume all steps
	 */
	public boolean resume();

	/**
	 * Sets the resume-value to the different value as the current.
	 * If resume is false, it will be true after the call. If resume
	 * if true, it will be false after the call.
	 */
	public void setResume();
	
	/**
	 * This method returns true if the debugger should make another step.
	 * It return false if the debugger should still stop at the current 
	 * point.
	 * 
	 * If the method returns true, it will return false immediately after.
	 * This makes the debugger stop at the next step until the user clicks
	 * "next".
	 * 
	 * @return true if the next step should be made.
	 */
	public boolean next();

	/**
	 * @param true to go to the next step
	 */
	public void setNext(boolean b);

}