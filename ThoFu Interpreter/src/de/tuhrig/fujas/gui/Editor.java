package de.tuhrig.fujas.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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

	private final RSyntaxTextArea textArea;

	Editor() {

		textArea = SwingFactory.createSyntaxTextArea("editor", "");

		textArea.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent event) {

				GUI.gui.markDirty();

				GUI.gui.markGrubby();
			}
		});
		
		AutoCompletion autoCompletion = new AutoCompletion(provider);
		autoCompletion.install(textArea);

		RTextScrollPane scrollPane = new RTextScrollPane(textArea);

		scrollPane.setPreferredSize(new Dimension(1, 300));

		this.setLayout(new BorderLayout(3, 3));
		this.setBorder(new EmptyBorder(5, 5, 5, 5));

		this.add(scrollPane);
	}

	public void setInterpreter(IInterpreter interpreter) {

		this.interpreter = interpreter;

		update(interpreter.getEnvironment());
	}

	@Override
	public void reset(Environment environment) {

		textArea.setText("");
	}

	@Override
	public void update(Environment environment) {

		this.provider.clear();

		for (Entry<LSymbol, LObject> entry : environment.entrySet()) {

			provider.addCompletion(new BasicCompletion(provider, entry.getKey().toString()));
		}
	}

	public void save(File file) {

		try {

			Files.write(file.toPath(), textArea.getText().getBytes(), StandardOpenOption.CREATE);
		}
		catch (Exception e) {

			logger.error(e.getMessage());

			e.printStackTrace();
		}
	}

	public void load(File file) {

		try {

			String content = new Parser().read(file);
			
			textArea.setText(content);
		}
		catch (Exception e) {

			logger.error(e.getMessage());

			e.printStackTrace();
		}
	}

	public String getText() {

		return textArea.getText();
	}

	public void setText(String buffer) {

		textArea.setText(buffer);
	}

	void execute() {

		String commands = textArea.getText();

		Parser parser = new Parser();
		
		commands = parser.format(commands);

		List<LObject> objects = parser.parseAll(commands);
	
		Executer.instance.eval(objects, interpreter);
	}

	public JPopupMenu getPopupMenu() {

		return textArea.getPopupMenu();
	}
}