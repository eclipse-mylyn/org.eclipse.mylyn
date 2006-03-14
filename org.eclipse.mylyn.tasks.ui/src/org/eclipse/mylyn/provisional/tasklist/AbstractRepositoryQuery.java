/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.provisional.tasklist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Mik Kersten
 */
public abstract class AbstractRepositoryQuery extends AbstractTaskContainer {

	protected String repositoryUrl;

	protected String queryUrl;

	protected int maxHits;

	private Set<AbstractQueryHit> hits = new HashSet<AbstractQueryHit>();

	protected Date lastRefresh;

	private boolean currentlySynchronizing = false;

	public abstract String getRepositoryKind();
	
	public AbstractRepositoryQuery(String description, TaskList taskList) {
		super(description, taskList);
	}
	
	public String getQueryUrl() {
		return queryUrl;
	}

	public Set<ITask> getChildren() {
		Set<ITask> tasks = new HashSet<ITask>();
		for (AbstractQueryHit hit : new ArrayList<AbstractQueryHit>(getHits())) {
			ITask task = hit.getCorrespondingTask();
			if (task != null) {
				tasks.add(task);
			}
		}
		return tasks;
	}

	public boolean isArchive() {
		return false;
	}

	public void setIsArchive(boolean isArchive) {
		// ignore
	}

	public Set<AbstractQueryHit> getHits() {
		return Collections.unmodifiableSet(hits);
	}

	public void clearHits() {
		hits.clear();
	}
	
	public void addHit(AbstractQueryHit hit) {
		ITask correspondingTask = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(hit.getHandleIdentifier());
		if (correspondingTask instanceof AbstractRepositoryTask) {
			hit.setCorrespondingTask((AbstractRepositoryTask) correspondingTask);
		}
		hits.add(hit);
	}

	public void removeHit(AbstractQueryHit hit) {
		hits.remove(hit);
	}

	public void setQueryUrl(String url) {
		this.queryUrl = url;
	}

	public String getPriority() {
		String highestPriority = Task.PriorityLevel.P5.toString();
		if (hits.isEmpty()) {
			return Task.PriorityLevel.P1.toString();
		}
		for (AbstractQueryHit hit : hits) {
			if (highestPriority.compareTo(hit.getPriority()) > 0) {
				highestPriority = hit.getPriority();
			}
		}
		return highestPriority;
	}

	public boolean isLocal() {
		return false;
	}

	public boolean isCompleted() {
		return false;
	}

	public int getMaxHits() {
		return maxHits;
	}

	public void setMaxHits(int maxHits) {
		this.maxHits = maxHits;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public Date getLastRefresh() {
		return lastRefresh;
	}

	public void setLastRefresh(Date lastRefresh) {
		this.lastRefresh = lastRefresh;
	}

	public boolean isSynchronizing() {
		return currentlySynchronizing;
	}

	public void setCurrentlySynchronizing(boolean currentlySynchronizing) {
		this.currentlySynchronizing = currentlySynchronizing;
	}
	
	@Override
	final void add(ITask task) {
		// ignore, can not add tasks to a query
	}
}