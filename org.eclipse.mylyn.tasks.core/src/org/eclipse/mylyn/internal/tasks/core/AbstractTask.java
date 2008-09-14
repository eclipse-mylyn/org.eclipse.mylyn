/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * Encapsulates tasks that reside on a repository or local computer and participate in synchronization with the source
 * that contains their data.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @since 2.0
 */
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
	private IStatus status = null;

	private boolean stale = false;

	private Date completionDate = null;

	private Date creationDate = null;

	private Date modificationDate = null;

	private DateRange scheduledForDate = null;

	private Date dueDate = null;

	private String notes = "";

	private int estimatedTimeHours = 1;

	private boolean markReadPending;

	// TODO 3.0 make private
	protected String taskKey;

	private AttributeMap attributeMap;

	private boolean changed;

	public AbstractTask(String repositoryUrl, String taskId, String summary) {
		super(RepositoryTaskHandleUtil.getHandle(repositoryUrl, taskId));
		this.repositoryUrl = repositoryUrl;
		this.taskId = taskId;
		this.summary = summary;
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
	 * 
	 * @deprecated use <code>task instanceof LocalTask</code> instead
	 */
	@Deprecated
	public abstract boolean isLocal();

	public abstract String getConnectorKind();

	@Deprecated
	public String getLastReadTimeStamp() {
		return lastReadTimeStamp;
	}

	@Deprecated
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
		if (!areEqual(this.owner, owner)) {
			String oldValue = this.owner;
			this.owner = owner;
			firePropertyChange("owner", oldValue, owner);
		}
	}

	/**
	 * Return the status, such as an error or warning, associated with this task.
	 */
	public IStatus getStatus() {
		return status;
	}

	public void setStatus(IStatus status) {
		this.status = status;
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

	@Deprecated
	public boolean isSubmitting() {
		return submitting;
	}

	@Deprecated
	public void setSubmitting(boolean submitting) {
		this.submitting = submitting;
	}

	@Override
	public String toString() {
		return summary;
	}

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
		if (!areEqual(this.priority, priority)) {
			String oldValue = this.priority;
			this.priority = priority;
			firePropertyChange("priority", oldValue, priority);
		}
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

	public int getEstimatedTimeHours() {
		return estimatedTimeHours;
	}

	public void setEstimatedTimeHours(int estimated) {
		this.estimatedTimeHours = estimated;
	}

	public void addParentContainer(AbstractTaskContainer container) {
		containers.add(container);
	}

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

	public void setCreationDate(Date creationDate) {
		if (!areEqual(this.creationDate, creationDate)) {
			Date oldValue = this.creationDate;
			this.creationDate = creationDate;
			firePropertyChange("creationDate", oldValue, creationDate);
		}
	}

	public void setSummary(String summary) {
		Assert.isNotNull(summary);
		if (!areEqual(this.summary, summary)) {
			String oldValue = this.summary;
			this.summary = summary;
			firePropertyChange("summary", oldValue, summary);
		}
	}

	public void setCompletionDate(Date completionDate) {
		if (!areEqual(this.completionDate, completionDate)) {
			Date oldValue = this.completionDate;
			this.completionDate = completionDate;
			firePropertyChange("completionDate", oldValue, completionDate);
		}
	}

	private boolean areEqual(Object oldValue, Object newValue) {
		return (oldValue != null) ? oldValue.equals(newValue) : oldValue == newValue;
	}

	private void firePropertyChange(String key, Object oldValue, Object newValue) {
//			PropertyChangeEvent event = new PropertyChangeEvent(this, key, oldValue, newValue);
//			for (PropertyChangeListener listener : propertyChangeListeners) {
//				listener.propertyChange(event);
//			}
		changed = true;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	/**
	 * @deprecated use {@link TaskActivityManager#isPastReminder(AbstractTask)} instead
	 */
	@Deprecated
	public boolean isPastReminder() {
		if (isCompleted() || scheduledForDate == null || !getScheduledForDate().isDay()) {
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

	public String getTaskKind() {
		return taskKind;
	}

	public void setTaskKind(String taskKind) {
		if (!areEqual(this.taskKind, taskKind)) {
			String oldValue = this.taskKind;
			this.taskKind = taskKind;
			firePropertyChange("taskKind", oldValue, taskKind);
		}
	}

	@Override
	public int compareTo(IRepositoryElement taskListElement) {
		return summary.compareTo(((AbstractTask) taskListElement).summary);
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date date) {
		if (!areEqual(this.dueDate, date)) {
			Date oldValue = this.dueDate;
			this.dueDate = date;
			firePropertyChange("dueDate", oldValue, date);
		}
	}

	@Deprecated
	public boolean isStale() {
		return stale;
	}

	@Deprecated
	public void setStale(boolean stale) {
		this.stale = stale;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		if (!areEqual(this.modificationDate, modificationDate)) {
			Date oldValue = this.modificationDate;
			this.modificationDate = modificationDate;
			firePropertyChange("modificationDate", oldValue, modificationDate);
		}
	}

	public boolean isMarkReadPending() {
		return markReadPending;
	}

	public void setMarkReadPending(boolean markReadPending) {
		this.markReadPending = markReadPending;
	}

	public void setTaskKey(String taskKey) {
		if (!areEqual(this.taskKey, taskKey)) {
			String oldValue = this.taskKey;
			this.taskKey = taskKey;
			firePropertyChange("taskKey", oldValue, taskKey);
		}
	}

	public synchronized String getAttribute(String key) {
		return (attributeMap != null) ? attributeMap.getAttribute(key) : null;
	}

	public synchronized Map<String, String> getAttributes() {
		if (attributeMap != null) {
			return attributeMap.getAttributes();
		} else {
			return Collections.emptyMap();
		}
	}

	public void setAttribute(String key, String value) {
		String oldValue;
		synchronized (this) {
			if (attributeMap == null) {
				attributeMap = new AttributeMap();
			}
			oldValue = attributeMap.getAttribute(key);
			if (!areEqual(oldValue, value)) {
				attributeMap.setAttribute(key, value);
			} else {
				return;
			}
		}
		firePropertyChange(key, oldValue, value);
	}

	@Override
	public void setUrl(String url) {
		String oldValue = getUrl();
		if (!areEqual(oldValue, url)) {
			super.setUrl(url);
			firePropertyChange("url", oldValue, url);
		}
	}

}
