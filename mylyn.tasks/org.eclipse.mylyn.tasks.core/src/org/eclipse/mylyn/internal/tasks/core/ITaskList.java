/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.Set;

import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Steffen Pingel
 * @author Robert Elves
 * @author Mik Kersten
 * @since 3.0
 */
public interface ITaskList {

	void addChangeListener(ITaskListChangeListener listener);

	void addQuery(RepositoryQuery query) throws IllegalArgumentException;

	/**
	 * Add orphaned task to the task list
	 */
	void addTask(ITask task) throws IllegalArgumentException;

	/**
	 * Precondition: {@code container} already exists in tasklist (be it a parent task, category, or query) If the parentContainer is null
	 * the task is considered an orphan and added to the appropriate repository's orphaned tasks container.
	 * 
	 * @param task
	 *            to be added
	 * @param container
	 *            task container, query or parent task must not be null
	 */
	boolean addTask(ITask task, AbstractTaskContainer parentContainer);

	void deleteCategory(AbstractTaskCategory category);

	void deleteQuery(RepositoryQuery query);

	/**
	 * TODO: refactor around querying containers for their tasks Task is removed from all containers: root, archive, category, and orphan
	 * bin Currently subtasks are not deleted but rather are rather potentially orphaned
	 */
	void deleteTask(ITask task);

	Set<AbstractTaskCategory> getCategories();

	Set<RepositoryQuery> getQueries();

	/**
	 * @since 2.0
	 */
	ITask getTask(String repositoryUrl, String taskId);

	/**
	 * @param task
	 *            list element
	 */
	void notifyElementChanged(IRepositoryElement element);

	void notifySynchronizationStateChanged(IRepositoryElement element);

	void removeChangeListener(ITaskListChangeListener listener);

	/**
	 * @since 3.0
	 */
	void removeFromContainer(AbstractTaskContainer container, ITask task);

}