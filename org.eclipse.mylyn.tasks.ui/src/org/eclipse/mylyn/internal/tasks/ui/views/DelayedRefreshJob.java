/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.LinkedHashSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.ui.util.TreeWalker;
import org.eclipse.mylyn.internal.tasks.ui.util.TreeWalker.TreeVisitor;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * @author Steffen Pingel
 */
abstract class DelayedRefreshJob extends WorkbenchJob {

	static final long REFRESH_DELAY_DEFAULT = 200;

	static final long REFRESH_DELAY_MAX = 500;
	
	private final TreeViewer treeViewer;

	private static final int NOT_SCHEDULED = -1;

	private LinkedHashSet<Object> queue = new LinkedHashSet<Object>();

	private long scheduleTime = NOT_SCHEDULED;

	public DelayedRefreshJob(TreeViewer treeViewer, String name) {
		super(name);
		this.treeViewer = treeViewer;
		setSystem(true);
	}

	public void forceRefresh() {
		queue.add(null);
		runInUIThread(new NullProgressMonitor());
	}

	public synchronized void refresh() {
		refreshTask(null);
	}
	
	public synchronized void refreshTask(Object element) {
		queue.add(element);

		if (scheduleTime == NOT_SCHEDULED) {
			scheduleTime = System.currentTimeMillis();
			schedule(REFRESH_DELAY_DEFAULT);
		} else if (System.currentTimeMillis() - scheduleTime < REFRESH_DELAY_MAX - REFRESH_DELAY_DEFAULT) {
			// reschedule to aggregate more refreshes
			cancel();
			schedule(REFRESH_DELAY_DEFAULT);
		}
	}

	public IStatus runInUIThread(IProgressMonitor monitor) {
		if (treeViewer.getControl() == null || treeViewer.getControl().isDisposed()) {
			return Status.CANCEL_STATUS;
		}

		final Object[] items;
		synchronized (this) {
			if (queue.contains(null)) {
				items = null;
			} else {
				items = queue.toArray(new Object[0]);
			}
			queue.clear();

			scheduleTime = NOT_SCHEDULED;
		}

		// in case the refresh removes the currently selected item, 
		// remember the next item in the tree to restore the selection
		TreeItem[] selection = treeViewer.getTree().getSelection();
		TreePath treePath = null;
		if (selection.length > 0) {
			TreeWalker treeWalker = new TreeWalker(treeViewer);
			treePath  = treeWalker.walk(new TreeVisitor() {
				@Override
				public boolean visit(Object object) {
					return true;
				}				
			}, selection[selection.length - 1]);
		}
				
		refresh(items);

		if (treePath != null) {
			ISelection newSelection = treeViewer.getSelection();
			if (newSelection == null || newSelection.isEmpty()) {
				treeViewer.setSelection(new TreeSelection(treePath), true);
			}
		}

		return Status.OK_STATUS;
	}

	protected void refresh(final Object[] items) {
		if (items == null) {
			treeViewer.refresh(true);
		} else if (items.length > 0) {
			try {
				for (Object item : items) {
					if (item instanceof AbstractTask) {
						AbstractTask task = (AbstractTask) item;
						treeViewer.refresh(task, true);
					} else {
						treeViewer.refresh(item, true);
					}
					updateExpansionState(item);
				}
			} catch (SWTException e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to refresh viewer: " + treeViewer, e));
			}
		}
	}

	protected abstract void updateExpansionState(Object item);

}