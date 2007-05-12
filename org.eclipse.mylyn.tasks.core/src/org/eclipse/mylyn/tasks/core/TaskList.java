/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasks.core;

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

import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.core.RepositoryTaskHandleUtil;

/**
 * TODO: some asymetry left between query containers and other task containers
 * 
 * @author Mik Kersten
 */
public class TaskList {

	//public static final String LABEL_ROOT = "Uncategorized";// "Root

	// (automatic)";

	private int lastTaskNum = 0;

	private Set<ITaskListChangeListener> changeListeners = new CopyOnWriteArraySet<ITaskListChangeListener>();

	private Map<String, ITask> tasks;

	private Map<String, AbstractQueryHit> queryHits;

	private Map<String, AbstractTaskContainer> categories;

	private Map<String, AbstractRepositoryQuery> queries;

	private TaskArchive archiveContainer;

	private UncategorizedCategory uncategorizedCategory;

	private List<ITask> activeTasks;

	public TaskList() {
		reset();
	}

	/**
	 * Public for testing.
	 */
	public void reset() {
		tasks = new ConcurrentHashMap<String, ITask>();
		queryHits = new ConcurrentHashMap<String, AbstractQueryHit>();

		categories = new ConcurrentHashMap<String, AbstractTaskContainer>();
		queries = new ConcurrentHashMap<String, AbstractRepositoryQuery>();

		archiveContainer = new TaskArchive(this);
		uncategorizedCategory = new UncategorizedCategory(this);

		activeTasks = new CopyOnWriteArrayList<ITask>();
		lastTaskNum = 0;
		categories.put(uncategorizedCategory.getHandleIdentifier(), uncategorizedCategory);
		categories.put(archiveContainer.getHandleIdentifier(), archiveContainer);
	}

	public void addTask(ITask task) {
		addTask(task, archiveContainer);
	}

	public void addTask(ITask task, AbstractTaskContainer category) {
		tasks.put(task.getHandleIdentifier(), task);
		if (category != null) {
			category.add(task);
			task.setContainer(category);
		} else {
			uncategorizedCategory.add(task);
			task.setContainer(uncategorizedCategory);
		}
		for (ITaskListChangeListener listener : changeListeners) {
			listener.taskAdded(task);
		}
	}

	public void refactorRepositoryUrl(String oldRepositoryUrl, String newRepositoryUrl) {
		// TODO: update mappings in offline task data, will currently lose them
		for (ITask task : tasks.values()) {
			if (task instanceof AbstractRepositoryTask) {
				AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) task;
				if (oldRepositoryUrl.equals(RepositoryTaskHandleUtil.getRepositoryUrl(repositoryTask
						.getHandleIdentifier()))) {
					tasks.remove(repositoryTask.getHandleIdentifier());
// String taskId =
// AbstractRepositoryTask.getTaskId(repositoryTask.getHandleIdentifier());
// String newHandle = AbstractRepositoryTask.getHandle(newUrl, taskId);
// repositoryTask.setHandleIdentifier(newHandle);
					repositoryTask.setRepositoryUrl(newRepositoryUrl);
					tasks.put(repositoryTask.getHandleIdentifier(), repositoryTask);

					String taskUrl = repositoryTask.getTaskUrl();
					if (taskUrl.startsWith(oldRepositoryUrl)) {
						repositoryTask.setTaskUrl(newRepositoryUrl + taskUrl.substring(oldRepositoryUrl.length()));
					}
				}
			}
		}

