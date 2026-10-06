/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.notifications.ui.popup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.notifications.NotificationPopup;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.commons.notifications.core.NotificationSink;
import org.eclipse.mylyn.commons.notifications.core.NotificationSinkEvent;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class PopupNotificationSink extends NotificationSink {

	private static final long DELAY_OPEN = 1 * 1000;

	private static final boolean runSystem = true;

	private final WeakHashMap<Object, Object> cancelledTokens = new WeakHashMap<>();

	private final Set<AbstractNotification> notifications = new HashSet<>();

	private final Set<AbstractNotification> currentlyNotifying = Collections.synchronizedSet(notifications);

	private NotificationPopup popup;

	private final Job openJob = new Job(Messages.PopupNotificationSink_Popup_Noifier_Job_Label) {
		private static NotificationPopupContent getPopupContent(NotificationPopup notificationPopup)
				throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			Field field = NotificationPopup.class.getDeclaredField("contentCreator"); //$NON-NLS-1$
			field.setAccessible(true);
			return (NotificationPopupContent) field.get(notificationPopup);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				if (Platform.isRunning() && PlatformUI.getWorkbench() != null
						&& PlatformUI.getWorkbench().getDisplay() != null
						&& !PlatformUI.getWorkbench().getDisplay().isDisposed()) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
						collectNotifications();

						try {
							if (popup != null && popup.getReturnCode() == Window.CANCEL) {
								NotificationPopupContent popupContent = getPopupContent(popup);
								List<AbstractNotification> notifications = popupContent != null
										? popupContent.getNotifications()
												: Collections.emptyList();
								notifications.stream()
								.filter(notification -> notification.getToken() != null)
								.forEach(notification -> cancelledTokens.put(notification.getToken(), null));
							}

							currentlyNotifying.removeIf(notification -> notification.getToken() != null
									&& cancelledTokens.containsKey(notification.getToken()));
						} catch (NoSuchFieldException | IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						synchronized (PopupNotificationSink.class) {
							if (!currentlyNotifying.isEmpty()) {
								showPopup();
							}
						}
					});
				}
			} finally {
				if (popup != null) {
					schedule(popup.getDelayClose() / 2);
				}
			}

			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			return Status.OK_STATUS;
		}

	};

	public PopupNotificationSink() {
		openJob.setSystem(runSystem);
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
		openJob.schedule(DELAY_OPEN);
	}

	public void showPopup() {
		if (popup != null) {
			popup.close();
		}
		Display display = PlatformUI.getWorkbench().getDisplay();
		List<AbstractNotification> notificationsToDisplay = new ArrayList<>(currentlyNotifying);
		Collections.sort(notificationsToDisplay);
		cleanNotified();
		NotificationPopupContent popupContent = new NotificationPopupContent(notificationsToDisplay);
		popup = NotificationPopup.forDisplay(display) //
				.fadeIn(true) //
				.content(popupContent) //
				.titleImage(WorkbenchUtil.getWorkbenchShellImage(16)) //
				.build();

		popup.open();

	}
}