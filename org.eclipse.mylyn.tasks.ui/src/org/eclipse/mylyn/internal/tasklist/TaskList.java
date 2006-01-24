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
/*
 * Created on Dec 22, 2004
 */
package org.eclipse.mylar.internal.tasklist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.mylar.internal.tasklist.ui.ITaskListElement;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskCategory;
import org.eclipse.mylar.tasklist.IRepositoryQuery;

/**
 * @author Mik Kersten
 */
public class TaskList implements Serializable {

	private static final long serialVersionUID = 3618984485791021105L;

	private List<ITask> rootTasks = new ArrayList<ITask>();

	private List<ITaskCategory> categories = new ArrayList<ITaskCategory>();

	private List<IRepositoryQuery> queries = new ArrayList<IRepositoryQuery>();

	private transient List<ITask> activeTasks = new ArrayList<ITask>();

	void addRootTask(ITask task) {
		rootTasks.add(task);
	}

	void removeFromRoot(ITask task) {
		rootTasks.remove(task);
	}

	void addCategory(ITaskCategory cat) {
		categories.add(cat);
	}

	void addQuery(IRepositoryQuery query) {
		queries.add(query);
	}

	/**
	 * XXX Only public so that other externalizers can use it
	 */
	public void internalAddCategory(ITaskCategory cat) {
		categories.add(cat);
	}

	/**
	 * XXX Only public so that other externalizers can use it
	 */
	public void internalAddQuery(IRepositoryQuery query) {
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

	void deleteCategory(ITaskCategory category) {
		categories.remove(category);
	}

	void deleteQuery(IRepositoryQuery query) {
		queries.remove(query);
	}

	public ITask getTaskForHandle(String handle, boolean lookInArchives) {
		ITask foundTask = null;
		for (ITaskCategory cat : categories) {
			if (!lookInArchives && cat.isArchive())
				continue;
			if ((foundTask = findTaskHelper(cat.getChildren(), handle)) != null) {
				return foundTask;
			}
		}
		for (IRepositoryQuery query : queries) {
			if ((foundTask = findTaskHelper(query.getHits(), handle)) != null) {
				return foundTask;
			}
		}
		return findTaskHelper(rootTasks, handle);
	}

	private IQueryHit findQueryHitHelper(List<? extends ITaskListElement> elements, String handle) {
		if (handle == null)
			return null;
		for (ITaskListElement element : elements) {
			if (element instanceof IQueryHit) {
				IQueryHit hit = (IQueryHit) element;
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
			} else if (element instanceof IQueryHit) {
				IQueryHit hit = (IQueryHit) element;
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

	public List<ITask> getRootTasks() {
		return rootTasks;
	}

	public List<ITaskCategory> getCategories() {
		return categories;
	}

	public List<ITaskCategory> getUserCategories() {
		List<ITaskCategory> included = new ArrayList<ITaskCategory>();
		for (ITaskCategory category : categories) {
			if (!category.getDescription().endsWith(DelegatingLocalTaskExternalizer.LABEL_AUTOMATIC)) {
				included.add(category);
			}
		}
		return included;
	}

	public List<IRepositoryQuery> getQueries() {
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
			if (task.participatesInTaskHandles()) {
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
		for (ITaskCategory cat : categories)
			roots.add(cat);
		for (IRepositoryQuery query : queries)
			roots.add(query);
		return roots;
	}

	public Set<ITask> getAllTasks() {
		Set<ITask> allTasks = new HashSet<ITask>();
		allTasks.addAll(rootTasks);
		for (ITaskCategory cat : categories) {
			allTasks.addAll(cat.getChildren());
		}
		return allTasks;
	}

	public List<TaskCategory> getTaskCategories() {
		List<TaskCategory> cats = new ArrayList<TaskCategory>();
		for (ITaskCategory cat : categories) {
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
	public IQueryHit getQueryHitForHandle(String handle) {
		IQueryHit foundHit = null;
		for (IRepositoryQuery query : queries) {
			if ((foundHit = findQueryHitHelper(query.getHits(), handle)) != null) {
				return foundHit;
			}
		}
		return foundHit;
	}

	// public void clearActiveTasks() {
	// for (ITask task : activeTasks) {
	// task.setActive(false);
	// }
	// activeTasks.clear();
	// }
}
