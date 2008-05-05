/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Date;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask.SynchronizationState;

/**
 * @since 3.0
 */
public interface ITask extends ITaskElement {

	/**
	 * Final to preserve the handle identifier format required by the framework.
	 */
	public abstract String getHandleIdentifier();

	/**
	 * True for tasks that can be modified without a round-trip to a server. For example, such a task can be marked
	 * completed via the Task List.
	 */
	public abstract boolean isLocal();

	public abstract String getConnectorKind();

	public abstract String getLastReadTimeStamp();

	public abstract void setLastReadTimeStamp(String lastReadTimeStamp);

	/**
	 * @since 3.0
	 */
	public abstract void setSynchronizationState(SynchronizationState syncState);

	/**
	 * @since 3.0
	 */
	public abstract SynchronizationState getSynchronizationState();

	public abstract boolean isSynchronizing();

	public abstract void setSynchronizing(boolean sychronizing);

	public abstract boolean isNotified();

	public abstract void setNotified(boolean notified);

	public abstract String getOwner();

	public abstract void setOwner(String owner);

	public abstract IStatus getSynchronizationStatus();

	public abstract void setSynchronizationStatus(IStatus status);

	public abstract String getTaskId();

	public abstract String getRepositoryUrl();

	/**
	 * User identifiable key for the task to be used in UI facilities such as label displays and hyperlinked references.
	 * Can return the same as the ID (e.g. in the case of Bugzilla). Can return null if no such label exists.
	 */
	public abstract String getTaskKey();

	public abstract boolean isSubmitting();

	public abstract void setSubmitting(boolean submitting);

	public abstract boolean isCompleted();

	public abstract String getPriority();

	public abstract void setPriority(String priority);

	public abstract String getNotes();

	public abstract void setNotes(String notes);

	/**
	 * @since 3.0
	 */
	public abstract int getEstimatedTimeHours();

	public abstract void setEstimatedTimeHours(int estimated);

	public abstract Set<AbstractTaskContainer> getParentContainers();

	public abstract String getSummary();

	public abstract Date getCompletionDate();

	public abstract void setScheduledForDate(Date date);

	public abstract Date getScheduledForDate();

	public abstract boolean isReminded();

	public abstract void setReminded(boolean reminded);

	public abstract Date getCreationDate();

	public abstract void setCreationDate(Date date);

	public abstract void setSummary(String summary);

	public abstract void setCompletionDate(Date completionDate);

	public abstract boolean hasValidUrl();

	public abstract String getTaskKind();

	public abstract void setTaskKind(String kind);

	public abstract Date getDueDate();

	public abstract void setDueDate(Date date);

	public abstract boolean isStale();

	public abstract void setStale(boolean stale);

	/**
	 * @since 3.0
	 */
	public abstract Date getModificationDate();

	/**
	 * @since 3.0
	 */
	public abstract void setModificationDate(Date modificationDate);

	/**
	 * @since 3.0
	 */
	public abstract void setMarkReadPending(boolean markReadPending);

	@Deprecated
	public abstract boolean isActive();

	@Deprecated
	public abstract void setActive(boolean b);

	@Deprecated
	public abstract void removeParentContainer(AbstractTaskContainer category);

	public abstract boolean isPastReminder();

	@Deprecated
	public abstract boolean internalIsFloatingScheduledDate();

	@Deprecated
	public abstract boolean isMarkReadPending();

	@Deprecated
	public abstract void setCompleted(boolean completed);

	@Deprecated
	public abstract void setUrl(String taskUrl);

	@Deprecated
	public abstract void internalSetFloatingScheduledDate(boolean floating);

}