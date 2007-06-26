/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class MoveTaskToRootAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.move.toroot";

	// private final TaskListView view;
	public MoveTaskToRootAction(TaskListView view) {
		// this.view = view;
		setText("Move Task to Root");
		setToolTipText("Move Task to Root");
		setId(ID);
	}

	@Override
	public void run() {
		throw new RuntimeException("unimplemented");
		// ISelection selection = this.view.getViewer().getSelection();
		// Object obj = ((IStructuredSelection)selection).getFirstElement();
		// if (obj instanceof ITask) {
		// ITask t = (ITask) obj;
		// TaskCategory cat = t.getCategory();
		// if (cat != null) {
		// cat.removeTask(t);
		// t.setCategory(null);
		// t.setParent(null);
		// MylarTaskListPlugin.getTaskListManager().getTaskList().addRootTask(t);
		// this.view.getViewer().refresh();
		// } else if (t.getParent() != null) {
		// t.getParent().removeSubTask(t);
		// t.setParent(null);
		// MylarTaskListPlugin.getTaskListManager().getTaskList().addRootTask(t);
		// this.view.getViewer().refresh();
		// }
		// }
	}
}
