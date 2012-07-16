package de.tuhrig.fujas.gui;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

class Status extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel message = new JLabel("");
	
	public Status() {
		
		this.setName("status");
		
		this.add(message);
	}
	
	public void setMessage(String message) {
		
		this.message.setText(message);
	}

	public void setColor(Color color) {

		this.setBackground(color);
	}
}