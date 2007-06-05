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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylar.internal.tasks.core.RepositoryTaskHandleUtil;

/**
 * Virtual proxy for a repository task.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 */
public abstract class AbstractRepositoryTask extends Task {

	/** The last time this task's bug report was in a synchronized (read?) state. */
	protected String lastSynchronizedDateStamp;

	protected String repositoryUrl;

	protected String taskId;
	
	protected String owner;
		
	protected boolean isNotifiedIncoming = false;

	public enum RepositoryTaskSyncState {
		OUTGOING, SYNCHRONIZED, INCOMING, CONFLICT
	}

	protected RepositoryTaskSyncState syncState = RepositoryTaskSyncState.SYNCHRONIZED;

	// transient
	protected IStatus errorStatus = null;

	// transient
	protected boolean currentlySynchronizing;

	// transient
	protected boolean submitting;
	
	public AbstractRepositoryTask(String repositoryUrl, String taskId, String summary, boolean newTask) {
		// NOTE: Repository tasks specify their own handle format.
		super(null, summary, newTask);
		this.repositoryUrl = repositoryUrl;
		this.taskId = taskId;
	}

	@Override
	public final String getHandleIdentifier() {
		return RepositoryTaskHandleUtil.getHandle(repositoryUrl, taskId);
	}

	@Override
	public abstract String getRepositoryKind();

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

	/**
	 * TODO: remove
	 */
	@Override
	public final boolean isLocal() {
		return false;
	}

	public boolean isSynchronizing() {
		return currentlySynchronizing;
	}

	public void setCurrentlySynchronizing(boolean currentlySychronizing) {
		this.currentlySynchronizing = currentlySychronizing;
	}

	public boolean isNotified() {
		return isNotifiedIncoming;
	}

	public void setNotified(boolean notified) {
		isNotifiedIncoming = notified;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public IStatus getStatus() {
		return errorStatus;
	}

	public void setStatus(IStatus status) {
		this.errorStatus = status;
	}

	public final String getTaskId() {
		return taskId;
	}

	public final String getRepositoryUrl() {
		return repositoryUrl;
	}

	public final void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	/**
	 * User identifiable key for the task to be used in UI facilities such as
	 * label displays and hyperlinked references. Can return the same as the ID
	 * (e.g. in the case of Bugzilla). Can return null if no such label exists.
	 */
	public String getTaskKey() {
		return taskId;
	}

	
	public boolean isSubmitting() {
		return submitting;
	}

	public void setSubmitting(boolean submitting) {
		this.submitting = submitting;
	}
}
