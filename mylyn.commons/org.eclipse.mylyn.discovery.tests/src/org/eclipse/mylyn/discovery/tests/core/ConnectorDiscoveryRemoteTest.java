/*******************************************************************************
 * Copyright (c) 2009, 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.discovery.tests.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.discovery.core.DiscoveryCore;
import org.eclipse.mylyn.internal.discovery.core.model.ConnectorDiscovery;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryConnector;
import org.eclipse.mylyn.internal.discovery.core.model.RemoteBundleDiscoveryStrategy;

import junit.framework.TestCase;

/**
 * A test that uses the real discovery directory and verifies that it works, and that all referenced update sites appear
 * to be available.
 *
 * @author David Green
 */
public class ConnectorDiscoveryRemoteTest extends TestCase {

	private ConnectorDiscovery connectorDiscovery;

	@SuppressWarnings("restriction")
	@Override
	protected void setUp() throws Exception {
		if (CommonTestUtil.fixProxyConfiguration()) {
			CommonTestUtil.dumpSystemInfo(System.err);
		}
		connectorDiscovery = new ConnectorDiscovery();
		connectorDiscovery.setVerifyUpdateSiteAvailability(false);

		connectorDiscovery.getDiscoveryStrategies().clear();
		RemoteBundleDiscoveryStrategy remoteStrategy = new RemoteBundleDiscoveryStrategy();
		remoteStrategy.setDirectoryUrl(DiscoveryCore.getDiscoveryUrl());
		connectorDiscovery.getDiscoveryStrategies().add(remoteStrategy);
	}

	public void testRemoteDirectory() throws CoreException {
		connectorDiscovery.performDiscovery(new NullProgressMonitor());
//FIXME: AF: see https://github.com/eclipse-mylyn/org.eclipse.mylyn/issues/196
//		assertFalse(connectorDiscovery.getCategories().isEmpty());
//		assertFalse(connectorDiscovery.getConnectors().isEmpty());
	}

	public void testVerifyAvailability() throws Exception {
		connectorDiscovery.performDiscovery(new NullProgressMonitor());
		for (DiscoveryConnector connector : connectorDiscovery.getConnectors()) {
			assertNull(connector.getAvailable());
		}
		connectorDiscovery.verifySiteAvailability(new NullProgressMonitor());

//FIXME: AF: see https://github.com/eclipse-mylyn/org.eclipse.mylyn/issues/196
//		assertFalse(connectorDiscovery.getConnectors().isEmpty());

		int unavailableCount = 0;
		for (DiscoveryConnector connector : connectorDiscovery.getConnectors()) {
			if (connector.getAvailable() == null) {
				// connectors that can't be verified need to have a valid install message set
				assertNotNull("Failed to verify availability for " + connector.getId(),
						connector.getAttributes().get(DiscoveryConnector.ATTRIBUTE_INSTALL_MESSAGE));
//FIXME: AF: remove this condition, currently we ignore outdated content of remote discovery
//see https://github.com/eclipse-mylyn/org.eclipse.mylyn/issues/169
			} else if (!connector.getSiteUrl().startsWith("https://download.eclipse.org/mylyn/")) {
				continue;
			} else if (!connector.getAvailable()) {
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
			if (connector.getAvailable() != null && !connector.getAvailable()) {
				if (message.length() > 0) {
					message += ", ";
				}
				message += connector.getName();
			}
		}
		return message;
	}
}
