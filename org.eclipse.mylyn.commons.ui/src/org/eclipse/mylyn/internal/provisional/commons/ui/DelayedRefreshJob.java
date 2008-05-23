/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.commons.ui.TreeWalker;
import org.eclipse.mylyn.internal.commons.ui.TreeWalker.TreeVisitor;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * @author Steffen Pingel
 * @author Mik Kersten
 */
public abstract class DelayedRefreshJob extends WorkbenchJob {

	static final long REFRESH_DELAY_DEFAULT = 200;

	static final long REFRESH_DELAY_MAX = REFRESH_DELAY_DEFAULT * 2;

	protected final StructuredViewer viewer;

	private static final int NOT_SCHEDULED = -1;

	private final LinkedHashSet<Object> queue = new LinkedHashSet<Object>();

	private long scheduleTime = NOT_SCHEDULED;

	public DelayedRefreshJob(StructuredViewer treeViewer, String name) {
		super(name);
		this.viewer = treeViewer;
		setSystem(true);
	}

	public void forceRefresh() {
		queue.add(null);
		runInUIThread(new NullProgressMonitor());
	}

	public synchronized void refresh() {
		refreshElement(null);
	}

	public synchronized void refreshElements(Object[] elements) {
		queue.addAll(Arrays.asList(elements));

		if (scheduleTime == NOT_SCHEDULED) {
			scheduleTime = System.currentTimeMillis();
			schedule(REFRESH_DELAY_DEFAULT);
		} else if (System.currentTimeMillis() - scheduleTime < REFRESH_DELAY_MAX - REFRESH_DELAY_DEFAULT) {
			// reschedule to aggregate more refreshes
			cancel();
			schedule(REFRESH_DELAY_DEFAULT);
		}
	}

	public synchronized void refreshElement(Object element) {
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

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		if (viewer.getControl() == null || viewer.getControl() == null || viewer.getControl().isDisposed()) {
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

		TreeViewer treeViewer = null;
		TreePath treePath = null;
		if (viewer instanceof TreeViewer) {
			treeViewer = (TreeViewer) viewer;
		}
		if (treeViewer != null) {
			// in case the refresh removes the currently selected item, 
			// remember the next item in the tree to restore the selection
			// TODO: consider making this optional
			TreeItem[] selection = treeViewer.getTree().getSelection();
			if (selection.length > 0) {
				TreeWalker treeWalker = new TreeWalker(treeViewer);
				treePath = treeWalker.walk(new TreeVisitor() {
					@Override
					public boolean visit(Object object) {
						return true;
					}
				}, selection[selection.length - 1]);
			}
		}

		refresh(items);

		if (treeViewer != null && treePath != null) {
			ISelection newSelection = viewer.getSelection();
			if (newSelection == null || newSelection.isEmpty()) {
				treeViewer.setSelection(new TreeSelection(treePath), true);
			}
		}

		return Status.OK_STATUS;
	}

	protected abstract void refresh(final Object[] items);
}