/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.commons.core.ICoreRunnable;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.UnsubmittedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.TaskRepositoryUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

/**
 * @author Mik Kersten
 * @author David Shepherd
 * @author Steffen Pingel
 */
public class DeleteTaskRepositoryAction extends AbstractTaskRepositoryAction {

	private static final String ID = "org.eclipse.mylyn.tasklist.repositories.delete"; //$NON-NLS-1$

	public DeleteTaskRepositoryAction() {
		super(Messages.DeleteTaskRepositoryAction_Delete_Repository);
		setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setId(ID);
		setEnabled(false);
		setActionDefinitionId(IWorkbenchActionDefinitionIds.DELETE);
		setSingleSelect(true);
	}

	public void init(IViewPart view) {
		// ignore
	}

	@Override
	public void run() {
		final TaskRepository repositoryToDelete = getTaskRepository(getStructuredSelection());
		if (repositoryToDelete != null) {
			run(repositoryToDelete);
		}
	}

	public static void run(final TaskRepository repositoryToDelete) {
		Assert.isNotNull(repositoryToDelete);

		final List<IRepositoryQuery> queriesToDelete = new ArrayList<>();
		final List<AbstractTask> tasksToDelete = new ArrayList<>();

		// check for queries over this repository
		Set<RepositoryQuery> queries = TasksUiInternal.getTaskList().getQueries();
		for (IRepositoryQuery query : queries) {
			if (repositoryToDelete.getRepositoryUrl().equals(query.getRepositoryUrl())
					&& repositoryToDelete.getConnectorKind().equals(query.getConnectorKind())) {
				queriesToDelete.add(query);
			}
		}

		// check for tasks from this repository
		final Set<ITask> tasks = TasksUiPlugin.getTaskList().getTasks(repositoryToDelete.getRepositoryUrl());
		for (ITask task : tasks) {
			if (repositoryToDelete.getRepositoryUrl().equals(task.getRepositoryUrl())
					&& repositoryToDelete.getConnectorKind().equals(task.getConnectorKind())) {
				tasksToDelete.add((AbstractTask) task);
			}
		}

		// add unsubmitted tasks
		UnsubmittedTaskContainer unsubmitted = TasksUiPlugin.getTaskList()
				.getUnsubmittedContainer(repositoryToDelete.getRepositoryUrl());
		if (unsubmitted != null) {
			Collection<ITask> children = unsubmitted.getChildren();
			if (children != null) {
				for (ITask task : children) {
					tasksToDelete.add((AbstractTask) task);
				}
			}
		}

		// confirm that the user wants to delete all tasks and queries that are associated
		boolean deleteConfirmed;
		if (queriesToDelete.size() > 0 || tasksToDelete.size() > 0) {
			deleteConfirmed = MessageDialog.openQuestion(WorkbenchUtil.getShell(),
					Messages.DeleteTaskRepositoryAction_Confirm_Delete,
					NLS.bind(Messages.DeleteTaskRepositoryAction_Delete_the_selected_task_repositories,
							new Integer[] { tasksToDelete.size(), queriesToDelete.size() }));
		} else {
			deleteConfirmed = MessageDialog.openQuestion(WorkbenchUtil.getShell(),
					Messages.DeleteTaskRepositoryAction_Confirm_Delete,
					NLS.bind(Messages.DeleteTaskRepositoryAction_Delete_Specific_Task_Repository,
							new String[] { repositoryToDelete.getRepositoryLabel() }));

		}
		if (deleteConfirmed) {
			ICoreRunnable op = monitor -> {
				try {
					monitor.beginTask(Messages.DeleteTaskRepositoryAction_Delete_Repository_In_Progress,
							IProgressMonitor.UNKNOWN);
					DeleteAction.prepareDeletion(tasksToDelete);
					DeleteAction.prepareDeletion(queriesToDelete);
					TasksUiPlugin.getTaskList().run(monitor1 -> {
						// delete tasks
						DeleteAction.performDeletion(tasksToDelete);
						// delete queries
						DeleteAction.performDeletion(queriesToDelete);
						// delete repository
						TasksUiPlugin.getRepositoryManager().removeRepository(repositoryToDelete);
						// if repository is contributed via template, ensure it isn't added again
						TaskRepositoryUtil.disableAddAutomatically(repositoryToDelete.getRepositoryUrl());
					}, monitor);
				} finally {
					monitor.done();
				}
			};
			try {
				WorkbenchUtil.runInUi(op, null);
			} catch (CoreException e) {
				Status status = new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						NLS.bind("Problems encountered deleting task repository: {0}", e.getMessage()), e); //$NON-NLS-1$
				TasksUiInternal.logAndDisplayStatus(Messages.DeleteTaskRepositoryAction_Delete_Task_Repository_Failed,
						status);
			} catch (OperationCanceledException e) {
				// canceled
			}
		}
	}
}
