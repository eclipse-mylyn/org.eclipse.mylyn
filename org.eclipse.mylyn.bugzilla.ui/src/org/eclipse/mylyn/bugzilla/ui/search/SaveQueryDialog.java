/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.ui.search;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for naming a saved query.
 */
public class SaveQueryDialog extends Dialog
{
	private Text queryName;
	
	/**
	 * The Ok button.
	 */
	private Button okButton;

	/**
	 * The title of the dialog.
	 */
	private String title;
	
	public SaveQueryDialog(Shell parentShell, String dialogTitle) {
		super(parentShell);
		this.title = dialogTitle;
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(title);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and cancel buttons
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		okButton.setEnabled(false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// create composite
		Composite composite = (Composite)super.createDialogArea(parent);
		
		createMainDialogArea(composite);
		return composite;
	}
	
	protected void createMainDialogArea(Composite parent) {
		queryName = new Text(parent, SWT.SINGLE | SWT.BORDER);
		queryName.setLayoutData(new GridData(GridData.BEGINNING | GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		queryName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (queryName.getText().compareTo("") != 0 && queryName.getText() != null)
					okButton.setEnabled(true);
				else
					okButton.setEnabled(false);
				queryNameText = queryName.getText();
			}
		});
	}

	String queryNameText;

	public String getText() {
		return queryNameText;
	}
}
