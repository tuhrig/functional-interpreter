package de.tuhrig.thofu.interfaces;

import de.tuhrig.thofu.Environment;

public interface IInterpreter {

	public String execute(String expression);

	public Environment getEnvironment();

	public void addEnvironmentListener(EnvironmentListener listener);

	void addHistoryListener(HistoryListener history);
}