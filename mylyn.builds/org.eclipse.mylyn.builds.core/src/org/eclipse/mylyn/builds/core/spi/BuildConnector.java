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

package org.eclipse.mylyn.builds.core.spi;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;

/**
 * Provides entry points to access functionality to connect to a particular type of build system.
 * <p>
 * Sub-classes may override any non-final method.
 *
 * @author Steffen Pingel
 */
public abstract class BuildConnector {

	private String connectorKind;

	private String label;

	/**
	 * Returns a class that describes the behavior for location.
	 *
	 * @param location
	 *            the location of the build server; guaranteed to have a connector kind that matches the kind of this connector
	 * @return an instance that implements the behavior for location
	 * @throws CoreException
	 *             thrown in case of an error
	 */
	public abstract BuildServerBehaviour getBehaviour(RepositoryLocation location) throws CoreException;

	/**
	 * Returns an element that is described by <code>url</code> or <code>null</code> if url is not associated with <code>server</code>.
	 *
	 * @param server
	 *            the build server; guaranteed to have a connector kind that matches the kind of this connector
	 * @param url
	 *            a url
	 * @return null, if <code>url</code> does not represent a build element of <code>server</code>
	 */
	public IBuildElement getBuildElementFromUrl(IBuildServer server, String url) {
		return null;
	}

	/**
	 * Returns a unique identifier for the type of build system handled by this connector.
	 *
	 * @return the connector kind
	 */
	public final String getConnectorKind() {
		return connectorKind;
	}

	/**
	 * Returns a label for the type of build system handled by this connector.
	 */
	public final String getLabel() {
		return label;
	}

	/**
	 * Initializes the connector instance. This method is only intended to be invoked by the Builds framework.
	 *
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public final void init(String connectorKind, String label) {
		Assert.isNotNull(connectorKind);
		Assert.isNotNull(label);
		if (this.connectorKind != null) {
			throw new IllegalStateException("Already initialized"); //$NON-NLS-1$
		}
		this.connectorKind = connectorKind;
		this.label = label;
	}

}
