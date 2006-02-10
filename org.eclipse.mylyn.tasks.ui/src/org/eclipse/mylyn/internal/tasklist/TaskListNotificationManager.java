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

package org.eclipse.mylar.internal.tasklist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 */
public final class TaskListNotificationManager {

	private static final String CLOSE_NOTIFICATION_JOB = "Close Notification Job";

	private static final String OPEN_NOTIFICATION_JOB = "Open Notification Job";

	private static final long CLOSE_POPUP_DELAY = 1000 * 10;

	private static final long OPEN_POPUP_DELAY = 1000 * 60;

	private static TaskListNotificationPopup popup;

	private static List<ITaskListNotification> notifications = new ArrayList<ITaskListNotification>();

	private static List<ITaskListNotification> currentlyNotifying = Collections.synchronizedList(notifications);

	private static Job openJob = new Job(OPEN_NOTIFICATION_JOB) {
		@Override
		protected IStatus run(IProgressMonitor monitor) {

			try {

				if (!PlatformUI.getWorkbench().getDisplay().isDisposed()) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							if ((popup != null && popup.close()) || popup == null) {
								closeJob.cancel();
								cleanNotified();
								synchronized (currentlyNotifying) {
									if (currentlyNotifying.size() > 0) {
										popup = new TaskListNotificationPopup(PlatformUI.getWorkbench().getDisplay()
												.getActiveShell());
										popup.setContents(currentlyNotifying);
										popup.setBlockOnOpen(false);
										popup.open();
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

	private static Job closeJob = new Job(CLOSE_NOTIFICATION_JOB) {

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

	private static ShellListener SHELL_LISTENER = new ShellListener() {

		public void shellClosed(ShellEvent arg0) {
		}

		/**
		 * bug 1002249: too slow to save state here
		 */
		public void shellDeactivated(ShellEvent arg0) {
			popup.close();
			// don't want notifications right away
			openJob.cancel();
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

	private static void cleanNotified() {
		synchronized (currentlyNotifying) {
			for (ITaskListNotification notification : currentlyNotifying)
				if (notification.isNotified())
					currentlyNotifying.remove(notification);
		}
	}

	public static void notify(List<ITaskListNotification> toNotify) {
		synchronized (currentlyNotifying) {
			currentlyNotifying.removeAll(toNotify);
			currentlyNotifying.addAll(0, toNotify);
		}
	}

	public static void notify(ITaskListNotification toNotify) {
		synchronized (currentlyNotifying) {
			currentlyNotifying.remove(toNotify);
			currentlyNotifying.add(0, toNotify);
		}
	}

	public static void startNotification() {
		openJob.schedule(OPEN_POPUP_DELAY);
	}

	public static void stopNotification() {
		openJob.cancel();
	}

	/**
	 * For testing purposes
	 */
	public static List<ITaskListNotification> getNotifications() {
		synchronized (currentlyNotifying) {
			return Collections.unmodifiableList(notifications);
		}
	}

	/**
	 * For testing purposes
	 */
	public static void clearNotifications() {
		synchronized (currentlyNotifying) {
			currentlyNotifying.clear();
		}
	}

}
