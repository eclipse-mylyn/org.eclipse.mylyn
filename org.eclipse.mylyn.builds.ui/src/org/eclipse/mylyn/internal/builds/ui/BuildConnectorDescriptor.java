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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.osgi.util.NLS;

public class BuildConnectorDescriptor {

	private final IConfigurationElement element;

	final String connectorKind;

	private IStatus status;

	private BuildConnector core;

	final String label;

	public BuildConnectorDescriptor(IConfigurationElement element) {
		this.element = element;
		this.connectorKind = element.getAttribute("kind");
		this.label = element.getAttribute("label");
	}

	public IStatus createConnector() {
		try {
			Object object = element.createExecutableExtension("core"); //$NON-NLS-1$
			if (object instanceof BuildConnector) {
				core = (BuildConnector) object;
				core.init(connectorKind, label);
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

	public IStatus validate() {
		if (connectorKind == null) {
			return new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, NLS.bind(
					"Connector core extension contributed by {0} does not specify kind attribute", getPluginId())); //$NON-NLS-1$
		} else if (label == null) {
			return new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, NLS.bind(
					"Connector core extension contributed by {0} does not specify label attribute", getPluginId())); //$NON-NLS-1$
		} else if (element.getAttribute("core") == null) {
			return new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, NLS.bind(
					"Connector core extension contributed by {0} does not specify core attribute", getPluginId())); //$NON-NLS-1$
		}
		return Status.OK_STATUS;
	}

}
