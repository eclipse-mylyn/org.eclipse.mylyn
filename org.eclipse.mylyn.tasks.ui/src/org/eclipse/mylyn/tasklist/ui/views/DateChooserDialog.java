/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.ui.views;

import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Ken Sueda
 */
public class DateChooserDialog extends Dialog {

//	private DatePicker picker = null;
	private Date reminderDate = null;
	
	public DateChooserDialog(Shell parentShell) {
		super(parentShell);
	}

//	protected Control createDialogArea(Composite parent) {
//		Composite composite = (Composite) super.createDialogArea(parent);
//		GridLayout gl = new GridLayout(1, false);
//		composite.setLayout(gl);
//		GridData data = new GridData(GridData.FILL_BOTH);
//		
//		picker = new DatePicker(composite, SWT.NONE);
//		data.heightHint = 90; // HACK
//		picker.setLayoutData(data);
//		return composite;
//	}
//	
//	protected void buttonPressed(int buttonId) {
//		if (buttonId == IDialogConstants.OK_ID) {
//			reminderDate = picker.getDate();
//		} else {
//		}
//		super.buttonPressed(buttonId);
//	}
	
	public Date getReminderDate() {
		return reminderDate;
	}
}
