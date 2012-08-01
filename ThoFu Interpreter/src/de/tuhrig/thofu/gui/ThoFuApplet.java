package de.tuhrig.thofu.gui;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JApplet;
import javax.swing.JMenuBar;

public class ThoFuApplet extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init() {
		
		ThoFuUi gui = new ThoFuUi();
		
		Container pane = gui.getContentPane();
		
		JMenuBar menu = gui.getJMenuBar();

		setSize(1000, 500);
		
		pane.setPreferredSize(new Dimension(1000, 500));
		
		add(pane);
		setJMenuBar(menu);
	}
}
