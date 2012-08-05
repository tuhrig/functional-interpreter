package de.tuhrig.thofu.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;

import de.tuhrig.thofu.Interpreter;
import de.tuhrig.thofu.Parser;
import de.tuhrig.thofu.interfaces.IDebugger;
import de.tuhrig.thofu.interfaces.IInterpreter;

/**
 * The GUI.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public class ThoFuUi extends JFrame {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(ThoFuUi.class);

	public static ThoFuUi gui;

	private final Repl repl = new Repl();

	private final Inspector inspector = new Inspector();

	private final Editor editor = new Editor();

	private final Status status = new Status();

	private final static Running running = new Running();
	
	private final static HistoryView history = new HistoryView();
	
	private final static Debugger debugger = new Debugger();
	
	private final JFileChooser fc = new JFileChooser();

	private boolean grubby = true;

	private JMenuItem start;

	private JMenuItem reset;

	ThoFuUi() {
		
		gui = this;

		// Create the components of the GUI
		final FileBrowser browser = new FileBrowser();
		final Log log = new Log();
		
		// editor area (middle)
		JSplitPane editorArea = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.editor, this.repl);
		editorArea.setOneTouchExpandable(true);
		editorArea.setName("Editor area");

		// main area
		JPanel inner = new JPanel();
		inner.setLayout(new BorderLayout(3, 3));
		inner.setBorder(new EmptyBorder(5, 5, 5, 5));
		inner.add(editorArea);

		// tools (right)
		JTabbedPane tools = new JTabbedPane();
		tools.addTab("Debugger", SwingFactory.create("icons/Appointment Urgent.png", 24, 24), debugger);
		tools.addTab("Inspector", SwingFactory.create("icons/inspect.png", 24, 24), inspector);
		tools.addTab("History", SwingFactory.create("icons/tree.png", 24, 24), history);
		
		// multi split pane
		JXMultiSplitPane msp = new JXMultiSplitPane();

		String layoutDef = 
				"(COLUMN " +
						"(ROW weight=0.8" +
							"(LEAF name=left.top weight=0.25) " + 
							"(LEAF name=left.middle weight=0.5)" + 
							"(LEAF name=editor weight=0.25))" + 
						"(LEAF name=bottom weight=0.2))";

		MultiSplitLayout.Node modelRoot = MultiSplitLayout.parseModel(layoutDef);
		msp.getMultiSplitLayout().setModel(modelRoot);
		
		msp.add(browser, "left.top");
		msp.add(inner, "left.middle");
		msp.add(tools, "editor");
		msp.add(log, "bottom");
		
		msp.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		// some size settings
		browser.setPreferredSize(new Dimension(100, 100));
		log.setPreferredSize(new Dimension(100, 100));
		tools.setPreferredSize(new Dimension(100, 100));		
		
		// menu
		JMenuItem exit = SwingFactory.createItem("Exit", "icons/Remove.png");
		start = SwingFactory.createItem("Start Script", "icons/Play.png");
		JMenuItem stop = SwingFactory.createItem("Stop", "icons/Stop.png");	
		reset = SwingFactory.createItem("Reset Interpreter", "icons/Play All.png");	
		
		JMenuItem save = SwingFactory.createItem("Save", "icons/User.png");	
		JMenuItem saveAll = SwingFactory.createItem("Save All", "icons/Users.png");	
		JMenuItem saveAs = SwingFactory.createItem("Save as...", "icons/Add.png");	
		
		JMenuItem close = SwingFactory.createItem("Close", "icons/Pause.png");	
		JMenuItem closeAll = SwingFactory.createItem("Close All", "icons/Pause All.png");		
		
		JMenuItem open = SwingFactory.createItem("Open", "icons/New Document.png");
		JMenuItem about = SwingFactory.createItem("About", "icons/iChat Alt.png");
		JMenuItem newFile = SwingFactory.createItem("New", "icons/Document.png");
		
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
	
				logger.warn("================================================");
				logger.warn("Started Script");
				logger.warn("================================================");

				editor.execute();

				repl.focus();
				
				markFresh();
			}
		});

		close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				editor.close();
			}
		});
		
		closeAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				editor.closeAll();
			}
		});
		
		reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				debugger.setDebugg(false);
				
				setInterpreter(new Interpreter());
				
				if(editor.openFiles())
					markGrubby();
				else
					markFresh();
			}
		});

		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				editor.save();
			}
		});

		saveAs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				int returnVal = fc.showSaveDialog(ThoFuUi.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					editor.saveAs(fc.getSelectedFile());
				}
			}
		});

		open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				int returnVal = fc.showOpenDialog(ThoFuUi.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					open(fc.getSelectedFile());
				}
			}
		});
		
		newFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				int returnVal = fc.showSaveDialog(ThoFuUi.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					open(fc.getSelectedFile());
				}
			}
		});

		about.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				String content = new Parser().read(getClass(), "README.md");

				new HtmlViewer(content);	
			}
		});
		
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				promptUnsavedWarningIfDirty();
							
				System.exit(0);
			}
		});

		JMenu file = SwingFactory.createMenu("File", "icons/Write Document.png");
		file.add(newFile);
		file.add(open);
		file.add(new JSeparator());
		file.add(save);
		file.add(saveAll);
		file.add(saveAs);
		file.add(new JSeparator());
		file.add(close);
		file.add(closeAll);
		file.add(new JSeparator());
		file.add(exit);
		
		final JMenu edit = SwingFactory.createMenu("Edit", "icons/Menu.png");

		edit.addMenuListener(new MenuListener() {
			
			@Override
			public void menuSelected(MenuEvent arg0) {
				
			    edit.removeAll();

			    JMenu editorMenu = SwingFactory.createMenu("Editor", "icons/editor.png", editor.getPopupMenu());
			    edit.add(editorMenu);
			    
				if(editor.getPopupMenu() == null) {

					editorMenu.setEnabled(false);
				}
				
				JMenu replMenu = SwingFactory.createMenu("Console", "icons/console.png", repl.getPopupMenu());
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
		
		this.markFresh();
		this.setInterpreter(new Interpreter());
		this.getContentPane().add(status, BorderLayout.NORTH);
		this.getContentPane().add(msp, BorderLayout.CENTER);
		this.getContentPane().add(running, BorderLayout.SOUTH);
		
		this.setExtendedState(Frame.MAXIMIZED_BOTH); 
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("Thomas' Functional Interpreter - ThoFu!");
		this.setLocationRelativeTo(null);
		this.setSize(new Dimension(1000, 800));
		
		logger.info("GUI started");
	}

	protected void open(File file) {

		editor.open(file);

		markGrubby();
	}

	private void promptUnsavedWarningIfDirty() {

		if (editor.isDirty()) {
			
			int confirmed = JOptionPane.showConfirmDialog(null, "Save all files?", "Unsaved edits", JOptionPane.YES_NO_OPTION);
	
			if (confirmed == JOptionPane.YES_OPTION) {

				editor.saveAll();
			}
		}
	}

	private void setInterpreter(IInterpreter interpreter) {

		editor.setInterpreter(interpreter);
		repl.setInterpreter(interpreter);

		repl.reset(interpreter.getEnvironment());
		inspector.reset(interpreter.getEnvironment());

		interpreter.addHistoryListener(history);
		interpreter.addEnvironmentListener(inspector);
		interpreter.addEnvironmentListener(repl);
		interpreter.addEnvironmentListener(editor);
	}

	void markGrubby() {

		if (!grubby) {

			grubby = true;

			status.setMessage("Definitions have changed! Start Script!");
			status.setColor(Color.RED);
		}
	}

	void markFresh() {

		if (grubby) {

			grubby = false;

			status.setMessage("Ready");
			status.setColor(Color.GREEN);
		}
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

	public int showSaveDialog() {

		return fc.showSaveDialog(this);
	}
	
	public File getSelectedFile() {

		return fc.getSelectedFile();
	}
	
	public void setCurrentDirectory(File file) {

		fc.setCurrentDirectory(file);
	}
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, InterruptedException {

		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		
//		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		Locale.setDefault(Locale.ENGLISH);

		new ThoFuUi();

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				gui.setVisible(true);
				gui.repl.focus();
			}
		});
	}

	public void enableControls(boolean b) {

		repl.setEnabled(b);
		start.setEnabled(b);
		reset.setEnabled(b);
	}

	public static IDebugger getDebugger() {

		return debugger;
	}
}