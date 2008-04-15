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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITaskList;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.TaskContainerDelta;

/**
 * Stores and manages task list elements and their containment hierarchy.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @since 2.0
 */
public class TaskList implements ISchedulingRule, ITaskList {

	private static ILock lock = Job.getJobManager().newLock();

	private Map<String, AbstractTaskCategory> categories;

	private final Set<ITaskListChangeListener> changeListeners = new CopyOnWriteArraySet<ITaskListChangeListener>();

	private UncategorizedTaskContainer defaultCategory;

	private int maxLocalTaskId;

	private Map<String, AbstractRepositoryQuery> queries;

	private Map<String, UnmatchedTaskContainer> repositoryOrphansMap;

	private Map<String, AbstractTask> tasks;

	public TaskList() {
		reset();
	}

	public void addCategory(TaskCategory category) throws IllegalArgumentException {
		Assert.isNotNull(category);

		try {
			lock.acquire();
			categories.put(category.getHandleIdentifier(), category);
		} finally {
			lock.release();
		}
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		delta.add(new TaskContainerDelta(category, TaskContainerDelta.Kind.ADDED));
		fireDelta(delta);
	}

	public void addChangeListener(ITaskListChangeListener listener) {
		changeListeners.add(listener);
	}

	/**
	 * precondition: task must not be null and must exist in the task list
	 */
	private void addOrphan(AbstractTask task, Set<TaskContainerDelta> delta) {
		if (!task.getParentContainers().isEmpty()) {
			// Current policy is not to archive/orphan if the task exists in some other container
			return;
		}

		AbstractTaskContainer orphans = getUnmatchedContainer(task.getRepositoryUrl());
		if (orphans != null) {
			task.addParentContainer(orphans);
			orphans.internalAddChild(task);
			delta.add(new TaskContainerDelta(orphans, TaskContainerDelta.Kind.CHANGED));
		}
	}

	public void addQuery(AbstractRepositoryQuery query) throws IllegalArgumentException {
		Assert.isNotNull(query);

		try {
			lock.acquire();
			queries.put(query.getHandleIdentifier(), query);
		} finally {
			lock.release();
		}
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		delta.add(new TaskContainerDelta(query, TaskContainerDelta.Kind.ADDED));
		fireDelta(delta);
	}

	/**
	 * Add orphaned task to the task list
	 */
	public void addTask(AbstractTask task) {
		addTask(task, null);
	}

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
	public void addTask(AbstractTask task, AbstractTaskContainer parentContainer) {
		moveTask(task, parentContainer);
	}

	public void addUnmatchedContainer(UnmatchedTaskContainer orphanedTasksContainer) {
		repositoryOrphansMap.put(orphanedTasksContainer.getRepositoryUrl(), orphanedTasksContainer);
	}

	/**
	 * @since 3.0
	 */
	public boolean contains(ISchedulingRule rule) {
		return isConflicting(rule);
	}

	public void deleteCategory(AbstractTaskCategory category) {
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		try {
			lock.acquire();
			categories.remove(category.getHandleIdentifier());
			for (AbstractTask task : category.getChildren()) {
				task.removeParentContainer(category);
				addOrphan(task, delta);
			}
		} finally {
			lock.release();
		}
		delta.add(new TaskContainerDelta(category, TaskContainerDelta.Kind.REMOVED));
		fireDelta(delta);
	}

	public void deleteQuery(AbstractRepositoryQuery query) {
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		try {
			lock.acquire();
			queries.remove(query.getHandleIdentifier());
			for (AbstractTask task : query.getChildren()) {
				task.removeParentContainer(query);
				addOrphan(task, delta);
			}
		} finally {
			lock.release();
		}
		delta.add(new TaskContainerDelta(query, TaskContainerDelta.Kind.REMOVED));
		fireDelta(delta);
	}

