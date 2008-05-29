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

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * TODO use extensible deltas instead of Objects for refresh
 * 
 * @author Steffen Pingel
 * @author Mik Kersten
 */
public abstract class DelayedRefreshJob extends WorkbenchJob {

	static final long REFRESH_DELAY_DEFAULT = 200;

	static final long REFRESH_DELAY_MAX = REFRESH_DELAY_DEFAULT * 2;

	// FIXME make private
	protected final StructuredViewer viewer;

	private static final int NOT_SCHEDULED = -1;

	private final LinkedHashSet<Object> queue = new LinkedHashSet<Object>();

	private long scheduleTime = NOT_SCHEDULED;

	public DelayedRefreshJob(StructuredViewer viewer, String name) {
		super(name);
		Assert.isNotNull(viewer);
		this.viewer = viewer;
		setSystem(true);
	}

	// XXX needs to be called from UI thread
	public void refreshNow() {
		queue.add(null);
		runInUIThread(new NullProgressMonitor());
	}

	public synchronized void refresh() {
		refreshElement(null);
	}

	public synchronized void refreshElements(Object[] elements) {
		queue.addAll(Arrays.asList(elements));
		// FIXME this is a copy of refreshElement(Object)
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
		if (viewer.getControl() == null || viewer.getControl().isDisposed()) {
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

		doRefresh(items);

		return Status.OK_STATUS;
	}

	protected abstract void doRefresh(final Object[] items);

}