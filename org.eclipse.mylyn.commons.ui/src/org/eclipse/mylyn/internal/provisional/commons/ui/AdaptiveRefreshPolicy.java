/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class AdaptiveRefreshPolicy {

	private int refreshDelay = 1500;

	private final Set<IFilteredTreeListener> listeners = new HashSet<IFilteredTreeListener>();

	private String oldText = "";

	protected Job refreshJob;

	@Deprecated
	public AdaptiveRefreshPolicy(Job refreshJob, Text filterText) {
		this(refreshJob);
	}

	public AdaptiveRefreshPolicy(Job refreshJob) {
		Assert.isNotNull(refreshJob);
		this.refreshJob = refreshJob;
		refreshJob.addJobChangeListener(REFRESH_JOB_LISTENER);
	}

	public void dispose() {
		refreshJob.removeJobChangeListener(REFRESH_JOB_LISTENER);
	}

	protected final IJobChangeListener REFRESH_JOB_LISTENER = new IJobChangeListener() {

		public void aboutToRun(IJobChangeEvent event) {
			// ignore
		}

		public void awake(IJobChangeEvent event) {
			// ignore
		}

		public void done(IJobChangeEvent event) {
			if (event.getResult().isOK()) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						for (IFilteredTreeListener listener : listeners) {
							listener.filterTextChanged(oldText);
						}
					}
				});
			}
		}

		public void running(IJobChangeEvent event) {
			// ignore
		}

		public void scheduled(IJobChangeEvent event) {
			// ignore
		}

		public void sleeping(IJobChangeEvent event) {
			// ignore
		}
	};

	public void textChanged(String text) {
		if (text == null || text.equals(oldText)) {
			return;
		}

		refreshJob.cancel();
		int delay = 0;
		int textLength = text.length();
		if (textLength > 0) {
			delay = (int) (this.refreshDelay / (textLength * 0.6));
		}
		refreshJob.schedule(delay);

		this.oldText = text;
	}

	/**
	 * for testing purposes only
	 */
	public void internalForceRefresh() {
		((WorkbenchJob) refreshJob).runInUIThread(new NullProgressMonitor());
	}

	public void addListener(IFilteredTreeListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IFilteredTreeListener listener) {
		listeners.remove(listener);
	}

	public void setRefreshDelay(int refreshDelay) {
		this.refreshDelay = refreshDelay;
	}

	public int getRefreshDelay() {
		return refreshDelay;
	}

}
