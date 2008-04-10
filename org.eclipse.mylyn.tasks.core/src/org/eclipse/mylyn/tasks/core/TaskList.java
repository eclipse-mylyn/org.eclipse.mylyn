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
 * @since 2.0
 */
public class TaskList {

	private int lastLocalTaskId = 0;

	private final Set<ITaskListChangeListener> changeListeners = new CopyOnWriteArraySet<ITaskListChangeListener>();

	private Map<String, UnmatchedTaskContainer> repositoryOrphansMap;

	private Map<String, AbstractTask> tasks;

	private Map<String, AbstractTaskCategory> categories;

	private Map<String, AbstractRepositoryQuery> queries;

	private List<AbstractTask> activeTasks;

	private TaskArchive archiveContainer;

	private UncategorizedTaskContainer defaultCategory;

	public TaskList() {
		reset();
	}

	/**
	 * @since 2.2
	 */
	private void addOrphan(AbstractTask task, Set<TaskContainerDelta> delta) {
		addOrphan(task, delta, new HashSet<AbstractTaskContainer>());
	}

	/**
	 * @since 2.2
	 */
	private void addOrphan(AbstractTask task, Set<TaskContainerDelta> delta,
			Set<AbstractTaskContainer> visitedContainers) {
		if (!task.getParentContainers().isEmpty()) {
			// Current policy is not to archive/orphan if the task exists in some other container
			return;
		}

		if (task instanceof LocalTask) {
			moveTask(task, defaultCategory);
			return;
		}

		UnmatchedTaskContainer orphans = repositoryOrphansMap.get(task.getRepositoryUrl());
		//		if (orphans == null) {
		//			orphans = new OrphanedTasksContainer(task.getConnectorKind(), task.getRepositoryUrl());
		//			repositoryOrphansMap.put(task.getRepositoryUrl(), orphans);
		//			//categories.put(orphans.getHandleIdentifier(), orphans);
		//		}

		if (orphans != null) {
			//if (!orphans.contains(task.getHandleIdentifier())) {
			orphans.internalAddChild(task);
			if (delta != null) {
				delta.add(new TaskContainerDelta(orphans, TaskContainerDelta.Kind.CHANGED));
			}
			//}
			task.addParentContainer(orphans);
		} else {
			//StatusHandler.log("Orphan container not found for: " + task.getRepositoryUrl(), this);
		}

		if (!task.isEmpty()) {
			for (AbstractTask child : task.getChildren()) {
				if (visitedContainers.contains(child)) {
					continue;
				}
				visitedContainers.add(child);
				addOrphan(child, delta, visitedContainers);
			}
		}

	}

	/**
	 * @since 2.2
	 */
	private void removeOrphan(AbstractTask task, Set<TaskContainerDelta> delta) {
		removeOrphan(task, delta, new HashSet<AbstractTaskContainer>());
	}

	/**
	 * @since 2.2
	 */
	private void removeOrphan(AbstractTask task, Set<TaskContainerDelta> delta, Set<AbstractTaskContainer> visitedTasks) {
		UnmatchedTaskContainer orphans = repositoryOrphansMap.get(task.getRepositoryUrl());
		if (orphans != null) {
			if (orphans.contains(task.getHandleIdentifier())) {
				orphans.internalRemoveChild(task);
				if (delta != null) {
					delta.add(new TaskContainerDelta(orphans, TaskContainerDelta.Kind.CHANGED));
				}

				if (!task.isEmpty()) {
					for (AbstractTask child : task.getChildren()) {
						if (visitedTasks.contains(child)) {
							continue;
						}
						visitedTasks.add(child);
						removeOrphan(child, delta, visitedTasks);
					}
				}

			}

//			if (orphans.isEmpty() && !orphans.getRepositoryUrl().equals(LocalRepositoryConnector.REPOSITORY_URL)) {
//				repositoryOrphansMap.remove(task.getRepositoryUrl());
//				if (delta != null) {
//					delta.add(new TaskContainerDelta(orphans, TaskContainerDelta.Kind.CHANGED));
//				}
//			}
			task.removeParentContainer(orphans);
		}
	}

	/**
	 * @API-3.0 make internal
	 * @since 2.2
	 */
	public Set<UnmatchedTaskContainer> getOrphanContainers() {
		return Collections.unmodifiableSet(new HashSet<UnmatchedTaskContainer>(repositoryOrphansMap.values()));
	}

