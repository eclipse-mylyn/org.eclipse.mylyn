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
package org.eclipse.mylyn.internal.discovery.test.core.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryCategory;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryConnector;
import org.eclipse.mylyn.internal.discovery.core.model.RemoteBundleDiscoveryStrategy;

/**
 * 
 * @author David Green
 */
@SuppressWarnings("restriction")
public class RemoteBundleDiscoveryStrategyTest extends TestCase {

	private RemoteBundleDiscoveryStrategy discoveryStrategy;

	@Override
	protected void setUp() throws Exception {
		discoveryStrategy = new RemoteBundleDiscoveryStrategy();
		discoveryStrategy.setDirectoryUrl("http://localhost/~dgreen/plugins/directory.txt");
		discoveryStrategy.setCategories(new ArrayList<DiscoveryCategory>());
		discoveryStrategy.setConnectors(new ArrayList<DiscoveryConnector>());
	}
	
	public void testPerformDiscovery() throws CoreException, IOException {
		discoveryStrategy.performDiscovery(new NullProgressMonitor());
		assertFalse(discoveryStrategy.getCategories().isEmpty());
		for (DiscoveryCategory category: discoveryStrategy.getCategories()) {
			System.out.println(String.format("%s: %s: %s",category.getId(),category.getName(),category.getDescription()));
			assertNotNull(category.getId());
			assertNotNull(category.getName());
			assertNotNull(category.getDescription());
		}
		assertFalse(discoveryStrategy.getConnectors().isEmpty());
		for (DiscoveryConnector connector: discoveryStrategy.getConnectors()) {
			System.out.println(String.format("%s: %s: %s",connector.getId(),connector.getName(),connector.getDescription()));
			assertNotNull(connector.getId());
			assertNotNull(connector.getKind());
			assertNotNull(connector.getName());
			assertNotNull(connector.getDescription());
			
			// we _know_ that the bundle must have a plugin.xml... so verify that the source is working correctly
			assertNotNull(connector.getSource());
			URL pluginXmlUrl = connector.getSource().getResource("plugin.xml");
			System.out.println("URL: "+pluginXmlUrl);
			InputStream in = pluginXmlUrl.openStream();
			assertNotNull(in);
			in.close();
		}
		
	}

}
