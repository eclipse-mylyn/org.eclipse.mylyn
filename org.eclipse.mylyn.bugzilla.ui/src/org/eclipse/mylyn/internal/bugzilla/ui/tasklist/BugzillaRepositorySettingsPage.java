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

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClientFactory;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaStatus;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants.BugzillaServerVersion;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaRepositorySettingsPage extends AbstractRepositorySettingsPage {

	private static final String LABEL_SHORT_LOGINS = "Local users enabled:";

	public static final String LABEL_AUTOMATIC_VERSION = "Automatic (Use Validate Settings)";

	private static final String TITLE = "Bugzilla Repository Settings";

	private static final String DESCRIPTION = "Example: https://bugs.eclipse.org/bugs (do not include index.cgi)";

	protected Combo repositoryVersionCombo;

	private Button cleanQAContact;

	public BugzillaRepositorySettingsPage(AbstractRepositoryConnectorUi repositoryUi) {
		super(TITLE, DESCRIPTION, repositoryUi);
		setNeedsAnonymousLogin(true);
		setNeedsEncoding(true);
		setNeedsTimeZone(false);
		setNeedsHttpAuth(true);
	}

	@Override
	protected void createAdditionalControls(Composite parent) {

		for (RepositoryTemplate template : connector.getTemplates()) {
			serverUrlCombo.add(template.label);
		}
		serverUrlCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = serverUrlCombo.getText();
				RepositoryTemplate template = connector.getTemplate(text);
				if (template != null) {
					repositoryLabelEditor.setStringValue(template.label);
					setUrl(template.repositoryUrl);
					// setAnonymous(info.anonymous);
					setBugzillaVersion(template.version);
					if (template.characterEncoding != null) {
						setEncoding(template.characterEncoding);
					}
					getContainer().updateButtons();
					return;
				}
			}
		});

		Label repositoryVersionLabel = new Label(parent, SWT.NONE);
		repositoryVersionLabel.setText("Repository Version: ");
		repositoryVersionCombo = new Combo(parent, SWT.READ_ONLY);

		repositoryVersionCombo.add(LABEL_AUTOMATIC_VERSION);

		for (BugzillaServerVersion version : BugzillaServerVersion.values()) {
			repositoryVersionCombo.add(version.toString());
		}
		if (repository != null && repositoryVersionCombo.indexOf(repository.getVersion()) >= 0) {
			repositoryVersionCombo.select(repositoryVersionCombo.indexOf(repository.getVersion()));
		} else {
			int defaultIndex = repositoryVersionCombo.getItemCount() - 1;
			repositoryVersionCombo.select(defaultIndex);
			setVersion(repositoryVersionCombo.getItem(defaultIndex));
		}

		repositoryVersionCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (repositoryVersionCombo.getSelectionIndex() >= 0) {
					setVersion(repositoryVersionCombo.getItem(repositoryVersionCombo.getSelectionIndex()));
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});

		Label shortLoginLabel = new Label(parent, SWT.NONE);
		shortLoginLabel.setText(LABEL_SHORT_LOGINS);
		cleanQAContact = new Button(parent, SWT.CHECK | SWT.LEFT);
		if (repository != null) {
			boolean shortLogin = Boolean.parseBoolean(repository
					.getProperty(IBugzillaConstants.REPOSITORY_SETTING_SHORT_LOGIN));
			cleanQAContact.setSelection(shortLogin);
		}

	}

	public void setBugzillaVersion(String version) {
		if (version == null) {
			repositoryVersionCombo.select(0);
		} else {
			int i = repositoryVersionCombo.indexOf(version.toString());
			if (i != -1) {
				repositoryVersionCombo.select(i);
				setVersion(version);
			} else {
				for (IBugzillaConstants.BugzillaServerVersion serverVersion : IBugzillaConstants.BugzillaServerVersion
						.values()) {
					if (version.startsWith(serverVersion.toString())) {
						i = repositoryVersionCombo.indexOf(serverVersion.toString());
						if (i != -1) {
							repositoryVersionCombo.select(i);
							setVersion(serverVersion.toString());
							break;
						}
					}
				}
				if (i == -1) {
					StatusHandler.log("Could not resolve repository version: " + version, this);
					setVersion(IBugzillaConstants.BugzillaServerVersion.SERVER_218.toString());
				}
			}
		}
	}

	@Override
	public void updateProperties(TaskRepository repository) {
		repository.setProperty(IBugzillaConstants.REPOSITORY_SETTING_SHORT_LOGIN, String.valueOf(cleanQAContact
				.getSelection()));
	}

	@Override
	public boolean isPageComplete() {
		return super.isPageComplete();
	}

	@Override
	protected boolean isValidUrl(String name) {
		if (name.startsWith(URL_PREFIX_HTTPS) || name.startsWith(URL_PREFIX_HTTP)) {
			try {
				new URL(name);
				return true;
			} catch (MalformedURLException e) {
			}
		}
		return false;
	}

