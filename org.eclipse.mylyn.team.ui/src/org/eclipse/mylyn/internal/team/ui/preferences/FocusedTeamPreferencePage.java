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

package org.eclipse.mylyn.internal.team.ui.preferences;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylyn.internal.team.ui.FocusedTeamUiPlugin;
import org.eclipse.mylyn.internal.team.ui.templates.TemplateHandlerContentProposalProvider;
import org.eclipse.mylyn.team.ui.AbstractContextChangeSetManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;

/**
 * @author Mik Kersten
 */
public class FocusedTeamPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public final static String PAGE_ID = "org.eclipse.mylyn.team.ui.ui.preferences"; //$NON-NLS-1$

	private Button manageChangeSets;

	private Text commitTemplate = null;

	public FocusedTeamPreferencePage() {
		super();
		setPreferenceStore(FocusedTeamUiPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		createChangeSetGroup(container);
		createCommitGroup(container);
		applyDialogFont(container);
		return container;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean performOk() {
		getPreferenceStore().setValue(FocusedTeamUiPlugin.COMMIT_TEMPLATE, commitTemplate.getText());
		getPreferenceStore().setValue(FocusedTeamUiPlugin.CHANGE_SET_MANAGE, manageChangeSets.getSelection());

		if (manageChangeSets.getSelection()) {
			for (AbstractContextChangeSetManager changeSetManager : FocusedTeamUiPlugin.getDefault()
					.getContextChangeSetManagers()) {
				changeSetManager.enable();
			}
		} else {
			for (AbstractContextChangeSetManager changeSetManager : FocusedTeamUiPlugin.getDefault()
					.getContextChangeSetManagers()) {
				changeSetManager.disable();
			}
		}
		return true;
	}

	@Override
	public boolean performCancel() {
		return true;
	}

	@Override
	public void performDefaults() {
		super.performDefaults();
		commitTemplate.setText(getPreferenceStore().getDefaultString(FocusedTeamUiPlugin.COMMIT_TEMPLATE));
		manageChangeSets.setSelection(getPreferenceStore().getDefaultBoolean(FocusedTeamUiPlugin.CHANGE_SET_MANAGE));
	}

	private void createChangeSetGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText(Messages.FocusedTeamPreferencePage_Change_Set_Management);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		manageChangeSets = new Button(group, SWT.CHECK);
		manageChangeSets.setText(Messages.FocusedTeamPreferencePage_Automatically_create_and_manage_with_task_context);
		manageChangeSets.setSelection(getPreferenceStore().getBoolean(FocusedTeamUiPlugin.CHANGE_SET_MANAGE));
	}

	private void createCommitGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText(Messages.FocusedTeamPreferencePage_Commit_Comment_Template);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

//		Label completedLabel = createLabel(group, "Template: ");
//		completedLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		String completedTemplate = getPreferenceStore().getString(FocusedTeamUiPlugin.COMMIT_TEMPLATE);
		commitTemplate = addTemplateField(group, completedTemplate, new TemplateHandlerContentProposalProvider());
	}

	private Text addTemplateField(final Composite parent, final String text, IContentProposalProvider provider) {
		IControlContentAdapter adapter = new TextContentAdapter();
		Text control = new Text(parent, SWT.BORDER | SWT.MULTI);
		control.setText(text);

		new ContentAssistCommandAdapter(control, adapter, provider, null, new char[] { '$' }, true);

		GridData gd = new GridData();
		gd.heightHint = 60;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = GridData.CENTER;
		gd.grabExcessVerticalSpace = false;
		control.setLayoutData(gd);

		return control;
	}
}
