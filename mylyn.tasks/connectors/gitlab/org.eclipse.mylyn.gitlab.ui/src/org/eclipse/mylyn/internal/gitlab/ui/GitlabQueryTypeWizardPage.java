/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.gitlab.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class GitlabQueryTypeWizardPage extends WizardPage {

	private Button buttonCustom;

	private Button buttonForm;

	private Composite composite;

	public GitlabQueryTypeWizardPage(TaskRepository repository, AbstractRepositoryConnector connector) {
		super("Super"); //$NON-NLS-1$
		setTitle("Title"); //$NON-NLS-1$
		setDescription("Desc"); //$NON-NLS-1$
		setImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	@Override
	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessVerticalSpace = false;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));

		buttonForm = new Button(composite, SWT.RADIO);
		buttonForm.setText("Messages.BugzillaRestQueryTypeWizardPage_CreateQueryUsingForm"); //$NON-NLS-1$
		buttonForm.setSelection(true);

		buttonCustom = new Button(composite, SWT.RADIO);
		buttonCustom.setText("Messages.BugzillaRestQueryTypeWizardPage_CreateQueryFromExistingURL"); //$NON-NLS-1$

		setPageComplete(true);
		setControl(composite);
		Dialog.applyDialogFont(composite);
	}

}
