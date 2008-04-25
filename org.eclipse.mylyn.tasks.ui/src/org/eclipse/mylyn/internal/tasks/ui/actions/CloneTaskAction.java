/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskSelection;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Maarten Meijer
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class CloneTaskAction extends BaseSelectionListenerAction implements IViewActionDelegate {

	private static final String LABEL = "Clone This Task";

	private static final String ID = "org.eclipse.mylyn.tasklist.actions.clone";

	protected ISelection selection;

	public CloneTaskAction() {
		super(LABEL);
		setId(ID);
		setImageDescriptor(TasksUiImages.TASK_NEW);
		setAccelerator(SWT.MOD1 + 'd');
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			super.selectionChanged((IStructuredSelection) selection);
		}
	}

	public void run(IAction action) {
		run();
	}

	@Override
	public void run() {
		try {
			for (Object selectedObject : getStructuredSelection().toList()) {
				if (selectedObject instanceof AbstractTask) {
					AbstractTask task = (AbstractTask) selectedObject;

					String description = "Cloned from: " + CopyTaskDetailsAction.getTextForTask(task);

					final TaskSelection taskSelection;
					RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(
							task.getRepositoryUrl(), task.getTaskId());
					if (taskData != null) {
						taskSelection = new TaskSelection(taskData);
						taskSelection.getTaskData().setDescription(description + "\n\n> " + taskData.getDescription());
					} else {
						taskSelection = new TaskSelection(task);
						if (task instanceof LocalTask) {
							String notes = task.getNotes();
							if (!"".equals(notes)) {
								taskSelection.getTaskData().setDescription(description + "\n\n" + notes);
							} else {
								taskSelection.getTaskData().setDescription(description);
							}
						} else {
							taskSelection.getTaskData().setDescription(description);
						}
					}

					Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
					if (!TasksUiUtil.openNewTaskEditor(shell, taskSelection, null)) {
						// do not process other tasks if canceled
						return;
					}
				}
			}
		} catch (NullPointerException e) {
			// FIXME check for null instead?
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Could not remove task from category, it may still be refreshing.", e));
		}
	}

	public void init(IViewPart view) {
		// ignore
	}

}