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
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.internal.ui.TaskListInterestFilter;
import org.eclipse.mylar.internal.ui.TaskListInterestSorter;
import org.eclipse.mylar.provisional.ui.InterestFilter;

/**
 * TODO: abuses contract from super class
 * 
 * @author Mik Kersten
 */
public class ApplyMylarToTaskListAction extends AbstractApplyMylarAction {

	private static ApplyMylarToTaskListAction INSTANCE;
	
	private TaskListInterestFilter taskListInterestFilter = new TaskListInterestFilter();
	
	private TaskListInterestSorter taskListInterestSorter = new TaskListInterestSorter();
	
	private Set<AbstractTaskListFilter> previousFilters = new HashSet<AbstractTaskListFilter>();
	
	private ViewerSorter previousSorter;
	
	public ApplyMylarToTaskListAction() {
		super(new InterestFilter());
		INSTANCE = this;
	}
	
	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		if (TaskListView.getDefault() != null) {
			viewers.add(TaskListView.getDefault().getViewer());
		}
		return viewers;
	}

	@Override
	protected boolean installInterestFilter(StructuredViewer viewer) {
		previousSorter = TaskListView.getDefault().getViewer().getSorter();
		TaskListView.getDefault().getViewer().setSorter(taskListInterestSorter);
		previousFilters = new HashSet<AbstractTaskListFilter>(TaskListView.getDefault().getFilters());
		TaskListView.getDefault().clearFilters(true);
		TaskListView.getDefault().addFilter(taskListInterestFilter);
		TaskListView.getDefault().setPriorityButtonEnabled(false);
		TaskListView.getDefault().refreshAndFocus();
		return true;
	}

	@Override
	protected void uninstallInterestFilter(StructuredViewer viewer) {
		TaskListView.getDefault().getViewer().setSorter(previousSorter);
		TaskListView.getDefault().removeFilter(taskListInterestFilter);
		TaskListView.getDefault().setPriorityButtonEnabled(true);
		for (AbstractTaskListFilter filter : previousFilters) {
			TaskListView.getDefault().addFilter(filter);
		}
		TaskListView.getDefault().getViewer().collapseAll();
		TaskListView.getDefault().refreshAndFocus();
	}

	public void propertyChange(PropertyChangeEvent event) {
		// ignore
	}

	public static ApplyMylarToTaskListAction getDefault() {
		return INSTANCE;
	}
	
	@Override
	public List<Class> getPreservedFilters() {
		return Collections.emptyList();
	}
}
