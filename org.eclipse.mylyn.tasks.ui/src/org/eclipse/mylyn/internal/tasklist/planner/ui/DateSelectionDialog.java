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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.mylar.internal.tasklist.ui.views.DatePickerPanel;
import org.eclipse.mylar.internal.tasklist.ui.views.DatePickerPanel.DateSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 * @author Rob Elves
 */
public class DateSelectionDialog extends Dialog {

	private Date reminderDate = null;
	private String title = "Date Selection";
	private Calendar initialCalendar = GregorianCalendar.getInstance();

	public DateSelectionDialog(Shell parentShell, String title) {
		this(parentShell, GregorianCalendar.getInstance(), title);
	}

	public DateSelectionDialog(Shell parentShell, Calendar initialDate, String title) {
		super(parentShell);
		if(title != null) {
			this.title = title;
		}
		if(initialDate != null) {
			this.initialCalendar.setTime(initialDate.getTime());
		}
		reminderDate = initialCalendar.getTime();
	}

	protected Control createDialogArea(Composite parent) {
		getShell().setText(title);
		DatePickerPanel datePanel = new DatePickerPanel(parent, SWT.NULL, initialCalendar);

		datePanel.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if (!event.getSelection().isEmpty()) {
					DateSelection dateSelection = (DateSelection) event.getSelection();
					reminderDate = dateSelection.getDate().getTime();
				}
			}
		});

		return datePanel;
	}

	public Date getDate() {
		return reminderDate;
	}
}
