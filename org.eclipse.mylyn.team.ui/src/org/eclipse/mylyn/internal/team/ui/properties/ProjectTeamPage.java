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

package org.eclipse.mylyn.internal.team.ui.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.mylyn.internal.team.ui.FocusedTeamUiPlugin;
import org.eclipse.mylyn.internal.team.ui.preferences.FocusedTeamPreferencePage;
import org.eclipse.mylyn.internal.team.ui.templates.TemplateHandlerContentProposalProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 * @see Adapted from org.eclipse.ui.internal.ide.dialogs.ProjectReferencePage
 */
public class ProjectTeamPage extends PropertyPage {

	private IProject project;

	private boolean modified = false;

	private Button useProjectSettings;

	private Text commitTemplateText;

	private Composite propertiesComposite;

	private Hyperlink configurationHyperlink;

	private Label label;

	public ProjectTeamPage() {
		noDefaultAndApplyButton();
	}

	@Override
	protected Control createContents(Composite parent) {
		Font font = parent.getFont();

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setFont(font);

		createDescription(composite);
		createPropertiesControl(composite);

		initialize();

		return composite;
	}

	private void createDescription(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		useProjectSettings = new Button(composite, SWT.CHECK);
		useProjectSettings.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				modified = true;
				setPropertiesEnabled(useProjectSettings.getSelection());
			}
		});
		useProjectSettings.setText("Enable project specific settings");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(useProjectSettings);

		configurationHyperlink = new Hyperlink(composite, SWT.NONE);
		configurationHyperlink.setUnderlined(true);
		configurationHyperlink.setText("Configure workspace");
		configurationHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				PreferenceDialog dlg = PreferencesUtil.createPreferenceDialogOn(getShell(),
						FocusedTeamPreferencePage.PAGE_ID, new String[] { FocusedTeamPreferencePage.PAGE_ID }, null);
				dlg.open();
			}
		});

		Label horizontalLine = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		horizontalLine.setFont(composite.getFont());
	}

	private void setPropertiesEnabled(boolean enabled) {
		propertiesComposite.setEnabled(enabled);
		for (Control child : propertiesComposite.getChildren()) {
			child.setEnabled(enabled);
		}
		commitTemplateText.setEnabled(enabled);

		configurationHyperlink.setEnabled(!enabled);
		if (!enabled) {
			configurationHyperlink.setForeground(JFaceColors.getHyperlinkText(getShell().getDisplay()));
		} else {
			configurationHyperlink.setForeground(getShell().getDisplay().getSystemColor(
					SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		}
	}

	private void createPropertiesControl(Composite parent) {
		propertiesComposite = new Composite(parent, SWT.NONE);
		propertiesComposite.setFont(parent.getFont());
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		propertiesComposite.setLayout(layout);
		propertiesComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		label = new Label(propertiesComposite, SWT.NONE);
		label.setText("Commit Comment Template");

		String completedTemplate = ""; //getPreferenceStore().getString(FocusedTeamUiPlugin.COMMIT_TEMPLATE);
		commitTemplateText = addTemplateField(propertiesComposite, completedTemplate,
				new TemplateHandlerContentProposalProvider());
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

	private void initialize() {
		project = (IProject) getElement().getAdapter(IResource.class);

		TeamPropertiesLinkProvider provider = new TeamPropertiesLinkProvider();
		String template = provider.getCommitCommentTemplate(project);
		if (template == null) {
			useProjectSettings.setSelection(false);
			setPropertiesEnabled(false);
			commitTemplateText.setText(FocusedTeamUiPlugin.getDefault().getPreferenceStore().getString(
					FocusedTeamUiPlugin.COMMIT_TEMPLATE));
		} else {
			useProjectSettings.setSelection(true);
			setPropertiesEnabled(true);
			commitTemplateText.setText(template);
		}
	}

	@Override
	public boolean performOk() {
		if (!modified) {
			return true;
		}

		TeamPropertiesLinkProvider provider = new TeamPropertiesLinkProvider();
		if (useProjectSettings.getSelection()) {
			provider.setCommitCommentTemplate(project, commitTemplateText.getText());
		} else {
			provider.setCommitCommentTemplate(project, null);
		}

		return true;
	}
}
