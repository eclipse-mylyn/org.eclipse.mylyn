/*******************************************************************************
 * Copyright (c) 2018 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.externalization;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITransferList;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * This class delegates the calls to underlying {@link ITransferList} while altering behavior of {@link #addTask(ITask)}
 * and {@link #addTask(ITask, category)} methods to avoid adding tasks being loaded to task list's
 * {@link UnmatchedTaskContainer} if they belong to a different task container. Instead, tasks added with
 * {@link #addTask(ITask)} method are stored internally, and only pushed to underlying {@link ITransferList} during
 * subsequent calls to {@link #addTask(ITask, category)} or on final {@link #commit(void)}.
 * <p>
 * <b>Usage</b>
 * <p>
 * The caller can call {@link #addTask(ITask)} and {@link #addTask(ITask, category)} methods to load task list as usual.
 * <p>
 * The caller is expected to call {@link #commit(void)} to add the remaining uncategorized tasks to task list.
 *
 * @see {@link #addTask(ITask)}
 * @see {@link #addTask(ITask, AbstractTaskContainer)}
 * @see {@link #commit()}
 */
class LazyTransferList implements ITransferList {

	private final Map<String, ITask> untransferedTasks = new HashMap<>();

	private final ITransferList taskList;

	LazyTransferList(ITransferList taskList) {
		this.taskList = taskList;
	}

	public void addCategory(TaskCategory category) {
		taskList.addCategory(category);
	}

	public void addQuery(RepositoryQuery query) {
		taskList.addQuery(query);
	}

	public void addTask(ITask task) {
		untransferedTasks.put(task.getHandleIdentifier(), task);
	}

	public boolean addTask(ITask task, AbstractTaskContainer parentContainer) {
		boolean result = taskList.addTask(task, commit(parentContainer));
		untransferedTasks.remove(task.getHandleIdentifier());
		return result;
	}

	public AbstractTaskCategory getContainerForHandle(String handle) {
		return taskList.getContainerForHandle(handle);
	}

	public Collection<AbstractTask> getAllTasks() {
		return taskList.getAllTasks();
	}

	public Set<AbstractTaskCategory> getCategories() {
		return taskList.getCategories();
	}

	public Set<RepositoryQuery> getQueries() {
		return taskList.getQueries();
	}

	public AbstractTask getTask(String handleIdentifier) {
		AbstractTask task = (AbstractTask) untransferedTasks.get(handleIdentifier);

		if (task == null) {
			task = taskList.getTask(handleIdentifier);
		}

		return task;
	}

	public ITask getTask(String repositoryUrl, String taskId) {
		return taskList.getTask(repositoryUrl, taskId);
	}

	/**
	 * If the container is an {@link ITask}, pushes it to task list
	 *
	 * @param container
	 */
	private AbstractTaskContainer commit(AbstractTaskContainer container) {
		if (container instanceof ITask) {
			AbstractTask task = (AbstractTask) untransferedTasks.get(container.getHandleIdentifier());

			if (task != null) {
				taskList.addTask(task);
				untransferedTasks.remove(container.getHandleIdentifier());
			}
		}

		return container;
	}

	/**
	 * Pushes the remaining tasks to task list
	 */
	public void commit() {
		for (ITask task : untransferedTasks.values()) {
			taskList.addTask(task);
		}
		untransferedTasks.clear();
	}
}