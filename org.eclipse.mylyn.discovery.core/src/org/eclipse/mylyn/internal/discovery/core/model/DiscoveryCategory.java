/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.discovery.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author David Green
 */
public class DiscoveryCategory extends ConnectorCategory {
	private AbstractDiscoverySource source;
	private List<DiscoveryConnector> connectors = new ArrayList<DiscoveryConnector>();

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