	/**
	 * @API-3.0 make internal
	 * @since 2.2
	 */
	public UnmatchedTaskContainer getOrphanContainer(String repositoryUrl) {
		return repositoryOrphansMap.get(repositoryUrl);
	}

	/**
	 * @API-3.0 make internal
	 * @since 2.2
	 */
	public void addOrphanContainer(UnmatchedTaskContainer orphanedTasksContainer) {
		repositoryOrphansMap.put(orphanedTasksContainer.getRepositoryUrl(), orphanedTasksContainer);
	}

	/**
	 * @API-3.0 make internal
	 * @since 2.2
	 */
	public void removeOrphanContainer(String url) {
		if (url != null && !url.equals(LocalRepositoryConnector.REPOSITORY_URL)) {
			repositoryOrphansMap.remove(url);
		}
	}

	/**
	 * Public for testing.
	 */
	public void reset() {
		tasks = new ConcurrentHashMap<String, AbstractTask>();

		repositoryOrphansMap = new ConcurrentHashMap<String, UnmatchedTaskContainer>();

		categories = new ConcurrentHashMap<String, AbstractTaskCategory>();
		queries = new ConcurrentHashMap<String, AbstractRepositoryQuery>();

		archiveContainer = new TaskArchive(this);
		defaultCategory = new UncategorizedTaskContainer();

		activeTasks = new CopyOnWriteArrayList<AbstractTask>();
		lastLocalTaskId = 0;
		categories.put(defaultCategory.getHandleIdentifier(), defaultCategory);
//		categories.put(archiveContainer.getHandleIdentifier(), archiveContainer);
	}

	/**
	 * Add orphaned task to the task list
	 */
	public void addTask(AbstractTask task) throws IllegalArgumentException {
		addTask(task, null);
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
	 * Precondition: {@code container} already exists in tasklist (be it a parent task, category, or query) If the
	 * parentContainer is null the task is considered an orphan and added to the appropriate repository's orphaned tasks
	 * container.
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
			// ensure that we don't have loops
			if (task.contains(parentContainer.getHandleIdentifier())) {
				parentContainer = null;
			}
		}

		if (parentContainer != null) {
			// ensure local tasks aren't duplicated in the uncategorized category when subtasks are enabled
			if (task instanceof LocalTask && parentContainer instanceof LocalTask) {
				if (!tasks.containsKey(task.getHandleIdentifier())) {
					tasks.put(task.getHandleIdentifier(), task);
				}

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

			if (parentContainer instanceof AbstractTask) {
				// Ensure the parent task exists in the task list
				tasks.put(parentContainer.getHandleIdentifier(), (AbstractTask) parentContainer);
				addOrphan((AbstractTask) parentContainer, null);
			}

			if (parentContainer instanceof AbstractTaskCategory) {
				removeFromCategory(TaskCategory.getParentTaskCategory(newTask), newTask);
			}
			removeOrphan(newTask, null);
			newTask.addParentContainer(parentContainer);
			parentContainer.internalAddChild(newTask);

//			if (!(parentContainer instanceof AbstractTask) && !(parentContainer instanceof AbstractRepositoryQuery)) {
//				newTask.addParentContainer(parentContainer);
//			}
		} else {
			// TODO: if the task has a parent task which isn't orphaned than the current task isn't orphaned
			// check map of children to parents first
			addOrphan(task, null);
		}
	}

