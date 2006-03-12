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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.mylar.internal.core.util.MylarStatusHandler;

/**
 * TODO: in need of refactoring since there is duplication between categories and fields.
 * 
 * @author Mik Kersten
 */
public class TaskList {

	public static final String LABEL_ARCHIVE = "Archive (all tasks)";

	public static final String LABEL_ROOT = "Root (automatic)";

	private static List<ITaskListChangeListener> changeListeners = new ArrayList<ITaskListChangeListener>();
	
	private TaskCategory archiveCategory = new TaskCategory(LABEL_ARCHIVE);

	private TaskCategory rootCategory = new TaskCategory(LABEL_ROOT);
	
	private Set<ITaskContainer> categories = new HashSet<ITaskContainer>();

	private Set<ITask> rootTasks = new HashSet<ITask>();

	private List<AbstractRepositoryQuery> queries = new ArrayList<AbstractRepositoryQuery>();

	private List<ITask> activeTasks = new ArrayList<ITask>();
		
	public TaskList() {
		archiveCategory.setIsArchive(true);
		categories.add(archiveCategory);
	}

//	/**
//	 * For testing.
//	 */
//	public void clear() {
//		archiveCategory.getChildren().clear();
//		rootCategory.getChildren().clear();
//		categories.clear();
//		rootTasks.clear();
//		queries.clear();
//		activeTasks.clear();
//	}
	
	public void moveToRoot(ITask task) {
		ITaskContainer currentCategory = task.getCategory();
		if (currentCategory instanceof TaskCategory) {
			((TaskCategory)currentCategory).removeTask(task);
		} 
		internalAddRootTask(task);
		for (ITaskListChangeListener listener : changeListeners) {
			listener.taskMoved(task, currentCategory, getRootCategory());
		}
	}

	public void moveToCategory(TaskCategory toCategory, ITask task) {
		ITaskContainer fromCategory = task.getCategory();
		if (toCategory.equals(getRootCategory())) {
			moveToRoot(task);
		} else {
			removeFromRoot(task);
		}
		if (fromCategory instanceof TaskCategory) {
			((TaskCategory)fromCategory).removeTask(task);
		}
		if (!toCategory.getChildren().contains(task)) {
			toCategory.addTask(task);
		}
		task.setCategory(toCategory);
		for (ITaskListChangeListener listener : changeListeners) {
			listener.taskMoved(task, fromCategory, toCategory);
		}
	}

	public void addCategory(ITaskContainer category) {
		categories.add(category);
		for (ITaskListChangeListener listener : changeListeners) {
			listener.containerAdded(category);
		} 
	}

	public void removeFromCategory(TaskCategory category, ITask task) {
		if (!category.isArchive()) {
			category.removeTask(task);
			task.setCategory(null);
		}
		for (ITaskListChangeListener listener : changeListeners) {
			listener.taskMoved(task, category, null);
		}
	}

	public void removeFromRoot(ITask task) {
		rootTasks.remove(task);
		task.setCategory(archiveCategory);
		
		for (ITaskListChangeListener listener : changeListeners) {
			listener.taskMoved(task, null, null);
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
		deleteTaskHelper(archiveCategory.getChildren(), task);
		boolean deleted = deleteTaskHelper(rootTasks, task);
		task.setCategory(null);
		if (!deleted) {
			for (TaskCategory cat : getTaskCategories()) {
				deleted = deleteTaskHelper(cat.getChildren(), task);
				if (deleted) {
					return;
				}
			}
		}
		for (ITaskListChangeListener listener : changeListeners) {
			listener.taskDeleted(task);
		}
	}

	public void deleteCategory(ITaskContainer category) {
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
			listener.localInfoChanged(task); // to ensure comleted filter
			// notices
		}
	}

	public void addChangeListener(ITaskListChangeListener listener) {
		changeListeners.add(listener);
	}

	public void removeChangeListener(ITaskListChangeListener listener) {
		changeListeners.remove(listener);
	}
	
	public void internalAddRootTask(ITask task) {
		rootTasks.add(task);
		task.setCategory(rootCategory); 
	}

	/**
	 * XXX Only public so that other externalizers can use it
	 */
	public void internalAddCategory(ITaskContainer cat) {
		categories.add(cat);
	}

	/**
	 * XXX Only public so that other externalizers can use it
	 */
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

