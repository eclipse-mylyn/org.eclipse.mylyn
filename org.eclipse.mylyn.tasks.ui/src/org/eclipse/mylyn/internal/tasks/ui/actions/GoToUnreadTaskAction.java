/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Steffen Pingel
 */
public class GoToUnreadTaskAction extends Action implements IViewActionDelegate {

	public enum Direction {
		UP, DOWN
	};

	public static final String ID_NEXT = "org.eclipse.mylyn.tasklist.actions.goToNextUnread";

	public static final String ID_PREVIOUS = "org.eclipse.mylyn.tasklist.actions.goToPreviousUnread";

	private TaskListView taskListView;

	private Direction direction = Direction.DOWN;

	public void init(IViewPart view) {
		this.taskListView = (TaskListView) view;
	}

	@Override
	public void run() {
		TreeViewer treeViewer = taskListView.getViewer();

		Tree tree = treeViewer.getTree();

		try {
			tree.setRedraw(false);

			TreePath treePath = getUnreadItem(treeViewer, tree);
			if (treePath != null) {
				treeViewer.expandToLevel(treePath, 0);
				treeViewer.setSelection(new TreeSelection(treePath));
				treeViewer.reveal(treePath);
			}
		} finally {
			tree.setRedraw(true);
		}
	}

	public Direction getDirection() {
		return direction;
	}
	
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	private TreePath getTreePath(TreeItem item) {
		List<Object> path = new ArrayList<Object>();
		do {
			path.add(0, item.getData());
			item = item.getParentItem();
		} while (item != null);
		return new TreePath(path.toArray());
	}

	private TreePath getUnreadItem(TreeViewer treeViewer, Tree tree) {
		TreeItem[] selection = tree.getSelection();
		TreeItem selectedItem = (selection.length > 0) ? selection[0] : null;

		Visitor visitor = new Visitor() {
			@Override
			public boolean visit(Object object) {
				if (object instanceof AbstractTask) {
					AbstractTask task = (AbstractTask) object;
					if (task.getSynchronizationState() == RepositoryTaskSyncState.INCOMING) {
						return true;
					}
				}
				return false;
			}
		};

		TreePath unreadItem = null;
		if (selectedItem != null) {
			if (direction == Direction.DOWN) {
				unreadItem = visitChildren(treeViewer, getTreePath(selectedItem), selectedItem, visitor);
			}
			if (unreadItem == null) {
				unreadItem = visitSiblings(treeViewer, selectedItem, visitor);
			}
		} else {
			unreadItem = visitItems(treeViewer, TreePath.EMPTY, tree.getItems(), null, visitor);
		}

		return unreadItem;
	}

	private TreePath visitSiblings(TreeViewer viewer, TreeItem item, Visitor visitor) {
		TreeItem[] siblings;
		TreePath path;
		TreeItem parent = item.getParentItem();
		if (parent != null) {
			siblings = parent.getItems();
			path = getTreePath(parent);
		} else {
			siblings = viewer.getTree().getItems();
			path = TreePath.EMPTY;
		}
		return visitItems(viewer, path, siblings, item, visitor);
	}

	private TreePath visitItems(TreeViewer viewer, TreePath parentPath, TreeItem[] items, TreeItem visitedItem,
			Visitor visitor) {
		if (direction == Direction.UP) {
			Collections.reverse(Arrays.asList(items));
		}
		
		boolean found = (visitedItem == null);
		for (TreeItem item : items) {
			if (!found) {
				if (item == visitedItem) {
					found = true;
				}
			} else {
				TreePath itemPath = parentPath.createChildPath(item.getData());

				if (direction == Direction.DOWN) {
					if (visitor.visit(item.getData())) {
						return itemPath;
					}
				}
				
				TreePath childPath = visitChildren(viewer, itemPath, item, visitor);
				if (childPath != null) {
					return childPath;
				}
				
				if (direction == Direction.UP) {
					if (visitor.visit(item.getData())) {
						return itemPath;
					}
				}
			}
		}

		// visit parent siblings
		if (visitedItem != null) {
			TreeItem parent = visitedItem.getParentItem();
			if (parent != null) {
				if (direction == Direction.UP) {
					if (visitor.visit(parent.getData())) {
						return parentPath;
					}
				}

				return visitSiblings(viewer, parent, visitor);
			}
		}

		return null;
	}

	private TreePath visitChildren(TreeViewer viewer, TreePath itemPath, TreeItem item, Visitor visitor) {
		// expand item
		boolean expandedState = item.getExpanded();
		if (!expandedState) {
			viewer.setExpandedState(itemPath, true);
		}

		TreeItem[] children = item.getItems();
		if (children.length > 0) {
			TreePath childPath = visitItems(viewer, itemPath, children, null, visitor);
			if (childPath != null) {
				return childPath;
			}
		}

		// restore item state
		viewer.setExpandedState(itemPath, expandedState);

		return null;
	}

	public void run(IAction action) {
		if (ID_PREVIOUS.equals(action.getId())) {
			setDirection(Direction.UP);
		} else {
			setDirection(Direction.DOWN);
		}
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

	private abstract class Visitor {

		public abstract boolean visit(Object object);

	}
	
}