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
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;

/**
 * Stores and manages task list elements and their containment hierarchy.
 *
 * @author Mik Kersten
 * @author Rob Elves
 * @since 3.0
 */
public class TaskList implements ITaskList, ITransferList {

	private static String DEFAULT_HANDLE_PREFIX = "handle-"; //$NON-NLS-1$

	private static ILock lock = Job.getJobManager().newLock();

	private Map<String, AbstractTaskCategory> categories;

	private final Set<ITaskListChangeListener> changeListeners = new CopyOnWriteArraySet<>();

	private UncategorizedTaskContainer defaultCategory;

	private int maxLocalTaskId;

	private Map<String, RepositoryQuery> queries;

	private Map<String, UnmatchedTaskContainer> unmatchedMap;

	private Map<String, UnsubmittedTaskContainer> unsubmittedTasksMap;

	private Map<String, AbstractTask> tasks;

	private Set<TaskContainerDelta> delta;

	private int nextHandle = 1;

	public TaskList() {
		reset();
	}

	@Override
	public void addCategory(TaskCategory category) {
		Assert.isNotNull(category);
		try {
			lock();
			if (categories.containsKey(category.getHandleIdentifier())) {
				throw new IllegalArgumentException("Handle " + category.getHandleIdentifier() //$NON-NLS-1$
				+ " already exists in task list"); //$NON-NLS-1$
			}
			categories.put(category.getHandleIdentifier(), category);
			delta.add(new TaskContainerDelta(category, TaskContainerDelta.Kind.ADDED));
		} finally {
			unlock();
		}
	}

	@Override
	public void addChangeListener(ITaskListChangeListener listener) {
		changeListeners.add(listener);
	}

