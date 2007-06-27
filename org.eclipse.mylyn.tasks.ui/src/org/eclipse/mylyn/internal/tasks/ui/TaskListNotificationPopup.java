/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.List;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Rob Elves
 */
public class TaskListNotificationPopup extends PopupDialog {

	private static final String NOTIFICATIONS_HIDDEN = " more changes...";

	private static final int NUM_NOTIFICATIONS_TO_DISPLAY = 3;

	private static final String MYLAR_NOTIFICATION_LABEL = "Mylyn Notification";

	private Form form;

	private Rectangle bounds;

	private List<ITaskListNotification> notifications;

	private Composite sectionClient;

	private FormToolkit toolkit;

	public TaskListNotificationPopup(Shell parent) {
		super(parent, PopupDialog.INFOPOPUP_SHELLSTYLE | SWT.ON_TOP, false, false, false, false, null, null);
		toolkit = new FormToolkit(parent.getDisplay());
	}

	public void setContents(List<ITaskListNotification> notifications) {
		this.notifications = notifications;
	}

	@Override
	protected Control createContents(Composite parent) {
		getShell().setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		return createDialogArea(parent);
	}

	@Override
	protected final Control createDialogArea(final Composite parent) {

		getShell().setText(MYLAR_NOTIFICATION_LABEL);

		form = toolkit.createForm(parent);
		form.getBody().setLayout(new GridLayout());

		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);

		section.setText(MYLAR_NOTIFICATION_LABEL);
		section.setLayout(new GridLayout());

		sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new GridLayout(2, false));
		int count = 0;
		for (final ITaskListNotification notification : notifications) {
			if (count < NUM_NOTIFICATIONS_TO_DISPLAY) {
				Label notificationLabelIcon = toolkit.createLabel(sectionClient, "");
				notificationLabelIcon.setImage(notification.getOverlayIcon());
				ImageHyperlink link = toolkit.createImageHyperlink(sectionClient, SWT.BEGINNING | SWT.WRAP);
				link.setText(notification.getLabel());
				link.setImage(notification.getNotificationIcon());
				link.addHyperlinkListener(new HyperlinkAdapter() {
					@Override
					public void linkActivated(HyperlinkEvent e) {
						notification.openTask();
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

				String descriptionText = null;
				if (notification.getDescription() != null) {
					descriptionText = notification.getDescription();
				}
				if (descriptionText != null) {
					Label descriptionLabel = toolkit.createLabel(sectionClient, descriptionText);
					GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).applyTo(descriptionLabel);
				}
			} else {
				int numNotificationsRemain = notifications.size() - count;
				Hyperlink remainingHyperlink = toolkit.createHyperlink(sectionClient, numNotificationsRemain
						+ NOTIFICATIONS_HIDDEN, SWT.NONE);
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

		section.setClient(sectionClient);

		ImageHyperlink hyperlink = new ImageHyperlink(section, SWT.NONE);
		toolkit.adapt(hyperlink, true, true);
		hyperlink.setBackground(null);
		hyperlink.setImage(TasksUiImages.getImage(TasksUiImages.NOTIFICATION_CLOSE));
		hyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				close();
			}
		});

		section.setTextClient(hyperlink);

		parent.pack();
		return form; 
	}

	/**
	 * Initialize the shell's bounds.
	 */
	@Override
	public void initializeBounds() {
		getShell().setBounds(restoreBounds());
	}

	private Rectangle restoreBounds() {
		bounds = getShell().getBounds();
		Rectangle maxBounds = null;

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			maxBounds = window.getShell().getMonitor().getClientArea();
		} else {
			// fallback
			Display display = Display.getCurrent();
			if (display == null)
				display = Display.getDefault();
			if (display != null && !display.isDisposed())
				maxBounds = display.getPrimaryMonitor().getClientArea();
		}

		if (bounds.width > -1 && bounds.height > -1) {
			if (maxBounds != null) {
				bounds.width = Math.min(bounds.width, maxBounds.width);
				bounds.height = Math.min(bounds.height, maxBounds.height);
			}
			// Enforce an absolute minimal size
			bounds.width = Math.max(bounds.width, 30);
			bounds.height = Math.max(bounds.height, 30);
		}

		if (bounds.x > -1 && bounds.y > -1 && maxBounds != null) {
			// bounds.x = Math.max(bounds.x, maxBounds.x);
			// bounds.y = Math.max(bounds.y, maxBounds.y);

			if (bounds.width > -1 && bounds.height > -1) {
				bounds.x = maxBounds.x + maxBounds.width - bounds.width;
				bounds.y = maxBounds.y + maxBounds.height - bounds.height;
			}
		}

		return bounds;
	}

	@Override
	public boolean close() {
		if (toolkit != null) {
			if (toolkit.getColors() != null) {
				toolkit.dispose();
			}
		}
		return super.close();
	}
}
