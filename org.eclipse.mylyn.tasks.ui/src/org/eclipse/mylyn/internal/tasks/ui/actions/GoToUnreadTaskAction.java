/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask.SynchronizationState;
import org.eclipse.mylyn.internal.tasks.ui.util.TreeWalker;
import org.eclipse.mylyn.internal.tasks.ui.util.TreeWalker.Direction;
import org.eclipse.mylyn.internal.tasks.ui.util.TreeWalker.TreeVisitor;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Steffen Pingel
 */
public class GoToUnreadTaskAction extends Action implements IViewActionDelegate, IWorkbenchWindowActionDelegate {

	public static final String ID_NEXT = "org.eclipse.mylyn.tasklist.actions.goToNextUnread";

	public static final String ID_PREVIOUS = "org.eclipse.mylyn.tasklist.actions.goToPreviousUnread";

	private TaskListView taskListView;

	private Direction direction = Direction.DOWN;

	public void dispose() {
		// ignore		
	}

	public Direction getDirection() {
		return direction;
	}

	private TreePath getUnreadItem(TreeViewer treeViewer, Tree tree) {
		TreeItem[] selection = tree.getSelection();
		TreeItem selectedItem = (selection.length > 0) ? selection[0] : null;

		TreeVisitor visitor = new TreeVisitor() {
			@Override
			public boolean visit(Object object) {
				if (object instanceof ITask) {
					ITask task = (ITask) object;
					if (task.getSynchronizationState() == SynchronizationState.INCOMING) {
						return true;
					}
				}
				return false;
			}
		};

		TreeWalker treeWalker = new TreeWalker(treeViewer);
		treeWalker.setDirection(direction);
		treeWalker.setExpandNodes(true);
		return treeWalker.walk(visitor, selectedItem);
	}

	public void init(IViewPart view) {
		this.taskListView = (TaskListView) view;
	}

	public void init(IWorkbenchWindow window) {
	}

	@Override
	public void run() {
		TreeViewer treeViewer;
		if (taskListView == null) {
			TaskListView activeTaskListView = TaskListView.getFromActivePerspective();
			if (activeTaskListView == null) {
				return;
			}
			treeViewer = activeTaskListView.getViewer();
		} else {
			treeViewer = taskListView.getViewer();
		}

		Tree tree = treeViewer.getTree();

		// need to expand nodes to traverse the tree, disable redraw to avoid flickering
		TreePath treePath = null;
		try {
			tree.setRedraw(false);
			treePath = getUnreadItem(treeViewer, tree);
		} finally {
			tree.setRedraw(true);
		}

		if (treePath != null) {
			treeViewer.expandToLevel(treePath, 0);
			treeViewer.setSelection(new TreeSelection(treePath), true);
		}
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

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

}