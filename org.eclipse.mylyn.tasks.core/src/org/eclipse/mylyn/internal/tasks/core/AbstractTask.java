/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;

/**
 * Encapsulates tasks that reside on a repository or local computer and participate in synchronization with the source
 * that contains their data.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @since 2.0
 */
//API 3.0 move to internal package
public abstract class AbstractTask extends AbstractTaskContainer implements ITask {

	public static final String DEFAULT_TASK_KIND = "task";

	private String repositoryUrl;

	private String taskKind = DEFAULT_TASK_KIND;

	private final String taskId;

	private String owner;

	private boolean active = false;

	private String summary;

	private String priority = PriorityLevel.getDefault().toString();

	private boolean isNotifiedIncoming = false;

	private boolean reminded = false;

	private final Set<AbstractTaskContainer> containers = new HashSet<AbstractTaskContainer>();

	// ************ Synch ****************

	/** The last time this task's bug report was in a synchronized (read?) state. */
	private String lastReadTimeStamp;

	private boolean synchronizing;

	private boolean submitting;

	private SynchronizationState synchronizationState = SynchronizationState.SYNCHRONIZED;

	// transient
	private IStatus synchronizationStatus = null;

	private boolean stale = false;

	private Date completionDate = null;

	private Date creationDate = null;

	private Date modificationDate = null;

	private DateRange scheduledForDate = null;

	private Date dueDate = null;

	private String notes = "";

	private int estimatedTimeHours = 1;

	private boolean markReadPending;

	private String taskKey;

	public AbstractTask(String repositoryUrl, String taskId, String summary) {
		super(RepositoryTaskHandleUtil.getHandle(repositoryUrl, taskId));
		this.repositoryUrl = repositoryUrl;
		this.taskId = taskId;
		this.summary = summary;
		this.url = "";
	}

	/**
	 * Final to preserve the handle identifier format required by the framework.
	 */
	@Override
	public final String getHandleIdentifier() {
		return super.getHandleIdentifier();
	}

	/**
	 * True for tasks that can be modified without a round-trip to a server. For example, such a task can be marked
	 * completed via the Task List.
	 */
	public abstract boolean isLocal();

	public abstract String getConnectorKind();

	public String getLastReadTimeStamp() {
		return lastReadTimeStamp;
	}

	public void setLastReadTimeStamp(String lastReadTimeStamp) {
		this.lastReadTimeStamp = lastReadTimeStamp;
	}

	/**
	 * @since 3.0
	 */
	public void setSynchronizationState(SynchronizationState syncState) {
		Assert.isNotNull(syncState);
		this.synchronizationState = syncState;
	}

	/**
	 * @since 3.0
	 */
	public SynchronizationState getSynchronizationState() {
		return synchronizationState;
	}

	public boolean isSynchronizing() {
		return synchronizing;
	}

	public void setSynchronizing(boolean sychronizing) {
		this.synchronizing = sychronizing;
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

	public IStatus getSynchronizationStatus() {
		return synchronizationStatus;
	}

	public void setSynchronizationStatus(IStatus status) {
		this.synchronizationStatus = status;
	}

	public final String getTaskId() {
		return taskId;
	}

	public final String getRepositoryUrl() {
		return repositoryUrl;
	}

	@Override
	public final void setHandleIdentifier(String handleIdentifier) {
		throw new RuntimeException("Cannot set the handle identifier of a task, set repository URL instead.");
	}

	public final void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
		super.setHandleIdentifier(RepositoryTaskHandleUtil.getHandle(repositoryUrl, taskId));
	}

	/**
	 * User identifiable key for the task to be used in UI facilities such as label displays and hyperlinked references.
	 * Can return the same as the ID (e.g. in the case of Bugzilla). Can return null if no such label exists.
	 */
	public String getTaskKey() {
		return (taskKey == null) ? taskId : taskKey;
	}

	public boolean isSubmitting() {
		return submitting;
	}

	public void setSubmitting(boolean submitting) {
		this.submitting = submitting;
	}

