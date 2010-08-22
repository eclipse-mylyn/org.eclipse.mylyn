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
import org.eclipse.mylyn.builds.ui.spi.BuildConnectorUi;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class BuildConnectorDescriptor {

	final String connectorKind;

	BuildConnector core;

	private BuildConnectorDelegate coreDelegate;

	private final IConfigurationElement element;

	final String label;

	private IStatus status;

	BuildConnectorUi ui;

	private BuildConnectorUiDelegate uiDelegate;

	public BuildConnectorDescriptor(IConfigurationElement element) {
		this.element = element;
		this.connectorKind = element.getAttribute("kind");
		this.label = element.getAttribute("label");
	}

	public IStatus createCore() {
		try {
			Object object = element.createExecutableExtension("core"); //$NON-NLS-1$
			if (object instanceof BuildConnector) {
				core = (BuildConnector) object;
				core.init(connectorKind, label);
				return Status.OK_STATUS;
			} else {
				return new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, NLS.bind(
						"Connector core ''{0}'' does not extend expected class for extension contributed by {1}", //$NON-NLS-1$
						object.getClass().getCanonicalName(), getPluginId()));
			}
		} catch (Throwable e) {
			return new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, NLS.bind(
					"Connector core failed to load for extension contributed by {0}", getPluginId()), e); //$NON-NLS-1$
		}
	}

	public IStatus createUi() {
		if (core == null) {
			// FIXME set core on BuildConnectorDelegate
			IStatus result = createCore();
			if (!result.isOK()) {
				return result;
			}
		}

		try {
			Object object = element.createExecutableExtension("ui"); //$NON-NLS-1$
			if (object instanceof BuildConnectorUi) {
				ui = (BuildConnectorUi) object;
				ui.init(core, getElement());
				return Status.OK_STATUS;
			} else {
				return new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, NLS.bind(
						"Connector ui ''{0}'' does not extend expected class for extension contributed by {1}", //$NON-NLS-1$
						object.getClass().getCanonicalName(), getPluginId()));
			}
		} catch (Throwable e) {
			return new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, NLS.bind(
					"Connector ui failed to load for extension contributed by {0}", getPluginId()), e); //$NON-NLS-1$
		}
	}

	public String getConnectorKind() {
		return connectorKind;
	}

	public BuildConnectorDelegate getCoreDelegate() {
		if (coreDelegate == null) {
			coreDelegate = new BuildConnectorDelegate(this);
		}
		return coreDelegate;
	}

	public IConfigurationElement getElement() {
		return element;
	}

	public String getPluginId() {
		return element.getContributor().getName();
	}

	public BuildConnectorUiDelegate getUiDelegate() {
		if (uiDelegate == null) {
			uiDelegate = new BuildConnectorUiDelegate(this, getCoreDelegate());
		}
		return uiDelegate;
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
