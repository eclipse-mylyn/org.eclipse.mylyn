/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.planner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.mylyn.tasks.ui.DatePicker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 * @author Rob Elves
 */
public class ReminderCellEditor extends DialogCellEditor {

	private Date reminderDate;

	private DateSelectionDialog dialog;

	private String formatString = "dd-MMM-yyyy";

	private SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);

	public ReminderCellEditor(Composite parent) {
		super(parent, SWT.NONE);
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		Calendar initialCalendar = null;
		String value = (String) super.getValue();

		if (value != null) {
			try {
				Date tempDate = format.parse(value);
				if (tempDate != null) {
					initialCalendar = GregorianCalendar.getInstance();
					initialCalendar.setTime(tempDate);
				}
			} catch (ParseException e) {
				// ignore
			}
		}
		Calendar newCalendar = GregorianCalendar.getInstance();
		if (initialCalendar != null) {
			newCalendar.setTime(initialCalendar.getTime());
		}

		dialog = new DateSelectionDialog(cellEditorWindow.getShell(), newCalendar, DatePicker.TITLE_DIALOG, true);
		int dialogResponse = dialog.open();

		if (dialogResponse == DateSelectionDialog.CANCEL) {
			if (initialCalendar != null) {
				reminderDate = initialCalendar.getTime();
			} else {
				reminderDate = null;
			}
		} else {
			reminderDate = dialog.getDate();
		}

		String result = null;
		if (reminderDate != null) {
			result = format.format(reminderDate);
		}
		return result;
	}

	public Date getReminderDate() {
		return reminderDate;
	}

	@Override
	protected void doSetFocus() {
		reminderDate = null;
		super.doSetFocus();
	}

}
