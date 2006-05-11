/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public class TaskActivateAction extends Action implements IViewActionDelegate {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.context.activate";

	public ITask task = null;

	public TaskActivateAction() {
		setId(ID);
		setText("Activate");
		setImageDescriptor(TaskListImages.TASK_ACTIVE);
	}

	public void init(IViewPart view) {
		// TODO Auto-generated method stub

	}

	public void run() {
//		MylarPlugin.getContextManager().actionObserved(this, Boolean.TRUE.toString());
		run(TaskListView.getDefault().getSelectedTask());
	}

	public void run(ITask task) {
		if (task != null) {
			MylarTaskListPlugin.getTaskListManager().activateTask(task);
			if (TaskListView.getDefault() != null) {
				TaskListView.getDefault().refreshAndFocus();
			}
		}
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}
}
