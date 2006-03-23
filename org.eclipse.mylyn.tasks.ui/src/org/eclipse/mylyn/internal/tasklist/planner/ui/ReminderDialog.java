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

package org.eclipse.mylar.internal.tasklist.planner.ui;

import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.mylar.internal.tasklist.ui.views.DatePicker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 * @author Rob Elves (pulled out of ReminderCellEditor)
 */
public class ReminderDialog extends Dialog {

	private Date reminderDate = null;

	public ReminderDialog(Shell parentShell) {
		super(parentShell);
	}

	public ReminderDialog(IShellProvider parentShell) {
		super(parentShell);
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		final DatePicker datePicker = new DatePicker(composite, SWT.NULL, "<reminder>");
		datePicker.addPickerSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				if(datePicker.getDate() != null) {
					reminderDate = datePicker.getDate().getTime();
				}
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// ignore
			}
		});
		return composite;
	}

	public Date getDate() {
		return reminderDate;
	}
}
