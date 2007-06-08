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

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Mik Kersten
 */
public class ExistingTaskWizardPage extends WizardPage {

	private static final String TITLE = "Enter Task ID";

	private static final String DESCRIPTION = "Provide the identifier for the task, issue, or bug report.\n" 
		+ "The format is specific to the repository.";

	private Text taskIdText;

	public ExistingTaskWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		setImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		GridData gd = new GridData();
		gd.widthHint = 200;

		Label label = new Label(container, SWT.NULL);
		label.setText("Enter Key/ID: ");
		taskIdText = new Text(container, SWT.BORDER);
		taskIdText.setLayoutData(gd);
		taskIdText.setFocus();
		taskIdText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				getWizard().getContainer().updateButtons();
				// try {
				// numDaysToReport = Integer.parseInt(taskId.getText());
				// setErrorMessage(null);
				// } catch (Exception ex) {
				// setErrorMessage("Must be integer");
				// numDaysToReport = 0;
				// }
			}
		});

		setControl(container);
	}

	@Override
	public boolean isPageComplete() {
		return getTaskId() != null && !getTaskId().trim().equals("");
	}

	public String getTaskId() {
		return taskIdText.getText();
	}
}
