/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.bugzilla.rest.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestTaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class BugzillaRestQueryTypeWizardPage extends WizardPage {

	private final AbstractRepositoryQueryPage2 customPage;

	private final AbstractRepositoryQueryPage2 searchPage;

	private Button buttonCustom;

	private Button buttonForm;

	private Composite composite;

	public BugzillaRestQueryTypeWizardPage(TaskRepository repository, AbstractRepositoryConnector connector) {
		super(Messages.BugzillaRestQueryTypeWizardPage_ChooseQueryType);
		setTitle(Messages.BugzillaRestQueryTypeWizardPage_ChooseQueryType);
		setDescription(Messages.BugzillaRestQueryTypeWizardPage_SelectAvailableQueryTypes);
		setImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
		BugzillaRestConnector connectorREST = (BugzillaRestConnector) connector;
		TaskData taskDataSimpleURL = new TaskData(new BugzillaRestTaskAttributeMapper(repository, connectorREST),
				repository.getConnectorKind(), Messages.BugzillaRestQueryTypeWizardPage_Query,
				Messages.BugzillaRestQueryTypeWizardPage_Query);
		TaskData taskDataSearch = new TaskData(new BugzillaRestTaskAttributeMapper(repository, connectorREST),
				repository.getConnectorKind(), Messages.BugzillaRestQueryTypeWizardPage_Query,
				Messages.BugzillaRestQueryTypeWizardPage_Query);
		customPage = BugzillaRestUiUtil.createBugzillaRestSearchPage(true, false, taskDataSimpleURL, connectorREST,
				repository, null);
		searchPage = BugzillaRestUiUtil.createBugzillaRestSearchPage(false, false, taskDataSearch, connectorREST,
				repository, null);
	}

	@Override
	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessVerticalSpace = false;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));

		buttonForm = new Button(composite, SWT.RADIO);
		buttonForm.setText(Messages.BugzillaRestQueryTypeWizardPage_CreateQueryUsingForm);
		buttonForm.setSelection(true);

		buttonCustom = new Button(composite, SWT.RADIO);
		buttonCustom.setText(Messages.BugzillaRestQueryTypeWizardPage_CreateQueryFromExistingURL);

		setPageComplete(true);
		setControl(composite);
		Dialog.applyDialogFont(composite);
	}

	@Override
	public IWizardPage getNextPage() {
		if (buttonForm.getSelection()) {
			searchPage.setWizard(this.getWizard());
			return searchPage;
		}
		customPage.setWizard(this.getWizard());
		return customPage;
	}
}
