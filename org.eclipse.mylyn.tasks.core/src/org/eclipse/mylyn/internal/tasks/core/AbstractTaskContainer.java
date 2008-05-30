/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;

/**
 * Top-level Task List element that can contain other Task List elements.
 * 
 * @author Mik Kersten
 */
public abstract class AbstractTaskContainer extends PlatformObject implements IRepositoryElement, ITaskContainer {

	private String handleIdentifier = "";

	private final Collection<ITask> children = new CopyOnWriteArrayList<ITask>();

	/**
	 * Optional URL corresponding to the web resource associated with this container.
	 */
	private String url;

	public AbstractTaskContainer(String handleAndDescription) {
		Assert.isNotNull(handleAndDescription);
		this.handleIdentifier = handleAndDescription;
	}

	/**
	 * Use {@link TaskList} methods instead.
	 */
	public void internalAddChild(AbstractTask task) {
		Assert.isNotNull(task);
		children.add(task);
	}

	/**
	 * Use {@link TaskList} methods instead.
	 * 
	 * @return
	 * @since 3.0
	 */
	public boolean internalRemoveChild(ITask task) {
		return children.remove(task);
	}

	/**
	 * Removes any cyclic dependencies in children.
	 * 
	 * TODO: review to make sure that this is too expensive, or move to creation.
	 * 
	 * @since 3.0
	 */
	public Collection<ITask> getChildren() {
		return Collections.unmodifiableCollection(children);
	}

	/**
	 * Maxes out at a depth of 10.
	 * 
	 * TODO: review policy
	 */
	public boolean contains(String handle) {
		Assert.isNotNull(handle);
		return containsHelper(children, handle, new HashSet<IRepositoryElement>());
	}

	private boolean containsHelper(Collection<ITask> children, String handle, Set<IRepositoryElement> visitedContainers) {
		for (ITask child : children) {
			if (visitedContainers.contains(child)) {
				continue;
			}
			visitedContainers.add(child);
			if (child instanceof ITaskContainer) {
				if (handle.equals(child.getHandleIdentifier())
						|| containsHelper(((ITaskContainer) child).getChildren(), handle, visitedContainers)) {
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
		if (object == null) {
			return false;
		}
		if (object instanceof AbstractTaskContainer) {
			IRepositoryElement compare = (IRepositoryElement) object;
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
		Collection<ITask> tasks = getChildren();
		if (tasks.isEmpty()) {
			return PriorityLevel.P1.toString();
		}
		for (ITask task : tasks) {
			if (highestPriority.compareTo(task.getPriority()) > 0) {
				highestPriority = task.getPriority();
			}
		}
		return highestPriority;
	}

	/**
	 * The handle for most containers is their summary. Override to specify a different natural ordering.
	 */
	public int compareTo(IRepositoryElement taskListElement) {
		return getHandleIdentifier().compareTo(taskListElement.getHandleIdentifier());
	}

	/**
	 * If false, user is unable to manipulate (i.e. rename/delete), no preferences are available.
	 * 
	 * @since 2.3
	 */
	public boolean isUserManaged() {
		return true;
	}

}
