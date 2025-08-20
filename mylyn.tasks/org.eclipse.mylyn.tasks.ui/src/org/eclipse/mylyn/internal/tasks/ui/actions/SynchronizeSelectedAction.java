/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.Person;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskGroup;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class SynchronizeSelectedAction extends ActionDelegate implements IViewActionDelegate {

	@Override
	public void run(IAction action) {
		TaskListView taskListView = TaskListView.getFromActivePerspective();
		if (taskListView != null) {
			ISelection selection = taskListView.getViewer().getSelection();
			if (selection.isEmpty()) {
				TasksUiInternal.synchronizeAllRepositories(true);
			} else if (selection instanceof IStructuredSelection) {
				synchronizeSelected((IStructuredSelection) selection);
			}
		}
	}

	private void synchronizeSelected(IStructuredSelection selection) {
		Map<AbstractRepositoryConnector, List<RepositoryQuery>> queriesToSyncMap = new LinkedHashMap<>();
		Map<AbstractRepositoryConnector, List<ITask>> tasksToSyncMap = new LinkedHashMap<>();

		// collect queries and tasks
		for (Object obj : selection.toList()) {
			if (obj instanceof IRepositoryQuery) {
				final RepositoryQuery repositoryQuery = (RepositoryQuery) obj;
				AbstractRepositoryConnector client = TasksUi.getRepositoryManager()
						.getRepositoryConnector(repositoryQuery.getConnectorKind());
				if (client != null) {
					List<RepositoryQuery> queriesToSync = queriesToSyncMap.get(client);
					if (queriesToSync == null) {
						queriesToSync = new ArrayList<>();
						queriesToSyncMap.put(client, queriesToSync);
					}
					queriesToSync.add(repositoryQuery);
				}
			} else if (obj instanceof TaskCategory cat) {
				for (ITask task : cat.getChildren()) {
					AbstractRepositoryConnector client = TasksUi.getRepositoryManager()
							.getRepositoryConnector(task.getConnectorKind());
					addTaskToSync(client, task, tasksToSyncMap);
				}
			} else if (obj instanceof ITask) {
				AbstractTask repositoryTask = (AbstractTask) obj;
				AbstractRepositoryConnector client = TasksUi.getRepositoryManager()
						.getRepositoryConnector(repositoryTask.getConnectorKind());
				addTaskToSync(client, repositoryTask, tasksToSyncMap);
			} else if (obj instanceof ScheduledTaskContainer scheduledContainer) {
				for (ITask task : scheduledContainer.getChildren()) {
					AbstractRepositoryConnector client = TasksUi.getRepositoryManager()
							.getRepositoryConnector(task.getConnectorKind());
					addTaskToSync(client, task, tasksToSyncMap);
				}
			} else if (obj instanceof Person person) {
				for (ITask task : person.getChildren()) {
					AbstractRepositoryConnector client = TasksUi.getRepositoryManager()
							.getRepositoryConnector(task.getConnectorKind());
					addTaskToSync(client, task, tasksToSyncMap);
				}
			} else if (obj instanceof TaskGroup group) {
				for (ITask task : group.getChildren()) {
					AbstractRepositoryConnector client = TasksUi.getRepositoryManager()
							.getRepositoryConnector(task.getConnectorKind());
					addTaskToSync(client, task, tasksToSyncMap);
				}
			}
		}

		// update queries
		if (!queriesToSyncMap.isEmpty()) {
			// determine which repositories to synch changed tasks for
			HashMap<TaskRepository, Set<RepositoryQuery>> repositoriesToSync = new HashMap<>();
			for (AbstractRepositoryConnector connector : queriesToSyncMap.keySet()) {
				List<RepositoryQuery> queriesToSync = queriesToSyncMap.get(connector);
				if (queriesToSync == null || queriesToSync.isEmpty()) {
					continue;
				}

				for (RepositoryQuery query : queriesToSync) {
					TaskRepository repos = TasksUi.getRepositoryManager()
							.getRepository(query.getConnectorKind(), query.getRepositoryUrl());
					if (repos == null) {
						StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								"Failed to synchronize query \"" + query.getUrl() //$NON-NLS-1$
										+ "\" because Repository is null")); //$NON-NLS-1$
						continue;
					}

					Set<RepositoryQuery> queries = repositoriesToSync.get(repos);
					if (queries == null) {
						queries = new HashSet<>();
						repositoriesToSync.put(repos, queries);
					}
					queries.add(query);
				}
			}

			for (Map.Entry<TaskRepository, Set<RepositoryQuery>> entry : repositoriesToSync.entrySet()) {
				TaskRepository repository = entry.getKey();
				AbstractRepositoryConnector connector = TasksUi.getRepositoryManager()
						.getRepositoryConnector(repository.getConnectorKind());
				Set<RepositoryQuery> queries = entry.getValue();
				TasksUiInternal.synchronizeQueries(connector, repository, queries, null, true);
			}
		}
		// update tasks
		if (!tasksToSyncMap.isEmpty()) {
			for (AbstractRepositoryConnector connector : tasksToSyncMap.keySet()) {
				List<ITask> tasksToSync = tasksToSyncMap.get(connector);
				if (tasksToSync != null && tasksToSync.size() > 0) {
					TasksUiInternal.synchronizeTasks(connector, new HashSet<>(tasksToSync), true, null);
				}
			}
		}
	}

	private void addTaskToSync(AbstractRepositoryConnector connector, ITask task,
			Map<AbstractRepositoryConnector, List<ITask>> tasksToSyncMap) {
		if (connector == null //
				|| task instanceof LocalTask //
				|| task.getSynchronizationState() == SynchronizationState.OUTGOING_NEW) {
			return;
		}

		List<ITask> tasksToSync = tasksToSyncMap.get(connector);
		if (tasksToSync == null) {
			tasksToSync = new ArrayList<>();
			tasksToSyncMap.put(connector, tasksToSync);
		}
		tasksToSync.add(task);
	}

	private IAction action;

	@Override
	public void init(IAction action) {
		this.action = action;
	}

	@Override
	public void init(IViewPart view) {
		IActionBars actionBars = view.getViewSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.REFRESH.getId(), action);
		actionBars.updateActionBars();
	}

}
