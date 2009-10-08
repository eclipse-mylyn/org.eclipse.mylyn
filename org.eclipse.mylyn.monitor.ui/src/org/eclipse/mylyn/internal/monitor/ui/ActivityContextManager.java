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

package org.eclipse.mylyn.internal.monitor.ui;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.ui.AbstractUserActivityMonitor;
import org.eclipse.mylyn.monitor.ui.IActivityContextManager;
import org.eclipse.mylyn.monitor.ui.IUserAttentionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

/**
 * Manages the meta task-activity context.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @since 2.0
 */
@SuppressWarnings("restriction")
public class ActivityContextManager implements IActivityContextManager {

	//private AbstractUserActivityMonitor userActivityMonitor;

	private final Set<IUserAttentionListener> attentionListeners = new CopyOnWriteArraySet<IUserAttentionListener>();

	private final CheckActivityJob checkJob;

	private volatile String lastInteractionOrigin;

	private IWorkingSet[] workingSets;

	private final List<AbstractUserActivityMonitor> activityMonitors;

	public static final String ACTIVITY_TIMEOUT = "org.eclipse.mylyn.monitor.ui.activity.timeout"; //$NON-NLS-1$

	public static final String ACTIVITY_TIMEOUT_ENABLED = "org.eclipse.mylyn.monitor.ui.activity.timeout.enabled"; //$NON-NLS-1$

	private final IPropertyChangeListener WORKING_SET_CHANGE_LISTENER = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (IWorkingSetManager.CHANGE_WORKING_SET_CONTENT_CHANGE.equals(event.getProperty())) {
				updateWorkingSetSelection();
			}
		}
	};

	public ActivityContextManager(List<AbstractUserActivityMonitor> monitors) {
		this.activityMonitors = new CopyOnWriteArrayList<AbstractUserActivityMonitor>(monitors);
		checkJob = new CheckActivityJob(new IActivityManagerCallback() {
			public void addMonitoredActivityTime(long localStartTime, long currentTime) {
				ActivityContextManager.this.addMonitoredActivityTime(localStartTime, currentTime);
			}

			public void inactive() {
				ActivityContextManager.this.fireInactive();
			}

			public long getLastEventTime() {
				return ActivityContextManager.this.getLastInteractionTime();
			}

			public void active() {
				ActivityContextManager.this.fireActive();
			}
		});
		checkJob.setSystem(true);
		checkJob.setPriority(Job.INTERACTIVE);
	}

	void init(List<AbstractUserActivityMonitor> monitors) {
		this.activityMonitors.addAll(monitors);
	}

	protected void updateWorkingSetSelection() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window != null) {
					IWorkbenchPage page = window.getActivePage();
					workingSets = page.getWorkingSets();
				}
			}
		});
	}

	public void start() {
		for (final AbstractUserActivityMonitor monitor : activityMonitors) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					disableFailedMonitor(monitor, e);
				}

				public void run() throws Exception {
					monitor.start();
				}
			});
		}
		updateWorkingSetSelection();
		PlatformUI.getWorkbench().getWorkingSetManager().addPropertyChangeListener(WORKING_SET_CHANGE_LISTENER);
		checkJob.reschedule();
	}

	public void stop() {
		for (final AbstractUserActivityMonitor monitor : activityMonitors) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					disableFailedMonitor(monitor, e);
				}

				public void run() throws Exception {
					monitor.stop();
				}
			});
		}
		PlatformUI.getWorkbench().getWorkingSetManager().removePropertyChangeListener(WORKING_SET_CHANGE_LISTENER);
		checkJob.cancel();
	}

	public void addListener(IUserAttentionListener listener) {
		attentionListeners.add(listener);
	}

	public void removeListener(IUserAttentionListener listener) {
		attentionListeners.remove(listener);
	}

	private void addMonitoredActivityTime(long start, long end) {
		if ((end > 0 && start > 0) && (end > start)) {
			String origin = lastInteractionOrigin;
			if (origin == null) {
				origin = InteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH;
			}

			String handle = getStructureHandle();
			if (handle == null) {
				if (workingSets != null && workingSets.length > 0) {
					for (IWorkingSet workingSet : workingSets) {
						String workingSetName = workingSet.getName();
						processWorkbenchEvent(origin, InteractionContextManager.ACTIVITY_STRUCTUREKIND_WORKINGSET,
								workingSetName, start, end);
					}
				} else {
					processWorkbenchEvent(origin, InteractionContextManager.ACTIVITY_STRUCTUREKIND_WORKINGSET,
							InteractionContextManager.ACTIVITY_HANDLE_NONE, start, end);
				}
			} else {
				processWorkbenchEvent(origin, InteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, handle, start,
						end);
			}
		}
	}

	private void processWorkbenchEvent(String origin, String structureKind, String handle, long start, long end) {
		ContextCorePlugin.getContextManager().processActivityMetaContextEvent(
				new InteractionEvent(InteractionEvent.Kind.ATTENTION, structureKind, handle, origin, null,
						InteractionContextManager.ACTIVITY_DELTA_ADDED, 1f, new Date(start), new Date(end)));
	}

	public void addActivityTime(String handle, long start, long end) {
		if (handle != null) {
			ContextCorePlugin.getContextManager().processActivityMetaContextEvent(
					new InteractionEvent(InteractionEvent.Kind.ATTENTION,
							InteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, handle,
							InteractionContextManager.ACTIVITY_ORIGINID_USER, null,
							InteractionContextManager.ACTIVITY_DELTA_ADDED, 1f, new Date(start), new Date(end)));
		}
	}

	public void removeActivityTime(String handle, long start, long end) {
		if (handle != null) {
			ContextCorePlugin.getContextManager().processActivityMetaContextEvent(
					new InteractionEvent(InteractionEvent.Kind.ATTENTION,
							InteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING, handle,
							InteractionContextManager.ACTIVITY_ORIGINID_USER, null,
							InteractionContextManager.ACTIVITY_DELTA_REMOVED, 1f, new Date(start), new Date(end)));
		}
	}

	private void fireInactive() {
		for (IUserAttentionListener attentionListener : attentionListeners) {
			attentionListener.userAttentionLost();
		}
	}

	private void fireActive() {
		for (IUserAttentionListener attentionListener : attentionListeners) {
			attentionListener.userAttentionGained();
		}
	}

	public long getLastInteractionTime() {
		for (final AbstractUserActivityMonitor monitor : activityMonitors) {
			final boolean[] success = new boolean[1];
			final long[] result = new long[1];
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					disableFailedMonitor(monitor, e);
				}

				public void run() throws Exception {
					if (monitor.isEnabled()) {
						result[0] = monitor.getLastInteractionTime();
						lastInteractionOrigin = monitor.getOriginId();
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

	private void disableFailedMonitor(AbstractUserActivityMonitor monitor, Throwable e) {
		StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN,
				"Activity monitor ''{0}'' was disabled due to a failure")); //$NON-NLS-1$
		activityMonitors.remove(monitor);
	}

	public void setInactivityTimeout(int inactivityTimeout) {
		checkJob.setInactivityTimeout(inactivityTimeout);
	}

	public int getInactivityTimeout() {
		return checkJob.getInactivityTimeout();
	}

	/**
	 * @return null when no task is active
	 */
	public String getStructureHandle() {
		if (ContextCore.getContextManager().getActiveContext().getHandleIdentifier() != null) {
			return ContextCore.getContextManager().getActiveContext().getHandleIdentifier();
		}
		return null;
	}
}
