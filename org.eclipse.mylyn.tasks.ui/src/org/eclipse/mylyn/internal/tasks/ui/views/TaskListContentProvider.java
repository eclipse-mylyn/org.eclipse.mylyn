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

package org.eclipse.mylar.internal.tasks.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylar.internal.tasks.ui.TaskListPreferenceConstants;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.core.TaskArchive;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * Provides custom content for the task list, e.g. guaranteed visibility of some
 * elements, ability to suppress containers showing if nothing should show under
 * them.
 * 
 * TODO: move to viewer filter architecture?
 * 
 * @author Mik Kersten
 */
public class TaskListContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	protected final TaskListView view;

	public TaskListContentProvider(TaskListView view) {
		this.view = view;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		this.view.expandToActiveTasks();
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		if (parent.equals(this.view.getViewSite())) {
			return applyFilter(TasksUiPlugin.getTaskListManager().getTaskList().getRootElements()).toArray();
		}
		return getChildren(parent);
	}

	public Object getParent(Object child) {
		if (child instanceof ITask) {
			if (((ITask) child).getParent() != null) {
				return ((ITask) child).getParent();
			} else {
				return ((ITask) child).getContainer();
			}
		}
		return null;
	}

	public Object[] getChildren(Object parent) {
		return getFilteredChildrenFor(parent).toArray();
	}

	/**
	 * NOTE: If parent is an ITask, this method checks if parent has unfiltered
	 * children (see bug 145194).
	 */
	public boolean hasChildren(Object parent) {
		if (parent instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery t = (AbstractRepositoryQuery) parent;
			Set<AbstractQueryHit> hits = t.getHits();
			// TODO: should provide hasHits() method!
			return hits != null && hits.size() > 0;
		} else if (parent instanceof AbstractTaskContainer) {
			AbstractTaskContainer cat = (AbstractTaskContainer) parent;
			// TODO: should provide hasChildren method!
			return cat.getChildren() != null && cat.getChildren().size() > 0;
		} else if (parent instanceof ITask) {
			return taskHasUnfilteredChildren((ITask) parent);
		} else if (parent instanceof AbstractQueryHit) {
			if (((AbstractQueryHit) parent).getCorrespondingTask() != null) {
				return taskHasUnfilteredChildren(((AbstractQueryHit) parent).getCorrespondingTask());
			} else {
				return false;
			}
		}
		return false;
	}

	private boolean taskHasUnfilteredChildren(ITask parent) {
		boolean filterSubtasks = TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(TaskListPreferenceConstants.FILTER_SUBTASKS);
		if(filterSubtasks) return false;
		Set<ITask> children = parent.getChildren();
		if (children != null) {
			for (ITask task : children) {
				if (!filter(parent, task)) {
					return true;
				}
			}
		}
		return false;
	}

	protected List<ITaskListElement> applyFilter(Set<ITaskListElement> roots) {
		String filterText = (this.view.getFilteredTree().getFilterControl()).getText();
		if (containsNoFilterText(filterText)) {
			List<ITaskListElement> filteredRoots = new ArrayList<ITaskListElement>();
			for (ITaskListElement element : roots) {
				if (element instanceof ITask) {
					if (!filter(null, element)) {
						filteredRoots.add(element);
					}
				} else if (element instanceof AbstractRepositoryQuery) {
					if (selectQuery((AbstractRepositoryQuery) element)) {
						filteredRoots.add(element);
					}
				} else if (element instanceof AbstractTaskContainer) {
					if (selectContainer((AbstractTaskContainer) element)) {
						filteredRoots.add(element);
					}
				}
			}
			return filteredRoots;
		} else {
			return new ArrayList<ITaskListElement>(roots);
		}
	}
	
	/**
	 * See bug 109693
	 */
	private boolean containsNoFilterText(String filterText) {
		return filterText == null || filterText.length() == 0;
	}

	// TODO: should only know about containers, not queries
	private boolean selectQuery(AbstractRepositoryQuery query) {
		Set<AbstractQueryHit> hits = query.getHits();
		if (hits.size() == 0) {
			return true;
		}
		for (AbstractQueryHit element : hits) {
			if (!filter(query, element)) {
				return true;
			}
		}
		return false;
	}

	private boolean selectContainer(AbstractTaskContainer container) {
		if (filter(null, container) && !shouldAlwaysShow(container)) {
			return false;
		}

		Set<ITask> children = container.getChildren();
		if (children.size() == 0) {
			return true;
		}
		for (ITaskListElement child : children) {
			if (!filter(container, child)) {
				return true;
			}
		}
		return false;
	}

	private boolean shouldAlwaysShow(AbstractTaskContainer container) {
		for (ITask task : container.getChildren()) {
			if (shouldAlwaysShow(container, task)) {
				if (container instanceof TaskArchive) {
					if (TasksUiPlugin.getTaskListManager().getTaskList().getContainerForHandle(
							task.getHandleIdentifier()) == null
							&& TasksUiPlugin.getTaskListManager().getTaskList().getQueriesForHandle(
									task.getHandleIdentifier()).isEmpty()) {
						return true;
					}
				} else {
					return true;
				}
			}
		}
		return false;
	}

	private boolean shouldAlwaysShow(Object parent, ITask task) {
		for (AbstractTaskListFilter filter : this.view.getFilters()) {
			if (filter.shouldAlwaysShow(parent, task, !TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(TaskListPreferenceConstants.FILTER_SUBTASKS))) {
				return true;
			}
		}
		return false;
	}

	private List<ITaskListElement> getFilteredChildrenFor(Object parent) {
		if (containsNoFilterText((this.view.getFilteredTree().getFilterControl()).getText())) {
			List<ITaskListElement> children = new ArrayList<ITaskListElement>();
			if (parent instanceof AbstractTaskContainer && ((AbstractTaskContainer) parent).isLocal()) {
				if (filter(null, parent)) {
					if (((AbstractTaskContainer) parent) instanceof TaskArchive) {
						for (ITask task : ((AbstractTaskContainer) parent).getChildren()) {
							if (shouldAlwaysShow(parent, task)) {
								// TODO: archive logic?
								if (TasksUiPlugin.getTaskListManager().getTaskList().getQueryHit(
										task.getHandleIdentifier()) == null) {
									children.add(task);
								}
							}
						}
						return children;
					}
				}
				Set<ITask> parentsTasks = ((AbstractTaskContainer) parent).getChildren();
				for (ITaskListElement element : parentsTasks) {
					if (!filter(parent, element)) {
						children.add(element);
					}
				}
				return children;
			} else if (parent instanceof AbstractRepositoryQuery) {
				for (ITaskListElement element : ((AbstractRepositoryQuery) parent).getHits()) {
					if (!filter(parent, element)) {
						children.add(element);
					}
				}
				return children;
			} else if (parent instanceof Task) {
				Set<ITask> subTasks = ((Task) parent).getChildren();
				for (ITask t : subTasks) {
					if (!filter(parent, t)) {
						children.add(t);
					}
				}
				return children;
			} else if (parent instanceof AbstractQueryHit) {
				AbstractRepositoryTask task = ((AbstractQueryHit) parent).getCorrespondingTask();
				if (task != null) {
					for (ITask t : task.getChildren()) {
						if (!filter(parent, t)) {
							children.add(t);
						}
					}
				}
				return children;
			}
		} else {
			List<ITaskListElement> children = new ArrayList<ITaskListElement>();
			if (parent instanceof AbstractRepositoryQuery) {
				children.addAll(((AbstractRepositoryQuery) parent).getHits());
				return children;
			} else if (parent instanceof AbstractTaskContainer) {
				children.addAll(((AbstractTaskContainer) parent).getChildren());
				return children;
			} else if (parent instanceof Task) {
				children.addAll(((Task) parent).getChildren());
				return children;
			}
		}
		return Collections.emptyList();
	}

	private boolean filter(Object parent, Object object) {
		for (AbstractTaskListFilter filter : this.view.getFilters()) {
			if (!filter.select(parent, object)) {
				return true;
			}
		}
		return false;
	}

}
