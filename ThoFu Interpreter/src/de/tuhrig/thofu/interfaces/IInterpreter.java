package de.tuhrig.thofu.interfaces;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.types.LObject;

public interface IInterpreter {

	public String execute(String expression);
	
	public String execute(LObject object);
	
	public void setParser(Parser parser);
	
	public Parser getParser();

	public Environment getEnvironment();

	public void addEnvironmentListener(EnvironmentListener listener);

	public void addHistoryListener(HistoryListener history);
	
	public void setStringBuilder(StringBuilder builder);
	
	public StringBuilder getStringBuilder();
}