package de.tuhrig.thofu.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
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

	private static Logger logger = Logger.getLogger(REPL.class);

	private final DefaultCompletionProvider provider = new DefaultCompletionProvider();

	private IInterpreter interpreter;

	private final static JTabbedPane tabbs = new JTabbedPane();

	Editor() {

		this.setLayout(new BorderLayout(3, 3));
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setPreferredSize(new Dimension(100, 700));
		
		setLogo();
	}

	public void open(File file) {
		
		setTabbs();
		
		RSyntaxTextArea area = SwingFactory.createSyntaxTextArea("editor", "");

		AutoCompletion autoCompletion = new AutoCompletion(provider);
		autoCompletion.install(area);

		RTextScrollPane scrollPane = new FileTabb(area, file);

		if(file.exists()) {
			
			try {

				String content = new Parser().read(file);
				
				area.setText(content);
			}
			catch (Exception e) {

				logger.error(e.getMessage());
			}
		}

		tabbs.add(scrollPane);
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
		
		FileTabb tabb = (FileTabb) tabbs.getSelectedComponent();

		tabb.save();
	}

	public void saveAll() {
		
		for(int i = 0; i < tabbs.getTabCount(); i++) {
	
			FileTabb tabb = (FileTabb) tabbs.getComponent(i);
			
			tabb.save();
		}
	}
	
	public void saveAs(File file) {

		FileTabb tabb = (FileTabb) tabbs.getSelectedComponent();
		
		tabb.saveAs(file);
	}

	public void close() {

		tabbs.remove(tabbs.getSelectedComponent());
		
		if(tabbs.getComponentCount() == 0)
			setLogo();
	}
	
	public void closeAll() {

		tabbs.removeAll();

		setLogo();
	}
	
	void execute() {
		
		for(int i = 0; i < tabbs.getTabCount(); i++ ) {

			String commands = ((FileTabb) tabbs.getComponent(i)).getText();
	
			Parser parser = new Parser();
			
			commands = parser.format(commands);

			List<LObject> objects = parser.parseAll(commands);
		
			Executer.instance.eval(objects, interpreter);
		}
	}

	public JPopupMenu getPopupMenu() {

		if(tabbs.getComponentCount() > 0) {

			FileTabb tabb = (FileTabb) tabbs.getSelectedComponent();
			
			return tabb.getPopupMenu();
		}
		
		return null;
	}

	public boolean isDirty() {

		for(int i = 0; i < tabbs.getComponentCount(); i++) {
			
			FileTabb tabb = (FileTabb) tabbs.getComponent(i);

			if(tabb.isDirty())
				return true;
		}

		return false;
	}
	
	static class FileTabb extends RTextScrollPane {

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

					GUI.gui.markGrubby();
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
		
				e.printStackTrace();
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