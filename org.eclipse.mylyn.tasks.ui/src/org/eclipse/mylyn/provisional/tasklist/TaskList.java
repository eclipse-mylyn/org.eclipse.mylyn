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
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * TODO: in need of refactoring since there is duplication between categories and fields.
 * 
 * @author Mik Kersten
 */
public class TaskList {

	public static final String LABEL_ARCHIVE = "Archive (automatic)";

	public static final String LABEL_ROOT = "Root (automatic)";
	
	private TaskCategory archiveCategory = new TaskCategory(LABEL_ARCHIVE);

	private TaskCategory rootCategory = new TaskCategory(LABEL_ROOT);
	
	private Set<ITaskContainer> categories = new HashSet<ITaskContainer>();

	private Set<ITask> rootTasks = new HashSet<ITask>();

	private List<AbstractRepositoryQuery> queries = new ArrayList<AbstractRepositoryQuery>();

	private transient List<ITask> activeTasks = new ArrayList<ITask>();

	public TaskList() {
		archiveCategory.setIsArchive(true);
		categories.add(archiveCategory);
	}

	public void internalAddRootTask(ITask task) {
		rootTasks.add(task);
		task.setCategory(rootCategory); 
	}

	void removeFromRoot(ITask task) {
		rootTasks.remove(task);
		task.setCategory(archiveCategory);
	}

	void addCategory(ITaskContainer cat) {
		categories.add(cat);
	}

	void addQuery(AbstractRepositoryQuery query) {
		queries.add(query);
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

	/**
	 * TODO: refactor around querying containers for their tasks
	 */
	void deleteTask(ITask task) {
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

	void deleteCategory(ITaskContainer category) {
		categories.remove(category);
	}

	void deleteQuery(AbstractRepositoryQuery query) {
		queries.remove(query);
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
			if (task.isLocal()) {
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

	public void clear() {
		activeTasks.clear();
		categories.clear();
		rootTasks.clear();
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

}
