package de.tuhrig.fujas.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;

import de.tuhrig.fujas.Interpreter;
import de.tuhrig.fujas.interfaces.IInterpreter;

class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(GUI.class);

	public static GUI gui;

	private final REPL repl = new REPL();

	private final Inspector inspector = new Inspector();

	private final Editor editor = new Editor();

	private final Log log = new Log();

	private final Status status = new Status();

	private final FileBrowser browser = new FileBrowser();

	private final HistoryView history = new HistoryView();

	private final Running running = new Running();
	
	private File file;

	private final JFileChooser fc = new JFileChooser();

	private boolean dirty = true;

	private boolean grubby = true;

	public GUI() {

		gui = this;

		setFile(null);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.editor, this.repl);
		splitPane.setOneTouchExpandable(true);

		JPanel inner = new JPanel();
		inner.setLayout(new BorderLayout(3, 3));
		inner.setBorder(new EmptyBorder(5, 5, 5, 5));
		inner.add(splitPane);

		JTabbedPane tabbs = new JTabbedPane();
		tabbs.addTab("History", SwingFactory.create("icons/tree.png", 24, 24), history);
		tabbs.addTab("Inspector", SwingFactory.create("icons/inspect.png", 24, 24), inspector);
		
		JXMultiSplitPane msp = new JXMultiSplitPane();

		String layoutDef = 
				"(COLUMN " +
						"(ROW weight=0.8" +
							"(LEAF name=left.top weight=0.2) " + 
							"(LEAF name=left.middle weight=0.5)" + 
							"(LEAF name=editor weight=0.3))" + 
						"(LEAF name=bottom weight=0.2))";

		MultiSplitLayout.Node modelRoot = MultiSplitLayout.parseModel(layoutDef);
		msp.getMultiSplitLayout().setModel(modelRoot);

		msp.add(this.browser, "left.top");
		msp.add(inner, "left.middle");
		msp.add(tabbs, "editor");
		msp.add(this.log, "bottom");

		msp.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		JMenuItem close = SwingFactory.createItem("Close", "icons/Remove.png");
		JMenuItem start = SwingFactory.createItem("Start", "icons/Play.png");
		JMenuItem stop = SwingFactory.createItem("Stop", "icons/Stop.png");	
		JMenuItem reset = SwingFactory.createItem("Reset", "icons/Play All.png");	
		JMenuItem save = SwingFactory.createItem("Save", "icons/User.png");	
		JMenuItem saveAs = SwingFactory.createItem("Save as...", "icons/Users.png");	
		JMenuItem open = SwingFactory.createItem("Open", "icons/New Document.png");
		JMenuItem help = SwingFactory.createItem("Help", "icons/Help Blue Button.png");	
		JMenuItem about = SwingFactory.createItem("About", "icons/iChat Alt.png");
		JMenuItem newFile = SwingFactory.createItem("New", "icons/Document.png");
		
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
	
				logger.warn("================================================");
				logger.warn("Start");
				logger.warn("================================================");

				editor.execute();

				repl.focus();
			}
		});

		reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				GUI.this.setInterpreter(new Interpreter());

				markFresh();
				markClean();

				setFile(null);
			}
		});

		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (file == null) {

					int returnVal = fc.showSaveDialog(GUI.this);

					if (returnVal == JFileChooser.APPROVE_OPTION) {

						setFile(fc.getSelectedFile());

						saveAndMarkClean();
					}
				}
				else {

					saveAndMarkClean();
				}
			}
		});

		saveAs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				int returnVal = fc.showSaveDialog(GUI.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					setFile(fc.getSelectedFile());

					saveAndMarkClean();
				}
			}
		});

		open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				promptUnsavedWarningIfDirty();

				int returnVal = fc.showOpenDialog(GUI.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					GUI.this.load(fc.getSelectedFile());
				}
			}
		});
		
		newFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				GUI.this.editor.show(null);
			}
		});
		
		help.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				try {
					
					Desktop.getDesktop().browse(new URI("https://bitbucket.org/wordless/hdm/wiki/Home"));
				}
				catch (Exception e) {
					
					JOptionPane.showMessageDialog(GUI.this, "Can't access https://bitbucket.org/wordless/hdm/wiki/Home", "Error", JOptionPane.ERROR_MESSAGE);		
				}
			}
		});
		
		about.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				new HTMLViewer(new File("README.md"));
			}
		});
		
		close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				promptUnsavedWarningIfDirty();
							
				System.exit(0);
			}
		});

		JMenu file = SwingFactory.createMenu("File", "icons/Write Document.png");
		file.add(newFile);
		file.add(open);
		file.add(save);
		file.add(saveAs);
		file.add(new JSeparator());
		file.add(close);
		
		final JMenu edit = SwingFactory.createMenu("Edit", "icons/Menu.png");

		edit.addMenuListener(new MenuListener() {
			
			@Override
			public void menuSelected(MenuEvent arg0) {
			
				JMenu editorMenu = SwingFactory.createMenu("Editor", "icons/editor.png", editor.getPopupMenu());
				JMenu replMenu = SwingFactory.createMenu("Console", "icons/console.png", repl.getPopupMenu());
				
				edit.removeAll();
				edit.add(editorMenu);
				edit.add(replMenu);
			}
			
			@Override
			public void menuDeselected(MenuEvent arg0) {}
			
			@Override
			public void menuCanceled(MenuEvent arg0) {}
		});

		JMenu run = SwingFactory.createMenu("Run", "icons/Gear Alt.png");
		run.add(start);
		run.add(reset);
		run.add(stop);
		
		JMenu info = SwingFactory.createMenu("More", "icons/Add.png");
		info.add(help);
		info.add(about);
		
		JMenuBar menubar = SwingFactory.createMenuBar();
		menubar.add(file);
		menubar.add(edit);
		menubar.add(run);
		menubar.add(info);

		this.setJMenuBar(menubar);

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {

				promptUnsavedWarningIfDirty();				
			}
		});
		
		this.markClean();
		this.markFresh();
		
		this.setInterpreter(new Interpreter());
		this.setLayout(new BorderLayout());
		this.getContentPane().add(status, BorderLayout.NORTH);
		this.getContentPane().add(msp, BorderLayout.CENTER);
		this.getContentPane().add(running, BorderLayout.SOUTH);
		
		this.setPreferredSize(new Dimension(1400, 800));
		this.pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		repl.focus();
		
		logger.info("GUI started");
	}

	protected void load(File file) {

		setFile(file);

		GUI.this.setInterpreter(new Interpreter());

		editor.show(file);

		markClean();

		markGrubby();
	}

	private void promptUnsavedWarningIfDirty() {

		if (dirty) {
			
			int confirmed = JOptionPane.showConfirmDialog(null, "Save file?", "Unsaved edits", JOptionPane.YES_NO_OPTION);
	
			if (confirmed == JOptionPane.YES_OPTION) {
	
				if (file == null) {
	
					int returnVal = fc.showSaveDialog(GUI.this);
	
					if (returnVal == JFileChooser.APPROVE_OPTION) {
	
						setFile(fc.getSelectedFile());
	
						saveAndMarkClean();
					}
				}
				else {
	
					saveAndMarkClean();
				}
			}
		}
	}

	private void setInterpreter(IInterpreter interpreter) {

		this.editor.setInterpreter(interpreter);
		this.repl.setInterpreter(interpreter);

		this.editor.reset(interpreter.getEnvironment());
		this.repl.reset(interpreter.getEnvironment());
		this.inspector.reset(interpreter.getEnvironment());
		
		interpreter.addHistoryListener(history);
		
		interpreter.addEnvironmentListener(inspector);
		interpreter.addEnvironmentListener(repl);
		interpreter.addEnvironmentListener(editor);
	}

	private void setFile(File file) {

		this.file = file;

		if(file == null) {
			
			setTitle("no file");
		}
		else { 
			
			this.setTitle(file.getAbsolutePath());
		}
	}

	void markDirty() {

		if (!dirty) {

			this.dirty = true;

			this.setTitle(getTitle() + "*");
		}
	}

	private void markClean() {

		if (dirty) {

			this.dirty = false;

			this.setTitle(getTitle().replace("*", ""));
		}
	}

	void markGrubby() {

		if (!grubby) {

			this.grubby = true;

			this.status.setMessage("Definitions have changed! Restart the interpreter!");
			this.status.setColor(Color.RED);
		}
	}

	void markFresh() {

		if (grubby) {

			this.grubby = false;

			this.status.setMessage("Ready");
			this.status.setColor(Color.GREEN);
		}
	}

	private void saveAndMarkClean() {

		editor.save();

		markClean();
	}
	
	public void stop() {

		editor.setEnabled(false);
		repl.setEnabled(false);
		
		running.stop();
	}

	public void start() {

		editor.setEnabled(true);
		repl.setEnabled(false);
		
		running.start();
	}

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		
//		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		Locale.setDefault(Locale.ENGLISH);
		
		new GUI().setVisible(true);
	}
}