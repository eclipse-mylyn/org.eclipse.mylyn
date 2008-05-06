/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;

/**
 * A container that stores tasks from a specific repository.
 * 
 * @author Mik Kersten
 * @author Eugene Kuleshov
 * @author Rob Elves
 * @since 2.0
 */
public abstract class AbstractRepositoryQuery extends AbstractTaskContainer {

	protected String repositoryUrl;

	protected String lastSynchronizedStamp = "<never>";

	private boolean synchronizing = false;

	protected IStatus status = null;

	/**
	 * @since 3.0
	 */
	public abstract String getConnectorKind();

	public AbstractRepositoryQuery(String description) {
		super(description);
	}

	@Override
	public String getPriority() {
		if (super.isEmpty()) {
			return PriorityLevel.P1.toString();
		}
		String highestPriority = PriorityLevel.P5.toString();
		for (ITask hit : getChildren()) {
			if (highestPriority.compareTo(hit.getPriority()) > 0) {
				highestPriority = hit.getPriority();
			}
		}
		return highestPriority;
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
		return synchronizing;
	}

	public void setSynchronizing(boolean synchronizing) {
		this.synchronizing = synchronizing;
	}

	public String getLastSynchronizedTimeStamp() {
		return lastSynchronizedStamp;
	}

	public void setLastSynchronizedStamp(String lastRefreshTimeStamp) {
		this.lastSynchronizedStamp = lastRefreshTimeStamp;
	}

	public IStatus getSynchronizationStatus() {
		return status;
	}

	public void setSynchronizationStatus(IStatus status) {
		this.status = status;
	}

}
