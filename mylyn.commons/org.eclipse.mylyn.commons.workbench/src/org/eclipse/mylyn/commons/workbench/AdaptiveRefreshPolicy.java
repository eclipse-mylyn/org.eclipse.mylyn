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
 * @since 3.7
 */
public class AdaptiveRefreshPolicy {

	public interface IFilteredTreeListener {

		void filterTextChanged(String text);

	}

	private int refreshDelay = 1500;

	private final Set<IFilteredTreeListener> listeners = new HashSet<>();

	private String oldText = ""; //$NON-NLS-1$

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

		@Override
		public void aboutToRun(IJobChangeEvent event) {
			// ignore
		}

		@Override
		public void awake(IJobChangeEvent event) {
			// ignore
		}

		@Override
		public void done(IJobChangeEvent event) {
			if (event.getResult().isOK()) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
					for (IFilteredTreeListener listener : listeners) {
						listener.filterTextChanged(oldText);
					}
				});
			}
		}

		@Override
		public void running(IJobChangeEvent event) {
			// ignore
		}

		@Override
		public void scheduled(IJobChangeEvent event) {
			// ignore
		}

		@Override
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
			delay = (int) (refreshDelay / (textLength * 0.6));
		}
		refreshJob.schedule(delay);

		oldText = text;
	}

	/**
	 * the filter has changed in some way (not the text)
	 */
	public void filterChanged() {
		refreshJob.cancel();
		refreshJob.schedule(refreshDelay / 2);
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
