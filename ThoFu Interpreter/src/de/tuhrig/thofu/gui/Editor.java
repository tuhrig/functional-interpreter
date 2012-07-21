package de.tuhrig.thofu.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
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

import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.Environment.Container;
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

	private List<Tabb> areas = new ArrayList<>();
	
	private final JTabbedPane tabbs;

	private boolean dirty;

	Editor() {

		tabbs = new JTabbedPane();

		this.setLayout(new BorderLayout(3, 3));
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setLogo();
	}

	public void open(File file) {
		
		setTabbs();
		
		RSyntaxTextArea area = SwingFactory.createSyntaxTextArea("editor", "");

		area.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent event) {

				GUI.gui.markGrubby();
				
				Editor.this.markDirty();
			}
		});
		
		AutoCompletion autoCompletion = new AutoCompletion(provider);
		autoCompletion.install(area);

		RTextScrollPane scrollPane = new RTextScrollPane(area);

		scrollPane.setPreferredSize(new Dimension(1, 300));

		areas.add(new Tabb(file, scrollPane));
		
		if(file.exists()) {
			
			try {

				String content = new Parser().read(file);
				
				area.setText(content);
			}
			catch (Exception e) {

				logger.error(e.getMessage());
			}
		}

		tabbs.addTab(file.getName(), scrollPane);
	}

	protected void markDirty() {

		if (!dirty) {

			this.dirty = true;

			int index = tabbs.getSelectedIndex();

			tabbs.setTitleAt(index, tabbs.getTitleAt(index) + "*");
		}
	}

	private void setTabbs() {

		this.removeAll();
		this.setBackground(null);
		this.add(tabbs);
	}
	
	private void setLogo() {

		JLabel label = new JLabel(new ImageIcon(SwingFactory.create("icons/logo.gif")));
		this.add(label);
		this.setBackground(Color.white);
	}

	public void setInterpreter(IInterpreter interpreter) {

		this.interpreter = interpreter;

		update(interpreter.getEnvironment());
	}

	@Override
	public void reset(Environment environment) {

		areas = new ArrayList<>();

		tabbs.removeAll();
	}

	@Override
	public void update(Environment environment) {

		this.provider.clear();

		for (Entry<LSymbol, Container> entry : environment.entrySet()) {

			provider.addCompletion(new BasicCompletion(provider, entry.getKey().toString()));
		}
	}

	public void save() {

		try {

			Component tmp = getCurrentTab();

			File file = getTabbFor(tmp).file;
			
			System.out.println(file);
			
			String content = getTabbFor(tmp).getText();
			
			Files.write(file.toPath(), content.getBytes(), StandardOpenOption.CREATE);
		}
		catch (Exception e) {

			e.printStackTrace();
			
			logger.error(e.getMessage());
		}
		
		markClean();
	}

	public void saveAll() {
		
		for(int i = 0; i < tabbs.getTabCount(); i++) {
		
			try {
	
				File file= areas.get(i).file;
				
				String content = areas.get(i).getText();
				
				Files.write(file.toPath(), content.getBytes(), StandardOpenOption.CREATE);
			}
			catch (Exception e) {
	
				logger.error(e.getMessage());
			}
		}
		
		markAllClean();
	}
	
	public void saveAs(File selectedFile) {

		Component tmp = getCurrentTab();
		
		getTabbFor(tmp).file = selectedFile;
		
		save();
	}

	public void close() {
		
		Component tmp = getCurrentTab();
		
		tabbs.remove(tmp);
		
		areas.remove(tmp);
		
		if(tabbs.getComponentCount() == 0)
			setLogo();
	}
	
	private Component getCurrentTab() {

		return tabbs.getSelectedComponent();
	}
	
	private Tabb getTabbFor(Component component) {

		System.out.println(Arrays.toString(areas.toArray()));
		System.out.println(component);

		
		return areas.get(areas.indexOf(new Tabb(component)));
	}
	
	public void closeAll() {

		tabbs.removeAll();

		this.areas = new ArrayList<>();
		
		setLogo();
	}
	
	private void markClean() {

		int index = tabbs.getSelectedIndex();

		tabbs.setTitleAt(index, tabbs.getTitleAt(index).replace("*", ""));	
	}
	
	private void markAllClean() {

		for(int i = 0; i < tabbs.getTabCount(); i++) {
	
			tabbs.setTitleAt(i, tabbs.getTitleAt(i).replace("*", ""));
		}
	}

	public String getText() {

		Component tmp = getCurrentTab();

		return getTabbFor(tmp).getText();
	}

	public void setText(String buffer) {

		Component tmp = getCurrentTab();
		
		getTabbFor(tmp).setText(buffer);
	}

	void execute() {
		
		for(int i = 0; i < tabbs.getTabCount(); i++ ) {

			String commands = areas.get(i).getText();
	
			Parser parser = new Parser();
			
			commands = parser.format(commands);

			List<LObject> objects = parser.parseAll(commands);
		
			Executer.instance.eval(objects, interpreter);
		}
	}

	public JPopupMenu getPopupMenu() {
	
		Component tmp = getCurrentTab();
		
		if(tmp != null) {

			return getTabbFor(tmp).getPopupMenu();
		}
		
		return null;
	}

	public boolean isDirty() {

		return dirty;
	}
	
	static class Tabb {
		
		File file;
		
		RTextScrollPane area;
		
		public Tabb(File file, RTextScrollPane area) {

			this.file = file;
			
			this.area = area;
		}

		public Tabb(Component component) {

			this.area = (RTextScrollPane) component;
		}

		public JPopupMenu getPopupMenu() {

			return ((RSyntaxTextArea) area.getComponent(0)).getPopupMenu();
		}

		public void setText(String buffer) {

			((RSyntaxTextArea) area.getComponent(0)).setText(buffer);
		}

		public String getText() {

			return ((RSyntaxTextArea) area.getComponent(0)).getText();
		}
		
		public boolean equals(Object object) {
		
			System.out.println("fgifgj");
			
			return area.equals(((Tabb) object).area);
		}
	}
}