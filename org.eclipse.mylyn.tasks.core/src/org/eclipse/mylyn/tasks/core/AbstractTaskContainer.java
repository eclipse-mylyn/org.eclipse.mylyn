/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.tasks.core;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;

/**
 * Top-level Task List element that can contain other Task List elements.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public abstract class AbstractTaskContainer extends PlatformObject implements Comparable<AbstractTaskContainer> {

	private String handleIdentifier = "";

	private Set<AbstractTask> children = new CopyOnWriteArraySet<AbstractTask>();

	/**
	 * Optional URL corresponding to the web resource associated with this container.
	 */
	protected String url = null;

	public AbstractTaskContainer(String handleAndDescription) {
		assert handleIdentifier != null;
		this.handleIdentifier = handleAndDescription;
	}

	/**
	 * Use {@link TaskList} methods instead.
	 */
	public void internalAddChild(AbstractTask task) {
		children.add(task);
	}

	/**
	 * Use {@link TaskList} methods instead.
	 */
	public void internalRemoveChild(AbstractTask task) {
		children.remove(task);
	}

	/**
	 * Does not delete tasks from TaskList
	 */
	public void clear() {
		children.clear();
	}

	public Set<AbstractTask> getChildren() {
		return Collections.unmodifiableSet(children);
	}

	public boolean contains(String handle) {
		for (AbstractTask child : children) {
			if (handle.equals(child.getHandleIdentifier())) {
				return true;
			} else if(child.getChildren() != null && child.getChildren().size() > 0){
				if(child.contains(handle)){
					return true;
				}
			}
		}
		return false;
	}

	public String getSummary() {
		return handleIdentifier;
	}

	public boolean isEmpty() {
		return children.isEmpty();
	}

	public String getHandleIdentifier() {
		return handleIdentifier;
	}

	public void setHandleIdentifier(String handleIdentifier) {
		this.handleIdentifier = handleIdentifier;
	}

	@Override
	public int hashCode() {
		return handleIdentifier.hashCode();
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return can be null
	 */
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
		return "container: " + handleIdentifier;
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
	 * The handle for most containers is their summary. Override to specify a different natural ordering.
	 */
	public int compareTo(AbstractTaskContainer taskListElement) {
		return getHandleIdentifier().compareTo(taskListElement.getHandleIdentifier());
	}
}
