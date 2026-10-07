/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.notifications.ui.popup;

import java.util.List;
import java.util.function.Function;

import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.mylyn.commons.workbench.forms.ScalingHyperlink;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

/**
 * Creates the content of the notification popup.
 *
 * @author Rob Elves
 * @author Mik Kersten
 */
public class NotificationPopupContent implements Function<Composite, Control> {

	private static final int NUM_NOTIFICATIONS_TO_DISPLAY = 4;

	private final List<AbstractNotification> notifications;

	public NotificationPopupContent(List<AbstractNotification> notifications) {
		this.notifications = notifications;
	}

	@Override
	public Control apply(Composite popupParent) {
		Composite parent = new Composite(popupParent, SWT.NO_FOCUS);
		GridLayoutFactory.fillDefaults().applyTo(parent);
		parent.setBackground(popupParent.getBackground());
		int count = 0;
		for (final AbstractNotification notification : notifications) {
			Composite notificationComposite = new Composite(parent, SWT.NO_FOCUS);
			GridLayout gridLayout = new GridLayout(2, false);
			GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(notificationComposite);
			notificationComposite.setLayout(gridLayout);
			notificationComposite.setBackground(parent.getBackground());

			if (count < NUM_NOTIFICATIONS_TO_DISPLAY) {
				final Label notificationLabelIcon = new Label(notificationComposite, SWT.NO_FOCUS);
				notificationLabelIcon.setBackground(parent.getBackground());
				if (notification instanceof AbstractUiNotification) {
					notificationLabelIcon.setImage(((AbstractUiNotification) notification).getNotificationKindImage());
				}
				// FIXME
//				if (!(notification instanceof TaskListNotificationReminder)) {
//					final AbstractTask task = (AbstractTask) notification.getAdapter(AbstractTask.class);
//					if (task != null) {
//						notificationLabelIcon.addMouseListener(new MouseAdapter() {
//							@Override
//							public void mouseUp(MouseEvent e) {
//								TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
//								notificationLabelIcon.setImage(null);
//								notificationLabelIcon.setToolTipText(null);
//							}
//						});
//						notificationLabelIcon.setToolTipText(Messages.TaskListNotificationPopup_Mark_Task_Read);
//					}
//				}

				// FIXME
//				final TaskScalingHyperlink itemLink = new TaskScalingHyperlink(notificationComposite, SWT.BEGINNING
//						| SWT.NO_FOCUS);

				final ScalingHyperlink itemLink = new ScalingHyperlink(notificationComposite,
						SWT.BEGINNING | SWT.NO_FOCUS);
				GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(itemLink);
				itemLink.registerMouseTrackListener();
				itemLink.setText(LegacyActionTools.escapeMnemonics(notification.getLabel()));
				if (notification instanceof AbstractUiNotification) {
					itemLink.setImage(((AbstractUiNotification) notification).getNotificationImage());
				}
				itemLink.setBackground(parent.getBackground());
				itemLink.addHyperlinkListener(new HyperlinkAdapter() {
					@Override
					public void linkActivated(HyperlinkEvent e) {
						if (notification instanceof AbstractUiNotification) {
							((AbstractUiNotification) notification).open();
						}
						IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
						if (window != null) {
							Shell windowShell = window.getShell();
							if (windowShell != null) {
								if (windowShell.getMinimized()) {
									windowShell.setMinimized(false);
								}

								windowShell.open();
								windowShell.forceActive();
							}
						}
					}
				});

				String descriptionText = null;
				if (notification.getDescription() != null) {
					descriptionText = notification.getDescription();
				}
				if (descriptionText != null && !descriptionText.trim().equals("")) { //$NON-NLS-1$
					Label descriptionLabel = new Label(notificationComposite, SWT.NO_FOCUS);
					descriptionLabel.setText(LegacyActionTools.escapeMnemonics(descriptionText));
					descriptionLabel.setBackground(parent.getBackground());
					GridDataFactory.fillDefaults()
							.span(2, SWT.DEFAULT)
							.grab(true, false)
							.align(SWT.FILL, SWT.TOP)
							.applyTo(descriptionLabel);
				}
			} else {
				int numNotificationsRemain = notifications.size() - count;
				ScalingHyperlink remainingLink = new ScalingHyperlink(notificationComposite, SWT.NO_FOCUS);
				remainingLink.registerMouseTrackListener();
				remainingLink.setBackground(parent.getBackground());

				remainingLink.setText(NLS.bind("{0} more", numNotificationsRemain)); //$NON-NLS-1$
				GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).applyTo(remainingLink);
				remainingLink.addHyperlinkListener(new HyperlinkAdapter() {
					@Override
					public void linkActivated(HyperlinkEvent e) {
						// FIXME
						//						TasksUiUtil.openTasksViewInActivePerspective().setFocus();
						IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
						if (window != null) {
							Shell windowShell = window.getShell();
							if (windowShell != null) {
								windowShell.setMaximized(true);
								windowShell.open();
							}
						}
					}
				});
				break;
			}
			count++;
		}
		return parent;
	}

}
