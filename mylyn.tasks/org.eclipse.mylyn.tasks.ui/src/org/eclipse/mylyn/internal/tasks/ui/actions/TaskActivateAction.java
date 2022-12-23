/*******************************************************************************
 
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 */
public class TaskActivateAction extends BaseSelectionListenerAction implements IViewActionDelegate {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.context.activate"; //$NON-NLS-1$

	public TaskActivateAction() {
		super(Messages.TaskActivateAction_Activate);
		setId(ID);
		setActionDefinitionId("org.eclipse.mylyn.tasks.ui.command.activateSelectedTask"); //$NON-NLS-1$
		setImageDescriptor(TasksUiImages.CONTEXT_ACTIVE_CENTERED);
	}

	public void init(IViewPart view) {
		// ignore
	}

	@Override
	public void run() {
		TaskListView taskListView = TaskListView.getFromActivePerspective();
		if (taskListView != null) {
			run(taskListView.getSelectedTask());
		}
	}

	@Deprecated
	public void run(ITask task) {
		if (task != null && !task.isActive()) {
			TasksUi.getTaskActivityManager().activateTask(task);
		}
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			selectionChanged((IStructuredSelection) selection);
		} else {
			selectionChanged(StructuredSelection.EMPTY);
		}
		action.setEnabled(isEnabled());
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		return selection != null && selection.size() == 1 && selection.getFirstElement() instanceof ITask;
	}

}
