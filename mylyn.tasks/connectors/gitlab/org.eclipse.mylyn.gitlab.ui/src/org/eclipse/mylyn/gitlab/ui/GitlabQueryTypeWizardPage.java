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
 *******************************************************************************/

package org.eclipse.mylyn.gitlab.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.provisional.tasks.ui.wizards.AbstractQueryPageSchema;
import org.eclipse.mylyn.internal.provisional.tasks.ui.wizards.QueryPageDetails;
import org.eclipse.mylyn.internal.provisional.tasks.ui.wizards.RepositoryQuerySchemaPage;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage2;
import org.eclipse.osgi.util.NLS;
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
		super("Super");
		setTitle("Title");
		setDescription("Desc");
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
		buttonForm.setText("Messages.BugzillaRestQueryTypeWizardPage_CreateQueryUsingForm");
		buttonForm.setSelection(true);

		buttonCustom = new Button(composite, SWT.RADIO);
		buttonCustom.setText("Messages.BugzillaRestQueryTypeWizardPage_CreateQueryFromExistingURL");

		setPageComplete(true);
		setControl(composite);
		Dialog.applyDialogFont(composite);
	}


}
