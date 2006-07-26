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

package org.eclipse.mylar.internal.tasks.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Jeff Pound
 */
public class FindRelatedReportsPage extends WizardPage implements IWizardPage {

	private static final String PAGE_DESCRIPTION = "Enter the stack trace to search for, you can trim the stack trace to make your search more general";

	private static final String PAGE_TITLE = "Find Related Reports";

	static final String PAGE_NAME = "FindRelatedReportsPage";

	private Text stackTraceBox;

	private Text searchTermsText;

	private DuplicateDetectionData duplicateData;

	public FindRelatedReportsPage(DuplicateDetectionData duplicateData) {
		super(PAGE_NAME);
		setTitle(PAGE_TITLE);
		setDescription(PAGE_DESCRIPTION);
		
		// Description doesn't show up without an image present TODO: proper image.
		setImageDescriptor(TasksUiPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.bugzilla.ui",
				"icons/wizban/bug-wizard.gif"));
		this.duplicateData = duplicateData;
		if (this.duplicateData == null) {
			this.duplicateData = new DuplicateDetectionData();
		}
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		setControl(composite);

		Label searchLabel = new Label(composite, SWT.LEFT);
		searchLabel.setText("Search Terms ");
		searchTermsText = new Text(composite, SWT.SINGLE);
		searchTermsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchTermsText.setText("");
		searchTermsText.setEnabled(false);

		Label stackLabel = new Label(composite, SWT.LEFT);
		stackLabel.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));
		stackLabel.setText("Stack trace");
		stackTraceBox = new Text(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		stackTraceBox.setText(duplicateData.getStackTrace());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		// prevent the box from oversizing the wizard dialog
		if (!"".equals(duplicateData.getStackTrace())) {
			gd.heightHint = 200;
			gd.widthHint = 600;
		}
		stackTraceBox.setLayoutData(gd);
		stackTraceBox.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				if ("".equals(stackTraceBox.getText().trim())) {
					setPageComplete(false);
				} else {
					setPageComplete(true);
				}
			}
		});
	}

	public DuplicateDetectionData getDuplicateData() {
		if (stackTraceBox != null) {
			// in case stack trace is user modified
			duplicateData.setStackTrace(stackTraceBox.getText());
		}
		return duplicateData;
	}

	public IWizardPage getNextPage() {
		DisplayRelatedReportsPage nextPage = (DisplayRelatedReportsPage) super.getNextPage();
		nextPage.setRelatedTasks(((AbstractDuplicateDetectingReportWizard) getWizard())
				.searchForDuplicates(getDuplicateData()));

		return super.getNextPage();
	}

	public boolean canFlipToNextPage() {
		return !"".equals(stackTraceBox.getText());
	}
}
