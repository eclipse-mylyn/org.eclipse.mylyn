/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DefaultTaskMapping;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
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
		for (Object selectedObject : getStructuredSelection().toList()) {
			if (selectedObject instanceof AbstractTask) {
				AbstractTask task = (AbstractTask) selectedObject;

				String description = "Cloned from: " + CopyTaskDetailsAction.getTextForTask(task);
				if (task instanceof LocalTask) {
					String notes = task.getNotes();
					if (!"".equals(notes)) {
						description += "\n\n" + notes;
					}
				}

				ITaskMapping taskSelection = new DefaultTaskMapping();
				((DefaultTaskMapping) taskSelection).setDescription(description);

				TaskData taskData;
				try {
					taskData = TasksUi.getTaskDataManager().getTaskData(task);
				} catch (CoreException e) {
					TasksUiInternal.displayStatus("Clone Task Failed", e.getStatus());
					continue;
				}

				if (taskData != null) {
					AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(taskData.getConnectorKind());
					ITaskMapping mapping = connector.getTaskMapping(taskData);
					if (mapping.getDescription() != null) {
						((DefaultTaskMapping) taskSelection).setDescription(description + "\n\n"
								+ mapping.getDescription());
					}
					mapping.merge(taskSelection);
					taskSelection = mapping;
				}

				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				if (!TasksUiUtil.openNewTaskEditor(shell, taskSelection, null)) {
					// do not process other tasks if canceled
					return;
				}
			}
		}
	}

	public void init(IViewPart view) {
		// ignore
	}

}