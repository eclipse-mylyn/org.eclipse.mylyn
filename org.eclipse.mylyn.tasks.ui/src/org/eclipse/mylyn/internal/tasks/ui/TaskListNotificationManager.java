/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractNotification;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskListNotificationPopup;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 */
public class TaskListNotificationManager implements IPropertyChangeListener {

	private static final String OPEN_NOTIFICATION_JOB = "Open Notification Job";

	private static final long DELAY_OPEN = 5 * 1000;

	private static final boolean runSystem = true;

	private TaskListNotificationPopup popup;

	private final Set<AbstractNotification> notifications = new HashSet<AbstractNotification>();

	private final Set<AbstractNotification> currentlyNotifying = Collections.synchronizedSet(notifications);

	private final List<ITaskListNotificationProvider> notificationProviders = new ArrayList<ITaskListNotificationProvider>();

	private final Job openJob = new Job(OPEN_NOTIFICATION_JOB) {
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {

				if (Platform.isRunning() && PlatformUI.getWorkbench() != null
						&& PlatformUI.getWorkbench().getDisplay() != null
						&& !PlatformUI.getWorkbench().getDisplay().isDisposed()) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

						public void run() {
							collectNotifications();
							synchronized (TaskListNotificationManager.class) {
								if (currentlyNotifying.size() > 0) {
//										popup.close();
									showPopup();
								}
							}
						}
					});
				}
			} finally {
				if (popup != null) {
					schedule(popup.getDelayClose() / 2);
				} else {
					schedule(DELAY_OPEN);
				}
			}

			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			return Status.OK_STATUS;
		}

	};

	public void showPopup() {
		if (popup != null) {
			popup.close();
		}

		Shell shell = new Shell(PlatformUI.getWorkbench().getDisplay());
		popup = new TaskListNotificationPopup(shell);
		popup.setFadingEnabled(TasksUiInternal.isAnimationsEnabled());
		List<AbstractNotification> toDisplay = new ArrayList<AbstractNotification>(currentlyNotifying);
		Collections.sort(toDisplay);
		popup.setContents(toDisplay);
		cleanNotified();
		popup.setBlockOnOpen(false);
		popup.open();
	}

	private void cleanNotified() {
		currentlyNotifying.clear();
	}

	/** public for testing */
	public void collectNotifications() {
		for (ITaskListNotificationProvider provider : notificationProviders) {
			currentlyNotifying.addAll(provider.getNotifications());
		}
	}

	public void startNotification(long initialStartupTime) {
		if (TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(TasksUiPreferenceConstants.NOTIFICATIONS_ENABLED)) {
			if (!openJob.cancel()) {
				try {
					openJob.join();
				} catch (InterruptedException e) {
					// ignore
				}
			}
			openJob.setSystem(runSystem);
			openJob.schedule(initialStartupTime);
		}
	}

	public void stopNotification() {
		openJob.cancel();
//		closeJob.cancel();
//		if (popup != null) {
//			popup.close();
//		}
	}

	public void addNotificationProvider(ITaskListNotificationProvider notification_provider) {
		notificationProviders.add(notification_provider);
	}

	public void removeNotificationProvider(ITaskListNotificationProvider notification_provider) {
		notificationProviders.remove(notification_provider);
	}

	/**
	 * public for testing purposes
	 */
	public Set<AbstractNotification> getNotifications() {
		synchronized (TaskListNotificationManager.class) {
			return currentlyNotifying;
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(TasksUiPreferenceConstants.NOTIFICATIONS_ENABLED)) {
			Object newValue = event.getNewValue();
			if (!(newValue instanceof Boolean)) {
				// default if no preference value
				startNotification(0);
			} else if ((Boolean) newValue == true) {
				startNotification(0);
			} else {
				stopNotification();
			}
		}
	}
}
