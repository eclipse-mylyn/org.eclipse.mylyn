/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;

/**
 * @author Steffen Pingel
 */
public class BuildConnectorDelegate extends BuildConnector {

	private IStatus status;

	private BuildConnector core;

	private final BuildConnectorDescriptor descriptor;

	public BuildConnectorDelegate(BuildConnectorDescriptor descriptor) {
		this.descriptor = descriptor;
		init(descriptor.connectorKind, descriptor.label);
	}

	public synchronized BuildConnector getCore() throws CoreException {
		if (core != null) {
			return core;
		}
		if (status != null) {
			throw new CoreException(status);
		}
		IStatus result = descriptor.createCore();
		if (result.isOK()) {
			core = descriptor.core;
		} else {
			status = result;
		}
		if (status != null) {
			throw new CoreException(status);
		}
		return core;
	}

	@Override
	public BuildServerBehaviour getBehaviour(RepositoryLocation location) throws CoreException {
		return getCore().getBehaviour(location);
	}

	@Override
	public IBuildElement getBuildElementFromUrl(IBuildServer server, String url) {
		try {
			return getCore().getBuildElementFromUrl(server, url);
		} catch (CoreException e) {
			// ignore
			return null;
		}
	}

}
