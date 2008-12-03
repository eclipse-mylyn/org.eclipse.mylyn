/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonFonts;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.dialogs.IWorkingSetPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Shawn Minto
 */
public class TaskContextWorkingSetPage extends WizardPage implements IWorkingSetPage {

	private Text workingSetNameText;

	private IWorkingSet workingSet;

	public static final String WORKING_SET_NAME = Messages.TaskContextWorkingSetPage_TASK_CONTEXT_FOR_SEARCH;

	public TaskContextWorkingSetPage() {
		super(
				"org.eclipse.mylyn.monitor.ui.workingSetPage", Messages.TaskContextWorkingSetPage_Mylyn_Task_Context_Working_Set, //$NON-NLS-1$
				AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylyn.context.ui", //$NON-NLS-1$
						"icons/wizban/banner-prefs.gif")); //$NON-NLS-1$
		setDescription(Messages.TaskContextWorkingSetPage_CREATE_THE_MYLYN_CONTEXT_WORKING_SET);
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		setControl(composite);

		Label label = new Label(composite, SWT.WRAP);
		label.setText(Messages.TaskContextWorkingSetPage_Name);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER);
		label.setLayoutData(gd);

		workingSetNameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		workingSetNameText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		workingSetNameText.setFocus();
		workingSetNameText.setEditable(false);
		workingSetNameText.setText(WORKING_SET_NAME);

		label = new Label(composite, SWT.WRAP);
		label.setText(""); //$NON-NLS-1$
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		label.setLayoutData(gd);

		label = new Label(composite, SWT.WRAP);
		label.setText(Messages.TaskContextWorkingSetPage_NOTE_THIS_WORKING_SET_SHOULD_ONLY_BE_USED_FOR_SEARCHS);
		label.setFont(CommonFonts.BOLD);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		label.setLayoutData(gd);

		label = new Label(composite, SWT.WRAP);
		label.setText(""); //$NON-NLS-1$
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		label.setLayoutData(gd);

		// label = new Label(composite, SWT.WRAP);
		// label.setText("PLEASE DO NOT ACTIVATE THIS WORKING SET AT
		// ANYTIME.\nTHIS WORKING SET IS ONLY USEFUL FOR SEARCHING.");
		// label.setFont(UiUtil.BOLD);
		// gd= new GridData(GridData.GRAB_HORIZONTAL |
		// GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		// label.setLayoutData(gd);

		Dialog.applyDialogFont(composite);
	}

	public IWorkingSet getSelection() {
		return workingSet;
	}

	public void setSelection(IWorkingSet workingSet) {
		// don't need to do anything here
	}

	public void finish() {
		String workingSetName = workingSetNameText.getText();
		ArrayList<IAdaptable> elements = new ArrayList<IAdaptable>(1);
		ContextWorkingSetManager.getElementsFromContext(elements);
		if (workingSet == null) {
			IWorkingSetManager workingSetManager = ContextUiPlugin.getDefault().getWorkbench().getWorkingSetManager();
			if ((workingSet = workingSetManager.getWorkingSet(workingSetName)) == null) {
				workingSet = workingSetManager.createWorkingSet(workingSetName,
						elements.toArray(new IAdaptable[elements.size()]));
			}
		}
	}

	@Override
	public boolean isPageComplete() {
		String workingSetName = workingSetNameText.getText();
		IWorkingSetManager workingSetManager = ContextUiPlugin.getDefault().getWorkbench().getWorkingSetManager();
		if (workingSetManager.getWorkingSet(workingSetName) != null) {
			setErrorMessage(Messages.TaskContextWorkingSetPage_Cannot_create_another_Active_Taskscape_Working_Set);
			return false;
		} else {
			return true;
		}
	}

}
