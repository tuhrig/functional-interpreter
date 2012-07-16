package de.tuhrig.fujas.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

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

import de.tuhrig.fujas.Environment;
import de.tuhrig.fujas.Parser;
import de.tuhrig.fujas.interfaces.EnvironmentListener;
import de.tuhrig.fujas.interfaces.IInterpreter;
import de.tuhrig.fujas.interfaces.InterpreterListener;
import de.tuhrig.fujas.types.LObject;
import de.tuhrig.fujas.types.LSymbol;

class Editor extends JPanel implements EnvironmentListener, InterpreterListener {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(REPL.class);

	private final DefaultCompletionProvider provider = new DefaultCompletionProvider();

	private IInterpreter interpreter;

	private List<RSyntaxTextArea> areas = new ArrayList<>();
	
	private List<File> files = new ArrayList<>();

	private final JTabbedPane tabbs;

	Editor() {

		tabbs = new JTabbedPane();

		this.setLayout(new BorderLayout(3, 3));
		this.setBorder(new EmptyBorder(5, 5, 5, 5));

		this.add(tabbs);
	}
	
	public void show(File file) {

		RSyntaxTextArea area = SwingFactory.createSyntaxTextArea("editor", "");

		files.add(file);
		areas.add(area);
		
		area.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent event) {

				GUI.gui.markDirty();
				GUI.gui.markGrubby();
			}
		});
		
		AutoCompletion autoCompletion = new AutoCompletion(provider);
		autoCompletion.install(area);

		RTextScrollPane scrollPane = new RTextScrollPane(area);

		scrollPane.setPreferredSize(new Dimension(1, 300));
		
		if(file != null && file.exists()) {
			
			try {

				String content = new Parser().read(file);
				
				area.setText(content);
			}
			catch (Exception e) {

				logger.error(e.getMessage());

				e.printStackTrace();
			}
		}
		
		if(file != null)
			tabbs.addTab(file.getName(), scrollPane);
		
		else
			tabbs.addTab("new file", scrollPane);
	}
	
	public void createNewFile() {
		
		show(null);
	}

	public void setInterpreter(IInterpreter interpreter) {

		this.interpreter = interpreter;

		update(interpreter.getEnvironment());
	}

	@Override
	public void reset(Environment environment) {

		areas = new ArrayList<>();
		
		files = new ArrayList<>();
		
		tabbs.removeAll();
		
		show(null);
	}

	@Override
	public void update(Environment environment) {

		this.provider.clear();

		for (Entry<LSymbol, LObject> entry : environment.entrySet()) {

			provider.addCompletion(new BasicCompletion(provider, entry.getKey().toString()));
		}
	}

	public void save() {

		try {

			int index = tabbs.getSelectedIndex();
			
			File file= files.get(index);
			
			RSyntaxTextArea area = areas.get(index);
			
			Files.write(file.toPath(), area.getText().getBytes(), StandardOpenOption.CREATE);
		}
		catch (Exception e) {

			logger.error(e.getMessage());

			e.printStackTrace();
		}
	}

	public String getText() {

		int index = tabbs.getSelectedIndex();
		
		RSyntaxTextArea area = areas.get(index);
		
		return area.getText();
	}

	public void setText(String buffer) {

		int index = tabbs.getSelectedIndex();
		
		RSyntaxTextArea area = areas.get(index);

		area.setText(buffer);
	}

	void execute() {
		
		for(int i = 0; i < tabbs.getTabCount(); i++ ) {

			RSyntaxTextArea area = areas.get(i);
			
			String commands = area.getText();
	
			Parser parser = new Parser();
			
			commands = parser.format(commands);
	
			List<LObject> objects = parser.parseAll(commands);
		
			Executer.instance.eval(objects, interpreter);
		}
	}

	public JPopupMenu getPopupMenu() {

		int index = tabbs.getSelectedIndex();
		
		RSyntaxTextArea area = areas.get(index);
		
		return area.getPopupMenu();
	}
}