	@Override
	public String toString() {
		return summary;
	}

	/**
	 * Package visible in order to prevent sets that don't update the index.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractTask) {
			return this.getHandleIdentifier().equals(((ITask) obj).getHandleIdentifier());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return taskId.hashCode();
	}

	public boolean isCompleted() {
		return completionDate != null;
	}

	/**
	 * @deprecated use setCompletionDate()
	 */
	@Deprecated
	public void setCompleted(boolean completed) {
		if (completed) {
			completionDate = new Date();
		} else {
			completionDate = null;
		}
	}

	@Override
	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getNotes() {
		// TODO: removed check for null once xml updated.
		if (notes == null) {
			notes = "";
		}
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @deprecated Use {@link #getEstimatedTimeHours()} instead
	 */
	@Deprecated
	public int getEstimateTimeHours() {
		return getEstimatedTimeHours();
	}

	/**
	 * @since 3.0
	 */
	public int getEstimatedTimeHours() {
		return estimatedTimeHours;
	}

	public void setEstimatedTimeHours(int estimated) {
		this.estimatedTimeHours = estimated;
	}

	/**
	 * @API 3.0: Rename to internalAddParentContainer
	 */
	public void addParentContainer(AbstractTaskContainer container) {
		containers.add(container);
	}

	/**
	 * @API 3.0: Rename to internalremoveParentContainer
	 * @since 3.0
	 */
	public void removeParentContainer(AbstractTaskContainer container) {
		containers.remove(container);
	}

	public Set<AbstractTaskContainer> getParentContainers() {
		return new HashSet<AbstractTaskContainer>(containers);
	}

	@Override
	public String getSummary() {
		return summary;
	}

	public Date getCompletionDate() {
		return completionDate;
	}

	public void setScheduledForDate(DateRange reminderDate) {
		scheduledForDate = reminderDate;
	}

	public DateRange getScheduledForDate() {
		return scheduledForDate;
	}

	public boolean isReminded() {
		return reminded;
	}

	public void setReminded(boolean reminded) {
		this.reminded = reminded;
	}

	public Date getCreationDate() {
		if (creationDate == null) {
			creationDate = new Date();
		}
		return creationDate;
	}

	public void setCreationDate(Date date) {
		this.creationDate = date;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;
	}

	/**
	 * @deprecated use {@link TaskActivityManager#isPastReminder(AbstractTask)} instead
	 */
	@Deprecated
	public boolean isPastReminder() {
		if (isCompleted() || scheduledForDate == null) {
			return false;
		} else {
			if (/*!internalIsFloatingScheduledDate() && */scheduledForDate.getEndDate().compareTo(
					TaskActivityUtil.getCalendar()) < 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean hasValidUrl() {
		String taskUrl = getUrl();
		if (taskUrl != null && !taskUrl.equals("") && !taskUrl.equals("http://") && !taskUrl.equals("https://")) {
			try {
				new URL(taskUrl);
				return true;
			} catch (MalformedURLException e) {
				return false;
			}
		}
		return false;
	}

	public String getTaskKind() {
		return taskKind;
	}

	public void setTaskKind(String kind) {
		this.taskKind = kind;
	}

	@Override
	public int compareTo(ITaskElement taskListElement) {
		return summary.compareTo(((AbstractTask) taskListElement).summary);
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date date) {
		this.dueDate = date;
	}

	public boolean isStale() {
		return stale;
	}

	public void setStale(boolean stale) {
		this.stale = stale;
	}

	/**
	 * @since 3.0
	 */
	public Date getModificationDate() {
		return modificationDate;
	}

	/**
	 * @since 3.0
	 */
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	/**
	 * @since 3.0
	 */
	public boolean isMarkReadPending() {
		return markReadPending;
	}

	/**
	 * @since 3.0
	 */
	public void setMarkReadPending(boolean markReadPending) {
		this.markReadPending = markReadPending;
	}

	public void setTaskKey(String taskKey) {
		this.taskKey = taskKey;
	}

}