	public ITask getTaskForHandle(String handle, boolean lookInArchives) {
		ITask foundTask = null;
		for (ITaskContainer cat : categories) {
			if (!lookInArchives && cat.isArchive())
				continue;
			if ((foundTask = findTaskHelper(cat.getChildren(), handle)) != null) {
				return foundTask;
			}
		}
		for (AbstractRepositoryQuery query : queries) {
			if ((foundTask = findTaskHelper(query.getHits(), handle)) != null) {
				return foundTask;
			}
		}
		return findTaskHelper(rootTasks, handle);
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

	private ITask findTaskHelper(Set<? extends ITaskListElement> elements, String handle) {
		if (handle == null)
			return null;
		for (ITaskListElement element : elements) {
			if (element instanceof ITask) {
				if (element.getHandleIdentifier().compareTo(handle) == 0)
					return (ITask) element;
			} else if (element instanceof AbstractQueryHit) {
				AbstractQueryHit hit = (AbstractQueryHit) element;
				if (hit.getHandleIdentifier().compareTo(handle) == 0 && hit.getCorrespondingTask() != null) {
					return hit.getCorrespondingTask();
				}
			}

			// for subtasks
			if (element instanceof ITask) {
				ITask searchTask = (ITask) element;
				ITask t = findTaskHelper(searchTask.getChildren(), handle);
				if (t != null) {
					return t;
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
		return rootTasks;
	}

	public Set<ITaskContainer> getCategories() {
		return categories;
	}

	public List<ITaskContainer> getUserCategories() {
		List<ITaskContainer> included = new ArrayList<ITaskContainer>();
		for (ITaskContainer category : categories) {
			if (!category.getDescription().endsWith(DelegatingTaskExternalizer.LABEL_AUTOMATIC)) {
				included.add(category);
			}
		}
		return included;
	}

	public List<AbstractRepositoryQuery> getQueries() {
		return queries;
	}

	public int findLargestTaskHandle() {
		int max = 0;
		max = Math.max(largestTaskHandleHelper(rootTasks), max);
		for (TaskCategory cat : getTaskCategories()) {
			max = Math.max(largestTaskHandleHelper(cat.getChildren()), max);
		}
		return max;
	}

	private int largestTaskHandleHelper(Set<ITask> tasks) {
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
		// roots.add(archiveCategory);
		for (ITask task : rootTasks)
			roots.add(task);
		for (ITaskContainer cat : categories)
			roots.add(cat);
		for (AbstractRepositoryQuery query : queries)
			roots.add(query);
		return roots;
	}

	public Set<ITask> getAllTasks() {
		Set<ITask> allTasks = new HashSet<ITask>();
		allTasks.addAll(rootTasks);
		for (ITaskContainer container : categories) {
			allTasks.addAll(container.getChildren());
		}
		return allTasks;
	}

	public Set<TaskCategory> getTaskCategories() {
		Set<TaskCategory> cats = new HashSet<TaskCategory>();
		for (ITaskContainer cat : categories) {
			if (cat instanceof TaskCategory) {
				cats.add((TaskCategory) cat);
			}
		}
		return cats;
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
				&& getCategories().iterator().next().equals(archiveCategory) && archiveCategory.getChildren().isEmpty();
		return getAllTasks().size() == 0 && archiveIsEmpty && getQueries().size() == 0;
	}

	public void addTaskToArchive(ITask task) {
		archiveCategory.internalAddTask(task);
	}

	public ITask getTaskFromArchive(String handleIdentifier) {
		for (ITask task : archiveCategory.getChildren()) {
			if (task.getHandleIdentifier().equals(handleIdentifier)) {
				return task;
			}
		}
		return null;
		// return archiveMap.get(handleIdentifier);
	}

	public Set<ITask> getArchiveTasks() {
		return archiveCategory.getChildren();
		// List<ITask> archiveTasks = new ArrayList<ITask>();
		// archiveTasks.addAll(archiveMap.values());
		// return archiveTasks;
	}

	public void setArchiveCategory(TaskCategory category) {
		this.archiveCategory = category;
	}

	/**
	 * For testing.
	 */
	public void clearArchive() {
		archiveCategory.getChildren().clear();
		// archiveMap.clear();
	}

	public TaskCategory getCategoryForHandle(String categoryHandle) {
		for (ITaskContainer cat : categories) {
			if (cat instanceof TaskCategory) {
				if (cat.getHandleIdentifier().equals(categoryHandle)) {
					return (TaskCategory) cat;
				}
			}
		}
		return null;
	}

	public TaskCategory getRootCategory() {
		return rootCategory;
	}

	public TaskCategory getArchiveCategory() {
		return archiveCategory;
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
}
