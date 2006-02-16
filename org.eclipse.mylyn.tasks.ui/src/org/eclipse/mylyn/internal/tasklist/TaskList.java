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

package org.eclipse.mylar.internal.tasklist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author Mik Kersten
 */
public class TaskList implements Serializable {

	private static final long serialVersionUID = 3618984485791021105L;

	private List<ITask> rootTasks = new ArrayList<ITask>();

	private List<ITaskContainer> categories = new ArrayList<ITaskContainer>();

	private List<AbstractRepositoryQuery> queries = new ArrayList<AbstractRepositoryQuery>();

	private transient List<ITask> activeTasks = new ArrayList<ITask>();

	public void internalAddRootTask(ITask task) {
		rootTasks.add(task);
	}

	void removeFromRoot(ITask task) {
		rootTasks.remove(task);
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

	void deleteTask(ITask task) {
		boolean deleted = deleteTaskHelper(rootTasks, task);
		if (!deleted) {
			for (TaskCategory cat : getTaskCategories()) {
				deleted = deleteTaskHelper(cat.getChildren(), task);
				if (deleted) {
					return;
				}
			}
		}
	}

	private boolean deleteTaskHelper(List<ITask> tasks, ITask t) {
		for (ITask task : tasks) {
			if (task.getHandleIdentifier().equals(t.getHandleIdentifier())) {
				tasks.remove(task);
				return true;
			} else {
				if (deleteTaskHelper(task.getChildren(), t))
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

	private AbstractQueryHit findQueryHitHelper(List<? extends ITaskListElement> elements, String handle) {
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

	private ITask findTaskHelper(List<? extends ITaskListElement> elements, String handle) {
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
	 * @return
	 */
	public ITask getActiveTask() {
		if (activeTasks.size() > 0) {
			return activeTasks.get(0);
		} else {
			return null;
		}
	}
	
	public List<ITask> getRootTasks() {
		return rootTasks;
	}

	public List<ITaskContainer> getCategories() {
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

	private int largestTaskHandleHelper(List<ITask> tasks) {
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

	public List<Object> getRoots() {
		List<Object> roots = new ArrayList<Object>();
		for (ITask t : rootTasks)
			roots.add(t);
		for (ITaskContainer cat : categories)
			roots.add(cat);
		for (AbstractRepositoryQuery query : queries)
			roots.add(query);
		return roots;
	}

	public Set<ITask> getAllTasks() {
		Set<ITask> allTasks = new HashSet<ITask>();
		allTasks.addAll(rootTasks);
		for (ITaskContainer cat : categories) {
			allTasks.addAll(cat.getChildren());
		}
		return allTasks;
	}

	public List<TaskCategory> getTaskCategories() {
		List<TaskCategory> cats = new ArrayList<TaskCategory>();
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

	/**
	 * Use to obtain the QueryHit object associated with a particular task
	 * handle if it exists.
	 * 
	 * @param handle
	 *            handle of task
	 * @return IQueryHit corresponding to the first hit found in all queries
	 */
	public AbstractQueryHit getQueryHitForHandle(String handle) {
		AbstractQueryHit foundHit = null;
		for (AbstractRepositoryQuery query : queries) {
			if ((foundHit = findQueryHitHelper(query.getHits(), handle)) != null) {
				return foundHit;
			}
		}
		return foundHit;
	}

	public boolean isEmpty() {
		return getAllTasks().size() == 0 && getCategories().size() == 0 && getQueries().size() == 0;
	}
}
