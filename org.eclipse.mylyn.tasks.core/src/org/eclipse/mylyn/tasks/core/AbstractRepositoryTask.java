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

import java.util.Date;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylar.tasks.core.web.HtmlStreamTokenizer;

/**
 * Virtual proxy for a repository task.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 */
public abstract class AbstractRepositoryTask extends Task {

	private static final String CONTEXT_HANDLE_DELIM = "-";

	private static final String MISSING_REPOSITORY_HANDLE = "norepository" + CONTEXT_HANDLE_DELIM;

	/** The last time this task's bug report was in a synchronized (read?) state. */
	protected String lastSynchronizedDateStamp;

	protected transient RepositoryTaskData taskData;

	protected boolean currentlySynchronizing;

	protected boolean isNotifiedIncoming = true;

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

	protected IStatus status = null;
	
	public AbstractRepositoryTask(String handle, String label, boolean newTask) {
		super(handle, label, newTask);
	}

	@Override
	public abstract String getRepositoryKind();

	public boolean isDownloaded() {
		return taskData != null;
	}

	public String getLastSyncDateStamp() {
		return lastSynchronizedDateStamp;
	}

	public void setLastSyncDateStamp(String lastSyncDateStamp) {
		this.lastSynchronizedDateStamp = lastSyncDateStamp;
	}

	public void setSyncState(RepositoryTaskSyncState syncState) {
		this.syncState = syncState;
	}

	public RepositoryTaskSyncState getSyncState() {
		return syncState;
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

	public void setCurrentlySynchronizing(boolean currentlySychronizing) {
		this.currentlySynchronizing = currentlySychronizing;
	}

	/**
	 * Human readable identifier for this task.  Override if different than ID, can return
	 * null if no such label exists.
	 */
	public String getIdLabel() {
		return getTaskId(handleIdentifier);
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
		return url;
	}

	public static String getHandle(String repositoryUrl, String taskId) {
		if (repositoryUrl == null) {
			return MISSING_REPOSITORY_HANDLE + taskId;
		} else if (taskId.contains(CONTEXT_HANDLE_DELIM)) {
			throw new RuntimeException("invalid handle for task, can not contain: " + CONTEXT_HANDLE_DELIM + ", was: "
					+ taskId);
		} else {
			return repositoryUrl + CONTEXT_HANDLE_DELIM + taskId;
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

	public RepositoryTaskData getTaskData() {
		return taskData;
	}

	public void setTaskData(RepositoryTaskData taskData) {
		this.taskData = taskData;
		// TODO: remove?
		if (taskData != null) {
			setDescription(HtmlStreamTokenizer.unescape(taskData.getSummary()));
		}
	}

	public boolean isNotified() {
		return isNotifiedIncoming;
	}

	public void setNotified(boolean notified) {
		isNotifiedIncoming = notified;
	}

	public String getOwner() {
		if (taskData != null && taskData.getAssignedTo() != null) {
			return taskData.getAssignedTo();
		} else {
			return "<unknown>";
		}
	}
	
	public IStatus getStatus() {
		return status;
	}
	
	public void setStatus(IStatus status) {
		this.status = status;
	}
	
	/**
	 * Need to update URL since it is derived from handle identifier.
	 */
	@Override
	public void setHandleIdentifier(String newHandleIdentifier) {
		String oldHandleIdentifier = getHandleIdentifier();
		String url = getUrl();
		if (oldHandleIdentifier != null && url != null) {
			String oldRepositoryUrl = AbstractRepositoryTask.getRepositoryUrl(oldHandleIdentifier);
			String newRepositoryUrl = AbstractRepositoryTask.getRepositoryUrl(newHandleIdentifier);

			if (url.startsWith(oldRepositoryUrl)) {
				setUrl(newRepositoryUrl + url.substring(oldRepositoryUrl.length()));
			}
		}		
		super.setHandleIdentifier(newHandleIdentifier);		
	}
	
}
