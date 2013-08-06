package de.tuhrig.thofu.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * A basic File Manager. Requires 1.6+ for the Desktop & SwingWorker classes, amongst other minor
 * things.
 * 
 * Includes support classes FileTableModel & FileTreeCellRenderer.
 * 
 * @author Andrew Thompson
 * @version 2011-06-01
 * @see http://stackoverflow.com/questions/6182110
 * @license LGPL
 */
class FileBrowser extends JPanel {

	private static final long serialVersionUID = 1L;

	private FileSystemView fileSystemView;

	private JTree tree;

	private DefaultTreeModel treeModel;

	public FileBrowser() {

		this.setLayout(new BorderLayout(3, 3));
		this.setBorder(new EmptyBorder(5, 5, 5, 5));

		fileSystemView = FileSystemView.getFileSystemView();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode();

		treeModel = new DefaultTreeModel(root);

		TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {

			public void valueChanged(TreeSelectionEvent tse) {

				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();

				showChildren(node);
			}
		};

		// show the file system roots.
		File[] roots = fileSystemView.getRoots();

		for (File fileSystemRoot : roots) {

			DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);

			root.add(node);

			File[] files = fileSystemView.getFiles(fileSystemRoot, true);

			for (File file : files) {

				node.add(new DefaultMutableTreeNode(file));
			}
		}

		tree = new JTree(treeModel);
		tree.setRootVisible(false);
		tree.addTreeSelectionListener(treeSelectionListener);
		tree.setCellRenderer(new FileTreeCellRenderer());
		tree.expandRow(0);
		tree.setVisibleRowCount(15);

		tree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent me) {

				setCurrentDirectory(me);
				
				if(me.getClickCount() == 2) {
	
					TreePath tp = tree.getPathForLocation(me.getX(), me.getY());

					if(tp != null) {
						
						File file = new File(tp.getLastPathComponent().toString());
	
						if (file.exists() && file.isFile()) {
	
							ThoFuUi.instance().open(file);
						}
					}
				}
				
				if(me.isMetaDown()) {
		
					JPopupMenu menu = new JPopupMenu();
					        
					JMenuItem newFile = SwingFactory.createItem("New", "icons/Document.png");
					menu.add(newFile);
			        menu.show(me.getComponent(), me.getX(), me.getY());
			        
					newFile.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {

							int returnVal = ThoFuUi.instance().showSaveDialog();

							if (returnVal == JFileChooser.APPROVE_OPTION) {

								ThoFuUi.instance().open(ThoFuUi.instance().getSelectedFile());
							}
						}
					});
				}
			}
		});

		this.add(new JScrollPane(tree));
	}

	public void setCurrentDirectory(MouseEvent me) {
	
		TreePath tp = tree.getPathForLocation(me.getX(), me.getY());

		if(tp != null) {
		
			File file = new File(tp.getLastPathComponent().toString());
			
			if(file.isDirectory()) {
				
				ThoFuUi.instance().setCurrentDirectory(file);
			}
			else {
				
				ThoFuUi.instance().setCurrentDirectory(file.getParentFile());
			}
		}
	}

	/**
	 * Add the files that are contained within the directory of this node. Thanks to Hovercraft Full
	 * Of Eels.
	 */
	private void showChildren(final DefaultMutableTreeNode node) {

		tree.setEnabled(false);

		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {

			@Override
			public Void doInBackground() {

				File file = (File) node.getUserObject();

				if (file.isDirectory()) {

					File[] files = fileSystemView.getFiles(file, true);

					if (node.isLeaf()) {

						for (File child : files) {

							publish(child);
						}
					}
				}

				return null;
			}

			@Override
			protected void process(List<File> chunks) {

				for (File child : chunks) {
					node.add(new DefaultMutableTreeNode(child));
				}
			}

			@Override
			protected void done() {

				tree.setEnabled(true);
			}
		};

		worker.execute();
	}
}

/** A TreeCellRenderer for a File. */
class FileTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private FileSystemView fileSystemView;

	private JLabel label;

	FileTreeCellRenderer() {

		label = new JLabel();
		label.setOpaque(true);
		fileSystemView = FileSystemView.getFileSystemView();
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		File file = (File) node.getUserObject();
		label.setIcon(fileSystemView.getSystemIcon(file));
		label.setText(fileSystemView.getSystemDisplayName(file));
		label.setToolTipText(file.getPath());

		if (selected) {
			label.setBackground(backgroundSelectionColor);
			label.setForeground(textSelectionColor);
		}
		else {
			label.setBackground(backgroundNonSelectionColor);
			label.setForeground(textNonSelectionColor);
		}

		return label;
	}
}