/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;

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
			return applyFilter(TasksUiPlugin.getTaskList().getRootElements()).toArray();
		}
		return getChildren(parent);
	}

	/**
	 * @return first parent found
	 */
	public Object getParent(Object child) {
		// return first parent found, first search within categories then queries
		if (child instanceof ITask) {
			ITask task = (ITask) child;
			AbstractTaskCategory parent = TaskCategory.getParentTaskCategory(task);
			if (parent != null) {
				return parent;
			}

			Set<AbstractTaskContainer> parents = ((AbstractTask) task).getParentContainers();
			Iterator<AbstractTaskContainer> it = parents.iterator();
			if (it.hasNext()) {
				return parents.iterator().next();
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
		List<AbstractTaskContainer> filteredRoots = new ArrayList<AbstractTaskContainer>();
		for (AbstractTaskContainer element : roots) {
			// NOTE: tasks can no longer appear as root elements
			if (selectContainer(element)) {
				filteredRoots.add(element);
			}
		}
		return filteredRoots;
	}

	/**
	 * See bug 109693
	 */
	private boolean containsNoFilterText(String filterText) {
		return filterText == null || filterText.length() == 0;
	}

	private boolean selectContainer(ITaskContainer container) {
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

	private List<IRepositoryElement> getFilteredChildrenFor(Object parent) {
		if (containsNoFilterText((this.taskListView.getFilteredTree().getFilterControl()).getText())) {
			List<IRepositoryElement> children = new ArrayList<IRepositoryElement>();
			if (parent instanceof ITask) {
				Collection<ITask> subTasks = ((AbstractTask) parent).getChildren();
				for (ITask task : subTasks) {
					if (!filter(parent, task)) {
						children.add(task);
					}
				}
				return children;
			} else if (parent instanceof ITaskContainer) {
				return getFilteredRootChildren((ITaskContainer) parent);
			}
		} else {
			List<IRepositoryElement> children = new ArrayList<IRepositoryElement>();
			if (parent instanceof ITaskContainer) {
				children.addAll(((ITaskContainer) parent).getChildren());
				return children;
			}
		}
		return Collections.emptyList();
	}

	/**
	 * @return all children who aren't already revealed as a sub task
	 */
	private List<IRepositoryElement> getFilteredRootChildren(ITaskContainer parent) {
		List<IRepositoryElement> result = new ArrayList<IRepositoryElement>();
		if (TasksUiPlugin.getDefault().groupSubtasks(parent)) {
			Collection<ITask> parentTasks = parent.getChildren();
			Set<IRepositoryElement> parents = new HashSet<IRepositoryElement>();
			Set<ITask> children = new HashSet<ITask>();
			// get all children
			for (ITask element : parentTasks) {
				if (element instanceof ITaskContainer) {
					for (ITask abstractTask : ((ITaskContainer) element).getChildren()) {
						children.add(abstractTask);
					}
				}
			}
			for (ITask task : parentTasks) {
				if (!filter(parent, task) && !children.contains(task)) {
					parents.add(task);
				}
			}
			result.addAll(parents);
		} else {
			for (IRepositoryElement element : parent.getChildren()) {
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
