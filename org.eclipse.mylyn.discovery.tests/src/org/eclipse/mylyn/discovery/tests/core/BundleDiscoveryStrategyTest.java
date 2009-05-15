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

package org.eclipse.mylyn.discovery.tests.core;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.discovery.tests.core.mock.MockBundleDiscoveryStrategy;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryCategory;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryConnector;
import org.eclipse.mylyn.internal.discovery.core.model.Policy;

/**
 * @author David Green
 */
public class BundleDiscoveryStrategyTest extends TestCase {

	private MockBundleDiscoveryStrategy discoveryStrategy;

	private final List<DiscoveryCategory> categories = new ArrayList<DiscoveryCategory>();

	private final List<DiscoveryConnector> connectors = new ArrayList<DiscoveryConnector>();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		discoveryStrategy = new MockBundleDiscoveryStrategy();
		discoveryStrategy.setPolicy(new Policy(true));
		discoveryStrategy.setCategories(categories);
		discoveryStrategy.setConnectors(connectors);
	}

	public void testDiscovery() throws CoreException {
		discoveryStrategy.performDiscovery(new NullProgressMonitor());

		assertFalse(categories.isEmpty());
		assertFalse(connectors.isEmpty());
		DiscoveryCategory category = findCategoryById("org.eclipse.mylyn.discovery.tests.connectorCategory1");
		assertNotNull(category);
		DiscoveryConnector connector = findConnectorById("org.eclipse.mylyn.discovery.tests.connectorDescriptor1");
		assertNotNull(connector);
	}

	public void testDiscoveryNoCategoriesPolicy() throws CoreException {
		discoveryStrategy.setPolicy(new Policy(false));
		discoveryStrategy.performDiscovery(new NullProgressMonitor());

		assertTrue(categories.isEmpty());
	}

	private DiscoveryConnector findConnectorById(String id) {
		for (DiscoveryConnector descriptor : connectors) {
			if (id.equals(descriptor.getId())) {
				return descriptor;
			}
		}
		return null;
	}

	private DiscoveryCategory findCategoryById(String id) {
		for (DiscoveryCategory descriptor : categories) {
			if (id.equals(descriptor.getId())) {
				return descriptor;
			}
		}
		return null;
	}
}
