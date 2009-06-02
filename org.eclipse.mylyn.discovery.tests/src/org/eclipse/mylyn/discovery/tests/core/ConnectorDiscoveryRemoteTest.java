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

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.discovery.tests.DiscoveryTests;
import org.eclipse.mylyn.internal.discovery.core.model.ConnectorDiscovery;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryConnector;
import org.eclipse.mylyn.internal.discovery.core.model.RemoteBundleDiscoveryStrategy;

/**
 * A test that uses the real discovery directory and verifies that it works, and that all referenced update sites appear
 * to be available.
 * 
 * @author David Green
 */
@SuppressWarnings("restriction")
public class ConnectorDiscoveryRemoteTest extends TestCase {

	private ConnectorDiscovery connectorDiscovery;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		connectorDiscovery = new ConnectorDiscovery();
		connectorDiscovery.setVerifyUpdateSiteAvailability(false);

		connectorDiscovery.getDiscoveryStrategies().clear();
		RemoteBundleDiscoveryStrategy remoteStrategy = new RemoteBundleDiscoveryStrategy();
		remoteStrategy.setDirectoryUrl(DiscoveryTests.DEFAULT_MYLYN_DISCOVERY_URL);
		connectorDiscovery.getDiscoveryStrategies().add(remoteStrategy);
	}

	public void testRemoteDirectory() throws CoreException {
		connectorDiscovery.performDiscovery(new NullProgressMonitor());

		assertFalse(connectorDiscovery.getCategories().isEmpty());
		assertFalse(connectorDiscovery.getConnectors().isEmpty());
	}

	public void testVerifyAvailability() throws CoreException {
		connectorDiscovery.performDiscovery(new NullProgressMonitor());
		for (DiscoveryConnector connector : connectorDiscovery.getConnectors()) {
			assertNull(connector.getAvailable());
		}
		connectorDiscovery.verifySiteAvailability(new NullProgressMonitor());

		assertFalse(connectorDiscovery.getConnectors().isEmpty());

		int unavailableCount = 0;
		for (DiscoveryConnector connector : connectorDiscovery.getConnectors()) {
			assertNotNull(connector.getAvailable());
			if (!connector.getAvailable()) {
				++unavailableCount;
			}
		}
		if (unavailableCount > 0) {
			fail(String.format("%s unavailable: %s", unavailableCount, computeUnavailableConnetorDescriptorNames()));
		}
	}

	private String computeUnavailableConnetorDescriptorNames() {
		String message = "";
		for (DiscoveryConnector connector : connectorDiscovery.getConnectors()) {
			if (!connector.getAvailable()) {
				if (message.length() > 0) {
					message += ", ";
				}
				message += connector.getName();
			}
		}
		return message;
	}
}
