/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.tasks.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;

/**
 * Top-level Task List element that can contain other Task List elements.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public abstract class AbstractTaskContainer extends PlatformObject implements Comparable<AbstractTaskContainer> {

	private String handleIdentifier = "";

	private final Set<AbstractTask> children = new CopyOnWriteArraySet<AbstractTask>();

	/**
	 * Optional URL corresponding to the web resource associated with this container.
	 */
	protected String url = null;

	private final ILock lock = Job.getJobManager().newLock();

	public AbstractTaskContainer(String handleAndDescription) {
		assert handleIdentifier != null;
		this.handleIdentifier = handleAndDescription;
	}

	/**
	 * Use {@link TaskList} methods instead.
	 */
	public void internalAddChild(AbstractTask task) {
		if (task != null) {
			try {
				lock.acquire();
				children.add(task);
			} finally {
				lock.release();
			}
		}
	}

	/**
	 * Use {@link TaskList} methods instead.
	 */
	public void internalRemoveChild(AbstractTask task) {
		try {
			lock.acquire();
			children.remove(task);
		} finally {
			lock.release();
		}
	}

	/**
	 * Remove all children held by this container Does not delete tasks from TaskList
	 */
	public void clear() {
		try {
			lock.acquire();
			children.clear();
		} finally {
			lock.release();
		}
	}

	/**
	 * Removes any cyclic dependencies in children.
	 * 
	 * TODO: review to make sure that this is too expensive, or move to creation.
	 */
	public Set<AbstractTask> getChildren() {
		Set<AbstractTask> childrenWithoutCycles = new HashSet<AbstractTask>();
		try {
			lock.acquire();
			if (children.isEmpty()) {
				return Collections.emptySet();
			}

			childrenWithoutCycles = new HashSet<AbstractTask>(children.size());
			for (AbstractTask child : children) {
				if (child != null && !child.contains(this.getHandleIdentifier())) {
					childrenWithoutCycles.add(child);
				}
			}
		} finally {
			lock.release();
		}
		return childrenWithoutCycles;
	}

	/**
	 * Internal method. Do not use.
	 * 
	 * API-3.0: remove this method (bug 207659)
	 * 
	 * @since 2.2
	 */
	public Set<AbstractTask> getChildrenInternal() {
		try {
			lock.acquire();
			return Collections.unmodifiableSet(children);
		} finally {
			lock.release();
		}
	}

	/**
	 * Maxes out at a depth of 10.
	 * 
	 * TODO: review policy
	 */
	public boolean contains(String handle) {
		Assert.isNotNull(handle);
		return containsHelper(getChildrenInternal(), handle, new HashSet<AbstractTaskContainer>());
	}

	private boolean containsHelper(Set<AbstractTask> children, String handle,
			Set<AbstractTaskContainer> visitedContainers) {
		for (AbstractTask child : children) {
			if (visitedContainers.contains(child)) {
				continue;
			}
			visitedContainers.add(child);

			if (handle.equals(child.getHandleIdentifier())
					|| containsHelper(child.getChildrenInternal(), handle, visitedContainers)) {
				return true;
			}
		}
		return false;
	}

	public String getSummary() {
		try {
			lock.acquire();
			return handleIdentifier;
		} finally {
			lock.release();
		}
	}

	public boolean isEmpty() {
		try {
			lock.acquire();
			return children.isEmpty();
		} finally {
			lock.release();
		}
	}

	public String getHandleIdentifier() {
		try {
			lock.acquire();
			return handleIdentifier;
		} finally {
			lock.release();
		}
	}

	public void setHandleIdentifier(String handleIdentifier) {
		try {
			lock.acquire();
			this.handleIdentifier = handleIdentifier;
		} finally {
			lock.release();
		}
	}

	@Override
	public int hashCode() {
		try {
			lock.acquire();
			return handleIdentifier.hashCode();
		} finally {
			lock.release();
		}
	}

	public void setUrl(String url) {
		try {
			lock.acquire();
			this.url = url;
		} finally {
			lock.release();
		}
	}

	/**
	 * @return can be null
	 */
	public String getUrl() {
		try {
			lock.acquire();
			return url;
		} finally {
			lock.release();
		}
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
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

	/**
	 * If false, user is unable to manipulate (i.e. rename/delete), no preferences are available.
	 * 
	 * @since 2.3
	 */
	public boolean isUserManaged() {
		return true;
	}

}
