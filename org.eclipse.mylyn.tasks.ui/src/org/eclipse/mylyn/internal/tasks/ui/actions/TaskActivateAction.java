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
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public class TaskActivateAction extends Action implements IViewActionDelegate {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.context.activate";

	public TaskActivateAction() {
		setId(ID);
		setText("Activate");
		setImageDescriptor(TasksUiImages.CONTEXT_ACTIVE_CENTERED);
	}

	public void init(IViewPart view) {
		// ignore
	}

	@Override
	public void run() {
		run(TaskListView.getFromActivePerspective().getSelectedTask());
	}

	public void run(AbstractTask task) {
		if (task != null && !task.isActive()) {
			TasksUi.getTaskListManager().activateTask(task);
		}
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}
}
