/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.workbench.DelayedRefreshJob;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnsubmittedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskListChangeAdapter;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.util.TreeWalker;
import org.eclipse.mylyn.internal.tasks.ui.util.TreeWalker.TreeVisitor;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

public final class TaskListRefreshJob extends DelayedRefreshJob {
	private final AbstractTaskListView taskListView;

	private final ITaskListChangeListener TASKLIST_CHANGE_LISTENER = new TaskListChangeAdapter() {

		@Override
		public void containersChanged(final Set<TaskContainerDelta> deltas) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					for (TaskContainerDelta taskContainerDelta : deltas) {
						if (taskListView.isScheduledPresentation()) {
							// TODO: implement refresh policy for scheduled presentation
							refresh();
						} else {
							switch (taskContainerDelta.getKind()) {
							case ROOT:
								refresh();
								break;
							case ADDED:
							case REMOVED:
								if (isFilteredContainer(taskContainerDelta)) {
									// container may have changed visibility, refresh root
									refresh();
								} else {
									if (taskContainerDelta.getElement() != null) {
										refreshElement(taskContainerDelta.getElement());
									}
									if (taskContainerDelta.getParent() != null) {
										refreshElement(taskContainerDelta.getParent());
									} else {
										// element was added/removed from the root
										refresh();
									}
								}
								break;
							case CONTENT:
								refreshElement(taskContainerDelta.getElement());
							}

						}
					}
				}

				private boolean isFilteredContainer(TaskContainerDelta taskContainerDelta) {
					ITaskContainer parent = taskContainerDelta.getParent();
					return parent instanceof UnsubmittedTaskContainer || parent instanceof UnmatchedTaskContainer
							|| parent instanceof UncategorizedTaskContainer;
				}
			});
		}
	};

	public TaskListRefreshJob(AbstractTaskListView taskListView, TreeViewer treeViewer, String name) {
		super(treeViewer, name);
		this.taskListView = taskListView;
		TasksUiInternal.getTaskList().addChangeListener(TASKLIST_CHANGE_LISTENER);
	}

	@Override
	protected void doRefresh(Object[] items) {
		TreePath selection = preserveSelection();

		if (items == null) {
			viewer.refresh(true);
		} else if (items.length > 0) {
			try {
				if (taskListView.isFocusedMode()) {
					Set<Object> children = new HashSet<Object>(Arrays.asList(items));
					Set<AbstractTaskContainer> parents = new HashSet<AbstractTaskContainer>();
					for (Object item : items) {
						if (item instanceof AbstractTask) {
							parents.addAll(((AbstractTask) item).getParentContainers());
						}
					}
					// 1. refresh parents
					children.removeAll(parents);
					for (AbstractTaskContainer parent : parents) {
						viewer.refresh(parent, false);
						// only refresh label of parent
						viewer.update(parent, null);
					}
					// 2. refresh children
					for (Object item : children) {
						viewer.refresh(item, true);
					}
					// 3. update states of all changed items
					for (Object item : items) {
						updateExpansionState(item);
					}
				} else {
					Set<AbstractTaskContainer> parents = new HashSet<AbstractTaskContainer>();
					for (Object item : items) {
						if (item instanceof AbstractTask) {
							parents.addAll(((AbstractTask) item).getParentContainers());
						}
						viewer.refresh(item, true);
						updateExpansionState(item);
					}
					// refresh labels of parents for task activation or incoming indicators
					for (AbstractTaskContainer parent : parents) {
						// only refresh label
						viewer.update(parent, null);
					}
				}
			} catch (SWTException e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to refresh viewer: " //$NON-NLS-1$
						+ viewer, e));
			}
		}

		taskListView.updateToolTip(false);
		restoreSelection(selection);
	}

	private TreePath preserveSelection() {
		if (viewer instanceof TreeViewer) {
			TreeViewer treeViewer = (TreeViewer) viewer;
			// in case the refresh removes the currently selected item, 
			// remember the next item in the tree to restore the selection
			// TODO: consider making this optional
			TreeItem[] selection = treeViewer.getTree().getSelection();
			if (selection.length > 0) {
				TreeWalker treeWalker = new TreeWalker(treeViewer);
				return treeWalker.walk(new TreeVisitor() {
					@Override
					public boolean visit(Object object) {
						return true;
					}
				}, selection[selection.length - 1]);
			}
		}
		return null;
	}

	private void restoreSelection(TreePath treePath) {
		if (treePath != null) {
			ISelection newSelection = viewer.getSelection();
			if (newSelection == null || newSelection.isEmpty()) {
				viewer.setSelection(new TreeSelection(treePath), true);
			}
		}
	}

	protected void updateExpansionState(Object item) {
		if (taskListView.isFocusedMode() && taskListView.isAutoExpandMode()) {
			taskListView.getViewer().expandToLevel(item, 3);
		}
	}

	public void dispose() {
		TasksUiInternal.getTaskList().removeChangeListener(TASKLIST_CHANGE_LISTENER);
	}
}