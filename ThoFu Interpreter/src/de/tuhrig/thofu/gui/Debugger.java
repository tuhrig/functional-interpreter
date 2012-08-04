package de.tuhrig.thofu.gui;

import static de.tuhrig.thofu.Literal.NL;
import static de.tuhrig.thofu.Literal.TP;

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
import de.tuhrig.thofu.Literal;
import de.tuhrig.thofu.interfaces.IDebugger;
import de.tuhrig.thofu.types.LObject;

/**
 * A simple graphical debugger that shows a stack. It's 
 * currently not working for complex commands. However,
 * simple commands like (+ 1 2 3) are working fine.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class Debugger extends JPanel implements IDebugger {

	private static final long serialVersionUID = 1L;
	
	private final DefaultTableModel model = new DefaultTableModel();
	
	private final JToggleButton activeButton = new JToggleButton("Activate Step-by-Step Debugger");
	
	private final JButton nextButton = new JButton("Next");
	
	private final JToggleButton resumeButton = new JToggleButton("Resume");
	
	private int step = 0;
	
	private boolean debugg;

	private boolean next;

	private boolean resume;
	
	// singleton
	public Debugger() {

		setLayout(new BorderLayout());

		model.addColumn(Literal.EMPTY);
		model.addColumn("#");
		model.addColumn("Object");
		model.addColumn("Type");
		model.addColumn("Tokens");
		model.addColumn("Environment");

		final JTable table = new JTable(model){

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

		nextButton.setIcon(new ImageIcon(SwingFactory.create("icons/Play Blue Button.png")));
		
		resumeButton.setIcon(new ImageIcon(SwingFactory.create("icons/Play Green Button.png")));
		
		activeButton.setIcon(new ImageIcon(SwingFactory.create("icons/Grey Ball.png")));
		activeButton.setSelectedIcon(new ImageIcon(SwingFactory.create("icons/Green Ball.png")));
		
		nextButton.setEnabled(false);
		resumeButton.setEnabled(false);
		
		activeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				if(activeButton.isSelected()) {
					
					setDebugg(true);
				}
				else {
					
					setDebugg(false);
				}
			}
		});
		
		nextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				setNext(true);
			}
		});
		
		resumeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				setResume();
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
							"Action:" + TP + TP + obj0 + NL +
							"Step:" + TP + TP + obj1 + NL
							);
					
					area.append(obj2.inspect());
					
					JScrollPane scrollPane = new JScrollPane(area);
					
					area.setCaretPosition(0);
					scrollPane.getVerticalScrollBar().setValue(0);
					scrollPane.repaint();		
					
					JFrame frame = new JFrame(obj2.toString());
					frame.getContentPane().add(scrollPane);
					frame.setSize(800, 400);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				}
			}
		});
		
		JPanel buttons = new JPanel();
		buttons.add(nextButton);
		buttons.add(resumeButton);
		
		add(activeButton, BorderLayout.NORTH);
		add(buttons, BorderLayout.SOUTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.IDebugger#pushCall(de.tuhrig.thofu.types.LObject, de.tuhrig.thofu.Environment, de.tuhrig.thofu.types.LObject, int)
	 */
	@Override
	public void pushCall(final LObject self, Environment environment, LObject tokens, int arguments) {

		step++;

		for(int i = 0; i < arguments; i++)
			model.removeRow(model.getRowCount() - 1);
	
		model.addRow(new Object[]{">", step, self, self.getClass(), tokens, environment});
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.IDebugger#pushResult(de.tuhrig.thofu.types.LObject, de.tuhrig.thofu.Environment, de.tuhrig.thofu.types.LObject, int)
	 */
	@Override
	public void pushResult(LObject result, Environment environment, LObject tokens, int arguments) {

		for(int i = 0; i < arguments; i++)
			model.removeRow(model.getRowCount() - 1);

		model.addRow(new Object[]{"<", step, result, result.getClass(), tokens, environment});
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.IDebugger#isDebugging()
	 */
	@Override
	public boolean debugg() {

		return debugg;
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.IDebugger#setDebugging(boolean)
	 */
	@Override
	public void setDebugg(boolean debugg) {

		this.debugg = debugg;
		
		if(debugg) {
			
			activeButton.setText("Reset");
			activeButton.setSelected(true);		// selected manually for the case that
												// the interpreter was reseted
			
			while(model.getRowCount() > 0)
				model.removeRow(0);
			
			setNext(false);
		}
		else {
		
			activeButton.setText("Activate Step-by-Step Debugger");
			activeButton.setSelected(false);	// selected manually for the case that
												// the interpreter was reseted
			
			nextButton.setEnabled(false);
			resumeButton.setEnabled(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.IDebugger#next()
	 */
	@Override
	public boolean next() {

		if(next) {
			
			next = false;
			return true;
		}
		
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.IDebugger#setNext(boolean)
	 */
	@Override
	public void setNext(boolean b) {

		next = b;

		nextButton.setEnabled(true);
		resumeButton.setEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.IDebugger#resume()
	 */
	@Override
	public boolean resume() {

		return resume;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.tuhrig.thofu.interfaces.IDebugger#setResume()
	 */
	@Override
	public void setResume() {

		if(resume)
			resume = false;
		else
			resume = true;
	}
}