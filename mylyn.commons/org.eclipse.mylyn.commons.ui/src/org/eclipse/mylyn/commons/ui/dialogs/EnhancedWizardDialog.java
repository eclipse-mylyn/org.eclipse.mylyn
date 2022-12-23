/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui.dialogs;

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
 * @since 3.7
 */
public abstract class EnhancedWizardDialog extends WizardDialog {

	private boolean isInFinish;

	private int runningOperations;

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
	protected void finishPressed() {
		// ignore recursive calls
		if (isInFinish) {
			return;
		}
		try {
			isInFinish = true;
			super.finishPressed();
		} finally {
			isInFinish = false;
		}
	}

	@Override
	public void updateButtons() {
		// all navigation buttons should be disabled while an operation is running  
		if (runningOperations > 0) {
			return;
		}

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
			runningOperations++;
			savedEnabledState = saveAndSetEnabledStateMylyn();
			super.run(fork, cancelable, runnable);
		} finally {
			runningOperations--;
			if (savedEnabledState != null) {
				restoreEnabledStateMylyn(savedEnabledState);
			}
		}
	}

}
