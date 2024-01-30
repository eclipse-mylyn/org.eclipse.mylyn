/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench;

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
 * @since 3.7
 */
public abstract class DelayedRefreshJob extends WorkbenchJob {

	static final long REFRESH_DELAY_DEFAULT = 200;

	static final long REFRESH_DELAY_MAX = REFRESH_DELAY_DEFAULT * 2;

	// FIXME make private
	protected final StructuredViewer viewer;

	private static final int NOT_SCHEDULED = -1;

	private final LinkedHashSet<Object> queue = new LinkedHashSet<>();

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
		refreshElements(new Object[] { element });
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