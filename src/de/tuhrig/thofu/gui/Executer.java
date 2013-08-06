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

/**
 * The executer executes a command in a separate thread then the GUI.
 * This makes the GUI responding, even when the evaluation takes long.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class Executer {

	private static Logger logger = Logger.getLogger(Executer.class);
	
	/**
	 * Singleton instance of the executer
	 */
	public final static Executer instance = new Executer();

	private Executer() {
		
		// singleton
	}

	/**
	 * @param textArea to append the result to
	 * @param objects to evaluate
	 * @param interpreter to use
	 */
	public void evaluate(final JTextArea textArea, final List<LObject> objects, final IInterpreter interpreter) {

		// start the "is running" animation
		ThoFuUi.instance().start();
		
		Thread worker = new Thread() {
			
            private String value;

			public void run() {

				ThoFuUi.instance().enableControls(false);
				
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
	
	                    	if(textArea != null) {
	                    		
	                    		textArea.append(value + "\n" + "Out: "+ interpreter.getStringBuilder() + "\n>> ");
	                    		
	                    		interpreter.setStringBuilder(new StringBuilder());
	                    	}
	                    	
	                    	ThoFuUi.instance().enableControls(true);
	                    	
	                    	// stop the "is running" animation
							ThoFuUi.instance().stop();
	                    }
	                });
                }
            }
        };
        
        worker.start();
	}
}