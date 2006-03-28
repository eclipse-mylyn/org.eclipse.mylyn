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

package org.eclipse.mylar.internal.tasklist.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.internal.tasklist.ui.AbstractTaskListFilter;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.AbstractTaskContainer;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskListElement;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.Task;
import org.eclipse.mylar.provisional.tasklist.TaskArchive;
import org.eclipse.swt.widgets.Text;

/**
 * TODO: move to viewer filter architecture?
 * 
 * @author Mik Kersten
 */
public class TaskListContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	private final TaskListView view;

	private static class ContentTaskFilter extends AbstractTaskListFilter {
		@Override
		public boolean select(Object element) {
			return true;
		}

		@Override
		public boolean shouldAlwaysShow(ITask task) {
			return super.shouldAlwaysShow(task);
		}
	};

	private ContentTaskFilter contentTaskFilter = new ContentTaskFilter();

	public TaskListContentProvider(TaskListView view) {
		this.view = view;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		this.view.expandToActiveTasks();
	}

	public void dispose() {
		// ignore
	}

	public Object[] getElements(Object parent) {
		if (parent.equals(this.view.getViewSite())) {
			return applyFilter(MylarTaskListPlugin.getTaskListManager().getTaskList().getRootElements()).toArray();
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

	public boolean hasChildren(Object parent) {
		if (parent instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery t = (AbstractRepositoryQuery) parent;
			return t.getHits() != null && t.getHits().size() > 0;
		} else if (parent instanceof AbstractTaskContainer) {
			AbstractTaskContainer cat = (AbstractTaskContainer) parent;
			return cat.getChildren() != null && cat.getChildren().size() > 0;
		} else if (parent instanceof Task) {
			Task t = (Task) parent;
			return t.getChildren() != null && t.getChildren().size() > 0;
		} 
		return false;
	}

	private List<ITaskListElement> applyFilter(Set<ITaskListElement> roots) {
		String filterText = ((Text) this.view.getFilteredTree().getFilterControl()).getText();
		if (containsNoFilterText(filterText)) {
			List<ITaskListElement> filteredRoots = new ArrayList<ITaskListElement>();
			for (ITaskListElement element : roots) {
				if (element instanceof ITask) {
					if (!filter(element)) {
						filteredRoots.add(element);
					}
				} else if (element instanceof AbstractRepositoryQuery) {
					if (selectQuery((AbstractRepositoryQuery)element)) {
						filteredRoots.add(element);
					}
				} else if (element instanceof AbstractTaskContainer) { 
					if (selectContainer((AbstractTaskContainer)element)) {
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
		Set<? extends ITaskListElement> list = cat.getHits();
		if (list.size() == 0) {
			return true;
		}
		for (ITaskListElement element : list) {
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
//		if (container instanceof TaskArchive) {
//			for (ITask task : container.getChildren()) {
//				if (contentTaskFilter.shouldAlwaysShow(task)) {
//					// TODO: archive logic?
//					ITask t = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(task.getHandleIdentifier());
//					if (t == null) {
//						return true;
//					}
//				}
//			}
//			return false;
//		}
		
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
			if (contentTaskFilter.shouldAlwaysShow(task)) {
				if (container instanceof TaskArchive) {
					Set<AbstractQueryHit> existingHits = MylarTaskListPlugin.getTaskListManager().getTaskList().getQueryHitsForHandle(task.getHandleIdentifier());
					if (existingHits.isEmpty()) {
						return true;							
					}
				} else {
					return true;
				}
			}
		} 
		return false;
	}

	private List<Object> getFilteredChildrenFor(Object parent) {
		if (containsNoFilterText(((Text) this.view.getFilteredTree().getFilterControl()).getText())
				|| ((Text) this.view.getFilteredTree().getFilterControl()).getText().startsWith(TaskListView.FILTER_LABEL)) {
			List<Object> children = new ArrayList<Object>();
			if (parent instanceof AbstractTaskContainer && ((AbstractTaskContainer)parent).isLocal()) { 
				if (filter(parent)) {
					if (((AbstractTaskContainer)parent) instanceof TaskArchive) {
						for (ITask task : ((AbstractTaskContainer) parent).getChildren()) { 
							if (contentTaskFilter.shouldAlwaysShow(task)) {
								// TODO: archive logic?
								Set<AbstractQueryHit> existingHits = MylarTaskListPlugin.getTaskListManager().getTaskList().getQueryHitsForHandle(task.getHandleIdentifier());
								if (existingHits.isEmpty()) {
									children.add(task);								
								} 
	//							ITask t = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(
	//									task.getHandleIdentifier());
	//							if (t == null)
	//								children.add(task);
							}
						} 
						return children;
					}
				}
				Set<? extends ITaskListElement> list = ((AbstractTaskContainer) parent).getChildren();
				for (ITaskListElement element : list) {
					if (!filter(element)) {
						children.add(element);
					}
				}
				return children;
			} else if (parent instanceof AbstractRepositoryQuery) {
				Set<? extends ITaskListElement> list = ((AbstractRepositoryQuery) parent).getHits();
				for (ITaskListElement element : list) {
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
}
