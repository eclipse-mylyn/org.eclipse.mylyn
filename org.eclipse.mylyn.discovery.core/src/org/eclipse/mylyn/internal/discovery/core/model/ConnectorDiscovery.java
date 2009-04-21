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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.discovery.core.DiscoveryCore;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

/**
 * A means of discovering connectors.
 * 
 * @author David Green
 */
public class ConnectorDiscovery {

	private List<DiscoveryConnector> connectors = Collections.emptyList();
	private List<DiscoveryCategory> categories = Collections.emptyList();

	private List<AbstractDiscoveryStrategy> discoveryStrategies = new ArrayList<AbstractDiscoveryStrategy>();

	private Dictionary<Object, Object> environment = System.getProperties();

	public ConnectorDiscovery() {
	}

	/**
	 * get the discovery strategies to use.
	 */
	public List<AbstractDiscoveryStrategy> getDiscoveryStrategies() {
		return discoveryStrategies;
	}

	/**
	 * Initialize this by performing discovery. Discovery may take a long time
	 * as it involves network access. PRECONDITION: must add at least one
	 * {@link #getDiscoveryStrategies() discovery strategy} prior to calling.
	 * 
	 */
	public void performDiscovery(IProgressMonitor monitor) throws CoreException {
		if (discoveryStrategies.isEmpty()) {
			throw new IllegalStateException();
		}
		connectors = new ArrayList<DiscoveryConnector>();
		categories = new ArrayList<DiscoveryCategory>();

		final int totalTicks = 10000;
		monitor.beginTask("Discovering connectors", totalTicks);

		// FIXME: policy for where categories can be declared
		for (AbstractDiscoveryStrategy discoveryStrategy : discoveryStrategies) {
			discoveryStrategy.setCategories(categories);
			discoveryStrategy.setConnectors(connectors);
			discoveryStrategy.performDiscovery(new SubProgressMonitor(monitor,
					totalTicks / discoveryStrategies.size()));
		}

		filterDescriptors();
		connectCategoriesToDescriptors();

		monitor.done();
	}

	/**
	 * get the top-level categories
	 * 
	 * @return the categories, or an empty list if there are none.
	 */
	public List<DiscoveryCategory> getCategories() {
		return categories;
	}

	/**
	 * get the connectors
	 * 
	 * @return the connectors, or an empty list if there are none.
	 */
	public List<DiscoveryConnector> getConnectors() {
		return connectors;
	}

	/**
	 * The environment used to resolve
	 * {@link ConnectorDescriptor#getPlatformFilter() platform filters}.
	 * Defaults to the current environment.
	 */
	public Dictionary<Object, Object> getEnvironment() {
		return environment;
	}

	/**
	 * The environment used to resolve
	 * {@link ConnectorDescriptor#getPlatformFilter() platform filters}.
	 * Defaults to the current environment.
	 */
	public void setEnvironment(Dictionary<Object, Object> environment) {
		if (environment == null) {
			throw new IllegalArgumentException();
		}
		this.environment = environment;
	}

	private void connectCategoriesToDescriptors() {
		Map<String, DiscoveryCategory> idToCategory = new HashMap<String, DiscoveryCategory>();
		for (DiscoveryCategory category : categories) {
			DiscoveryCategory previous = idToCategory.put(category.getId(),
					category);
			if (previous != null) {
				StatusHandler.log(new Status(
								IStatus.ERROR,
								DiscoveryCore.BUNDLE_ID,
								MessageFormat.format(
												"Duplicate category id ''{0}'': declaring sources: {1}, {2}",
												category.getId(), category.getSource().getId(),
												previous.getSource().getId())));
			}
		}

		for (DiscoveryConnector connector : connectors) {
			DiscoveryCategory category = idToCategory.get(connector
					.getCategoryId());
			if (category != null) {
				category.getConnectors().add(connector);
				connector.setCategory(category);
			} else {
				StatusHandler
						.log(new Status(
								IStatus.ERROR,
								DiscoveryCore.BUNDLE_ID,
								MessageFormat.format(
												"Unknown category ''{0}'' referenced by connector ''{1}'' declared in {2}",
												connector.getCategoryId(),
												connector.getId(), connector.getSource().getId())));
			}
		}
	}

	/**
	 * eliminate any connectors whose
	 * {@link ConnectorDescriptor#getPlatformFilter() platform filters} don't
	 * match
	 */
	private void filterDescriptors() {
		for (DiscoveryConnector connector : new ArrayList<DiscoveryConnector>(
				connectors)) {
			if (connector.getPlatformFilter() != null
					&& connector.getPlatformFilter().trim().length() > 0) {
				boolean match = false;
				try {
					Filter filter = FrameworkUtil.createFilter(connector
							.getPlatformFilter());
					match = filter.match(environment);
				} catch (InvalidSyntaxException e) {
					StatusHandler
							.log(new Status(
									IStatus.ERROR,
									DiscoveryCore.BUNDLE_ID,
									MessageFormat.format(
													"Illegal filter syntax ''{0}'' in connector ''{1}'' declared in {2}",
													connector.getPlatformFilter(),
													connector.getId(),
													connector.getSource().getId())));
				}
				if (!match) {
					connectors.remove(connector);
				}
			}
		}
	}
}
