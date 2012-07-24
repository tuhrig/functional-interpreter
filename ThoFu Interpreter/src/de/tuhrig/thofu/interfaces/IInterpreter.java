package de.tuhrig.thofu.interfaces;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.types.LObject;

public interface IInterpreter {

	public String execute(String expression);
	
	public LObject execute(LObject object);

	public Environment getEnvironment();

	public void addEnvironmentListener(EnvironmentListener listener);

	public void addHistoryListener(HistoryListener history);
}