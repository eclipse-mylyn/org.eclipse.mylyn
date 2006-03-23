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

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 */
public class ReminderCellEditor extends DialogCellEditor {

	private Date reminderDate;

	public ReminderCellEditor(Composite parent) {
		super(parent, SWT.NONE);
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		ReminderDialog dialog = new ReminderDialog(cellEditorWindow.getShell());
		dialog.open();
		reminderDate = dialog.getDate();
		return reminderDate;
	}

	public Date getReminderDate() {
		return reminderDate;
	}
}
