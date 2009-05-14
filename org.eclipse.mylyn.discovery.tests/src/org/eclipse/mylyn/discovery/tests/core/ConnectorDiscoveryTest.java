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

import java.util.Dictionary;
import java.util.Properties;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.discovery.tests.core.mock.DiscoveryConnectorMockFactory;
import org.eclipse.mylyn.discovery.tests.core.mock.MockDiscoveryStrategy;
import org.eclipse.mylyn.internal.discovery.core.model.ConnectorDiscovery;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryConnector;

/**
 * 
 * @author David Green
 */
@SuppressWarnings("restriction")
public class ConnectorDiscoveryTest extends TestCase {

	private ConnectorDiscovery connectorDiscovery;

	private MockDiscoveryStrategy mockDiscoveryStrategy;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		connectorDiscovery = new ConnectorDiscovery();
		mockDiscoveryStrategy = new MockDiscoveryStrategy();
		connectorDiscovery.getDiscoveryStrategies().add(mockDiscoveryStrategy);
	}

	public void testPlatformFilter_None() throws CoreException {
		connectorDiscovery.performDiscovery(new NullProgressMonitor());
		assertEquals(mockDiscoveryStrategy.getConnectorCount(), connectorDiscovery.getConnectors().size());
	}

	public void testPlatformFilter_NegativeMatch() throws CoreException {
		mockDiscoveryStrategy.setConnectorMockFactory(new DiscoveryConnectorMockFactory() {
			@Override
			protected void populateMockData() {
				super.populateMockData();
				platformFilter("(& (osgi.os=macosx) (osgi.ws=carbon))");
			}
		});
		// test to ensure that all non-matching platform filters are not discovered
		Dictionary<Object, Object> environment = new Properties();
		environment.put("osgi.os", "win32");
		environment.put("osgi.ws", "windows");
		connectorDiscovery.setEnvironment(environment);
		connectorDiscovery.performDiscovery(new NullProgressMonitor());

		assertTrue(connectorDiscovery.getConnectors().isEmpty());
	}

	public void testPlatformFilter_PositiveMatch() throws CoreException {
		mockDiscoveryStrategy.setConnectorMockFactory(new DiscoveryConnectorMockFactory() {
			@Override
			protected void populateMockData() {
				super.populateMockData();
				platformFilter("(& (osgi.os=macosx) (osgi.ws=carbon))");
			}
		});
		Dictionary<Object, Object> environment = new Properties();

		// test to ensure that all matching platform filters are discovered
		environment.put("osgi.os", "macosx");
		environment.put("osgi.ws", "carbon");
		connectorDiscovery.setEnvironment(environment);
		connectorDiscovery.performDiscovery(new NullProgressMonitor());

		assertFalse(connectorDiscovery.getConnectors().isEmpty());
		assertEquals(mockDiscoveryStrategy.getConnectorCount(), connectorDiscovery.getConnectors().size());
	}

	public void testCategorization() throws CoreException {
		connectorDiscovery.performDiscovery(new NullProgressMonitor());
		assertTrue(!connectorDiscovery.getConnectors().isEmpty());
		assertTrue(!connectorDiscovery.getCategories().isEmpty());

		for (DiscoveryConnector connector : connectorDiscovery.getConnectors()) {
			assertNotNull(connector.getCategory());
			assertEquals(connector.getCategoryId(), connector.getCategory().getId());
			assertTrue(connector.getCategory().getConnectors().contains(connector));
		}
	}

	public void testMultipleStrategies() throws CoreException {
		MockDiscoveryStrategy strategy = new MockDiscoveryStrategy();
		strategy.setConnectorMockFactory(mockDiscoveryStrategy.getConnectorMockFactory());
		strategy.setCategoryMockFactory(mockDiscoveryStrategy.getCategoryMockFactory());
		connectorDiscovery.getDiscoveryStrategies().add(strategy);

		connectorDiscovery.performDiscovery(new NullProgressMonitor());

		assertEquals(mockDiscoveryStrategy.getConnectorMockFactory().getCreatedCount(),
				connectorDiscovery.getConnectors().size());
		assertEquals(mockDiscoveryStrategy.getCategoryMockFactory().getCreatedCount(),
				connectorDiscovery.getCategories().size());
	}
}
