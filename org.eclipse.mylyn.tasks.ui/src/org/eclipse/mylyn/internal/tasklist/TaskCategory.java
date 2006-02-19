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
/*
 * Created on Dec 26, 2004
 */
package org.eclipse.mylar.internal.tasklist;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mik Kersten
 */
public class TaskCategory implements ITaskContainer {

	private Set<ITask> tasks = new HashSet<ITask>();

//	protected String description = "";

	private String handle = "";

	private boolean isArchive = false;

	public TaskCategory(String handleAndDescription) {
//		this.description = description;
		this.handle = handleAndDescription;
	}

	public String getDescription() {
		return handle;
	}

	public String getHandleIdentifier() {
		return handle;
	}

	public void setDescription(String description) {
		this.handle = description;
	}

	public void setHandleIdentifier(String handle) {
		this.handle = handle;
	}

	public String getPriority() {
		String highestPriority = Task.PriorityLevel.P5.toString();
		if (tasks.isEmpty()) {
			return Task.PriorityLevel.P1.toString();
		}
		for (ITask task : tasks) {
			if (highestPriority.compareTo(task.getPriority()) > 0) {
				highestPriority = task.getPriority();
			}
		}
		return highestPriority;
	}

	void addTask(ITask task) {
		tasks.add(task);
	}

	/**
	 * HACK: public so it can be used by other externalizers
	 */
	public void internalAddTask(ITask task) {
		tasks.add(task);
	}

	public void removeTask(ITask task) {
		tasks.remove(task);
	}

	public Set<ITask> getChildren() {
		return tasks;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (object instanceof TaskCategory) {
			TaskCategory compare = (TaskCategory) object;
			return this.getHandleIdentifier().equals(compare.getHandleIdentifier());
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return handle.hashCode();
	}

	public ITask getOrCreateCorrespondingTask() {
		return null;
	}

	public boolean hasCorrespondingActivatableTask() {
		return false;
	}

	public boolean isLocal() {
		return true;
	}

	public boolean isCompleted() {
		return false;
	}

	public String getToolTipText() {
		if (tasks.size() == 1) {
			return "1 task";
		} else {
			return tasks.size() + " tasks";
		}
	}

	public boolean isArchive() {
		return isArchive;
	}

	public void setIsArchive(boolean isArchive) {
		this.isArchive = isArchive;
		;
	}

	public String getStringForSortingDescription() {
		return getDescription();
	}

	public String toString() {
		return getDescription();
	}
}
