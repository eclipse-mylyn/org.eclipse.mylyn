/*******************************************************************************
 * Copyright (c) 2010, 2012 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class BugzillaResponseDetailDialog extends Dialog {

	private final String titleText;

	private final String messageText;

	public BugzillaResponseDetailDialog(Shell parentShell, String titleText, String messageText) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.titleText = titleText;
		this.messageText = messageText;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(titleText);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		composite.setLayoutData(gd);

		Text text = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		gd.heightHint = 120;
		gd.widthHint = 300;
		text.setLayoutData(gd);
		text.setEditable(false);
		text.setText(messageText);
		parent.pack();
		applyDialogFont(composite);
		return composite;
	}
}
