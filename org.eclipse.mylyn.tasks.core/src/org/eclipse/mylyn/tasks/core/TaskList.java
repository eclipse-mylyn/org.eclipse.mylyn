/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskArchive;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.monitor.core.StatusHandler;

/**
 * Stores and manages task list elements and their containment hierarchy.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @since 2.0
 */
public class TaskList implements ISchedulingRule {

	private int lastLocalTaskId = 0;

	private final Set<ITaskListChangeListener> changeListeners = new CopyOnWriteArraySet<ITaskListChangeListener>();

	private Map<String, UnmatchedTaskContainer> repositoryOrphansMap;

	private Map<String, AbstractTask> tasks;

	private Map<String, AbstractTaskCategory> categories;

	private Map<String, AbstractRepositoryQuery> queries;

	private List<AbstractTask> activeTasks;

	private UncategorizedTaskContainer defaultCategory;

//	private final IJobManager jobManager = Job.getJobManager();

	private static ILock lock = Job.getJobManager().newLock();

	public TaskList() {
		reset();
	}

	/**
	 * precondition: task must not be null and must exist in the task list
	 * 
	 * @since 2.2
	 */
	private void addOrphan(AbstractTask task, Set<TaskContainerDelta> delta) {

		if (!task.getParentContainers().isEmpty()) {
			// Current policy is not to archive/orphan if the task exists in some other container
			return;
		}

		if (task instanceof LocalTask) {
			try {
				lock.acquire();
				defaultCategory.internalAddChild(task);
			} finally {
				lock.release();
			}
			task.addParentContainer(defaultCategory);
			return;
		}

		UnmatchedTaskContainer orphans;

		try {
			lock.acquire();
			orphans = repositoryOrphansMap.get(task.getRepositoryUrl());
		} finally {
			lock.release();
		}

		if (orphans != null) {
			try {
				lock.acquire();
				orphans.internalAddChild(task);
			} finally {
				lock.release();
			}
			if (delta != null) {
				delta.add(new TaskContainerDelta(orphans, TaskContainerDelta.Kind.CHANGED));
			}
			task.addParentContainer(orphans);
		} else {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Orphan container not found for: " + task.getRepositoryUrl()));
		}

		// orphan all children as appropriate
//			if (!task.isEmpty()) {
//				for (AbstractTask child : task.getChildren()) {
//					if (visitedContainers.contains(child)) {
//						continue;
//					}
//					visitedContainers.add(child);
//					addOrphan(child, delta, visitedContainers);
//				}
//			}

	}

	/**
	 * @since 2.2
	 */
	private void removeOrphan(AbstractTask task, Set<TaskContainerDelta> delta) {

		UnmatchedTaskContainer orphans;
		try {
			lock.acquire();
			orphans = repositoryOrphansMap.get(task.getRepositoryUrl());

			if (orphans != null) {
				if (orphans.contains(task.getHandleIdentifier())) {
					orphans.internalRemoveChild(task);
					if (delta != null) {
						delta.add(new TaskContainerDelta(orphans, TaskContainerDelta.Kind.CHANGED));
					}
				}
				task.removeParentContainer(orphans);
			}
		} finally {
			lock.release();
		}
	}

	/**
	 * @API-3.0 make internal
	 * @since 2.2
	 */
	public Set<UnmatchedTaskContainer> getOrphanContainers() {
		try {
			lock.acquire();
			return Collections.unmodifiableSet(new HashSet<UnmatchedTaskContainer>(repositoryOrphansMap.values()));
		} finally {
			lock.release();
		}
	}

	/**
	 * @API-3.0 make internal
	 * @since 2.2
	 */
	public UnmatchedTaskContainer getOrphanContainer(String repositoryUrl) {
		try {
			lock.acquire();
			return repositoryOrphansMap.get(repositoryUrl);
		} finally {
			lock.release();
		}
	}

	/**
	 * @API-3.0 make internal
	 * @since 2.2
	 */
	public void addOrphanContainer(UnmatchedTaskContainer orphanedTasksContainer) {
		try {
			lock.acquire();
			repositoryOrphansMap.put(orphanedTasksContainer.getRepositoryUrl(), orphanedTasksContainer);
		} finally {
			lock.release();
		}
	}

