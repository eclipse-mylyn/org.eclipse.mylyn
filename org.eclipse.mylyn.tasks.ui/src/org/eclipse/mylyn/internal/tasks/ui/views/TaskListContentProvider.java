/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkingSet;

/**
 * Provides custom content for the task list, e.g. guaranteed visibility of some elements, ability to suppress
 * containers showing if nothing should show under them.
 * 
 * TODO: move to viewer filter architecture?
 * 
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskListContentProvider extends AbstractTaskListContentProvider {

	public TaskListContentProvider(TaskListView taskListView) {
		super(taskListView);
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		this.taskListView.expandToActiveTasks();
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		if (parent.equals(this.taskListView.getViewSite())) {
			return applyFilter(TasksUiPlugin.getTaskListManager().getTaskList().getRootElements()).toArray();
		}
		return getChildren(parent);
	}

	/**
	 * @return first parent found
	 */
	public Object getParent(Object child) {
		// Return first parent found, first search within queries then categories.
		if (child instanceof AbstractTask) {
			Set<AbstractRepositoryQuery> queries = TasksUiPlugin.getTaskListManager()
					.getTaskList()
					.getQueriesForHandle(((AbstractTask) child).getHandleIdentifier());
			if (queries.size() > 0) {
				return queries.toArray()[0];
			}

			AbstractTaskContainer container = TasksUiPlugin.getTaskListManager().getTaskList().getContainerForHandle(
					((AbstractTask) child).getHandleIdentifier());

			if (container != null) {
				return container;
			}

		}
		// no parent found
		return null;
	}


	public Object[] getChildren(Object parent) {
		return getFilteredChildrenFor(parent).toArray();
	}

	/**
	 * NOTE: If parent is an ITask, this method checks if parent has unfiltered children (see bug 145194).
	 */
	public boolean hasChildren(Object parent) {
		Object[] children = getChildren(parent);
		return children != null && children.length > 0;

//		if (parent instanceof AbstractRepositoryQuery) {
//			AbstractRepositoryQuery query = (AbstractRepositoryQuery) parent;
//			return !getFilteredChildrenFor(query).isEmpty();
//			//return !query.isEmpty();
//		} else if (parent instanceof AbstractTask) {
//			return taskHasUnfilteredChildren((AbstractTask) parent);
//		} else if (parent instanceof AbstractTaskContainer) {
//			AbstractTaskContainer container = (AbstractTaskContainer) parent;
//			return !getFilteredChildrenFor(container).isEmpty();
//			//return !container.getChildren().isEmpty();
//		}
//		return false;
	}

	protected List<AbstractTaskContainer> applyFilter(Set<AbstractTaskContainer> roots) {
		String filterText = (taskListView.getFilteredTree().getFilterControl()).getText();
		if (containsNoFilterText(filterText)) {
			List<AbstractTaskContainer> filteredRoots = new ArrayList<AbstractTaskContainer>();
			for (AbstractTaskContainer element : roots) {
				// NOTE: tasks can no longer appear as root elements
				if (selectContainer(element)) {
					filteredRoots.add(element);
				}
			}
			return filteredRoots;
		} else {
			// only match working sets when filter is on
			Set<IWorkingSet> workingSets = TaskListView.getActiveWorkingSets();
			Set<AbstractTaskContainer> workingSetContainers = new HashSet<AbstractTaskContainer>();
			if (workingSets.isEmpty()) {
				return new ArrayList<AbstractTaskContainer>(roots);
			} else {
				for (IWorkingSet workingSet : workingSets) {
					IAdaptable[] elements = workingSet.getElements();
					for (IAdaptable adaptable : elements) {
						if (adaptable instanceof AbstractTaskContainer && roots.contains(adaptable)) {
							workingSetContainers.add((AbstractTaskContainer) adaptable);
						}
					}
				}
				return new ArrayList<AbstractTaskContainer>(workingSetContainers);
			}
		}
	}

	/**
	 * See bug 109693
	 */
	private boolean containsNoFilterText(String filterText) {
		return filterText == null || filterText.length() == 0;
	}

	private boolean selectContainer(AbstractTaskContainer container) {
//		if (container instanceof ScheduledTaskContainer) {
//			ScheduledTaskContainer scheduleContainer = (ScheduledTaskContainer) container;
//			if (TasksUiPlugin.getTaskActivityManager().isWeekDay(scheduleContainer)
//					&& (scheduleContainer.isPresent() || scheduleContainer.isFuture())) {
//				return true;
//			} else if (taskListView.isFocusedMode()) {
//				return false;
//			}
//		}

		if (filter(null, container)) {
			return false;
		}
		return true;
	}

	private List<AbstractTaskContainer> getFilteredChildrenFor(Object parent) {
		if (containsNoFilterText((this.taskListView.getFilteredTree().getFilterControl()).getText())) {
			List<AbstractTaskContainer> children = new ArrayList<AbstractTaskContainer>();
			if (parent instanceof AbstractTask) {
				Set<AbstractTask> subTasks = ((AbstractTask) parent).getChildren();
				for (AbstractTask t : subTasks) {
					if (!filter(parent, t)) {
						children.add(t);
					}
				}
				return children;
			} else if (parent instanceof AbstractTaskContainer) {
				return getFilteredRootChildren((AbstractTaskContainer) parent);
			}
		} else {
			List<AbstractTaskContainer> children = new ArrayList<AbstractTaskContainer>();
			if (parent instanceof AbstractTaskContainer) {
				children.addAll(((AbstractTaskContainer) parent).getChildren());
				return children;
			}
		}
		return Collections.emptyList();
	}

	/**
	 * @return all children who aren't already revealed as a sub task
	 */
	private List<AbstractTaskContainer> getFilteredRootChildren(AbstractTaskContainer parent) {
		List<AbstractTaskContainer> result = new ArrayList<AbstractTaskContainer>();
		if (TasksUiPlugin.getDefault().groupSubtasks(parent)) {
			Set<AbstractTask> parentTasks = parent.getChildren();
			Set<AbstractTaskContainer> parents = new HashSet<AbstractTaskContainer>();
			Set<AbstractTask> children = new HashSet<AbstractTask>();
			// get all children
			for (AbstractTask element : parentTasks) {
				for (AbstractTask abstractTask : element.getChildren()) {
					children.add(abstractTask);
				}
			}
			for (AbstractTask task : parentTasks) {
				if (!filter(parent, task) && !children.contains(task)) {
					parents.add(task);
				}
			}
			result.addAll(parents);
		} else {
			for (AbstractTaskContainer element : parent.getChildren()) {
				if (!filter(parent, element)) {
					result.add(element);
				}
			}
		}
		return result;
	}

	protected boolean filter(Object parent, Object object) {
		for (AbstractTaskListFilter filter : this.taskListView.getFilters()) {
			if (!filter.select(parent, object)) {
				return true;
			}
		}
		return false;
	}

}
