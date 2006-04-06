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

package org.eclipse.mylar.provisional.tasklist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylar.internal.core.util.MylarStatusHandler;

/**
 * TODO: some asymetry left between query containers and other task containers
 * 
 * @author Mik Kersten
 */
public class TaskList {

	public static final String LABEL_ROOT = "Root (automatic)";

	private List<ITaskListChangeListener> changeListeners = new ArrayList<ITaskListChangeListener>();
	
	private Map<String, ITask> tasks;
	
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
		archiveContainer = new TaskArchive(this);
		rootCategory = new TaskCategory(LABEL_ROOT, this);
		categories = new HashSet<AbstractTaskContainer>();
		queries = new HashSet<AbstractRepositoryQuery>();
		activeTasks = new ArrayList<ITask>();
		
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
	
	public void moveToRoot(ITask task) {
		moveToContainer(rootCategory, task);
	}

	public void moveToContainer(AbstractTaskContainer toContainer, ITask task) {
		if (!tasks.containsKey(task.getHandleIdentifier())) {
			tasks.put(task.getHandleIdentifier(), task);
		}
		
		AbstractTaskContainer fromContainer = task.getContainer();
		if (fromContainer instanceof AbstractTaskContainer) {
			((AbstractTaskContainer)fromContainer).remove(task);
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
			container.setDescription(newDescription);
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
	 */
	public void deleteTask(ITask task) {
		deleteTaskHelper(archiveContainer.getChildren(), task);
		boolean deleted = deleteTaskHelper(rootCategory.getChildren(), task);
		task.setContainer(null);
		if (!deleted) {
			for (AbstractTaskContainer cat : getTaskContainers()) {
				deleted = deleteTaskHelper(cat.getChildren(), task);
				if (deleted) {
					break;
				}
			}
		}
		tasks.remove(task.getHandleIdentifier());
		for (ITaskListChangeListener listener : changeListeners) {
			listener.taskDeleted(task);
		}
	}

	public void deleteCategory(AbstractTaskContainer category) {
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

	void setActive(ITask task, boolean active) {
		task.setActive(active);
		if (active && !activeTasks.contains(task)) {
			activeTasks.add(task);
		} else if (!active) {
			activeTasks.remove(task);
		}
	}
	
	private boolean deleteTaskHelper(Set<ITask> tasks, ITask toDelete) {
		for (ITask task : tasks) {
			if (task.getHandleIdentifier().equals(toDelete.getHandleIdentifier())) {
				tasks.remove(task);
				return true;
			} else {
				if (deleteTaskHelper(task.getChildren(), toDelete))
					return true;
			}
		}
		return false;
	}

	private AbstractQueryHit findQueryHitHelper(Set<? extends ITaskListElement> elements, String handle) {
		if (handle == null)
			return null;
		for (ITaskListElement element : elements) {
			if (element instanceof AbstractQueryHit) {
				AbstractQueryHit hit = (AbstractQueryHit) element;
				if (hit.getHandleIdentifier().compareTo(handle) == 0) {
					return hit;
				}
			}
		}
		return null;
	}

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
			if (!category.getDescription().endsWith(DelegatingTaskExternalizer.LABEL_AUTOMATIC)) {
				included.add(category);
			}
		}
		return included;
	}

	public Set<AbstractRepositoryQuery> getQueries() {
		return queries;
	}

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
				String string = task.getHandleIdentifier().substring(task.getHandleIdentifier().indexOf('-') + 1,
						task.getHandleIdentifier().length());
				if (!"".equals(string)) {
					ihandle = Integer.parseInt(string);
				}
			}
			max = Math.max(ihandle, max);
			ihandle = largestTaskHandleHelper(task.getChildren());
			max = Math.max(ihandle, max);
		}
		return max;
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
			if ((findQueryHitHelper(query.getHits(), handle)) != null) {
				return query;
			}
		}
		return null;
	}

	/**
	 * NOTE: will only return first occurrence of the hit in the first
	 * category it is matched in.
	 */
	public AbstractQueryHit getQueryHitForHandle(String handle) {
		if (handle == null) {
			return null;
		}
		AbstractQueryHit foundHit = null;
		for (AbstractRepositoryQuery query : queries) {
			if ((foundHit = findQueryHitHelper(query.getHits(), handle)) != null) {
				return foundHit;
			}
		}
		return foundHit;
	}

	public boolean isEmpty() {
		boolean archiveIsEmpty = getCategories().size() == 1
				&& getCategories().iterator().next().equals(archiveContainer) && archiveContainer.getChildren().isEmpty();
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

	/** if handle == null or no queries found an empty set is returned **/	 
	public Set<AbstractRepositoryQuery> getQueriesForHandle(String handle) {
		Set<AbstractRepositoryQuery> queriesForHandle = new HashSet<AbstractRepositoryQuery>();
		if (handle == null) {
			return queriesForHandle;
		}
		for (AbstractRepositoryQuery query : queries) {
			if ((findQueryHitHelper(query.getHits(), handle)) != null) {
				queriesForHandle.add(query);
			}
		}
		return queriesForHandle;
	}
	/**
	 *  return all queries for the given repository url
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

	/** if handle == null or no query hits found an empty set is returned **/
	public Set<AbstractQueryHit> getQueryHitsForHandle(String handle) {
		Set<AbstractQueryHit> hitsForHandle = new HashSet<AbstractQueryHit>();
		if (handle == null) {
			return hitsForHandle;
		}
		AbstractQueryHit foundHit = null;
		for (AbstractRepositoryQuery query : queries) {
			if ((foundHit = findQueryHitHelper(query.getHits(), handle)) != null) {
				hitsForHandle.add(foundHit);
			}
		}
		return hitsForHandle;
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

	public void notifyQueryUpdated(AbstractRepositoryQuery query) {
		for (ITaskListChangeListener listener : new ArrayList<ITaskListChangeListener>(changeListeners)) {
			try {
				listener.containerInfoChanged(query);
			} catch (Throwable t) {
				MylarStatusHandler.fail(t, "notification failed for: " + listener, false);
			}
		}	
	}
}

