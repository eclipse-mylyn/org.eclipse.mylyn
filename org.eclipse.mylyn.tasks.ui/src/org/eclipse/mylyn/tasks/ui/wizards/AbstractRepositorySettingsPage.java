/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *     Helen Bershadskaya - improvements for bug 242445
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTemplateManager;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.wizards.Messages;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * Extend to provide custom repository settings. This page is typically invoked by the user requesting properties via
 * the Task Repositories view.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 * @author Frank Becker
 * @author David Green
 * @author Helen Bershadskaya
 * @since 2.0
 */
public abstract class AbstractRepositorySettingsPage extends AbstractTaskRepositoryPage implements ITaskRepositoryPage {

	protected static final String PREFS_PAGE_ID_NET_PROXY = "org.eclipse.ui.net.NetPreferences"; //$NON-NLS-1$

	protected static final String LABEL_REPOSITORY_LABEL = Messages.AbstractRepositorySettingsPage_Label_;

	protected static final String LABEL_SERVER = Messages.AbstractRepositorySettingsPage_Server_;

	protected static final String LABEL_USER = Messages.AbstractRepositorySettingsPage_User_ID_;

	protected static final String LABEL_PASSWORD = Messages.AbstractRepositorySettingsPage_Password_;

	protected static final String URL_PREFIX_HTTPS = "https://"; //$NON-NLS-1$

	protected static final String URL_PREFIX_HTTP = "http://"; //$NON-NLS-1$

	protected static final String INVALID_REPOSITORY_URL = Messages.AbstractRepositorySettingsPage_Repository_url_is_invalid;

	protected static final String INVALID_LOGIN = Messages.AbstractRepositorySettingsPage_Unable_to_authenticate_with_repository;

	protected AbstractRepositoryConnector connector;

	protected StringFieldEditor repositoryLabelEditor;

	protected Combo serverUrlCombo;

	private String serverVersion = TaskRepository.NO_VERSION_SPECIFIED;

	protected StringFieldEditor repositoryUserNameEditor;

	protected StringFieldEditor repositoryPasswordEditor;

	protected StringFieldEditor httpAuthUserNameEditor;

	protected StringFieldEditor httpAuthPasswordEditor;

	protected StringFieldEditor proxyHostnameEditor;

	protected StringFieldEditor proxyPortEditor;

	protected StringFieldEditor proxyUserNameEditor;

	protected StringFieldEditor proxyPasswordEditor;

	// FIXME shadows declaration in super
	protected TaskRepository repository;

	private Combo otherEncodingCombo;

	private Button defaultEncoding;

	// private Combo timeZonesCombo;

	protected Button anonymousButton;

	private String oldUsername;

	private String oldPassword;

	private String oldHttpAuthUserId;

	private String oldHttpAuthPassword;

	private boolean needsAnonymousLogin;

	private boolean needsTimeZone;

	private boolean needsEncoding;

	private boolean needsHttpAuth;

	private boolean needsValidation;

	private boolean needsAdvanced;

	protected Composite compositeContainer;

	private Composite advancedComp;

	private Composite httpAuthComp;

	private Composite proxyAuthComp;

	private Set<String> repositoryUrls;

	private String originalUrl;

	private Button otherEncoding;

	private Button httpAuthButton;

	private boolean needsProxy;

	private Button systemProxyButton;

	private String oldProxyUsername = ""; //$NON-NLS-1$

	private String oldProxyPassword = ""; //$NON-NLS-1$

	// private Button proxyAuthButton;

	private String oldProxyHostname = ""; //$NON-NLS-1$

	private String oldProxyPort = ""; //$NON-NLS-1$

	private Button proxyAuthButton;

	private Hyperlink createAccountHyperlink;

	private Hyperlink manageAccountHyperlink;

	private Button savePasswordButton;

	private Button saveHttpPasswordButton;

	private Button saveProxyPasswordButton;

	private Button disconnectedButton;

	/**
	 * @since 3.0
	 */
	public AbstractRepositorySettingsPage(String title, String description, TaskRepository taskRepository) {
		super(title, description, taskRepository);
		repository = taskRepository;
		this.connector = TasksUi.getRepositoryManager().getRepositoryConnector(getConnectorKind());
		setNeedsAnonymousLogin(false);
		setNeedsEncoding(true);
		setNeedsTimeZone(true);
		setNeedsProxy(true);
		setNeedsValidation(true);
		setNeedsAdvanced(true);
	}

	/**
	 * @since 3.0
	 */
	@Override
	public abstract String getConnectorKind();

	@Override
	public void dispose() {
		super.dispose();
	}

//	/**
//	 * @since 2.0
//	 */
//	@Override
//	protected Control createContents(Composite parent) {
//		compositeContainer = new Composite(parent, SWT.NONE);
//		GridLayout layout = new GridLayout(3, false);
//		compositeContainer.setLayout(layout);
//
//		createSettingControls(parent);
//	}

	@Override
	public void createControl(Composite parent) {
		toolkit = new FormToolkit(TasksUiPlugin.getDefault().getFormColors(parent.getDisplay()));

		Composite compositeContainer = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		compositeContainer.setLayout(layout);

		createSettingControls(compositeContainer);

		setControl(compositeContainer);
	}

