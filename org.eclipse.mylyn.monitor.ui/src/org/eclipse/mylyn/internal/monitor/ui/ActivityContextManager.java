/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.ui;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.ui.AbstractUserActivityMonitor;
import org.eclipse.mylyn.monitor.ui.IUserAttentionListener;
import org.eclipse.mylyn.monitor.ui.MonitorUiPlugin;

/**
 * Manages the meta task-activity context.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @since	2.0
 */
public class ActivityContextManager {

	private int TICK = 30 * 1000;

	private int SHORT_TICK = 5 * 1000;

	private AbstractUserActivityMonitor userActivityMonitor;

	private Set<IUserAttentionListener> attentionListeners = new CopyOnWriteArraySet<IUserAttentionListener>();

	private long startTime = -1;

	private final int timeout;

	private Object startTimeLock = new Object();

	private boolean wasTimedOut = true;

	private int wait = SHORT_TICK;

	private CheckActivityJob checkJob;

	public ActivityContextManager(int timeout, AbstractUserActivityMonitor userActivityMonitor) {
		this.userActivityMonitor = userActivityMonitor;
		this.timeout = timeout;
	}

	public void fireActive(long start, long end) {
		ContextCorePlugin.getContextManager().processActivityMetaContextEvent(
				new InteractionEvent(InteractionEvent.Kind.COMMAND, InteractionContextManager.ACTIVITY_STRUCTURE_KIND,
						InteractionContextManager.ACTIVITY_HANDLE_ATTENTION, InteractionContextManager.ACTIVITY_ORIGIN_ID, null,
						InteractionContextManager.ACTIVITY_DELTA_ACTIVATED, 1f, new Date(start), new Date(end)));
		for (IUserAttentionListener attentionListener : attentionListeners) {
			attentionListener.userAttentionGained();
		}
	}

	public void fireInactive() {
		for (IUserAttentionListener attentionListener : attentionListeners) {
			attentionListener.userAttentionLost();
		}
	}

	public void start() {
		userActivityMonitor.start();
		checkJob = new CheckActivityJob();
		checkJob.setSystem(true);
		checkJob.setPriority(Job.DECORATE);
		checkJob.schedule(TICK);
	}

	public void stop() {
		userActivityMonitor.stop();
		if (checkJob != null) {
			checkJob.cancel();
		}
	}

	public void addListener(IUserAttentionListener listener) {
		attentionListeners.add(listener);
	}

	public void removeListener(IUserAttentionListener listener) {
		attentionListeners.remove(listener);
	}

	public long getLastEventTime() {
		return userActivityMonitor.getLastInteractionTime();
	}

	public long getStartTime() {
		synchronized (startTimeLock) {
			return startTime;
		}
	}

	public void setStartTime(long startTime) {
		synchronized (startTimeLock) {
			this.startTime = startTime;
		}
	}

	class CheckActivityJob extends Job {

		public CheckActivityJob() {
			super("Activity Monitor Job");
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				if (Platform.isRunning()) {
					if (!MonitorUiPlugin.getDefault().getWorkbench().isClosing()) {

						long localLastEventTime = getLastEventTime();
						long localStartTime = getStartTime();
						long currentTime = System.currentTimeMillis();
						if ((currentTime - localLastEventTime) >= timeout) {
							if (wasTimedOut == false) {
								fireInactive();
								// timed out
								wasTimedOut = true;
							}
							wait = SHORT_TICK;
						} else {
							if (wasTimedOut) {
								wasTimedOut = false;
								// back...
								setStartTime(localLastEventTime);
							} else {
								fireActive(localStartTime, currentTime);
								setStartTime(currentTime);
							}
							wait = TICK;
						}

					}
				}
				return Status.OK_STATUS;
			} finally {
				if (Platform.isRunning()) {
					checkJob.schedule(wait);
				}
			}
		}
	}
}