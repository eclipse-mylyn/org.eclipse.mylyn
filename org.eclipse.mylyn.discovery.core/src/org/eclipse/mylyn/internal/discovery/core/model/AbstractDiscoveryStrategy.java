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

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * An abstraction of a strategy for discovering connectors and categories.
 * Strategy design pattern.
 * 
 * @author David Green
 */
public abstract class AbstractDiscoveryStrategy {

	protected List<DiscoveryCategory> categories;
	protected List<DiscoveryConnector> connectors;

	/**
	 * Perform discovery and add discovered items to {@link #getCategories()
	 * categories} and {@link #getConnectors()}.
	 * 
	 * @param monitor
	 *            the monitor
	 */
	public abstract void performDiscovery(IProgressMonitor monitor)
			throws CoreException;

	public List<DiscoveryCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<DiscoveryCategory> categories) {
		this.categories = categories;
	}

	public List<DiscoveryConnector> getConnectors() {
		return connectors;
	}

	public void setConnectors(List<DiscoveryConnector> connectors) {
		this.connectors = connectors;
	}
}
