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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylar.internal.core.util.MylarStatusHandler;

/**
 * @author Mik Kersten
 */
public class Task implements ITask {

	private static final String REPOSITORY_KIND_LOCAL = "local";

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
			if (string == null)
				return null;
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
			return P3;
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
			return null;
		}

	}

	private boolean active = false;

	protected String handle = "-1";

	private boolean category = false;

	private boolean hasReminded = false;

	private String description;

	private String priority = PriorityLevel.P3.toString();

	private String notes = "";

	private int estimatedTimeHours = 0;

	private boolean completed;

	private String url = "";

	private AbstractTaskContainer parentCategory = null;

	private long timeActive = 0;

	private Date completionDate = null;

	private Date creationDate = null;

	private Date reminderDate = null;

	/**
	 * @return null if root
	 */
	private transient ITask parent;

	private Set<ITask> children = new HashSet<ITask>();

	protected String kind;

	@Override
	public String toString() {
		return description;
	}

	public Task(String handle, String label, boolean newTask) {
		this.handle = handle;
		this.description = label;
		if (newTask) {
			creationDate = new Date();
		}
	}

	public String getHandleIdentifier() {
		return handle;
	}

	public void setHandleIdentifier(String id) {
		this.handle = id;
	}

	public ITask getParent() {
		return parent;
	}

	public void setParent(ITask parent) {
		this.parent = parent;
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

	public String getToolTipText() {
		return getDescription();
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

	public boolean isCategory() {
		return category;
	}

	public void setIsCategory(boolean category) {
		this.category = category;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
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

	public long getElapsedTime() {
		return timeActive;
	}

	public void setElapsedTime(long elapsedTime) {
		if (elapsedTime >= 0) {
			this.timeActive = elapsedTime;
		} else {
			MylarStatusHandler.log("Attempt to set negative time on task: " + getDescription(), this);
		}
	}

	public int getEstimateTimeHours() {
		return estimatedTimeHours;
	}

	public void setEstimatedTimeHours(int estimated) {
		this.estimatedTimeHours = estimated;
	}

	public Set<ITask> getChildren() {
		return children;
	}

	public void addSubTask(ITask t) {
		children.add(t);
	}

	public void removeSubTask(ITask t) {
		children.remove(t);
	}

	public void setContainer(AbstractTaskContainer cat) {
		this.parentCategory = cat;
	}

	public AbstractTaskContainer getContainer() {
		return parentCategory;
	}

	public String getDescription() {
		return description;
	}

	public boolean isLocal() {
		return true;
	}

	public Date getCompletionDate() {
		return completionDate;
	}

	public void setReminderDate(Date date) {
		reminderDate = date;
	}

	public Date getReminderDate() {
		return reminderDate;
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

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;
	}

	public boolean isPastReminder() {
		if (reminderDate == null) {
			return false;
		} else {
			Date now = new Date();
			if (reminderDate.compareTo(now) < 0) {
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

	public String getRepositoryKind() {
		return REPOSITORY_KIND_LOCAL;
	}

	public String getTaskType() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

}
