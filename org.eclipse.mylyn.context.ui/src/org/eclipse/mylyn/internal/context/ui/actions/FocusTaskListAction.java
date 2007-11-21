/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.mylyn.internal.context.ui.TaskListInterestFilter;
import org.eclipse.mylyn.internal.context.ui.TaskListInterestSorter;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.views.IFilteredTreeListener;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IViewPart;

/**
 * TODO: abuses contract from super class
 * 
 * @author Mik Kersten
 */
public class FocusTaskListAction extends AbstractFocusViewAction implements IFilteredTreeListener {

	private TaskListInterestFilter taskListInterestFilter = new TaskListInterestFilter();

	private TaskListInterestSorter taskListInterestSorter = new TaskListInterestSorter();

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
		IViewPart part = super.getPartForAction();
		if (part instanceof TaskListView) {
			((TaskListView) part).getFilteredTree().setShowProgress(super.isChecked());
		}
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
			TaskListView taskListView = (TaskListView) part;

			try {
				taskListView.getViewer().getControl().setRedraw(false);
				taskListView.setFocusedMode(true);
				previousSorter = taskListView.getViewer().getSorter();
				previousFilters = new HashSet<AbstractTaskListFilter>(taskListView.getFilters());
				taskListView.clearFilters(false);
				if (!taskListView.getFilters().contains(taskListInterestFilter)) {
					taskListView.addFilter(taskListInterestFilter);
				}
//				taskListView.getViewer().getTree().setHeaderVisible(false);
				taskListView.getViewer().expandAll();
				// Setting sorter causes root refresh
				taskListView.getViewer().setSorter(taskListInterestSorter);
				taskListView.setManualFiltersEnabled(false);
				taskListView.selectedAndFocusTask(TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask());
				//taskListView.refreshAndFocus(true);
			} finally {
				taskListView.getViewer().getControl().setRedraw(true);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void uninstallInterestFilter(StructuredViewer viewer) {
		IViewPart part = super.getPartForAction();
		if (part instanceof TaskListView) {
			TaskListView taskListView = (TaskListView) part;
			try {
				taskListView.getViewer().getControl().setRedraw(false);
				taskListView.setFocusedMode(false);
				taskListView.removeFilter(taskListInterestFilter);
				taskListView.setManualFiltersEnabled(true);
				for (AbstractTaskListFilter filter : previousFilters) {
					TaskListView.getFromActivePerspective().addFilter(filter);
				}
//				taskListView.getViewer().getTree().setHeaderVisible(true);
				taskListView.getViewer().collapseAll();

				// Setting the sorter causes a root refresh
				taskListView.getViewer().setSorter(previousSorter);
				taskListView.selectedAndFocusTask(TasksUiPlugin.getTaskListManager().getTaskList().getActiveTask());
				//taskListView.refreshAndFocus(false);
			} finally {
				taskListView.getViewer().getControl().setRedraw(true);
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

}
