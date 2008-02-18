/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.notifications;

import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.tasks.ui.TaskListHyperlink;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class TaskListNotificationPopup extends AbstractNotificationPopup {

	private static final String NOTIFICATIONS_HIDDEN = "more, Open Task List to view";

	private static final int NUM_NOTIFICATIONS_TO_DISPLAY = 4;

	private List<AbstractNotification> notifications;

	public TaskListNotificationPopup(Shell parent) {
		super(parent.getDisplay());
	}

	public void setContents(List<AbstractNotification> notifications) {
		this.notifications = notifications;
	}

	@Override
	protected void createTitleArea(Composite parent) {
		super.createTitleArea(parent);
	}

	@Override
	protected void createContentArea(Composite parent) {
		int count = 0;
		for (final AbstractNotification notification : notifications) {
			Composite notificationComposite = new Composite(parent, SWT.NO_FOCUS);
			notificationComposite.setLayout(new GridLayout(2, false));
			notificationComposite.setBackground(parent.getBackground());

			if (count < NUM_NOTIFICATIONS_TO_DISPLAY) {
				final Label notificationLabelIcon = new Label(notificationComposite, SWT.NO_FOCUS);
				notificationLabelIcon.setBackground(parent.getBackground());
				notificationLabelIcon.setImage(notification.getNotificationKindImage());
				if (!(notification instanceof TaskListNotificationReminder)) {
					final AbstractTask task = (AbstractTask) notification.getAdapter(AbstractTask.class);
					if (task != null) {
						notificationLabelIcon.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseUp(MouseEvent e) {
								TasksUiPlugin.getSynchronizationManager().setTaskRead(task, true);
								notificationLabelIcon.setImage(null);
								notificationLabelIcon.setToolTipText(null);
							}
						});
						notificationLabelIcon.setToolTipText("Mark Task Read");
					}
				}

				final TaskListHyperlink itemLink = new TaskListHyperlink(notificationComposite, SWT.BEGINNING
						| SWT.WRAP | SWT.NO_FOCUS);
				itemLink.setText(notification.getLabel());
				itemLink.setImage(notification.getNotificationImage());
				itemLink.setBackground(parent.getBackground());
				itemLink.addHyperlinkListener(new HyperlinkAdapter() {
					@Override
					public void linkActivated(HyperlinkEvent e) {
						notification.open();
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
				if (descriptionText != null && !descriptionText.trim().equals("")) {
					Label descriptionLabel = new Label(notificationComposite, SWT.NO_FOCUS);
					descriptionLabel.setText(descriptionText);
					descriptionLabel.setBackground(parent.getBackground());
					GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).applyTo(descriptionLabel);
				}
			} else {
				int numNotificationsRemain = notifications.size() - count;
				TaskListHyperlink remainingHyperlink = new TaskListHyperlink(notificationComposite, SWT.NO_FOCUS);
				remainingHyperlink.setBackground(parent.getBackground());

				remainingHyperlink.setText(numNotificationsRemain + " " + NOTIFICATIONS_HIDDEN);
				GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).applyTo(remainingHyperlink);
				remainingHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
					@Override
					public void linkActivated(HyperlinkEvent e) {
						TaskListView.openInActivePerspective().setFocus();
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
	}

}
