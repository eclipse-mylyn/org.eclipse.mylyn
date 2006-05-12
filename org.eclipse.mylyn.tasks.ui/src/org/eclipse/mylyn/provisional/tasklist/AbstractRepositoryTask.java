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

import java.util.Date;

import org.eclipse.mylar.internal.core.MylarContextManager;

/**
 * Virtual proxy for a repository task.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 */
public abstract class AbstractRepositoryTask extends Task {

	/** The last time this task's bug report was downloaded from the server. */
	protected Date lastRefresh;
	
	protected boolean currentlySynchronizing;
	
	/**
	 * Value is <code>true</code> if the bug report has saved changes that
	 * need synchronizing with the repository.
	 */
	protected boolean isDirty;

	public enum RepositoryTaskSyncState {
		OUTGOING, SYNCHRONIZED, INCOMING, CONFLICT
	}
	
	protected RepositoryTaskSyncState syncState = RepositoryTaskSyncState.SYNCHRONIZED;

	public static final String HANDLE_DELIM = "-";
		
	public AbstractRepositoryTask(String handle, String label, boolean newTask) {
		super(handle, label, newTask);
	}

	public abstract String getRepositoryKind();
	
	/**
	 * @return	true	if the task can be queried and manipulated without connecting to the server
	 */
	public abstract boolean isPersistentInWorkspace();
	
	public abstract boolean isDownloaded();
	
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
		return AbstractRepositoryTask.getRepositoryUrl(getHandleIdentifier());
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

	public boolean isSynchronizing() {
		return currentlySynchronizing;
	}

	public void setCurrentlyDownloading(boolean currentlyDownloading) {
		this.currentlySynchronizing = currentlyDownloading;
	}

	public static String getTaskId(String taskHandle) {
		int index = taskHandle.lastIndexOf(AbstractRepositoryTask.HANDLE_DELIM);
		if (index != -1) {
			String id = taskHandle.substring(index + 1);
			return id;
		}
		return null;
	}

	public static String getRepositoryUrl(String taskHandle) {
		int index = taskHandle.lastIndexOf(AbstractRepositoryTask.HANDLE_DELIM);
		String url = null;
		if (index != -1) {
			url = taskHandle.substring(0, index);
		}
		if (url != null && url.equals(TaskRepositoryManager.PREFIX_REPOSITORY_OLD)) {
			String repositoryKind = TaskRepositoryManager.PREFIX_REPOSITORY_OLD.toLowerCase();
			TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getDefaultRepository(repositoryKind);
			if (repository != null) {
				url = repository.getUrl();
			}
		}
		return url;
	}

	public static int getTaskIdAsInt(String taskHandle) {
		String idString = getTaskId(taskHandle);
		if (idString != null) {
			return Integer.parseInt(idString);
		} else {
			return -1;
		}
	}

	/**
	 * @param taskId	must be an integer
	 */
	public static String getHandle(String repositoryUrl, String taskId) {
		if (repositoryUrl == null) {
			return TaskRepositoryManager.MISSING_REPOSITORY_HANDLE + taskId;
		} else {
			// MylarContextManager.CONTEXT_HANDLE_DELIM + taskId);
			return repositoryUrl + MylarContextManager.CONTEXT_HANDLE_DELIM + taskId;
		}
	}

	public static String getHandle(String repositoryUrl, int taskId) {
		return AbstractRepositoryTask.getHandle(repositoryUrl, "" + taskId);
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}
}
