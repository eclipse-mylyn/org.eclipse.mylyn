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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.provisional.tasklist.DateRangeContainer;
import org.eclipse.mylar.provisional.tasklist.DateRangeActivityDelegate;
import org.eclipse.mylar.provisional.tasklist.TaskListManager;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class TaskActivityContentProvider implements IStructuredContentProvider, ITreeContentProvider {

//	private TreeViewer treeViewer;

	private TaskListManager taskListManager;

	public TaskActivityContentProvider(TaskListManager taskActivityManager) {
		this.taskListManager = taskActivityManager;
//		this.treeViewer = viewer;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
//		treeViewer = (TreeViewer)v;
//		taskActivityManager = MylarTaskListPlugin.getTaskActivityManager();
//		taskActivityManager.addListener(this);
	}

	public void dispose() {
//		taskActivityManager.removeListener(this);
	}

	public Object[] getElements(Object parent) {
		return taskListManager.getDateRanges();
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

//	public void changed(Object o) {
//		if (o != null) {
//			treeViewer.refresh(o);
//		} else {
//			treeViewer.refresh();
//		}
//	}
}
