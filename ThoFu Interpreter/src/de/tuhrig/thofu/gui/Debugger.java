package de.tuhrig.thofu.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
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
	
	private static final Debugger instance = new Debugger();
	
	private final JToggleButton active = new JToggleButton("Activate Step-by-Step Debugger");

	private final JButton next = new JButton("Next");

	private final DefaultTableModel model = new DefaultTableModel();
	
	private final JTable table = new JTable();
	
	private int step = 0;
	
	// singleton
	private Debugger() {

		setLayout(new BorderLayout());

		model.addColumn("#");
		model.addColumn("Object");
		model.addColumn("Type");
		model.addColumn("Tokens");
		model.addColumn("Environment");
		
		table.setModel(model);
		table.setName("Stack Table");
		table.setToolTipText("Shows the current stack if debbger is activated");
		
		TableColumn operation = table.getColumnModel().getColumn(0);
		operation.setPreferredWidth(30);
		
		TableColumn object = table.getColumnModel().getColumn(1);
		object.setPreferredWidth(100);
		
		TableColumn environment = table.getColumnModel().getColumn(4);
		environment.setPreferredWidth(100);

		next.setIcon(new ImageIcon(SwingFactory.create("icons/Play Blue Button.png")));
		active.setIcon(new ImageIcon(SwingFactory.create("icons/Grey Ball.png")));
		active.setSelectedIcon(new ImageIcon(SwingFactory.create("icons/Green Ball.png")));
		
		next.setEnabled(false);
		
		active.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				if(active.isSelected()) {
					
					Interpreter.setDebugg(true);
					active.setText("Reset");
					
					while(model.getRowCount() > 0)
						model.removeRow(0);
					
					Interpreter.setNext(false);
					
					next.setEnabled(true);
				}
				else {
					
					Interpreter.setDebugg(false);
					active.setText("Activate Step-by-Step Debugger");
					
					next.setEnabled(false);
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
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	public void pushCall(final LObject obj, Environment environment, LObject tokens, int arguments) {

		step++;

		for(int i = 0; i < arguments; i++)
			model.removeRow(model.getRowCount() - 1);
	
		model.addRow(new Object[]{step, obj, obj.getClass().getSimpleName(), tokens, environment});
	}

	public void pushResult(LObject obj, Environment environment, LObject tokens, int arguments) {

		for(int i = 0; i < arguments; i++)
			model.removeRow(model.getRowCount() - 1);

		model.addRow(new Object[]{step, obj, obj.getClass().getSimpleName(), tokens, environment});
	}

	public static Debugger getInstance() {

		return instance;
	}
}