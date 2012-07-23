package de.tuhrig.thofu.gui;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import de.tuhrig.thofu.interfaces.IInterpreter;
import de.tuhrig.thofu.types.LObject;

public class Executer {

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
				
            	Callable<LObject> callable = new Callable<LObject>() {
            		
					@Override
					public LObject call() {
						
						LObject result = null;
						
						for(LObject object: objects) {
							
							result = interpreter.execute(object);
						}
						
						return result;
					}
				};
				
				ExecutorService executor = Executors.newCachedThreadPool();
				
				Future<LObject> result = executor.submit(callable);

				try {
					
					value  = result.get().toString();
				}
				catch (InterruptedException | ExecutionException e) {
					
					e.printStackTrace();
				}
                
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
        };
        
        worker.start();
	}
}