	/**
	 * @since 2.0
	 */
	@Override
	protected void createSettingControls(Composite parent) {
		compositeContainer = parent;

		initializeOldValues();

		Label serverLabel = new Label(compositeContainer, SWT.NONE);
		serverLabel.setText(LABEL_SERVER);
		serverUrlCombo = new Combo(compositeContainer, SWT.DROP_DOWN);
		serverUrlCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (getWizard() != null) {
					getWizard().getContainer().updateButtons();
				}
			}
		});
		serverUrlCombo.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				updateHyperlinks();
			}
		});
		serverUrlCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (getWizard() != null) {
					getWizard().getContainer().updateButtons();
				}
			}
		});

		GridDataFactory.fillDefaults().hint(300, SWT.DEFAULT).grab(true, false).span(2, SWT.DEFAULT).applyTo(
				serverUrlCombo);

		repositoryLabelEditor = new StringFieldEditor("", LABEL_REPOSITORY_LABEL, StringFieldEditor.UNLIMITED, //$NON-NLS-1$
				compositeContainer) {

			@Override
			protected boolean doCheckState() {
				return true;
				// return isValidUrl(getStringValue());
			}

			@Override
			protected void valueChanged() {
				super.valueChanged();
				if (getWizard() != null) {
					getWizard().getContainer().updateButtons();
				}
			}

			@Override
			public int getNumberOfControls() {
				return 2;
			}
		};
		disconnectedButton = new Button(compositeContainer, SWT.CHECK);
		disconnectedButton.setText(Messages.AbstractRepositorySettingsPage_Disconnected);
		disconnectedButton.setSelection(repository != null ? repository.isOffline() : false);

		repositoryUserNameEditor = new StringFieldEditor("", LABEL_USER, StringFieldEditor.UNLIMITED, //$NON-NLS-1$
				compositeContainer) {

			@Override
			protected boolean doCheckState() {
				return true;
			}

			@Override
			protected void valueChanged() {
				super.valueChanged();
				isPageComplete();
				if (getWizard() != null) {
					getWizard().getContainer().updateButtons();
				}
			}

			@Override
			public int getNumberOfControls() {
				// if will have anonymous checkbox on same line, make this control only span 2 columns
				return needsAnonymousLogin() ? 2 : 3;
			}
		};
		if (needsAnonymousLogin()) {
			// need to increase column number here, because above string editor will use them if declared beforehand
			//((GridLayout) (compositeContainer.getLayout())).numColumns++;
			anonymousButton = new Button(compositeContainer, SWT.CHECK);

			anonymousButton.setText(Messages.AbstractRepositorySettingsPage_Anonymous_Access);
			anonymousButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setAnonymous(anonymousButton.getSelection());
					isPageComplete();
				}
			});
		}

		repositoryPasswordEditor = new RepositoryStringFieldEditor("", LABEL_PASSWORD, StringFieldEditor.UNLIMITED, //$NON-NLS-1$
				compositeContainer) {

			@Override
			protected boolean doCheckState() {
				return true;
			}

			@Override
			protected void valueChanged() {
				super.valueChanged();
				isPageComplete();
				if (getWizard() != null) {
					getWizard().getContainer().updateButtons();
				}
			}

			@Override
			public int getNumberOfControls() {
				return 2;
			}
		};

		// need to increase column number here, because above string editor will use them if declared beforehand
		//((GridLayout) (compositeContainer.getLayout())).numColumns++;
		savePasswordButton = new Button(compositeContainer, SWT.CHECK);
		savePasswordButton.setText(Messages.AbstractRepositorySettingsPage_Save_Password);

		if (repository != null) {
			try {
				String repositoryLabel = repository.getProperty(IRepositoryConstants.PROPERTY_LABEL);
				if (repositoryLabel != null && repositoryLabel.length() > 0) {
					// repositoryLabelCombo.add(repositoryLabel);
					// repositoryLabelCombo.select(0);
					repositoryLabelEditor.setStringValue(repositoryLabel);
				}
				serverUrlCombo.setText(repository.getRepositoryUrl());
				AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
				if (credentials != null) {
					repositoryUserNameEditor.setStringValue(credentials.getUserName());
					repositoryPasswordEditor.setStringValue(credentials.getPassword());
				} else {
					repositoryUserNameEditor.setStringValue(""); //$NON-NLS-1$
					repositoryPasswordEditor.setStringValue(""); //$NON-NLS-1$
				}
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not set field value", t)); //$NON-NLS-1$
			}
		}

		if (needsAnonymousLogin()) {
			if (repository != null) {
				setAnonymous(repository.getCredentials(AuthenticationType.REPOSITORY) == null);
			} else {
				setAnonymous(true);
			}
		}

		if (repository != null) {
			savePasswordButton.setSelection(repository.getSavePassword(AuthenticationType.REPOSITORY));
		} else {
			savePasswordButton.setSelection(false);
		}

		// TODO: put this back if we can't get the info from all connectors
		// if (needsTimeZone()) {
		// Label timeZoneLabel = new Label(container, SWT.NONE);
		// timeZoneLabel.setText("Repository time zone: ");
		// timeZonesCombo = new Combo(container, SWT.READ_ONLY);
		// String[] timeZoneIds = TimeZone.getAvailableIDs();
		// Arrays.sort(timeZoneIds);
		// for (String zone : timeZoneIds) {
		// timeZonesCombo.add(zone);
		// }
		// boolean setZone = false;
		// if (repository != null) {
		// if (timeZonesCombo.indexOf(repository.getTimeZoneId()) > -1) {
		// timeZonesCombo.select(timeZonesCombo.indexOf(repository.getTimeZoneId()));
		// setZone = true;
		// }
		// }
		// if (!setZone) {
		// timeZonesCombo.select(timeZonesCombo.indexOf(TimeZone.getDefault().getID()));
		// }
		// }

		if (needsAdvanced() || needsEncoding()) {
			createAdvancedSection();
		}

		if (needsHttpAuth()) {
			createHttpAuthSection();
		}

		if (needsProxy()) {
			createProxySection();
		}

		createContributionControls(parent);

		Composite managementComposite = new Composite(compositeContainer, SWT.NULL);
		GridLayout managementLayout = new GridLayout(4, false);
		managementLayout.marginHeight = 0;
		managementLayout.marginWidth = 0;
		managementLayout.horizontalSpacing = 10;
		managementComposite.setLayout(managementLayout);
		managementComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1));

		createAccountHyperlink = toolkit.createHyperlink(managementComposite,
				Messages.AbstractRepositorySettingsPage_Create_new_account, SWT.NONE);
		createAccountHyperlink.setBackground(managementComposite.getBackground());
		createAccountHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
//				TaskRepository repository = getRepository();
				TaskRepository repository = createTaskRepository();
