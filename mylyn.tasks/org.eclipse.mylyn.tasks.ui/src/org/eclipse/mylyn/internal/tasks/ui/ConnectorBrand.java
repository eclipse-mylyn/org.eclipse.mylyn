/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Objects;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.osgi.util.NLS;

public class ConnectorBrand {
	private final AbstractRepositoryConnector connector;

	private final String brandId;

	public ConnectorBrand(AbstractRepositoryConnector connector, String brandId) {
		this.connector = connector;
		this.brandId = brandId;
	}

	public AbstractRepositoryConnector getConnector() {
		return connector;
	}

	public String getBrandId() {
		return brandId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(brandId, connector);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || !(obj instanceof ConnectorBrand other)) {
			return false;
		}
		if (!Objects.equals(brandId, other.brandId)) {
			return false;
		}
		if (!Objects.equals(connector, other.connector)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return NLS.bind("<{0},{1}>", connector.getConnectorKind(), brandId); //$NON-NLS-1$
	}
}
