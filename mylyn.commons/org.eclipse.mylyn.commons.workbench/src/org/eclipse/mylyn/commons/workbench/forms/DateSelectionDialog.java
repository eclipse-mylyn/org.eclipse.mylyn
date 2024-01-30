/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench.forms;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.mylyn.commons.workbench.forms.DatePickerPanel.DateSelection;
import org.eclipse.mylyn.internal.commons.workbench.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 * @author Rob Elves
 * @since 3.7
 */
public class DateSelectionDialog extends Dialog {

	private Date selectedDate = null;

	private String title = Messages.DateSelectionDialog_Date_Selection;

	private final Calendar initialCalendar = Calendar.getInstance();

	private boolean includeTime = true;

	private int hourOfDay = 0;

	public DateSelectionDialog(Shell parentShell, Calendar initialDate, String title, boolean includeTime,
			int hourOfDay) {
		super(parentShell);
		this.includeTime = includeTime;
		this.hourOfDay = hourOfDay;
		if (title != null) {
			this.title = title;
		}
		if (initialDate != null) {
			initialCalendar.setTime(initialDate.getTime());
		}
		selectedDate = initialCalendar.getTime();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(title);
		final DatePickerPanel datePanel = new DatePickerPanel(parent, SWT.NULL, initialCalendar, includeTime,
				hourOfDay);
		datePanel.addSelectionChangedListener(event -> {
			if (!event.getSelection().isEmpty()) {
				DateSelection dateSelection = (DateSelection) event.getSelection();
				selectedDate = dateSelection.getDate().getTime();
				if (dateSelection.isDefaultSelection()) {
					okPressed();
				}
			}
		});

		datePanel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		applyDialogFont(datePanel);
		return datePanel;
	}

	@Override
	public boolean close() {
		return super.close();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
		if (buttonId == IDialogConstants.CLIENT_ID + 1) {
			selectedDate = null;
			okPressed();
		}
	}

	public Date getDate() {
		return selectedDate;
	}
}
