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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.FixtureDefinition;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestClient;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConfiguration;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestException;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestVersion;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Field;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.LoginToken;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Parameters;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Product;
import org.eclipse.mylyn.internal.bugzilla.rest.test.support.BugzillaRestTestFixture;
import org.eclipse.mylyn.internal.commons.core.operations.NullOperationMonitor;
import org.eclipse.mylyn.internal.commons.repositories.core.InMemoryCredentialsStore;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.google.gson.Gson;

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

	private BugzillaRestConnector connector;

	public BugzillaRestClientTest(BugzillaRestTestFixture fixture) {
		this.actualFixture = fixture;
	}

	@Before
	public void setUp() {
		connector = new BugzillaRestConnector();
	}

	@Test
	public void testConnectorClientCache() throws Exception {
		BugzillaRestClient client1 = connector.getClient(actualFixture.repository());
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
		LoginToken token = client.getClient().getLoginToken();
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

	@Test
	public void testGetConfiguration() throws Exception {
		TaskRepository repository = actualFixture.repository();
		BugzillaRestClient client = connector.getClient(repository);
		BugzillaRestConfiguration configuration = client.getConfiguration(repository, new NullOperationMonitor());
		Map<String, Field> fields = configuration.getFields();
		Collection<Field> fieldCollection = fields.values();
		assertConfigurationFieldNames(fieldCollection);
		assertEquals(
				IOUtils.toString(CommonTestUtil.getResource(this, actualFixture.getTestDataFolder() + "/fields.json")),
				new Gson().toJson(fields));
		Map<String, Product> products = configuration.getProducts();
		assertEquals(
				IOUtils.toString(CommonTestUtil.getResource(this, actualFixture.getTestDataFolder() + "/products.json")),
				new Gson().toJson(products));
		Parameters parameter = configuration.getParameters();
		assertEquals(
				IOUtils.toString(CommonTestUtil.getResource(this, actualFixture.getTestDataFolder()
						+ "/parameters.json")), new Gson().toJson(parameter));
		assertEquals(
				IOUtils.toString(CommonTestUtil.getResource(this, actualFixture.getTestDataFolder()
						+ "/configuration.json")),
						new Gson().toJson(configuration).replaceAll(repository.getRepositoryUrl(), "http://dummy.url"));

	}

	private void assertConfigurationFieldNames(Collection<Field> fields) throws IOException {
		List<String> fieldNameList = new ArrayList<String>(fields.size());
		for (Field field : fields) {
			fieldNameList.add(field.getName());
		}
		Collections.sort(fieldNameList);
		assertEquals(
				IOUtils.toString(CommonTestUtil.getResource(this, actualFixture.getTestDataFolder() + "/fieldName.json")),
				new Gson().toJson(fieldNameList));
	}

}
