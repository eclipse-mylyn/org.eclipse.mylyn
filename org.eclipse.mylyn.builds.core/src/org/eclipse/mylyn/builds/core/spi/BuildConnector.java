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

package org.eclipse.mylyn.builds.core.spi;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;

/**
 * @author Steffen Pingel
 */
public abstract class BuildConnector {

	private String connectorKind;

	private String label;

	public abstract BuildServerBehaviour getBehaviour(RepositoryLocation location) throws CoreException;

	public final String getConnectorKind() {
		return connectorKind;
	}

	public final String getLabel() {
		return label;
	}

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
