/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
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

package org.eclipse.mylyn.internal.bugzilla.rest.ui;

import java.text.MessageFormat;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestCore;
import org.eclipse.mylyn.internal.bugzilla.rest.core.IBugzillaRestConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.google.common.base.Strings;

public class BugzillaRestRepositorySettingsPage extends AbstractRepositorySettingsPage {
	private static final String LABEL_VERSION_NUMBER = "5.0"; //$NON-NLS-1$

	private static final String DESCRIPTION = MessageFormat
			.format(Messages.BugzillaRestRepositorySettingsPage_SupportsVersionMessage, LABEL_VERSION_NUMBER);

	private Button useApiKey;

	private Text apiKey;

	private String oldApiKeyValue;

	private Hyperlink apikeyPreferenceLink;

	private Label apiKeyLabel;

	private final String userprefs = "/userprefs.cgi?tab=apikey"; //$NON-NLS-1$

	public BugzillaRestRepositorySettingsPage(TaskRepository taskRepository, AbstractRepositoryConnector connector,
			AbstractRepositoryConnectorUi connectorUi) {
		super(Messages.BugzillaRestRepositorySettingsPage_RestRepositorySetting, DESCRIPTION, taskRepository, connector,
				connectorUi);
		setNeedsAnonymousLogin(true);
		setNeedsEncoding(false);
		setNeedsAdvanced(true);
		setNeedsValidateOnFinish(true);
	}

	@Override
	public String getConnectorKind() {
		return BugzillaRestCore.CONNECTOR_KIND;
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		FormToolkit toolkit = new FormToolkit(TasksUiPlugin.getDefault().getFormColors(parent.getDisplay()));
		Composite apiKeyContainer = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(2).applyTo(apiKeyContainer);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(apiKeyContainer);

		Label useApiKeyLabel = new Label(apiKeyContainer, SWT.NONE);
		useApiKeyLabel.setText(Messages.BugzillaRestRepositorySettingsPage_use_api_key);
		useApiKey = new Button(apiKeyContainer, SWT.CHECK | SWT.LEFT);
		useApiKey.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selectedValue = useApiKey.getSelection();
				apiKey.setEnabled(selectedValue);
				if (!selectedValue) {
					String apiKeyText = apiKey.getText();
					if (!Strings.isNullOrEmpty(apiKeyText)) {
						oldApiKeyValue = apiKeyText;
					}
					apiKey.setText(""); //$NON-NLS-1$
				} else {
					if (!Strings.isNullOrEmpty(oldApiKeyValue)) {
						apiKey.setText(oldApiKeyValue);
					}
				}
			}
		});

		apiKeyLabel = new Label(apiKeyContainer, SWT.NONE);
		apiKeyLabel.setText(Messages.BugzillaRestRepositorySettingsPage_api_key);
		apiKey = new Text(apiKeyContainer, SWT.BORDER);
		GridDataFactory.fillDefaults()
				.grab(true, false)
				.align(SWT.FILL, SWT.CENTER)
				.hint(300, SWT.DEFAULT)
				.applyTo(apiKey);
		apiKey.setEnabled(false);
		apikeyPreferenceLink = toolkit.createHyperlink(apiKeyContainer, "", SWT.NONE); //$NON-NLS-1$
		GridDataFactory.fillDefaults()
				.grab(true, false)
				.span(2, 1)
				.align(SWT.FILL, SWT.BEGINNING)
				.hint(300, SWT.DEFAULT)
				.applyTo(apikeyPreferenceLink);
		apikeyPreferenceLink.setBackground(apiKeyContainer.getBackground());
		apikeyPreferenceLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				BrowserUtil.openUrl(apikeyPreferenceLink.getText(), IWorkbenchBrowserSupport.AS_EXTERNAL);
			}
		});
		if (repository != null) {
			boolean useApiKeyValue = Boolean
					.parseBoolean(repository.getProperty(IBugzillaRestConstants.REPOSITORY_USE_API_KEY));
			useApiKey.setSelection(useApiKeyValue);
			String apiKeyValue = repository.getProperty(IBugzillaRestConstants.REPOSITORY_API_KEY);
			apiKey.setText(Strings.nullToEmpty(apiKeyValue));
			apiKey.setEnabled(useApiKeyValue);
		}

		updateURLInformation();
		serverUrlCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateURLInformation();
			}
		});
		serverUrlCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateURLInformation();
			}
		});
	}

	protected void updateURLInformation() {
		String url = serverUrlCombo.getText() + userprefs;
		apiKey.setToolTipText(
				NLS.bind(Messages.BugzillaRestRepositorySettingsPage_Please_create_or_copy_the_API_Key_from, url));
		apiKeyLabel.setToolTipText(
				NLS.bind(Messages.BugzillaRestRepositorySettingsPage_Please_create_or_copy_the_API_Key_from, url));
		apikeyPreferenceLink.setText(url);
		apikeyPreferenceLink
				.setToolTipText(NLS.bind(Messages.BugzillaRestRepositorySettingsPage_View_your_apikey_settings, url));
		apikeyPreferenceLink.setEnabled(TasksUiInternal.isValidUrl(url));
	}

	@Override
	public void applyTo(TaskRepository repository) {
		repository.setProperty(IBugzillaRestConstants.REPOSITORY_USE_API_KEY,
				Boolean.toString(useApiKey.getSelection()));
		repository.setProperty(IBugzillaRestConstants.REPOSITORY_API_KEY, apiKey.getText());
		super.applyTo(repository);
	}
}
