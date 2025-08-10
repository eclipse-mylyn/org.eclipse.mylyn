/*******************************************************************************
 * Copyright (c) 2004, 2011 Helen Bershadskaya and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Helen Bershadskaya - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui.dialogs;

import java.util.HashMap;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.commons.ui.Messages;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * Wizard dialog for displaying repository settings page. Necessary so we can add a validate button in the button bar.
 *
 * @author Helen Bershadskaya
 * @since 3.7
 */
public class ValidatableWizardDialog extends EnhancedWizardDialog {

	private static final String VALIDATE_BUTTON_KEY = "validate"; //$NON-NLS-1$

	private Button validateServerButton;

	private static final int VALIDATE_BUTTON_ID = 2000;

	/**
	 * @see WizardDialog#WizardDialog(Shell, IWizard)
	 */
	public ValidatableWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
		setHelpAvailable(false);
	}

	@Override
	protected void createExtraButtons(Composite composite) {
		validateServerButton = createButton(composite, VALIDATE_BUTTON_ID,
				Messages.ValidatableWizardDialog_Validate_Button_Label, false);
		validateServerButton.setImage(CommonImages.getImage(CommonImages.VALIDATE));
		validateServerButton.setVisible(false);
		setButtonLayoutData(validateServerButton);
	}

	@Override
	public void updateExtraButtons() {
		IValidatable validatable = getValidatablePage();
		if (validatable != null && validatable.needsValidation()) {
			if (!validateServerButton.isVisible()) {
				validateServerButton.setVisible(true);
			}
			validateServerButton.setEnabled(validatable.canValidate());
		} else if (validateServerButton != null && validateServerButton.isVisible()) {
			validateServerButton.setVisible(false);
		}
	}

	private IValidatable getValidatablePage() {
		IValidatable validatable = null;
		IWizardPage currentPage = getCurrentPage();
		if (currentPage instanceof IValidatable) {
			validatable = (IValidatable) currentPage;
		} else if (currentPage instanceof IAdaptable) {
			validatable = ((IAdaptable) currentPage).getAdapter(IValidatable.class);
		}
		return validatable;
	}

	@Override
	protected boolean handleExtraButtonPressed(int buttonId) {
		if (buttonId == VALIDATE_BUTTON_ID) {
			IValidatable validatable = getValidatablePage();
			if (validatable != null) {
				validatable.validate();
				return true;
			}
		}
		return false;
	}

	/**
	 * Modeled after WizardDialog.saveAndSetEnabledState(), but that one is private, so create our own
	 */
	@Override
	protected HashMap<String, Boolean> saveAndSetEnabledStateMylyn() {
		HashMap<String, Boolean> savedEnabledState = null;
		if (getShell() != null) {
			savedEnabledState = new HashMap<>();
			if (validateServerButton != null && validateServerButton.getShell() == getShell()) {
				savedEnabledState.put(VALIDATE_BUTTON_KEY, validateServerButton.getEnabled());
				validateServerButton.setEnabled(false);
			}
		}
		return savedEnabledState;
	}

	/**
	 * Modeled after WizardDialog.restoreEnabledState() and WizardDialog.restoreUIState() -- couldn't override those since they are private,
	 * so create our own. Currently only single button to work with, so don't create two separate methods
	 */
	@Override
	protected void restoreEnabledStateMylyn(HashMap<String, Boolean> savedEnabledState) {
		if (savedEnabledState != null) {
			Boolean savedValidateEnabledState = savedEnabledState.get(VALIDATE_BUTTON_KEY);
			if (validateServerButton != null && savedValidateEnabledState != null) {
				validateServerButton.setEnabled(savedValidateEnabledState);
			}
		}
	}

}
