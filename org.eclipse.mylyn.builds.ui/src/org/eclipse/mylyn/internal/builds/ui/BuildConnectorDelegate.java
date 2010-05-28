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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class BuildConnectorDelegate extends BuildConnector {

	private final IConfigurationElement element;

	private final String connectorKind;

	private IStatus status;

	private BuildConnector core;

	public BuildConnectorDelegate(IConfigurationElement element) {
		this.element = element;
		this.connectorKind = element.getAttribute("kind");
	}

	public String getConnectorKind() {
		return connectorKind;
	}

	public IStatus createConnector() {
		try {
			Object object = element.createExecutableExtension("core"); //$NON-NLS-1$
			if (object instanceof BuildConnector) {
				core = (BuildConnector) object;
				return Status.OK_STATUS;
			} else {
				return new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, NLS.bind(
						"Connector core ''{0}'' does not extens expected class for extension contributed by {1}", //$NON-NLS-1$
						object.getClass().getCanonicalName(), getPluginId()));
			}
		} catch (Throwable e) {
			return new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, NLS.bind(
					"Connector core failed to load for extension contributed by {0}", getPluginId()), e); //$NON-NLS-1$
		}
	}

	public String getPluginId() {
		return element.getContributor().getName();
	}

	public synchronized BuildConnector getCore() throws CoreException {
		if (core != null) {
			return core;
		}
		if (status != null) {
			throw new CoreException(status);
		}
		createConnector();
		if (status != null) {
			throw new CoreException(status);
		}
		return core;
	}

	@Override
	public BuildServerBehaviour getBehaviour(IBuildServer server) throws CoreException {
		return getCore().getBehaviour(server);
	}

	public IStatus validateExtension() {
		if (getConnectorKind() == null) {
			return new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, NLS.bind(
					"Connector core extension contributed by {0} does not specify kind attribute", getPluginId())); //$NON-NLS-1$
		} else if (element.getAttribute("core") == null) {
			return new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, NLS.bind(
					"Connector core extension contributed by {0} does not specify core attribute", getPluginId())); //$NON-NLS-1$
		}
		return Status.OK_STATUS;
	}

}
