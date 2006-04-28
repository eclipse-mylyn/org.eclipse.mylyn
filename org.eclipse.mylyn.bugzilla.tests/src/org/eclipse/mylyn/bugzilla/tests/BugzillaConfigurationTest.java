/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.bugzilla.tests;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.internal.RepositoryConfiguration;
import org.eclipse.mylar.internal.bugzilla.core.internal.ServerConfigurationFactory;

public class BugzillaConfigurationTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	
	public void test222RDFProductConfig() throws IOException {
		ServerConfigurationFactory factory = ServerConfigurationFactory.getInstance();
		RepositoryConfiguration config = factory.getConfiguration(IBugzillaConstants.TEST_BUGZILLA_222_URL);
		assertNotNull(config);
		assertEquals("2.22rc1", config.getInstallVersion());
		assertEquals(7, config.getStatusValues().size());
		assertEquals(8, config.getResolutions().size());
		assertEquals(4, config.getPlatforms().size());
		assertEquals(5, config.getOSs().size());
		assertEquals(5, config.getPriorities().size());
		assertEquals(7, config.getSeverities().size());
		assertEquals(1, config.getProducts().size());
		assertEquals(4, config.getOpenStatusValues().size());
		assertEquals(1, config.getComponents("TestProduct").size());
		assertEquals(1, config.getVersions("TestProduct").size());
		assertEquals(1, config.getTargetMilestones("TestProduct").size());
	}
	
	public void test2201RDFProductConfig() throws IOException {
		ServerConfigurationFactory factory = ServerConfigurationFactory.getInstance();
		RepositoryConfiguration config = factory.getConfiguration(IBugzillaConstants.TEST_BUGZILLA_2201_URL);
		assertNotNull(config);
		assertEquals("2.20.1", config.getInstallVersion());
		assertEquals(7, config.getStatusValues().size());
		assertEquals(8, config.getResolutions().size());
		assertEquals(4, config.getPlatforms().size());
		assertEquals(5, config.getOSs().size());
		assertEquals(5, config.getPriorities().size());
		assertEquals(7, config.getSeverities().size());
		assertEquals(1, config.getProducts().size());
		assertEquals(4, config.getOpenStatusValues().size());
		assertEquals(2, config.getComponents("TestProduct").size());
		assertEquals(1, config.getVersions("TestProduct").size());	
		//assertEquals(1, config.getTargetMilestones("TestProduct").size());
	}
	
	public void test220RDFProductConfig() throws IOException {
		ServerConfigurationFactory factory = ServerConfigurationFactory.getInstance();
		RepositoryConfiguration config = factory.getConfiguration(IBugzillaConstants.TEST_BUGZILLA_220_URL);
		assertNotNull(config);
		assertEquals("2.20", config.getInstallVersion());
		assertEquals(7, config.getStatusValues().size());
		assertEquals(8, config.getResolutions().size());
		assertEquals(4, config.getPlatforms().size());
		assertEquals(5, config.getOSs().size());
		assertEquals(5, config.getPriorities().size());
		assertEquals(7, config.getSeverities().size());
		assertEquals(2, config.getProducts().size());
		assertEquals(4, config.getOpenStatusValues().size());
		assertEquals(2, config.getComponents("TestProduct").size());
		assertEquals(1, config.getVersions("TestProduct").size());
		//assertEquals(1, config.getTargetMilestones("TestProduct").size());
	}
	
	public void test218RDFProductConfig() throws IOException {
		ServerConfigurationFactory factory = ServerConfigurationFactory.getInstance();
		RepositoryConfiguration config = factory.getConfiguration(IBugzillaConstants.TEST_BUGZILLA_218_URL);
		assertNotNull(config);
		assertEquals("2.18.5", config.getInstallVersion());
		assertEquals(7, config.getStatusValues().size());
		assertEquals(8, config.getResolutions().size());
		assertEquals(8, config.getPlatforms().size());
		assertEquals(37, config.getOSs().size());
		assertEquals(5, config.getPriorities().size());
		assertEquals(7, config.getSeverities().size());
		assertEquals(1, config.getProducts().size());
		assertEquals(4, config.getOpenStatusValues().size());
		assertEquals(1, config.getComponents("TestProduct").size());
		assertEquals(1, config.getVersions("TestProduct").size());
		//assertEquals(1, config.getTargetMilestones("TestProduct").size());
	}
	
	public void testEclipseRDFProductConfig() throws IOException {
		ServerConfigurationFactory factory = ServerConfigurationFactory.getInstance();
		RepositoryConfiguration config = factory.getConfiguration(IBugzillaConstants.ECLIPSE_BUGZILLA_URL);
		assertNotNull(config);
		assertEquals("2.20.1", config.getInstallVersion());
		assertEquals(7, config.getStatusValues().size());
		assertEquals(8, config.getResolutions().size());
		assertEquals(6, config.getPlatforms().size());
		assertEquals(27, config.getOSs().size());
		assertEquals(5, config.getPriorities().size());
		assertEquals(7, config.getSeverities().size());
		assertEquals(53, config.getProducts().size());
		assertEquals(4, config.getOpenStatusValues().size());
		assertEquals(10, config.getComponents("Mylar").size());
		//assertEquals(10, config.getComponents("Hyades").size());
		//assertEquals(1, config.getTargetMilestones("TestProduct").size());
	}
}
