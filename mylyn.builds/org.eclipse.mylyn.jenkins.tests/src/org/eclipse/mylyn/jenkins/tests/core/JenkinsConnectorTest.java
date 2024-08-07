/*******************************************************************************
 * Copyright (c) 2012, 2013 Tasktop Technologies.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.jenkins.tests.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildFactory;
import org.eclipse.mylyn.internal.jenkins.core.JenkinsConnector;
import org.junit.jupiter.api.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class JenkinsConnectorTest {

	@Test
	public void testBuildElementFromUrlJobUrl() throws Exception {
		IBuildServer server = BuildFactory.eINSTANCE.createBuildServer();
		server.setUrl("http://server/");
		JenkinsConnector connector = new JenkinsConnector();
		IBuildElement element = connector.getBuildElementFromUrl(server, "http://server/job/my-plan/3/");
		assertNotNull(element);
		assertTrue(element instanceof IBuild, "Expected IBuild, got " + element.getClass());
		IBuild build = (IBuild) element;
		assertEquals("3", build.getId());
		assertEquals("my-plan", build.getPlan().getId());
		assertEquals("http://server/job/my-plan/3/", build.getUrl());
		assertEquals("http://server/job/my-plan/", build.getPlan().getUrl());
	}

	@Test
	public void testBuildElementFromUrlViewsUrl() throws Exception {
		IBuildServer server = BuildFactory.eINSTANCE.createBuildServer();
		server.setUrl("http://server/");
		JenkinsConnector connector = new JenkinsConnector();
		IBuildElement element = connector.getBuildElementFromUrl(server, "http://server/me/my-view/All/job/my-plan/3/");
		assertNotNull(element);
		assertTrue(element instanceof IBuild, "Expected IBuild, got " + element.getClass());
		IBuild build = (IBuild) element;
		assertEquals("3", build.getId());
		assertEquals("my-plan", build.getPlan().getId());
		assertEquals("http://server/me/my-view/All/job/my-plan/3/", build.getUrl());
		assertEquals("http://server/me/my-view/All/job/my-plan/", build.getPlan().getUrl());
	}

	@Test
	public void testBuildElementFromUrlUserUrl() throws Exception {
		IBuildServer server = BuildFactory.eINSTANCE.createBuildServer();
		server.setUrl("http://server/");
		JenkinsConnector connector = new JenkinsConnector();
		IBuildElement element = connector.getBuildElementFromUrl(server, "http://server/user/myid/");
		assertNull(element);
	}

	@Test
	public void testBuildElementFromUrlNotMatching() throws Exception {
		IBuildServer server = BuildFactory.eINSTANCE.createBuildServer();
		server.setUrl("http://server/");
		JenkinsConnector connector = new JenkinsConnector();
		IBuildElement element = connector.getBuildElementFromUrl(server, "http://server2/job/my-plan/3/");
		assertNull(element);
	}

}
