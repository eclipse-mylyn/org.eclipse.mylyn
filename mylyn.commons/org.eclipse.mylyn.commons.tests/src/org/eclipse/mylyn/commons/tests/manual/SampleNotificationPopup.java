/*******************************************************************************
 * Copyright (c) 2000, 2024 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Benjamin Pasero - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.manual;

import org.eclipse.mylyn.commons.ui.dialogs.AbstractNotificationPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * @author Benjamin Pasero
 * @author Mik Kersten
 */
@SuppressWarnings("nls")
public class SampleNotificationPopup extends AbstractNotificationPopup {

	public SampleNotificationPopup(Display display) {
		super(display);
	}

	@Override
	protected void createTitleArea(Composite parent) {
		((GridData) parent.getLayoutData()).heightHint = 24;

		Label titleCircleLabel = new Label(parent, SWT.NONE);
		titleCircleLabel.setText("RSSOwl - Incoming News");
		titleCircleLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		titleCircleLabel.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

		Label closeButton = new Label(parent, SWT.NONE);
		closeButton.setText("Close");
		closeButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		closeButton.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				close();
			}
		});
	}

	@Override
	protected void createContentArea(Composite parent) {
		for (int i = 0; i < 5; i++) {
			Label l = new Label(parent, SWT.None);
			l.setText("News: " + i);
			l.setBackground(parent.getBackground());
		}
	}

	@Override
	protected String getPopupShellTitle() {
		return "Sample Notification";
	}
}