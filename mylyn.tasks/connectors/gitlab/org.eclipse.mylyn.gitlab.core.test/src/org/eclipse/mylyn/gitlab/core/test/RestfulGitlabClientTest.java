/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gitlab.core.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.charset.Charset;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.commons.core.operations.NullOperationMonitor;
import org.eclipse.mylyn.internal.gitlab.core.GitlabRestClient;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class RestfulGitlabClientTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		try {
			GitlabTestFixture.current();
			System.setProperty("gitlabInstance", "active"); //$NON-NLS-1$//$NON-NLS-2$
		} catch (Exception e) {
			System.setProperty("gitlabInstance", "inactive"); //$NON-NLS-1$//$NON-NLS-2$
		}
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@DisabledIfSystemProperty(named = "gitlabInstance", matches = ".*inactive.*")
	@interface GitLabTestService {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	@GitLabTestService
	void setUp() throws Exception {
		GitlabTestFixture.current().clearOverwriteProperties();
// when you want to execute the tests with user and password instead of the accestoken.
// change the password stored in
// /org.eclipse.mylyn.gitlab.core.test/testdata/credentials.properties
// and uncomment the next line
//		GitlabTestFixture.current().addOverwriteProperty(GitlabCoreActivator.USE_PERSONAL_ACCESS_TOKEN, "false");
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	@GitLabTestService
	void testVersion() throws Exception {
		GitlabRestClient client = GitlabTestFixture.current().client();
		String version = client.getVersion(new NullOperationMonitor());
		assertNotNull(version);
		assertTrue(version.matches("\\d+\\.\\d+\\.\\d+"));
	}

	@Test
	@GitLabTestService
	void testVersionAndRevision() throws Exception {
		GitlabRestClient client = GitlabTestFixture.current().client();
		String version = client.getVersionAndRevision(new NullOperationMonitor());
		assertNotNull(version);
		assertTrue(version.matches("\\d+\\.\\d+\\.\\d+\\(rev: \\w+\\)"));
	}

	@Test
	@GitLabTestService
	void validate() throws Exception {
		GitlabRestClient client = GitlabTestFixture.current().client();
		assertTrue(client.validate(new NullOperationMonitor()));
	}

	@Test
	@GitLabTestService
	void testGetMetadata() throws Exception {
		GitlabRestClient client = GitlabTestFixture.current().client();
		JsonObject metaData = client.getMetadata(new NullOperationMonitor());
		assertNotNull(metaData);
		Set<String> keys = metaData.keySet();
		assertEquals(4, keys.size());
		assertTrue(keys.contains("version"));
		assertTrue(keys.contains("revision"));
		assertTrue(keys.contains("kas"));
		assertTrue(keys.contains("enterprise"));
	}

	@Test
	@GitLabTestService
	void testGetNamespaces() throws Exception {
		GitlabRestClient client = GitlabTestFixture.current().client();
		JsonElement namespaces = client.getNamespaces(new NullOperationMonitor());
		assertNotNull(namespaces);
		assertTrue(namespaces.isJsonArray());
		JsonArray namespacesArray = namespaces.getAsJsonArray();
		assertEquals(5, namespacesArray.size());

		// size of root_repository_size could be 1923 or 1924
		// so we change the expected value for this field
		int size = namespacesArray.get(3).getAsJsonObject().get("root_repository_size").getAsInt();
		String expected = IOUtils
				.toString(CommonTestUtil.getResource(this, "testdata/getNamespaces.json"), Charset.defaultCharset())
				.replace("##size##", Integer.toString(size));
		assertEquals("eclipse-mylyn", namespacesArray.get(3).getAsJsonObject().get("name").getAsString());
		assertEquals(expected, new GsonBuilder().setPrettyPrinting().create().toJson(namespaces));
	}

	@Test
	@GitLabTestService
	void testGetUser() throws Exception {
		GitlabRestClient client = GitlabTestFixture.current().client();
		TaskRepository tr = client.getTaskRepository();
		JsonElement user = client.getUser(new NullOperationMonitor());
		assertNotNull(user);
		String expected = IOUtils
				.toString(CommonTestUtil.getResource(this, "testdata/getUser.json"), Charset.defaultCharset())
				.replace("##created_at##", user.getAsJsonObject().get("created_at").getAsString())
				.replace("##confirmed_at##", user.getAsJsonObject().get("confirmed_at").getAsString())
				.replace("##last_activity_on##", user.getAsJsonObject().get("last_activity_on").getAsString());
		assertEquals(expected, new GsonBuilder().setPrettyPrinting().create().toJson(user));
	}

}
