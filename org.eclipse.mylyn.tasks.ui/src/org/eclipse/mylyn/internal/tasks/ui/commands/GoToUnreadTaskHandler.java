/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.ui.util.TreeWalker;
import org.eclipse.mylyn.internal.tasks.ui.util.TreeWalker.Direction;
import org.eclipse.mylyn.internal.tasks.ui.util.TreeWalker.TreeVisitor;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Steffen Pingel
 */
public abstract class GoToUnreadTaskHandler extends AbstractTaskListViewHandler {

	public static final String ID_NEXT = "org.eclipse.mylyn.tasklist.actions.goToNextUnread"; //$NON-NLS-1$

	public static final String ID_PREVIOUS = "org.eclipse.mylyn.tasklist.actions.goToPreviousUnread"; //$NON-NLS-1$

	private Direction direction = Direction.DOWN;

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
					if (task.getSynchronizationState().isIncoming()) {
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

	@Override
	protected void execute(ExecutionEvent event, TaskListView taskListView, IRepositoryElement item) {
		TreeViewer treeViewer = taskListView.getViewer();
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

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public static void execute(ExecutionEvent event, Direction direction) throws ExecutionException {
		GoToUnreadTaskHandler handler = new GoToUnreadTaskHandler() {
		};
		handler.setDirection(direction);
		handler.execute(event);
	}

	public static class GoToNextUnreadTaskHandler extends GoToUnreadTaskHandler {

		public GoToNextUnreadTaskHandler() {
			setDirection(Direction.DOWN);
		}

	}

	public static class GoToPreviousUnreadTaskHandler extends GoToUnreadTaskHandler {

		public GoToPreviousUnreadTaskHandler() {
			setDirection(Direction.UP);
		}

	}

}