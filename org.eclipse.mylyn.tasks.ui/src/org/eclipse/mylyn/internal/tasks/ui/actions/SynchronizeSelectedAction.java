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
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
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

	private final Map<AbstractRepositoryConnector, List<AbstractRepositoryQuery>> queriesToSyncMap = new LinkedHashMap<AbstractRepositoryConnector, List<AbstractRepositoryQuery>>();

	private final Map<AbstractRepositoryConnector, List<AbstractTask>> tasksToSyncMap = new LinkedHashMap<AbstractRepositoryConnector, List<AbstractTask>>();

	@Override
	public void run(IAction action) {

		if (TaskListView.getFromActivePerspective() != null) {

			ISelection selection = TaskListView.getFromActivePerspective().getViewer().getSelection();
			for (Object obj : ((IStructuredSelection) selection).toList()) {
				if (obj instanceof AbstractRepositoryQuery) {
					final AbstractRepositoryQuery repositoryQuery = (AbstractRepositoryQuery) obj;
					AbstractRepositoryConnector client = TasksUi.getRepositoryManager().getRepositoryConnector(
							repositoryQuery.getConnectorKind());
					if (client != null) {
						List<AbstractRepositoryQuery> queriesToSync = queriesToSyncMap.get(client);
						if (queriesToSync == null) {
							queriesToSync = new ArrayList<AbstractRepositoryQuery>();
							queriesToSyncMap.put(client, queriesToSync);
						}
						queriesToSync.add(repositoryQuery);
					}
				} else if (obj instanceof TaskCategory) {
					TaskCategory cat = (TaskCategory) obj;
					for (AbstractTask task : cat.getChildren()) {
						AbstractRepositoryConnector client = TasksUi.getRepositoryManager().getRepositoryConnector(
								task.getConnectorKind());
						addTaskToSync(client, task);
					}
				} else if (obj instanceof AbstractTask) {
					AbstractTask repositoryTask = (AbstractTask) obj;
					AbstractRepositoryConnector client = TasksUi.getRepositoryManager().getRepositoryConnector(
							repositoryTask.getConnectorKind());
					addTaskToSync(client, repositoryTask);
				}
			}

			ITaskDataManager syncManager = TasksUiPlugin.getTaskDataManager();
			if (!queriesToSyncMap.isEmpty()) {

				// determine which repositories to synch changed tasks for
				HashMap<TaskRepository, Set<AbstractRepositoryQuery>> repositoriesToSync = new HashMap<TaskRepository, Set<AbstractRepositoryQuery>>();
				for (AbstractRepositoryConnector connector : queriesToSyncMap.keySet()) {
					List<AbstractRepositoryQuery> queriesToSync = queriesToSyncMap.get(connector);
					if (queriesToSync == null || queriesToSync.isEmpty()) {
						continue;
					}

					for (AbstractRepositoryQuery query : queriesToSync) {
						TaskRepository repos = TasksUi.getRepositoryManager().getRepository(query.getConnectorKind(),
								query.getRepositoryUrl());
						Set<AbstractRepositoryQuery> queries = repositoriesToSync.get(repos);
						if (queries == null) {
							queries = new HashSet<AbstractRepositoryQuery>();
							repositoriesToSync.put(repos, queries);
						}
						queries.add(query);
					}
				}

				for (Map.Entry<TaskRepository, Set<AbstractRepositoryQuery>> entry : repositoriesToSync.entrySet()) {
					TaskRepository repository = entry.getKey();
					AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
							repository.getConnectorKind());
					Set<AbstractRepositoryQuery> queries = entry.getValue();
					TasksUiInternal.synchronizeQueries(connector, repository, queries, null, true);
				}
			}
			if (!tasksToSyncMap.isEmpty()) {
				for (AbstractRepositoryConnector connector : tasksToSyncMap.keySet()) {
					List<AbstractTask> tasksToSync = tasksToSyncMap.get(connector);
					if (tasksToSync != null && tasksToSync.size() > 0) {
						TasksUiInternal.synchronizeTasks(connector, new HashSet<AbstractTask>(tasksToSync), true, null);
					}
				}
			}

		}

		queriesToSyncMap.clear();
		tasksToSyncMap.clear();

//		TasksUiPlugin.getTaskListManager().getTaskList().notifyContainerUpdated(null);

//		if (TaskListView.getFromActivePerspective() != null) {
//			TaskListView.getFromActivePerspective().getViewer().refresh();
//		}		
	}

	private void addTaskToSync(AbstractRepositoryConnector client, AbstractTask repositoryTask) {
		if (client != null) {
			List<AbstractTask> tasksToSync = tasksToSyncMap.get(client);
			if (tasksToSync == null) {
				tasksToSync = new ArrayList<AbstractTask>();
				tasksToSyncMap.put(client, tasksToSync);
			}
			tasksToSync.add(repositoryTask);
		}
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
