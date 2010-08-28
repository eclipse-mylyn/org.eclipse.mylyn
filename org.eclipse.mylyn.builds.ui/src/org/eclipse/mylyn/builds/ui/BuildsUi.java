/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.ui;

import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.builds.ui.spi.BuildConnectorUi;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.internal.builds.ui.BuildConnectorDescriptor;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Steffen Pingel
 */
public class BuildsUi {

	private static HashMap<String, BuildConnectorDescriptor> desctiptorByKind;

	public synchronized static BuildConnector getConnector(String connectorKind) {
		BuildConnectorDescriptor descriptor = getConnectorDescriptorByKind().get(connectorKind);
		return (descriptor != null) ? descriptor.getCoreDelegate() : null;
	}

	public synchronized static BuildConnectorUi getConnectorUi(String connectorKind) {
		BuildConnectorDescriptor descriptor = getConnectorDescriptorByKind().get(connectorKind);
		return (descriptor != null) ? descriptor.getUiDelegate() : null;
	}

	public synchronized static IBuildModel getModel() {
		return BuildsUiInternal.getModel();
	}

	private static HashMap<String, BuildConnectorDescriptor> getConnectorDescriptorByKind() {
		if (desctiptorByKind != null) {
			return desctiptorByKind;
		}

		desctiptorByKind = new HashMap<String, BuildConnectorDescriptor>();

		MultiStatus result = new MultiStatus(BuildsUiPlugin.ID_PLUGIN, 0, "Build connectors failed to load.", null); //$NON-NLS-1$

		// read core and migrator extensions to check for id conflicts
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint repositoriesExtensionPoint = registry.getExtensionPoint(BuildsUiPlugin.ID_PLUGIN
				+ ".connectors");
		IExtension[] repositoryExtensions = repositoriesExtensionPoint.getExtensions();
		for (IExtension repositoryExtension : repositoryExtensions) {
			IConfigurationElement[] elements = repositoryExtension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				BuildConnectorDescriptor descriptor = new BuildConnectorDescriptor(element);
				IStatus status = descriptor.validate();
				if (status.isOK()) {
					desctiptorByKind.put(descriptor.getConnectorKind(), descriptor);
				} else {
					result.add(status);
				}
			}
		}

		if (!result.isOK()) {
			StatusManager.getManager().handle(result);
		}

		return desctiptorByKind;
	}

	public static IBuildServer createServer(String connectorKind) {
		return BuildsUiInternal.createServer(connectorKind, new RepositoryLocation());
	}

	public static BuildConnector getConnector(IBuildServer server) {
		return getConnector(((BuildServer) server).getConnectorKind());
	}

	public static BuildConnectorUi getConnectorUi(IBuildServer server) {
		return getConnectorUi(((BuildServer) server).getConnectorKind());
	}

}
