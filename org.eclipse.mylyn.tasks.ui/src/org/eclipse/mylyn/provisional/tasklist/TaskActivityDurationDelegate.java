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

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * @author 	Rob Elves
 * @author 	Mik Kersten
 */
public class TaskActivityDurationDelegate implements ITask {
	
	private ITask task = null;

	private Calendar start = null;

	private Calendar end = null;

	private DateRangeContainer parent;

	public TaskActivityDurationDelegate(DateRangeContainer parent, ITask task, Calendar start, Calendar end) {
		this.task = task;
		this.start = start;
		this.end = end;
		this.parent = parent;
	}

	public Calendar getEnd() {
		return end;
	}

	public Calendar getStart() {
		return start;
	}

//	public ITask getTask() {
//		return task;
//	}

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
		final TaskActivityDurationDelegate other = (TaskActivityDurationDelegate) obj;
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

	public DateRangeContainer getContainer() {
		return parent;
	}

	public void addSubTask(ITask task) {
		task.addSubTask(task);
	}

	public ITaskContainer getCategory() {
		return task.getCategory();
	}

	public Set<ITask> getChildren() {
		return task.getChildren();
	}

	public Date getCompletionDate() {
		return task.getCompletionDate();
	}

	public Date getCreationDate() {
		return task.getCreationDate();
	}

	public String getDescription() {
		return task.getDescription();
	}

	public long getElapsedTime() {
		return task.getElapsedTime();
	}

	public int getEstimateTimeHours() {
		return task.getEstimateTimeHours();
	}

	public String getHandleIdentifier() {
		return task.getHandleIdentifier();
	}

	public String getKind() {
		return task.getKind();
	}

	public String getNotes() {
		return task.getNotes();
	}

	public ITask getParent() {
		return task.getParent();
	}

	public String getPriority() {
		return task.getPriority();
	}

	public Date getReminderDate() {
		return task.getReminderDate();
	}

	public String getRepositoryKind() {
		return task.getRepositoryKind();
	}

	public String getUrl() {
		return task.getUrl();
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

	public void removeSubTask(ITask task) {
		task.removeSubTask(task);
	}

	public void setActive(boolean active) {
		task.setActive(active);
	}

	public void setCategory(ITaskContainer category) {
		task.setCategory(category);
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

	public void setDescription(String description) {
		task.setDescription(description);
	}

	public void setElapsedTime(long elapsed) {
		task.setElapsedTime(elapsed);
	}

	public void setEstimatedTimeHours(int estimated) {
		task.setEstimatedTimeHours(estimated);
	}

	public void setHandleIdentifier(String id) {
		task.setHandleIdentifier(id);
	}

	public void setKind(String kind) {
		task.setKind(kind);
	}

	public void setNotes(String notes) {
		task.setNotes(notes);
	}

	public void setParent(ITask parent) {
		task.setParent(parent);
	}

	public void setPriority(String priority) {
		task.setPriority(priority);
	}

	public void setReminded(boolean reminded) {
		task.setReminded(reminded);
	}

	public void setReminderDate(Date date) {
		task.setReminderDate(date);
	}

	public void setUrl(String url) {
		task.setUrl(url);
	}
}
