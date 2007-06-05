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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 * @author Rob Elves
 */
public abstract class AbstractRepositoryQuery extends AbstractTaskContainer {

	protected String repositoryUrl;

	protected String lastRefreshTimeStamp = "<never>";

	private boolean currentlySynchronizing = false;

	protected IStatus status = null;

	public abstract String getRepositoryKind();

	/**
	 * Query must be added to tasklist or synchronization will result in empty
	 * result set due to removeOrphanedHits(). All hits that don't have a query
	 * in the tasklist are removed.
	 */
	public AbstractRepositoryQuery(String description, TaskList taskList) {
		super(description, taskList);
	}

	public boolean isArchive() {
		return false;
	}

	public void setIsArchive(boolean isArchive) {
		// ignore
	}

	public synchronized Set<AbstractRepositoryTask> getHits() {
		Set<AbstractRepositoryTask> repositoryTasks = new HashSet<AbstractRepositoryTask>();
		for (ITask task : super.getChildren()) {
			if (task instanceof AbstractRepositoryTask) {
				repositoryTasks.add((AbstractRepositoryTask) task);
			}
		}
		return repositoryTasks;
	}

	public synchronized void updateHits(Collection<AbstractRepositoryTask> newHits) {
		clear();
		for (AbstractRepositoryTask abstractRepositoryTask : newHits) {
			addHit(abstractRepositoryTask);
		}
	}

	public synchronized void addHit(AbstractRepositoryTask hit) {
		// TODO: Move up?
		if(!taskList.getAllTasks().contains(hit)) {
			taskList.addTask(hit);
		}			
		super.add(hit);
	}

	public synchronized void removeHit(AbstractRepositoryTask hit) {
		super.remove(hit);
	}

	public synchronized String getPriority() {
		if (super.isEmpty()) {
			return Task.PriorityLevel.P1.toString();
		}
		String highestPriority = Task.PriorityLevel.P5.toString();
		for (AbstractRepositoryTask hit : getHits()) {
			if (highestPriority.compareTo(hit.getPriority()) > 0) {
				highestPriority = hit.getPriority();
			}
		}
		return highestPriority;
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	@Override
	public boolean isCompleted() {
		return false;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String newRepositoryUrl) {
		if (repositoryUrl != null && url != null) {
			// the repository url has changed, so change corresponding part of
			// query URL
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

	public String getLastRefreshTimeStamp() {
		return lastRefreshTimeStamp;
	}

	public void setLastRefreshTimeStamp(String lastRefreshTimeStamp) {
		this.lastRefreshTimeStamp = lastRefreshTimeStamp;
	}

	public IStatus getStatus() {
		return status;
	}

	public void setStatus(IStatus status) {
		this.status = status;
	}

}