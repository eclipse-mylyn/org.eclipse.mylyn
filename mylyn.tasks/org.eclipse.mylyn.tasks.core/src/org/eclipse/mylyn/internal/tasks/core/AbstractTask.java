/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * Encapsulates tasks that reside on a repository or local computer and participate in synchronization with the source that contains their
 * data.
 *
 * @author Mik Kersten
 * @author Rob Elves
 * @since 2.0
 */
public abstract class AbstractTask extends AbstractTaskContainer implements ITask, ITaskRepositoryElement {

	public static final String DEFAULT_TASK_KIND = "task"; //$NON-NLS-1$

	private String repositoryUrl;

	private String taskKind = DEFAULT_TASK_KIND;

	private final String taskId;

	private String owner;

	private boolean active = false;

	private String summary;

	private String priority = PriorityLevel.getDefault().toString();

	private boolean isNotifiedIncoming = false;

	private boolean reminded = false;

	private final Set<AbstractTaskContainer> containers = new CopyOnWriteArraySet<>();

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

	private String notes = ""; //$NON-NLS-1$

	private int estimatedTimeHours = 0;

	private boolean markReadPending;

	// TODO 4.0 make private
	protected String taskKey;

	private AttributeMap attributeMap;

	private boolean changed;

	private String ownerId;

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
	 * True for tasks that can be modified without a round-trip to a server. For example, such a task can be marked completed via the Task
	 * List.
	 *
	 * @deprecated use <code>task instanceof LocalTask</code> instead
	 */
	@Deprecated
	public abstract boolean isLocal();

	@Override
	public abstract String getConnectorKind();

	@Deprecated
	public String getLastReadTimeStamp() {
		return lastReadTimeStamp;
	}

	@Deprecated
	public void setLastReadTimeStamp(String lastReadTimeStamp) {
		this.lastReadTimeStamp = lastReadTimeStamp != null ? lastReadTimeStamp.intern() : null;
	}

