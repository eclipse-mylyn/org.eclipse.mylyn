/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.hudson.tests.core;

import junit.framework.TestCase;

import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildFactory;
import org.eclipse.mylyn.internal.hudson.core.HudsonConnector;

/**
 * @author Steffen Pingel
 */
public class HudsonConnectorTest extends TestCase {

	public void testBuildElementFromUrlJobUrl() throws Exception {
		IBuildServer server = BuildFactory.eINSTANCE.createBuildServer();
		server.setUrl("http://server/");
		HudsonConnector connector = new HudsonConnector();
		IBuildElement element = connector.getBuildElementFromUrl(server, "http://server/job/my-plan/3/");
		assertNotNull(element);
		assertTrue("Expected IBuild, got " + element.getClass(), element instanceof IBuild);
		IBuild build = (IBuild) element;
		assertEquals("3", build.getId());
		assertEquals("my-plan", build.getPlan().getId());
	}

	public void testBuildElementFromUrlViewsUrl() throws Exception {
		IBuildServer server = BuildFactory.eINSTANCE.createBuildServer();
		server.setUrl("http://server/");
		HudsonConnector connector = new HudsonConnector();
		IBuildElement element = connector.getBuildElementFromUrl(server, "http://server/me/my-view/All/job/my-plan/3/");
		assertNotNull(element);
		assertTrue("Expected IBuild, got " + element.getClass(), element instanceof IBuild);
		IBuild build = (IBuild) element;
		assertEquals("3", build.getId());
		assertEquals("my-plan", build.getPlan().getId());
	}

	public void testBuildElementFromUrlUserUrl() throws Exception {
		IBuildServer server = BuildFactory.eINSTANCE.createBuildServer();
		server.setUrl("http://server/");
		HudsonConnector connector = new HudsonConnector();
		IBuildElement element = connector.getBuildElementFromUrl(server, "http://server/user/myid/");
		assertNull(element);
	}

	public void testBuildElementFromUrlNotMatching() throws Exception {
		IBuildServer server = BuildFactory.eINSTANCE.createBuildServer();
		server.setUrl("http://server/");
		HudsonConnector connector = new HudsonConnector();
		IBuildElement element = connector.getBuildElementFromUrl(server, "http://server2/job/my-plan/3/");
		assertNull(element);
	}

}
