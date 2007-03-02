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
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.DateRangeActivityDelegate;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.core.TaskArchive;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * TODO: move to viewer filter architecture?
 * 
 * @author Mik Kersten
 */
public class TaskListContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	public static final String ID = "tasklist.mode.categorized";
	
	protected final TaskListView view;
	
	private final String providerLabel = "Categorized";

	protected String id;
	
	public TaskListContentProvider(TaskListView view) {
		this.view = view;
		this.id = ID;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		this.view.expandToActiveTasks();
	}

	public void dispose() {
		// ignore
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
			Set<AbstractQueryHit> hits = t.getHits(); // FIXME should provide
														// hasHits() method!
			return hits != null && hits.size() > 0;
		} else if (parent instanceof AbstractTaskContainer) {
			AbstractTaskContainer cat = (AbstractTaskContainer) parent;
			return cat.getChildren() != null && cat.getChildren().size() > 0; // FIXME
																				// should
																				// provide
																				// hasChildren
																				// method!
		} else if (parent instanceof ITask) {
			return taskHasUnfilteredChildren((ITask) parent);
		}
		return false;
	}

	private boolean taskHasUnfilteredChildren(ITask parent) {
		Set<ITask> children = parent.getChildren();
		if (children != null) {
			for (ITask task : children) {
				if (!filter(task)) {
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
					if (!filter(element)) {
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

	private boolean selectQuery(AbstractRepositoryQuery cat) {
		Set<AbstractQueryHit> hits = cat.getHits();
		if (hits.size() == 0) {
			return true;
		}
		for (AbstractQueryHit element : hits) {
			if (!filter(element)) {
				return true;
			}
		}
		return false;
	}

	private boolean selectContainer(AbstractTaskContainer container) {
		if (filter(container) && !shouldAlwaysShow(container)) {
			return false;
		}

		Set<ITask> children = container.getChildren();
		if (children.size() == 0) {
			return true;
		}
		for (ITaskListElement element : children) {
			if (!filter(element)) {
				return true;
			}
		}
		return false;
	}

	private boolean shouldAlwaysShow(AbstractTaskContainer container) {
		for (ITask task : container.getChildren()) {
			if (shouldAlwaysShow(task)) {
				if (container instanceof TaskArchive) {
					if (TasksUiPlugin.getTaskListManager().getTaskList().getContainerForHandle(
							task.getHandleIdentifier()) == null
							&& TasksUiPlugin.getTaskListManager().getTaskList().getQueriesForHandle(
									task.getHandleIdentifier()).isEmpty()) {
						// if
						// (TasksUiPlugin.getTaskListManager().getTaskList().getQueryHit(task.getHandleIdentifier())
						// != null) {
						return true;
					}
				} else {
					return true;
				}
			}
		}
		return false;
	}

	private boolean shouldAlwaysShow(ITask task) {
		for (AbstractTaskListFilter filter : this.view.getFilters()) {
			if (filter.shouldAlwaysShow(task)) {
				return true;
			}
		}
		return false;
	}

	private List<Object> getFilteredChildrenFor(Object parent) {
		if (containsNoFilterText((this.view.getFilteredTree().getFilterControl()).getText())) {
			List<Object> children = new ArrayList<Object>();
			if (parent instanceof AbstractTaskContainer && ((AbstractTaskContainer) parent).isLocal()) {
				if (filter(parent)) {
					if (((AbstractTaskContainer) parent) instanceof TaskArchive) {
						for (ITask task : ((AbstractTaskContainer) parent).getChildren()) {
							if (shouldAlwaysShow(task)) {
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
					if(element instanceof DateRangeActivityDelegate) {
						element = ((DateRangeActivityDelegate)element).getCorrespondingTask();
					}
					if (!filter(element)) {
						children.add(element);
					}
				}
				return children;
			} else if (parent instanceof AbstractRepositoryQuery) {
				for (ITaskListElement element : ((AbstractRepositoryQuery) parent).getHits()) {
					if (!filter(element)) {
						children.add(element);
					}
				}
				return children;
			} else if (parent instanceof Task) {
				Set<ITask> subTasks = ((Task) parent).getChildren();
				for (ITask t : subTasks) {
					if (!filter(t)) {
						children.add(t);
					}
				}
				return children;
			}
		} else {
			List<Object> children = new ArrayList<Object>();
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
		return new ArrayList<Object>();
	}

	private boolean filter(Object obj) {
		for (AbstractTaskListFilter filter : this.view.getFilters()) {
			if (!filter.select(obj)) {
				return true;
			}
		}
		return false;
	}

	public String getLabel() {
		return providerLabel;
	}
	
	public String getId() {
		return id;
	}
}
