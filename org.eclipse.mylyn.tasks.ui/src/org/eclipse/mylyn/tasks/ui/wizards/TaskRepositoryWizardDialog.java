/*******************************************************************************
 * Copyright (c) 2004, 2008 Helen Bershadskaya and others. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Helen Bershadskaya - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.wizards.Messages;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Wizard dialog for displaying repository settings page. Necessary so we can add a validate button in the button bar.
 * 
 * @author Helen Bershadskaya
 * @since 3.1
 */
public class TaskRepositoryWizardDialog extends WizardDialog {

	private static final String VALIDATE_BUTTON_KEY = "validate"; //$NON-NLS-1$

	private Button validateServerButton;

	private static final int VALIDATE_BUTTON_ID = 2000;

	/**
	 * @see WizardDialog#WizardDialog(Shell, IWizard)
	 * @since 3.1
	 */
	public TaskRepositoryWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
	}

	/**
	 * Overridden so we can add a validate button to the wizard button bar, if a repository settings page requires it.
	 * Validate button is added left justified at button bar bottom (next to help image).
	 * 
	 * @since 3.1
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

		validateServerButton = createButton(composite, VALIDATE_BUTTON_ID,
				Messages.AbstractRepositorySettingsPage_Validate_Settings, false);
		validateServerButton.setImage(CommonImages.getImage(TasksUiImages.REPOSITORY_VALIDATE));
		validateServerButton.setVisible(false);
		setButtonLayoutData(validateServerButton);
		Label filler = new Label(composite, SWT.NONE);
		filler.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		((GridLayout) composite.getLayout()).numColumns++;

		super.createButtonsForButtonBar(composite);

		return composite;
	}

	@Override
	public void updateButtons() {
		if (getCurrentPage() instanceof AbstractRepositorySettingsPage
				&& ((AbstractRepositorySettingsPage) getCurrentPage()).needsValidation()) {

			if (!validateServerButton.isVisible()) {
				validateServerButton.setVisible(true);
			}
		} else {
			if (validateServerButton != null && validateServerButton.isVisible()) {
				validateServerButton.setVisible(false);
			}
		}
		super.updateButtons();
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
		if (buttonId == VALIDATE_BUTTON_ID) {
			if (getCurrentPage() instanceof AbstractRepositorySettingsPage) {
				((AbstractRepositorySettingsPage) getCurrentPage()).validateSettings();
			}
		} else {
			super.buttonPressed(buttonId);
		}
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

	/**
	 * Modeled after super.saveAndSetEnabledState(), but that one is private, so create our own
	 */
	private HashMap<String, Boolean> saveAndSetEnabledStateMylyn() {
		HashMap<String, Boolean> savedEnabledState = null;
		if (getShell() != null) {
			savedEnabledState = new HashMap<String, Boolean>();
			if (validateServerButton != null && getShell().getDisplay().getFocusControl() == validateServerButton
					&& validateServerButton.getShell() == getShell()) {
				savedEnabledState.put(VALIDATE_BUTTON_KEY, validateServerButton.getEnabled());
				validateServerButton.setEnabled(false);
			}
		}
		return savedEnabledState;
	}

	/**
	 * Modeled after super.restoreEnabledState() and super.restoreUIState() -- couldn't override those since they are
	 * private, so create our own. Currently only single button to work with, so don't create two separate methods
	 */
	private void restoreEnabledStateMylyn(HashMap<String, Boolean> savedEnabledState) {
		if (savedEnabledState != null) {
			Boolean savedValidateEnabledState = savedEnabledState.get(VALIDATE_BUTTON_KEY);
			if (validateServerButton != null && savedValidateEnabledState != null) {
				validateServerButton.setEnabled(savedValidateEnabledState);
			}
		}
	}
}
