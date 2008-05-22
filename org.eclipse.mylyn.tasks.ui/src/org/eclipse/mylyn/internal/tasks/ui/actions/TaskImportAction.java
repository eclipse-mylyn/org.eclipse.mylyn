/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.TaskRepository;
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
				Set<TaskRepository> repositories = TasksUiPlugin.getTaskListManager()
						.getTaskListWriter()
						.readRepositories(file);
				Collection<AbstractTask> readTasks = TasksUiPlugin.getTaskListManager().getTaskListWriter().readTasks(
						file);
				if (readTasks.size() > 0) {
					TasksUiInternal.importTasks(readTasks, repositories, file, shell);
//					refreshTaskListView();
				} else {
					MessageDialog.openError(shell, "Task Import Error",
							"The specified file is not an exported task. Please, check that you have provided the correct file.");
					return;
				}
			}
		}
		return;
	}
}
