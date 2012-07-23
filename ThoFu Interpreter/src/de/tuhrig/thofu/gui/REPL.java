package de.tuhrig.thofu.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Element;

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

class REPL extends JPanel implements EnvironmentListener, InterpreterListener {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(REPL.class);

	private final DefaultCompletionProvider provider = new DefaultCompletionProvider();

	private List<String> history = new ArrayList<String>();

	private IInterpreter interpreter;

	private RSyntaxTextArea textArea;

	private final String welcome = "ThoFu Interpreter\nPress CTRL + ENTER to submit a command\n>> ";

	REPL() {

		setPreferredSize(new Dimension(100, 100));
		
		// create the RSyntaxArea as an editor for the REPL
		textArea = SwingFactory.createSyntaxTextArea("repl", welcome); 
		
		final RTextScrollPane scrollPane = new RTextScrollPane(textArea);
		
		// a key listener to process CTRL + Enter (= submit a command)
		textArea.addKeyListener(new KeyAdapter() {

			private int arrow = 0;

			private String lastInserted = "";

			public void keyReleased(KeyEvent event) {

				int code = event.getKeyCode();

				if (code == 17) {

					arrow = 0;
					lastInserted = "";
				}
			}

			@Override
			public void keyPressed(KeyEvent event) {

				int code = event.getKeyCode();
				int modifiers = event.getModifiers();

				int caret = textArea.getCaretPosition();
				int last = textArea.getText().lastIndexOf(">> ") + 3;

				/**
				 * Make old parts non editable
				 */
				if ((!(code == KeyEvent.VK_C && modifiers == KeyEvent.CTRL_MASK) && code != KeyEvent.VK_CONTROL) && caret <= last) {

					if (code == KeyEvent.VK_BACK_SPACE) {

						textArea.append(" ");

						textArea.setCaretPosition(last + 1);
					}

					textArea.setCaretPosition(textArea.getText().length());
				}

				/**
				 * C + CTRL --> CANCEL
				 */
				if (code == KeyEvent.VK_C && modifiers == KeyEvent.CTRL_MASK) {

					if (textArea.getSelectedText() == null)
						textArea.append(" [CANCEL]\n>> ");
				}

				/**
				 * ARROW-UP + CTRL --> HISTORY
				 */
				if (code == 38 && modifiers == KeyEvent.CTRL_MASK) {

					if (history.size() > arrow) {

						String text = textArea.getText();

						textArea.setText(text.substring(0, text.length() - lastInserted.length()));

						textArea.append(history.get(arrow));

						lastInserted = history.get(arrow);
					}

					if (arrow < history.size())
						arrow++;
				}

				/**
				 * ARROW-DOWN + CTRL --> HISTORY
				 */
				if (code == 40 && modifiers == KeyEvent.CTRL_MASK) {

					if (history.size() > arrow && arrow <= 0) {

						String text = textArea.getText();

						textArea.setText(text.substring(0, text.length() - lastInserted.length()));

						textArea.append(history.get(arrow));

						lastInserted = history.get(arrow);
					}

					if (arrow > 0)
						arrow--;
				}

				/**
				 * ENTER + CTRL --> SUBMIT COMMAND
				 */
				if (code == KeyEvent.VK_ENTER && modifiers == KeyEvent.CTRL_MASK) {

					Dimension pref = scrollPane.getPreferredSize();
					
					textArea.setText(textArea.getText() + "\n");

					try {

						int lines = textArea.getLineCount();

						try {

							String input = "";

							int lineCount = 2;

							while (true) {

								// if we have the first line of the command
								if (input.startsWith(">>")) {

									input = input.replaceFirst(">>", "").trim();
									break;
								}
								// if we have another line
								else {

									Element line = textArea.getDocument().getDefaultRootElement().getElement(lines - lineCount);

									int lineStart = line.getStartOffset();
									int lineEnd = line.getEndOffset();

									input = textArea.getText(lineStart, lineEnd - lineStart) + input;
									lineCount++;
								}
							}

							logger.info("Input: " + input);

							history.add(0, input);
		
							Parser parser = new Parser();
							
							input = parser.format(input);

							List<LObject> objects = parser.parseAll(input);
						
							Executer.instance.evaluate(textArea, objects, interpreter);

//							SwingUtilities.invokeLater(new Runnable() {
//
//								public void run() {
//
//									textArea.append(value + "\n>> ");
//
//									GUI.gui.stop();
//								}
//							});
						}
						catch (Exception e) {

							textArea.setText(textArea.getText() + "Error" + "\n>> ");
							e.printStackTrace();
						}
					}
					catch (ArrayIndexOutOfBoundsException e) {

						// ignore
					}

					scrollPane.setPreferredSize(pref);
				}
			}
		});

		AutoCompletion autoCompletion = new AutoCompletion(provider);
		autoCompletion.install(textArea);

		this.setLayout(new BorderLayout(3, 3));
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		this.add(scrollPane);
	}

	public void setInterpreter(IInterpreter interpreter) {

		this.interpreter = interpreter;

		update(interpreter.getEnvironment());
	}

	public void reset(Environment environment) {

		textArea.setText(welcome);
		focus();
	}

	@Override
	public void update(Environment environment) {

		logger.debug("Update auto-completion in " + this.getClass());

		this.provider.clear();

		for (Entry<LSymbol, Container> entry : environment.entrySet()) {

			provider.addCompletion(new BasicCompletion(provider, entry.getKey().toString()));
		}
	}

	void focus() {

		textArea.setCaretPosition(textArea.getDocument().getLength());
		textArea.requestFocusInWindow();
		textArea.requestFocus();
	}

	public JPopupMenu getPopupMenu() {

		return textArea.getPopupMenu();
	}
}