/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.search;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.LegacyTaskDataCollector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITaskList;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
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
 * @since 2.0
 */
public class SearchHitCollector extends LegacyTaskDataCollector implements ISearchQuery {

	private static final String LABEL_MAX_HITS_REACHED = "Max allowed number of hits returned exceeded. Some hits may not be displayed. Please narrow query scope.";

	private static final String QUERYING_REPOSITORY = "Querying Repository...";

	private final Set<AbstractTask> taskResults = new HashSet<AbstractTask>();

//	/** The string to display to the user when we have 1 match */
//	private static final String MATCH = "1 match";
//
//	/** The string to display to the user when we have multiple or no matches */
//	private static final String MATCHES = "{0} matches";

	private final ITaskList taskList;

	private final TaskRepository repository;

	private final AbstractRepositoryQuery repositoryQuery;

	private final RepositorySearchResult searchResult;

	private final AbstractRepositoryConnector connector;

	/**
	 * @since 3.0
	 */
	public SearchHitCollector(ITaskList tasklist, TaskRepository repository, AbstractRepositoryQuery repositoryQuery) {
		this.taskList = tasklist;
		this.repository = repository;
		this.repositoryQuery = repositoryQuery;
		this.connector = TasksUi.getRepositoryManager().getRepositoryConnector(repository.getConnectorKind());
		this.searchResult = new RepositorySearchResult(this);
	}

	public void aboutToStart() {
		taskResults.clear();

		searchResult.removeAll();
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				NewSearchUI.activateSearchResultView();
			}
		});
	}

//	public void accept(AbstractTask task) {
//		if (task == null) {
//			throw new IllegalArgumentException();
//		}
//
//		AbstractTask hitTask = taskList.getTask(task.getHandleIdentifier());
//		if (hitTask == null) {
//			hitTask = task;
//		}
//
//		taskResults.add(hitTask);
//		this.searchResult.addMatch(new Match(hitTask, 0, 0));
//	}

	@Override
	public void accept(RepositoryTaskData taskData) {
		AbstractTask task = taskList.getTask(repository.getRepositoryUrl(), taskData.getTaskId());
		if (task == null) {
			task = connector.createTask(taskData.getRepositoryUrl(), taskData.getTaskId(), "");
			((AbstractLegacyRepositoryConnector) connector).updateTaskFromTaskData(repository, task, taskData);
		}
		taskResults.add(task);
		this.searchResult.addMatch(new Match(task, 0, 0));
	}

	public String getLabel() {
		return QUERYING_REPOSITORY;
	}

	public boolean canRerun() {
		return true;
	}

	public boolean canRunInBackground() {
		return true;
	}

	public ISearchResult getSearchResult() {
		if (searchResult.getMatchCount() >= TaskDataCollector.MAX_HITS) {
			TasksUiInternal.displayStatus("Maximum hits reached", RepositoryStatus.createStatus(
					repository.getRepositoryUrl(), IStatus.WARNING, TasksUiPlugin.ID_PLUGIN, LABEL_MAX_HITS_REACHED));
		}
		return searchResult;
	}

	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		//monitor.beginTask(QUERYING_REPOSITORY, IProgressMonitor.UNKNOWN);
		aboutToStart();

		if (monitor.isCanceled()) {
			throw new OperationCanceledException("Search cancelled");
		}
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				repositoryQuery.getConnectorKind());
		if (connector != null) {
			IStatus status = connector.performQuery(repository, repositoryQuery, this, null, monitor);
			if (!status.isOK()) {
				TasksUiInternal.displayStatus("Search failed", status);
			}
		} else {
			return new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, IStatus.OK,
					"repository connector could not be found", null);
		}

		return Status.OK_STATUS;
	}

//	protected String getFormattedMatchesString(int count) {
//		if (count == 1) {
//			return MATCH;
//		}
//		Object[] messageFormatArgs = { new Integer(count) };
//		return MessageFormat.format(MATCHES, messageFormatArgs);
//	}

	public Set<AbstractTask> getTasks() {
		return taskResults;
	}

	public AbstractRepositoryQuery getRepositoryQuery() {
		return repositoryQuery;
	}

}
