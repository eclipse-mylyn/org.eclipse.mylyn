/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.mylyn.internal.provisional.commons.ui.DatePickerPanel.DateSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 * @author Rob Elves
 */
public class DateSelectionDialog extends Dialog {

	private Date selectedDate = null;

	private String title = "Date Selection";

	private final Calendar initialCalendar = Calendar.getInstance();

	private boolean includeTime = true;

	private int hourOfDay = 0;

	public DateSelectionDialog(Shell parentShell, Calendar initialDate, String title, boolean includeTime, int hourOfDay) {
		super(parentShell);
		this.includeTime = includeTime;
		this.hourOfDay = hourOfDay;
		if (title != null) {
			this.title = title;
		}
		if (initialDate != null) {
			this.initialCalendar.setTime(initialDate.getTime());
		}
		selectedDate = initialCalendar.getTime();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(title);
		final DatePickerPanel datePanel = new DatePickerPanel(parent, SWT.NULL, initialCalendar, includeTime, hourOfDay);
		datePanel.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if (!event.getSelection().isEmpty()) {
					DateSelection dateSelection = (DateSelection) event.getSelection();
					selectedDate = dateSelection.getDate().getTime();
				}
			}
		});

		datePanel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		return datePanel;
	}

	@Override
	public boolean close() {
		return super.close();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CLIENT_ID + 1, "Clear", false);
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
