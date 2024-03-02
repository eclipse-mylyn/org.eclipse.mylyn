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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.gitlab.core.GitlabConfiguration;
import org.eclipse.mylyn.internal.commons.core.operations.NullOperationMonitor;
import org.eclipse.mylyn.internal.gitlab.core.GitlabRestClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
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
		String version = GitlabTestFixture.current().client().getVersion(new NullOperationMonitor());
		assertNotNull(version);
		assertTrue(version.matches("\\d+\\.\\d+\\.\\d+")); //$NON-NLS-1$
	}

	@Test
	@GitLabTestService
	void testVersionAndRevision() throws Exception {
		String version = GitlabTestFixture.current().client().getVersionAndRevision(new NullOperationMonitor());
		assertNotNull(version);
		assertTrue(version.matches("\\d+\\.\\d+\\.\\d+\\(rev: \\w+\\)")); //$NON-NLS-1$
	}

	@Test
	@GitLabTestService
	void validate() throws Exception {
		assertTrue(GitlabTestFixture.current().client().validate(new NullOperationMonitor()));
	}

	@Test
	@GitLabTestService
	void testGetMetadata() throws Exception {
		JsonObject metaData = GitlabTestFixture.current().client().getMetadata(new NullOperationMonitor());
		assertNotNull(metaData);
		Set<String> keys = metaData.keySet();
		assertEquals(4, keys.size());
		assertTrue(keys.contains("version")); //$NON-NLS-1$
		assertTrue(keys.contains("revision")); //$NON-NLS-1$
		assertTrue(keys.contains("kas")); //$NON-NLS-1$
		assertTrue(keys.contains("enterprise")); //$NON-NLS-1$
	}

	@Test
	@GitLabTestService
	void testGetNamespaces() throws Exception {
		JsonElement resultElement = GitlabTestFixture.current().client().getNamespaces(new NullOperationMonitor());
		assertNotNull(resultElement);
		assertTrue(resultElement.isJsonArray());
		ArrayList<String> techUsers = new ArrayList<>();
		JsonArray resultArray = resultElement.getAsJsonArray();
		for (JsonElement resultObj : resultArray) {
			JsonObject jsonObject = resultObj.getAsJsonObject();
			JsonElement jsonElement = jsonObject.get("root_repository_size"); //$NON-NLS-1$
			if ("support-bot".equals(jsonObject.get("path").getAsString())
					|| "alert-bot".equals(jsonObject.get("path").getAsString())) {
				techUsers.add(jsonObject.get("path").getAsString());
			}
			if (jsonElement != null && !(jsonElement instanceof JsonNull)) {
				// size of root_repository_size could be 1923 or 1924
				// so we change the expected value for this field
				jsonObject.addProperty("root_repository_size", 1924); //$NON-NLS-1$
			}
			jsonElement = jsonObject.get("avatar_url"); //$NON-NLS-1$
			if (jsonElement != null && !(jsonElement instanceof JsonNull)) {
				jsonObject.addProperty("avatar_url", //$NON-NLS-1$
						"https://secure.gravatar.com/avatar/42?s\u003d80\u0026d\u003didenticon"); //$NON-NLS-1$
			}
		}
		techUsers.sort(Comparator.naturalOrder());
		String fName = "testdata/getNamespaces" + (techUsers.size() > 0 ? "_" : "") + String.join("_", techUsers)
		+ ".json";

		String actual = new GsonBuilder().setPrettyPrinting().create().toJson(resultElement);
		String expected = IOUtils.toString(CommonTestUtil.getResource(this, fName), Charset.defaultCharset());
		assertEquals(expected, actual);
	}

	@Test
	@GitLabTestService
	void testGetUser() throws Exception {
		JsonElement user = GitlabTestFixture.current().client().getUser(new NullOperationMonitor());
		assertNotNull(user);
		JsonObject userObj = user.getAsJsonObject();
		// remove Date and Time Attributes
		userObj.remove("created_at"); //$NON-NLS-1$
		userObj.remove("confirmed_at"); //$NON-NLS-1$
		userObj.remove("last_activity_on"); //$NON-NLS-1$
		userObj.remove("last_sign_in_at"); //$NON-NLS-1$
		userObj.remove("current_sign_in_at"); //$NON-NLS-1$
		// The avatar_url can change with a newer Gitlab version
		userObj.remove("avatar_url"); //$NON-NLS-1$
		String actual = new GsonBuilder().setPrettyPrinting().create().toJson(user);
		String expectedStr = IOUtils.toString(CommonTestUtil.getResource(this, "testdata/getUser.json"), //$NON-NLS-1$
				Charset.defaultCharset());
		assertEquals(expectedStr, actual);
	}

	@Test
	@GitLabTestService
	void testGetUsers() throws Exception {
		JsonElement resultElement = GitlabTestFixture.current().client().getUsers("", new NullOperationMonitor());
		assertNotNull(resultElement);
		ArrayList<String> techUsers = new ArrayList<>();
		for (JsonElement resultObj : resultElement.getAsJsonArray()) {
			JsonObject jsonObject = resultObj.getAsJsonObject();
			if ("support-bot".equals(jsonObject.get("username").getAsString())
					|| "alert-bot".equals(jsonObject.get("username").getAsString())) {
				techUsers.add(jsonObject.get("username").getAsString());
			}
			// remove Date and Time Attributes
			jsonObject.remove("created_at"); //$NON-NLS-1$
			jsonObject.remove("confirmed_at"); //$NON-NLS-1$
			jsonObject.remove("last_activity_on"); //$NON-NLS-1$
			jsonObject.remove("last_sign_in_at"); //$NON-NLS-1$
			jsonObject.remove("current_sign_in_at"); //$NON-NLS-1$
			// The avatar_url can change with a newer Gitlab version
			jsonObject.remove("avatar_url"); //$NON-NLS-1$
		}
		techUsers.sort(Comparator.naturalOrder());
		String fName = "testdata/getUsers" + (techUsers.size() > 0 ? "_" : "") + String.join("_", techUsers) + ".json";
		String actual = new GsonBuilder().setPrettyPrinting().create().toJson(resultElement);
		String expected = IOUtils.toString(CommonTestUtil.getResource(this, fName), Charset.defaultCharset());
		assertEquals(expected, actual);
	}

	@Test
	@GitLabTestService
	void testGetGroups() throws Exception {
		JsonElement resultElement = GitlabTestFixture.current().client().getGroups(new NullOperationMonitor());
		assertNotNull(resultElement);
		for (JsonElement resultObj : resultElement.getAsJsonArray()) {
			JsonObject jsonObject = resultObj.getAsJsonObject();
			jsonObject.addProperty("created_at", "2024-02-14T11:11:11.111Z"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		String actual = new GsonBuilder().setPrettyPrinting().create().toJson(resultElement);
		String expected = IOUtils.toString(CommonTestUtil.getResource(this, "testdata/getGroups.json"), //$NON-NLS-1$
				Charset.defaultCharset());
		assertEquals(expected, actual);
	}

	@Test
	@GitLabTestService
	void testgetConfiguration() throws Exception {
		GitlabRestClient client = GitlabTestFixture.current().client();
		GitlabConfiguration configuration = client.getConfiguration(client.getTaskRepository(),
				new NullOperationMonitor());
		assertNotNull(configuration);
		JsonObject user = configuration.getUserDetails().getAsJsonObject();
		// remove Date and Time Attributes
		user.remove("created_at"); //$NON-NLS-1$
		user.remove("confirmed_at"); //$NON-NLS-1$
		user.remove("last_activity_on"); //$NON-NLS-1$
		user.remove("last_sign_in_at"); //$NON-NLS-1$
		user.remove("current_sign_in_at"); //$NON-NLS-1$
		// The avatar_url can change with a newer Gitlab version
		user.remove("avatar_url"); //$NON-NLS-1$

		for (Integer integer : configuration.getProjectIDs()) {
			JsonObject project = configuration.getProductWithID(integer);
			// remove Date and Time Attributes
			project.remove("created_at"); //$NON-NLS-1$
			project.remove("updated_at"); //$NON-NLS-1$
			project.remove("last_activity_at"); //$NON-NLS-1$
			JsonObject projectContainerPolicy = project.get("container_expiration_policy").getAsJsonObject();
			projectContainerPolicy.remove("next_run_at");
			// Attributet that can change during the usage of the Gitlab Test Instance
			project.remove("open_issues_count"); //$NON-NLS-1$
			project.remove("runners_token"); //$NON-NLS-1$
			project.addProperty("auto_devops_enabled", true); //$NON-NLS-1$
		}
		for (String string : configuration.getGroupNames()) {
			JsonObject group = configuration.getGroupDetail(string).getAsJsonObject();
			group.remove("created_at"); //$NON-NLS-1$
			group.remove("runners_token"); //$NON-NLS-1$
		}
		String actual = new GsonBuilder().setPrettyPrinting().create().toJson(configuration);
		String expected = IOUtils.toString(CommonTestUtil.getResource(this, "testdata/configuration.json"), //$NON-NLS-1$
				Charset.defaultCharset());
		assertEquals(expected, actual);
	}

}
