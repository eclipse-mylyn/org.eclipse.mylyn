/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.context.ui.AbstractFocusViewAction;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.mylyn.internal.provisional.commons.ui.IFilteredTreeListener;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListInterestFilter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListInterestSorter;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.ui.IViewPart;

/**
 * TODO: abuses contract from super class
 * 
 * @author Mik Kersten
 */
@SuppressWarnings("restriction")
public class FocusTaskListAction extends AbstractFocusViewAction implements IFilteredTreeListener {

	private final TaskListInterestFilter taskListInterestFilter = new TaskListInterestFilter();

	private final TaskListInterestSorter taskListInterestSorter = new TaskListInterestSorter();

	private Set<AbstractTaskListFilter> previousFilters = new HashSet<AbstractTaskListFilter>();

	private ViewerSorter previousSorter;

	public FocusTaskListAction() {
		super(new InterestFilter(), false, true, false);
	}

	@Override
	public void init(IViewPart view) {
		super.init(view);
		IViewPart part = super.getPartForAction();
		if (part instanceof TaskListView) {
			((TaskListView) part).getFilteredTree().getRefreshPolicy().addListener(this);
		}

		update();
		((TaskListView) part).getFilteredTree().setShowProgress(super.isChecked());
	}

	@Override
	protected boolean updateEnablementWithContextActivation() {
		return false;
	}

	@Override
	public void dispose() {
		super.dispose();
		IViewPart part = super.getPartForAction();
		if (part instanceof TaskListView) {
			((TaskListView) part).getFilteredTree().getRefreshPolicy().removeListener(this);
		}
	}

	@Override
	public void run(IAction action) {
		super.run(action);
//		IViewPart part = super.getPartForAction();
//		if (part instanceof TaskListView) {
//			((TaskListView) part).getFilteredTree().setShowProgress(super.isChecked());
//		}
	}

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		IViewPart part = super.getPartForAction();
		if (part instanceof TaskListView) {
			viewers.add(((TaskListView) part).getViewer());
		}
		return viewers;
	}

	@Override
	protected boolean installInterestFilter(StructuredViewer viewer) {
		IViewPart part = super.getPartForAction();
		if (part instanceof TaskListView) {
			final TaskListView taskListView = (TaskListView) part;
			TasksUiInternal.preservingSelection(taskListView.getViewer(), new Runnable() {
				public void run() {
					try {
						taskListView.getViewer().getControl().setRedraw(false);
						taskListView.setFocusedMode(true);
						previousSorter = taskListView.getViewer().getSorter();
						previousFilters = new HashSet<AbstractTaskListFilter>(taskListView.getFilters());
						taskListView.clearFilters(false);
						if (!taskListView.getFilters().contains(taskListInterestFilter)) {
							taskListView.addFilter(taskListInterestFilter);
						}
						// Setting sorter causes root refresh
						taskListView.getViewer().setSorter(taskListInterestSorter);
						taskListView.getViewer().expandAll();
						taskListView.setManualFiltersEnabled(false);
//				taskListView.selectedAndFocusTask(TasksUiPlugin.getTaskList().getActiveTask());

						showProgressBar(taskListView, true);
					} finally {
						taskListView.getViewer().getControl().setRedraw(true);
					}
				}

			});
			return true;
		} else {
			return false;
		}
	}

	private void showProgressBar(TaskListView taskListView, boolean visible) {
		taskListView.getFilteredTree().setShowProgress(visible);
	}

	@Override
	protected void uninstallInterestFilter(StructuredViewer viewer) {
		IViewPart part = super.getPartForAction();
		if (part instanceof TaskListView) {
			final TaskListView taskListView = (TaskListView) part;
			if (taskListView.isFocusedMode()) {
				TasksUiInternal.preservingSelection(taskListView.getViewer(), new Runnable() {
					public void run() {
						try {
							taskListView.getViewer().getControl().setRedraw(false);
							taskListView.setFocusedMode(false);
							taskListView.removeFilter(taskListInterestFilter);
							taskListView.setManualFiltersEnabled(true);
							for (AbstractTaskListFilter filter : previousFilters) {
								TaskListView.getFromActivePerspective().addFilter(filter);
							}
							taskListView.getViewer().collapseAll();

							taskListView.getViewer().setSorter(previousSorter);
							showProgressBar(taskListView, false);
						} finally {
							taskListView.getViewer().getControl().setRedraw(true);
						}
					}
				});
			}
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		// ignore
	}

	public void filterTextChanged(final String text) {
		if (isChecked() && (text == null || "".equals(text))) {
			IViewPart part = FocusTaskListAction.super.getPartForAction();
			if (part instanceof TaskListView) {
				((TaskListView) part).getViewer().expandAll();
			}
		}
	}

	@Override
	protected String getEmptyViewMessage() {
		return "No tasks scheduled for this week";
	}
}
