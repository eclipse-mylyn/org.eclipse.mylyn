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

package org.eclipse.mylar.internal.tasklist.ui;

import java.util.List;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.mylar.internal.tasklist.ITaskListNotification;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Rob Elves
 */
public class TaskListNotificationPopup extends PopupDialog {

	private static final int BUTTON_FONT_SIZE = 7;

	static boolean takeFocusOnOpen = false;

	static boolean persistBounds = false;

	static boolean showDialogMenu = false;

	static boolean showPersistAction = false;

	static String titleText;

	static final String MYLAR_NOTIFICATION_LABEL = "Mylar Notification";

	static String infoText = null;

	private FormToolkit toolkit;

	private Form form;

	private Rectangle bounds;

	List<ITaskListNotification> notifications;

	private Composite sectionClient;

	public TaskListNotificationPopup(Shell parent) {
		super(parent, PopupDialog.INFOPOPUP_SHELLSTYLE, takeFocusOnOpen, persistBounds, showDialogMenu,
				showPersistAction, titleText, infoText);
	}

	public void setContents(List<ITaskListNotification> notifications) {
		this.notifications = notifications;
	}

	protected Control createContents(Composite parent) {
		getShell().setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY));
		return createDialogArea(parent);
	}

	protected final Control createDialogArea(final Composite parent) {

		getShell().setText(MYLAR_NOTIFICATION_LABEL);

		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);
		form.getBody().setLayout(new GridLayout());

		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);

		section.setText(MYLAR_NOTIFICATION_LABEL);
		section.setLayout(new GridLayout());

		sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		for (final ITaskListNotification notification : notifications) {
			ImageHyperlink link = toolkit.createImageHyperlink(sectionClient, SWT.WRAP | SWT.TOP);
			link.setText(notification.getDescription());
			link.setImage(notification.getNotificationIcon());
			link.addHyperlinkListener(new HyperlinkAdapter() {
				public void linkActivated(HyperlinkEvent e) {
					notification.setNotified(true);
					notification.openResource();
					IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					if (window != null) {
						Shell windowShell = window.getShell();
						if (windowShell != null) {
							windowShell.moveAbove(null);
							windowShell.open();
						}
					}
				}
			});
		}

		section.setClient(sectionClient);

		Composite buttonsComposite = toolkit.createComposite(sectionClient);
		buttonsComposite.setLayout(new RowLayout());
		Button buttonOpenAll = toolkit.createButton(buttonsComposite, "Open All", SWT.NONE);

		{
			Font initialFont = buttonOpenAll.getFont();
			FontData[] fontData = initialFont.getFontData();
			for (int i = 0; i < fontData.length; i++) {
				fontData[i].setHeight(BUTTON_FONT_SIZE);
			}
			Font newFont = new Font(getShell().getDisplay(), fontData);
			buttonOpenAll.setFont(newFont);
		}
		buttonOpenAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				for (ITaskListNotification notification : notifications) {
					notification.setNotified(true);
					notification.openResource();
				}
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window != null) {
					Shell windowShell = window.getShell();
					if (windowShell != null) {
						windowShell.open();
					}
				}
				close();
			}
		});

		RowData buttonOpenAllRowData = new RowData(45, 15);
		buttonOpenAll.setLayoutData(buttonOpenAllRowData);

		Button buttonDismiss = toolkit.createButton(buttonsComposite, "Close", SWT.NONE);

		{
			Font initialFont = buttonDismiss.getFont();
			FontData[] fontData = initialFont.getFontData();
			for (int i = 0; i < fontData.length; i++) {
				fontData[i].setHeight(BUTTON_FONT_SIZE);
			}
			Font newFont = new Font(getShell().getDisplay(), fontData);
			buttonDismiss.setFont(newFont);
		}
		buttonDismiss.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				for (ITaskListNotification notification : notifications) {
					notification.setNotified(true);
				}
				close();
			}
		});

		RowData buttonDismissRowData = new RowData(30, 15);
		buttonDismiss.setLayoutData(buttonDismissRowData);
		// toolkit.paintBordersFor(parent);
		form.pack();
		return parent;
	}

	/**
	 * Initialize the shell's bounds.
	 */
	public void initializeBounds() {
		getShell().setBounds(restoreBounds());
	}

	private Rectangle restoreBounds() {
		bounds = form.getBounds();
		Rectangle maxBounds = null;
		if (getShell() != null && !getShell().isDisposed())
			maxBounds = getShell().getDisplay().getClientArea();
		else {
			// fallback
			Display display = Display.getCurrent();
			if (display == null)
				display = Display.getDefault();
			if (display != null && !display.isDisposed())
				maxBounds = display.getBounds();
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
			bounds.x = Math.max(bounds.x, maxBounds.x);
			bounds.y = Math.max(bounds.y, maxBounds.y);

			if (bounds.width > -1 && bounds.height > -1) {
				bounds.x = maxBounds.width - bounds.width;
				bounds.y = maxBounds.height - bounds.height;
			}
		}
		return bounds;
	}

}
