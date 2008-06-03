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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * Stores and manages task list elements and their containment hierarchy.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @since 3.0
 */
public class TaskList implements ITaskList {

	private static ILock lock = Job.getJobManager().newLock();

	private Map<String, AbstractTaskCategory> categories;

	private final Set<ITaskListChangeListener> changeListeners = new CopyOnWriteArraySet<ITaskListChangeListener>();

	private UncategorizedTaskContainer defaultCategory;

	private int maxLocalTaskId;

	private Map<String, RepositoryQuery> queries;

	private Map<String, UnmatchedTaskContainer> repositoryOrphansMap;

	private Map<String, AbstractTask> tasks;

	private Set<TaskContainerDelta> delta;

	public TaskList() {
		reset();
	}

	public void addCategory(TaskCategory category) {
		Assert.isNotNull(category);
		try {
			lock();
			if (categories.containsKey(category.getHandleIdentifier())) {
				throw new IllegalArgumentException("Handle " + category.getHandleIdentifier()
						+ " already exists in task list");
			}
			categories.put(category.getHandleIdentifier(), category);
			delta.add(new TaskContainerDelta(category, TaskContainerDelta.Kind.ADDED));
		} finally {
			unlock();
		}
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
			delta.add(new TaskContainerDelta(task, orphans, TaskContainerDelta.Kind.ADDED));
		}
	}

	public void addQuery(RepositoryQuery query) throws IllegalArgumentException {
		Assert.isNotNull(query);
		try {
			lock();
			if (queries.containsKey(query.getHandleIdentifier())) {
				throw new IllegalArgumentException("Handle " + query.getHandleIdentifier()
						+ " already exists in task list");
			}
			queries.put(query.getHandleIdentifier(), query);
			delta.add(new TaskContainerDelta(query, TaskContainerDelta.Kind.ADDED));
		} finally {
			unlock();
		}
	}

	/**
	 * Add orphaned task to the task list
	 */
	public void addTask(ITask task) {
		addTask(task, null);
	}

	public boolean addTask(ITask itask, AbstractTaskContainer container) {
		AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(task);
		Assert.isLegal(!(container instanceof UnmatchedTaskContainer));

		try {
			lock();
			task = getOrCreateTask(task);
			if (container == null) {
				container = getUnmatchedContainer(task.getRepositoryUrl());
			} else {
				container = getValidElement(container);
			}

			// ensure parent is valid and does not contain task already
			if (container == null || task.equals(container) || task.getParentContainers().contains(container)) {
				return false;
			}

			// ensure that we don't create cycles
			if ((task).contains(container.getHandleIdentifier())) {
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

			(task).addParentContainer(container);
			container.internalAddChild(task);
			delta.add(new TaskContainerDelta(task, container, TaskContainerDelta.Kind.ADDED));
		} finally {
			unlock();
		}

		return true;
	}

	public void addUnmatchedContainer(UnmatchedTaskContainer orphanedTasksContainer) {
		repositoryOrphansMap.put(orphanedTasksContainer.getRepositoryUrl(), orphanedTasksContainer);
	}

	public void deleteCategory(AbstractTaskCategory category) {
		try {
			lock();
			categories.remove(category.getHandleIdentifier());
			for (ITask task : category.getChildren()) {
				((AbstractTask) task).removeParentContainer(category);
				addOrphan((AbstractTask) task, delta);
			}
			delta.add(new TaskContainerDelta(category, TaskContainerDelta.Kind.REMOVED));
		} finally {
			unlock();
		}
	}

	public void deleteQuery(RepositoryQuery query) {
		try {
			lock();
			queries.remove(query.getHandleIdentifier());
			for (ITask task : query.getChildren()) {
				((AbstractTask) task).removeParentContainer(query);
				addOrphan((AbstractTask) task, delta);
			}
			delta.add(new TaskContainerDelta(query, TaskContainerDelta.Kind.REMOVED));
		} finally {
			unlock();
		}
	}

	/**
	 * Task is removed from all containers. Currently subtasks are not deleted but rather are rather potentially
	 * orphaned.
	 */
	public void deleteTask(ITask itask) {
		Assert.isNotNull(itask);
		AbstractTask task = (AbstractTask) itask;
		try {
			lock();

			// remove task from all parent containers
			for (AbstractTaskContainer container : task.getParentContainers()) {
				removeFromContainerInternal(container, task, delta);
			}

			// remove this task as a parent for all subtasks
			for (ITask child : task.getChildren()) {
				removeFromContainerInternal(task, child, delta);
				addOrphan((AbstractTask) child, delta);
			}

			tasks.remove(task.getHandleIdentifier());
			delta.add(new TaskContainerDelta(task, TaskContainerDelta.Kind.REMOVED));
		} finally {
			unlock();
		}
	}

	private void fireDelta(HashSet<TaskContainerDelta> deltasToFire) {
		for (ITaskListChangeListener listener : changeListeners) {
			try {
				listener.containersChanged(Collections.unmodifiableSet(deltasToFire));
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Notification failed for: "
						+ listener, t));
			}
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
			lock();
			return ++maxLocalTaskId;
		} finally {
			unlock();
		}
	}

	private AbstractTask getOrCreateTask(AbstractTask taskListElement) {
		AbstractTask task = tasks.get(taskListElement.getHandleIdentifier());
		if (task == null) {
			tasks.put(taskListElement.getHandleIdentifier(), taskListElement);
			task = taskListElement;
			if (task instanceof LocalTask) {
				try {
					int taskId = Integer.parseInt(task.getTaskId());
					maxLocalTaskId = Math.max(maxLocalTaskId, taskId);
				} catch (NumberFormatException e) {
					// ignore
				}
			}
		}
		return task;
	}

	public Set<RepositoryQuery> getQueries() {
		return Collections.unmodifiableSet(new HashSet<RepositoryQuery>(queries.values()));
	}

	/**
	 * return all queries for the given repository url
	 */
	public Set<RepositoryQuery> getRepositoryQueries(String repositoryUrl) {
		Assert.isNotNull(repositoryUrl);

		Set<RepositoryQuery> repositoryQueries = new HashSet<RepositoryQuery>();
		for (RepositoryQuery query : queries.values()) {
			if (query.getRepositoryUrl().equals(repositoryUrl)) {
				repositoryQueries.add(query);
			}
		}
		return repositoryQueries;
	}

	public Set<AbstractTaskContainer> getRootElements() {
		Set<AbstractTaskContainer> roots = new HashSet<AbstractTaskContainer>();
		roots.add(defaultCategory);
		for (AbstractTaskCategory cat : categories.values()) {
			roots.add(cat);
		}
		for (RepositoryQuery query : queries.values()) {
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

	public ITask getTask(String repositoryUrl, String taskId) {
		if (!RepositoryTaskHandleUtil.isValidTaskId(taskId)) {
			return null;
		}

		String handle = RepositoryTaskHandleUtil.getHandle(repositoryUrl, taskId);
		return getTask(handle);
	}

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

	public Set<AbstractTaskCategory> getTaskCategories() {
		Set<AbstractTaskCategory> containers = new HashSet<AbstractTaskCategory>();
		for (AbstractTaskCategory container : categories.values()) {
			if (container instanceof TaskCategory) {
				containers.add(container);
			}
		}
		return containers;
	}

	/**
	 * Returns all tasks for the given repository url.
	 */
	public Set<ITask> getTasks(String repositoryUrl) {
		Set<ITask> repositoryTasks = new HashSet<ITask>();
		if (repositoryUrl != null) {
			for (ITask task : tasks.values()) {
				if (task.getRepositoryUrl().equals(repositoryUrl)) {
					repositoryTasks.add(task);
				}
			}
		}
		return repositoryTasks;
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
	private AbstractTaskContainer getValidElement(IRepositoryElement taskListElement) {
		AbstractTaskContainer result = null;
		if (taskListElement instanceof ITask) {
			result = tasks.get(taskListElement.getHandleIdentifier());
		} else if (taskListElement instanceof UncategorizedTaskContainer) {
			result = defaultCategory;
		} else if (taskListElement instanceof UnmatchedTaskContainer) {
			result = repositoryOrphansMap.get(((UnmatchedTaskContainer) taskListElement).getRepositoryUrl());
		} else if (taskListElement instanceof TaskCategory) {
			result = categories.get(taskListElement.getHandleIdentifier());
		} else if (taskListElement instanceof IRepositoryQuery) {
			result = queries.get(taskListElement.getHandleIdentifier());
		}

		if (result == null) {
			throw new IllegalArgumentException("Element " + taskListElement.getHandleIdentifier()
					+ " does not exist in the task list.");
		} else {
			return result;
		}
	}

	public void notifyElementsChanged(Set<? extends IRepositoryElement> elements) {
		HashSet<TaskContainerDelta> deltas = new HashSet<TaskContainerDelta>();
		if (elements == null) {
			deltas.add(new TaskContainerDelta(null, TaskContainerDelta.Kind.ROOT));
		} else {
			for (IRepositoryElement element : elements) {
				deltas.add(new TaskContainerDelta(element, TaskContainerDelta.Kind.CONTENT));
			}
		}

		fireDelta(deltas);
	}

	public void notifySynchronizationStateChanged(Set<? extends IRepositoryElement> elements) {
		HashSet<TaskContainerDelta> taskChangeDeltas = new HashSet<TaskContainerDelta>();
		for (IRepositoryElement abstractTaskContainer : elements) {
			TaskContainerDelta delta = new TaskContainerDelta(abstractTaskContainer, TaskContainerDelta.Kind.CONTENT);
			delta.setTransient(true);
			taskChangeDeltas.add(delta);
		}

		fireDelta(taskChangeDeltas);
	}

	public void notifySynchronizationStateChanged(IRepositoryElement element) {
		notifySynchronizationStateChanged(Collections.singleton(element));
	}

	public void notifyElementChanged(IRepositoryElement element) {
		notifyElementsChanged(Collections.singleton(element));
	}

	public void refactorRepositoryUrl(String oldRepositoryUrl, String newRepositoryUrl) {
		Assert.isNotNull(oldRepositoryUrl);
		Assert.isNotNull(newRepositoryUrl);

		try {
			lock();
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

			for (RepositoryQuery query : queries.values()) {
				if (query.getRepositoryUrl().equals(oldRepositoryUrl)) {
					query.setRepositoryUrl(newRepositoryUrl);
					delta.add(new TaskContainerDelta(query, TaskContainerDelta.Kind.CONTENT));
				}
			}

			for (UnmatchedTaskContainer orphans : repositoryOrphansMap.values()) {
				if (orphans.getRepositoryUrl().equals(oldRepositoryUrl)) {
					repositoryOrphansMap.remove(oldRepositoryUrl);
					//categories.remove(orphans.getHandleIdentifier());
					orphans.setRepositoryUrl(newRepositoryUrl);
					repositoryOrphansMap.put(newRepositoryUrl, orphans);
					//categories.put(orphans.getHandleIdentifier(), orphans);
					delta.add(new TaskContainerDelta(orphans, TaskContainerDelta.Kind.CONTENT));
				}
			}
		} finally {
			unlock();
		}
	}

	public void removeChangeListener(ITaskListChangeListener listener) {
		changeListeners.remove(listener);
	}

	public void removeFromContainer(AbstractTaskContainer container, ITask task) {
		Assert.isNotNull(container);
		Assert.isNotNull(task);

		removeFromContainer(container, Collections.singleton(task));
	}

	public void removeFromContainer(AbstractTaskContainer container, Set<ITask> tasks) {
		Assert.isNotNull(container);
		Assert.isNotNull(tasks);
		try {
			lock();
			for (ITask task : tasks) {
				removeFromContainerInternal(container, task, delta);
				addOrphan((AbstractTask) task, delta);
			}
		} finally {
			unlock();
		}
	}

	/**
	 * Note: does not add <code>task</code> to the unmatched container.
	 */
	private void removeFromContainerInternal(AbstractTaskContainer container, ITask task, Set<TaskContainerDelta> delta) {
		assert container.getChildren().contains(task);

		container.internalRemoveChild(task);
		((AbstractTask) task).removeParentContainer(container);

		delta.add(new TaskContainerDelta(task, container, TaskContainerDelta.Kind.REMOVED));
	}

	private void removeOrphan(AbstractTask task, Set<TaskContainerDelta> delta) {
		AbstractTaskContainer orphans = getUnmatchedContainer(task.getRepositoryUrl());
		if (orphans != null) {
			if (orphans.internalRemoveChild(task)) {
				delta.add(new TaskContainerDelta(task, orphans, TaskContainerDelta.Kind.REMOVED));
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
		Assert.isLegal(!(container instanceof ITask));
		Assert.isLegal(!(container instanceof UnmatchedTaskContainer));
		try {
			lock();
			if (container instanceof TaskCategory) {
				((TaskCategory) container).setSummary(newDescription);
			} else if (container instanceof RepositoryQuery) {
				((RepositoryQuery) container).setSummary(newDescription);
			}
			delta.add(new TaskContainerDelta(container, TaskContainerDelta.Kind.CONTENT));
		} finally {
			unlock();
		}
	}

	/**
	 * Public for testing.
	 */
	public void reset() {
		try {
			lock();
			tasks = new ConcurrentHashMap<String, AbstractTask>();

			repositoryOrphansMap = new ConcurrentHashMap<String, UnmatchedTaskContainer>();

			categories = new ConcurrentHashMap<String, AbstractTaskCategory>();
			queries = new ConcurrentHashMap<String, RepositoryQuery>();

			defaultCategory = new UncategorizedTaskContainer();

			maxLocalTaskId = 0;
			categories.put(defaultCategory.getHandleIdentifier(), defaultCategory);
		} finally {
			unlock();
		}
	}

	public void run(ITaskListRunnable runnable) throws CoreException {
		run(runnable, null);
	}

	public void run(ITaskListRunnable runnable, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		try {
			lock(monitor);

			runnable.execute(monitor);

		} finally {
			unlock();
		}

	}

	private void lock() {
		lock.acquire();
		if (lock.getDepth() == 1) {
			delta = new HashSet<TaskContainerDelta>();
		}
	}

	private void lock(IProgressMonitor monitor) throws CoreException {
		while (!monitor.isCanceled()) {
			try {
				if (lock.acquire(3000)) {
					if (lock.getDepth() == 1) {
						delta = new HashSet<TaskContainerDelta>();
					}
					return;
				}
			} catch (InterruptedException e) {
				throw new OperationCanceledException();
			}
		}
		throw new OperationCanceledException();
	}

	private void unlock() {
		HashSet<TaskContainerDelta> toFire = null;
		if (lock.getDepth() == 1) {
			toFire = new HashSet<TaskContainerDelta>(delta);
		}
		lock.release();
		if (toFire != null && toFire.size() > 0) {
			fireDelta(toFire);
		}
	}

	public static ISchedulingRule getSchedulingRule() {
		return ITasksCoreConstants.TASKLIST_SCHEDULING_RULE;
	}

}
