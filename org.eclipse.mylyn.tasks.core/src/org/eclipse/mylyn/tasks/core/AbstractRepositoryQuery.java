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
package org.eclipse.mylar.tasks.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public abstract class AbstractRepositoryQuery extends AbstractTaskContainer {

	protected String repositoryUrl;

	protected int maxHits;

	private Set<AbstractQueryHit> hits = new HashSet<AbstractQueryHit>();

	protected String lastRefreshTimeStamp = "<never>";

	private boolean currentlySynchronizing = false;

	public abstract String getRepositoryKind();

	public AbstractRepositoryQuery(String description, TaskList taskList) {
		super(description, taskList);
	}

	// TODO: this overriding is a bit weird
	public Set<ITask> getChildren() {
		Set<ITask> tasks = new HashSet<ITask>();
		for (AbstractQueryHit hit : getHits()) {
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

	public synchronized AbstractQueryHit findQueryHit(String handle) {
		if (handle != null) {
			for (AbstractQueryHit hit : hits) {
				if (handle.equals(hit.getHandleIdentifier())) {
					return hit;
				}
			}
		}
		return null;
	}
	
	public synchronized Set<AbstractQueryHit> getHits() {
		return new HashSet<AbstractQueryHit>(hits);
	}

	public synchronized void updateHits(List<AbstractQueryHit> newHits, TaskList taskList) {
		Set<AbstractQueryHit> oldHits = new HashSet<AbstractQueryHit>(hits);
		hits.clear();
		for (AbstractQueryHit oldHit : oldHits) {
			if (newHits.contains(oldHit)) {
				newHits.get(newHits.indexOf(oldHit)).setNotified(oldHit.isNotified);
			}
		}
		for (AbstractQueryHit hit : newHits) {
			this.addHit(hit, taskList);
		}
	}

	public synchronized void addHit(AbstractQueryHit hit, TaskList taskList) {
		ITask correspondingTask = taskList.getTask(hit.getHandleIdentifier());
		if (correspondingTask instanceof AbstractRepositoryTask) {
			hit.setCorrespondingTask((AbstractRepositoryTask) correspondingTask);
		}
		hit.setParent(this);
		hits.add(hit);
	}

	public synchronized void removeHit(AbstractQueryHit hit) {
		hits.remove(hit);
	}

	public synchronized String getPriority() {
		if (hits.isEmpty()) {
			return Task.PriorityLevel.P1.toString();
		}
		String highestPriority = Task.PriorityLevel.P5.toString();
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

	public void setRepositoryUrl(String newRepositoryUrl) {
		if (repositoryUrl != null && url != null) {
			// the repository url has changed, so change corresponding part of query URL
			this.url = newRepositoryUrl + url.substring(repositoryUrl.length());
		}
		this.repositoryUrl = newRepositoryUrl;
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

	public String getLastRefreshTimeStamp() {
		return lastRefreshTimeStamp;
	}

	public void setLastRefreshTimeStamp(String lastRefreshTimeStamp) {
		this.lastRefreshTimeStamp = lastRefreshTimeStamp;
	}
}