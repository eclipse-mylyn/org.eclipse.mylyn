/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.tasks.ui.search;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.RepositoryStatus;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.PlatformUI;

/**
 * Used for returning results from Eclipse Search view. Collects results of a
 * repository search
 * 
 * @author Rob Elves
 */
public class SearchHitCollector extends QueryHitCollector implements ISearchQuery {

	private static final String QUERYING_REPOSITORY = "Querying Repository...";

	private TaskRepository repository;

	private AbstractRepositoryQuery repositoryQuery;

	private RepositorySearchResult searchResult;

	public SearchHitCollector(TaskList tasklist, TaskRepository repository, AbstractRepositoryQuery repositoryQuery) {
		super(tasklist);
		this.repository = repository;
		this.repositoryQuery = repositoryQuery;
		this.searchResult = new RepositorySearchResult(this);
	}

	@Override
	public void aboutToStart(int startMatchCount) throws CoreException {
		super.aboutToStart(startMatchCount);
		searchResult.removeAll();
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				NewSearchUI.activateSearchResultView();
			}
		});
	}

	@Override
	public void done() {
		super.done();
	}

	@Override
	public void addMatch(AbstractQueryHit hit) {
		super.addMatch(hit);
		this.searchResult.addMatch(new Match(hit, 0, 0));
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
		if (searchResult.getMatchCount() >= QueryHitCollector.MAX_HITS) {
			MylarStatusHandler.displayStatus("Maximum hits reached", RepositoryStatus.createStatus(repository.getUrl(),
					IStatus.WARNING, TasksUiPlugin.PLUGIN_ID, MAX_HITS_REACHED));
		}
		return searchResult;
	}

	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
		this.setProgressMonitor(monitor);
		IStatus status = Status.OK_STATUS;
		try {
			aboutToStart(0);
			monitor.beginTask(QUERYING_REPOSITORY, IProgressMonitor.UNKNOWN);

			if (monitor.isCanceled()) {
				throw new OperationCanceledException("Search cancelled");
			}
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					repositoryQuery.getRepositoryKind());
			if (connector != null) {
				status = connector.performQuery(repositoryQuery, repository, monitor, this);
			} else {
				return new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, IStatus.OK,
						"repository connector could not be found", null);
			}

			if (!status.isOK()) {
				throw new CoreException(status);
			}

		} catch (final CoreException exception) {
			MylarStatusHandler.displayStatus("Search failed", exception.getStatus());
		} finally {
			done();
		}
		return Status.OK_STATUS;
	}

}