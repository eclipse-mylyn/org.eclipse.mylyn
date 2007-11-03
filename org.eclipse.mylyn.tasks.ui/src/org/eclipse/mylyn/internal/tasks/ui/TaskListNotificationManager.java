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
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 */
public class TaskListNotificationManager implements IPropertyChangeListener {

	private static final String CLOSE_NOTIFICATION_JOB = "Close Notification Job";

	private static final String OPEN_NOTIFICATION_JOB = "Open Notification Job";

	private static final long CLOSE_POPUP_DELAY = 1000 * 12;

	private static final long OPEN_POPUP_DELAY = 1000 * 30;

	private static final boolean runSystem = true;

	private TaskListNotificationPopup popup;

	private Set<ITaskListNotification> notifications = new HashSet<ITaskListNotification>();

	private Set<ITaskListNotification> currentlyNotifying = Collections.synchronizedSet(notifications);

	private List<ITaskListNotificationProvider> notificationProviders = new ArrayList<ITaskListNotificationProvider>();

	private Job openJob = new Job(OPEN_NOTIFICATION_JOB) {
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {

				if (!PlatformUI.getWorkbench().getDisplay().isDisposed()) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

						public void run() {
							if ((popup != null && popup.close()) || popup == null) {
								closeJob.cancel();
								collectNotifications();
								//setNotified();
								synchronized (TaskListNotificationManager.class) {
									if (currentlyNotifying.size() > 0) {
										popup = new TaskListNotificationPopup(new Shell(PlatformUI.getWorkbench()
												.getDisplay()));
										List<ITaskListNotification> toDisplay = new ArrayList<ITaskListNotification>(
												currentlyNotifying);
										Collections.sort(toDisplay);
										popup.setContents(toDisplay);
										cleanNotified();
										popup.setBlockOnOpen(false);
										popup.open();
										
//										for (int i = 2; i <= 6; i+= 2) {
//											popup.getShell().setLocation(popup.getShell().getLocation().x, popup.getShell().getLocation().y - i);
//											try {
//												Thread.sleep(70);
//											} catch (InterruptedException e) {
//												// ignore
//											}
//										}
										closeJob.setSystem(runSystem);
										closeJob.schedule(CLOSE_POPUP_DELAY);
										popup.getShell().addShellListener(SHELL_LISTENER);
									}
								}
							}

						}
					});
				}
			} finally {
				schedule(OPEN_POPUP_DELAY);
			}

			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

			return Status.OK_STATUS;
		}

	};

	private Job closeJob = new Job(CLOSE_NOTIFICATION_JOB) {

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			if (!PlatformUI.getWorkbench().getDisplay().isDisposed()) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (popup != null) {
							synchronized (popup) {
								popup.close();
							}
						}
					}
				});
			}
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;

			return Status.OK_STATUS;
		}

	};

	private ShellListener SHELL_LISTENER = new ShellListener() {

		public void shellClosed(ShellEvent arg0) {
		}

		public void shellDeactivated(ShellEvent arg0) {
			popup.close();
			// don't want notifications right away
			openJob.cancel();
			openJob.setSystem(runSystem);
			openJob.schedule(OPEN_POPUP_DELAY);
		}

		public void shellActivated(ShellEvent arg0) {
			closeJob.cancel();
		}

		public void shellDeiconified(ShellEvent arg0) {
			// ingore
		}

		public void shellIconified(ShellEvent arg0) {
			// ignore
		}
	};

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
		closeJob.cancel();
		if (popup != null) {
			popup.close();
		}
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
	public Set<ITaskListNotification> getNotifications() {
		synchronized (TaskListNotificationManager.class) {
			return currentlyNotifying;
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(TasksUiPreferenceConstants.NOTIFICATIONS_ENABLED)) {
			Object newValue = event.getNewValue();
			if (!(newValue instanceof Boolean)) {
				// default if no preference value
				startNotification(OPEN_POPUP_DELAY);
			} else if ((Boolean) newValue == true) {
				startNotification(OPEN_POPUP_DELAY);
			} else {
				stopNotification();
			}
		}
	}
}
