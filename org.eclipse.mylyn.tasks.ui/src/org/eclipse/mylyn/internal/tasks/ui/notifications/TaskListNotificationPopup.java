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
import org.eclipse.swt.SWT;
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

	protected void createTitleArea(Composite parent) {
		super.createTitleArea(parent);
	}

	protected void createContentArea(Composite parent) {
		int count = 0;
		for (final AbstractNotification notification : notifications) {
			Composite notificationComposite = new Composite(parent, SWT.NULL);
			notificationComposite.setLayout(new GridLayout(2, false));
			notificationComposite.setBackground(parent.getBackground());
			
			if (count < NUM_NOTIFICATIONS_TO_DISPLAY) {
				Label notificationLabelIcon = new Label(notificationComposite, SWT.NONE);
				notificationLabelIcon.setImage(notification.getNotificationKindImage());
				final TaskListHyperlink itemLink = new TaskListHyperlink(notificationComposite, SWT.BEGINNING | SWT.WRAP);
				
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
				if (descriptionText != null && !descriptionText.trim().equals("")) {
					Label descriptionLabel = new Label(notificationComposite, SWT.NULL);
					descriptionLabel.setText(descriptionText);
					descriptionLabel.setBackground(parent.getBackground());
					GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).applyTo(descriptionLabel);
				}
			} else {
				int numNotificationsRemain = notifications.size() - count;
				TaskListHyperlink remainingHyperlink = new TaskListHyperlink(notificationComposite, SWT.NONE);
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
	
//	protected final Control createDialogArea(final Composite parent) {
//
//		getShell().setText(LABEL_NOTIFICATION);
//
//		form = toolkit.createForm(parent);
//		form.getBody().setLayout(new GridLayout());
//
//		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
//
//		section.setText(LABEL_NOTIFICATION);
//		section.setLayout(new GridLayout());
//
//		sectionClient = toolkit.createComposite(section);
//		sectionClient.setLayout(new GridLayout(2, false));
//		int count = 0;
//		for (final AbstractNotification notification : notifications) {
//			if (count < NUM_NOTIFICATIONS_TO_DISPLAY) {
//				Label notificationLabelIcon = toolkit.createLabel(sectionClient, "");
//				notificationLabelIcon.setImage(notification.getOverlayIcon());
//				ImageHyperlink link = toolkit.createImageHyperlink(sectionClient, SWT.BEGINNING | SWT.WRAP);
//				link.setText(notification.getLabel());
//				link.setImage(notification.getNotificationIcon());
//				link.addHyperlinkListener(new HyperlinkAdapter() {
//					@Override
//					public void linkActivated(HyperlinkEvent e) {
//						notification.open();
//						IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//						if (window != null) {
//							Shell windowShell = window.getShell();
//							if (windowShell != null) {
//								windowShell.setMaximized(true);
//								windowShell.open();
//							}
//						}
//					}
//				});
//
//				String descriptionText = null;
//				if (notification.getDescription() != null) {
//					descriptionText = notification.getDescription();
//				}
//				if (descriptionText != null) {
//					Label descriptionLabel = toolkit.createLabel(sectionClient, descriptionText);
//					GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).applyTo(descriptionLabel);
//				}
//			} else {
//				int numNotificationsRemain = notifications.size() - count;
//				Hyperlink remainingHyperlink = toolkit.createHyperlink(sectionClient, numNotificationsRemain
//						+ NOTIFICATIONS_HIDDEN, SWT.NONE);
//				GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).applyTo(remainingHyperlink);
//				remainingHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
//
//					@Override
//					public void linkActivated(HyperlinkEvent e) {
//						TaskListView.openInActivePerspective().setFocus();
//						IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//						if (window != null) {
//							Shell windowShell = window.getShell();
//							if (windowShell != null) {
//								windowShell.setMaximized(true);
//								windowShell.open();
//							}
//						}
//					}
//				});
//				break;
//			}
//			count++;
//		}
//
//		section.setClient(sectionClient);
//
//		ImageHyperlink hyperlink = new ImageHyperlink(section, SWT.NONE);
//		toolkit.adapt(hyperlink, true, true);
//		hyperlink.setBackground(null);
//		hyperlink.setImage(TasksUiImages.getImage(TasksUiImages.NOTIFICATION_CLOSE));
//		hyperlink.addHyperlinkListener(new HyperlinkAdapter() {
//			@Override
//			public void linkActivated(HyperlinkEvent e) {
//				close();
//			}
//		});
//
//		section.setTextClient(hyperlink);
//
//		parent.pack();
//		return form; 
//	}

//	@Override
//	public void initializeBounds() {
//		super.initializeBounds();
//		getShell().setBounds(restoreBounds());
//	}

//	private Rectangle restoreBounds() {
//		bounds = getShell().getBounds();
//		Rectangle maxBounds = null;
//
//		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//		if (window != null) {
//			maxBounds = window.getShell().getMonitor().getClientArea();
//		} else {
//			// fallback
//			Display display = Display.getCurrent();
//			if (display == null)
//				display = Display.getDefault();
//			if (display != null && !display.isDisposed())
//				maxBounds = display.getPrimaryMonitor().getClientArea();
//		}
//
//		if (bounds.width > -1 && bounds.height > -1) {
//			if (maxBounds != null) {
//				bounds.width = Math.min(bounds.width, maxBounds.width);
//				bounds.height = Math.min(bounds.height, maxBounds.height);
//			}
//			// Enforce an absolute minimal size
//			bounds.width = Math.max(bounds.width, 30);
//			bounds.height = Math.max(bounds.height, 30);
//		}
//
//		if (bounds.x > -1 && bounds.y > -1 && maxBounds != null) {
//			// bounds.x = Math.max(bounds.x, maxBounds.x);
//			// bounds.y = Math.max(bounds.y, maxBounds.y);
//
//			if (bounds.width > -1 && bounds.height > -1) {
//				bounds.x = maxBounds.x + maxBounds.width - bounds.width;
//				bounds.y = maxBounds.y + maxBounds.height - bounds.height;
//			}
//		}
//
//		return bounds;
//	}

//	@Override
//	public boolean close() {
//		if (toolkit != null) {
//			if (toolkit.getColors() != null) {
//				toolkit.dispose();
//			}
//		}
//		return super.close();
//	}
}
