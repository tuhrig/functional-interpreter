package de.tuhrig.thofu.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import de.tuhrig.thofu.Container;
import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.Parser;
import de.tuhrig.thofu.interfaces.EnvironmentListener;
import de.tuhrig.thofu.interfaces.IInterpreter;
import de.tuhrig.thofu.interfaces.InterpreterListener;
import de.tuhrig.thofu.types.LObject;
import de.tuhrig.thofu.types.LSymbol;

class Editor extends JPanel implements EnvironmentListener, InterpreterListener {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(Repl.class);

	private final DefaultCompletionProvider provider = new DefaultCompletionProvider();

	private IInterpreter interpreter;

	private JTabbedPane tabbs = new JTabbedPane();
	
	private int i = 0;

	private final char open = '(';
	
	Editor() {

		this.setLayout(new BorderLayout(3, 3));
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setPreferredSize(new Dimension(100, 700));
		
		setLogo();
	}

	public void open(File file) {
		
		setTabbs();
		
		final RSyntaxTextArea area = SwingFactory.createSyntaxTextArea("editor", "");

		area.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent event) {
				
				/**
				 * Make a ) for every (
				 */
				if(event.getKeyChar() == open) {
					
					int current = area.getCaretPosition();
					
					area.append(")");
					area.setCaretPosition(current);
				}
			}
		});
		
		AutoCompletion autoCompletion = new AutoCompletion(provider);
		autoCompletion.install(area);

		FileTabb tab = new FileTabb(area, file);

		if(file.exists()) {
			
			try {

				String content = new Parser().read(file);
				
				area.setText(content);
			}
			catch (Exception e) {

				logger.error(e.getMessage());
			}
		}
		
		tabbs.addTab("" + i++, tab);

		JLabel label = new JLabel(tab.getName());
		label.setSize(30, 5);

		JButton tmp1 = new JButton();
		tmp1.setSize(1, 1);
		tmp1.setIcon(SwingFactory.create("icons/delete2.png", 16, 16));
		tmp1.setPressedIcon(SwingFactory.create("icons/delete1.png", 16, 16));
		tmp1.setBorderPainted(false); 
		tmp1.setContentAreaFilled(false); 
		tmp1.setFocusPainted(false); 
		tmp1.setOpaque(false);
		
		JButton tmp2 = new JButton();
		tmp2.setSize(1, 1);
		tmp2.setIcon(SwingFactory.create("icons/Write Document.png", 16, 16));
		tmp2.setBorderPainted(false); 
		tmp2.setContentAreaFilled(false); 
		tmp2.setFocusPainted(false); 
		tmp2.setOpaque(false);
		
		tmp1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
			
				close();
			}
		});

		FlowLayout layout = new FlowLayout();
		layout.setVgap(0);
		layout.setHgap(0);
		
		JPanel title = new JPanel();
		title.setLayout(layout);
		title.setSize(30, 5);
		title.setOpaque(false);
		title.add(tmp2);
		title.add(label);
		title.add(tmp1);

		tabbs.setTabComponentAt(tabbs.indexOfTab("" + (i-1)), title);
	}

	private void setTabbs() {

		removeAll();
		setBackground(null);
		add(tabbs);
	}
	
	private void setLogo() {

		JLabel label = new JLabel(new ImageIcon(SwingFactory.create("icons/logo.gif")));
		add(label);
		setBackground(Color.white);
	}

	public void setInterpreter(IInterpreter interpreter) {

		this.interpreter = interpreter;

		update(interpreter.getEnvironment());
	}

	@Override
	public void update(Environment environment) {

		this.provider.clear();

		for (Entry<LSymbol, Container> entry : environment.entrySet()) {

			provider.addCompletion(new BasicCompletion(provider, entry.getKey().toString()));
		}
	}

	public void save() {
		
		FileTabb tabb = (FileTabb) tabbs.getTabComponentAt(tabbs.getSelectedIndex());

		tabb.save();
	}

	public void saveAll() {
		
		for(int i = 0; i < tabbs.getTabCount(); i++) {
	
			FileTabb tabb = (FileTabb) tabbs.getComponentAt(i);
			
			tabb.save();
		}
	}
	
	public void saveAs(File file) {

		FileTabb tabb = (FileTabb) tabbs.getTabComponentAt(tabbs.getSelectedIndex());
		
		tabb.saveAs(file);
		
		close();
		
		open(file);
	}

	public void close() {

		tabbs.remove(tabbs.getSelectedIndex());
		
		if(tabbs.getTabCount() == 0)
			setLogo();
	}
	
	public void closeAll() {

		tabbs.removeAll();

		setLogo();
	}
	
	void execute() {
		
		for(int i = 0; i < tabbs.getTabCount(); i++ ) {

			String commands = ((FileTabb) tabbs.getComponentAt(i)).getText();
	
			Parser parser = new Parser();
			
			commands = parser.format(commands);

			List<LObject> objects = parser.parseAll(commands);
		
			Executer.instance.evaluate(null, objects, interpreter);
		}
	}

	public JPopupMenu getPopupMenu() {

		if(tabbs.getTabCount() > 0) {

			FileTabb tabb = (FileTabb) tabbs.getSelectedComponent();
			
			return tabb.getPopupMenu();
		}
		
		return null;
	}

	public boolean isDirty() {

		for(int i = 0; i < tabbs.getTabCount(); i++) {

			if(tabbs.getComponentAt(i) instanceof FileTabb) {
			
		    	FileTabb tabb = (FileTabb) tabbs.getComponentAt(i);
	
				if(tabb.isDirty())
					return true;
			}
		}

		return false;
	}
	
	class FileTabb extends RTextScrollPane {

		private static final long serialVersionUID = 1L;
		
		private File file;
		
		private boolean dirty = false;

		private RSyntaxTextArea area;

		public FileTabb(RSyntaxTextArea area, File file) {

			super(area);

			this.area = area;
			
			setName("no title");
			setFile(file);
			setPreferredSize(new Dimension(1, 300));
			
			area.addKeyListener(new KeyAdapter() {

				@Override
				public void keyPressed(KeyEvent event) {

					ThoFuUi.gui.markGrubby();
					FileTabb.this.markDirty();
				}
			});
			
			// if it's a new file, we mark it dirty, because it's not saved
			if(!file.exists())
				markDirty();
		}

		public String getText() {

			return area.getText();
		}

		public File getFile() {

			return file;
		}

		public void setFile(File file) {

			setName(file.getName());
		
			this.file = file;
		}
		
		public void markDirty() {

			dirty = true;
			
			setName(file.getName() + "*");
		}
		
		public void markClean() {
			
			dirty = false;
			
			setName(file.getName());
		}
		
		public boolean isDirty() {
			
			return dirty;
		}
		
		public void save() {
			
			String content = getText();
			File file = getFile();
			
			try {
				
				Files.write(file.toPath(), content.getBytes(), StandardOpenOption.CREATE);
			}
			catch (IOException e) {
		
				logger.warn("error on saving", e);
			}
			
			markClean();
		}
		
		public void saveAs(File file) {
			
			setFile(file);
			
			save();
			
			markClean();
		}
		
		public void setName(String name) {
			
			super.setName(name);
		
			int index = tabbs.indexOfComponent(this);
			
			if(index != -1)
				tabbs.setTitleAt(index, name);
		}
		
		public JPopupMenu getPopupMenu() {
			
			return area.getPopupMenu();
		}
	}

	public boolean openFiles() {

		return tabbs.getComponentCount() > 0;
	}
}