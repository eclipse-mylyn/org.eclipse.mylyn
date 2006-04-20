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
import java.util.List;

import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.internal.ui.TaskListInterestFilter;
import org.eclipse.mylar.provisional.ui.InterestFilter;

/**
 * TODO: abuses contract from super class
 * 
 * @author Mik Kersten
 */
public class ApplyMylarToTaskListAction extends AbstractApplyMylarAction {

	private static ApplyMylarToTaskListAction INSTANCE;
	
	private TaskListInterestFilter taskListInterestFilter = new TaskListInterestFilter();
	
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
		TaskListView.getDefault().addFilter(taskListInterestFilter);
		TaskListView.getDefault().refreshAndFocus();
		return true;
	}

	@Override
	protected void uninstallInterestFilter(StructuredViewer viewer) {
		TaskListView.getDefault().removeFilter(taskListInterestFilter);
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
