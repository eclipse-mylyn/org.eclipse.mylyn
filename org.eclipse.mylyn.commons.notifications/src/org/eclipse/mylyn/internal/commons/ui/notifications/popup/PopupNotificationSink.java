/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui.notifications.popup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.ui.notifications.AbstractNotification;
import org.eclipse.mylyn.commons.ui.notifications.NotificationSink;
import org.eclipse.mylyn.commons.ui.notifications.NotificationSinkEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class PopupNotificationSink extends NotificationSink {

	private static final long DELAY_OPEN = 5 * 1000;

	private static final boolean runSystem = true;

	private final WeakHashMap<Object, Object> cancelledTokens = new WeakHashMap<Object, Object>();

	private final Set<AbstractNotification> notifications = new HashSet<AbstractNotification>();

	private final Set<AbstractNotification> currentlyNotifying = Collections.synchronizedSet(notifications);

	private final Job openJob = new Job("Popup Notifier") {
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				if (Platform.isRunning() && PlatformUI.getWorkbench() != null
						&& PlatformUI.getWorkbench().getDisplay() != null
						&& !PlatformUI.getWorkbench().getDisplay().isDisposed()) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

						public void run() {
							collectNotifications();

							if (popup != null && popup.getReturnCode() == Window.CANCEL) {
								List<AbstractNotification> notifications = popup.getNotifications();
								for (AbstractNotification notification : notifications) {
									if (notification.getToken() != null) {
										cancelledTokens.put(notification.getToken(), null);
									}
								}
							}

							for (Iterator<AbstractNotification> it = currentlyNotifying.iterator(); it.hasNext();) {
								AbstractNotification notification = it.next();
								if (notification.getToken() != null
										&& cancelledTokens.containsKey(notification.getToken())) {
									it.remove();
								}
							}

							synchronized (PopupNotificationSink.class) {
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

	private NotificationPopup popup;

	public PopupNotificationSink() {
	}

	private void cleanNotified() {
		currentlyNotifying.clear();
	}

	/** public for testing */
	public void collectNotifications() {
	}

	/**
	 * public for testing purposes
	 */
	public Set<AbstractNotification> getNotifications() {
		synchronized (PopupNotificationSink.class) {
			return currentlyNotifying;
		}
	}

	public boolean isAnimationsEnabled() {
		IPreferenceStore store = PlatformUI.getPreferenceStore();
		return store.getBoolean(IWorkbenchPreferenceConstants.ENABLE_ANIMATIONS);
	}

	@Override
	public void notify(NotificationSinkEvent event) {
		currentlyNotifying.addAll(event.getNotifications());

		if (!openJob.cancel()) {
			try {
				openJob.join();
			} catch (InterruptedException e) {
				// ignore
			}
		}
		openJob.setSystem(runSystem);
		openJob.schedule(DELAY_OPEN);
	}

	public void showPopup() {
		if (popup != null) {
			popup.close();
		}

		Shell shell = new Shell(PlatformUI.getWorkbench().getDisplay());
		popup = new NotificationPopup(shell);
		popup.setFadingEnabled(isAnimationsEnabled());
		List<AbstractNotification> toDisplay = new ArrayList<AbstractNotification>(currentlyNotifying);
		Collections.sort(toDisplay);
		popup.setContents(toDisplay);
		cleanNotified();
		popup.setBlockOnOpen(false);
		popup.open();
	}

}
