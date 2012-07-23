package de.tuhrig.thofu.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.Interpreter;
import de.tuhrig.thofu.types.LObject;

public class Debugger extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JToggleButton active;

	private JButton next;

	private static DefaultTableModel model;
	
	private static JTable view = new JTable();
	
	private static int step = 0;
	
	public Debugger() {

		setLayout(new BorderLayout());

		view.addColumn(new TableColumn());
		
		model = new DefaultTableModel();
		model.addColumn("Operation");
		model.addColumn("Object");
		model.addColumn("Type");
		model.addColumn("Tokens");
		
		view.setModel(model);
		
		active = new JToggleButton("Activate Step-by-Step Debugger");
		
		next = new JButton("Next");
		
		active.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				if(active.isSelected()) {
					
					Interpreter.setDebugg(true);
					active.setText("Reset");
					
					while(model.getRowCount() > 0)
						model.removeRow(0);
				}
				else {
					
					Interpreter.setDebugg(false);
					active.setText("Activate Step-by-Step Debugger");
				}
			}
		});
		
		next.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				Interpreter.setNext(true);
			}
		});
		
		add(active, BorderLayout.NORTH);
		add(next, BorderLayout.SOUTH);
		add(new JScrollPane(view), BorderLayout.CENTER);
	}

	public static void call(final LObject obj, Environment environment, LObject tokens) {

		step++;
		
		model.addRow(new Object[]{step, obj, obj.getClass().getSimpleName(), tokens});
	}

	public static void result(LObject result, int arguments) {

		for(int i = 0; i < arguments; i++)
			model.removeRow(model.getRowCount() - 1);
		
		model.addRow(new Object[]{step, result, result.getClass().getSimpleName(), ""});
	}
}