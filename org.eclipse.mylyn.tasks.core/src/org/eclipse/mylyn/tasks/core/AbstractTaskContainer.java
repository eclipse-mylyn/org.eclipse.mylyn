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
package org.eclipse.mylar.tasks.core;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.PlatformObject;

/**
 * Manipulate containers via TaskListManager
 * 
 * @author Mik Kersten
 */
public abstract class AbstractTaskContainer extends PlatformObject implements ITaskListElement {

	private String handle = "";

	private Set<String> childHandles = new CopyOnWriteArraySet<String>();

	protected TaskList taskList;

	/**
	 * Optional URL corresponding to the web resource associated with this
	 * container.
	 */
	protected String url = null;

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
	
	public boolean contains(String handle) {
		return childHandles.contains(handle);
	}

	public String getSummary() {
		return handle;
	}

	
	/**
	 * @since 2.0
	 */
	public boolean isEmpty() {
		return childHandles.isEmpty();
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

	void add(ITask task) {
		childHandles.add(task.getHandleIdentifier());
	}

	void remove(ITask task) {
		childHandles.remove(task.getHandleIdentifier());
	}

	void clear() {
		childHandles.clear();
	}

	public boolean isCompleted() {
		return false;
	}

	@Override
	public int hashCode() {
		return handle.hashCode();
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
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

	public boolean canRename() {
		return true;
	}

	/**
	 * The handle for most containers is their summary. Override to specify a
	 * different natural ordering.
	 */
	public int compareTo(ITaskListElement taskListElement) {
		return getHandleIdentifier().compareTo(((AbstractTaskContainer) taskListElement).getHandleIdentifier());
	}
}
