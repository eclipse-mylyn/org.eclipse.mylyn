/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui.wizards;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class NewAttachmentWizardDialog extends WizardDialog {

	private static final String ATTACHMENT_WIZARD_SETTINGS_SECTION = "PatchWizard"; //$NON-NLS-1$

	public NewAttachmentWizardDialog(Shell parent, IWizard wizard) {
		super(parent, wizard);

		setShellStyle(getShellStyle() | SWT.RESIZE);
		setMinimumPageSize(600, 300);
		setPageSize(600, 300);
	}

	protected IDialogSettings getDialogBoundsSettings() {
		IDialogSettings settings = MylarPlugin.getDefault().getDialogSettings();
		IDialogSettings section = settings.getSection(ATTACHMENT_WIZARD_SETTINGS_SECTION);
		if (section == null) {
			section = settings.addNewSection(ATTACHMENT_WIZARD_SETTINGS_SECTION);
		}
		return section;
	}
}
