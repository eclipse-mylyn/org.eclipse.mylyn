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

import java.util.HashSet;
import java.util.Set;

/**
 * Manipulate containers via TaskListManager
 * 
 * @author Mik Kersten
 */
public abstract class AbstractTaskContainer implements ITaskListElement {

	private String handle = "";
	
	private Set<String> childHandles = new HashSet<String>();

	private TaskList taskList;
	
//	public abstract boolean isArchive();
//
//	public abstract void setIsArchive(boolean isArchive);

	public AbstractTaskContainer(String handleAndDescription, TaskList taskList) {
		assert handle != null;
		this.handle = handleAndDescription;
		this.taskList = taskList;
	}
	
	public abstract boolean isLocal();
	
	public Set<ITask> getChildren() {
		Set<ITask> children = new HashSet<ITask>();
		for (String childHandle : childHandles) {
			ITask task = taskList.getTask(childHandle);
			if (task != null) {
				children.add(task);
			}
		}
		return children;
	}

	public String getDescription() {
		return handle;
	}
	
	public String getHandleIdentifier() {
		return handle;
	}

	void setDescription(String description) {
		this.handle = description;
	}

	public void setHandleIdentifier(String handle) {
		this.handle = handle;
	}
	
	void add(ITask task) {
		childHandles.add(task.getHandleIdentifier());
	}
	
	void remove(ITask task) {
		childHandles.remove(task.getHandleIdentifier());
	}

	public boolean isCompleted() {
		return false;
	}

	public int hashCode() {
		return handle.hashCode();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (object instanceof AbstractTaskContainer) {
			AbstractTaskContainer compare = (AbstractTaskContainer) object;
			return this.getHandleIdentifier().equals(compare.getHandleIdentifier());
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "container: " + handle;
	}
}
