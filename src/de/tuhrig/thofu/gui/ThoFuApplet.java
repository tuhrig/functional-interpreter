package de.tuhrig.thofu.gui;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JApplet;
import javax.swing.JMenuBar;

/**
 * An applet containg the GUI.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class ThoFuApplet extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * @see java.applet.Applet#init()
	 */
	public void init() {

		Container pane = ThoFuUi.instance().getContentPane();
		
		JMenuBar menu = ThoFuUi.instance().getJMenuBar();

		setSize(1000, 500);
		
		pane.setPreferredSize(new Dimension(1000, 500));
		
		add(pane);
		setJMenuBar(menu);
	}
}