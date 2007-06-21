/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *     Eike tepper - commit comment template preferences
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui.preferences;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.*;
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
import org.eclipse.ui.fieldassist.*;

/**
 * @author Mik Kersten
 */
public class FocusedTeamPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
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
			for (AbstractContextChangeSetManager changeSetManager : FocusedTeamUiPlugin.getDefault().getContextChangeSetManagers()) {
				changeSetManager.enable();
			}
//			MylarTeamPlugin.getDefault().getChangeSetManager().enable();
		} else {
			for (AbstractContextChangeSetManager changeSetManager : FocusedTeamUiPlugin.getDefault().getContextChangeSetManagers()) {
				changeSetManager.disable();
			}
//			MylarTeamPlugin.getDefault().getChangeSetManager().disable();
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
		commitTemplate.setText(getPreferenceStore()
				.getDefaultString(FocusedTeamUiPlugin.COMMIT_TEMPLATE));
		manageChangeSets.setSelection(getPreferenceStore().getDefaultBoolean(FocusedTeamUiPlugin.CHANGE_SET_MANAGE));
	}

//	private Label createLabel(Composite parent, String text) {
//		Label label = new Label(parent, SWT.LEFT);
//		label.setText(text);
//		GridData data = new GridData();
//		data.horizontalSpan = 2;
//		data.horizontalAlignment = GridData.BEGINNING;
//		label.setLayoutData(data);
//		return label;
//	}

	private void createChangeSetGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Change Set Management");
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		manageChangeSets = new Button(group, SWT.CHECK);
		manageChangeSets.setText("Automatically create and manage with task context");
		manageChangeSets.setSelection(getPreferenceStore().getBoolean(FocusedTeamUiPlugin.CHANGE_SET_MANAGE));
	}

	private void createCommitGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Commit Comment Template");
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

//		Label completedLabel = createLabel(group, "Template: ");
//		completedLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		String completedTemplate = getPreferenceStore().getString(FocusedTeamUiPlugin.COMMIT_TEMPLATE);
		commitTemplate = addTemplateField(group, completedTemplate, new TemplateHandlerContentProposalProvider());
	}

	@SuppressWarnings("deprecation")
	private Text addTemplateField(final Composite parent, final String text, IContentProposalProvider provider) {
		IControlContentAdapter adapter = new TextContentAdapter();
		IControlCreator controlCreator = new IControlCreator() {
			public Control createControl(Composite parent, int style) {
				Text control = new Text(parent, style);
				control.setText(text);
				return control;
			}
		};

		ContentAssistField field = new ContentAssistField(parent, SWT.BORDER | SWT.MULTI, controlCreator, adapter, provider, null,
				new char[] { '$' });

		GridData gd = new GridData();
		gd.heightHint = 60;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = GridData.CENTER;
		gd.grabExcessVerticalSpace = false;
		field.getLayoutControl().setLayoutData(gd);

		return (Text) field.getControl();
	}
}
