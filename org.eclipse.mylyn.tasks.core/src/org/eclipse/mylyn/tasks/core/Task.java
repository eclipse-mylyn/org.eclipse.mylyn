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

/**
 * @author Mik Kersten
 */
public class Task extends AbstractTaskContainer implements ITask {

	private static final String REPOSITORY_KIND_LOCAL = "local";

	public static final String DEFAULT_TASK_KIND = "task";

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

	private boolean active = false;

//	private boolean category = false;

	private boolean hasReminded = false;

	private String summary;

	private String priority = PriorityLevel.getDefault().toString();

	private String notes = "";

	private int estimatedTimeHours = 1;

	private boolean completed;

	private String taskUrl = "";

	private AbstractTaskContainer parentCategory = null;

	private Date completionDate = null;

	private Date creationDate = null;

	private Date scheduledForDate = null;

	private Date dueDate = null;

	//private Set<ITask> children = new HashSet<ITask>();

	protected String kind = DEFAULT_TASK_KIND;

	@Override
	public String toString() {
		return summary;
	}

	public Task(String handle, String summary) {
		super(handle);
		this.summary = summary;
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
		if (obj instanceof Task && obj != null) {
			return this.getHandleIdentifier().compareTo(((Task) obj).getHandleIdentifier()) == 0;
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


	public void setContainer(AbstractTaskContainer cat) {
		this.parentCategory = cat;
	}

	public AbstractTaskContainer getContainer() {
		return parentCategory;
	}

	public String getSummary() {
		return summary;
	}

	// TODO: Remove
	public boolean isLocal() {
		return true;
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

	public String getRepositoryKind() {
		return REPOSITORY_KIND_LOCAL;
	}

	public String getTaskKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public int compareTo(ITaskListElement taskListElement) {
		return summary.compareTo(((Task) taskListElement).summary);
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date date) {
		this.dueDate = date;
	}
}
