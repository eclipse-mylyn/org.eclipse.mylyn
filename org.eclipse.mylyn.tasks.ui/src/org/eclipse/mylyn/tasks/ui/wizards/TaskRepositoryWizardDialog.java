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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
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
		// if any pages require validation, create validate button
		for (IWizardPage page : getWizard().getPages()) {
			if (page instanceof AbstractRepositorySettingsPage
					&& ((AbstractRepositorySettingsPage) page).needsValidation()) {
				validateServerButton = createButton(composite, VALIDATE_BUTTON_ID,
						Messages.AbstractRepositorySettingsPage_Validate_Settings, false);
				validateServerButton.setImage(CommonImages.getImage(TasksUiImages.REPOSITORY_SYNCHRONIZE_SMALL));
				setButtonLayoutData(validateServerButton);
				Label filler = new Label(composite, SWT.NONE);
				filler.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
				((GridLayout) composite.getLayout()).numColumns++;

				// found a page that requires validate button, so get out of loop here
				break;
			}
		}

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
		if (buttonId == VALIDATE_BUTTON_ID) {
			for (IWizardPage page : getWizard().getPages()) {
				if (page instanceof AbstractRepositorySettingsPage) {
					((AbstractRepositorySettingsPage) page).validateSettings();
				}
			}
		} else {
			super.buttonPressed(buttonId);
		}
	}
	
}