//	/* public for testing */
//	@Override
//	public void validateSettings() {
//
//		final String serverUrl = getServerUrl();
//		final String newUserId = getUserName();
//		final String newPassword = getPassword();
//		final boolean isAnonymous = isAnonymousAccess();
//		final String newEncoding = getCharacterEncoding();
//		final String httpAuthUser = getHttpAuthUserId();
//		final String httpAuthPass = getHttpAuthPassword();
//		final Proxy tempProxy;
//		try {
//			setMessage("Validating server settings...");
//			setErrorMessage(null);
//			if (getUseDefaultProxy()) {
//				tempProxy = TaskRepository.getSystemProxy();
//			} else {
//				tempProxy = WebClientUtil.getProxy(getProxyHostname(), getProxyPort(), getProxyUsername(),
//						getProxyPassword());
//			}
//			final boolean checkVersion = repositoryVersionCombo.getSelectionIndex() == 0;
//			final String[] version = new String[1];
//			getWizard().getContainer().run(true, false, new IRunnableWithProgress() {
//				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
//					if (monitor == null) {
//						monitor = new NullProgressMonitor();
//					}
//					try {
//						monitor.beginTask("Validating server settings", IProgressMonitor.UNKNOWN);
//						BugzillaClient client = null;
//						if (isAnonymous) {
//							client = BugzillaClientFactory.createClient(serverUrl, newUserId, newPassword,
//									httpAuthUser, httpAuthPass, tempProxy, newEncoding);
//							client.logout();
//						} else if (version != null) {
//							client = BugzillaClientFactory.createClient(serverUrl, newUserId, newPassword,
//									httpAuthUser, httpAuthPass, tempProxy, newEncoding);
//							client.validate();
//						}
//						if (checkVersion && client != null) {
//							RepositoryConfiguration config = client.getRepositoryConfiguration();
//							if (config != null) {
//								version[0] = config.getInstallVersion();
//							}
//						}
//
//					} catch (Exception ex) {
//						throw new InvocationTargetException(ex);
//
//					} finally {
//						monitor.done();
//					}
//				}
//			});
//
//			if (version[0] != null) {
//				setBugzillaVersion(version[0]);
//			}
//
//			if (!isAnonymous) {
//				setMessage("Valid Bugzilla server found and your login was accepted");
//			} else {
//				setMessage("Valid Bugzilla server found");
//			}
//		} catch (InvocationTargetException e) {
//			setMessage(null);
//			displayError(serverUrl, e.getTargetException());
//
//		} catch (InterruptedException e) {
//			setErrorMessage("Could not connect to Bugzilla server or authentication failed");
//		}
//	}


	@Override
	protected Validator getValidator(TaskRepository repository) {
		
		if(repositoryVersionCombo.getSelectionIndex() != 0) {
			return new BugzillaValidator(repository, repositoryVersionCombo.getItem(repositoryVersionCombo.getSelectionIndex()));
		} else {
			return new BugzillaValidator(repository, null);
		}
	}
	
