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

import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskArchive;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.UnfiledCategory;
import org.eclipse.mylyn.monitor.core.StatusHandler;

/**
 * Stores and manages task list elements and their containment hierarchy.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public class TaskList {

	private int lastLocalTaskId = 0;

	private Set<ITaskListChangeListener> changeListeners = new CopyOnWriteArraySet<ITaskListChangeListener>();

	private Map<String, AbstractTask> tasks;

	private Map<String, AbstractTaskCategory> categories;

	private Map<String, AbstractRepositoryQuery> queries;

	private TaskArchive archiveContainer;

	private UnfiledCategory defaultCategory;

	private List<AbstractTask> activeTasks;

	public TaskList() {
		reset();
	}

	/**
	 * Public for testing.
	 */
	public void reset() {
		tasks = new ConcurrentHashMap<String, AbstractTask>();

		categories = new ConcurrentHashMap<String, AbstractTaskCategory>();
		queries = new ConcurrentHashMap<String, AbstractRepositoryQuery>();

		archiveContainer = new TaskArchive();
		defaultCategory = new UnfiledCategory();

		activeTasks = new CopyOnWriteArrayList<AbstractTask>();
		lastLocalTaskId = 0;
		categories.put(defaultCategory.getHandleIdentifier(), defaultCategory);
		categories.put(archiveContainer.getHandleIdentifier(), archiveContainer);
	}

	public void addTask(AbstractTask task) throws IllegalArgumentException {
		addTask(task, archiveContainer);
	}

	/**
	 * Returns an ITask for each of the given handles
	 * 
	 * @since 2.0
	 */
	public Set<AbstractTask> getTasks(Set<String> handles) {
		HashSet<AbstractTask> result = new HashSet<AbstractTask>();
		Map<String, AbstractTask> tempTasks = Collections.unmodifiableMap(tasks);
		for (String handle : handles) {
			AbstractTask tempTask = tempTasks.get(handle);
			if (tempTask != null) {
				result.add(tempTask);
			}
		}
		return result;
	}

	/**
	 * Precondition: {@code container} already exists in tasklist (be it a parent task, category, or query)
	 * 
	 * @param task
	 *            to be added (hit, subtask, etc)
	 * @param container
	 *            task container, query or parent task
	 */
	public void addTask(AbstractTask task, AbstractTaskContainer parentContainer) throws IllegalArgumentException {
		if (task == null) {
			throw new IllegalArgumentException("Task cannot be null");
		}

		AbstractTask newTask = tasks.get(task.getHandleIdentifier());

		if (newTask == null) {
			newTask = task;
			tasks.put(newTask.getHandleIdentifier(), newTask);
//			archiveContainer.addChild(newTask);
//			newTask.addParentContainer(archiveContainer);

			// NOTE: only called for newly-created tasks
			Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
			delta.add(new TaskContainerDelta(newTask, TaskContainerDelta.Kind.ADDED));
			if (parentContainer != null) {
				delta.add(new TaskContainerDelta(parentContainer, TaskContainerDelta.Kind.CHANGED));
			}
			for (ITaskListChangeListener listener : changeListeners) {
				listener.containersChanged(delta);
			}
		}

		if (parentContainer != null) {
			parentContainer.internalAddChild(newTask);
			if (!(parentContainer instanceof AbstractTask) && !(parentContainer instanceof AbstractRepositoryQuery)) {
				newTask.addParentContainer(parentContainer);
			}
		} else {
			defaultCategory.internalAddChild(newTask);
			newTask.addParentContainer(defaultCategory);
		}
	}

	public void moveToContainer(AbstractTask task, AbstractTaskCategory container) {
		if (!tasks.containsKey(task.getHandleIdentifier())) {
			tasks.put(task.getHandleIdentifier(), task);
		}
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		delta.add(new TaskContainerDelta(container, TaskContainerDelta.Kind.CHANGED));

		Set<AbstractTaskContainer> currentContainers = task.getParentContainers();
		for (AbstractTaskContainer taskContainer : currentContainers) {
			if (taskContainer instanceof AbstractTaskCategory) {
				if (!(taskContainer instanceof TaskArchive)) {
					(taskContainer).internalRemoveChild(task);
				}
				task.removeParentContainer(taskContainer);
				delta.add(new TaskContainerDelta(taskContainer, TaskContainerDelta.Kind.CHANGED));
			}
		}
		if (container != null) {
			internalAddTask(task, container);
			delta.add(new TaskContainerDelta(container, TaskContainerDelta.Kind.CHANGED));
			if (archiveContainer.contains(task.getHandleIdentifier())) {
				archiveContainer.internalRemoveChild(task);
				delta.add(new TaskContainerDelta(archiveContainer, TaskContainerDelta.Kind.CHANGED));
			}
		} else {
			internalAddTask(task, archiveContainer);
		}
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	public void refactorRepositoryUrl(String oldRepositoryUrl, String newRepositoryUrl) {
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

		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		for (AbstractRepositoryQuery query : queries.values()) {
			if (query.getRepositoryUrl().equals(oldRepositoryUrl)) {
				query.setRepositoryUrl(newRepositoryUrl);
				delta.add(new TaskContainerDelta(query, TaskContainerDelta.Kind.CHANGED));
			}
		}
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	public void addCategory(TaskCategory category) throws IllegalArgumentException {
		if (category == null) {
			throw new IllegalArgumentException("Category cannot be null");
		}

		categories.put(category.getHandleIdentifier(), category);

		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		delta.add(new TaskContainerDelta(category, TaskContainerDelta.Kind.ADDED));
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	public void removeFromCategory(TaskCategory category, AbstractTask task) {
		moveToContainer(task, archiveContainer);
	}

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
		if (container instanceof AbstractTask) {
			return;
		} else if (!(container instanceof TaskArchive) && !(container instanceof UnfiledCategory)) {
			if (queries.remove(container.getHandleIdentifier()) != null) {
				if (container instanceof AbstractTaskCategory) {
					((AbstractTaskCategory) container).setHandleIdentifier(newDescription);
				} else if (container instanceof AbstractRepositoryQuery) {
					((AbstractRepositoryQuery) container).setHandleIdentifier(newDescription);
					this.addQuery((AbstractRepositoryQuery) container);
				}
			} else if (container instanceof TaskCategory && categories.remove(container.getHandleIdentifier()) != null) {
				((TaskCategory) container).setHandleIdentifier(newDescription);
				this.addCategory((TaskCategory) container);
			}
		}
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		delta.add(new TaskContainerDelta(container, TaskContainerDelta.Kind.CHANGED));
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	public void addQuery(AbstractRepositoryQuery query) throws IllegalArgumentException {
		if (query == null) {
			throw new IllegalArgumentException("Query cannot be null");
		}
		queries.put(query.getHandleIdentifier(), query);
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		delta.add(new TaskContainerDelta(query, TaskContainerDelta.Kind.ADDED));
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	/**
	 * TODO: refactor around querying containers for their tasks
	 * 
	 * Task is removed from all containers: root, archive, category, and tasks catchall (Currently no support for
	 * deletion of subtasks)
	 */
	public void deleteTask(AbstractTask task) {
		archiveContainer.internalRemoveChild(task);
		defaultCategory.internalRemoveChild(task);

		for (AbstractTaskContainer container : task.getParentContainers()) {
			container.internalRemoveChild(task);
			task.removeParentContainer(container);
		}
		tasks.remove(task.getHandleIdentifier());

		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		delta.add(new TaskContainerDelta(task, TaskContainerDelta.Kind.REMOVED));
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	public void deleteCategory(AbstractTaskCategory category) {
		for (AbstractTask task : category.getChildren()) {
			defaultCategory.internalAddChild(task);
		}
		categories.remove(category.getHandleIdentifier());

		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		delta.add(new TaskContainerDelta(defaultCategory, TaskContainerDelta.Kind.CHANGED));
		delta.add(new TaskContainerDelta(category, TaskContainerDelta.Kind.REMOVED));

		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	public void deleteQuery(AbstractRepositoryQuery query) {
		queries.remove(query.getHandleIdentifier());

		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		delta.add(new TaskContainerDelta(query, TaskContainerDelta.Kind.REMOVED));
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

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
	 * NOTE: Only public so that other externalizers can use it
	 */
	public void internalAddCategory(TaskCategory category) {
		categories.put(category.getHandleIdentifier(), category);
	}

	public void internalAddTask(AbstractTask task, AbstractTaskCategory container) {
		tasks.put(task.getHandleIdentifier(), task);
		if (container != null) {
			container.internalAddChild(task);
			if (container instanceof TaskCategory || container instanceof UnfiledCategory) {
				task.addParentContainer(container);
			}
		} else {
			defaultCategory.internalAddChild(task);
			task.addParentContainer(defaultCategory);
		}
	}

	public void internalAddRootTask(AbstractTask task) {
		internalAddTask(task, defaultCategory);
	}

	public void internalAddQuery(AbstractRepositoryQuery query) {
		queries.put(query.getHandleIdentifier(), query);
	}

	public void setActive(AbstractTask task, boolean active) {
		task.setActive(active);
		if (active && !activeTasks.contains(task)) {
			activeTasks.add(task);
		} else if (!active) {
			activeTasks.remove(task);
		}
	}

	/**
	 * For normal user operation getActiveTask() should be used instead.
	 */
	public List<AbstractTask> getActiveTasks() {
		return activeTasks;
	}

	/**
	 * @return First in set of all active tasks. Normal user operations only supports a single active task.
	 */
	public AbstractTask getActiveTask() {
		if (activeTasks.size() > 0) {
			return activeTasks.get(0);
		} else {
			return null;
		}
	}

	public Set<AbstractTaskCategory> getCategories() {
		return Collections.unmodifiableSet(new HashSet<AbstractTaskCategory>(categories.values()));
	}

	public List<AbstractTaskCategory> getUserCategories() {
		List<AbstractTaskCategory> included = new ArrayList<AbstractTaskCategory>();
		for (AbstractTaskCategory category : categories.values()) {
			if (!(category instanceof TaskArchive)) {
				included.add(category);
			}
		}
		return included;
	}

	public Set<AbstractRepositoryQuery> getQueries() {
		// TODO: remove wrapping once API can change
		return Collections.unmodifiableSet(new HashSet<AbstractRepositoryQuery>(queries.values()));
	}

	public Set<AbstractTaskContainer> getRootElements() {
		Set<AbstractTaskContainer> roots = new HashSet<AbstractTaskContainer>();
		roots.add(defaultCategory);
		for (AbstractTaskCategory cat : categories.values())
			roots.add(cat);
		for (AbstractRepositoryQuery query : queries.values())
			roots.add(query);
		return roots;
	}

	public Collection<AbstractTask> getAllTasks() {
		return Collections.unmodifiableCollection(tasks.values());
	}

	public Set<AbstractTaskCategory> getTaskContainers() {
		Set<AbstractTaskCategory> containers = new HashSet<AbstractTaskCategory>();
		for (AbstractTaskCategory container : categories.values()) {
			if (container instanceof TaskCategory || container instanceof TaskArchive) {
				containers.add(container);
			}
		}
		return containers;
	}

	public AbstractRepositoryQuery getQueryForHandle(String handle) {
		if (handle == null) {
			return null;
		}
		for (AbstractRepositoryQuery query : queries.values()) {
			if (query.contains(handle)) {
				return query;
			}
		}
		return null;
	}

	public boolean isEmpty() {
		boolean archiveIsEmpty = getCategories().size() == 2
				&& getCategories().iterator().next().equals(archiveContainer)
				&& archiveContainer.getChildren().isEmpty();
		return getAllTasks().size() == 0 && archiveIsEmpty && getQueries().size() == 0;
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
		for (AbstractTask currTask : tasks.values()) {
			String currUrl = currTask.getUrl();
			if (currUrl != null && !currUrl.equals("") && currUrl.equals(taskUrl)) {
				return currTask;
			}
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
		for (AbstractTask task : tasks.values()) {
			String currentTaskKey = task.getTaskKey();
			if (currentTaskKey != null && currentTaskKey.equals(taskKey)
					&& task.getRepositoryUrl().equals(repositoryUrl)) {
				return task;
			}
		}
		return null;
	}

	public AbstractTaskCategory getContainerForHandle(String categoryHandle) {
		for (AbstractTaskCategory cat : categories.values()) {
			if (cat != null) {
				if (cat.getHandleIdentifier().equals(categoryHandle)) {
					return cat;
				}
			}
		}
		return null;
	}

	public AbstractTaskCategory getDefaultCategory() {
		return defaultCategory;
	}

	public TaskArchive getArchiveContainer() {
		return archiveContainer;
	}

	/** if handle == null or no queries found an empty set is returned * */
	public Set<AbstractRepositoryQuery> getQueriesForHandle(String handle) {
		if (handle == null) {
			return Collections.emptySet();
		}
		Set<AbstractRepositoryQuery> queriesForHandle = new HashSet<AbstractRepositoryQuery>();
		for (AbstractRepositoryQuery query : queries.values()) {
			if (query.contains(handle)) {
				queriesForHandle.add(query);
			}
		}
		return queriesForHandle;
	}

// /** if handle == null or no query hits found an empty set is returned * */
// public Set<AbstractQueryHit> getQueryHits(Set<String> handles) {
// if (handles == null) {
// return Collections.emptySet();
// }
// HashSet<AbstractQueryHit> result = new HashSet<AbstractQueryHit>();
// for (String handle : handles) {
// AbstractQueryHit hit = queryHits.get(handle);
// if (hit != null) {
// result.add(hit);
// }
// }
// return result;
// }
//
// public AbstractQueryHit getQueryHit(String handle) {
// if (handle != null) {
// return queryHits.get(handle);
// }
// return null;
// }

// /** for testing */
// public Set<AbstractQueryHit> getQueryHits() {
// // TODO: remove wrapping once API can change
// return new HashSet<AbstractQueryHit>(queryHits.values());
// }
//
// /** called by AbstractRepositoryQuery */
// public void addQueryHit(AbstractQueryHit hit) {
// queryHits.put(hit.getHandleIdentifier(), hit);
// }

	/**
	 * return all queries for the given repository url
	 */
	public Set<AbstractRepositoryQuery> getRepositoryQueries(String repositoryUrl) {
		Set<AbstractRepositoryQuery> repositoryQueries = new HashSet<AbstractRepositoryQuery>();
		if (repositoryUrl != null) {
			for (AbstractRepositoryQuery query : queries.values()) {
				if (query.getRepositoryUrl().equals(repositoryUrl)) {
					repositoryQueries.add(query);
				}
			}
		}
		return repositoryQueries;
	}

	/**
	 * return all tasks for the given repository url
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

	/**
	 * Exposed for unit testing
	 * 
	 * @return unmodifiable collection of ITaskActivityListeners
	 */
	public Set<ITaskListChangeListener> getChangeListeners() {
		return Collections.unmodifiableSet(changeListeners);
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
				StatusHandler.fail(t, "Notification failed for: " + listener, false);
			}
		}
	}

	public void notifyContainersUpdated(Set<? extends AbstractTaskContainer> containers) {
		if (containers == null) {
			Set<TaskContainerDelta> rootDelta = new HashSet<TaskContainerDelta>();
			rootDelta.add(new TaskContainerDelta(null, TaskContainerDelta.Kind.ROOT));
		} else {
			Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
			for (AbstractTaskContainer abstractTaskContainer : containers) {
				delta.add(new TaskContainerDelta(abstractTaskContainer, TaskContainerDelta.Kind.CHANGED));
			}
			for (ITaskListChangeListener listener : new ArrayList<ITaskListChangeListener>(changeListeners)) {
				try {
					listener.containersChanged(delta);
				} catch (Throwable t) {
					StatusHandler.fail(t, "notification failed for: " + listener, false);
				}
			}
		}
	}

	public int getNextLocalTaskId() {
		return ++lastLocalTaskId;
	}

	public void setLastLocalTaskId(int lastTaskNum) {
		this.lastLocalTaskId = lastTaskNum;
	}

	/** For tasklist persistence. Use getNextTaskNum for task construction */
	public int getLastLocalTaskId() {
		return lastLocalTaskId;
	}

	/** Note: use getNextTaskNum for new task construction */
	public int findLargestTaskId() {
		int max = 0;
		max = Math.max(largestTaskIdHelper(tasks.values()), max);
		for (AbstractTaskCategory cat : getTaskContainers()) {
			max = Math.max(largestTaskIdHelper(cat.getChildren()), max);
		}
		return max;
	}

	private int largestTaskIdHelper(Collection<AbstractTask> tasks) {
		int ihandle = 0;
		int max = 0;
		for (AbstractTask task : tasks) {
			if (task instanceof LocalTask) {
				String string = task.getHandleIdentifier().substring(task.getHandleIdentifier().lastIndexOf('-') + 1,
						task.getHandleIdentifier().length());
				try {
					ihandle = Integer.parseInt(string);
				} catch (NumberFormatException nfe) {
				}
			}
			max = Math.max(ihandle, max);
			ihandle = largestTaskIdHelper(task.getChildren());
			max = Math.max(ihandle, max);
		}
		return max;
	}

// /**
// * Orphaned hits arise when no query in the tasklist references a hit in the
// * master list maintained by the tasklist. Orphaned hits don't span
// * workbench re-start but this just helps maintain the list in case of
// * prolonged workbench uptime.
// */
// public void removeOrphanedHits() {
// for (String handle : new HashSet<String>(queryHits.keySet())) {
// Set<AbstractRepositoryQuery> queries = getQueriesForHandle(handle);
// if (queries == null || queries.isEmpty()) {
// queryHits.remove(handle);
// }
// }
// }
}
