/*******************************************************************************
 * Copyright (c) 2011 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Wizard dialog for displaying additional button in the button bar. Based on ValidatableWizardDialog from Helen
 * Bershadskaya
 * 
 * @author Helen Bershadskaya
 * @author Frank Becker
 */

public abstract class EnhancedWizardDialog extends WizardDialog {

	public EnhancedWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
	}

	abstract protected void createExtraButtons(Composite composite);

	abstract protected void updateExtraButtons();

	abstract protected boolean handleExtraButtonPressed(int buttonId);

	abstract protected HashMap<String, Boolean> saveAndSetEnabledStateMylyn();

	abstract protected void restoreEnabledStateMylyn(HashMap<String, Boolean> savedEnabledState);

	/**
	 * Overridden so we can add a validate button to the wizard button bar, if a repository settings page requires it.
	 * Validate button is added left justified at button bar bottom (next to help image).
	 */
	@Override
	protected Control createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 0; // create 
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);

		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// create help control if needed
		if (isHelpAvailable()) {
			createHelpControl(composite);
		}

		createExtraButtons(composite);
		Label filler = new Label(composite, SWT.NONE);
		filler.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		((GridLayout) composite.getLayout()).numColumns++;

		super.createButtonsForButtonBar(composite);

		return composite;
	}

	/**
	 * Overridden so we can react to the validate button being pressed. This could have been done with a straight
	 * selection listener in the creation method above, but this is more consistent with how the other buttons work in
	 * the wizard dialog.
	 * 
	 * @since 3.1
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (!handleExtraButtonPressed(buttonId)) {
			super.buttonPressed(buttonId);
		}
	}

	@Override
	public void updateButtons() {
		updateExtraButtons();
		super.updateButtons();
	}

	/**
	 * Overridden to be able to set proper state for our validate button
	 */
	@Override
	public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InvocationTargetException,
			InterruptedException {

		HashMap<String, Boolean> savedEnabledState = null;
		try {
			savedEnabledState = saveAndSetEnabledStateMylyn();
			super.run(fork, cancelable, runnable);
		} finally {
			if (savedEnabledState != null) {
				restoreEnabledStateMylyn(savedEnabledState);
			}
		}
	}

}
