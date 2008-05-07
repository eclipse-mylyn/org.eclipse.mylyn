/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * Action to import a task from an external file. Imports also task's related repository and context information.
 * 
 * @author Jevgeni Holodkov
 */
public class TaskImportAction extends Action implements IViewActionDelegate {

	protected ISelection selection;

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

	@Override
	public void run() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog dialog = new FileDialog(shell);
		dialog.setFilterExtensions(new String[] { "*" + ITasksCoreConstants.FILE_EXTENSION });

		String path = dialog.open();
		if (path != null) {
			File file = new File(path);
			if (file.isFile()) {

				Map<AbstractTask, InteractionContext> taskContexts = new HashMap<AbstractTask, InteractionContext>();

				List<AbstractTask> readTasks = TasksUiPlugin.getTaskListManager().getTaskListWriter().readTasks(file);
				for (AbstractTask task : readTasks) {
					// deactivate all tasks
					task.setActive(false);
					taskContexts.put(task, ContextCorePlugin.getContextManager().loadContext(
							task.getHandleIdentifier(), file));
				}
				Set<TaskRepository> repositories = TasksUiPlugin.getTaskListManager()
						.getTaskListWriter()
						.readRepositories(file);

				if (taskContexts.size() > 0) {
					importTasks(taskContexts, repositories, shell);
					refreshTaskListView();
				} else {
					MessageDialog.openError(shell, "Task Import Error",
							"The specified file is not an exported task. Please, check that you have provided the correct file.");
					return;
				}
			}
		}
		return;
	}

	public void refreshTaskListView() {
		if (TaskListView.getFromActivePerspective() != null) {
			TaskListView.getFromActivePerspective().refresh();
		}
	}

	/**
	 * @param queries
	 * @param repositories
	 * @param shell
	 * @return true if any task imported
	 */
	public void importTasks(Map<AbstractTask, InteractionContext> taskContexts, Set<TaskRepository> repositories,
			Shell shell) {
		TasksUiPlugin.getRepositoryManager().insertRepositories(repositories,
				TasksUiPlugin.getDefault().getRepositoriesFilePath());

		for (AbstractTask loadedTask : taskContexts.keySet()) {
			TaskList taskList = TasksUiPlugin.getTaskList();
			if (taskList.getTask(loadedTask.getHandleIdentifier()) != null) {
				boolean confirmed = MessageDialog.openConfirm(shell, ITasksUiConstants.TITLE_DIALOG, "Task '"
						+ loadedTask.getSummary()
						+ "' already exists. Do you want to override it's context with the source?");
				if (confirmed) {
					ContextCorePlugin.getContextManager().importContext(taskContexts.get(loadedTask));
				}
			} else {
				ContextCorePlugin.getContextManager().importContext(taskContexts.get(loadedTask));
				TasksUi.getTaskList().addTask(loadedTask);
			}
		}

	}

}
