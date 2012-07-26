package de.tuhrig.thofu.gui;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import de.tuhrig.thofu.interfaces.IInterpreter;
import de.tuhrig.thofu.types.LObject;

public class Executer {

	private static Logger logger = Logger.getLogger(Executer.class);
	
	public final static Executer instance = new Executer();

	private Executer() {
		
		// singleton
	}

	public void evaluate(final JTextArea area, final List<LObject> objects, final IInterpreter interpreter) {

		GUI.gui.start();
		
		Thread worker = new Thread() {
			
            private String value = "";

			public void run() {

				GUI.gui.enableControls(false);
				
            	Callable<String> callable = new Callable<String>() {
            		
					@Override
					public String call() {
						
						String result = null;
						
						for(LObject object: objects) {
							
							result = interpreter.execute(object);
						}
						
						return result;
					}
				};
				
				ExecutorService executor = Executors.newCachedThreadPool();
				
				Future<String> result = executor.submit(callable);

				try {
					
					value  = result.get();
				}
				catch (InterruptedException | ExecutionException e) {
					
					logger.warn("error", e);
				}
                finally {
                	
	                SwingUtilities.invokeLater(new Runnable() {
	                	
	                    public void run() {
	
	                    	if(area != null) {
	                    		
	                    		area.append(value + "\n>> ");
	                    	}
	                    	
	                    	GUI.gui.enableControls(true);
	                    	
							GUI.gui.stop();
	                    }
	                });
                }
            }
        };
        
        worker.start();
	}
}