//				if (repository == null && getServerUrl() != null && getServerUrl().length() > 0) {
//					repository = createTaskRepository();
//				}
				if (repository != null) {
					String accountCreationUrl = TasksUiPlugin.getConnectorUi(connector.getConnectorKind())
							.getAccountCreationUrl(repository);
					if (accountCreationUrl != null) {
						TasksUiUtil.openUrl(accountCreationUrl);
					}
				}
			}
		});

		manageAccountHyperlink = toolkit.createHyperlink(managementComposite,
				Messages.AbstractRepositorySettingsPage_Change_account_settings, SWT.NONE);
		manageAccountHyperlink.setBackground(managementComposite.getBackground());
		manageAccountHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				TaskRepository repository = getRepository();
				if (repository == null && getRepositoryUrl() != null && getRepositoryUrl().length() > 0) {
					repository = createTaskRepository();
				}
				if (repository != null) {
					String accountManagementUrl = TasksUiPlugin.getConnectorUi(connector.getConnectorKind())
							.getAccountManagementUrl(repository);
					if (accountManagementUrl != null) {
						TasksUiUtil.openUrl(accountManagementUrl);
					}
				}
			}
		});

		// bug 131656: must set echo char after setting value on Mac
		((RepositoryStringFieldEditor) repositoryPasswordEditor).getTextControl().setEchoChar('*');

		if (needsAnonymousLogin()) {
			// do this after username and password widgets have been intialized
			if (repository != null) {
				setAnonymous(isAnonymousAccess());
			}
		}

		updateHyperlinks();

		GridLayout layout = new GridLayout(3, false);
		compositeContainer.setLayout(layout);

		Dialog.applyDialogFont(compositeContainer);
	}

	private void createAdvancedSection() {
		ExpandableComposite section = createSection(compositeContainer,
				Messages.AbstractRepositorySettingsPage_Additional_Settings);

		advancedComp = toolkit.createComposite(section, SWT.NONE);
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 2;
		gridLayout2.verticalSpacing = 5;
		gridLayout2.marginWidth = 0;
		advancedComp.setLayout(gridLayout2);
		advancedComp.setBackground(compositeContainer.getBackground());
		section.setClient(advancedComp);

		createAdditionalControls(advancedComp);

		if (needsEncoding()) {
			Label encodingLabel = new Label(advancedComp, SWT.HORIZONTAL);
			encodingLabel.setText(Messages.AbstractRepositorySettingsPage_Character_encoding);
			GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.TOP).applyTo(encodingLabel);

			Composite encodingContainer = new Composite(advancedComp, SWT.NONE);
			GridLayout gridLayout = new GridLayout(2, false);
			gridLayout.marginWidth = 0;
			gridLayout.marginHeight = 0;
			encodingContainer.setLayout(gridLayout);

			defaultEncoding = new Button(encodingContainer, SWT.RADIO);
			defaultEncoding.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
			defaultEncoding.setText(Messages.AbstractRepositorySettingsPage_Default__
					+ TaskRepository.DEFAULT_CHARACTER_ENCODING + ")"); //$NON-NLS-1$
			defaultEncoding.setSelection(true);

			otherEncoding = new Button(encodingContainer, SWT.RADIO);
			otherEncoding.setText(Messages.AbstractRepositorySettingsPage_Other);
			otherEncodingCombo = new Combo(encodingContainer, SWT.READ_ONLY);
			try {
				for (String encoding : Charset.availableCharsets().keySet()) {
					if (!encoding.equals(TaskRepository.DEFAULT_CHARACTER_ENCODING)) {
						otherEncodingCombo.add(encoding);
					}
				}
			} catch (LinkageError e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						Messages.AbstractRepositorySettingsPage_Problems_encountered_determining_available_charsets, e));
				// bug 237972: 3rd party encodings can cause availableCharsets() to fail 
				otherEncoding.setEnabled(false);
				otherEncodingCombo.setEnabled(false);
			}

			setDefaultEncoding();

			otherEncoding.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (otherEncoding.getSelection()) {
						defaultEncoding.setSelection(false);
						otherEncodingCombo.setEnabled(true);
					} else {
						defaultEncoding.setSelection(true);
						otherEncodingCombo.setEnabled(false);
					}
				}
			});

			if (repository != null) {
				try {
					String repositoryEncoding = repository.getCharacterEncoding();
					if (repositoryEncoding != null) {// &&
						// !repositoryEncoding.equals(defaultEncoding))
						// {
						if (otherEncodingCombo.getItemCount() > 0
								&& otherEncodingCombo.indexOf(repositoryEncoding) > -1) {
							otherEncodingCombo.setEnabled(true);
							otherEncoding.setSelection(true);
							defaultEncoding.setSelection(false);
							otherEncodingCombo.select(otherEncodingCombo.indexOf(repositoryEncoding));
						} else {
							setDefaultEncoding();
						}
					}
				} catch (Throwable t) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not set field value", t)); //$NON-NLS-1$
				}
			}
		}
	}

	private void createHttpAuthSection() {
		ExpandableComposite section = createSection(compositeContainer,
				Messages.AbstractRepositorySettingsPage_Http_Authentication);

		httpAuthComp = toolkit.createComposite(section, SWT.NONE);
		httpAuthComp.setBackground(compositeContainer.getBackground());
		section.setClient(httpAuthComp);

		httpAuthButton = new Button(httpAuthComp, SWT.CHECK);
		GridDataFactory.fillDefaults().indent(0, 5).align(SWT.LEFT, SWT.TOP).span(3, SWT.DEFAULT).applyTo(
				httpAuthButton);

		httpAuthButton.setText(Messages.AbstractRepositorySettingsPage_Enable_http_authentication);

		httpAuthButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setHttpAuth(httpAuthButton.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});

		httpAuthUserNameEditor = new StringFieldEditor(
				"", Messages.AbstractRepositorySettingsPage_User_ID_, StringFieldEditor.UNLIMITED, httpAuthComp) { //$NON-NLS-1$

			@Override
			protected boolean doCheckState() {
				return true;
			}

			@Override
			protected void valueChanged() {
				super.valueChanged();
				if (getWizard() != null) {
					getWizard().getContainer().updateButtons();
				}
			}

			@Override
			public int getNumberOfControls() {
				return 3;
			}
		};

		httpAuthPasswordEditor = new RepositoryStringFieldEditor(
				"", Messages.AbstractRepositorySettingsPage_Password_, StringFieldEditor.UNLIMITED, //$NON-NLS-1$
				httpAuthComp) {
			@Override
			public int getNumberOfControls() {
				return 2;
			}
		};
		((RepositoryStringFieldEditor) httpAuthPasswordEditor).getTextControl().setEchoChar('*');

		saveHttpPasswordButton = new Button(httpAuthComp, SWT.CHECK);
		saveHttpPasswordButton.setText(Messages.AbstractRepositorySettingsPage_Save_Password);

		httpAuthUserNameEditor.setEnabled(httpAuthButton.getSelection(), httpAuthComp);
		httpAuthPasswordEditor.setEnabled(httpAuthButton.getSelection(), httpAuthComp);
		saveHttpPasswordButton.setEnabled(httpAuthButton.getSelection());

		if (repository != null) {
			saveHttpPasswordButton.setSelection(repository.getSavePassword(AuthenticationType.HTTP));
		} else {
			saveHttpPasswordButton.setSelection(false);
		}
		setHttpAuth(oldHttpAuthPassword != null || oldHttpAuthUserId != null);
		section.setExpanded(httpAuthButton.getSelection());

		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 3;
		gridLayout2.marginWidth = 0;
		httpAuthComp.setLayout(gridLayout2);
	}

	private void initializeOldValues() {
		if (repository != null) {
			originalUrl = repository.getRepositoryUrl();
			AuthenticationCredentials oldCredentials = repository.getCredentials(AuthenticationType.REPOSITORY);
			if (oldCredentials != null) {
				oldUsername = oldCredentials.getUserName();
				oldPassword = oldCredentials.getPassword();
			} else {
				oldUsername = ""; //$NON-NLS-1$
				oldPassword = ""; //$NON-NLS-1$
			}

			AuthenticationCredentials oldHttpCredentials = repository.getCredentials(AuthenticationType.HTTP);
			if (oldHttpCredentials != null) {
				oldHttpAuthUserId = oldHttpCredentials.getUserName();
				oldHttpAuthPassword = oldHttpCredentials.getPassword();
			} else {
				oldHttpAuthPassword = null;
				oldHttpAuthUserId = null;
			}

			oldProxyHostname = repository.getProperty(TaskRepository.PROXY_HOSTNAME);
			oldProxyPort = repository.getProperty(TaskRepository.PROXY_PORT);
			if (oldProxyHostname == null) {
				oldProxyHostname = ""; //$NON-NLS-1$
			}
			if (oldProxyPort == null) {
				oldProxyPort = ""; //$NON-NLS-1$
			}

			AuthenticationCredentials oldProxyCredentials = repository.getCredentials(AuthenticationType.PROXY);
			if (oldProxyCredentials != null) {
				oldProxyUsername = oldProxyCredentials.getUserName();
				oldProxyPassword = oldProxyCredentials.getPassword();
			} else {
				oldProxyUsername = null;
				oldProxyPassword = null;
			}

		} else {
			oldUsername = ""; //$NON-NLS-1$
			oldPassword = ""; //$NON-NLS-1$
			oldHttpAuthPassword = null;
			oldHttpAuthUserId = null;
		}
	}

	private void createProxySection() {
		ExpandableComposite section = createSection(compositeContainer,
				Messages.AbstractRepositorySettingsPage_Proxy_Server_Configuration);

		proxyAuthComp = toolkit.createComposite(section, SWT.NONE);
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.verticalSpacing = 0;
		gridLayout2.numColumns = 3;
		proxyAuthComp.setLayout(gridLayout2);
		proxyAuthComp.setBackground(compositeContainer.getBackground());
		section.setClient(proxyAuthComp);

		Composite systemSettingsComposite = new Composite(proxyAuthComp, SWT.NULL);
		GridLayout gridLayout3 = new GridLayout();
		gridLayout3.verticalSpacing = 0;
		gridLayout3.numColumns = 2;
		gridLayout3.marginWidth = 0;
		systemSettingsComposite.setLayout(gridLayout3);

		systemProxyButton = new Button(systemSettingsComposite, SWT.CHECK);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).span(3, SWT.DEFAULT).applyTo(systemSettingsComposite);

		systemProxyButton.setText(Messages.AbstractRepositorySettingsPage_Use_global_Network_Connections_preferences);

		systemProxyButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setUseDefaultProxy(systemProxyButton.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});

		Hyperlink changeProxySettingsLink = toolkit.createHyperlink(systemSettingsComposite,
				Messages.AbstractRepositorySettingsPage_Change_Settings, SWT.NULL);
		changeProxySettingsLink.setBackground(compositeContainer.getBackground());
		changeProxySettingsLink.addHyperlinkListener(new IHyperlinkListener() {

			public void linkActivated(HyperlinkEvent e) {
				PreferenceDialog dlg = PreferencesUtil.createPreferenceDialogOn(getShell(), PREFS_PAGE_ID_NET_PROXY,
						new String[] { PREFS_PAGE_ID_NET_PROXY }, null);
				dlg.open();
			}

			public void linkEntered(HyperlinkEvent e) {
				// ignore
			}

			public void linkExited(HyperlinkEvent e) {
				// ignore
			}
		});

		proxyHostnameEditor = new StringFieldEditor(
				"", Messages.AbstractRepositorySettingsPage_Proxy_host_address_, StringFieldEditor.UNLIMITED, //$NON-NLS-1$
				proxyAuthComp) {

			@Override
			protected boolean doCheckState() {
				return true;
			}

			@Override
			protected void valueChanged() {
				super.valueChanged();
				if (getWizard() != null) {
					getWizard().getContainer().updateButtons();
				}
			}

			@Override
			public int getNumberOfControls() {
				return 3;
			}
		};
		proxyHostnameEditor.setStringValue(oldProxyHostname);

		proxyPortEditor = new RepositoryStringFieldEditor(
				"", Messages.AbstractRepositorySettingsPage_Proxy_host_port_, StringFieldEditor.UNLIMITED, //$NON-NLS-1$
				proxyAuthComp) {

			@Override
			public int getNumberOfControls() {
				return 3;
			}
		};

		proxyPortEditor.setStringValue(oldProxyPort);

		proxyHostnameEditor.setEnabled(systemProxyButton.getSelection(), proxyAuthComp);
		proxyPortEditor.setEnabled(systemProxyButton.getSelection(), proxyAuthComp);

		// ************* PROXY AUTHENTICATION **************

		proxyAuthButton = new Button(proxyAuthComp, SWT.CHECK);
		GridDataFactory.fillDefaults().span(3, SWT.DEFAULT).applyTo(proxyAuthButton);
		proxyAuthButton.setText(Messages.AbstractRepositorySettingsPage_Enable_proxy_authentication);
		proxyAuthButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setProxyAuth(proxyAuthButton.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});

		proxyUserNameEditor = new StringFieldEditor(
				"", Messages.AbstractRepositorySettingsPage_User_ID_, StringFieldEditor.UNLIMITED, proxyAuthComp) { //$NON-NLS-1$

			@Override
			protected boolean doCheckState() {
				return true;
			}

			@Override
			protected void valueChanged() {
				super.valueChanged();
				if (getWizard() != null) {
					getWizard().getContainer().updateButtons();
				}
			}

			@Override
			public int getNumberOfControls() {
				return 3;
			}
		};

		proxyPasswordEditor = new RepositoryStringFieldEditor(
				"", Messages.AbstractRepositorySettingsPage_Password_, StringFieldEditor.UNLIMITED, //$NON-NLS-1$
				proxyAuthComp) {
			@Override
			public int getNumberOfControls() {
				return 2;
			}
		};
		((RepositoryStringFieldEditor) proxyPasswordEditor).getTextControl().setEchoChar('*');

		// proxyPasswordEditor.setEnabled(httpAuthButton.getSelection(),
		// advancedComp);
		// ((StringFieldEditor)
		// httpAuthPasswordEditor).setEnabled(httpAuthButton.getSelection(),
		// advancedComp);

		// need to increase column number here, because above string editor will use them if declared beforehand
		((GridLayout) (proxyAuthComp.getLayout())).numColumns++;
		saveProxyPasswordButton = new Button(proxyAuthComp, SWT.CHECK);
		saveProxyPasswordButton.setText(Messages.AbstractRepositorySettingsPage_Save_Password);
		saveProxyPasswordButton.setEnabled(proxyAuthButton.getSelection());

		if (repository != null) {
			saveProxyPasswordButton.setSelection(repository.getSavePassword(AuthenticationType.PROXY));
		} else {
			saveProxyPasswordButton.setSelection(false);
		}

		setProxyAuth(oldProxyUsername != null || oldProxyPassword != null);

		setUseDefaultProxy(repository != null ? repository.isDefaultProxyEnabled() : true);
		section.setExpanded(!systemProxyButton.getSelection());
	}

