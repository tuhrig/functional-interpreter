package de.tuhrig.fujas.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class LogLevelSelector extends JPanel {

	private static final long serialVersionUID = 1L;

	public LogLevelSelector() {

	    JRadioButton trace = new JRadioButton("Trace");
		JRadioButton debug = new JRadioButton("Debug");
		JRadioButton info = new JRadioButton("Info");
		JRadioButton warn = new JRadioButton("Warn");
		JRadioButton error = new JRadioButton("Error");
		JRadioButton fatal = new JRadioButton("Fatal");

	    ButtonGroup group = new ButtonGroup();
	    group.add(trace);
	    group.add(debug);
	    group.add(info);
	    group.add(warn);
	    group.add(error);
	    group.add(fatal);
	    
	    Level init = Logger.getRootLogger().getLevel();
	    
	    if(init.equals(Level.TRACE))
	    	trace.setSelected(true);
	    if(init.equals(Level.DEBUG))
	    	debug.setSelected(true);
	    if(init.equals(Level.INFO))
	    	info.setSelected(true);
	    if(init.equals(Level.WARN))
	    	warn.setSelected(true);
	    if(init.equals(Level.ERROR))
	    	error.setSelected(true);
	    if(init.equals(Level.FATAL))
	    	fatal.setSelected(true);
	    
	    trace.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Logger.getRootLogger().setLevel(Level.TRACE);
			}
	    });
	    
	    debug.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Logger.getRootLogger().setLevel(Level.DEBUG);
			}
	    });
	    
	    info.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Logger.getRootLogger().setLevel(Level.INFO);
			}
	    });
	    
	    warn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Logger.getRootLogger().setLevel(Level.WARN);
			}
	    });
	    
	    error.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Logger.getRootLogger().setLevel(Level.ERROR);
			}
	    });
	    
	    fatal.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Logger.getRootLogger().setLevel(Level.FATAL);
			}
	    });
	    
	    this.add(trace);
	    this.add(debug);
	    this.add(info);
	    this.add(warn);
	    this.add(error);
	    this.add(fatal);
	}
}