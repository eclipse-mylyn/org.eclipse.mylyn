/*******************************************************************************
 * Copyright (c) 2005, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui;

import java.text.MessageFormat;
import java.util.Calendar;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Retrieves an existing repository task and adds it to the tasklist
 * 
 * @author Willian Mitsuda
 */
public class AddExistingTaskJob extends Job {

	/**
	 * Task repository whose task will be added
	 */
	private final TaskRepository repository;

	/**
	 * Identifies a existing task on the repository
	 */
	private final String taskId;

	/**
	 * Optional; informs the task container the task initialy belongs to; if null, it will be added to the current
	 * selected task's category in task list
	 */
	private final AbstractTaskCategory taskContainer;

	public AddExistingTaskJob(TaskRepository repository, String taskId) {
		this(repository, taskId, null);
	}

	public AddExistingTaskJob(TaskRepository repository, String taskId, AbstractTaskCategory taskContainer) {
		super(MessageFormat.format("Adding task: \"{0}\"...", taskId));
		this.repository = repository;
		this.taskId = taskId;
		this.taskContainer = taskContainer;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			final AbstractTask newTask = TasksUiUtil.createTask(repository, taskId, monitor);
			if (newTask != null) {
				Calendar newSchedule = TaskActivityUtil.getCalendar();
				TaskActivityUtil.snapEndOfWorkDay(newSchedule);
				TasksUiPlugin.getTaskActivityManager().setScheduledFor(newTask, newSchedule.getTime());

				TasksUiInternal.refreshAndOpenTaskListElement(newTask);
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					public void run() {
						AbstractTaskCategory category = taskContainer;
						TaskListView taskListView = TaskListView.getFromActivePerspective();
						if (category == null) {
							Object selectedObject = ((IStructuredSelection) taskListView.getViewer().getSelection()).getFirstElement();
							if (selectedObject instanceof TaskCategory) {
								category = (TaskCategory) selectedObject;
							}
						}
						TasksUi.getTaskListManager().getTaskList().addTask(newTask, category);
						taskListView.getViewer().setSelection(new StructuredSelection(newTask));
					}
				});
			} else {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					public void run() {
						IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
						if (window != null) {
							MessageDialog.openWarning(window.getShell(), "Add Existing Task Failed",
									MessageFormat.format("Unable to retrieve task \"{0}\" from repository.", taskId));
						}
					}

				});
			}
		} catch (final CoreException e) {
			TasksUiInternal.displayStatus("Unable to open task", e.getStatus());
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}
}
