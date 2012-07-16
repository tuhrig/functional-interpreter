package de.tuhrig.fujas.interfaces;

import de.tuhrig.fujas.Environment;

public interface IInterpreter {

	public String execute(String expression);

	public Environment getEnvironment();

	public void addEnvironmentListener(EnvironmentListener listener);

	void addHistoryListener(HistoryListener history);
}