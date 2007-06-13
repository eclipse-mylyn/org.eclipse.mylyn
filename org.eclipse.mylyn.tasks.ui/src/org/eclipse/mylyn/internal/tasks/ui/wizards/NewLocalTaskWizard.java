/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Rob Elves
 */
public class NewLocalTaskWizard extends Wizard implements INewWizard {

	public NewLocalTaskWizard() {
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
		setNeedsProgressMonitor(true);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setForcePreviousAndNextButtons(false);
	}

	@Override
	public void addPages() {
		// ignore
	}

	@Override
	public boolean canFinish() {
		return true;
	}

	@Override
	public boolean performFinish() {

//		LocalTask newTask = new LocalTask(LocalRepositoryConnector.REPOSITORY_URL, TasksUiPlugin.getTaskListManager()
//				.genUniqueTaskHandle(), LocalRepositoryConnector.DEFAULT_SUMMARY);
//		TaskListManager.scheduleNewTask(newTask);
//
//		Object selectedObject = null;
//		TaskListView view = TaskListView.getFromActivePerspective();
//		if (view != null) {
//			selectedObject = ((IStructuredSelection) view.getViewer().getSelection()).getFirstElement();
//		}
//		if (selectedObject instanceof TaskCategory) {
//			TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask, (TaskCategory) selectedObject);
//		} else if (selectedObject instanceof ITask) {
//			ITask task = (ITask) selectedObject;
//			if (task.getContainer() instanceof TaskCategory) {
//				TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask, task.getContainer());
//			} else if (view != null && view.getDrilledIntoCategory() instanceof TaskCategory) {
//				TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask, view.getDrilledIntoCategory());
//			} else {
//				TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask,
//						TasksUiPlugin.getTaskListManager().getTaskList().getUncategorizedCategory());
//			}
//		} else if (view != null && view.getDrilledIntoCategory() instanceof TaskCategory) {
//			TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask, view.getDrilledIntoCategory());
//		} else {
//			if (view != null && view.getDrilledIntoCategory() != null) {
//				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), ITasksUiConstants.TITLE_DIALOG,
//						"The new task has been added to the root of the list, since tasks can not be added to a query.");
//			}
//			TasksUiPlugin.getTaskListManager().getTaskList().addTask(newTask,
//					TasksUiPlugin.getTaskListManager().getTaskList().getUncategorizedCategory());
//		}
		ITask task = TasksUiPlugin.getTaskListManager().createNewLocalTask(null);
		if (task != null) {
			TasksUiUtil.openEditor(task, true);
			return true;
		} else {
			return false;
		}

	}

}
