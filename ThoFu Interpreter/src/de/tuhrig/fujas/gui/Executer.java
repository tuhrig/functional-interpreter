package de.tuhrig.fujas.gui;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.SwingUtilities;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import de.tuhrig.fujas.interfaces.IInterpreter;
import de.tuhrig.fujas.types.LObject;

public class Executer {

	public final static Executer instance = new Executer();
	
	private String value;
	
	private Executer() {
		
		// singleton
	}
	
	public void execute(final IInterpreter interpreter, final String command, final RSyntaxTextArea textArea) {
	
		GUI.gui.start();

		Thread worker = new Thread() {
            
            public void run() {

            	Callable<String> callable = new Callable<String>() {
            		
					@Override
					public String call() {

						return 	interpreter.execute(command);
					}
				};
				
				ExecutorService executor = Executors.newCachedThreadPool();
				
				Future<String> result = executor.submit(callable);

				try {
					
					value = result.get();
				}
				catch (InterruptedException | ExecutionException e) {
					
					e.printStackTrace();
				}
                
                SwingUtilities.invokeLater(new Runnable() {
                	
                    public void run() {
                    	
						textArea.append(value + "\n>> ");
						
						GUI.gui.stop();
                    }
                });
            }
        };
        
        worker.start();
	}

	public void eval(final List<LObject> objects, final IInterpreter interpreter) {

		GUI.gui.start();

		Thread worker = new Thread() {
            
            public void run() {

            	Callable<LObject> callable = new Callable<LObject>() {
            		
					@Override
					public LObject call() {
						
						LObject result = null;
						
						for(LObject object: objects) {
							
							result = object.eval(interpreter.getEnvironment(), object);
						}
						
						return result;
					}
				};
				
				ExecutorService executor = Executors.newCachedThreadPool();
				
				Future<LObject> result = executor.submit(callable);

				try {
					
					result.get();
				}
				catch (InterruptedException | ExecutionException e) {
					
					e.printStackTrace();
				}
                
                SwingUtilities.invokeLater(new Runnable() {
                	
                    public void run() {

						GUI.gui.stop();
						GUI.gui.markFresh();
                    }
                });
            }
        };
        
        worker.start();
	}
}