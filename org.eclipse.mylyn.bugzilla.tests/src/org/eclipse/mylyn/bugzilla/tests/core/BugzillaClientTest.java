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

package org.eclipse.mylyn.bugzilla.tests.core;

import java.net.InetSocketAddress;
import java.net.Proxy;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClientFactory;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Robert Elves
 */
public class BugzillaClientTest extends TestCase {

	private BugzillaClient client;

	@Override
	protected void setUp() throws Exception {
		client = BugzillaFixture.current().client();
	}

	public void testRDFProductConfig() throws Exception {
		RepositoryConfiguration config = client.getRepositoryConfiguration();
		assertNotNull(config);
		assertEquals(BugzillaFixture.current().getVersion(), config.getInstallVersion().toString());
		assertEquals(7, config.getStatusValues().size());
		assertEquals(9, config.getResolutions().size());
		assertEquals(6, config.getPlatforms().size());
		assertEquals(32, config.getOSs().size());
		assertEquals(5, config.getPriorities().size());
		assertEquals(7, config.getSeverities().size());
		assertTrue(config.getProducts().size() > 50);
		assertEquals(4, config.getOpenStatusValues().size());
		assertEquals(14, config.getComponents("Mylyn").size());
		assertEquals(27, config.getKeywords().size());
		assertEquals(1, config.getComponents("TestProduct").size());
		assertEquals(1, config.getVersions("TestProduct").size());
		assertEquals(0, config.getTargetMilestones("TestProduct").size());
		// assertEquals(10, config.getComponents("Hyades").size());
		// assertEquals(1, config.getTargetMilestones("TestProduct").size());
	}

	public void testValidate() throws Exception {
		TaskRepository repository = BugzillaFixture.current().repository();
		AbstractWebLocation location = BugzillaFixture.current().location();
		client = new BugzillaClient(location, repository.getCharacterEncoding(), repository.getProperties(),
				BugzillaRepositoryConnector.getLanguageSetting(IBugzillaConstants.DEFAULT_LANG));
		client.validate(new NullProgressMonitor());
	}

	public void testValidateInvalidProxy() throws Exception {
		TaskRepository repository = BugzillaFixture.current().repository();
		AbstractWebLocation location = BugzillaFixture.current().location(PrivilegeLevel.USER,
				new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 12356)));

		client = BugzillaClientFactory.createClient(repository);
		client = new BugzillaClient(location, repository.getCharacterEncoding(), repository.getProperties(),
				BugzillaRepositoryConnector.getLanguageSetting(IBugzillaConstants.DEFAULT_LANG));
		client.validate(new NullProgressMonitor());
	}

//	public void testCredentialsEncoding() throws IOException, BugzillaException, KeyManagementException,
//			GeneralSecurityException {
//		String poundSignUTF8 = BugzillaClient.addCredentials(IBugzillaTestConstants.TEST_BUGZILLA_222_URL, "UTF-8",
//				"testUser", "\u00A3");
//		assertTrue(poundSignUTF8.endsWith("password=%C2%A3"));
//		String poundSignISO = BugzillaClient.addCredentials(IBugzillaTestConstants.TEST_BUGZILLA_222_URL, "ISO-8859-1",
//				"testUser", "\u00A3");
//		assertFalse(poundSignISO.contains("%C2%A3"));
//		assertTrue(poundSignISO.endsWith("password=%A3"));
//	}

}
