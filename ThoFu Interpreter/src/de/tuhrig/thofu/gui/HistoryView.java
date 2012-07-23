package de.tuhrig.thofu.gui;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import de.tuhrig.thofu.gui.History.Identifier;
import de.tuhrig.thofu.interfaces.HistoryListener;
import de.tuhrig.thofu.types.LList;
import de.tuhrig.thofu.types.LObject;

public class HistoryView extends JPanel implements HistoryListener {

	private static final long serialVersionUID = 1L;
	
	private final Set<TreePath> expanded = new HashSet<TreePath>();

	private final History history = History.instance;

	private JTree tree;

	private TreeModel model;

	private DefaultMutableTreeNode root;

	public HistoryView() {
		
		this.setLayout(new BorderLayout(3, 3));
		this.setBorder(new EmptyBorder(5, 5, 5, 5));

		root = new DefaultMutableTreeNode("Commands");

		this.model = new DefaultTreeModel(root) {

			private static final long serialVersionUID = 1L;
			
			public Object getRoot() {

				return root;
			}
			
			@Override
			public Object getChild(Object o, int index) {
				
				if(o.equals(root)) {
					
					Entry<Identifier, LList> entry = history.get(index);
	
					Identifier identifier = entry.getKey();
					long elapsed = identifier.getElapsedTime();

					return new DefaultMutableTreeNode("#" + identifier.number + " (" + elapsed + " ms)");
				}
				else if(o.toString().startsWith("#")) {
					
					int i = Integer.parseInt(o.toString().replace("#", "").split(" ")[0]);
					
					Entry<Identifier, LList> entry = history.get(i-1);

					return new DefaultMutableTreeNode(entry.getValue());
				}
				
				if(o instanceof LList) {
					
					return new DefaultMutableTreeNode(((LList) o).get(index));
				}
				
				if(o instanceof DefaultMutableTreeNode) {
					
					Object tmp = ((DefaultMutableTreeNode) o).getUserObject();
					
					if(tmp instanceof LList)
						return new DefaultMutableTreeNode(((LList) tmp).get(index));
				}
				
				return null;
			}

			@Override
			public int getChildCount(Object o) {
				
				if(o.equals(root)) {
					
					return history.size();
				}
				else if(o.toString().startsWith("#")) {
					
					return 1;
				}
				
				if(o instanceof DefaultMutableTreeNode) {
					
					Object tmp = ((DefaultMutableTreeNode) o).getUserObject();
					
					if(tmp instanceof LList)
						return ((LList) tmp).size();
				}
				
				return 0;
			}

			@Override
			public boolean isLeaf(Object o) {
				
				if(o.equals(root)) {
					
					return false;
				}
				else if(o.toString().startsWith("#")) {
					
					return false;
				}

				if(o instanceof DefaultMutableTreeNode) {
					
					Object tmp = ((DefaultMutableTreeNode) o).getUserObject();
					
					if(tmp instanceof LObject && !(tmp instanceof LList))
						return true;
				}
				
				if((o instanceof DefaultMutableTreeNode) || (o instanceof LList)) {
					
					return false;
				}
				
				return true;
			}
		};
		
		tree = new JTree(model);
		
		tree.expandRow(0);
		tree.setVisibleRowCount(15);
		
		tree.addTreeExpansionListener(new TreeExpansionListener() {

			@Override
			public void treeExpanded(TreeExpansionEvent e) {

				expanded.add(e.getPath());
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent e) {

				expanded.remove(e.getPath());
			}
		});

		add(new JScrollPane(tree));
	}

	@Override
	public void update(LList tokens, Date started, Date ended) {

		history.add(tokens, started, ended);
		
		((DefaultTreeModel) tree.getModel()).reload();
		
		for (final TreePath path : expanded) {

			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					
					tree.expandPath(path);
				}
			});
		}
	}
}