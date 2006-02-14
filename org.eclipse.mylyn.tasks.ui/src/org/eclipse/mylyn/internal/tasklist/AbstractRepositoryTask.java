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

package org.eclipse.mylar.internal.tasklist;

import java.util.Date;

/**
 * @author Mik Kersten and Robert Elves
 */
public abstract class AbstractRepositoryTask extends Task {

	/** The last time this task's bug report was downloaded from the server. */
	protected Date lastRefresh;
	
	protected boolean currentlyDownloading;

	public enum RepositoryTaskSyncState {
		OUTGOING, SYNCHRONIZED, INCOMING, CONFLICT
	}
	
	protected RepositoryTaskSyncState syncState = RepositoryTaskSyncState.SYNCHRONIZED;
		
	public AbstractRepositoryTask(String handle, String label, boolean newTask) {
		super(handle, label, newTask);
	}

	public Date getLastRefresh() {
		return lastRefresh;
	}

	public void setLastRefresh(Date lastRefresh) {
		this.lastRefresh = lastRefresh;
	}

	public void setSyncState(RepositoryTaskSyncState syncState) {
		this.syncState = syncState;
	}

	public RepositoryTaskSyncState getSyncState() {
		return syncState;
	}
	
	/**
	 * @return The number of seconds ago that this task's bug report was
	 *         downloaded from the server.
	 */
	public long getTimeSinceLastRefresh() {
		Date timeNow = new Date();
		return (timeNow.getTime() - lastRefresh.getTime()) / 1000;
	}

	public String getRepositoryUrl() {
		return TaskRepositoryManager.getRepositoryUrl(getHandleIdentifier());
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	public static long getLastRefreshTimeInMinutes(Date lastRefresh) {
		Date timeNow = new Date();
		if (lastRefresh == null)
			lastRefresh = new Date();
		long timeDifference = (timeNow.getTime() - lastRefresh.getTime()) / 60000;
		return timeDifference;
	}

	public boolean isCurrentlyDownloading() {
		return currentlyDownloading;
	}

	public void setCurrentlyDownloading(boolean currentlyDownloading) {
		this.currentlyDownloading = currentlyDownloading;
	}

}
