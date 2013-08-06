package de.tuhrig.thofu.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXBusyLabel;

/**
 * Swing Hacks Tips and Tools for Killer GUIs
 * By Joshua Marinacci, Chris Adamson
 * First Edition June 2005  
 * Series: Hacks
 * ISBN: 0-596-00907-0
 * Pages: 542
 * website: http://www.oreilly.com/catalog/swinghks/
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class Running extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JXBusyLabel label;

	public Running() {

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(10, 25));
		
		JPanel resize = new JPanel(new BorderLayout());
		resize.add(new JLabel(new AngledLinesWindowsCornerIcon()), BorderLayout.SOUTH);
		resize.setOpaque(false);

		label = new JXBusyLabel(new Dimension(20, 20)); 
        label.setName("busy"); 
        label.getBusyPainter().setHighlightColor(new Color(44, 61, 146).darker()); 
        label.getBusyPainter().setBaseColor(new Color(168, 204, 241).brighter()); 

        add(resize, BorderLayout.EAST);
        add(label, BorderLayout.CENTER);
        
        stop();
	}

	/**
	 * Starts the busy-animation
	 */
	public void start() {
		
        label.setBusy(true); 
        label.setText("Running");
	}
	
	/**
	 * Stops the busy-animation
	 */
	public void stop() {
		
        label.setBusy(false); 
        label.setText("Ready");
	}
	
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		int y = 0;
		g.setColor(new Color(156, 154, 140));
		g.drawLine(0, y, getWidth(), y);
		y++;
		g.setColor(new Color(196, 194, 183));
		g.drawLine(0, y, getWidth(), y);
		y++;
		g.setColor(new Color(218, 215, 201));
		g.drawLine(0, y, getWidth(), y);
		y++;
		g.setColor(new Color(233, 231, 217));
		g.drawLine(0, y, getWidth(), y);

		y = getHeight() - 3;
		g.setColor(new Color(233, 232, 218));
		g.drawLine(0, y, getWidth(), y);
		y++;
		g.setColor(new Color(233, 231, 216));
		g.drawLine(0, y, getWidth(), y);
		y = getHeight() - 1;
		g.setColor(new Color(221, 221, 220));
		g.drawLine(0, y, getWidth(), y);

	}

	static class AngledLinesWindowsCornerIcon implements Icon {

		static Color WHITE_LINE_COLOR = new Color(255, 255, 255);

		static Color GRAY_LINE_COLOR = new Color(172, 168, 153);

		private static final int WIDTH = 13;

		private static final int HEIGHT = 13;

		public int getIconHeight() {

			return WIDTH;
		}

		public int getIconWidth() {

			return HEIGHT;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {

			g.setColor(WHITE_LINE_COLOR);
			g.drawLine(0, 12, 12, 0);
			g.drawLine(5, 12, 12, 5);
			g.drawLine(10, 12, 12, 10);

			g.setColor(GRAY_LINE_COLOR);
			g.drawLine(1, 12, 12, 1);
			g.drawLine(2, 12, 12, 2);
			g.drawLine(3, 12, 12, 3);

			g.drawLine(6, 12, 12, 6);
			g.drawLine(7, 12, 12, 7);
			g.drawLine(8, 12, 12, 8);

			g.drawLine(11, 12, 12, 11);
			g.drawLine(12, 12, 12, 12);
		}
	}
}