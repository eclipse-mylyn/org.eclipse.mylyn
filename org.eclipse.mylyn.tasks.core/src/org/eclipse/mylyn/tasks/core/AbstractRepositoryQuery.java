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

	public AbstractRepositoryQuery(String description) {
		super(description);
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

//	public synchronized void updateHits(Collection<AbstractRepositoryTask> newHits) {
//		clear();
//		for (AbstractRepositoryTask abstractRepositoryTask : newHits) {
//			addHit(abstractRepositoryTask);
//		}
//	}

//	public synchronized void addHit(AbstractRepositoryTask hit) {
//		super.add(hit);
//	}
//
//	public synchronized void removeHit(AbstractRepositoryTask hit) {
//		super.remove(hit);
//	}

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