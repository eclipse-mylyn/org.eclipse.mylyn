/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.discovery.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.discovery.core.model.ConnectorDescriptorKind;
import org.eclipse.mylyn.internal.discovery.core.model.ConnectorDiscovery;
import org.eclipse.mylyn.internal.discovery.ui.DiscoveryUi;
import org.eclipse.mylyn.internal.discovery.ui.util.DiscoveryUiUtil;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * A wizard for performing discovery of connectors and selecting connectors to install. When finish is pressed, selected
 * connectors are downloaded and installed.
 * 
 * @see InstallConnectorsJob
 * @see ConnectorDiscoveryWizardMainPage
 * @author David Green
 */
public class ConnectorDiscoveryWizard extends Wizard {

	private ConnectorDiscoveryWizardMainPage mainPage;

	private final Map<ConnectorDescriptorKind, Boolean> connectorDescriptorKindToVisibility = new HashMap<ConnectorDescriptorKind, Boolean>();
	{
		for (ConnectorDescriptorKind kind : ConnectorDescriptorKind.values()) {
			connectorDescriptorKindToVisibility.put(kind, true);
		}
	}

	private boolean showConnectorDescriptorKindFilter = true;

	private boolean showConnectorDescriptorTextFilter = true;

	private Dictionary<Object, Object> environment;

	public ConnectorDiscoveryWizard() {
		setWindowTitle(Messages.ConnectorDiscoveryWizard_connectorDiscovery);
		setNeedsProgressMonitor(true);
		createEnvironment();
	}

	private void createEnvironment() {
		environment = new Hashtable<Object, Object>(System.getProperties());
		// add the installed Mylyn version to the environment so that we can have
		// connectors that are filtered based on version of Mylyn
		Bundle bundle = Platform.getBundle("org.eclipse.mylyn.tasks.core"); //$NON-NLS-1$
		Version version = bundle.getVersion();
		environment.put("org.eclipse.mylyn.version", version.toString()); //$NON-NLS-1$
		environment.put("org.eclipse.mylyn.version.major", version.getMajor()); //$NON-NLS-1$
		environment.put("org.eclipse.mylyn.version.minor", version.getMinor()); //$NON-NLS-1$
		environment.put("org.eclipse.mylyn.version.micro", version.getMicro()); //$NON-NLS-1$
	}

	@Override
	public void addPages() {
		addPage(mainPage = new ConnectorDiscoveryWizardMainPage());
	}

	@Override
	public boolean performFinish() {
		try {
			IRunnableWithProgress job = new InstallConnectorsJob(mainPage.getInstallableConnectors());
			getContainer().run(true, true, job);
		} catch (InvocationTargetException e) {
			IStatus status = new Status(IStatus.ERROR, DiscoveryUi.ID_PLUGIN, NLS.bind(
					Messages.ConnectorDiscoveryWizard_installProblems, new Object[] { e.getCause().getMessage() }),
					e.getCause());
			DiscoveryUiUtil.logAndDisplayStatus(Messages.ConnectorDiscoveryWizard_cannotInstall, status);
		} catch (InterruptedException e) {
			// canceled
		}
		return true;
	}

	/**
	 * configure the page to show or hide connector descriptors of the given kind
	 * 
	 * @see #connectorDescriptorKindVisibilityUpdated()
	 */
	public void setVisibility(ConnectorDescriptorKind kind, boolean visible) {
		if (kind == null) {
			throw new IllegalArgumentException();
		}
		connectorDescriptorKindToVisibility.put(kind, visible);
	}

	/**
	 * indicate if the given kind of connector is currently visible in the wizard
	 * 
	 * @see #setVisibility(ConnectorDescriptorKind, boolean)
	 */
	public boolean isVisible(ConnectorDescriptorKind kind) {
		if (kind == null) {
			throw new IllegalArgumentException();
		}
		return connectorDescriptorKindToVisibility.get(kind);
	}

	/**
	 * indicate if the connector descriptor filters should be shown in the UI. Changing this setting only has an effect
	 * before the UI is presented.
	 */
	public boolean isShowConnectorDescriptorKindFilter() {
		return showConnectorDescriptorKindFilter;
	}

	/**
	 * indicate if the connector descriptor filters should be shown in the UI. Changing this setting only has an effect
	 * before the UI is presented.
	 */
	public void setShowConnectorDescriptorKindFilter(boolean showConnectorDescriptorKindFilter) {
		this.showConnectorDescriptorKindFilter = showConnectorDescriptorKindFilter;
	}

	/**
	 * indicate if a text field should be provided to allow the user to filter connector descriptors
	 */
	public boolean isShowConnectorDescriptorTextFilter() {
		return showConnectorDescriptorTextFilter;
	}

	/**
	 * indicate if a text field should be provided to allow the user to filter connector descriptors
	 */
	public void setShowConnectorDescriptorTextFilter(boolean showConnectorDescriptorTextFilter) {
		this.showConnectorDescriptorTextFilter = showConnectorDescriptorTextFilter;
	}

	/**
	 * the environment in which discovery should be performed.
	 * 
	 * @see ConnectorDiscovery#getEnvironment()
	 */
	public Dictionary<Object, Object> getEnvironment() {
		return environment;
	}

	/**
	 * the environment in which discovery should be performed.
	 * 
	 * @see ConnectorDiscovery#getEnvironment()
	 */
	public void setEnvironment(Dictionary<Object, Object> environment) {
		if (environment == null) {
			throw new IllegalArgumentException();
		}
		this.environment = environment;
	}

}
