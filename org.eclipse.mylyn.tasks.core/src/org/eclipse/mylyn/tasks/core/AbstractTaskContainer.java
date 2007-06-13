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

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;

/**
 * Manipulate containers via TaskListManager
 * 
 * @author Mik Kersten
 */ 
public abstract class AbstractTaskContainer extends PlatformObject implements Comparable<AbstractTaskContainer> {

	private String handle = "";

	private Set<AbstractTask> children = new CopyOnWriteArraySet<AbstractTask>();

	/**
	 * Optional URL corresponding to the web resource associated with this
	 * container.
	 */
	protected String url = null;

	public AbstractTaskContainer(String handleAndDescription) {
		assert handle != null;
		this.handle = handleAndDescription;
	}
	
	public Set<AbstractTask> getChildren() {
		return Collections.unmodifiableSet(children);
	}

	public boolean contains(String handle) {
		for (AbstractTask child : children) {
			if (handle.equals(child.getHandleIdentifier())) {
				return true;
			}
		}
		return false;
	}

	public String getSummary() {
		return handle;
	}

	/**
	 * @since 2.0
	 */
	public boolean isEmpty() {
		return children.isEmpty();
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

	/**
	 * Use {@link TaskList} methods instead.
	 */
	@Deprecated
	protected void add(AbstractTask task) {
		children.add(task);
	}

	/**
	 * Does not delete task from TaskList
	 */
	@Deprecated
	protected void remove(AbstractTask task) {
		children.remove(task);
	}

	/**
	 * Does not delete tasks from TaskList
	 */
	public void clear() {
		children.clear();
	}

	@Override
	public int hashCode() {
		return handle.hashCode();
	}

	@Deprecated
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
	
	public String getPriority() {
		String highestPriority = PriorityLevel.P5.toString();
		Set<AbstractTask> tasks = getChildren();
		if (tasks.isEmpty()) {
			return PriorityLevel.P1.toString();
		}
		for (AbstractTask task : tasks) {
			if (highestPriority.compareTo(task.getPriority()) > 0) {
				highestPriority = task.getPriority();
			}
		}
		return highestPriority;
	}
	
	/**
	 * The handle for most containers is their summary. Override to specify a
	 * different natural ordering.
	 */
	public int compareTo(AbstractTaskContainer taskListElement) {
		return getHandleIdentifier().compareTo(((AbstractTaskContainer) taskListElement).getHandleIdentifier());
	}
}
