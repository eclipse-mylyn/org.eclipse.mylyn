/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.commons.activity.ui;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.activity.ui.UserActivityListener;
import org.eclipse.mylyn.commons.activity.ui.spi.AbstractUserActivityMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.osgi.util.NLS;

/**
 * Monitors user activity and notifies listeners.
 *
 * @author Steffen Pingel
 */
public class UserActivityManager implements IUserActivityManager {

	private final Set<UserActivityListener> activityListeners;

	private final List<AbstractUserActivityMonitor> activityMonitors;

	private final MonitorUserActivityJob monitorJob;

	public UserActivityManager(List<AbstractUserActivityMonitor> monitors) {
		activityListeners = new CopyOnWriteArraySet<>();
		activityMonitors = new CopyOnWriteArrayList<>(monitors);
		monitorJob = createMonitorActivityJob();
	}

	@Override
	public void addAttentionListener(UserActivityListener listener) {
		activityListeners.add(listener);
	}

	public int getInactivityTimeout() {
		return monitorJob.getInactivityTimeout();
	}

	public long getLastInteractionTime() {
		for (final AbstractUserActivityMonitor monitor : activityMonitors) {
			final boolean[] success = new boolean[1];
			final long[] result = new long[1];
			SafeRunner.run(new ISafeRunnable() {
				@Override
				public void handleException(Throwable e) {
					disableFailedMonitor(monitor, e);
				}

				@Override
				public void run() throws Exception {
					if (monitor.isEnabled()) {
						result[0] = monitor.getLastInteractionTime();
						success[0] = true;
					}
				}
			});
			if (success[0]) {
				return result[0];
			}
		}
		return -1;
	}

	/**
	 * Returns true, if other activity monitors than {@link WorkbenchUserActivityMonitor} have been registered.
	 */
	public boolean isTrackingOsTime() {
		return activityMonitors.size() > 1;
	}

	@Override
	public void removeAttentionListener(UserActivityListener listener) {
		activityListeners.remove(listener);
	}

	public void setInactivityTimeout(int inactivityTimeout) {
		monitorJob.setInactivityTimeout(inactivityTimeout);
	}

	public void start() {
		for (final AbstractUserActivityMonitor monitor : activityMonitors) {
			SafeRunner.run(new ISafeRunnable() {
				@Override
				public void handleException(Throwable e) {
					disableFailedMonitor(monitor, e);
				}

				@Override
				public void run() throws Exception {
					monitor.start();
				}
			});
		}
		monitorJob.reschedule();
	}

	public void stop() {
		monitorJob.cancel();
		for (final AbstractUserActivityMonitor monitor : activityMonitors) {
			SafeRunner.run(new ISafeRunnable() {
				@Override
				public void handleException(Throwable e) {
					disableFailedMonitor(monitor, e);
				}

				@Override
				public void run() throws Exception {
					monitor.stop();
				}
			});
		}
	}

	private MonitorUserActivityJob createMonitorActivityJob() {
		MonitorUserActivityJob job = new MonitorUserActivityJob(new IUserActivityManagerCallback() {
			@Override
			public void active() {
				UserActivityManager.this.fireActive();
			}

			@Override
			public void addMonitoredActivityTime(long localStartTime, long currentTime) {
				UserActivityManager.this.fireMonitoredActivityTime(localStartTime, currentTime);
			}

			@Override
			public long getLastEventTime() {
				return UserActivityManager.this.getLastInteractionTime();
			}

			@Override
			public void inactive() {
				UserActivityManager.this.fireInactive();
			}
		});
		job.setSystem(true);
		job.setPriority(Job.INTERACTIVE);
		return job;
	}

	private void disableFailedMonitor(AbstractUserActivityMonitor monitor, Throwable e) {
		StatusHandler.log(new Status(IStatus.WARNING, IActivityUiConstants.ID_PLUGIN,
				NLS.bind("Activity monitor ''{0}'' was disabled due to a failure", monitor.getClass()), e)); //$NON-NLS-1$
		activityMonitors.remove(monitor);
	}

	private void fireActive() {
		for (UserActivityListener attentionListener : activityListeners) {
			attentionListener.userAttentionGained();
		}
	}

	private void fireInactive() {
		for (UserActivityListener attentionListener : activityListeners) {
			attentionListener.userAttentionLost();
		}
	}

	/**
	 * Invoked when activity is recorded.
	 *
	 * @param start
	 *            time in milliseconds when user activity started
	 * @param end
	 *            time in milliseconds when user activity ended
	 */
	private void fireMonitoredActivityTime(long start, long end) {
		for (UserActivityListener attentionListener : activityListeners) {
			attentionListener.userActive(start, end);
		}
	}

	void init(List<AbstractUserActivityMonitor> monitors) {
		activityMonitors.addAll(monitors);
	}

}
