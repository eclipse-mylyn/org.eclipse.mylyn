/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.ide.team.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylar.ide.MylarIdePlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Mik Kersten
 */
public class MylarTeamPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Button changeSetsManage;
	
	private Text commitPrefixCompleted = null;

	private Text commitPrefixProgress = null;

	public MylarTeamPreferencePage() {
		super();
		setPreferenceStore(MylarIdePlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		createChangeSetGroup(container);
		createCommitGroup(container);
		return container;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean performOk() {
		getPreferenceStore().setValue(MylarIdePlugin.COMMIT_PREFIX_COMPLETED, commitPrefixCompleted.getText());
		getPreferenceStore().setValue(MylarIdePlugin.COMMIT_PREFIX_PROGRESS, commitPrefixProgress.getText());
		getPreferenceStore().setValue(MylarIdePlugin.CHANGE_SET_MANAGE, changeSetsManage.getSelection());
		
		if (changeSetsManage.getSelection()) {
			MylarIdePlugin.getDefault().getChangeSetManager().enable();
		} else {
			MylarIdePlugin.getDefault().getChangeSetManager().disable();
		}
		return true;
	}

	@Override
	public boolean performCancel() {
		return true;
	}

	public void performDefaults() {
		super.performDefaults();
		commitPrefixCompleted.setText(getPreferenceStore().getDefaultString(MylarIdePlugin.COMMIT_PREFIX_COMPLETED));
		commitPrefixProgress.setText(getPreferenceStore().getDefaultString(MylarIdePlugin.COMMIT_PREFIX_PROGRESS));
		changeSetsManage.setSelection(getPreferenceStore().getDefaultBoolean(MylarIdePlugin.CHANGE_SET_MANAGE));
	}

	private Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText(text);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.BEGINNING;
		label.setLayoutData(data);
		return label;
	}

	private void createChangeSetGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Active Change Set Management");
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		changeSetsManage = new Button(group, SWT.CHECK);
		changeSetsManage.setText("Manage change sets with task context");
		changeSetsManage.setSelection(getPreferenceStore().getBoolean(MylarIdePlugin.CHANGE_SET_MANAGE));
	}
	
	private void createCommitGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Task Context Commit");
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label completedLabel = createLabel(group, "Completed task prefix: ");
		completedLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		String completedPrefix = getPreferenceStore().getString(MylarIdePlugin.COMMIT_PREFIX_COMPLETED);
		commitPrefixCompleted = new Text(group, SWT.BORDER);
		commitPrefixCompleted.setText(completedPrefix);
		commitPrefixCompleted.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label progressLabel = createLabel(group, "In progress task prefix: ");
		progressLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		String progressPrefix = getPreferenceStore().getString(MylarIdePlugin.COMMIT_PREFIX_PROGRESS);
		commitPrefixProgress = new Text(group, SWT.BORDER);
		commitPrefixProgress.setText(progressPrefix);
		commitPrefixProgress.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
}
