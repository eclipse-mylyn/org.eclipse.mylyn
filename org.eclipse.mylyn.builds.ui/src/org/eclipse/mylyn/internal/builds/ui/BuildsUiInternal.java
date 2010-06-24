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

package org.eclipse.mylyn.internal.builds.ui;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.internal.builds.core.BuildModel;
import org.eclipse.mylyn.internal.builds.core.BuildServer;
import org.eclipse.mylyn.internal.builds.core.tasks.IBuildLoader;
import org.eclipse.mylyn.internal.builds.core.util.BuildModelManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class BuildsUiInternal {

	private static IBuildLoader buildLoader = new IBuildLoader() {
		public BuildServerBehaviour loadBehaviour(BuildServer server) throws CoreException {
			String connectorKind = server.getConnectorKind();
			if (connectorKind == null) {
				throw new CoreException(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, NLS.bind(
						"Loading of connector for server ''{0}'' failed. No connector kind was specified.",
						server.getName())));
			}
			BuildConnector connector = BuildsUi.getConnector(connectorKind);
			if (connector == null) {
				throw new CoreException(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, NLS.bind(
						"Loading of connector for server ''{0}'' failed. Connector kind ''{1}'' is not known.",
						server.getName(), connectorKind)));
			}
			BuildServerBehaviour behaviour;
			try {
				behaviour = connector.getBehaviour(server);
			} catch (Exception e) {
				throw new CoreException(
						new Status(
								IStatus.ERROR,
								BuildsUiPlugin.ID_PLUGIN,
								NLS.bind(
										"Unexpected error during loading of connector for server ''{0}'' with connector kind ''{1}'' failed.",
										server.getName(), connectorKind), e));
			} catch (LinkageError e) {
				throw new CoreException(
						new Status(
								IStatus.ERROR,
								BuildsUiPlugin.ID_PLUGIN,
								NLS.bind(
										"Unexpected error during loading of connector for server ''{0}'' with connector kind ''{1}'' failed.",
										server.getName(), connectorKind), e));
			} catch (AssertionError e) {
				throw new CoreException(
						new Status(
								IStatus.ERROR,
								BuildsUiPlugin.ID_PLUGIN,
								NLS.bind(
										"Unexpected error during loading of connector for server ''{0}'' with connector kind ''{1}'' failed.",
										server.getName(), connectorKind), e));

			}
			if (behaviour == null) {
				throw new CoreException(
						new Status(
								IStatus.ERROR,
								BuildsUiPlugin.ID_PLUGIN,
								NLS.bind(
										"Unexpected error during loading of connector for server ''{0}'' with connector kind ''{1}'' failed, returned behaviour object is null.",
										server.getName(), connectorKind)));
			}
			return behaviour;
		}
	};

	private static BuildModelManager manager;

	public static synchronized BuildModel getModel() {
		return getManager().getModel();
	}

	protected static synchronized BuildModelManager getManager() {
		if (manager == null) {
			manager = new BuildModelManager(BuildsUiPlugin.getDefault().getBuildsFile().toFile(), buildLoader);
			manager.setRepositoryManager(TasksUi.getRepositoryManager());
		}
		return manager;
	}

	public static synchronized void save() throws IOException {
		if (manager != null) {
			manager.save();
		}
	}

	public static IBuildServer createServer(TaskRepository repository) {
		return getManager().createServer(repository);
	}

	public static IBuildServer getServer(TaskRepository repository) {
		for (IBuildServer server : BuildsUi.getModel().getServers()) {
			if (repository.equals(server.getRepository())) {
				return server;
			}
		}
		return null;
	}

}
