/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Steffen Pingel
 */
public class RefreshRepositoryTasksAction extends AbstractTaskRepositoryAction implements IViewActionDelegate {

	private static final String ID = "org.eclipse.mylyn.tasklist.repositories.refreshAllTasks"; //$NON-NLS-1$

	public RefreshRepositoryTasksAction() {
		super(Messages.RefreshRepositoryTasksAction_Refresh_All_Tasks);
		setId(ID);
		setEnabled(false);
	}

	@Override
	public void run() {
		IStructuredSelection selection = getStructuredSelection();
		for (Object selectedObject : selection) {
			if (selectedObject instanceof TaskRepository repository) {
				synchronizeAllTasks(repository);
			}
		}
	}

	private void synchronizeAllTasks(TaskRepository repository) {
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager()
				.getRepositoryConnector(repository.getConnectorKind());
		if (connector != null) {
			Set<ITask> repositoryTasks = TasksUiPlugin.getTaskList().getTasks(repository.getRepositoryUrl());
			TasksUiInternal.synchronizeTasks(connector, repositoryTasks, true, null);
		}
	}

	@Override
	public void init(IViewPart view) {
	}

	@Override
	public void run(IAction action) {
		run();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		selectionChanged((IStructuredSelection) selection);
	}

}