//	public String getBugzillaVersion() {
//		if (repositoryVersionCombo.getSelectionIndex() == 0) {
//			return null;
//		} else {
//			return repositoryVersionCombo.getItem(repositoryVersionCombo.getSelectionIndex());
//		}
//	}

	@Override
	protected void applyValidatorResult(Validator validator) {
		super.applyValidatorResult(validator);

		if (((BugzillaValidator) validator).getResult() != null && ((BugzillaValidator) validator).getResult() != null) {
			setBugzillaVersion(((BugzillaValidator) validator).getResult());			
		}
	}

	public class BugzillaValidator extends Validator {

		final String serverUrl;

		final String newUserId;

		final String newPassword;

		final boolean isAnonymous;

		final String newEncoding;

		final String httpAuthUser;

		final String httpAuthPass;
		
		final Proxy proxy;

		private String[] versions = new String[1];;

		public BugzillaValidator(TaskRepository repository, String version) {
			serverUrl = getServerUrl();
			newUserId = getUserName();
			newPassword = getPassword();
			isAnonymous = isAnonymousAccess();
			newEncoding = getCharacterEncoding();
			httpAuthUser = getHttpAuthUserId();
			httpAuthPass = getHttpAuthPassword();
			proxy = repository.getProxy();
			versions[0] = version;
		}

		@Override
		public void run(IProgressMonitor monitor) throws CoreException {
			try {
				validate(monitor);
			} catch (Exception e) {
				displayError(serverUrl, e);
//				IStatus status;
//				if (e instanceof MalformedURLException) {
//					status = new MylarStatus(Status.WARNING, BugzillaCorePlugin.PLUGIN_ID,
//							IMylarStatusConstants.NETWORK_ERROR, "Server URL is invalid.");
//				} else if (e instanceof CoreException) {
//					status = ((CoreException) e).getStatus();
//				} else if (e instanceof IOException) {
//					status = new MylarStatus(Status.WARNING, BugzillaCorePlugin.PLUGIN_ID,
//							IMylarStatusConstants.IO_ERROR, serverUrl, e.getMessage());
//				} else {
//					status = new MylarStatus(Status.WARNING, BugzillaCorePlugin.PLUGIN_ID,
//							IMylarStatusConstants.NETWORK_ERROR, serverUrl, e.getMessage());
//				}
//				MylarStatusHandler.displayStatus("Validation failed", status);
			}
		}
		

		private void displayError(final String serverUrl, Throwable e) {
			IStatus status;
			if (e instanceof MalformedURLException) {
				status = new BugzillaStatus(Status.WARNING, BugzillaCorePlugin.PLUGIN_ID, RepositoryStatus.ERROR_NETWORK,
						"Server URL is invalid.");
			} else if (e instanceof CoreException) {
				status = ((CoreException) e).getStatus();
			} else if (e instanceof IOException) {
				status = new BugzillaStatus(Status.WARNING, BugzillaCorePlugin.PLUGIN_ID, RepositoryStatus.ERROR_IO,
						serverUrl, e.getMessage());
			} else {
				status = new BugzillaStatus(Status.WARNING, BugzillaCorePlugin.PLUGIN_ID, RepositoryStatus.ERROR_NETWORK,
						serverUrl, e.getMessage());
			}
			StatusHandler.displayStatus("Validation failed", status);
			setStatus(status);
		}

		public void validate(IProgressMonitor monitor) throws IOException, CoreException {

			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			try {
				monitor.beginTask("Validating server settings", IProgressMonitor.UNKNOWN);
				BugzillaClient client = null;

//				Proxy tempProxy = Proxy.NO_PROXY;
//
//				if (getUseDefaultProxy()) {
//					tempProxy = TaskRepository.getSystemProxy();
//				} else {
//					tempProxy = WebClientUtil.getProxy(getProxyHostname(), getProxyPort(), getProxyUsername(),
//							getProxyPassword());
//				}
				boolean checkVersion = versions[0] == null;

				if (isAnonymous) {
					client = BugzillaClientFactory.createClient(serverUrl, newUserId, newPassword, httpAuthUser,
							httpAuthPass, proxy, newEncoding);
					client.logout();
				} else if (versions != null) {
					client = BugzillaClientFactory.createClient(serverUrl, newUserId, newPassword, httpAuthUser,
							httpAuthPass, proxy, newEncoding);
					client.validate();
				}
				if (checkVersion && client != null) {
					RepositoryConfiguration config = client.getRepositoryConfiguration();
					if (config != null) {
						versions[0] = config.getInstallVersion();
					}
				}

			} finally {
				monitor.done();
			}
		}

		public String getResult() {
			return versions[0];
		}

	}

}