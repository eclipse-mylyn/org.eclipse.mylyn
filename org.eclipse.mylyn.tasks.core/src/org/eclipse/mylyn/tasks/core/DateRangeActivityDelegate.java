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

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class DateRangeActivityDelegate extends AbstractTask {

	private AbstractTask task = null;

	private DateRangeContainer parent;

	private long startMili = 0;

	private long endMili = 0;

	private long activity = 0;

	public DateRangeActivityDelegate(DateRangeContainer parent, AbstractTask task, Calendar start, Calendar end) {
		this(parent, task, start, end, 0);
	}

	public DateRangeActivityDelegate(DateRangeContainer parent, AbstractTask task, Calendar start, Calendar end, long activity) {
		super(task.getRepositoryUrl(), task.getTaskId(), task.getSummary());
		if (task == null) {
			throw new RuntimeException("attempted to instantiated with null task: " + parent);
		}
		this.task = task;
		if (start != null) {
			this.startMili = start.getTimeInMillis();
		}
		if (end != null) {
			this.endMili = end.getTimeInMillis();
		}
		this.parent = parent;
		this.activity = activity;
	}

	public long getEnd() {
		return endMili;
	}

	public long getStart() {
		return startMili;
	}

	public long getActivity() {
		return activity;
	}

	public AbstractTask getCorrespondingTask() {
		return task;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((task == null) ? 0 : task.hashCode());
		result = PRIME * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DateRangeActivityDelegate other = (DateRangeActivityDelegate) obj;
		if (task == null) {
			if (other.task != null)
				return false;
		} else if (!task.equals(other.task))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	}

	public DateRangeContainer getDateRangeContainer() {
		return parent;
	}

//	public void addSubTask(ITask task) {
//		task.addSubTask(task);
//	}

	public AbstractTaskListElement getContainer() {
		return task.getContainer();
	}

	public Set<AbstractTask> getChildren() {
		return task.getChildren();
	}

	public Date getCompletionDate() {
		return task.getCompletionDate();
	}

	public Date getCreationDate() {
		return task.getCreationDate();
	}

	public String getSummary() {
		return task.getSummary();
	}

	public int getEstimateTimeHours() {
		return task.getEstimateTimeHours();
	}

	public String getTaskKind() {
		return task.getTaskKind();
	}

	public String getNotes() {
		return task.getNotes();
	}

	public String getPriority() {
		return task.getPriority();
	}

	public Date getScheduledForDate() {
		return task.getScheduledForDate();
	}

	public String getTaskUrl() {
		return task.getTaskUrl();
	}

	public boolean hasBeenReminded() {
		return task.hasBeenReminded();
	}

	public boolean hasValidUrl() {
		return task.hasValidUrl();
	}

	public boolean isActive() {
		return task.isActive();
	}

	public boolean isCompleted() {
		return task.isCompleted();
	}

	public boolean isPastReminder() {
		return task.isPastReminder();
	}

//	public void removeSubTask(ITask task) {
//		task.removeSubTask(task);
//	}

	public void setActive(boolean active) {
		task.setActive(active);
	}

	public void setContainer(AbstractTaskListElement category) {
		task.setContainer(category);
	}

	public void setCompleted(boolean completed) {
		task.setCompleted(completed);
	}

	public void setCompletionDate(Date date) {
		task.setCompletionDate(date);
	}

	public void setCreationDate(Date date) {
		task.setCreationDate(date);
	}

	public void setEstimatedTimeHours(int estimated) {
		task.setEstimatedTimeHours(estimated);
	}

	// public void setHandleIdentifier(String taskId) {
	// task.setHandleIdentifier(taskId);
	// }

	public void setKind(String kind) {
		task.setKind(kind);
	}

	public void setNotes(String notes) {
		task.setNotes(notes);
	}

	public void setPriority(String priority) {
		task.setPriority(priority);
	}

	public void setReminded(boolean reminded) {
		task.setReminded(reminded);
	}

	public void setScheduledForDate(Date date) {
		task.setScheduledForDate(date);
	}

	public void setTaskUrl(String url) {
		task.setTaskUrl(url);
	}

	public int compareTo(AbstractTaskListElement taskListElement) {
		return task.toString().compareTo(((AbstractTask) taskListElement).toString());
	}

	public void setSummary(String description) {
		task.setSummary(description);
	}

	public Date getDueDate() {
		return task.getDueDate();
	}

	public void setDueDate(Date date) {
		task.setDueDate(date);
	}

	@Override
	public boolean isLocal() {
		return ((AbstractTask)task).isLocal();
	}

	@Override
	public String getRepositoryKind() {
		return task.getRepositoryKind();
	}
}
