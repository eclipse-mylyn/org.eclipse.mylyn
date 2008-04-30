/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.wizard;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracClientFactory;
import org.eclipse.mylyn.internal.trac.core.TracException;
import org.eclipse.mylyn.internal.trac.core.TracLoginException;
import org.eclipse.mylyn.internal.trac.core.TracPermissionDeniedException;
import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.ui.TracUiPlugin;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Steffen Pingel
 */
public class TracRepositorySettingsPage extends AbstractRepositorySettingsPage {

	private static final String TITLE = "Trac Repository Settings";

	private static final String DESCRIPTION = "Example: http://trac.edgewall.org";

	private Combo accessTypeCombo;

	/** Supported access types. */
	private Version[] versions;

	public TracRepositorySettingsPage(AbstractRepositoryConnectorUi repositoryUi) {
		super(TITLE, DESCRIPTION, repositoryUi);

		setNeedsAnonymousLogin(true);
		setNeedsEncoding(false);
		setNeedsTimeZone(false);
	}

	@Override
	protected void repositoryTemplateSelected(RepositoryTemplate template) {
		repositoryLabelEditor.setStringValue(template.label);
		setUrl(template.repositoryUrl);
		setAnonymous(template.anonymous);

		try {
			Version version = Version.valueOf(template.version);
			setTracVersion(version);
		} catch (RuntimeException ex) {
			setTracVersion(Version.TRAC_0_9);
		}

		getContainer().updateButtons();
	}

	@Override
	protected void createAdditionalControls(final Composite parent) {
		addRepositoryTemplatesToServerUrlCombo();

		Label accessTypeLabel = new Label(parent, SWT.NONE);
		accessTypeLabel.setText("Access Type: ");
		accessTypeCombo = new Combo(parent, SWT.READ_ONLY);

		accessTypeCombo.add("Automatic (Use Validate Settings)");
		versions = Version.values();
		for (Version version : versions) {
			accessTypeCombo.add(version.toString());
		}
		if (repository != null) {
			setTracVersion(Version.fromVersion(repository.getVersion()));
		} else {
			setTracVersion(null);
		}
		accessTypeCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (accessTypeCombo.getSelectionIndex() > 0) {
					setVersion(versions[accessTypeCombo.getSelectionIndex() - 1].name());
				}
				getWizard().getContainer().updateButtons();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});
	}

	@Override
	public boolean isPageComplete() {
		// make sure "Automatic" is not selected as a version
		return super.isPageComplete() && accessTypeCombo != null && accessTypeCombo.getSelectionIndex() != 0;
	}

	@Override
	protected boolean isValidUrl(String name) {
		if ((name.startsWith(URL_PREFIX_HTTPS) || name.startsWith(URL_PREFIX_HTTP)) && !name.endsWith("/")) {
			try {
				new URL(name);
				return true;
			} catch (MalformedURLException e) {
			}
		}
		return false;
	}

	public Version getTracVersion() {
		if (accessTypeCombo.getSelectionIndex() == 0) {
			return null;
		} else {
			return versions[accessTypeCombo.getSelectionIndex() - 1];
		}
	}

	public void setTracVersion(Version version) {
		if (version == null) {
			// select "Automatic"
			accessTypeCombo.select(0);
		} else {
			int i = accessTypeCombo.indexOf(version.toString());
			if (i != -1) {
				accessTypeCombo.select(i);
			}
			setVersion(version.name());
		}
	}

	@Override
	protected void applyValidatorResult(Validator validator) {
		super.applyValidatorResult(validator);

		if (((TracValidator) validator).getResult() != null) {
			setTracVersion(((TracValidator) validator).getResult());
		}
	}

	// public for testing
	public class TracValidator extends Validator {

		private final String repositoryUrl;

		private final TaskRepository taskRepository;

		private final Version version;

		private Version result;

		public TracValidator(TaskRepository taskRepository, Version version) {
			this.repositoryUrl = taskRepository.getRepositoryUrl();
			this.taskRepository = taskRepository;
			this.version = version;
		}

		@Override
		public void run(IProgressMonitor monitor) throws CoreException {
			try {
				//validate(Provider.of(monitor));
				validate(monitor);
			} catch (MalformedURLException e) {
				throw new CoreException(RepositoryStatus.createStatus(repositoryUrl, IStatus.ERROR,
						TracUiPlugin.PLUGIN_ID, INVALID_REPOSITORY_URL));
			} catch (TracLoginException e) {
				throw new CoreException(RepositoryStatus.createStatus(repositoryUrl, IStatus.ERROR,
						TracUiPlugin.PLUGIN_ID, INVALID_LOGIN));
			} catch (TracPermissionDeniedException e) {
				throw new CoreException(RepositoryStatus.createStatus(repositoryUrl, IStatus.ERROR,
						TracUiPlugin.PLUGIN_ID, "Insufficient permissions for selected access type."));
			} catch (TracException e) {
				String message = "No Trac repository found at url";
				if (e.getMessage() != null) {
					message += ": " + e.getMessage();
				}
				throw new CoreException(RepositoryStatus.createStatus(repositoryUrl, IStatus.ERROR,
						TracUiPlugin.PLUGIN_ID, message));
			}
		}

		public void validate(IProgressMonitor monitor) throws MalformedURLException, TracException {
			AbstractWebLocation location = new TaskRepositoryLocationFactory().createWebLocation(taskRepository);

			if (version != null) {
				ITracClient client = TracClientFactory.createClient(location, version);
				client.validate(monitor);
			} else {
				// probe version: XML-RPC access first, then web
				// access
				try {
					ITracClient client = TracClientFactory.createClient(location, Version.XML_RPC);
					client.validate(monitor);
					result = Version.XML_RPC;
				} catch (TracException e) {
					try {
						ITracClient client = TracClientFactory.createClient(location, Version.TRAC_0_9);
						client.validate(monitor);
						result = Version.TRAC_0_9;

						if (e instanceof TracPermissionDeniedException) {
							setStatus(RepositoryStatus.createStatus(repositoryUrl, IStatus.INFO,
									TracUiPlugin.PLUGIN_ID,
									"Authentication credentials are valid. Note: Insufficient permissions for XML-RPC access, falling back to web access."));
						}
					} catch (TracLoginException e2) {
						throw e;
					} catch (TracException e2) {
						throw new TracException();
					}
				}
			}
		}

		public Version getResult() {
			return result;
		}

	}

	@Override
	protected Validator getValidator(TaskRepository repository) {
		return new TracValidator(repository, getTracVersion());
	}

}
