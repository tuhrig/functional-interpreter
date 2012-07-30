package de.tuhrig.thofu.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

import de.tuhrig.thofu.Container;
import de.tuhrig.thofu.Environment;
import de.tuhrig.thofu.interfaces.EnvironmentListener;
import de.tuhrig.thofu.types.LLambda;
import de.tuhrig.thofu.types.LOperation;
import de.tuhrig.thofu.types.LSymbol;

class Inspector extends JPanel implements EnvironmentListener {

	private static final long serialVersionUID = 1L;

	private JTree tree;

	private TreeModel model;

	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Environment");

	private DefaultMutableTreeNode variables = new DefaultMutableTreeNode("Variables");

	private DefaultMutableTreeNode operations = new DefaultMutableTreeNode("Operations");
	
	private DefaultMutableTreeNode lambdas = new DefaultMutableTreeNode("Lambdas");

	private Set<TreePath> expanded = new HashSet<TreePath>();

	private Environment environment;

	Inspector() {

		this.setLayout(new BorderLayout(3, 3));
		this.setBorder(new EmptyBorder(5, 5, 5, 5));

		root.add(variables);
		root.add(operations);
		root.add(lambdas);
		
		this.model = new DefaultTreeModel(root) {

			private static final long serialVersionUID = 1L;

			public Object getRoot() {

				return root;
			}

			@Override
			public Object getChild(Object arg0, int arg1) {

				if (arg0.equals(root) && arg1 == 0)
					return variables;

				if (arg0.equals(root) && arg1 == 1)
					return operations;
				
				if (arg0.equals(root) && arg1 == 2)
					return lambdas;

				if (arg0.equals(variables)) {

					List<DefaultMutableTreeNode> objects = new ArrayList<DefaultMutableTreeNode>();

					for (Entry<LSymbol, Container> entry : environment.entrySet()) {

						if ((entry.getValue().getObject() instanceof LOperation) == false) {

							objects.add(new DefaultMutableTreeNode(entry.getKey() + " = " + entry.getValue().getObject()));
						}
					}

					return objects.get(arg1);
				}

				if (arg0.equals(operations)) {

					List<DefaultMutableTreeNode> objects = new ArrayList<DefaultMutableTreeNode>();

					for (Entry<LSymbol, Container> entry : environment.entrySet()) {

						if ((entry.getValue().getObject() instanceof LOperation) == true &&
							(entry.getValue().getObject() instanceof LLambda) == false) {

							objects.add(new DefaultMutableTreeNode(entry.getKey() + " = " + entry.getValue().getObject()));
						}
					}

					return objects.get(arg1);
				}
				
				if (arg0.equals(lambdas)) {

					List<DefaultMutableTreeNode> objects = new ArrayList<DefaultMutableTreeNode>();

					for (Entry<LSymbol, Container> entry : environment.entrySet()) {

						if ((entry.getValue().getObject() instanceof LLambda) == true) {
							
							objects.add(new DefaultMutableTreeNode(entry.getKey() + " = " + entry.getValue().getObject()));
						}
					}

					return objects.get(arg1);
				}

				return null;
			}

			@Override
			public int getChildCount(Object arg0) {

				if (arg0.equals(root))
					return 3;

				if (arg0.equals(variables)) {

					int count = 0;

					for (Entry<LSymbol, Container> entry : environment.entrySet()) {

						if ((entry.getValue().getObject() instanceof LOperation) == false) {

							count++;
						}
					}

					return count;
				}

				if (arg0.equals(operations)) {

					int count = 0;

					for (Entry<LSymbol, Container> entry : environment.entrySet()) {

						if ((entry.getValue().getObject() instanceof LOperation) == true &&
							(entry.getValue().getObject() instanceof LLambda) == false	) {

							count++;
						}
					}

					return count;
				}
				
				if (arg0.equals(lambdas)) {

					int count = 0;

					for (Entry<LSymbol, Container> entry : environment.entrySet()) {

						if ((entry.getValue().getObject() instanceof LLambda) == true) {

							count++;
						}
					}

					return count;
				}

				return 0;
			}

			@Override
			public boolean isLeaf(Object arg0) {

				return 
						arg0.equals(root) == false && 
						arg0.equals(variables) == false && 
						arg0.equals(operations) == false && 
						arg0.equals(lambdas) == false ;
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

		this.add(new JScrollPane(tree));
	}

	public void update(Environment environment) {

		this.environment = environment;

		((DefaultTreeModel) tree.getModel()).reload();

		for (final TreePath path : expanded) {

			SwingUtilities.invokeLater(new Runnable() {

				public void run() {

					tree.expandPath(path);
				}
			});
		}
	}

	public void reset(Environment environment) {

		tree.removeAll();

		update(environment);
	}
}