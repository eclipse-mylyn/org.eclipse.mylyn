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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylar.context.core.MylarStatusHandler;

/**
 * TODO: some asymetry left between query containers and other task containers
 * 
 * @author Mik Kersten
 */
public class TaskList {

	public static final String LABEL_ROOT = "Root (automatic)";

	private int lastTaskNum = 0;

	private List<ITaskListChangeListener> changeListeners = new ArrayList<ITaskListChangeListener>();

	private Map<String, ITask> tasks;

	private Map<String, AbstractQueryHit> queryHits;

	private TaskArchive archiveContainer;

	private TaskCategory rootCategory;

	private Set<AbstractTaskContainer> categories;

	private Set<AbstractRepositoryQuery> queries;

	private List<ITask> activeTasks;

	public TaskList() {
		reset();
	}

	/**
	 * Public for testing.
	 */
	public void reset() {
		tasks = new HashMap<String, ITask>();
		queryHits = new HashMap<String, AbstractQueryHit>();
		archiveContainer = new TaskArchive(this);
		rootCategory = new TaskCategory(LABEL_ROOT, this);
		categories = new HashSet<AbstractTaskContainer>();
		queries = new HashSet<AbstractRepositoryQuery>();
		activeTasks = new ArrayList<ITask>();
		lastTaskNum = 0;
		categories.add(archiveContainer);
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
			rootCategory.add(task);
			task.setContainer(rootCategory);
		}
		for (ITaskListChangeListener listener : changeListeners) {
			listener.taskAdded(task);
		}
	}

	public void refactorRepositoryUrl(Object oldUrl, String newUrl) {
		for (ITask task : new ArrayList<ITask>(tasks.values())) {
			if (task instanceof AbstractRepositoryTask) {
				AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) task;
				if (oldUrl.equals(AbstractRepositoryTask.getRepositoryUrl(repositoryTask.getHandleIdentifier()))) {
					tasks.remove(repositoryTask.getHandleIdentifier());
					String id = AbstractRepositoryTask.getTaskId(repositoryTask.getHandleIdentifier());
					String newHandle = AbstractRepositoryTask.getHandle(newUrl, id);
					repositoryTask.setHandleIdentifier(newHandle);
					tasks.put(newHandle, repositoryTask);
				}
			}
		}

		for (AbstractRepositoryQuery query : queries) {
			if (query.getRepositoryUrl().equals(oldUrl)) {
				query.setRepositoryUrl(newUrl);
				for (AbstractQueryHit hit : query.getHits()) {
					hit.setRepositoryUrl(newUrl);
				}
				for (ITaskListChangeListener listener : changeListeners) {
					listener.containerInfoChanged(query);
				}
			}
		}
	}

	public void moveToRoot(ITask task) {
		moveToContainer(rootCategory, task);
	}

	public void moveToContainer(AbstractTaskContainer toContainer, ITask task) {
		if (!tasks.containsKey(task.getHandleIdentifier())) {
			tasks.put(task.getHandleIdentifier(), task);
		}

		AbstractTaskContainer fromContainer = task.getContainer();
		if (fromContainer instanceof AbstractTaskContainer) {
			((AbstractTaskContainer) fromContainer).remove(task);
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
		categories.add(category);
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
		task.setDescription(description);
		for (ITaskListChangeListener listener : changeListeners) {
			listener.localInfoChanged(task);
		}
	}

	public void renameContainer(AbstractTaskContainer container, String newDescription) {
		if (!(container instanceof TaskArchive)) {
			if (queries.remove(container)) {
				container.setDescription(newDescription);
				if (container instanceof AbstractRepositoryQuery) {
					this.addQuery((AbstractRepositoryQuery) container);
				}
			} else if (categories.remove(container)) {
				container.setDescription(newDescription);
				this.addCategory(container);
			}
		}
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containerInfoChanged(container);
		}
	}

	public void addQuery(AbstractRepositoryQuery query) {
		queries.add(query);
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
		rootCategory.remove(task);
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
			rootCategory.add(task);
		}
		categories.remove(category);
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containerDeleted(category);
		}
	}

	public void deleteQuery(AbstractRepositoryQuery query) {
		queries.remove(query);		
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
	public void internalAddCategory(AbstractTaskContainer cat) {
		categories.add(cat);
	}

	public void internalAddTask(ITask task, AbstractTaskContainer container) {
		tasks.put(task.getHandleIdentifier(), task);
		if (container != null) {
			task.setContainer(container);
			container.add(task);
		} else {
			task.setContainer(rootCategory);
			rootCategory.add(task);
		}
	}

	public void internalAddRootTask(ITask task) {
		internalAddTask(task, rootCategory);
	}

	public void internalAddQuery(AbstractRepositoryQuery query) {
		queries.add(query);
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
		return rootCategory.getChildren();
	}

	public Set<AbstractTaskContainer> getCategories() {
		return categories;
	}

	public List<AbstractTaskContainer> getUserCategories() {
		List<AbstractTaskContainer> included = new ArrayList<AbstractTaskContainer>();
		for (AbstractTaskContainer category : categories) {
			if (!(category instanceof TaskArchive)) {
				included.add(category);
			}
		}
		return included;
	}

	public Set<AbstractRepositoryQuery> getQueries() {
		return queries;
	}

	public Set<ITaskListElement> getRootElements() {
		Set<ITaskListElement> roots = new HashSet<ITaskListElement>();
		for (ITask task : rootCategory.getChildren())
			roots.add(task);
		for (AbstractTaskContainer cat : categories)
			roots.add(cat);
		for (AbstractRepositoryQuery query : queries)
			roots.add(query);
		return roots;
	}

	public Collection<ITask> getAllTasks() {
		return tasks.values();
	}

	public Set<AbstractTaskContainer> getTaskContainers() {
		Set<AbstractTaskContainer> containers = new HashSet<AbstractTaskContainer>();
		for (AbstractTaskContainer container : categories) {
			if (container instanceof TaskCategory || container instanceof TaskArchive) {
				containers.add((AbstractTaskContainer) container);
			}
		}
		return containers;
	}

	public AbstractRepositoryQuery getQueryForHandle(String handle) {
		if (handle == null) {
			return null;
		}
		for (AbstractRepositoryQuery query : queries) {
			if (query.findQueryHit(handle) != null) {
				return query;
			}
		}
		return null;
	}

	public boolean isEmpty() {
		boolean archiveIsEmpty = getCategories().size() == 1
				&& getCategories().iterator().next().equals(archiveContainer)
				&& archiveContainer.getChildren().isEmpty();
		return getAllTasks().size() == 0 && archiveIsEmpty && getQueries().size() == 0;
	}

	public ITask getTask(String handleIdentifier) {
		return tasks.get(handleIdentifier);
	}

	public AbstractTaskContainer getContainerForHandle(String categoryHandle) {
		for (AbstractTaskContainer cat : categories) {
			if (cat instanceof AbstractTaskContainer) {
				if (cat.getHandleIdentifier().equals(categoryHandle)) {
					return (AbstractTaskContainer) cat;
				}
			}
		}
		return null;
	}

	public TaskCategory getRootCategory() {
		return rootCategory;
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
		for (AbstractRepositoryQuery query : queries) {
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
			if(hit != null) {
				result.add(queryHits.get(handle));
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
			for (AbstractRepositoryQuery query : queries) {
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
	public List<ITaskListChangeListener> getChangeListeners() {
		return Collections.unmodifiableList(changeListeners);
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
	 * master list maintained by the tasklist. Orphaned hits don't span workbench 
	 * re-start but this just helps maintain the list in case of prolonged 
	 * workbench uptime.
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
