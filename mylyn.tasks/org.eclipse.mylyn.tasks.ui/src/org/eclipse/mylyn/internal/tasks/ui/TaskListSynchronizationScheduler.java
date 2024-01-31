/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.mylyn.monitor.ui.IUserAttentionListener;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class TaskListSynchronizationScheduler implements IUserAttentionListener {

	private static final boolean TRACE_ENABLED = Boolean
			.parseBoolean(Platform.getDebugOption("org.eclipse.mylyn.tasks.ui/debug/synchronization")); //$NON-NLS-1$

	private long interval;

	private long inactiveInterval;

	private final Job refreshJob;

	private boolean userActive;

	/**
	 * Absolute time in milliseconds when refresh job will next run.
	 */
	private long scheduledTime;

	/**
	 * Absolute time in milliseconds when refresh job last completed.
	 */
	private long lastSyncTime;

	private final JobChangeAdapter jobListener;

	public TaskListSynchronizationScheduler(Job refreshJob) {
		userActive = true;
		jobListener = new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				jobDone();
			}

		};
		this.refreshJob = refreshJob;

		// do not show in progress view by default
		this.refreshJob.setSystem(true);
		this.refreshJob.setUser(false);
	}

	private synchronized void reschedule() {
		long delay = interval;
		if (delay != 0 && PlatformUI.isWorkbenchRunning()) {
			if (!userActive) {
				// triple scheduling interval each time
				inactiveInterval *= 3;
				delay = inactiveInterval;
				if (TRACE_ENABLED) {
					trace("Set inactive interval to " + DateUtil.getFormattedDurationShort(inactiveInterval)); //$NON-NLS-1$
				}
			}
			if (scheduledTime != 0) {
				if (scheduledTime < System.currentTimeMillis() + delay) {
					// already scheduled, nothing to do
					if (TRACE_ENABLED) {
						trace("Synchronization already scheduled in " //$NON-NLS-1$
								+ DateUtil.getFormattedDurationShort(scheduledTime - System.currentTimeMillis()));
					}
					return;
				} else {
					// reschedule for an earlier time
					cancel();
				}
			}

			schedule(delay);
		}
	}

	private synchronized void cancel() {
		// prevent listener from rescheduling due to cancel
		if (TRACE_ENABLED) {
			trace("Canceling synchronization scheduled to run in " //$NON-NLS-1$
					+ DateUtil.getFormattedDurationShort(scheduledTime - System.currentTimeMillis()));
		}
		refreshJob.removeJobChangeListener(jobListener);
		refreshJob.cancel();
		refreshJob.addJobChangeListener(jobListener);
	}

	private void schedule(long interval) {
		if (TRACE_ENABLED) {
			trace("Scheduling synchronization in " + DateUtil.getFormattedDurationShort(interval)); //$NON-NLS-1$
		}
		scheduledTime = System.currentTimeMillis() + interval;
		refreshJob.schedule(interval);
	}

	public synchronized void setInterval(long interval) {
		setInterval(interval, interval);
	}

	public synchronized void setInterval(long delay, long interval) {
		if (this.interval != interval) {
			this.interval = interval;
			inactiveInterval = interval;
			scheduledTime = 0;

			cancel();
			if (interval > 0) {
				schedule(delay);
			}
		}
	}

	@Override
	public void userAttentionGained() {
		synchronized (this) {
			if (!userActive) {
				if (TRACE_ENABLED) {
					trace("User activity detected"); //$NON-NLS-1$
				}
				userActive = true;
				// reset inactive interval each time the user becomes active
				inactiveInterval = interval;
				if (interval != 0) {
					if (System.currentTimeMillis() - lastSyncTime > interval) {
						// the last sync was long ago, sync right away
						cancel();
						schedule(0);
					} else {
						reschedule();
					}
				}
			}
		}
	}

	private void trace(String message) {
		System.err.println("[" + new Date() + "] " + message); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void userAttentionLost() {
		synchronized (this) {
			userActive = false;
		}
	}

	synchronized void jobDone() {
		scheduledTime = 0;
		lastSyncTime = System.currentTimeMillis();
		reschedule();
	}

	public void dispose() {
		refreshJob.removeJobChangeListener(jobListener);
		refreshJob.cancel();
	}

}
