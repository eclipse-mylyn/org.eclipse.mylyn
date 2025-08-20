/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;

/**
 * Provides custom content for the task list, e.g. guaranteed visibility of some elements, ability to suppress containers showing if nothing
 * should show under them. TODO: move to viewer filter architecture?
 *
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskListContentProvider extends AbstractTaskListContentProvider {

	protected static Object[] EMPTY_ARRRY = {};

	public TaskListContentProvider(AbstractTaskListView taskListView) {
		super(taskListView);
	}

	@Override
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		taskListView.expandToActiveTasks();
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object[] getElements(Object parent) {
		if (parent.equals(taskListView.getViewSite())) {
			return applyFilter(TasksUiPlugin.getTaskList().getRootElements()).toArray();
		}
		return getChildren(parent);
	}

	/**
	 * @return first parent found
	 */
	@Override
	public Object getParent(Object child) {
		// return first parent found, first search within categories then queries
		if (child instanceof ITask task) {
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

	@Override
	public Object[] getChildren(Object parent) {
		return getFilteredChildrenFor(parent).toArray();
	}

	/**
	 * NOTE: If parent is an ITask, this method checks if parent has unfiltered children (see bug 145194).
	 */
	@Override
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
		List<AbstractTaskContainer> filteredRoots = new ArrayList<>();
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
		return filterText == null || filterText.trim().length() == 0;
	}

	private boolean selectContainer(ITaskContainer container) {
		if (filter(null, container)) {
			return false;
		}
		return true;
	}

	protected List<IRepositoryElement> getFilteredChildrenFor(Object parent) {
		if (containsNoFilterText(taskListView.getFilteredTree().getFilterString())) {
			List<IRepositoryElement> children = new ArrayList<>();
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
			List<IRepositoryElement> children = new ArrayList<>();
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
		List<IRepositoryElement> result = new ArrayList<>();
		if (TasksUiPlugin.getDefault().groupSubtasks(parent)) {
			Collection<ITask> parentTasks = parent.getChildren();
			Set<IRepositoryElement> parents = new HashSet<>();
			Set<ITask> children = new HashSet<>();
			// get all children
			for (ITask element : parentTasks) {
				if (element instanceof ITaskContainer) {
					children.addAll(((ITaskContainer) element).getChildren());
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
		boolean notSearching = containsNoFilterText(taskListView.getFilteredTree().getFilterString());
		for (AbstractTaskListFilter filter : taskListView.getFilters()) {
			if (notSearching || filter.applyToFilteredText()) {
				if (!filter.select(parent, object)) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean filter(TreePath path, Object parent, Object object) {
		boolean emptyFilterText = containsNoFilterText(taskListView.getFilteredTree().getFilterString());
		for (AbstractTaskListFilter filter : taskListView.getFilters()) {
			if (emptyFilterText || filter.applyToFilteredText()) {
				if (filter instanceof TaskListInterestFilter) {
					if (!((TaskListInterestFilter) filter).select(path.getLastSegment(), object)) {
						return true;
					}
				} else if (!filter.select(parent, object)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isSearching() {
		return !containsNoFilterText(taskListView.getFilteredTree().getFilterString());
	}

	@Override
	public Object[] getChildren(TreePath parentPath) {
		Object parent = parentPath.getLastSegment();
		if (PresentationFilter.getInstance().isFilterNonMatching()) {
			ITaskContainer container = (ITaskContainer) parentPath.getFirstSegment();
			if (container instanceof IRepositoryQuery || container instanceof UnmatchedTaskContainer) {
				if (parent instanceof ITask) {
					if (container instanceof RepositoryQuery && !TasksUiPlugin.getDefault().groupSubtasks(container)) {
						return EMPTY_ARRRY;
					}
				}
				List<IRepositoryElement> children = getFilteredChildrenFor(parent);
				if (!isSearching()) {
					if (parent instanceof ITask) {
						// scope subtasks by query results
						for (Iterator<IRepositoryElement> it = children.iterator(); it.hasNext();) {
							IRepositoryElement element = it.next();
							if (!container.getChildren().contains(element)) {
								it.remove();
							}
						}
					}
				}
				return children.toArray();
			}
		}
		return getFilteredChildrenFor(parent).toArray();
	}

	@Override
	public boolean hasChildren(TreePath path) {
		return getChildren(path).length > 0;
	}

	@Override
	public TreePath[] getParents(Object element) {
		return new TreePath[0];
	}

}
