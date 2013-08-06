package de.tuhrig.thofu.gui;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

class SwingFactory {

	public static JButton createButton(String name) {

		JButton button = new JButton();
		button.setText(name);
		button.setName(name);
		button.setToolTipText(name);
		
		return button;
	}
	
	public static RSyntaxTextArea createSyntaxTextArea(String name, String text) {

		final RSyntaxTextArea area = new RSyntaxTextArea();

		area.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LISP);
		
		area.setText(text);
		area.setName(name);
		area.setToolTipText(name);
		
		for(int i = 0; i < area.getPopupMenu().getComponentCount(); i++) {
			
			Object object = area.getPopupMenu().getComponent(i);
			
			if(object instanceof JMenuItem) {
				
				JMenuItem item = (JMenuItem) object;

				if(item.getText().equals("Copy"))
					SwingFactory.setIcon(item, "icons/Copy.png");
				
				if(item.getText().equals("Paste"))
					SwingFactory.setIcon(item, "icons/Paste.png");
				
				if(item.getText().equals("Cut"))
					SwingFactory.setIcon(item, "icons/Cut.png");
				
				if(item.getText().contains("Delete"))
					SwingFactory.setIcon(item, "icons/delete.png");
				
				if(item.getText().contains("Select All"))
					SwingFactory.setIcon(item, "icons/edit_select_all.png");
				
				if(item.getText().contains("Undo"))
					SwingFactory.setIcon(item, "icons/undo.png");
				
				if(item.getText().contains("Redo"))
					SwingFactory.setIcon(item, "icons/redo.png");
			}
		}
		
		// mouse listener to resize font
		area.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				
				Font font = area.getFont();
				float size = font.getSize();
				
				if(e.getWheelRotation() < 0) {
					
					size = font.getSize() + 1f;
				}
				else if (e.getWheelRotation() > 0){

					size = font.getSize() - 1f;
				}
				
				area.setFont(font.deriveFont(size));
			}
		});
		
		return area;
	}

	public static JMenuItem createItem(String name, String icon) {

		JMenuItem item = new JMenuItem();
		item.setText(name);
		item.setName(name);
		item.setToolTipText(name);
		
		setIcon(item, icon);
		
		return item;
	}

	public static JMenu createMenu(String name, String icon) {

		JMenu menu = new JMenu();
		menu.setText(name);
		menu.setName(name);
		menu.setToolTipText(name);
		
		setIcon(menu, icon);
		
		return menu;
	}
	
	public static void setIcon(JMenuItem item, String icon) {
		
		item.setIcon(create(icon, 24, 24));
		
		item.setIconTextGap(10);
	}
	

	public static Image create(String icon) {

		URL url = new SwingFactory().getClass().getResource(icon);

		ImageIcon imageIcon = new ImageIcon(url);
		
		return imageIcon.getImage();
	}
	
	public static ImageIcon create(String icon, int h, int w) {

		Image img = create(icon);
		
		Image resizedImage = img.getScaledInstance(h, w, Image.SCALE_SMOOTH);
		
		return new ImageIcon(resizedImage);
	}

	public static JMenu createMenu(String name, String icon, JPopupMenu pop) {
		
		JMenu menu = createMenu(name, icon);
		
		if(pop != null) {
			
			for(int i = 0; i < pop.getComponentCount(); i++) {
				
				Object object = pop.getComponent(i);
	
				if(object instanceof JMenuItem) {
					
					JMenuItem item = (JMenuItem) object;
	
					JMenuItem tmp = new JMenuItem();
					
					tmp.setIcon(item.getIcon());
					tmp.setText(item.getText());
					tmp.setToolTipText(item.getToolTipText());
					tmp.setEnabled(item.isEnabled());
					
					for(ActionListener listener: item.getActionListeners())
						tmp.addActionListener(listener);
					
					menu.add(tmp);
				}
			}
		}
		
		return menu;
	}

	public static JMenuBar createMenuBar() {

		return new JMenuBar();
	}
}