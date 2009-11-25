/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *      Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.oslc.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.oslc.core.IOslcConnector;
import org.eclipse.mylyn.internal.oslc.core.IOslcCoreConstants;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceDescriptor;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceProvider;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Robert Elves
 */
public abstract class OslcRepositorySettingsPage extends AbstractRepositorySettingsPage {

	private static final String OSLC_BASEURL = "oslc.baseurl"; //$NON-NLS-1$

	private OslcServiceDescriptor descriptor;

	protected Text baseText;

	public OslcRepositorySettingsPage(String name, String desc, TaskRepository taskRepository) {
		super(name, desc, taskRepository);
		setNeedsAnonymousLogin(false);
		setNeedsEncoding(false);
		setNeedsTimeZone(false);
		setNeedsHttpAuth(false);
	}

	public OslcServiceDescriptor getProvider() {
		return descriptor;
	}

	public void setServiceDescriptor(OslcServiceDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	protected String getBaseUrl() {
		return baseText.getText();
	}

	@Override
	protected void createSettingControls(Composite parent) {

		Label baseUrlLabel = new Label(parent, SWT.NONE);
		baseUrlLabel.setText("Base URL:"); //$NON-NLS-1$
		baseText = new Text(parent, SWT.BORDER);
		if (repository != null) {
			String base = repository.getProperty(OSLC_BASEURL);
			if (base != null) {
				baseText.setText(base);
			}
		}
		baseText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				serverUrlCombo.setText(baseText.getText());
				if (getWizard() != null) {
					getWizard().getContainer().updateButtons();
				}
			}
		});

		GridDataFactory.fillDefaults().hint(300, SWT.DEFAULT).span(2, 1).grab(true, false).applyTo(baseText);

		super.createSettingControls(parent);
		if (serverUrlCombo.getText().length() == 0) {
			serverUrlCombo.setText(Messages.OslcRepositorySettingsPage_Enter_Base_Url_Above);
		}
		serverUrlCombo.setEnabled(false);
	}

	@Override
	public abstract String getConnectorKind();

	@Override
	protected Validator getValidator(TaskRepository repository) {
		return new OslcValidator(createTaskRepository(), baseText.getText());
	}

	@Override
	protected boolean isValidUrl(String url) {
		if (url.startsWith(URL_PREFIX_HTTPS) || url.startsWith(URL_PREFIX_HTTP)) {
			try {
				new URL(url);
				return true;
			} catch (MalformedURLException e) {
			}
		}
		return false;
	}

	@Override
	public void applyTo(TaskRepository repository) {
		repository.setProperty(OSLC_BASEURL, baseText.getText());
		super.applyTo(repository);
	};

	@Override
	protected void applyValidatorResult(Validator validator) {
		OslcValidator cqValidator = (OslcValidator) validator;

		if (!cqValidator.getProviders().isEmpty()) {
			if (repository == null) {
				repository = createTaskRepository();
			}
			OslcServiceDiscoveryWizard oslcWizard = new OslcServiceDiscoveryWizard(((IOslcConnector) connector),
					cqValidator.getRepository(), cqValidator.getProviders());
			OslcServiceDiscoveryWizardDialog dialog = new OslcServiceDiscoveryWizardDialog(getShell(), oslcWizard);
			dialog.setBlockOnOpen(true);
			dialog.create();
			int result = dialog.open();

			if (result == Window.OK && oslcWizard.getSelectedServiceDescriptor() != null) {
				setUrl(oslcWizard.getSelectedServiceDescriptor().getAboutUrl());
				setServiceDescriptor(oslcWizard.getSelectedServiceDescriptor());
			} else {
				cqValidator.setStatus(Status.CANCEL_STATUS);
			}
		}

		super.applyValidatorResult(validator);
	}

	private class OslcValidator extends Validator {

		final TaskRepository repository;

		private List<OslcServiceProvider> providers = new ArrayList<OslcServiceProvider>();

		private final String baseUrl;

		public TaskRepository getRepository() {
			return this.repository;
		}

		public OslcValidator(TaskRepository repository, String baseUrl) {
			this.repository = repository;
			this.baseUrl = baseUrl;
		}

		private String getBaseUrl() {
			return baseUrl;
		}

		@Override
		public void run(IProgressMonitor monitor) throws CoreException {
			try {
				new URL(getBaseUrl());

				// TODO: if only one ServiceProviderCatalog/ServiceProvider found, use it
				List<OslcServiceProvider> serviceProviders = ((IOslcConnector) connector).getAvailableServices(
						repository, getBaseUrl(), monitor);
				setProviders(serviceProviders);
			} catch (MalformedURLException ex) {
				throw new CoreException(new Status(IStatus.ERROR, IOslcCoreConstants.ID_PLUGIN, IStatus.OK,
						INVALID_REPOSITORY_URL, null));
			} catch (CoreException e) {
				StatusHandler.log(new Status(IStatus.ERROR, IOslcCoreConstants.ID_PLUGIN,
						"Error occurred during service discovery", e)); //$NON-NLS-1$
			}

		}

		private void setProviders(List<OslcServiceProvider> providers) {
			this.providers = providers;
		}

		public List<OslcServiceProvider> getProviders() {
			return providers;
		}

	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		// ignore
	}

}