		for (AbstractRepositoryQuery query : queries.values()) {
			if (query.getRepositoryUrl().equals(oldRepositoryUrl)) {
				query.setRepositoryUrl(newRepositoryUrl);
				for (AbstractQueryHit hit : query.getHits()) {
					queryHits.remove(hit.getHandleIdentifier());
					hit.setRepositoryUrl(newRepositoryUrl);
					queryHits.put(hit.getHandleIdentifier(), hit);
				}
				for (ITaskListChangeListener listener : changeListeners) {
					listener.containerInfoChanged(query);
				}
			}
		}
	}

	public void moveToRoot(ITask task) {
		moveToContainer(uncategorizedCategory, task);
	}

	public void moveToContainer(AbstractTaskContainer toContainer, ITask task) {
		if (!tasks.containsKey(task.getHandleIdentifier())) {
			tasks.put(task.getHandleIdentifier(), task);
		}

		AbstractTaskContainer fromContainer = task.getContainer();
		if (fromContainer instanceof AbstractTaskContainer) {
			(fromContainer).remove(task);
		}
		if (toContainer != null) {
			internalAddTask(task, toContainer);
		} else {
			internalAddTask(task, archiveContainer);
		}
		for (ITaskListChangeListener listener : changeListeners) {
			listener.taskMoved(task, fromContainer, toContainer);
		}
	}

	public void addCategory(AbstractTaskContainer category) {
		categories.put(category.getHandleIdentifier(), category);
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containerAdded(category);
		}
	}

	public void removeFromCategory(TaskCategory category, ITask task) {
		moveToContainer(archiveContainer, task);
	}

	public void removeFromRoot(ITask task) {
		moveToContainer(archiveContainer, task);
	}

	public void renameTask(Task task, String description) {
		task.setSummary(description);
		for (ITaskListChangeListener listener : changeListeners) {
			listener.localInfoChanged(task);
		}
	}

	public void renameContainer(AbstractTaskContainer container, String newDescription) {
		if (!(container instanceof TaskArchive) && !(container instanceof UncategorizedCategory)) {
			if (queries.remove(container.getHandleIdentifier()) != null) {
				container.setDescription(newDescription);
				if (container instanceof AbstractRepositoryQuery) {
					this.addQuery((AbstractRepositoryQuery) container);
				}
			} else if (categories.remove(container.getHandleIdentifier()) != null) {
				container.setDescription(newDescription);
				this.addCategory(container);
			}
		}
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containerInfoChanged(container);
		}
	}

	public void addQuery(AbstractRepositoryQuery query) {
		queries.put(query.getHandleIdentifier(), query);
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containerAdded(query);
		}
	}

	/**
	 * TODO: refactor around querying containers for their tasks
	 * 
	 * Task is removed from all containers: root, archive, category, and tasks
	 * catchall (Currently no support for deletion of subtasks)
	 */
	public void deleteTask(ITask task) {
		archiveContainer.remove(task);
		uncategorizedCategory.remove(task);
		if (task.getContainer() != null) {
			task.getContainer().remove(task);
			task.setContainer(null);
		}
		tasks.remove(task.getHandleIdentifier());
		for (ITaskListChangeListener listener : changeListeners) {
			listener.taskDeleted(task);
		}
	}

	public void deleteCategory(AbstractTaskContainer category) {
		for (ITask task : category.getChildren()) {
			uncategorizedCategory.add(task);
		}
		categories.remove(category.getHandleIdentifier());
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containerDeleted(category);
		}
	}

	public void deleteQuery(AbstractRepositoryQuery query) {
		queries.remove(query.getHandleIdentifier());
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containerDeleted(query);
		}
	}

	public void markComplete(ITask task, boolean complete) {
		task.setCompleted(complete);
		for (ITaskListChangeListener listener : new ArrayList<ITaskListChangeListener>(changeListeners)) {
			listener.localInfoChanged(task);
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
	public void internalAddCategory(AbstractTaskContainer category) {
		categories.put(category.getHandleIdentifier(), category);
	}

	public void internalAddTask(ITask task, AbstractTaskContainer container) {
		tasks.put(task.getHandleIdentifier(), task);
		if (container != null) {
			task.setContainer(container);
			container.add(task);
		} else {
			task.setContainer(uncategorizedCategory);
			uncategorizedCategory.add(task);
		}
	}

	public void internalAddRootTask(ITask task) {
		internalAddTask(task, uncategorizedCategory);
	}

	public void internalAddQuery(AbstractRepositoryQuery query) {
		queries.put(query.getHandleIdentifier(), query);
	}

	public void setActive(ITask task, boolean active) {
		task.setActive(active);
		if (active && !activeTasks.contains(task)) {
			activeTasks.add(task);
		} else if (!active) {
			activeTasks.remove(task);
		}
	}

	// private boolean deleteTaskHelper(Set<ITask> tasks, ITask toDelete) {
	// for (ITask task : tasks) {
	// if (task.getHandleIdentifier().equals(toDelete.getHandleIdentifier())) {
	// tasks.remove(task);
	// return true;
	// } else {
	// if (deleteTaskHelper(task.getChildren(), toDelete))
	// return true;
	// }
	// }
	// return false;
	// }

	public List<ITask> getActiveTasks() {
		return activeTasks;
	}

	/**
	 * HACK: returns first
	 * 
	 * @return
	 */
	public ITask getActiveTask() {
		if (activeTasks.size() > 0) {
			return activeTasks.get(0);
		} else {
			return null;
		}
	}

	public Set<ITask> getRootTasks() {
		return Collections.unmodifiableSet(uncategorizedCategory.getChildren());
	}

	public Set<AbstractTaskContainer> getCategories() {
		// TODO: remove wrapping once API can change
		return Collections.unmodifiableSet(new HashSet<AbstractTaskContainer>(categories.values()));
	}

	public List<AbstractTaskContainer> getUserCategories() {
		List<AbstractTaskContainer> included = new ArrayList<AbstractTaskContainer>();
		for (AbstractTaskContainer category : categories.values()) {
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

	public Set<ITaskListElement> getRootElements() {
		Set<ITaskListElement> roots = new HashSet<ITaskListElement>();
		roots.add(uncategorizedCategory);
		for (AbstractTaskContainer cat : categories.values())
			roots.add(cat);
		for (AbstractRepositoryQuery query : queries.values())
			roots.add(query);
		return roots;
	}

	public Collection<ITask> getAllTasks() {
		return Collections.unmodifiableCollection(tasks.values());
	}

	public Set<AbstractTaskContainer> getTaskContainers() {
		Set<AbstractTaskContainer> containers = new HashSet<AbstractTaskContainer>();
		for (AbstractTaskContainer container : categories.values()) {
			if (container instanceof TaskCategory || container instanceof TaskArchive || container instanceof UncategorizedCategory) {
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
			if (query.findQueryHit(handle) != null) {
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
	public ITask getTask(String handleIdentifier) {
		if (handleIdentifier == null) {
			return null;
		} else {
			return tasks.get(handleIdentifier);
		}
	}

	/**
	 * @since 2.0
	 */
	public AbstractRepositoryTask getTask(String repositoryUrl, String taskId) {
		String handle = RepositoryTaskHandleUtil.getHandle(repositoryUrl, taskId);
		ITask task = getTask(handle);
		if (task instanceof AbstractRepositoryTask) {
			return (AbstractRepositoryTask) task;
		} else {
			return null;
		}
	}

	/**
	 * Searches for a task whose URL matches
	 * 
	 * @return first task with a matching URL.
	 * @since 2.0
	 */
	public AbstractRepositoryTask getRepositoryTask(String taskUrl) {
		for (ITask currTask : tasks.values()) {
			if (currTask instanceof AbstractRepositoryTask) {
				String currUrl = ((AbstractRepositoryTask) currTask).getTaskUrl();
				if (currUrl != null && !currUrl.equals("") && currUrl.equals(taskUrl)) {
					return (AbstractRepositoryTask) currTask;
				}
			}
		}
		return null;
	}

	public AbstractTaskContainer getContainerForHandle(String categoryHandle) {
		for (AbstractTaskContainer cat : categories.values()) {
			if (cat instanceof AbstractTaskContainer) {
				if (cat.getHandleIdentifier().equals(categoryHandle)) {
					return cat;
				}
			}
		}
		return null;
	}

	public AbstractTaskContainer getUncategorizedCategory() {
		return uncategorizedCategory;
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
			if (query.findQueryHit(handle) != null) {
				queriesForHandle.add(query);
			}
		}
		return queriesForHandle;
	}

	/** if handle == null or no query hits found an empty set is returned * */
	public Set<AbstractQueryHit> getQueryHits(Set<String> handles) {
		if (handles == null) {
			return Collections.emptySet();
		}
		HashSet<AbstractQueryHit> result = new HashSet<AbstractQueryHit>();
		for (String handle : handles) {
			AbstractQueryHit hit = queryHits.get(handle);
			if (hit != null) {
				result.add(hit);
			}
		}
		return result;
	}

	public AbstractQueryHit getQueryHit(String handle) {
		if (handle != null) {
			return queryHits.get(handle);
		}
		return null;
	}

	/** for testing */
	public Set<AbstractQueryHit> getQueryHits() {
		// TODO: remove wrapping once API can change
		return new HashSet<AbstractQueryHit>(queryHits.values());
	}

	/** called by AbstractRepositoryQuery */
	public void addQueryHit(AbstractQueryHit hit) {
		queryHits.put(hit.getHandleIdentifier(), hit);
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
	 */
	public Set<AbstractRepositoryTask> getRepositoryTasks(String repositoryUrl) {
		Set<AbstractRepositoryTask> repositoryTasks = new HashSet<AbstractRepositoryTask>();
		if (repositoryUrl != null) {
			for (ITask task : tasks.values()) {
				if (task instanceof AbstractRepositoryTask) {
					AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) task;
					if (repositoryTask.getRepositoryUrl().equals(repositoryUrl)) {
						repositoryTasks.add(repositoryTask);
					}
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
	 * TODO: refactor into task deltas?
	 */
	public void notifyLocalInfoChanged(ITask task) {
		for (ITaskListChangeListener listener : new ArrayList<ITaskListChangeListener>(changeListeners)) {
			try {
				listener.localInfoChanged(task);
			} catch (Throwable t) {
				MylarStatusHandler.fail(t, "notification failed for: " + listener, false);
			}
		}
	}

	public void notifyRepositoryInfoChanged(ITask task) {
		for (ITaskListChangeListener listener : new ArrayList<ITaskListChangeListener>(changeListeners)) {
			try {
				listener.repositoryInfoChanged(task);
			} catch (Throwable t) {
				MylarStatusHandler.fail(t, "notification failed for: " + listener, false);
			}
		}
	}

	public void notifyContainerUpdated(AbstractTaskContainer container) {
		for (ITaskListChangeListener listener : new ArrayList<ITaskListChangeListener>(changeListeners)) {
			try {
				listener.containerInfoChanged(container);
			} catch (Throwable t) {
				MylarStatusHandler.fail(t, "notification failed for: " + listener, false);
			}
		}
	}

	public int getNextTaskNum() {
		return ++lastTaskNum;
	}

	public void setLastTaskNum(int lastTaskNum) {
		this.lastTaskNum = lastTaskNum;
	}

	/** For tasklist persistence. Use getNextTaskNum for task construction */
	public int getLastTaskNum() {
		return lastTaskNum;
	}

	/** Note: use getNextTaskNum for new task construction */
	public int findLargestTaskHandle() {
		int max = 0;
		max = Math.max(largestTaskHandleHelper(tasks.values()), max);
		for (AbstractTaskContainer cat : getTaskContainers()) {
			max = Math.max(largestTaskHandleHelper(cat.getChildren()), max);
		}
		return max;
	}

	private int largestTaskHandleHelper(Collection<ITask> tasks) {
		int ihandle = 0;
		int max = 0;
		for (ITask task : tasks) {
			if (!(task instanceof AbstractRepositoryTask)) {
				String string = task.getHandleIdentifier().substring(task.getHandleIdentifier().lastIndexOf('-') + 1,
						task.getHandleIdentifier().length());
				try {
					ihandle = Integer.parseInt(string);
				} catch (NumberFormatException nfe) {
				}
			}
			max = Math.max(ihandle, max);
			ihandle = largestTaskHandleHelper(task.getChildren());
			max = Math.max(ihandle, max);
		}
		return max;
	}

	/**
	 * Orphaned hits arise when no query in the tasklist references a hit in the
	 * master list maintained by the tasklist. Orphaned hits don't span
	 * workbench re-start but this just helps maintain the list in case of
	 * prolonged workbench uptime.
	 */
	public void removeOrphanedHits() {
		for (String handle : new HashSet<String>(queryHits.keySet())) {
			Set<AbstractRepositoryQuery> queries = getQueriesForHandle(handle);
			if (queries == null || queries.isEmpty()) {
				queryHits.remove(handle);
			}
		}
	}
}
