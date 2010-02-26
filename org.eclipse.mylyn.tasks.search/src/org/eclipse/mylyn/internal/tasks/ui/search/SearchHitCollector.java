/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.PlatformUI;

/**
 * Used for returning results from Eclipse Search view. Collects results of a repository search.
 * 
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class SearchHitCollector extends TaskDataCollector implements ISearchQuery {

	private final ITaskList taskList;

	private final TaskRepository repository;

	private final IRepositoryQuery repositoryQuery;

	private final RepositorySearchResult searchResult;

	private AbstractRepositoryConnector connector;

	public SearchHitCollector(ITaskList tasklist, TaskRepository repository, IRepositoryQuery repositoryQuery) {
		this.taskList = tasklist;
		this.repository = repository;
		this.repositoryQuery = repositoryQuery;
		this.searchResult = new RepositorySearchResult(this);
	}

	public void aboutToStart() {
		searchResult.removeAll();
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				NewSearchUI.activateSearchResultView();
			}
		});
	}

	@Override
	public void accept(TaskData taskData) {
		ITask task = taskList.getTask(repository.getRepositoryUrl(), taskData.getTaskId());
		if (task == null) {
			task = TasksUi.getRepositoryModel().createTask(repository, taskData.getTaskId());
			if (connector != null) {
				connector.updateTaskFromTaskData(repository, task, taskData);
			}
		}
		searchResult.addMatch(new Match(task, 0, 0));
	}

	public String getLabel() {
		return Messages.SearchHitCollector_Querying_Repository_;
	}

	public boolean canRerun() {
		return true;
	}

	public boolean canRunInBackground() {
		return true;
	}

	public ISearchResult getSearchResult() {
		return searchResult;
	}

	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
		monitor = Policy.monitorFor(monitor);

		aboutToStart();

		if (monitor.isCanceled()) {
			throw new OperationCanceledException(Messages.SearchHitCollector_Search_cancelled);
		}
		connector = TasksUi.getRepositoryManager().getRepositoryConnector(repositoryQuery.getConnectorKind());
		if (connector != null) {
			final IStatus status = connector.performQuery(repository, repositoryQuery, this, null, monitor);
			if (!status.isOK()) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						TasksUiInternal.displayStatus(Messages.SearchHitCollector_Search_failed, status);
					}
				});
			} else {
				if (searchResult.getMatchCount() >= TaskDataCollector.MAX_HITS) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							TasksUiInternal.displayStatus(Messages.SearchHitCollector_Search_returned_maximum_number_of_hits,
									RepositoryStatus.createStatus(repository.getRepositoryUrl(), IStatus.WARNING,
											TasksUiPlugin.ID_PLUGIN, Messages.SearchHitCollector_Max_allowed_number_of_hits_returned_exceeded));
						}
					});
				}
			}
		} else {
			return new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, IStatus.OK,
					Messages.SearchHitCollector_Repository_connector_could_not_be_found, null);
		}

		return Status.OK_STATUS;
	}

	public IRepositoryQuery getRepositoryQuery() {
		return repositoryQuery;
	}

}
