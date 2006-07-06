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

package org.eclipse.mylar.internal.tasklist.ui.wizards;

import java.nio.charset.Charset;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;

/**
 * @author Mik Kersten
 */
public abstract class AbstractRepositorySettingsPage extends WizardPage {

	protected static final String LABEL_SERVER = "Server: ";

	protected static final String LABEL_USER = "User Name: ";

	protected static final String LABEL_PASSWORD = "Password: ";

	protected static final String URL_PREFIX_HTTPS = "https://";

	protected static final String URL_PREFIX_HTTP = "http://";

	private AbstractRepositoryConnector connector;

	protected StringFieldEditor serverUrlEditor;

	protected StringFieldEditor userNameEditor;

	protected StringFieldEditor characterEncodingEditor;

	private String serverVersion = TaskRepository.NO_VERSION_SPECIFIED;

	protected StringFieldEditor passwordEditor;

	protected TaskRepository repository;

	private Button validateServerButton;

	private Combo otherEncodingCombo;

	private Button defaultEncoding;

	// private Combo timeZonesCombo;

	protected Button anonymousButton;

	private String oldUsername;

	private String oldPassword;

	private boolean needsAnonymousLogin;

	private boolean needsTimeZone;

	private boolean needsEncoding;

	public AbstractRepositorySettingsPage(String title, String description, AbstractRepositoryConnector connector) {
		super(title);
		super.setTitle(title);
		super.setDescription(description);
		this.connector = connector;

		setNeedsAnonymousLogin(false);
		setNeedsEncoding(true);
		setNeedsTimeZone(true);
	}

	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		FillLayout layout = new FillLayout();
		container.setLayout(layout);

		serverUrlEditor = new StringFieldEditor("", LABEL_SERVER, StringFieldEditor.UNLIMITED, container) {

			@Override
			protected boolean doCheckState() {
				return isValidUrl(getStringValue());
			}

			@Override
			protected void valueChanged() {
				super.valueChanged();
				if (getWizard() != null) {
					getWizard().getContainer().updateButtons();
				}
			}
		};
		serverUrlEditor.setErrorMessage("Server path must be a valid http(s):// url");

