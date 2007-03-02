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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylar.tasks.core.DateRangeActivityDelegate;
import org.eclipse.mylar.tasks.core.DateRangeContainer;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.ui.TaskListManager;

/**
 * @author Rob Elves
 */
public class TaskActivityContentProvider extends TaskListContentProvider {

	private static final String LABEL_ACTIVITY = "Scheduled";

	private TaskListManager taskListManager;

	public TaskActivityContentProvider(TaskListView view, TaskListManager taskActivityManager) {
		super(view);
		this.taskListManager = taskActivityManager;
	}

	public Object[] getElements(Object parent) {
		if (parent.equals(this.view.getViewSite())) {
			Set<ITaskListElement> ranges = new HashSet<ITaskListElement>();
			ranges.addAll(taskListManager.getDateRanges());
			return applyFilter(ranges).toArray();
		} else {
			return super.getElements(parent);
		}
	}

	public Object getParent(Object child) {
		if (child instanceof DateRangeActivityDelegate) {
			DateRangeActivityDelegate dateRangeTaskWrapper = (DateRangeActivityDelegate) child;
			return dateRangeTaskWrapper.getParent();
		} else {
			return null;
		}
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof DateRangeContainer) {
			DateRangeContainer dateRangeTaskCategory = (DateRangeContainer) parent;
			return dateRangeTaskCategory.getChildren() != null && dateRangeTaskCategory.getChildren().size() > 0;
		} else {
			return false;
		}
	}

	@Override
	public String getLabel() {
		return LABEL_ACTIVITY;
	}
}
