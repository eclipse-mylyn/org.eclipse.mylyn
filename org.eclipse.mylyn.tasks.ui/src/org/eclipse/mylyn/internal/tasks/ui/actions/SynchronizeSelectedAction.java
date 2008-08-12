/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
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

	private final Map<AbstractRepositoryConnector, List<RepositoryQuery>> queriesToSyncMap = new LinkedHashMap<AbstractRepositoryConnector, List<RepositoryQuery>>();

	private final Map<AbstractRepositoryConnector, List<ITask>> tasksToSyncMap = new LinkedHashMap<AbstractRepositoryConnector, List<ITask>>();

	@Override
	public void run(IAction action) {

		if (TaskListView.getFromActivePerspective() != null) {

			ISelection selection = TaskListView.getFromActivePerspective().getViewer().getSelection();
			for (Object obj : ((IStructuredSelection) selection).toList()) {
				if (obj instanceof IRepositoryQuery) {
					final RepositoryQuery repositoryQuery = (RepositoryQuery) obj;
					AbstractRepositoryConnector client = TasksUi.getRepositoryManager().getRepositoryConnector(
							repositoryQuery.getConnectorKind());
					if (client != null) {
						List<RepositoryQuery> queriesToSync = queriesToSyncMap.get(client);
						if (queriesToSync == null) {
							queriesToSync = new ArrayList<RepositoryQuery>();
							queriesToSyncMap.put(client, queriesToSync);
						}
						queriesToSync.add(repositoryQuery);
					}
				} else if (obj instanceof TaskCategory) {
					TaskCategory cat = (TaskCategory) obj;
					for (ITask task : cat.getChildren()) {
						AbstractRepositoryConnector client = TasksUi.getRepositoryManager().getRepositoryConnector(
								task.getConnectorKind());
						addTaskToSync(client, task);
					}
				} else if (obj instanceof ITask) {
					AbstractTask repositoryTask = (AbstractTask) obj;
					AbstractRepositoryConnector client = TasksUi.getRepositoryManager().getRepositoryConnector(
							repositoryTask.getConnectorKind());
					addTaskToSync(client, repositoryTask);
				}
			}

			ITaskDataManager syncManager = TasksUiPlugin.getTaskDataManager();
			if (!queriesToSyncMap.isEmpty()) {

				// determine which repositories to synch changed tasks for
				HashMap<TaskRepository, Set<RepositoryQuery>> repositoriesToSync = new HashMap<TaskRepository, Set<RepositoryQuery>>();
				for (AbstractRepositoryConnector connector : queriesToSyncMap.keySet()) {
					List<RepositoryQuery> queriesToSync = queriesToSyncMap.get(connector);
					if (queriesToSync == null || queriesToSync.isEmpty()) {
						continue;
					}

					for (RepositoryQuery query : queriesToSync) {
						TaskRepository repos = TasksUi.getRepositoryManager().getRepository(query.getConnectorKind(),
								query.getRepositoryUrl());
						Set<RepositoryQuery> queries = repositoriesToSync.get(repos);
						if (queries == null) {
							queries = new HashSet<RepositoryQuery>();
							repositoriesToSync.put(repos, queries);
						}
						queries.add(query);
					}
				}

				for (Map.Entry<TaskRepository, Set<RepositoryQuery>> entry : repositoriesToSync.entrySet()) {
					TaskRepository repository = entry.getKey();
					AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
							repository.getConnectorKind());
					Set<RepositoryQuery> queries = entry.getValue();
					TasksUiInternal.synchronizeQueries(connector, repository, queries, null, true);
				}
			}
			if (!tasksToSyncMap.isEmpty()) {
				for (AbstractRepositoryConnector connector : tasksToSyncMap.keySet()) {
					List<ITask> tasksToSync = tasksToSyncMap.get(connector);
					if (tasksToSync != null && tasksToSync.size() > 0) {
						TasksUiInternal.synchronizeTasks(connector, new HashSet<ITask>(tasksToSync), true, null);
					}
				}
			}

		}

		queriesToSyncMap.clear();
		tasksToSyncMap.clear();
	}

	private void addTaskToSync(AbstractRepositoryConnector connector, ITask task) {
		if (connector == null //
				|| task instanceof LocalTask //
				|| task.getSynchronizationState() == SynchronizationState.OUTGOING_NEW) {
			return;
		}

		List<ITask> tasksToSync = tasksToSyncMap.get(connector);
		if (tasksToSync == null) {
			tasksToSync = new ArrayList<ITask>();
			tasksToSyncMap.put(connector, tasksToSync);
		}
		tasksToSync.add(task);
	}

	private IAction action;

	@Override
	public void init(IAction action) {
		this.action = action;
	}

	public void init(IViewPart view) {
		IActionBars actionBars = view.getViewSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.REFRESH.getId(), action);
		actionBars.updateActionBars();
	}

}
