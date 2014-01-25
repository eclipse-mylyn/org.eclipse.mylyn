/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.FixtureDefinition;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestClient;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestException;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestVersion;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.BugzillaRestLoginToken;
import org.eclipse.mylyn.internal.bugzilla.rest.test.support.BugzillaRestTestFixture;
import org.eclipse.mylyn.internal.commons.core.operations.NullOperationMonitor;
import org.eclipse.mylyn.internal.commons.repositories.core.InMemoryCredentialsStore;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@SuppressWarnings("restriction")
@RunWith(Junit4TestFixtureRunner.class)
@FixtureDefinition(fixtureClass = BugzillaRestTestFixture.class, fixtureType = "bugzillaREST")
// use this if you only want to run the test class if the property exists with
// the value in the fixture.
// Note: When there is no fixture with this property no tests get executed
//@RunOnlyWhenProperty(property = "default", value = "1")
public class BugzillaRestClientTest {
	private final BugzillaRestTestFixture actualFixture;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static TaskRepositoryManager manager;

	private BugzillaRestConnector connector;

	public BugzillaRestClientTest(BugzillaRestTestFixture fixture) {
		this.actualFixture = fixture;
	}

	@BeforeClass
	public static void setUpClass() {
		manager = new TaskRepositoryManager();
	}

	@Before
	public void setUp() {
		manager.addRepository(actualFixture.repository());
		connector = new BugzillaRestConnector();
	}

	@After
	public void tearDown() throws Exception {
		manager.clearRepositories();
	}

	@Test
	public void testConnectorClientCache() throws Exception {
		BugzillaRestClient client1 = connector.getClient(actualFixture.repository());
		assertNotNull(client1);
	}

	@Test
	public void testConnectorClientCacheRepositoryNotInManagerFailwithID() throws Exception {
		TaskRepository repository = new TaskRepository(actualFixture.getConnectorKind(),
				actualFixture.getRepositoryUrl());
		UserCredentials credentials = CommonTestUtil.getCredentials(PrivilegeLevel.USER);
		repository.setCredentials(org.eclipse.mylyn.commons.net.AuthenticationType.REPOSITORY,
				new AuthenticationCredentials(credentials.getUserName(), credentials.getPassword()), true);
		BugzillaRestClient client1 = connector.getClient(repository);
		assertNotNull(client1);
	}

	@Test
	public void testGetVersion() throws Exception {
		BugzillaRestClient client = new BugzillaRestClient(actualFixture.location());
		assertNotNull(client.getClient());
		assertNull(client.getClient().getLoginToken());
		BugzillaRestVersion version = client.getVersion(new NullOperationMonitor());
		assertEquals("expeccted: " + actualFixture.getVersion() + " actual: " + version.toString(),
				actualFixture.getVersion(), version.toString());
	}

	@Test
	public void testValidate() throws Exception {
		BugzillaRestClient client = new BugzillaRestClient(actualFixture.location());
		assertNotNull(client.getClient());
		assertNull(client.getClient().getLoginToken());
		assertTrue(client.validate(new NullOperationMonitor()));
		assertNotNull(client.getClient());
		BugzillaRestLoginToken token = client.getClient().getLoginToken();
		assertNotNull(token);
		assertEquals("2", token.getId());
		assertNotNull(token.getToken());
		assertTrue(token.getToken().length() > 0);
	}

	@Test
	public void testInvalideUserValidate() throws BugzillaRestException {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl(actualFixture.getRepositoryUrl());
		location.setProxy(null);
		location.setCredentialsStore(new InMemoryCredentialsStore());
		location.setCredentials(AuthenticationType.REPOSITORY, new UserCredentials("wrong", "wrong"));
		thrown.expect(BugzillaRestException.class);
		thrown.expectMessage("Authentication failed");
		BugzillaRestClient client;
		client = new BugzillaRestClient(location);
		assertNotNull(client.getClient());
		assertNull(client.getClient().getLoginToken());
		client.validate(new NullOperationMonitor());
	}

	@Test
	public void testNoUserValidate() throws BugzillaRestException {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl(actualFixture.getRepositoryUrl());
		location.setProxy(null);
		location.setCredentialsStore(new InMemoryCredentialsStore());
		location.setCredentials(AuthenticationType.REPOSITORY, null);
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Authentication requested without valid credentials");
		BugzillaRestClient client;
		client = new BugzillaRestClient(location);
		assertNotNull(client.getClient());
		assertNull(client.getClient().getLoginToken());
		client.validate(new NullOperationMonitor());
	}
}