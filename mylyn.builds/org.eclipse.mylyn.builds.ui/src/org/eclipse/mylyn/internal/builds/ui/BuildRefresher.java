/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Itema AS - Automatic refresh when a new repo has been added; bug 330910
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.builds.internal.core.operations.BuildJob;
import org.eclipse.mylyn.builds.internal.core.operations.RefreshOperation;
import org.eclipse.mylyn.builds.internal.core.util.BuildScheduler;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor.OperationFlag;

/**
 * @author Steffen Pingel
 * @author Torkild U. Resheim
 */
public class BuildRefresher implements IPropertyChangeListener {

	private static final long STARTUP_DELAY = 5 * 1000;

	private class RefreshJob extends BuildJob {

		public RefreshJob() {
			super("Background Builds Refresh");
			setUser(false);
		}

		@Override
		protected IStatus doExecute(IOperationMonitor progress) {
			RefreshOperation refreshOperation = BuildsUiInternal.getFactory().getRefreshOperation();
			refreshOperation.addFlag(OperationFlag.BACKGROUND);
			return refreshOperation.doExecute(progress);
		}
	};

	private boolean running;

	private final IPreferenceStore preferenceStore;

	private final BuildScheduler scheduler;

	public BuildRefresher(IPreferenceStore preferenceStore, BuildScheduler scheduler) {
		this.preferenceStore = preferenceStore;
		this.scheduler = scheduler;
	}

	private RefreshJob refreshJob;

	private long getInterval() {
		return preferenceStore.getLong(BuildsUiInternal.PREF_AUTO_REFRESH_INTERVAL);
	}

	public boolean isEnabled() {
		return running && preferenceStore.getBoolean(BuildsUiInternal.PREF_AUTO_REFRESH_ENABLED);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(BuildsUiInternal.PREF_AUTO_REFRESH_ENABLED)
				|| event.getProperty().equals(BuildsUiInternal.PREF_AUTO_REFRESH_INTERVAL)) {
			reschedule(0L);
		}
	}

	public void start() {
		if (running) {
			throw new IllegalStateException();
		}
		preferenceStore.addPropertyChangeListener(this);
		running = true;
		reschedule(STARTUP_DELAY);
	}

	/**
	 * Performs an immediate one-shot refresh of build server data regardless of the automatic refresh preference
	 * setting.
	 */
	void refresh() {
		if (refreshJob == null) {
			refreshJob = new RefreshJob();
			refreshJob.setSystem(true);
		}
		scheduler.schedule(refreshJob, 0);
	}

	private synchronized void reschedule(long delay) {
		if (isEnabled()) {
			if (refreshJob == null) {
				refreshJob = new RefreshJob();
				refreshJob.setSystem(true);
				refreshJob.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						if (running) {
							reschedule(getInterval());
						}
					}
				});
			}
			scheduler.schedule(refreshJob, delay);
		} else {
			if (refreshJob != null) {
				refreshJob.cancel();
			}
		}
	}

	public synchronized void stop() {
		if (!running) {
			return;
		}
		running = false;
		preferenceStore.removePropertyChangeListener(this);
		if (refreshJob != null) {
			refreshJob.cancel();
		}
	}

}
