/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.bugzilla.ui.search.BugzillaSearchPage;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Rob Elves
 */
public class BugzillaQueryTypeWizardPage extends WizardPage {

	private static final String BUTTON_LABEL_QUERY = "Create query from existing URL";

	private static final String BUTTON_LABEL_FORM = "Create query using form";

	private static final String TITLE = "Choose query type";

	private static final String DESCRIPTION = "Select from the available query types.";

	private Button buttonCustom;

	private Button buttonForm;

	private Composite composite;
	
	private BugzillaCustomQueryWizardPage customPage;
	
	private BugzillaSearchPage searchPage;

	public BugzillaQueryTypeWizardPage(TaskRepository repository) {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		setImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
		customPage = new BugzillaCustomQueryWizardPage(repository);		
		searchPage = new BugzillaSearchPage(repository);
		searchPage.setRestoreQueryOptions(false);
	}

	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessVerticalSpace = false;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));

		buttonForm = new Button(composite, SWT.RADIO);
		buttonForm.setText(BUTTON_LABEL_FORM);
		buttonForm.setSelection(true);

		buttonCustom = new Button(composite, SWT.RADIO);
		buttonCustom.setText(BUTTON_LABEL_QUERY);

		setPageComplete(true);
		setControl(composite);
	}

	@Override
	public IWizardPage getNextPage() {
		if(buttonForm.getSelection()) {
			searchPage.setWizard(this.getWizard());
			return searchPage;
		}
		customPage.setWizard(this.getWizard());
		return customPage;
	}

}