	/**
	 * precondition: task must not be null and must exist in the task list
	 */
	private void addToUnmatched(AbstractTask task, Set<TaskContainerDelta> delta) {
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

	@Override
	public void addQuery(RepositoryQuery query) throws IllegalArgumentException {
		Assert.isNotNull(query);
		try {
			lock();
			if (queries.containsKey(query.getHandleIdentifier())) {
				throw new IllegalArgumentException("Handle " + query.getHandleIdentifier() //$NON-NLS-1$
				+ " already exists in task list"); //$NON-NLS-1$
			}
			queries.put(query.getHandleIdentifier(), query);
			delta.add(new TaskContainerDelta(query, TaskContainerDelta.Kind.ADDED));
		} finally {
			unlock();
		}
	}

	/**
	 * Add task to default category if it's not in the task list.
	 */
	public boolean addTaskIfAbsent(ITask task) {
		if (getTask(task.getRepositoryUrl(), task.getTaskId()) == null) {
			addTask(task, getDefaultCategory());
			return true;
		}
		return false;
	}

	/**
	 * Add orphaned task to the task list
	 */
	@Override
	public void addTask(ITask task) {
		addTask(task, null);
	}

	@Override
	public boolean addTask(ITask itask, AbstractTaskContainer container) {
		AbstractTask task = (AbstractTask) itask;
		Assert.isNotNull(task);
		Assert.isLegal(!(container instanceof UnmatchedTaskContainer));

		try {
			lock();
			task = getOrCreateTask(task);
			if (task.getSynchronizationState() == SynchronizationState.OUTGOING_NEW) {
				String repositoryUrl = task.getAttribute(ITasksCoreConstants.ATTRIBUTE_OUTGOING_NEW_REPOSITORY_URL);
				if (repositoryUrl != null) {
					container = getUnsubmittedContainer(repositoryUrl);
				}
			}
			if (container == null) {
				container = getUnmatchedContainer(task.getRepositoryUrl());
			} else {
				container = getValidElement(container);
			}

			if (container instanceof UnsubmittedTaskContainer && container.isEmpty()) {
				delta.add(new TaskContainerDelta(container, TaskContainerDelta.Kind.ROOT));
			}

			// ensure parent is valid and does not contain task already
			// ensure that we don't create cycles
			if (container == null || task.equals(container) || task.getParentContainers().contains(container) || task.contains(container.getHandleIdentifier())) {
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

			if (!(container instanceof UnmatchedTaskContainer)) {
				removeFromUnmatched(task, delta);
			}

			task.addParentContainer(container);
			container.internalAddChild(task);
			delta.add(new TaskContainerDelta(task, container, TaskContainerDelta.Kind.ADDED));
		} finally {
			unlock();
		}

		return true;
	}

	public void addUnmatchedContainer(UnmatchedTaskContainer orphanedTasksContainer) {
		unmatchedMap.put(orphanedTasksContainer.getRepositoryUrl(), orphanedTasksContainer);
		unsubmittedTasksMap.put(orphanedTasksContainer.getRepositoryUrl(), new UnsubmittedTaskContainer(
				orphanedTasksContainer.getConnectorKind(), orphanedTasksContainer.getRepositoryUrl()));
	}

	@Override
	public void deleteCategory(AbstractTaskCategory category) {
		try {
			lock();
			categories.remove(category.getHandleIdentifier());
			for (ITask task : category.getChildren()) {
				((AbstractTask) task).removeParentContainer(category);
				addToUnmatched((AbstractTask) task, delta);
			}
			delta.add(new TaskContainerDelta(category, TaskContainerDelta.Kind.REMOVED));
			delta.add(new TaskContainerDelta(category, TaskContainerDelta.Kind.DELETED));
		} finally {
			unlock();
		}
	}

	@Override
	public void deleteQuery(RepositoryQuery query) {
		try {
			lock();
			queries.remove(query.getHandleIdentifier());
			for (ITask task : query.getChildren()) {
				((AbstractTask) task).removeParentContainer(query);
				addToUnmatched((AbstractTask) task, delta);
			}
			delta.add(new TaskContainerDelta(query, TaskContainerDelta.Kind.REMOVED));
			delta.add(new TaskContainerDelta(query, TaskContainerDelta.Kind.DELETED));
		} finally {
			unlock();
		}
	}

	/**
	 * Task is removed from all containers. Currently subtasks are not deleted but rather are rather potentially orphaned.
	 */
	@Override
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
				addToUnmatched((AbstractTask) child, delta);
			}

			tasks.remove(task.getHandleIdentifier());
			delta.add(new TaskContainerDelta(task, TaskContainerDelta.Kind.REMOVED));
			delta.add(new TaskContainerDelta(task, TaskContainerDelta.Kind.DELETED));
		} finally {
			unlock();
		}
	}

	private void fireDelta(HashSet<TaskContainerDelta> deltasToFire) {
		for (ITaskListChangeListener listener : changeListeners) {
			try {
				listener.containersChanged(Collections.unmodifiableSet(deltasToFire));
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Notification failed for: " //$NON-NLS-1$
						+ listener, t));
			}
		}
	}

	@Override
	public Collection<AbstractTask> getAllTasks() {
		return Collections.unmodifiableCollection(tasks.values());
	}

	@Override
	public Set<AbstractTaskCategory> getCategories() {
		return Collections.unmodifiableSet(new HashSet<>(categories.values()));
	}

	/**
	 * Exposed for unit testing
	 *
	 * @return unmodifiable collection of ITaskActivityListeners
	 */
	public Set<ITaskListChangeListener> getChangeListeners() {
		return Collections.unmodifiableSet(changeListeners);
	}

	@Override
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

	@Override
	public Set<RepositoryQuery> getQueries() {
		return Collections.unmodifiableSet(new HashSet<>(queries.values()));
	}

	/**
	 * return all queries for the given repository url
	 */
	public Set<RepositoryQuery> getRepositoryQueries(String repositoryUrl) {
		Assert.isNotNull(repositoryUrl);

		Set<RepositoryQuery> repositoryQueries = new HashSet<>();
		for (RepositoryQuery query : queries.values()) {
			if (query.getRepositoryUrl().equals(repositoryUrl)) {
				repositoryQueries.add(query);
			}
		}
		return repositoryQueries;
	}

	public Set<AbstractTaskContainer> getRootElements() {
		Set<AbstractTaskContainer> roots = new HashSet<>();
		roots.add(defaultCategory);
		roots.addAll(categories.values());
		roots.addAll(queries.values());
		roots.addAll(unmatchedMap.values());
		roots.addAll(unsubmittedTasksMap.values());
		return roots;
	}

	/**
	 * TODO: consider removing, if everything becomes a repository task
	 *
	 * @return null if no such task.
	 */
	@Override
	public AbstractTask getTask(String handleIdentifier) {
		if (handleIdentifier == null) {
			return null;
		} else {
			return tasks.get(handleIdentifier);
		}
	}

	@Override
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
		Set<AbstractTaskCategory> containers = new HashSet<>();
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
		Set<ITask> repositoryTasks = new HashSet<>();
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
			UnmatchedTaskContainer unmatched = unmatchedMap.get(repositoryUrl);
			if (unmatched == null && !repositoryUrl.endsWith("/")) {
				unmatched = unmatchedMap.get(repositoryUrl + "/");
			}
			if (unmatched == null) {
				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
						"Failed to find unmatched container for repository \"" + repositoryUrl + "\"")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			return unmatched;
		}
	}

	public Set<UnmatchedTaskContainer> getUnmatchedContainers() {
		return Collections.unmodifiableSet(new HashSet<>(unmatchedMap.values()));
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
			result = unmatchedMap.get(((UnmatchedTaskContainer) taskListElement).getRepositoryUrl());
		} else if (taskListElement instanceof UnsubmittedTaskContainer) {
			result = unsubmittedTasksMap.get(((UnsubmittedTaskContainer) taskListElement).getRepositoryUrl());
		} else if (taskListElement instanceof TaskCategory) {
			result = categories.get(taskListElement.getHandleIdentifier());
		} else if (taskListElement instanceof IRepositoryQuery) {
			result = queries.get(taskListElement.getHandleIdentifier());
		}

		if (result == null) {
			throw new IllegalArgumentException("Element " + taskListElement.getHandleIdentifier() //$NON-NLS-1$
			+ " does not exist in the task list."); //$NON-NLS-1$
		} else {
			return result;
		}
	}

	public void notifyElementsChanged(Set<? extends IRepositoryElement> elements) {
		HashSet<TaskContainerDelta> deltas = new HashSet<>();
		if (elements == null) {
			deltas.add(new TaskContainerDelta(null, TaskContainerDelta.Kind.ROOT));
		} else {
			for (IRepositoryElement element : elements) {
				deltas.add(new TaskContainerDelta(element, TaskContainerDelta.Kind.CONTENT));
			}
		}

		fireDelta(deltas);
	}

	// TODO rename: this indicates a change of the synchronizing/status flag, not of the synchronization state
	public void notifySynchronizationStateChanged(Set<? extends IRepositoryElement> elements) {
		HashSet<TaskContainerDelta> taskChangeDeltas = new HashSet<>();
		for (IRepositoryElement abstractTaskContainer : elements) {
			TaskContainerDelta delta = new TaskContainerDelta(abstractTaskContainer, TaskContainerDelta.Kind.CONTENT);
			delta.setTransient(true);
			taskChangeDeltas.add(delta);
		}

		fireDelta(taskChangeDeltas);
	}

	// TODO rename: this indicates a change of the synchronizing/status flag, not of the synchronization state
	@Override
	public void notifySynchronizationStateChanged(IRepositoryElement element) {
		notifySynchronizationStateChanged(Collections.singleton(element));
	}

	@Override
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
				if (oldRepositoryUrl
						.equals(task.getAttribute(ITasksCoreConstants.ATTRIBUTE_OUTGOING_NEW_REPOSITORY_URL))) {
					task.setAttribute(ITasksCoreConstants.ATTRIBUTE_OUTGOING_NEW_REPOSITORY_URL, newRepositoryUrl);
				}
			}

			for (RepositoryQuery query : queries.values()) {
				if (query.getRepositoryUrl().equals(oldRepositoryUrl)) {
					query.setRepositoryUrl(newRepositoryUrl);
					delta.add(new TaskContainerDelta(query, TaskContainerDelta.Kind.CONTENT));
				}
			}

			for (UnmatchedTaskContainer unmatched : unmatchedMap.values()) {
				if (unmatched.getRepositoryUrl().equals(oldRepositoryUrl)) {
					unmatchedMap.remove(oldRepositoryUrl);
					//categories.remove(orphans.getHandleIdentifier());
					unmatched.setRepositoryUrl(newRepositoryUrl);
					unmatchedMap.put(newRepositoryUrl, unmatched);
					//categories.put(orphans.getHandleIdentifier(), orphans);
					delta.add(new TaskContainerDelta(unmatched, TaskContainerDelta.Kind.CONTENT));
				}
			}
			for (UnsubmittedTaskContainer unsubmitted : unsubmittedTasksMap.values()) {
				if (unsubmitted.getRepositoryUrl().equals(oldRepositoryUrl)) {
					unsubmittedTasksMap.remove(oldRepositoryUrl);
					unsubmitted.setRepositoryUrl(newRepositoryUrl);
					unsubmittedTasksMap.put(newRepositoryUrl, unsubmitted);
					delta.add(new TaskContainerDelta(unsubmitted, TaskContainerDelta.Kind.CONTENT));
				}
			}
		} finally {
			unlock();
		}
	}

	public AbstractTask refactorTaskId(ITask oldTask, String newTaskId) {
		TaskTask newTask = new TaskTask(oldTask.getConnectorKind(), oldTask.getRepositoryUrl(), newTaskId);

		newTask.setSummary(oldTask.getSummary());
		newTask.setPriority(oldTask.getPriority());
		newTask.setSynchronizationState(oldTask.getSynchronizationState());
		newTask.setCompletionDate(oldTask.getCompletionDate());
		newTask.setCreationDate(oldTask.getCreationDate());
		newTask.setModificationDate(oldTask.getModificationDate());
		newTask.setTaskKind(oldTask.getTaskKind());
		newTask.setOwnerId(oldTask.getOwnerId());
		newTask.setOwner(oldTask.getOwner());
		newTask.setTaskKey(oldTask.getTaskKey());
		if (oldTask instanceof AbstractTask task) {
			newTask.setSynchronizing(task.isSynchronizing());
			newTask.setMarkReadPending(task.isMarkReadPending());
			newTask.setNotified(task.isNotified());
			newTask.setChanged(task.isChanged());
			newTask.setReminded(task.isReminded());
			newTask.setStatus(task.getStatus());
			newTask.setNotes(task.getNotes());
			newTask.setEstimatedTimeHours(task.getEstimatedTimeHours());
			newTask.setUrl(task.getUrl());
			addTaskContainers(task, newTask);
		}
		Map<String, String> attributeMap = oldTask.getAttributes();
		for (String key : attributeMap.keySet()) {
			newTask.setAttribute(key, attributeMap.get(key));
		}

		deleteTask(oldTask);
		return newTask;
	}

	private void addTaskContainers(AbstractTask oldTask, AbstractTask newTask) {
		Set<AbstractTaskContainer> containers = oldTask.getParentContainers();
		if (containers.isEmpty()
				|| containers.size() == 1 && containers.iterator().next() instanceof UnmatchedTaskContainer) {
			addTask(newTask);
		} else {
			for (AbstractTaskContainer container : containers) {
				addTask(newTask, container);
			}
		}
		for (ITask subtask : oldTask.getChildren()) {
			addTask(subtask, newTask);
		}
	}

	@Override
	public void removeChangeListener(ITaskListChangeListener listener) {
		changeListeners.remove(listener);
	}

	@Override
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
				addToUnmatched((AbstractTask) task, delta);
			}
		} finally {
			unlock();
		}
	}

	/**
	 * Note: does not add <code>task</code> to the unmatched container.
	 */
	private void removeFromContainerInternal(AbstractTaskContainer container, ITask task,
			Set<TaskContainerDelta> delta) {
		assert container.getChildren().contains(task);

		container.internalRemoveChild(task);
		((AbstractTask) task).removeParentContainer(container);

		delta.add(new TaskContainerDelta(task, container, TaskContainerDelta.Kind.REMOVED));
	}

	private void removeFromUnmatched(AbstractTask task, Set<TaskContainerDelta> delta) {
		AbstractTaskContainer unmatched = getUnmatchedContainer(task.getRepositoryUrl());
		if (unmatched != null) {
			// first check that the task has the unmatched container as a parent
			// this provides considerable performance improvements when loading large task lists
			if (task.getParentContainers().contains(unmatched) && unmatched.internalRemoveChild(task)) {
				delta.add(new TaskContainerDelta(task, unmatched, TaskContainerDelta.Kind.REMOVED));
				task.removeParentContainer(unmatched);
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
			tasks = new ConcurrentHashMap<>();

			unmatchedMap = new ConcurrentHashMap<>();
			unsubmittedTasksMap = new ConcurrentHashMap<>();
			categories = new ConcurrentHashMap<>();
			queries = new ConcurrentHashMap<>();

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
		run(runnable, monitor, false);
	}

	public void run(ITaskListRunnable runnable, IProgressMonitor monitor, boolean ignoreInterrupts)
			throws CoreException {
		monitor = Policy.monitorFor(monitor);
		try {
			lock(monitor, ignoreInterrupts);

			runnable.execute(monitor);

		} finally {
			unlock();
		}
	}

	private void lock() {
		lock.acquire();
		if (lock.getDepth() == 1) {
			delta = new HashSet<>();
		}
	}

	private void lock(IProgressMonitor monitor, boolean ignoreInterrupts) throws CoreException {
		while (!monitor.isCanceled()) {
			try {
				if (lock.acquire(3000)) {
					if (lock.getDepth() == 1) {
						delta = new HashSet<>();
					}
					// success
					return;
				}
			} catch (InterruptedException e) {
				if (ignoreInterrupts) {
					// clear interrupted status to retry lock.aquire()
					Thread.interrupted();
				} else {
					break;
				}
			}
		}
		throw new OperationCanceledException();
	}

	private void unlock() {
		HashSet<TaskContainerDelta> toFire = null;
		try {
			if (lock.getDepth() == 1) {
				toFire = new HashSet<>(delta);
			}
		} finally {
			lock.release();
		}
		if (toFire != null && toFire.size() > 0) {
			fireDelta(toFire);
		}
	}

	public static ISchedulingRule getSchedulingRule() {
		return ITasksCoreConstants.TASKLIST_SCHEDULING_RULE;
	}

	public String getUniqueHandleIdentifier() {
		try {
			lock();
			while (nextHandle < Integer.MAX_VALUE) {
				String handle = DEFAULT_HANDLE_PREFIX + nextHandle;
				nextHandle++;
				if (!categories.containsKey(handle) && !queries.containsKey(handle) && !unmatchedMap.containsKey(handle)
						&& !tasks.containsKey(handle)) {
					return handle;
				}
			}
			throw new RuntimeException("No more unique handles for task list"); //$NON-NLS-1$
		} finally {
			unlock();
		}
	}

	public UnsubmittedTaskContainer getUnsubmittedContainer(String repositoryUrl) {
		return unsubmittedTasksMap.get(repositoryUrl);
	}

}
