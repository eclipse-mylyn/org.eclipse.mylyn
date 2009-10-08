/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.mylyn.internal.tasks.core.ITaskJobFactory;
import org.eclipse.mylyn.monitor.ui.IUserAttentionListener;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;

/**
 * @author Steffen Pingel
 */
public class TaskListSynchronizationScheduler implements IUserAttentionListener {

	private static final boolean TRACE_ENABLED = Boolean.valueOf(Platform.getDebugOption("org.eclipse.mylyn.tasks.ui/debug/synchronization")); //$NON-NLS-1$

	private long interval;

	private long incactiveInterval;

	private final ITaskJobFactory jobFactory;

	private SynchronizationJob refreshJob;

	private boolean userActive;

	private long scheduledTime;

	private long lastSyncTime;

	private final JobChangeAdapter jobListener;

	public TaskListSynchronizationScheduler(ITaskJobFactory jobFactory) {
		this.jobFactory = jobFactory;
		this.userActive = true;
		this.jobListener = new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				scheduledTime = 0;
				lastSyncTime = System.currentTimeMillis();
				reschedule();
			}

		};
	}

	private SynchronizationJob createRefreshJob() {
		SynchronizationJob job = jobFactory.createSynchronizeRepositoriesJob(null);
		// do not show in progress view by default
		job.setSystem(true);
		job.setUser(false);
		job.setFullSynchronization(true);
		return job;
	}

	public synchronized SynchronizationJob getRefreshJob() {
		return refreshJob;
	}

	private synchronized void reschedule() {
		long delay = this.interval;
		if (delay != 0) {
			if (!userActive) {
				// triple scheduling interval each time
				this.incactiveInterval *= 3;
				delay = this.incactiveInterval;
				if (TRACE_ENABLED) {
					System.err.println("Set inactive interval to " + DateUtil.getFormattedDurationShort(this.incactiveInterval)); //$NON-NLS-1$
				}
			}
			if (this.scheduledTime != 0) {
				if (this.scheduledTime < System.currentTimeMillis() + delay) {
					// already scheduled, nothing to do
					if (TRACE_ENABLED) {
						System.err.println("Synchronzation already scheduled in " + DateUtil.getFormattedDurationShort(this.scheduledTime - System.currentTimeMillis())); //$NON-NLS-1$
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
			System.err.println("Canceling synchronization in " + DateUtil.getFormattedDurationShort(this.scheduledTime - System.currentTimeMillis())); //$NON-NLS-1$
		}
		refreshJob.removeJobChangeListener(jobListener);
		refreshJob.cancel();
		refreshJob.addJobChangeListener(jobListener);
	}

	private void schedule(long interval) {
		if (TRACE_ENABLED) {
			System.err.println("Scheduling synchronzation in " + DateUtil.getFormattedDurationShort(interval)); //$NON-NLS-1$
		}
		this.scheduledTime = System.currentTimeMillis() + interval;
		refreshJob.schedule(interval);
	}

	public synchronized void setInterval(long interval) {
		setInterval(interval, interval);
	}

	public synchronized void setInterval(long delay, long interval) {
		if (this.interval != interval) {
			this.interval = interval;
			this.incactiveInterval = interval;
			this.scheduledTime = 0;

			if (refreshJob != null) {
				refreshJob.removeJobChangeListener(jobListener);
				cancel();
				refreshJob = null;
			}

			if (interval > 0) {
				refreshJob = createRefreshJob();
				refreshJob.addJobChangeListener(jobListener);
				schedule(delay);
			}
		}
	}

	public void userAttentionGained() {
		synchronized (this) {
			if (!userActive) {
				if (TRACE_ENABLED) {
					System.err.println("User activity detected"); //$NON-NLS-1$
				}
				this.userActive = true;
				// reset inactive interval each time the user becomes active
				this.incactiveInterval = interval;
				if (interval != 0 && System.currentTimeMillis() - lastSyncTime > interval) {
					// the last sync was long ago, sync right away
					cancel();
					schedule(0);
				} else {
					reschedule();
				}
			}
		}
	}

	public void userAttentionLost() {
		synchronized (this) {
			this.userActive = false;
		}
	}

}
