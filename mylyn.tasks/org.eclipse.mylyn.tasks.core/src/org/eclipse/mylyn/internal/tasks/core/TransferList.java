/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * Used to externalize queries.
 *
 * @author Steffen
 */
public class TransferList implements ITransferList {

	private final Set<RepositoryQuery> queries;

	private final List<AbstractTask> tasks;

	private final Set<AbstractTaskCategory> categories;

	public TransferList() {
		queries = new HashSet<>();
		tasks = new ArrayList<>();
		categories = new HashSet<>();
	}

	public TransferList(Set<AbstractTaskCategory> categories, Set<RepositoryQuery> queries, List<AbstractTask> tasks) {
		this.tasks = new ArrayList<>(tasks);
		this.queries = new HashSet<>(queries);
		this.categories = new HashSet<>(categories);
	}

	@Override
	public void addCategory(TaskCategory category) {
		categories.add(category);
	}

	@Override
	public void addQuery(RepositoryQuery query) {
		queries.add(query);
	}

	@Override
	public void addTask(ITask task) {
		tasks.add((AbstractTask) task);
	}

	@Override
	public boolean addTask(ITask task, AbstractTaskContainer parentContainer) {
		if (!tasks.contains(task)) {
			tasks.add((AbstractTask) task);
		}
		parentContainer.internalAddChild((AbstractTask) task);
		return true;
	}

	@Override
	public Collection<AbstractTask> getAllTasks() {
		return tasks;
	}

	@Override
	public Set<AbstractTaskCategory> getCategories() {
		return categories;
	}

	@Override
	public AbstractTaskCategory getContainerForHandle(String handle) {
		Assert.isNotNull(handle);
		for (AbstractTaskCategory category : categories) {
			if (category.getHandleIdentifier().equals(handle)) {
				return category;
			}
		}
		return null;
	}

	@Override
	public Set<RepositoryQuery> getQueries() {
		return queries;
	}

	@Override
	public AbstractTask getTask(String handleIdentifier) {
		Assert.isNotNull(handleIdentifier);
		for (AbstractTask task : tasks) {
			if (task.getHandleIdentifier().equals(handleIdentifier)) {
				return task;
			}
		}
		return null;
	}

	@Override
	public ITask getTask(String repositoryUrl, String taskId) {
		Assert.isNotNull(repositoryUrl);
		Assert.isNotNull(taskId);
		for (AbstractTask task : tasks) {
			if (task.getRepositoryUrl().equals(repositoryUrl) && task.getTaskId().equals(taskId)) {
				return task;
			}
		}
		return null;
	}

}
