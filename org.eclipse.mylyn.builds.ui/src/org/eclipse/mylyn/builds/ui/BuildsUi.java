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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.internal.builds.core.BuildPackage;
import org.eclipse.mylyn.internal.builds.core.BuildServer;
import org.eclipse.mylyn.internal.builds.core.tasks.IBuildLoader;
import org.eclipse.mylyn.internal.builds.ui.BuildConnectorDelegate;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Steffen Pingel
 */
public class BuildsUi {

	private static IBuildLoader buildLoader = new IBuildLoader() {
		public BuildServerBehaviour loadBehaviour(BuildServer server) throws CoreException {
			return getBuildConnector(server.getConnectorKind()).getBehaviour(server);
		}
	};

	private static HashMap<String, BuildConnector> connectorById;

	public static IBuildServer createServer(TaskRepository repository) {
		BuildServer server = BuildPackage.eINSTANCE.getBuildFactory().createBuildServer();
		server.setRepository(repository);
		server.setConnectorKind("org.eclipse.mylyn.builds.tests.mock");
		server.setLoader(buildLoader);
		return server;
	}

	public synchronized static BuildConnector getBuildConnector(String connenctorKind) {
		if (connectorById == null) {
			initConnectorCores(Platform.getExtensionRegistry());
		}
		return connectorById.get(connenctorKind);
	}

	private static void initConnectorCores(IExtensionRegistry registry) {
		connectorById = new HashMap<String, BuildConnector>();

		MultiStatus result = new MultiStatus(TasksUiPlugin.ID_PLUGIN, 0, "Build connectors failed to load.", null); //$NON-NLS-1$

		// read core and migrator extensions to check for id conflicts
		IExtensionPoint repositoriesExtensionPoint = registry.getExtensionPoint(BuildsUiPlugin.ID_PLUGIN
				+ ".connectors");
		IExtension[] repositoryExtensions = repositoriesExtensionPoint.getExtensions();
		for (IExtension repositoryExtension : repositoryExtensions) {
			IConfigurationElement[] elements = repositoryExtension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				BuildConnectorDelegate delegate = new BuildConnectorDelegate(element);
				IStatus status = delegate.validateExtension();
				if (status.isOK()) {
					connectorById.put(delegate.getConnectorKind(), delegate);
				} else {
					result.add(status);
				}
			}
		}

		if (!result.isOK()) {
			StatusManager.getManager().handle(result);
		}

	}

}