	/**
	 * @API-3.0 make internal
	 * @since 2.2
	 */
	public void removeOrphanContainer(String url) {
		if (url != null && !url.equals(LocalRepositoryConnector.REPOSITORY_URL)) {
			try {
				lock.acquire();
				repositoryOrphansMap.remove(url);
			} finally {
				lock.release();
			}
		}
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

			activeTasks = new CopyOnWriteArrayList<AbstractTask>();
			lastLocalTaskId = 0;
			categories.put(defaultCategory.getHandleIdentifier(), defaultCategory);
//		categories.put(archiveContainer.getHandleIdentifier(), archiveContainer);
		} finally {
			lock.release();
		}
	}

	/**
	 * Returns an ITask for each of the given handles
	 * 
	 * @since 2.0
	 */
	public Set<AbstractTask> getTasks(Set<String> handles) {
		HashSet<AbstractTask> result = new HashSet<AbstractTask>();
		Map<String, AbstractTask> tempTasks;
		try {
			lock.acquire();
			tempTasks = Collections.unmodifiableMap(tasks);
		} finally {
			lock.release();
		}
		for (String handle : handles) {
			AbstractTask tempTask = tempTasks.get(handle);
			if (tempTask != null) {
				result.add(tempTask);
			}
		}
		return result;
	}

	/**
	 * Add orphaned task to the task list
	 */
	public void addTask(AbstractTask task) throws IllegalArgumentException {
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
	public void addTask(AbstractTask task, AbstractTaskContainer parentContainer) throws IllegalArgumentException {
		moveTask(task, parentContainer);
		/*	task = (AbstractTask) getValidElement(task);

		if (parentContainer == null) {
			addOrphan(task, null);
			return;
		}

		parentContainer = getValidElement(parentContainer);

		// At this point, task != null and exists in task list
		// and parentContainer != null and exists in task list

		ISchedulingRule rule = task;
		if (parentContainer != null) {
			rule = MultiRule.combine(task, parentContainer);
		}
		try {
			jobManager.beginRule(rule, new NullProgressMonitor());
			//progressMonitor.beginTask(name, 100);
			try {

				if (parentContainer != null) {
					// ensure that we don't have loops
					if (task.contains(parentContainer.getHandleIdentifier())) {
						parentContainer = null;
					}
				}

				// move new task into appropriate containers...
				// precondition: task exists in task list
				if (parentContainer != null) {
					// ensure local tasks aren't duplicated in the uncategorized category when subtasks are enabled
					if (task instanceof LocalTask && parentContainer instanceof LocalTask) {

						Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
						delta.add(new TaskContainerDelta(parentContainer, TaskContainerDelta.Kind.CHANGED));

						AbstractTaskCategory category = TaskCategory.getParentTaskCategory(task);
						if (category != null) {
							task.removeParentContainer(category);
							category.internalRemoveChild(task);
							delta.add(new TaskContainerDelta(category, TaskContainerDelta.Kind.CHANGED));
						} else if (!task.getParentContainers().isEmpty()) {

							// local tasks should only have 1 parent
							for (AbstractTaskContainer parent : task.getParentContainers()) {
								if (parent != null) {
									// and again here?
									task.removeParentContainer(parent);
									parent.internalRemoveChild(task);
									delta.add(new TaskContainerDelta(parent, TaskContainerDelta.Kind.CHANGED));
								}
							}
						}
						if (!delta.isEmpty()) {
							for (ITaskListChangeListener listener : changeListeners) {
								listener.containersChanged(delta);
							}
						}
					}

					if (parentContainer instanceof AbstractTaskCategory) {
						removeFromContainer(TaskCategory.getParentTaskCategory(task), task);
					}
					removeOrphan(task, null);
					task.addParentContainer(parentContainer);
					parentContainer.internalAddChild(task);

				}

			} finally {
				//progressMonitor.done();
			}

		} finally {
			jobManager.endRule(rule);
		}*/
	}

	/**
	 * Task added if does not exist already. Ensures the element exists in the task list
	 * 
	 * @throws IllegalAgumentException
	 *             if null argument passed or element does not exist in task list
	 * @return element as passed in or instance from task list with same handle if exists
	 */
	private AbstractTaskContainer getValidElement(AbstractTaskContainer taskListElement)
			throws IllegalArgumentException {

		if (taskListElement == null) {
			throw new IllegalArgumentException("Argument can not be null");
		}

		AbstractTaskContainer result = null;

		if (taskListElement instanceof AbstractTask) {
//			boolean isNew = false;
			AbstractTask taskInTaskList;
			try {
				lock.acquire();
				// Access or modify data structure

				taskInTaskList = tasks.get(taskListElement.getHandleIdentifier());
				if (taskInTaskList == null) {
					tasks.put(taskListElement.getHandleIdentifier(), (AbstractTask) taskListElement);
					taskInTaskList = (AbstractTask) taskListElement;
//					isNew = true;
				}
				result = taskInTaskList;

			} finally {
				lock.release();
			}

//			if (isNew) {
//				// NOTE: only called for newly-created tasks 
//				// TODO: REVIEW, call in separate thread?
//				Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
//				delta.add(new TaskContainerDelta(taskListElement, TaskContainerDelta.Kind.ADDED));
////				if (parentContainer != null) {
////					delta.add(new TaskContainerDelta(parentContainer, TaskContainerDelta.Kind.CHANGED));
////				}
//				for (ITaskListChangeListener listener : changeListeners) {
//					listener.containersChanged(delta);
//				}
//			}
		}
		if (taskListElement instanceof UncategorizedTaskContainer) {
			result = defaultCategory;
		}
		if (taskListElement instanceof UnmatchedTaskContainer) {
			try {
				lock.acquire();
				UnmatchedTaskContainer container = repositoryOrphansMap.get(((UnmatchedTaskContainer) taskListElement).getRepositoryUrl());
				if (container != null) {
					result = container;
				}
			} finally {
				lock.release();
			}
		} else if (taskListElement instanceof TaskCategory) {
			try {
				lock.acquire();
				AbstractTaskCategory category = categories.get(taskListElement.getHandleIdentifier());
				if (category != null) {
					result = category;
				}
			} finally {
				lock.release();
			}
		} else if (taskListElement instanceof AbstractRepositoryQuery) {
			try {
				lock.acquire();
				AbstractRepositoryQuery query = queries.get(taskListElement.getHandleIdentifier());
				if (query != null) {
					result = query;
				}
			} finally {
				lock.release();
			}
		}

		if (result == null) {
			throw new IllegalArgumentException("Element " + taskListElement.getHandleIdentifier()
					+ " does not exist in the task list.");
		} else {
			return result;
		}

	}

	/**
	 * @since 2.2
	 */
	public void moveTask(AbstractTask task, AbstractTaskContainer container) {

		task = (AbstractTask) getValidElement(task);

		if (task instanceof LocalTask && container == null) {
			container = defaultCategory;
		} else if (container == null) {
			addOrphan(task, null);
			return;
		}

		container = getValidElement(container);

		// task != null && exists in task list
		// category exists and != null

		// ensure that we don't have loops
		if (task.contains(container.getHandleIdentifier())) {
			return;
		}

		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();

		if (task instanceof LocalTask && !task.getParentContainers().isEmpty()) {

			// local tasks should only have 1 parent
			for (AbstractTaskContainer parent : task.getParentContainers()) {
				if (parent != null) {
					task.removeParentContainer(parent);
					parent.internalRemoveChild(task);
					delta.add(new TaskContainerDelta(parent, TaskContainerDelta.Kind.CHANGED));
				}
			}
		}

		if (container instanceof AbstractTaskCategory) {
			// Tasks can only be in one task category at a time
			AbstractTaskCategory tempCat = TaskCategory.getParentTaskCategory(task);
			if (tempCat != null && !tempCat.equals(container)) {
				removeFromContainer(tempCat, task, delta);
			}
		}
		if (!(container instanceof UnmatchedTaskContainer)) {
			// If a task has a parent query/category/task it is no longer an orphan
			removeOrphan(task, delta);
		}
		task.addParentContainer(container);
		container.internalAddChild(task);

		delta.add(new TaskContainerDelta(task, TaskContainerDelta.Kind.CHANGED));
		delta.add(new TaskContainerDelta(container, TaskContainerDelta.Kind.CHANGED));
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	/**
	 * @deprecated use moveTask
	 */
	@Deprecated
	public void moveToContainer(AbstractTask task, AbstractTaskCategory container) {
		try {
			lock.acquire();
			if (!tasks.containsKey(task.getHandleIdentifier())) {
				tasks.put(task.getHandleIdentifier(), task);
			}
		} finally {
			lock.release();
		}
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		delta.add(new TaskContainerDelta(container, TaskContainerDelta.Kind.CHANGED));

		Set<AbstractTaskContainer> currentContainers = task.getParentContainers();
		for (AbstractTaskContainer taskContainer : currentContainers) {
			if (taskContainer instanceof AbstractTaskCategory) {
				if (!(taskContainer instanceof UnmatchedTaskContainer)) {
					(taskContainer).internalRemoveChild(task);
				}
//				if (!(taskContainer instanceof TaskArchive)) {
//					(taskContainer).internalRemoveChild(task);
//				}
				task.removeParentContainer(taskContainer);
				delta.add(new TaskContainerDelta(taskContainer, TaskContainerDelta.Kind.CHANGED));
			}
		}
		if (container != null) {
			internalAddTask(task, container);
			delta.add(new TaskContainerDelta(container, TaskContainerDelta.Kind.CHANGED));
			if (!(container instanceof UnmatchedTaskContainer)) {
				removeOrphan(task, delta);
			}
//			if (archiveContainer.contains(task.getHandleIdentifier())) {
//				archiveContainer.internalRemoveChild(task);
//				delta.add(new TaskContainerDelta(archiveContainer, TaskContainerDelta.Kind.CHANGED));
//			}
		} else {
			internalAddTask(task, null);
		}
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	public void refactorRepositoryUrl(String oldRepositoryUrl, String newRepositoryUrl) {
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
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	public void addCategory(TaskCategory category) throws IllegalArgumentException {
		if (category == null) {
			throw new IllegalArgumentException("Category cannot be null");
		}
		try {
			lock.acquire();
			categories.put(category.getHandleIdentifier(), category);
		} finally {
			lock.release();
		}
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		delta.add(new TaskContainerDelta(category, TaskContainerDelta.Kind.ADDED));
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	/**
	 * @deprecated use removeFromContainer(AbstractTaskContainer category, AbstractTask task)
	 */
	@Deprecated
	public void removeFromCategory(TaskCategory category, AbstractTask task) {
		removeFromContainer(category, task);
	}

	/**
	 * @deprecated removeFromContainer(AbstractTaskContainer category, AbstractTask task)
	 */
	@Deprecated
	public void removeFromQuery(AbstractRepositoryQuery query, AbstractTask task) {
		removeFromContainer(query, task);
	}

	public void removeFromContainer(AbstractTaskContainer container, AbstractTask task) {
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		removeFromContainer(container, task, delta);
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	/**
	 * @Since 3.0
	 */
	public void removeFromContainer(AbstractTaskContainer container, AbstractTask task, Set<TaskContainerDelta> delta) {
		if (container == null || task == null) {
			return;
		}
		container.internalRemoveChild(task);
		task.removeParentContainer(container);
		addOrphan(task, delta);
		delta.add(new TaskContainerDelta(task, TaskContainerDelta.Kind.CHANGED));
		delta.add(new TaskContainerDelta(container, TaskContainerDelta.Kind.CHANGED));
	}

	public void removeFromQuery(AbstractRepositoryQuery repositoryQuery, Set<AbstractTask> tasks) {
		// FIXME do a bulk remove
		for (AbstractTask task : tasks) {
			removeFromQuery(repositoryQuery, task);
		}
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public void renameTask(AbstractTask task, String description) {
		task.setSummary(description);

		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		delta.add(new TaskContainerDelta(task, TaskContainerDelta.Kind.CHANGED));

		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	/**
	 * Use renameTask(..) for renaming tasks.
	 */
	public void renameContainer(AbstractTaskContainer container, String newDescription) {
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		try {
			lock.acquire();
			if (container instanceof AbstractTask) {
				return;
			} else if (!(container instanceof TaskArchive) && !(container instanceof UnmatchedTaskContainer)) {
				if (queries.remove(container.getHandleIdentifier()) != null) {
					if (container instanceof AbstractTaskCategory) {
						((AbstractTaskCategory) container).setHandleIdentifier(newDescription);
					} else if (container instanceof AbstractRepositoryQuery) {
						((AbstractRepositoryQuery) container).setHandleIdentifier(newDescription);
						queries.put(((AbstractRepositoryQuery) container).getHandleIdentifier(),
								((AbstractRepositoryQuery) container));
					}
				} else if (container instanceof TaskCategory
						&& categories.remove(container.getHandleIdentifier()) != null) {
					((TaskCategory) container).setHandleIdentifier(newDescription);
					categories.put(((TaskCategory) container).getHandleIdentifier(), (TaskCategory) container);
				}
			}
		} finally {
			lock.release();
		}
		// TODO: make this delta policy symmetrical with tasks
		delta.add(new TaskContainerDelta(container, TaskContainerDelta.Kind.REMOVED));
		delta.add(new TaskContainerDelta(container, TaskContainerDelta.Kind.ADDED));
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	public void addQuery(AbstractRepositoryQuery query) throws IllegalArgumentException {
		if (query == null) {
			throw new IllegalArgumentException("Query cannot be null");
		}
		try {
			lock.acquire();
			queries.put(query.getHandleIdentifier(), query);
		} finally {
			lock.release();
		}
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		delta.add(new TaskContainerDelta(query, TaskContainerDelta.Kind.ADDED));
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	/**
	 * TODO: refactor around querying containers for their tasks
	 * 
	 * Task is removed from all containers: root, archive, category, and orphan bin
	 * 
	 * Currently subtasks are not deleted but rather are rather potentially orphaned
	 */
	public void deleteTask(AbstractTask task) {
		//defaultCategory.internalRemoveChild(task);
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		// Remove task from all parent containers
		for (AbstractTaskContainer container : task.getParentContainers()) {
			container.internalRemoveChild(task);
			task.removeParentContainer(container);
		}

		// Remove this task as a parent for all subtasks
		// moving to orphanage as necessary
		for (AbstractTask child : task.getChildren()) {
			child.removeParentContainer(task);
			addOrphan(child, delta);
		}
		task.clear();
		removeOrphan(task, delta);
		try {
			lock.acquire();
			tasks.remove(task.getHandleIdentifier());
		} finally {
			lock.release();
		}
		delta.add(new TaskContainerDelta(task, TaskContainerDelta.Kind.REMOVED));
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	public void deleteCategory(AbstractTaskCategory category) {
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		try {
			lock.acquire();
			categories.remove(category.getHandleIdentifier());
		} finally {
			lock.release();
		}
		for (AbstractTask task : category.getChildren()) {
			task.removeParentContainer(category);
			addOrphan(task, delta);
		}

		delta.add(new TaskContainerDelta(category, TaskContainerDelta.Kind.REMOVED));

		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	public void deleteQuery(AbstractRepositoryQuery query) {
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		try {
			lock.acquire();
			queries.remove(query.getHandleIdentifier());
		} finally {
			lock.release();
		}
		for (AbstractTask task : query.getChildren()) {
			task.removeParentContainer(query);
			addOrphan(task, delta);
		}

		delta.add(new TaskContainerDelta(query, TaskContainerDelta.Kind.REMOVED));
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	/**
	 * @param task
	 * @param complete
	 * @deprecated use AbstractTask.setCompletionDate() and notify appropriately
	 */
	@Deprecated
	public void markComplete(AbstractTask task, boolean complete) {
		task.setCompleted(complete);

		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		delta.add(new TaskContainerDelta(task, TaskContainerDelta.Kind.CHANGED));
		for (ITaskListChangeListener listener : new ArrayList<ITaskListChangeListener>(changeListeners)) {
			listener.containersChanged(delta);
		}
	}

	public void addChangeListener(ITaskListChangeListener listener) {
		changeListeners.add(listener);
	}

	public void removeChangeListener(ITaskListChangeListener listener) {
		changeListeners.remove(listener);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public void internalAddCategory(TaskCategory category) {
		try {
			lock.acquire();
			categories.put(category.getHandleIdentifier(), category);
		} finally {
			lock.release();
		}
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public void internalAddTask(AbstractTask task, AbstractTaskCategory container) {
		internalAddTask(task, (AbstractTaskContainer) container);
//		//tasks.put(task.getHandleIdentifier(), task);
//		if (container != null) {
//			tasks.put(task.getHandleIdentifier(), task);
//			container.internalAddChild(task);
//			if (container instanceof TaskCategory || container instanceof OrphanedTasksContainer) {
//				task.addParentContainer(container);
//			}
//		} else {
//			if (task.getLastReadTimeStamp() != null) {
//				// any unread task (hit) that is an orphan is discarded
//				tasks.put(task.getHandleIdentifier(), task);
//				addOrphan(task, null);
//			}
////			defaultCategory.internalAddChild(task);
////			task.addParentContainer(defaultCategory);
//		}
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	private void internalAddTask(AbstractTask task, AbstractTaskContainer container) {
		try {
			lock.acquire();
			tasks.put(task.getHandleIdentifier(), task);
		} finally {
			lock.release();
		}
		if (container != null) {
			container.internalAddChild(task);
			if (container instanceof TaskCategory || container instanceof UnmatchedTaskContainer
					|| container instanceof UncategorizedTaskContainer) {
				task.addParentContainer(container);
			}
		} else {
			//if (task.getLastReadTimeStamp() != null) {
			// any unread task (hit) that is an orphan is discarded
			addOrphan(task, null);
			//}
//			defaultCategory.internalAddChild(task);
//			task.addParentContainer(defaultCategory);
		}
	}

	/**
	 * @API-3.0 remove
	 * @deprecated
	 */
	@Deprecated
	public void internalAddRootTask(AbstractTask task) {
		internalAddTask(task, null);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public void internalAddQuery(AbstractRepositoryQuery query) {
		try {
			lock.acquire();
			queries.put(query.getHandleIdentifier(), query);
		} finally {
			lock.release();
		}
	}

	public void setActive(AbstractTask task, boolean active) {
		task.setActive(active);
		try {
			lock.acquire();
			if (active && !activeTasks.contains(task)) {
				activeTasks.add(task);
			} else if (!active) {
				activeTasks.remove(task);
			}
		} finally {
			lock.release();
		}
	}

	/**
	 * For normal user operation getActiveTask() should be used instead.
	 */
	public List<AbstractTask> getActiveTasks() {
		try {
			lock.acquire();
			return Collections.unmodifiableList(activeTasks);
		} finally {
			lock.release();
		}
	}

	/**
	 * @return First in set of all active tasks. Normal user operations only supports a single active task.
	 */
	public AbstractTask getActiveTask() {
		try {
			lock.acquire();
			if (activeTasks.size() > 0) {
				return activeTasks.get(0);
			} else {
				return null;
			}
		} finally {
			lock.release();
		}
	}

	public Set<AbstractTaskCategory> getCategories() {
		try {
			lock.acquire();
			return Collections.unmodifiableSet(new HashSet<AbstractTaskCategory>(categories.values()));
		} finally {
			lock.release();
		}
	}

	/**
	 * @deprecated
	 * @API 3.0: remove
	 */
	@Deprecated
	public List<AbstractTaskCategory> getUserCategories() {
		List<AbstractTaskCategory> included = new ArrayList<AbstractTaskCategory>();
		try {
			lock.acquire();
			for (AbstractTaskCategory category : categories.values()) {
				included.add(category);
			}
		} finally {
			lock.release();
		}
		return included;
	}

	public Set<AbstractRepositoryQuery> getQueries() {
		try {
			lock.acquire();
			return Collections.unmodifiableSet(new HashSet<AbstractRepositoryQuery>(queries.values()));
		} finally {
			lock.release();
		}
	}

	public Set<AbstractTaskContainer> getRootElements() {
		Set<AbstractTaskContainer> roots = new HashSet<AbstractTaskContainer>();
		roots.add(defaultCategory);
		try {
			lock.acquire();
			for (AbstractTaskCategory cat : categories.values()) {
				roots.add(cat);
			}
			for (AbstractRepositoryQuery query : queries.values()) {
				roots.add(query);
			}
			for (UnmatchedTaskContainer orphanContainer : repositoryOrphansMap.values()) {
				roots.add(orphanContainer);
			}
		} finally {
			lock.release();
		}
		return roots;
	}

	public Collection<AbstractTask> getAllTasks() {
		try {
			lock.acquire();
			return Collections.unmodifiableCollection(tasks.values());
		} finally {
			lock.release();
		}
	}

	public Set<AbstractTaskCategory> getTaskContainers() {
		Set<AbstractTaskCategory> containers = new HashSet<AbstractTaskCategory>();
		try {
			lock.acquire();
			for (AbstractTaskCategory container : categories.values()) {
				if (container instanceof TaskCategory || container instanceof TaskArchive) {
					containers.add(container);
				}
			}
		} finally {
			lock.release();
		}
		return containers;
	}

	/**
	 * API 3.0: remove
	 */
	public AbstractRepositoryQuery getQueryForHandle(String handle) {
		if (handle == null) {
			return null;
		}
		try {
			lock.acquire();
			for (AbstractRepositoryQuery query : queries.values()) {
				if (query.contains(handle)) {
					return query;
				}
			}
		} finally {
			lock.release();
		}
		return null;
	}

	public boolean isEmpty() {
		boolean isEmpty = getCategories().size() == 1 && getCategories().contains(defaultCategory);
		return getAllTasks().size() == 0 && isEmpty && getQueries().size() == 0;
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
			try {
				lock.acquire();
				return tasks.get(handleIdentifier);
			} finally {
				lock.release();
			}
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
		AbstractTask task = getTask(handle);
		return task;
	}

	/**
	 * Searches for a task whose URL matches
	 * 
	 * @return first task with a matching URL.
	 * @since 2.0
	 */
	public AbstractTask getRepositoryTask(String taskUrl) {
		try {
			lock.acquire();
			for (AbstractTask currTask : tasks.values()) {
				String currUrl = currTask.getUrl();
				if (currUrl != null && !currUrl.equals("") && currUrl.equals(taskUrl)) {
					return currTask;
				}
			}
		} finally {
			lock.release();
		}
		return null;
	}

	/**
	 * Searches for a task whose key matches.
	 * 
	 * @return first task with a key, null if no matching task is found
	 * @since 2.0
	 */
	public AbstractTask getTaskByKey(String repositoryUrl, String taskKey) {
		try {
			lock.acquire();
			for (AbstractTask task : tasks.values()) {
				String currentTaskKey = task.getTaskKey();
				if (currentTaskKey != null && currentTaskKey.equals(taskKey)
						&& task.getRepositoryUrl().equals(repositoryUrl)) {
					return task;
				}
			}
		} finally {
			lock.release();
		}
		return null;
	}

	public AbstractTaskCategory getContainerForHandle(String categoryHandle) {
		try {
			lock.acquire();
			for (AbstractTaskCategory cat : categories.values()) {
				if (cat != null) {
					if (cat.getHandleIdentifier().equals(categoryHandle)) {
						return cat;
					}
				}
			}
		} finally {
			lock.release();
		}
		return null;
	}

	public AbstractTaskCategory getDefaultCategory() {
		return defaultCategory;
	}

	/**
	 * @since 2.3
	 * @API 3.0 move to AbstractRepositoryQuery and make static?
	 */
	public Set<AbstractRepositoryQuery> getParentQueries(AbstractTask task) {
		Set<AbstractRepositoryQuery> parentQueries = new HashSet<AbstractRepositoryQuery>();
		for (AbstractTaskContainer container : task.getParentContainers()) {
			if (container instanceof AbstractRepositoryQuery) {
				parentQueries.add((AbstractRepositoryQuery) container);
			}
		}
		return parentQueries;
	}

	/**
	 * if handle == null or no queries found an empty set is returned
	 * 
	 * @API 3.0: remove
	 */
	public Set<AbstractRepositoryQuery> getQueriesForHandle(String handle) {
		if (handle == null) {
			return Collections.emptySet();
		}
		Set<AbstractRepositoryQuery> queriesForHandle = new HashSet<AbstractRepositoryQuery>();
		AbstractTask tempTask;
		try {
			lock.acquire();
			tempTask = tasks.get(handle);
		} finally {
			lock.release();
		}
		if (tempTask != null) {
			queriesForHandle = getParentQueries(tempTask);
		}

		return queriesForHandle;
	}

	/**
	 * return all queries for the given repository url
	 */
	public Set<AbstractRepositoryQuery> getRepositoryQueries(String repositoryUrl) {
		Set<AbstractRepositoryQuery> repositoryQueries = new HashSet<AbstractRepositoryQuery>();
		if (repositoryUrl != null) {
			try {
				lock.acquire();
				for (AbstractRepositoryQuery query : queries.values()) {
					if (query.getRepositoryUrl().equals(repositoryUrl)) {
						repositoryQueries.add(query);
					}
				}
			} finally {
				lock.release();
			}
		}
		return repositoryQueries;
	}

	/**
	 * return all tasks for the given repository url
	 * 
	 * API-3.0: add a parameter for the kind
	 */
	public Set<AbstractTask> getRepositoryTasks(String repositoryUrl) {
		Set<AbstractTask> repositoryTasks = new HashSet<AbstractTask>();
		if (repositoryUrl != null) {
			try {
				lock.acquire();
				for (AbstractTask task : tasks.values()) {
					if (task.getRepositoryUrl().equals(repositoryUrl)) {
						repositoryTasks.add(task);
					}
				}
			} finally {
				lock.release();
			}
		}
		return repositoryTasks;
	}

	/**
	 * Exposed for unit testing
	 * 
	 * @return unmodifiable collection of ITaskActivityListeners
	 */
	public Set<ITaskListChangeListener> getChangeListeners() {
		try {
			lock.acquire();
			return Collections.unmodifiableSet(changeListeners);
		} finally {
			lock.release();
		}

	}

	/**
	 * @param task
	 * @param content
	 *            true if the content for the task (e.g. repository task data) has changed
	 */
	public void notifyTaskChanged(AbstractTask task, boolean content) {
		for (ITaskListChangeListener listener : new ArrayList<ITaskListChangeListener>(changeListeners)) {
			try {
				Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
				TaskContainerDelta.Kind kind;
				if (content) {
					kind = TaskContainerDelta.Kind.CONTENT;
				} else {
					kind = TaskContainerDelta.Kind.CHANGED;
				}
				delta.add(new TaskContainerDelta(task, kind));
				listener.containersChanged(delta);
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Notification failed for: "
						+ listener, t));
			}
		}
	}

	public void notifyContainersUpdated(Set<? extends AbstractTaskContainer> containers) {
		Set<TaskContainerDelta> delta;
		if (containers == null) {
			delta = new HashSet<TaskContainerDelta>();
			delta.add(new TaskContainerDelta(null, TaskContainerDelta.Kind.ROOT));
		} else {
			delta = new HashSet<TaskContainerDelta>();
			for (AbstractTaskContainer abstractTaskContainer : containers) {
				delta.add(new TaskContainerDelta(abstractTaskContainer, TaskContainerDelta.Kind.CHANGED));
			}
		}
		for (ITaskListChangeListener listener : new ArrayList<ITaskListChangeListener>(changeListeners)) {
			try {
				listener.containersChanged(delta);
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Notification failed for: "
						+ listener, t));
			}
		}
	}

	public int getNextLocalTaskId() {
		try {
			lock.acquire();
			return ++lastLocalTaskId;
		} finally {
			lock.release();
		}
	}

	public void setLastLocalTaskId(int lastTaskNum) {
		try {
			lock.acquire();
			this.lastLocalTaskId = lastTaskNum;
		} finally {
			lock.release();
		}
	}

	/** For tasklist persistence. Use getNextTaskNum for task construction */
	public int getLastLocalTaskId() {
		try {
			lock.acquire();
			return lastLocalTaskId;
		} finally {
			lock.release();
		}
	}

	/** Note: use getNextTaskNum for new task construction */
	public int findLargestTaskId() {
		int max = 0;
		try {
			lock.acquire();
			max = Math.max(largestTaskIdHelper(tasks.values(), 0, 0), max);
		} finally {
			lock.release();
		}
		for (AbstractTaskCategory cat : getTaskContainers()) {
			max = Math.max(largestTaskIdHelper(cat.getChildren(), 0, 0), max);
		}
		return max;
	}

	/**
	 * Maxes out at 20 recursions for safety.
	 */
	private int largestTaskIdHelper(Collection<AbstractTask> tasks, int lastMax, int depth) {
		if (depth >= 20) {
			return lastMax;
		} else {
			depth++;
			int ihandle = 0;
			int max = 0;
			for (AbstractTask task : tasks) {
				if (task instanceof LocalTask) {
					String string = task.getHandleIdentifier().substring(
							task.getHandleIdentifier().lastIndexOf('-') + 1, task.getHandleIdentifier().length());
					try {
						ihandle = Integer.parseInt(string);
					} catch (NumberFormatException nfe) {
						// ignore
					}
					max = Math.max(ihandle, max);
					ihandle = largestTaskIdHelper(task.getChildren(), max, depth);
					max = Math.max(ihandle, max);
				}
			}
			return max;
		}
	}

	/**
	 * 
	 * @API-3.0 review/deprecate, use addTask directly?
	 * 
	 * @since 2.
	 * @deprecated
	 */
	@Deprecated
	public final void insertTask(AbstractTask task, AbstractTaskCategory legacyCategory, AbstractTask parent) {
		if (task.getCategoryHandle().length() > 0) {
			AbstractTaskCategory category = this.getContainerForHandle(task.getCategoryHandle());

			if (category != null) {
				this.internalAddTask(task, category);
			} else if (parent == null) {
				this.internalAddRootTask(task);
			}
		} else if (legacyCategory != null && !(legacyCategory instanceof TaskArchive)
				&& getCategories().contains(legacyCategory)) {
			task.addParentContainer(legacyCategory);
			legacyCategory.internalAddChild(task);
		} else {
			this.internalAddTask(task, null);
		}

		this.setActive(task, task.isActive());
	}

	public boolean contains(ISchedulingRule rule) {
		return isConflicting(rule);
	}

	public boolean isConflicting(ISchedulingRule rule) {
		return rule instanceof TaskList || rule instanceof AbstractTaskContainer;
	}

}
