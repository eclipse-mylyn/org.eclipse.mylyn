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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.tasks.core.DateRangeActivityDelegate;
import org.eclipse.mylar.tasks.core.DateRangeContainer;
import org.eclipse.mylar.tasks.ui.TaskListManager;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class TaskActivityViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	private TaskListManager taskListManager;

	public TaskActivityViewContentProvider(TaskListManager taskActivityManager) {
		this.taskListManager = taskActivityManager;
	}

	public Object[] getElements(Object parent) {
		return taskListManager.getDateRanges().toArray();
	}

	public Object getParent(Object child) {
		if (child instanceof DateRangeActivityDelegate) {
			DateRangeActivityDelegate dateRangeTaskWrapper = (DateRangeActivityDelegate) child;
			return dateRangeTaskWrapper.getParent();
		} else {
			return new Object[0];
		}
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof DateRangeContainer) {
			DateRangeContainer taskContainer = (DateRangeContainer) parent;
			return taskContainer.getChildren().toArray();
		} else {
			return new Object[0];
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

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
