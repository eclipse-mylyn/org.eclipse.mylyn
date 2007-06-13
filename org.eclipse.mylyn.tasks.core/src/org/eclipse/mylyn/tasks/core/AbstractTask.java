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

package org.eclipse.mylyn.tasks.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public abstract class AbstractTask extends AbstractTaskListElement {
	
	@Deprecated
	public static final String DEFAULT_TASK_KIND = "task";

	private String repositoryUrl;

	private String kind = DEFAULT_TASK_KIND;
	
	private String taskId;
	
	private String owner;
	
	private boolean active = false;
	
	private String summary;

	private String priority = PriorityLevel.getDefault().toString();

	private boolean completed;
	
	private boolean isNotifiedIncoming = false;
	
	private boolean hasReminded = false;

	private String taskUrl = "";


	@Deprecated
	private AbstractTaskContainer parentCategory = null;
	
	
	// ************ Synch ****************
	
	/** The last time this task's bug report was in a synchronized (read?) state. */
	private String lastSynchronizedDateStamp;

	private boolean currentlySynchronizing;

	private boolean submitting;
	
	private RepositoryTaskSyncState syncState = RepositoryTaskSyncState.SYNCHRONIZED;

	// transient
	private IStatus errorStatus = null;
	
	public enum RepositoryTaskSyncState {
		OUTGOING, SYNCHRONIZED, INCOMING, CONFLICT
	}
	
	// ************ Planning ****************
	
	private Date completionDate = null;

	private Date creationDate = null;

	private Date scheduledForDate = null;

	private Date dueDate = null;
	
	private String notes = "";

	private int estimatedTimeHours = 1;
		



	public enum PriorityLevel {
		P1, P2, P3, P4, P5;
	
		@Override
		public String toString() {
			switch (this) {
			case P1:
				return "P1";
			case P2:
				return "P2";
			case P3:
				return "P3";
			case P4:
				return "P4";
			case P5:
				return "P5";
			default:
				return "P3";
			}
		}
	
		public String getDescription() {
			switch (this) {
			case P1:
				return "Very High";
			case P2:
				return "High";
			case P3:
				return "Normal";
			case P4:
				return "Low";
			case P5:
				return "Very Low";
			default:
				return "";
			}
		}
	
		public static PriorityLevel fromString(String string) {
			if (string.equals("P1"))
				return P1;
			if (string.equals("P2"))
				return P2;
			if (string.equals("P3"))
				return P3;
			if (string.equals("P4"))
				return P4;
			if (string.equals("P5"))
				return P5;
			return getDefault();
		}
	
		public static PriorityLevel fromDescription(String string) {
			if (string == null)
				return null;
			if (string.equals("Very High"))
				return P1;
			if (string.equals("High"))
				return P2;
			if (string.equals("Normal"))
				return P3;
			if (string.equals("Low"))
				return P4;
			if (string.equals("Very Low"))
				return P5;
			return getDefault();
		}
	
		public static PriorityLevel getDefault() {
			return P3;
		}
	}
	
	public AbstractTask(String repositoryUrl, String taskId, String summary) {
		super(RepositoryTaskHandleUtil.getHandle(repositoryUrl, taskId));
		this.repositoryUrl = repositoryUrl;
		this.taskId = taskId;
		this.summary = summary;
	}

	public final String getHandleIdentifier() {
		// Note: when removing this consider implications to repository url refactoring
		// which only sets the repository url (so if we simply return handle here it will
		// be incorrect after a refactoring). 
		return RepositoryTaskHandleUtil.getHandle(repositoryUrl, taskId);
	}

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
		if (obj instanceof AbstractTask && obj != null) {
			return this.getHandleIdentifier().compareTo(((AbstractTask)obj).getHandleIdentifier()) == 0;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.getHandleIdentifier().hashCode();
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
		if (completed) {
			completionDate = new Date();
		} else {
			completionDate = null;
		}
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	/**
	 * TODO: consider removing
	 */
	public void setTaskUrl(String url) {
		this.taskUrl = url;
	}

	public String getTaskUrl() {
		return taskUrl;
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

	public int getEstimateTimeHours() {
		return estimatedTimeHours;
	}

	public void setEstimatedTimeHours(int estimated) {
		this.estimatedTimeHours = estimated;
	}

	/**
	 * Use TaskList for moving tasks between containers
	 * 
	 * TODO: get rid of this or we should make TaskCategory API.
	 */
	@Deprecated
	public void setCategory(AbstractTaskContainer category) {
		this.parentCategory = category;
	}

	@Deprecated
	public AbstractTaskContainer getCategory() {
		return parentCategory;
	}

	public String getSummary() {
		return summary;
	}

	public Date getCompletionDate() {
		return completionDate;
	}

	public void setScheduledForDate(Date date) {
		scheduledForDate = date;
	}

	public Date getScheduledForDate() {
		return scheduledForDate;
	}

	public boolean hasBeenReminded() {
		return hasReminded;
	}

	public void setReminded(boolean reminded) {
		this.hasReminded = reminded;
	}

	public Date getCreationDate() {
		if (creationDate == null)
			creationDate = new Date();
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

	public boolean isPastReminder() {
		if (scheduledForDate == null) {
			return false;
		} else {
			Date now = new Date();
			if (scheduledForDate.compareTo(now) < 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean hasValidUrl() {
		String taskUrl = getTaskUrl();
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
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public int compareTo(AbstractTaskListElement taskListElement) {
		return summary.compareTo(((AbstractTask)taskListElement).summary);
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date date) {
		this.dueDate = date;
	}
}