	/**
	 * @since 3.0
	 */
	public void setSynchronizationState(SynchronizationState syncState) {
		Assert.isNotNull(syncState);
		synchronizationState = syncState;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public SynchronizationState getSynchronizationState() {
		return synchronizationState;
	}

	public boolean isSynchronizing() {
		return synchronizing;
	}

	public void setSynchronizing(boolean sychronizing) {
		synchronizing = sychronizing;
	}

	public boolean isNotified() {
		return isNotifiedIncoming;
	}

	public void setNotified(boolean notified) {
		isNotifiedIncoming = notified;
	}

	@Override
	public String getOwner() {
		return owner;
	}

	@Override
	public void setOwner(String owner) {
		if (!areEqual(this.owner, owner)) {
			String oldValue = this.owner;
			this.owner = owner != null ? owner.intern() : null;
			firePropertyChange("owner", oldValue, owner); //$NON-NLS-1$
		}
	}

	@Override
	public String getOwnerId() {
		return ownerId;
	}

	@Override
	public void setOwnerId(String ownerId) {
		if (!areEqual(this.ownerId, ownerId)) {
			String oldValue = this.ownerId;
			this.ownerId = ownerId != null ? ownerId.intern() : null;
			firePropertyChange("ownerId", oldValue, ownerId); //$NON-NLS-1$
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

	@Override
	public final String getTaskId() {
		return taskId;
	}

	@Override
	public final String getRepositoryUrl() {
		return repositoryUrl;
	}

	@Override
	public final void setHandleIdentifier(String handleIdentifier) {
		throw new RuntimeException("Cannot set the handle identifier of a task, set repository URL instead."); //$NON-NLS-1$
	}

	public final void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl != null ? repositoryUrl.intern() : null;
		super.setHandleIdentifier(RepositoryTaskHandleUtil.getHandle(repositoryUrl, taskId));
	}

	/**
	 * User identifiable key for the task to be used in UI facilities such as label displays and hyperlinked references. Can return the same
	 * as the ID (e.g. in the case of Bugzilla). Can return null if no such label exists.
	 */
	@Override
	public String getTaskKey() {
		return taskKey == null ? taskId : taskKey;
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
		String taskKey = getTaskKey();
		if (taskKey != null) {
			return taskKey + ": " + summary; //$NON-NLS-1$
		} else {
			return summary;
		}
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractTask) {
			return getHandleIdentifier().equals(((ITask) obj).getHandleIdentifier());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return taskId.hashCode();
	}

	@Override
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

	@Override
	public void setPriority(String priority) {
		if (!areEqual(this.priority, priority)) {
			String oldValue = this.priority;
			this.priority = priority != null ? priority.intern() : null;
			firePropertyChange("priority", oldValue, priority); //$NON-NLS-1$
		}
	}

	public String getNotes() {
		// TODO: removed check for null once xml updated.
		if (notes == null) {
			notes = ""; //$NON-NLS-1$
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
		estimatedTimeHours = estimated;
	}

	void addParentContainer(AbstractTaskContainer container) {
		containers.add(container);
	}

	void removeParentContainer(AbstractTaskContainer container) {
		containers.remove(container);
	}

	public Set<AbstractTaskContainer> getParentContainers() {
		//return new HashSet<AbstractTaskContainer>(containers);
		return containers;
	}

	@Override
	public String getSummary() {
		return summary;
	}

	@Override
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

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public void setCreationDate(Date creationDate) {
		if (!areEqual(this.creationDate, creationDate)) {
			Date oldValue = this.creationDate;
			this.creationDate = creationDate;
			firePropertyChange("creationDate", oldValue, creationDate); //$NON-NLS-1$
		}
	}

	@Override
	public void setSummary(String summary) {
		Assert.isNotNull(summary);
		if (!areEqual(this.summary, summary)) {
			String oldValue = this.summary;
			this.summary = summary;
			firePropertyChange("summary", oldValue, summary); //$NON-NLS-1$
		}
	}

	@Override
	public void setCompletionDate(Date completionDate) {
		if (!areEqual(this.completionDate, completionDate)) {
			Date oldValue = this.completionDate;
			this.completionDate = completionDate;
			firePropertyChange("completionDate", oldValue, completionDate); //$NON-NLS-1$
		}
	}

	private boolean areEqual(Object oldValue, Object newValue) {
		return oldValue != null ? oldValue.equals(newValue) : oldValue == newValue;
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
		if (isCompleted() || scheduledForDate == null || !(getScheduledForDate() instanceof DayDateRange)) {
			return false;
		} else if (/*!internalIsFloatingScheduledDate() && */scheduledForDate.getEndDate()
				.compareTo(TaskActivityUtil.getCalendar()) < 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getTaskKind() {
		return taskKind;
	}

	@Override
	public void setTaskKind(String taskKind) {
		if (!areEqual(this.taskKind, taskKind)) {
			String oldValue = this.taskKind;
			this.taskKind = taskKind != null ? taskKind.intern() : null;
			firePropertyChange("taskKind", oldValue, taskKind); //$NON-NLS-1$
		}
	}

	@Override
	public int compareTo(IRepositoryElement taskListElement) {
		return summary.compareTo(((AbstractTask) taskListElement).summary);
	}

	@Override
	public Date getDueDate() {
		return dueDate;
	}

	@Override
	public void setDueDate(Date date) {
		if (!areEqual(dueDate, date)) {
			Date oldValue = dueDate;
			dueDate = date;
			firePropertyChange("dueDate", oldValue, date); //$NON-NLS-1$
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

	@Override
	public Date getModificationDate() {
		return modificationDate;
	}

	@Override
	public void setModificationDate(Date modificationDate) {
		if (!areEqual(this.modificationDate, modificationDate)) {
			Date oldValue = this.modificationDate;
			this.modificationDate = modificationDate;
			firePropertyChange("modificationDate", oldValue, modificationDate); //$NON-NLS-1$
		}
	}

	public boolean isMarkReadPending() {
		return markReadPending;
	}

	public void setMarkReadPending(boolean markReadPending) {
		this.markReadPending = markReadPending;
	}

	@Override
	public void setTaskKey(String taskKey) {
		if (!areEqual(this.taskKey, taskKey)) {
			String oldValue = this.taskKey;
			this.taskKey = taskKey;
			firePropertyChange("taskKey", oldValue, taskKey); //$NON-NLS-1$
		}
	}

	@Override
	public synchronized String getAttribute(String key) {
		return attributeMap != null ? attributeMap.getAttribute(key) : null;
	}

	@Override
	public synchronized Map<String, String> getAttributes() {
		if (attributeMap != null) {
			return attributeMap.getAttributes();
		} else {
			return Collections.emptyMap();
		}
	}

	@Override
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
			firePropertyChange("url", oldValue, url); //$NON-NLS-1$
		}
	}

}
