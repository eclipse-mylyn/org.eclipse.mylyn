/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.discovery.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author David Green
 */
public class DiscoveryCategory extends ConnectorCategory {

	private AbstractDiscoverySource source;

	private final List<DiscoveryConnector> connectors = new ArrayList<DiscoveryConnector>();

	public List<DiscoveryConnector> getConnectors() {
		return connectors;
	}

	public AbstractDiscoverySource getSource() {
		return source;
	}

	public void setSource(AbstractDiscoverySource source) {
		this.source = source;
	}

}