//	private void addContributionSection() {
//		Composite composite = toolkit.createComposite(compositeContainer);
//		GridDataFactory.fillDefaults().grab(true, false).span(3, SWT.DEFAULT).applyTo(composite);
//
//		GridLayout layout = new GridLayout(1, false);
//		layout.marginWidth = 0;
//		layout.marginTop = -5;
//		composite.setLayout(layout);
//
//		composite.setBackground(compositeContainer.getBackground());
//
//		createContributionControls(composite);
//	}

	protected void setEncoding(String encoding) {
		if (encoding.equals(TaskRepository.DEFAULT_CHARACTER_ENCODING)) {
			setDefaultEncoding();
		} else {
			if (otherEncodingCombo.indexOf(encoding) != -1) {
				defaultEncoding.setSelection(false);
				otherEncodingCombo.setEnabled(true);
				otherEncoding.setSelection(true);
				otherEncodingCombo.select(otherEncodingCombo.indexOf(encoding));
			} else {
				setDefaultEncoding();
			}
		}
	}

	private void setDefaultEncoding() {
		defaultEncoding.setSelection(true);
		otherEncoding.setSelection(false);
		otherEncodingCombo.setEnabled(false);
		if (otherEncodingCombo.getItemCount() > 0) {
			otherEncodingCombo.select(0);
		}
	}

	/**
	 * @since 2.0
	 */
	public void setAnonymous(boolean selected) {
		if (!needsAnonymousLogin) {
			return;
		}

		anonymousButton.setSelection(selected);

		if (selected) {
			oldUsername = repositoryUserNameEditor.getStringValue();
			oldPassword = (repositoryPasswordEditor).getStringValue();
			repositoryUserNameEditor.setStringValue(""); //$NON-NLS-1$
			repositoryPasswordEditor.setStringValue(""); //$NON-NLS-1$
		} else {
			repositoryUserNameEditor.setStringValue(oldUsername);
			repositoryPasswordEditor.setStringValue(oldPassword);
		}

		repositoryUserNameEditor.setEnabled(!selected, compositeContainer);
		repositoryPasswordEditor.setEnabled(!selected, compositeContainer);
		savePasswordButton.setEnabled(!selected);
		if (getWizard() != null) {
			getWizard().getContainer().updateButtons();
		}
	}

	/**
	 * @since 2.0
	 */
	public void setHttpAuth(boolean selected) {
		if (!needsHttpAuth) {
			return;
		}
		httpAuthButton.setSelection(selected);
		if (!selected) {
			oldHttpAuthUserId = httpAuthUserNameEditor.getStringValue();
			oldHttpAuthPassword = httpAuthPasswordEditor.getStringValue();
			httpAuthUserNameEditor.setStringValue(null);
			httpAuthPasswordEditor.setStringValue(null);
		} else {
			httpAuthUserNameEditor.setStringValue(oldHttpAuthUserId);
			httpAuthPasswordEditor.setStringValue(oldHttpAuthPassword);
		}
		httpAuthUserNameEditor.setEnabled(selected, httpAuthComp);
		httpAuthPasswordEditor.setEnabled(selected, httpAuthComp);
		saveHttpPasswordButton.setEnabled(selected);
	}

	/**
	 * @since 2.2
	 */
	public boolean getHttpAuth() {
		return httpAuthButton.getSelection();
	}

	/**
	 * @since 2.0
	 */
	public void setUseDefaultProxy(boolean selected) {
		if (!needsProxy) {
			return;
		}

		systemProxyButton.setSelection(selected);

		if (selected) {
			oldProxyHostname = proxyHostnameEditor.getStringValue();
			oldProxyPort = proxyPortEditor.getStringValue();
			// proxyHostnameEditor.setStringValue(null);
			// proxyPortEditor.setStringValue(null);
		} else {
			proxyHostnameEditor.setStringValue(oldProxyHostname);
			proxyPortEditor.setStringValue(oldProxyPort);
		}
		proxyHostnameEditor.setEnabled(!selected, proxyAuthComp);
		proxyPortEditor.setEnabled(!selected, proxyAuthComp);
		proxyAuthButton.setEnabled(!selected);
		setProxyAuth(proxyAuthButton.getSelection());
	}

	/**
	 * @since 2.0
	 */
	public void setProxyAuth(boolean selected) {
		proxyAuthButton.setSelection(selected);
		proxyAuthButton.setEnabled(!systemProxyButton.getSelection());
		if (!selected) {
			oldProxyUsername = proxyUserNameEditor.getStringValue();
			oldProxyPassword = proxyPasswordEditor.getStringValue();
			proxyUserNameEditor.setStringValue(null);
			proxyPasswordEditor.setStringValue(null);
		} else {
			proxyUserNameEditor.setStringValue(oldProxyUsername);
			proxyPasswordEditor.setStringValue(oldProxyPassword);
		}

		proxyUserNameEditor.setEnabled(selected && !systemProxyButton.getSelection(), proxyAuthComp);
		proxyPasswordEditor.setEnabled(selected && !systemProxyButton.getSelection(), proxyAuthComp);
		saveProxyPasswordButton.setEnabled(selected && !systemProxyButton.getSelection());
	}

	/**
	 * @since 2.2
	 */
	public boolean getProxyAuth() {
		return proxyAuthButton.getSelection();
	}

	/**
	 * @since 3.0
	 */
	protected void addRepositoryTemplatesToServerUrlCombo() {
		final RepositoryTemplateManager templateManager = TasksUiPlugin.getRepositoryTemplateManager();
		for (RepositoryTemplate template : templateManager.getTemplates(connector.getConnectorKind())) {
			serverUrlCombo.add(template.label);
		}
		serverUrlCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = serverUrlCombo.getText();
				RepositoryTemplate template = templateManager.getTemplate(connector.getConnectorKind(), text);
				if (template != null) {
					repositoryTemplateSelected(template);
					return;
				}
			}

		});
	}

	/**
	 * @since 3.0
	 */
	protected void repositoryTemplateSelected(RepositoryTemplate template) {
	}

	/**
	 * @since 2.0
	 */
	protected abstract void createAdditionalControls(Composite parent);

	/**
	 * @since 2.0
	 */
	protected abstract boolean isValidUrl(String url);

	private void updateHyperlinks() {
		if (getRepositoryUrl() != null && getRepositoryUrl().length() > 0) {
			TaskRepository repository = new TaskRepository(connector.getConnectorKind(), getRepositoryUrl());

			String accountCreationUrl = TasksUiPlugin.getConnectorUi(connector.getConnectorKind())
					.getAccountCreationUrl(repository);
			createAccountHyperlink.setEnabled(accountCreationUrl != null);
			createAccountHyperlink.setVisible(accountCreationUrl != null);

			String accountManagementUrl = TasksUiPlugin.getConnectorUi(connector.getConnectorKind())
					.getAccountManagementUrl(repository);
			manageAccountHyperlink.setEnabled(accountManagementUrl != null);
			manageAccountHyperlink.setVisible(accountManagementUrl != null);
		} else {
			createAccountHyperlink.setEnabled(false);
			createAccountHyperlink.setVisible(false);
			manageAccountHyperlink.setEnabled(false);
			manageAccountHyperlink.setVisible(false);
		}
	}

	/**
	 * @since 2.0
	 */
	public String getRepositoryLabel() {
		return repositoryLabelEditor.getStringValue();
	}

	/**
	 * @since 3.0
	 */
	public String getRepositoryUrl() {
		return TaskRepositoryManager.stripSlashes(serverUrlCombo.getText());
	}

	/**
	 * @since 2.0
	 */
	public String getUserName() {
		return repositoryUserNameEditor.getStringValue();
	}

	/**
	 * @since 2.0
	 */
	public String getPassword() {
		return repositoryPasswordEditor.getStringValue();
	}

	/**
	 * @since 2.0
	 */
	public String getHttpAuthUserId() {
		if (needsHttpAuth()) {
			return httpAuthUserNameEditor.getStringValue();
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @since 2.0
	 */
	public String getHttpAuthPassword() {
		if (needsHttpAuth()) {
			return httpAuthPasswordEditor.getStringValue();
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @since 2.0
	 */
	public String getProxyHostname() {
		if (needsProxy()) {
			return proxyHostnameEditor.getStringValue();
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @since 2.0
	 */
	public String getProxyPort() {
		if (needsProxy()) {
			return proxyPortEditor.getStringValue();
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @since 2.0
	 */
	public Boolean getUseDefaultProxy() {
		if (needsProxy()) {
			return systemProxyButton.getSelection();
		} else {
			return true;
		}
	}

	/**
	 * @since 2.0
	 */
	public String getProxyUserName() {
		if (needsProxy()) {
			return proxyUserNameEditor.getStringValue();
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @since 2.0
	 */
	public String getProxyPassword() {
		if (needsProxy()) {
			return proxyPasswordEditor.getStringValue();
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * @since 2.0
	 */
	public void init(IWorkbench workbench) {
		// ignore
	}

	/**
	 * @since 2.0
	 */
	public boolean isAnonymousAccess() {
		if (anonymousButton != null) {
			return anonymousButton.getSelection();
		} else {
			return false;
		}
	}

	/**
	 * Exposes StringFieldEditor.refreshValidState()
	 * 
	 * TODO: is there a better way?
	 */
	private static class RepositoryStringFieldEditor extends StringFieldEditor {
		public RepositoryStringFieldEditor(String name, String labelText, int style, Composite parent) {
			super(name, labelText, style, parent);
		}

		@Override
		public void refreshValidState() {
			try {
				super.refreshValidState();
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Problem refreshing password field", e)); //$NON-NLS-1$
			}
		}

		@Override
		public Text getTextControl() {
			return super.getTextControl();
		}

	}

	@Override
	public boolean isPageComplete() {
		String errorMessage = null;
		String url = getRepositoryUrl();
		errorMessage = isUniqueUrl(url);
		if (errorMessage == null && !isValidUrl(url)) {
			errorMessage = Messages.AbstractRepositorySettingsPage_Enter_a_valid_server_url;
		}
		if (errorMessage == null) {
			errorMessage = credentialsComplete();
		}

		setErrorMessage(errorMessage);
		return errorMessage == null && super.isPageComplete();
	}

	private String credentialsComplete() {
		if ((!needsAnonymousLogin() || !anonymousButton.getSelection()) && isMissingCredentials()) {
			return Messages.AbstractRepositorySettingsPage_Repository_user_name_and_password_must_not_be_blank;
		}
		return null;
	}

	private boolean isMissingCredentials() {
		return repositoryUserNameEditor.getStringValue().trim().equals("") //$NON-NLS-1$
				|| repositoryPasswordEditor.getStringValue().trim().equals(""); //$NON-NLS-1$
	}

	/**
	 * @since 2.0
	 */
	protected String isUniqueUrl(String urlString) {
		if (!urlString.equals(originalUrl)) {
			if (repositoryUrls == null) {
				List<TaskRepository> repositories = TasksUi.getRepositoryManager().getAllRepositories();
				repositoryUrls = new HashSet<String>(repositories.size());
				for (TaskRepository repository : repositories) {
					repositoryUrls.add(repository.getRepositoryUrl());
				}
			}

			if (repositoryUrls.contains(urlString)) {
				return Messages.AbstractRepositorySettingsPage_Repository_already_exists;
			}
		}
		return null;
	}

	/**
	 * @since 2.0
	 */
	@Deprecated
	public void setRepository(TaskRepository repository) {
		this.repository = repository;
	}

	/**
	 * @since 2.0
	 */
	public void setVersion(String previousVersion) {
		if (previousVersion == null) {
			serverVersion = TaskRepository.NO_VERSION_SPECIFIED;
		} else {
			serverVersion = previousVersion;
		}
	}

	/**
	 * @since 2.0
	 */
	public String getVersion() {
		return serverVersion;
	}

	/**
	 * @since 2.0
	 */
	public TaskRepository getRepository() {
		return repository;
	}

	/**
	 * @since 2.0
	 */
	public String getCharacterEncoding() {
		if (defaultEncoding == null) {
			return null;
		}

		if (defaultEncoding.getSelection()) {
			return TaskRepository.DEFAULT_CHARACTER_ENCODING;
		} else {
			if (otherEncodingCombo.getSelectionIndex() > -1) {
				return otherEncodingCombo.getItem(otherEncodingCombo.getSelectionIndex());
			} else {
				return TaskRepository.DEFAULT_CHARACTER_ENCODING;
			}
		}
	}

	/**
	 * @since 2.0
	 */
	public TaskRepository createTaskRepository() {
		TaskRepository repository = new TaskRepository(connector.getConnectorKind(), getRepositoryUrl());
		applyTo(repository);
		return repository;
	}

	/**
	 * @since 2.2
	 */
	@Override
	public void applyTo(TaskRepository repository) {
		repository.setVersion(getVersion());
		if (needsEncoding()) {
			repository.setCharacterEncoding(getCharacterEncoding());
		}

		if (isAnonymousAccess()) {
			repository.setCredentials(AuthenticationType.REPOSITORY, null, getSavePassword());
		} else {
			AuthenticationCredentials credentials = new AuthenticationCredentials(getUserName(), getPassword());
			repository.setCredentials(AuthenticationType.REPOSITORY, credentials, getSavePassword());
		}
		repository.setRepositoryLabel(getRepositoryLabel());

		if (needsHttpAuth()) {
			if (getHttpAuth()) {
				AuthenticationCredentials webCredentials = new AuthenticationCredentials(getHttpAuthUserId(),
						getHttpAuthPassword());
				repository.setCredentials(AuthenticationType.HTTP, webCredentials, getSaveHttpPassword());
			} else {
				repository.setCredentials(AuthenticationType.HTTP, null, getSaveHttpPassword());
			}
		}

		if (needsProxy()) {
			repository.setDefaultProxyEnabled(getUseDefaultProxy());
			repository.setProperty(TaskRepository.PROXY_HOSTNAME, getProxyHostname());
			repository.setProperty(TaskRepository.PROXY_PORT, getProxyPort());
			if (getProxyAuth()) {
				AuthenticationCredentials webCredentials = new AuthenticationCredentials(getProxyUserName(),
						getProxyPassword());
				repository.setCredentials(AuthenticationType.PROXY, webCredentials, getSaveProxyPassword());
			} else {
				repository.setCredentials(AuthenticationType.PROXY, null, getSaveProxyPassword());
			}
		}

		repository.setOffline(disconnectedButton.getSelection());

		super.applyTo(repository);
	}

	/**
	 * @since 2.0
	 */
	public AbstractRepositoryConnector getConnector() {
		return connector;
	}

	/**
	 * @since 2.0
	 */
	public boolean needsEncoding() {
		return needsEncoding;
	}

	/**
	 * @since 2.0
	 */
	public boolean needsTimeZone() {
		return needsTimeZone;
	}

	/**
	 * @since 2.0
	 */
	public boolean needsAnonymousLogin() {
		return needsAnonymousLogin;
	}

	/**
	 * @since 2.0
	 */
	public boolean needsAdvanced() {
		return needsAdvanced;
	}

	/**
	 * @since 2.0
	 */
	public void setNeedsEncoding(boolean needsEncoding) {
		this.needsEncoding = needsEncoding;
	}

	/**
	 * @since 2.0
	 */
	public void setNeedsTimeZone(boolean needsTimeZone) {
		this.needsTimeZone = needsTimeZone;
	}

	/**
	 * @since 2.0
	 */
	public void setNeedsAdvanced(boolean needsAdvanced) {
		this.needsAdvanced = needsAdvanced;
	}

	/**
	 * @since 2.0
	 */
	public boolean needsHttpAuth() {
		return this.needsHttpAuth;
	}

	/**
	 * @since 2.0
	 */
	public void setNeedsHttpAuth(boolean needsHttpAuth) {
		this.needsHttpAuth = needsHttpAuth;
	}

	/**
	 * @since 2.0
	 */
	public void setNeedsProxy(boolean needsProxy) {
		this.needsProxy = needsProxy;
	}

	/**
	 * @since 2.0
	 */
	public boolean needsProxy() {
		return this.needsProxy;
	}

	/**
	 * @since 2.0
	 */
	public void setNeedsAnonymousLogin(boolean needsAnonymousLogin) {
		this.needsAnonymousLogin = needsAnonymousLogin;
	}

	public void setNeedsValidation(boolean needsValidation) {
		this.needsValidation = needsValidation;
	}

	/**
	 * @since 2.0
	 */
	public boolean needsValidation() {
		return needsValidation;
	}

	/**
	 * Public for testing.
	 * 
	 * @since 2.0
	 */
	public void setUrl(String url) {
		serverUrlCombo.setText(url);
	}

	/**
	 * Public for testing.
	 * 
	 * @since 2.0
	 */
	public void setUserId(String id) {
		repositoryUserNameEditor.setStringValue(id);
	}

	/**
	 * Public for testing.
	 * 
	 * @since 2.0
	 */
	public void setPassword(String pass) {
		repositoryPasswordEditor.setStringValue(pass);
	}

	/**
	 * @since 2.2
	 */
	public Boolean getSavePassword() {
		return savePasswordButton.getSelection();
	}

	/**
	 * @since 2.2
	 */
	public Boolean getSaveProxyPassword() {
		if (needsProxy()) {
			return saveProxyPasswordButton.getSelection();
		} else {
			return false;
		}
	}

	/**
	 * @since 2.2
	 */
	public Boolean getSaveHttpPassword() {
		if (needsHttpAuth()) {
			return saveHttpPasswordButton.getSelection();
		} else {
			return false;
		}
	}

	/**
	 * Validate settings provided by the {@link #getValidator(TaskRepository) validator}, typically the server settings.
	 * 
	 * @since 2.0
	 */
	protected void validateSettings() {
		final Validator validator = getValidator(createTaskRepository());
		if (validator == null) {
			return;
		}

		try {
			getWizard().getContainer().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(Messages.AbstractRepositorySettingsPage_Validating_server_settings,
							IProgressMonitor.UNKNOWN);
					try {
						validator.run(monitor);
						if (validator.getStatus() == null) {
							validator.setStatus(Status.OK_STATUS);
						}
					} catch (CoreException e) {
						validator.setStatus(e.getStatus());
					} catch (OperationCanceledException e) {
						validator.setStatus(Status.CANCEL_STATUS);
						throw new InterruptedException();
					} catch (Exception e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}
			});
		} catch (InvocationTargetException e) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					Messages.AbstractRepositorySettingsPage_Internal_error_validating_repository, e.getCause()));
			return;
		} catch (InterruptedException e) {
			// canceled
			return;
		}

		applyValidatorResult(validator);
		getWizard().getContainer().updateButtons();
	}

	/**
	 * @since 3.1
	 */
	@Override
	protected IStatus validate() {
		return null;
	}

	/**
	 * @since 2.0
	 */
	protected void applyValidatorResult(Validator validator) {
		IStatus status = validator.getStatus();
		String message = status.getMessage();
		if (message == null || message.length() == 0) {
			message = null;
		}
		switch (status.getSeverity()) {
		case IStatus.OK:
			if (status == Status.OK_STATUS) {
				if (getUserName().length() > 0) {
					message = Messages.AbstractRepositorySettingsPage_Authentication_credentials_are_valid;
				} else {
					message = Messages.AbstractRepositorySettingsPage_Repository_is_valid;
				}
			}
			setMessage(message, IMessageProvider.INFORMATION);
			break;
		case IStatus.INFO:
			setMessage(message, IMessageProvider.INFORMATION);
			break;
		case IStatus.WARNING:
			setMessage(message, IMessageProvider.WARNING);
			break;
		default:
			setMessage(message, IMessageProvider.ERROR);
			break;
		}
		setErrorMessage(null);
	}

	/**
	 * @since 2.0
	 */
	protected abstract Validator getValidator(TaskRepository repository);

	/**
	 * Public for testing.
	 * 
	 * @since 2.0
	 */
	public abstract class Validator {

		private IStatus status;

		public abstract void run(IProgressMonitor monitor) throws CoreException;

		public IStatus getStatus() {
			return status;
		}

		public void setStatus(IStatus status) {
			this.status = status;
		}

	}

}
