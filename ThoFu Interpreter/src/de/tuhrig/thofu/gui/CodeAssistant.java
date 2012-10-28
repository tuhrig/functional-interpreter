package de.tuhrig.thofu.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;

import de.tuhrig.thofu.Literal;


public class CodeAssistant extends KeyAdapter {

	private final JTextArea textArea;
	
	public CodeAssistant(JTextArea textArea) {
		
		this.textArea = textArea;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.KeyAdapter#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent event) {
		
		/*
		 * Make a ) for every (
		 */
		if(String.valueOf(event.getKeyChar()).equals(Literal.LEFT_PARENTHESIS)) {
			
			int current = textArea.getCaretPosition();
			
			textArea.replaceRange(Literal.RIGHT_PARENTHESIS, current, current);
			textArea.setCaretPosition(current);
		}
		
		/*
		 * Make a } for every {
		 */
		if(String.valueOf(event.getKeyChar()).equals(Literal.LEFT_PARENTHESIS_CURLY)) {
			
			int current = textArea.getCaretPosition();
			
			textArea.replaceRange(Literal.RIGHT_PARENTHESIS_CURLY, current, current);
			textArea.setCaretPosition(current);
		}	
	}
}