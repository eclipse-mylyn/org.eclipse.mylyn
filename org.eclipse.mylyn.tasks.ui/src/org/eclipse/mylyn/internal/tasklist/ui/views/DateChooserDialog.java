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

package org.eclipse.mylar.internal.tasklist.ui.views;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Ken Sueda
 */
public class DateChooserDialog extends Dialog {

	private DatePicker picker = null;

	private Calendar reminderDate = null;

	public DateChooserDialog(Shell parentShell) {
		super(parentShell);
	}

	public DateChooserDialog(Shell parentShell, Date reminderDate) {
		super(parentShell);
		this.reminderDate = Calendar.getInstance();
		this.reminderDate.setTime(reminderDate);
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout gl = new GridLayout(1, false);
		composite.setLayout(gl);
		GridData data = new GridData(GridData.FILL_BOTH);

		picker = new DatePicker(composite, SWT.NONE);
		// picker.setDate()

		data.heightHint = 90; // HACK
		picker.setLayoutData(data);
		return composite;
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			reminderDate = picker.getDate();
		} else {
		}
		super.buttonPressed(buttonId);
	}

	public Calendar getReminderDate() {
		return reminderDate;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Select date");
	}
}
