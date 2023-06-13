/*******************************************************************************
 * Copyright (c) 2009, 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.discovery.tests.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.discovery.core.DiscoveryCore;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryCategory;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryCertification;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryConnector;
import org.eclipse.mylyn.internal.discovery.core.model.RemoteBundleDiscoveryStrategy;

import junit.framework.TestCase;

/**
 * @author David Green
 */
public class RemoteBundleDiscoveryStrategyTest extends TestCase {

	private RemoteBundleDiscoveryStrategy discoveryStrategy;

	@Override
	protected void setUp() throws Exception {
		discoveryStrategy = new RemoteBundleDiscoveryStrategy();
		discoveryStrategy.setDirectoryUrl(DiscoveryCore.getDiscoveryUrl());
		discoveryStrategy.setCategories(new ArrayList<DiscoveryCategory>());
		discoveryStrategy.setConnectors(new ArrayList<DiscoveryConnector>());
		discoveryStrategy.setCertifications(new ArrayList<DiscoveryCertification>());
	}

	public void testPerformDiscovery() throws CoreException, IOException {
//FIXME: AF: see https://github.com/eclipse-mylyn/org.eclipse.mylyn/issues/196
//		discoveryStrategy.performDiscovery(new NullProgressMonitor());
//		assertFalse(discoveryStrategy.getCategories().isEmpty());
		for (DiscoveryCategory category : discoveryStrategy.getCategories()) {
//			System.out.println(String.format("%s: %s: %s", category.getId(), category.getName(),
//					category.getDescription()));
			assertNotNull(category.getId());
			assertNotNull(category.getName());
			assertNotNull(category.getDescription());
		}
//FIXME: AF: see https://github.com/eclipse-mylyn/org.eclipse.mylyn/issues/196
//		assertFalse(discoveryStrategy.getConnectors().isEmpty());
		for (DiscoveryConnector connector : discoveryStrategy.getConnectors()) {
//			System.out.println(String.format("%s: %s: %s", connector.getId(), connector.getName(),
//					connector.getDescription()));
			assertNotNull(connector.getId());
			assertNotNull(connector.getKind());
			assertNotNull(connector.getName());
			assertNotNull(connector.getDescription());

			// we _know_ that the bundle must have a plugin.xml... so verify that the source is working correctly
			assertNotNull(connector.getSource());
			URL pluginXmlUrl = connector.getSource().getResource("plugin.xml");
//			System.out.println("URL: " + pluginXmlUrl);
			InputStream in = pluginXmlUrl.openStream();
			assertNotNull(in);
			in.close();
		}

	}

}
