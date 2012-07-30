package de.tuhrig.thofu.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
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
	
	private final JToggleButton resume = new JToggleButton("Resume");

	private final DefaultTableModel model = new DefaultTableModel();
	
	private JTable table;
	
	private int step = 0;
	
	// singleton
	private Debugger() {

		setLayout(new BorderLayout());

		model.addColumn("");
		model.addColumn("#");
		model.addColumn("Object");
		model.addColumn("Type");
		model.addColumn("Tokens");
		model.addColumn("Environment");

		
		table = new JTable(model){

			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				
				return false;
			}
		};
  
		table.setName("Stack Table");
		table.setToolTipText("Shows the current stack if debbger is activated");
		
		TableColumn type = table.getColumnModel().getColumn(0);
		type.setPreferredWidth(30);
		
		TableColumn operation = table.getColumnModel().getColumn(1);
		operation.setPreferredWidth(30);
		
		TableColumn object = table.getColumnModel().getColumn(2);
		object.setPreferredWidth(100);
		
		TableColumn environment = table.getColumnModel().getColumn(5);
		environment.setPreferredWidth(100);

		next.setIcon(new ImageIcon(SwingFactory.create("icons/Play Blue Button.png")));
		
		resume.setIcon(new ImageIcon(SwingFactory.create("icons/Play Green Button.png")));
		
		active.setIcon(new ImageIcon(SwingFactory.create("icons/Grey Ball.png")));
		active.setSelectedIcon(new ImageIcon(SwingFactory.create("icons/Green Ball.png")));
		
		next.setEnabled(false);
		resume.setEnabled(false);
		
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
					resume.setEnabled(true);
				}
				else {
					
					Interpreter.setDebugg(false);
					active.setText("Activate Step-by-Step Debugger");
					
					next.setEnabled(false);
					resume.setEnabled(false);
				}
			}
		});
		
		next.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				Interpreter.setNext(true);
			}
		});
		
		resume.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Interpreter.setResume();
			}
		});
		
		table.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() == 2) {

					JTable target = (JTable) e.getSource();
					
					int row = target.getSelectedRow();
					
					String obj0 = (String) model.getValueAt(row,0);
					int obj1 = (Integer) model.getValueAt(row,1);
					LObject obj2 = (LObject) model.getValueAt(row, 2);
	
					JTextArea area = new JTextArea();
					area.setEditable(false);
					
					if(obj0.equals(">"))
						obj0 = "Push";
					else
						obj0 = "Return";
					
					// set general information
					area.setText( 
							"Action:\t\t" + obj0 + "\n" +
							"Step:\t\t" + obj1 + "\n"
							);
					
					area.append(obj2.inspect());
					
					JFrame frame = new JFrame(obj2.toString());
					frame.getContentPane().add(new JScrollPane(area));
					frame.setSize(800, 400);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				}
			}
		});
		
		JPanel buttons = new JPanel();
		buttons.add(next);
		buttons.add(resume);
		
		add(active, BorderLayout.NORTH);
		add(buttons, BorderLayout.SOUTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	public void pushCall(final LObject self, Environment environment, LObject tokens, int arguments) {

		step++;

		for(int i = 0; i < arguments; i++)
			model.removeRow(model.getRowCount() - 1);
	
		model.addRow(new Object[]{">", step, self, self.getClass(), tokens, environment});
	}

	public void pushResult(LObject result, Environment environment, LObject tokens, int arguments) {

		for(int i = 0; i < arguments; i++)
			model.removeRow(model.getRowCount() - 1);

		model.addRow(new Object[]{"<", step, result, result.getClass(), tokens, environment});
	}

	public static Debugger getInstance() {

		return instance;
	}
}