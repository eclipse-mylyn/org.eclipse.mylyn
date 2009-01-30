/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
		this.queries = new HashSet<RepositoryQuery>();
		this.tasks = new ArrayList<AbstractTask>();
		this.categories = new HashSet<AbstractTaskCategory>();
	}

	public TransferList(Set<AbstractTaskCategory> categories, Set<RepositoryQuery> queries, List<AbstractTask> tasks) {
		this.tasks = new ArrayList<AbstractTask>(tasks);
		this.queries = new HashSet<RepositoryQuery>(queries);
		this.categories = new HashSet<AbstractTaskCategory>(categories);
	}

	public void addCategory(TaskCategory category) {
		categories.add(category);
	}

	public void addQuery(RepositoryQuery query) {
		queries.add(query);
	}

	public void addTask(ITask task) {
		tasks.add((AbstractTask) task);
	}

	public boolean addTask(ITask task, AbstractTaskContainer parentContainer) {
		tasks.add((AbstractTask) task);
		return true;
	}

	public Collection<AbstractTask> getAllTasks() {
		return tasks;
	}

	public Set<AbstractTaskCategory> getCategories() {
		return categories;
	}

	public AbstractTaskCategory getContainerForHandle(String handle) {
		Assert.isNotNull(handle);
		for (AbstractTaskCategory category : categories) {
			if (category.getHandleIdentifier().equals(handle)) {
				return category;
			}
		}
		return null;
	}

	public Set<RepositoryQuery> getQueries() {
		return queries;
	}

	public AbstractTask getTask(String handleIdentifier) {
		Assert.isNotNull(handleIdentifier);
		for (AbstractTask task : tasks) {
			if (task.getHandleIdentifier().equals(handleIdentifier)) {
				return task;
			}
		}
		return null;
	}

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