	/**
	 * @since 2.2
	 */
	public void moveTask(AbstractTask task, AbstractTaskContainer container) {
		if (!tasks.containsKey(task.getHandleIdentifier())) {
			tasks.put(task.getHandleIdentifier(), task);
		}

		// ensure that we don't have loops
		if (container != null && task.contains(container.getHandleIdentifier())) {
			return;
		}

		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		delta.add(new TaskContainerDelta(container, TaskContainerDelta.Kind.CHANGED));

		AbstractTaskCategory category = TaskCategory.getParentTaskCategory(task);
		if (category != null) {
			task.removeParentContainer(category);
			category.internalRemoveChild(task);
			delta.add(new TaskContainerDelta(category, TaskContainerDelta.Kind.CHANGED));
		} else if (task instanceof LocalTask && !task.getParentContainers().isEmpty()) {

			// local tasks should only have 1 parent
			for (AbstractTaskContainer parent : task.getParentContainers()) {
				if (parent != null) {
					task.removeParentContainer(parent);
					parent.internalRemoveChild(task);
					delta.add(new TaskContainerDelta(parent, TaskContainerDelta.Kind.CHANGED));
				}
			}
		}

		if (container != null) {
			addTask(task, container);
			//internalAddTask(task, container);
			delta.add(new TaskContainerDelta(container, TaskContainerDelta.Kind.CHANGED));
			if (!(container instanceof UnmatchedTaskContainer)) {
				removeOrphan(task, delta);
			}
		} else {
			addTask(task, null);
			//internalAddTask(task, null);
		}
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	/**
	 * @deprecated use moveTask
	 */
	@Deprecated
	public void moveToContainer(AbstractTask task, AbstractTaskCategory container) {
		if (!tasks.containsKey(task.getHandleIdentifier())) {
			tasks.put(task.getHandleIdentifier(), task);
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
		removeFromCategory((AbstractTaskCategory) category, task);
//		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
//		category.internalRemoveChild(task);
//		task.removeParentContainer(category);
//		addOrphan(task, delta);
//		delta.add(new TaskContainerDelta(category, TaskContainerDelta.Kind.ADDED));
//		for (ITaskListChangeListener listener : changeListeners) {
//			listener.containersChanged(delta);
//		}
	}

	/**
	 * @Since 2.3
	 */
	public void removeFromCategory(AbstractTaskCategory category, AbstractTask task) {
		if (category == null || task == null) {
			return;
		}
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		category.internalRemoveChild(task);
		task.removeParentContainer(category);
		addOrphan(task, delta);
		delta.add(new TaskContainerDelta(category, TaskContainerDelta.Kind.CHANGED));
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	/**
	 * TODO: merge this and removeFromCategory() into single removeFromContainer?
	 * 
	 * @since 2.1
	 */
	public void removeFromQuery(AbstractRepositoryQuery query, AbstractTask task) {

		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
		delta.add(new TaskContainerDelta(task, TaskContainerDelta.Kind.CHANGED));

		query.internalRemoveChild(task);
		task.removeParentContainer(query);
		addOrphan(task, delta);

//		for (ITaskListChangeListener listener : changeListeners) {
//			listener.containersChanged(delta);
//		}

//		if (task.getParentContainers().size() == 0 && task.getLastReadTimeStamp() != null) {
//			task.addParentContainer(archiveContainer);
//			archiveContainer.internalAddChild(task);
//		}
	}

	public void removeFromQuery(AbstractRepositoryQuery repositoryQuery, Set<AbstractTask> tasks) {
		// FIXME do a bulk remove
		for (AbstractTask task : tasks) {
			removeFromQuery(repositoryQuery, task);
		}
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
		} else if (!(container instanceof TaskArchive) && !(container instanceof UnmatchedTaskContainer)) {
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
		}
		// TODO: make this delta policy symmetrical with tasks
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();
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
		tasks.remove(task.getHandleIdentifier());
		delta.add(new TaskContainerDelta(task, TaskContainerDelta.Kind.REMOVED));
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containersChanged(delta);
		}
	}

	public void deleteCategory(AbstractTaskCategory category) {
		Set<TaskContainerDelta> delta = new HashSet<TaskContainerDelta>();

		categories.remove(category.getHandleIdentifier());
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

		queries.remove(query.getHandleIdentifier());
		for (AbstractTask task : query.getChildren()) {
			task.removeParentContainer(query);
			addOrphan(task, delta);
		}

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

	private void internalAddTask(AbstractTask task, AbstractTaskContainer container) {
		tasks.put(task.getHandleIdentifier(), task);
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

	/**
	 * @deprecated
	 * @API 3.0: remove
	 */
	@Deprecated
	public List<AbstractTaskCategory> getUserCategories() {
		List<AbstractTaskCategory> included = new ArrayList<AbstractTaskCategory>();
		for (AbstractTaskCategory category : categories.values()) {
			included.add(category);
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

	/**
	 * API 3.0: remove
	 */
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
		boolean isEmpty = getCategories().size() == 1 && getCategories().contains(defaultCategory) /*&& archiveContainer.getChildren().isEmpty()*/;
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

	/**
	 * @API 3.0: remove
	 * @deprecated
	 */
	@Deprecated
	public TaskArchive getArchiveContainer() {
		return archiveContainer;
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
		AbstractTask tempTask = tasks.get(handle);
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
		max = Math.max(largestTaskIdHelper(tasks.values(), 0, 0), max);
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
	 * @since 2.1
	 */
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

}