	/**
	 * Task is removed from all containers. Currently subtasks are not deleted but rather are rather potentially
	 * orphaned.
	 */
	public void deleteTask(AbstractTask task) {
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		try {
			lock.acquire();

			// remove task from all parent containers
			for (AbstractTaskContainer container : task.getParentContainers()) {
				removeFromContainerInternal(container, task, delta);
			}

			// remove this task as a parent for all subtasks
			for (AbstractTask child : task.getChildren()) {
				removeFromContainerInternal(task, child, delta);
				addOrphan(child, delta);
			}

			task.clear();

			tasks.remove(task.getHandleIdentifier());
		} finally {
			lock.release();
		}
		delta.add(new TaskContainerDelta(task, TaskContainerDelta.Kind.REMOVED));
		fireDelta(delta);
	}

	private void fireDelta(Set<TaskContainerDelta> delta) {
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	public Collection<AbstractTask> getAllTasks() {
		return Collections.unmodifiableCollection(tasks.values());
	}

	public Set<AbstractTaskCategory> getCategories() {
		return Collections.unmodifiableSet(new HashSet<AbstractTaskCategory>(categories.values()));
	}

	/**
	 * Exposed for unit testing
	 * 
	 * @return unmodifiable collection of ITaskActivityListeners
	 */
	public Set<ITaskListChangeListener> getChangeListeners() {
		return Collections.unmodifiableSet(changeListeners);
	}

	public AbstractTaskCategory getContainerForHandle(String categoryHandle) {
		Assert.isNotNull(categoryHandle);

		for (AbstractTaskCategory cat : categories.values()) {
			if (cat.getHandleIdentifier().equals(categoryHandle)) {
				return cat;
			}
		}
		return null;
	}

	public AbstractTaskCategory getDefaultCategory() {
		return defaultCategory;
	}

	public int getLastLocalTaskId() {
		return maxLocalTaskId;
	}

	public int getNextLocalTaskId() {
		try {
			lock.acquire();
			return ++maxLocalTaskId;
		} finally {
			lock.release();
		}
	}

	private AbstractTask getOrCreateTask(AbstractTask taskListElement) {
		AbstractTask task = tasks.get(taskListElement.getHandleIdentifier());
		if (task == null) {
			if (task instanceof LocalTask) {
				int taskId = Integer.parseInt(task.getTaskId());
				maxLocalTaskId = Math.max(maxLocalTaskId, taskId);
			}
			tasks.put(taskListElement.getHandleIdentifier(), taskListElement);
			task = taskListElement;
		}
		return task;
	}

	public Set<AbstractRepositoryQuery> getQueries() {
		return Collections.unmodifiableSet(new HashSet<AbstractRepositoryQuery>(queries.values()));
	}

	/**
	 * return all queries for the given repository url
	 */
	public Set<AbstractRepositoryQuery> getRepositoryQueries(String repositoryUrl) {
		Assert.isNotNull(repositoryUrl);

		Set<AbstractRepositoryQuery> repositoryQueries = new HashSet<AbstractRepositoryQuery>();
		for (AbstractRepositoryQuery query : queries.values()) {
			if (query.getRepositoryUrl().equals(repositoryUrl)) {
				repositoryQueries.add(query);
			}
		}
		return repositoryQueries;
	}

	/**
	 * Returns all tasks for the given repository url.
	 * 
	 * API-3.0: add a parameter for the kind
	 */
	public Set<AbstractTask> getRepositoryTasks(String repositoryUrl) {
		Set<AbstractTask> repositoryTasks = new HashSet<AbstractTask>();
		if (repositoryUrl != null) {
			for (AbstractTask task : tasks.values()) {
				if (task.getRepositoryUrl().equals(repositoryUrl)) {
					repositoryTasks.add(task);
				}
			}
		}
		return repositoryTasks;
	}

	public Set<AbstractTaskContainer> getRootElements() {
		Set<AbstractTaskContainer> roots = new HashSet<AbstractTaskContainer>();
		roots.add(defaultCategory);
		for (AbstractTaskCategory cat : categories.values()) {
			roots.add(cat);
		}
		for (AbstractRepositoryQuery query : queries.values()) {
			roots.add(query);
		}
		for (UnmatchedTaskContainer orphanContainer : repositoryOrphansMap.values()) {
			roots.add(orphanContainer);
		}
		return roots;
	}

	/**
	 * TODO: consider removing, if everything becomes a repository task
	 * 
	 * @return null if no such task.
	 */
	public AbstractTask getTask(String handleIdentifier) {
		if (handleIdentifier == null) {
			return null;
		} else {
			return tasks.get(handleIdentifier);
		}
	}

	/**
	 * @since 2.0
	 */
	public AbstractTask getTask(String repositoryUrl, String taskId) {
		if (!RepositoryTaskHandleUtil.isValidTaskId(taskId)) {
			return null;
		}

		String handle = RepositoryTaskHandleUtil.getHandle(repositoryUrl, taskId);
		return getTask(handle);
	}

	/**
	 * Searches for a task whose key matches.
	 * 
	 * @return first task with a key, null if no matching task is found
	 * @since 2.0
	 */
	public AbstractTask getTaskByKey(String repositoryUrl, String taskKey) {
		for (AbstractTask task : tasks.values()) {
			String currentTaskKey = task.getTaskKey();
			if (currentTaskKey != null && currentTaskKey.equals(taskKey)
					&& task.getRepositoryUrl().equals(repositoryUrl)) {
				return task;
			}
		}
		return null;
	}

	/**
	 * Returns all categories except for the Uncategorized container. Does not return Unmatched containers.
	 */
	public Set<AbstractTaskCategory> getTaskContainers() {
		Set<AbstractTaskCategory> containers = new HashSet<AbstractTaskCategory>();
		for (AbstractTaskCategory container : categories.values()) {
			if (container instanceof TaskCategory) {
				containers.add(container);
			}
		}
		return containers;
	}

	public AbstractTaskContainer getUnmatchedContainer(String repositoryUrl) {
		if (LocalRepositoryConnector.REPOSITORY_URL.equals(repositoryUrl)) {
			return defaultCategory;
		} else {
			UnmatchedTaskContainer orphans = repositoryOrphansMap.get(repositoryUrl);
			if (orphans == null) {
				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
						"Failed to find unmatched container for repository \"" + repositoryUrl + "\""));
			}
			return orphans;
		}
	}

	public Set<UnmatchedTaskContainer> getUnmatchedContainers() {
		return Collections.unmodifiableSet(new HashSet<UnmatchedTaskContainer>(repositoryOrphansMap.values()));
	}

	/**
	 * Task added if does not exist already. Ensures the element exists in the task list
	 * 
	 * @throws IllegalAgumentException
	 *             if null argument passed or element does not exist in task list
	 * @return element as passed in or instance from task list with same handle if exists
	 */
	private AbstractTaskContainer getValidElement(AbstractTaskContainer taskListElement) {
		AbstractTaskContainer result = null;
		if (taskListElement instanceof AbstractTask) {
			result = tasks.get(taskListElement.getHandleIdentifier());
		} else if (taskListElement instanceof UncategorizedTaskContainer) {
			result = defaultCategory;
		} else if (taskListElement instanceof UnmatchedTaskContainer) {
			result = repositoryOrphansMap.get(((UnmatchedTaskContainer) taskListElement).getRepositoryUrl());
		} else if (taskListElement instanceof TaskCategory) {
			result = categories.get(taskListElement.getHandleIdentifier());
		} else if (taskListElement instanceof AbstractRepositoryQuery) {
			result = queries.get(taskListElement.getHandleIdentifier());
		}

		if (result == null) {
			throw new IllegalArgumentException("Element " + taskListElement.getHandleIdentifier()
					+ " does not exist in the task list.");
		} else {
			return result;
		}
	}

	/**
	 * @since 3.0
	 */
	public boolean isConflicting(ISchedulingRule rule) {
		return rule instanceof TaskList || rule instanceof AbstractTaskContainer;
	}

	/**
	 * @since 2.2
	 */
	public boolean moveTask(AbstractTask task, AbstractTaskContainer container) {
		Assert.isNotNull(task);
		Assert.isLegal(!(container instanceof UnmatchedTaskContainer));

		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		try {
			lock.acquire();

			task = getOrCreateTask(task);
			if (container == null) {
				container = getUnmatchedContainer(task.getRepositoryUrl());
			} else {
				container = getValidElement(container);
			}

			// ensure parent is valid and does not contain task already
			if (container == null || task.getParentContainers().contains(container)) {
				return false;
			}

			// ensure that we don't create cycles
			if (task.contains(container.getHandleIdentifier())) {
				return false;
			}

			if (task instanceof LocalTask && task.getParentContainers().size() > 0) {
				// local tasks should only have 1 parent
				for (AbstractTaskContainer parent : task.getParentContainers()) {
					removeFromContainerInternal(parent, task, delta);
				}
			} else if (container instanceof AbstractTaskCategory) {
				// tasks can only be in one task category at a time
				AbstractTaskCategory tempCat = TaskCategory.getParentTaskCategory(task);
				if (tempCat != null) {
					removeFromContainerInternal(tempCat, task, delta);
				}
			}

			removeOrphan(task, delta);

			task.addParentContainer(container);
			container.internalAddChild(task);
		} finally {
			lock.release();
		}

		delta.add(new TaskContainerDelta(task, TaskContainerDelta.Kind.CHANGED));
		delta.add(new TaskContainerDelta(container, TaskContainerDelta.Kind.CHANGED));
		fireDelta(delta);
		return true;
	}

	public void notifyContainersUpdated(Set<? extends AbstractTaskContainer> containers) {
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		if (containers == null) {
			delta.add(new TaskContainerDelta(null, TaskContainerDelta.Kind.ROOT));
		} else {
			for (AbstractTaskContainer abstractTaskContainer : containers) {
				delta.add(new TaskContainerDelta(abstractTaskContainer, TaskContainerDelta.Kind.CHANGED));
			}
		}

		for (ITaskListChangeListener listener : changeListeners) {
			try {
				listener.containersChanged(Collections.unmodifiableSet(delta));
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Notification failed for: "
						+ listener, t));
			}
		}
	}

	/**
	 * @param task
	 * @param content
	 *            true if the content for the task (e.g. repository task data) has changed
	 */
	public void notifyTaskChanged(AbstractTask task, boolean content) {
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		TaskContainerDelta.Kind kind;
		if (content) {
			kind = TaskContainerDelta.Kind.CONTENT;
		} else {
			kind = TaskContainerDelta.Kind.CHANGED;
		}
		delta.add(new TaskContainerDelta(task, kind));

		for (ITaskListChangeListener listener : changeListeners) {
			try {
				listener.containersChanged(Collections.unmodifiableSet(delta));
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Notification failed for: "
						+ listener, t));
			}
		}
	}

	public void refactorRepositoryUrl(String oldRepositoryUrl, String newRepositoryUrl) {
		Assert.isNotNull(oldRepositoryUrl);
		Assert.isNotNull(newRepositoryUrl);

		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		try {
			lock.acquire();
			for (AbstractTask task : tasks.values()) {
				if (oldRepositoryUrl.equals(RepositoryTaskHandleUtil.getRepositoryUrl(task.getHandleIdentifier()))) {
					tasks.remove(task.getHandleIdentifier());
					task.setRepositoryUrl(newRepositoryUrl);
					tasks.put(task.getHandleIdentifier(), task);
					String taskUrl = task.getUrl();
					if (taskUrl != null && taskUrl.startsWith(oldRepositoryUrl)) {
						task.setUrl(newRepositoryUrl + taskUrl.substring(oldRepositoryUrl.length()));
					}
				}
			}

			for (AbstractRepositoryQuery query : queries.values()) {
				if (query.getRepositoryUrl().equals(oldRepositoryUrl)) {
					query.setRepositoryUrl(newRepositoryUrl);
					delta.add(new TaskContainerDelta(query, TaskContainerDelta.Kind.CHANGED));
				}
			}

			for (UnmatchedTaskContainer orphans : repositoryOrphansMap.values()) {
				if (orphans.getRepositoryUrl().equals(oldRepositoryUrl)) {
					repositoryOrphansMap.remove(oldRepositoryUrl);
					//categories.remove(orphans.getHandleIdentifier());
					orphans.setRepositoryUrl(newRepositoryUrl);
					repositoryOrphansMap.put(newRepositoryUrl, orphans);
					//categories.put(orphans.getHandleIdentifier(), orphans);
					delta.add(new TaskContainerDelta(orphans, TaskContainerDelta.Kind.CHANGED));
				}
			}
		} finally {
			lock.release();
		}
		fireDelta(delta);
	}

	public void removeChangeListener(ITaskListChangeListener listener) {
		changeListeners.remove(listener);
	}

	public void removeFromContainer(AbstractTaskContainer container, AbstractTask task) {
		Assert.isNotNull(container);
		Assert.isNotNull(task);

		removeFromContainer(container, Collections.singleton(task));
	}

	public void removeFromContainer(AbstractTaskContainer container, Set<AbstractTask> tasks) {
		Assert.isNotNull(container);
		Assert.isNotNull(tasks);

		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		try {
			lock.acquire();
			for (AbstractTask task : tasks) {
				removeFromContainerInternal(container, task, delta);
				addOrphan(task, delta);
			}
		} finally {
			lock.release();
		}
		fireDelta(delta);
	}

	/**
	 * Note: does not add <code>task</code> to the unmatched container.
	 */
	private void removeFromContainerInternal(AbstractTaskContainer container, AbstractTask task,
			Set<TaskContainerDelta> delta) {
		assert container.getChildren().contains(task);

		container.internalRemoveChild(task);
		task.removeParentContainer(container);

		delta.add(new TaskContainerDelta(task, TaskContainerDelta.Kind.CHANGED));
		delta.add(new TaskContainerDelta(container, TaskContainerDelta.Kind.CHANGED));
	}

	private void removeOrphan(AbstractTask task, Set<TaskContainerDelta> delta) {
		AbstractTaskContainer orphans = getUnmatchedContainer(task.getRepositoryUrl());
		if (orphans != null) {
			if (orphans.internalRemoveChild(task)) {
				delta.add(new TaskContainerDelta(orphans, TaskContainerDelta.Kind.CHANGED));
				task.removeParentContainer(orphans);
			}
		}
	}

	/**
	 * TODO separate category/query handle from name
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void renameContainer(AbstractTaskContainer container, String newDescription) {
		Assert.isLegal(!(container instanceof AbstractTask));
		Assert.isLegal(!(container instanceof UnmatchedTaskContainer));

		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		try {
			lock.acquire();

			if (queries.remove(container.getHandleIdentifier()) != null) {
				if (container instanceof AbstractTaskCategory) {
					((AbstractTaskCategory) container).setHandleIdentifier(newDescription);
				} else if (container instanceof AbstractRepositoryQuery) {
					((AbstractRepositoryQuery) container).setHandleIdentifier(newDescription);
					queries.put(((AbstractRepositoryQuery) container).getHandleIdentifier(),
							((AbstractRepositoryQuery) container));
				}
			} else if (container instanceof TaskCategory && categories.remove(container.getHandleIdentifier()) != null) {
				((TaskCategory) container).setHandleIdentifier(newDescription);
				categories.put(((TaskCategory) container).getHandleIdentifier(), (TaskCategory) container);
			}
		} finally {
			lock.release();
		}
		// TODO: make this delta policy symmetrical with tasks
		delta.add(new TaskContainerDelta(container, TaskContainerDelta.Kind.REMOVED));
		delta.add(new TaskContainerDelta(container, TaskContainerDelta.Kind.ADDED));
		fireDelta(delta);
	}

	/**
	 * Public for testing.
	 */
	public void reset() {
		try {
			lock.acquire();
			tasks = new ConcurrentHashMap<String, AbstractTask>();

			repositoryOrphansMap = new ConcurrentHashMap<String, UnmatchedTaskContainer>();

			categories = new ConcurrentHashMap<String, AbstractTaskCategory>();
			queries = new ConcurrentHashMap<String, AbstractRepositoryQuery>();

			defaultCategory = new UncategorizedTaskContainer();

			maxLocalTaskId = 0;
			categories.put(defaultCategory.getHandleIdentifier(), defaultCategory);
		} finally {
			lock.release();
		}
	}

}
