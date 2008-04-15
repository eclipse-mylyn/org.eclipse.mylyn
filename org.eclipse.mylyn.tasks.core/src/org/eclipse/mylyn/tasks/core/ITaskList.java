/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Collection;
import java.util.Set;

/**
 * @author Steffen Pingel
 * @author Robert Elves
 * @since 3.0
 */
public interface ITaskList {

	public abstract void addChangeListener(ITaskListChangeListener listener);

	public abstract void addQuery(AbstractRepositoryQuery query) throws IllegalArgumentException;

	/**
	 * Add orphaned task to the task list
	 */
	public abstract void addTask(AbstractTask task) throws IllegalArgumentException;

	/**
	 * Precondition: {@code container} already exists in tasklist (be it a parent task, category, or query) If the
	 * parentContainer is null the task is considered an orphan and added to the appropriate repository's orphaned tasks
	 * container.
	 * 
	 * @param task
	 *            to be added
	 * @param container
	 *            task container, query or parent task must not be null
	 */
	public abstract boolean addTask(AbstractTask task, AbstractTaskContainer parentContainer);

	public abstract void deleteCategory(AbstractTaskCategory category);

	public abstract void deleteQuery(AbstractRepositoryQuery query);

	/**
	 * TODO: refactor around querying containers for their tasks
	 * 
	 * Task is removed from all containers: root, archive, category, and orphan bin
	 * 
	 * Currently subtasks are not deleted but rather are rather potentially orphaned
	 */
	public abstract void deleteTask(AbstractTask task);

	public abstract Collection<AbstractTask> getAllTasks();

	public abstract Set<AbstractTaskCategory> getCategories();

	public abstract Set<AbstractRepositoryQuery> getQueries();

	/**
	 * @since 2.0
	 */
	public abstract AbstractTask getTask(String repositoryUrl, String taskId);

	/**
	 * Searches for a task whose key matches.
	 * 
	 * @return first task with a key, null if no matching task is found
	 * @since 2.0
	 */
	public abstract AbstractTask getTaskByKey(String repositoryUrl, String taskKey);

	/**
	 * @param task
	 * @param content
	 *            true if the content for the task (e.g. repository task data) has changed
	 */
	public abstract void notifyTaskChanged(AbstractTask task, boolean content);

	public abstract void removeChangeListener(ITaskListChangeListener listener);

	/**
	 * @since 3.0
	 */
	public abstract void removeFromContainer(AbstractTaskContainer container, AbstractTask task);

}