		if (needsAnonymousLogin()) {
			anonymousButton = new Button(container, SWT.CHECK);
			anonymousButton.setText("Anonymous Access");
			anonymousButton.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					boolean selected = anonymousButton.getSelection();
					updateAnonymousButton(selected, container);
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					// ignore
				}
			});

			Label anonymousLabel = new Label(container, SWT.NONE);
			anonymousLabel.setText("");
		}

		userNameEditor = new StringFieldEditor("", LABEL_USER, StringFieldEditor.UNLIMITED, container);
		passwordEditor = new RepositoryStringFieldEditor("", LABEL_PASSWORD, StringFieldEditor.UNLIMITED, container);
		if (repository != null) {
			oldUsername = repository.getUserName();
			oldPassword = repository.getPassword();
			try {
				serverUrlEditor.setStringValue(repository.getUrl());
				userNameEditor.setStringValue(repository.getUserName());
				passwordEditor.setStringValue(repository.getPassword());
			} catch (Throwable t) {
				MylarStatusHandler.fail(t, "could not set field value for: " + repository, false);
			}
		} else {
			oldUsername = "";
			oldPassword = "";
		}
		// bug 131656: must set echo char after setting value on Mac
		((RepositoryStringFieldEditor) passwordEditor).getTextControl().setEchoChar('*');

		if (needsAnonymousLogin()) {
			// do this after username and password widgets have been intialized
			if (repository != null) {
				anonymousButton.setSelection(isAnonymousAccess());
			}
			updateAnonymousButton(anonymousButton.getSelection(), container);
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

		createAdditionalControls(container);

		if (needsEncoding()) {
			Composite encodingContainer = new Composite(container, SWT.NONE);
			encodingContainer.setLayout(new GridLayout());
			GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).applyTo(encodingContainer);

			Group encodingGroup = new Group(encodingContainer, SWT.FLAT);
			encodingGroup.setText("Character Encoding");
			encodingGroup.setLayout(new GridLayout(2, false));
			defaultEncoding = new Button(encodingGroup, SWT.RADIO);
			defaultEncoding.setText("Default (" + TaskRepository.DEFAULT_CHARACTER_ENCODING + ")");
			defaultEncoding.setSelection(true);
			GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).applyTo(defaultEncoding);

			final Button otherEncoding = new Button(encodingGroup, SWT.RADIO);
			otherEncoding.setText("Other:");
			otherEncodingCombo = new Combo(encodingGroup, SWT.READ_ONLY);
			for (String encoding : Charset.availableCharsets().keySet()) {
				if (!encoding.equals(TaskRepository.DEFAULT_CHARACTER_ENCODING)) {
					otherEncodingCombo.add(encoding);
				}
			}

			setDefaultEncoding();

			otherEncoding.addSelectionListener(new SelectionAdapter() {

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
					if (repositoryEncoding != null && !repositoryEncoding.equals(defaultEncoding)) {
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
					MylarStatusHandler.fail(t, "could not set field value for: " + repository, false);
				}
			}
		}

		validateServerButton = new Button(container, SWT.PUSH);
		validateServerButton.setText("Validate Settings");
		validateServerButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				validateSettings();
			}
		});

		setControl(container);
	}

	private void setDefaultEncoding() {
		defaultEncoding.setSelection(true);
		otherEncodingCombo.setEnabled(false);
		if (otherEncodingCombo.getItemCount() > 0) {
			otherEncodingCombo.select(0);
		}
	}

	private void updateAnonymousButton(boolean selected, Composite parent) {
		if (selected) {
			oldUsername = userNameEditor.getStringValue();
			oldPassword = ((StringFieldEditor) passwordEditor).getStringValue();
			userNameEditor.setStringValue(null);
			((StringFieldEditor) passwordEditor).setStringValue(null);
		} else {
			userNameEditor.setStringValue(oldUsername);
			((StringFieldEditor) passwordEditor).setStringValue(oldPassword);
		}

		userNameEditor.setEnabled(!selected, parent);
		((StringFieldEditor) passwordEditor).setEnabled(!selected, parent);
	}

	protected abstract void createAdditionalControls(Composite parent);

	protected abstract void validateSettings();

	protected abstract boolean isValidUrl(String name);

	public String getServerUrl() {
		return serverUrlEditor.getStringValue();
	}

	public String getUserName() {
		return userNameEditor.getStringValue();
	}

	public String getPassword() {
		return passwordEditor.getStringValue();
	}

	public void init(IWorkbench workbench) {
		// ignore
	}

	private boolean isAnonymousAccess() {
		return "".equals(getUserName()) && "".equals(getPassword());
	}

	/**
	 * Exposes StringFieldEditor.refreshValidState() TODO: is there a better
	 * way?
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
				MylarStatusHandler.log(e, "problem refreshing password field");
			}
		}

		@Override
		public Text getTextControl() {
			return super.getTextControl();
		}

	}

	@Override
	public boolean isPageComplete() {
		return isValidUrl(serverUrlEditor.getStringValue());
	}

	public void setRepository(TaskRepository repository) {
		this.repository = repository;
	}

	public void setVersion(String previousVersion) {
		if (previousVersion == null) {
			serverVersion = TaskRepository.NO_VERSION_SPECIFIED;
		} else {
			serverVersion = previousVersion;
		}
	}

	public String getVersion() {
		return serverVersion;
	}

	public TaskRepository getRepository() {
		return repository;
	}

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

	// public String getTimeZoneId() {
	// return (timeZonesCombo != null) ?
	// timeZonesCombo.getItem(timeZonesCombo.getSelectionIndex()) : null;
	// }

	public TaskRepository createTaskRepository() {
		// TaskRepository repository = new
		// TaskRepository(connector.getRepositoryType(), getServerUrl(),
		// getVersion(),
		// getCharacterEncoding(), getTimeZoneId());

		TaskRepository repository = new TaskRepository(connector.getRepositoryType(), getServerUrl(), getVersion(),
				getCharacterEncoding(), "");

		repository.setAuthenticationCredentials(getUserName(), getPassword());
		return repository;
	}

	public AbstractRepositoryConnector getConnector() {
		return connector;
	}

	public boolean needsEncoding() {
		return needsEncoding;
	}

	public boolean needsTimeZone() {
		return needsTimeZone;
	}

	public boolean needsAnonymousLogin() {
		return needsAnonymousLogin;
	}

	public void setNeedsEncoding(boolean needsEncoding) {
		this.needsEncoding = needsEncoding;
	}

	public void setNeedsTimeZone(boolean needsTimeZone) {
		this.needsTimeZone = needsTimeZone;
	}

	public void setNeedsAnonymousLogin(boolean needsAnonymousLogin) {
		this.needsAnonymousLogin = needsAnonymousLogin;
	}

	/** for testing */
	public void setUrl(String url) {
		serverUrlEditor.setStringValue(url);
	}

	/** for testing */
	public void setUserId(String id) {
		userNameEditor.setStringValue(id);
	}

	/** for testing */
	public void setPassword(String pass) {
		passwordEditor.setStringValue(pass);
	}

}