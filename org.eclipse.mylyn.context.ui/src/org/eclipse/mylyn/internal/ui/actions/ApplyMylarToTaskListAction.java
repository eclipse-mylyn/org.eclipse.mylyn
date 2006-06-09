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

package org.eclipse.mylar.internal.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.internal.tasklist.ui.AbstractTaskListFilter;
import org.eclipse.mylar.internal.tasklist.ui.views.IFilteredTreeListener;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.internal.ui.TaskListInterestFilter;
import org.eclipse.mylar.internal.ui.TaskListInterestSorter;
import org.eclipse.mylar.provisional.ui.InterestFilter;
import org.eclipse.ui.IViewPart;

/**
 * TODO: abuses contract from super class
 * 
 * @author Mik Kersten
 */
public class ApplyMylarToTaskListAction extends AbstractApplyMylarAction implements IFilteredTreeListener {

	private TaskListInterestFilter taskListInterestFilter = new TaskListInterestFilter();

	private TaskListInterestSorter taskListInterestSorter = new TaskListInterestSorter();

	private Set<AbstractTaskListFilter> previousFilters = new HashSet<AbstractTaskListFilter>();

	private ViewerSorter previousSorter;

	public ApplyMylarToTaskListAction() {
		super(new InterestFilter());
	}

	@Override
	public void init(IViewPart view) {
		super.init(view);
		IViewPart part = super.getPartForAction();
		if (part instanceof TaskListView) {
			((TaskListView)part).getFilteredTree().addListener(this);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		IViewPart part = super.getPartForAction();
		if (part instanceof TaskListView) {
			((TaskListView)part).getFilteredTree().removeListener(this);
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
			previousSorter = TaskListView.getFromActivePerspective().getViewer().getSorter();
			taskListView.getViewer().setSorter(taskListInterestSorter);
			previousFilters = new HashSet<AbstractTaskListFilter>(taskListView.getFilters());
			taskListView.clearFilters(true);
			taskListView.addFilter(taskListInterestFilter);
			taskListView.setPriorityButtonEnabled(false);
			taskListView.refreshAndFocus(true);
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
			taskListView.getViewer().setSorter(previousSorter);
			taskListView.removeFilter(taskListInterestFilter);
			taskListView.setPriorityButtonEnabled(true);
			for (AbstractTaskListFilter filter : previousFilters) {
				TaskListView.getFromActivePerspective().addFilter(filter);
			}
			taskListView.getViewer().collapseAll();
			taskListView.refreshAndFocus(false);
		} 
	}

	public void propertyChange(PropertyChangeEvent event) {
		// ignore
	}

	@Override
	public List<Class> getPreservedFilters() {
		return Collections.emptyList();
	}

	
	public void filterTextChanged(String text) {
		if (isChecked() && (text == null || "".equals(text))) {
			IViewPart part = super.getPartForAction();
			if (part instanceof TaskListView) {
				((TaskListView)part).getViewer().expandAll();
			}
		}
	}
}
