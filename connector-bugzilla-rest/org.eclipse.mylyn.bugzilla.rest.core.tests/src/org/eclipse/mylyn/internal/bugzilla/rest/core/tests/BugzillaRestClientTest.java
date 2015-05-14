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
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestCreateTaskSchema;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestException;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestVersion;
import org.eclipse.mylyn.internal.bugzilla.rest.core.SingleTaskDataCollector;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Field;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.LoginToken;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Parameters;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Product;
import org.eclipse.mylyn.internal.bugzilla.rest.test.support.BugzillaRestTestFixture;
import org.eclipse.mylyn.internal.commons.core.operations.NullOperationMonitor;
import org.eclipse.mylyn.internal.commons.repositories.core.InMemoryCredentialsStore;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
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
		BugzillaRestClient client = new BugzillaRestClient(actualFixture.location(), connector);
		assertNotNull(client.getClient());
		assertNull(client.getClient().getLoginToken());
		BugzillaRestVersion version = client.getVersion(new NullOperationMonitor());
		assertEquals("expeccted: " + actualFixture.getVersion() + " actual: " + version.toString(),
				actualFixture.getVersion(), version.toString());
	}

	@Test
	public void testValidate() throws Exception {
		BugzillaRestClient client = new BugzillaRestClient(actualFixture.location(), connector);
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
		client = new BugzillaRestClient(location, connector);
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
		client = new BugzillaRestClient(location, connector);
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
				IOUtils.toString(
						CommonTestUtil.getResource(this, actualFixture.getTestDataFolder() + "/products.json")),
				new Gson().toJson(products));
		Parameters parameter = configuration.getParameters();
		assertEquals(
				IOUtils.toString(
						CommonTestUtil.getResource(this, actualFixture.getTestDataFolder() + "/parameters.json")),
				new Gson().toJson(parameter));
		assertEquals(
				IOUtils.toString(
						CommonTestUtil.getResource(this, actualFixture.getTestDataFolder() + "/configuration.json")),
				new Gson().toJson(configuration).replaceAll(repository.getRepositoryUrl(), "http://dummy.url"));

	}

	private void assertConfigurationFieldNames(Collection<Field> fields) throws IOException {
		List<String> fieldNameList = new ArrayList<String>(fields.size());
		for (Field field : fields) {
			fieldNameList.add(field.getName());
		}
		Collections.sort(fieldNameList);
		assertEquals(
				IOUtils.toString(
						CommonTestUtil.getResource(this, actualFixture.getTestDataFolder() + "/fieldName.json")),
				new Gson().toJson(fieldNameList));
	}

	@Test
	public void testinitializeTaskData() throws Exception {
		final TaskMapping taskMappingInit = new TaskMapping() {
			@Override
			public String getSummary() {
				return "The Summary";
			}

			@Override
			public String getDescription() {
				return "The Description";
			}
		};
		final TaskMapping taskMappingSelect = new TaskMapping() {
			@Override
			public String getProduct() {
				return "TestProduct";
			}
		};

		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskRepository repository = actualFixture.repository();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(repository);
		TaskData taskData = new TaskData(mapper, repository.getConnectorKind(), repository.getRepositoryUrl(), "");
		assertTrue(taskDataHandler.initializeTaskData(repository, taskData, null, null));
		assertEquals(
				IOUtils.toString(CommonTestUtil.getResource(this, actualFixture.getTestDataFolder() + "/taskData.txt")),
				taskData.getRoot().toString());
		taskData = new TaskData(mapper, repository.getConnectorKind(), repository.getRepositoryUrl(), "");
		assertTrue(taskDataHandler.initializeTaskData(repository, taskData, taskMappingInit, null));
		assertEquals(
				IOUtils.toString(
						CommonTestUtil.getResource(this, actualFixture.getTestDataFolder() + "/taskData1.txt")),
				taskData.getRoot().toString());
		taskData = new TaskData(mapper, repository.getConnectorKind(), repository.getRepositoryUrl(), "");
		assertTrue(taskDataHandler.initializeTaskData(repository, taskData, taskMappingSelect, null));
		assertEquals(
				IOUtils.toString(
						CommonTestUtil.getResource(this, actualFixture.getTestDataFolder() + "/taskData2.txt")),
				taskData.getRoot().toString());
	}

	@Test
	public void testPostTaskDataWithoutProduct() throws Exception {
		final TaskMapping taskMappingInit = new TaskMapping() {
			@Override
			public String getSummary() {
				return "The Summary";
			}

			@Override
			public String getDescription() {
				return "The Description";
			}

		};
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(actualFixture.repository());
		TaskData taskData = new TaskData(mapper, actualFixture.repository().getConnectorKind(),
				actualFixture.repository().getRepositoryUrl(), "");
		taskDataHandler.initializeTaskData(actualFixture.repository(), taskData, taskMappingInit, null);
		taskData.getRoot().getAttribute("cf_dropdown").setValue("one");
		try {
			connector.getClient(actualFixture.repository()).postTaskData(taskData, null);
			fail("BugzillaRestException expected, never reach this!");
		} catch (BugzillaRestException e) {
			String url = actualFixture.getRepositoryUrl();
			assertEquals("You must select/enter a product.  (status: Bad Request from "
					+ url.substring(url.lastIndexOf('/')) + "/rest.cgi/bug)", e.getMessage());
		}
	}

	@Test
	public void testPostTaskDataWithoutMilestone() throws Exception {
		final TaskMapping taskMappingInit = new TaskMapping() {
			@Override
			public String getSummary() {
				return "The Summary";
			}

			@Override
			public String getDescription() {
				return "The Description";
			}

			@Override
			public String getProduct() {
				return "ManualTest";
			}

			@Override
			public String getComponent() {
				return "ManualC1";
			}

			@Override
			public String getVersion() {
				return "R1";
			}
		};
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(actualFixture.repository());
		TaskData taskData = new TaskData(mapper, actualFixture.repository().getConnectorKind(),
				actualFixture.repository().getRepositoryUrl(), "");
		taskDataHandler.initializeTaskData(actualFixture.repository(), taskData, taskMappingInit, null);
		taskData.getRoot().getAttribute("cf_dropdown").setValue("one");
		try {
			connector.getClient(actualFixture.repository()).postTaskData(taskData, null);
			fail("never reach this!");
		} catch (BugzillaRestException e) {
			String url = actualFixture.getRepositoryUrl();
			assertEquals("You must select/enter a milestone.  (status: Bad Request from "
					+ url.substring(url.lastIndexOf('/')) + "/rest.cgi/bug)", e.getMessage());
		}
	}

	@Test
	public void testPostTaskData() throws Exception {
		final TaskMapping taskMappingInit = new TaskMapping() {
			@Override
			public String getSummary() {
				return "The Summary";
			}

			@Override
			public String getDescription() {
				return "The Description";
			}

			@Override
			public String getProduct() {
				return "ManualTest";
			}

			@Override
			public String getComponent() {
				return "ManualC1";
			}

			@Override
			public String getVersion() {
				return "R1";
			}
		};
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(actualFixture.repository());
		TaskData taskData = new TaskData(mapper, actualFixture.repository().getConnectorKind(),
				actualFixture.repository().getRepositoryUrl(), "");
		taskDataHandler.initializeTaskData(actualFixture.repository(), taskData, taskMappingInit, null);
		taskData.getRoot().getAttribute("cf_dropdown").setValue("one");
		taskData.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().TARGET_MILESTONE.getKey())
				.setValue("M2");
		RepositoryResponse reposonse = connector.getClient(actualFixture.repository()).postTaskData(taskData, null);
		assertEquals(ResponseKind.TASK_CREATED, reposonse.getReposonseKind());
	}

	@Test
	public void testPostTaskDataFromTaskdata() throws Exception {
		final TaskMapping taskMappingInit = new TaskMapping() {
			@Override
			public String getSummary() {
				return "The Summary";
			}

			@Override
			public String getDescription() {
				return "The Description";
			}

			@Override
			public String getProduct() {
				return "ManualTest";
			}

			@Override
			public String getComponent() {
				return "ManualC1";
			}

			@Override
			public String getVersion() {
				return "R1";
			}
		};
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(actualFixture.repository());
		TaskData taskData = new TaskData(mapper, actualFixture.repository().getConnectorKind(),
				actualFixture.repository().getRepositoryUrl(), "");
		taskDataHandler.initializeTaskData(actualFixture.repository(), taskData, taskMappingInit, null);
		taskData.getRoot().getAttribute("cf_dropdown").setValue("one");
		taskData.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().TARGET_MILESTONE.getKey())
				.setValue("M2");

		TaskData taskDataSubmit = new TaskData(mapper, actualFixture.repository().getConnectorKind(),
				actualFixture.repository().getRepositoryUrl(), "");
		taskDataHandler.initializeTaskData(actualFixture.repository(), taskDataSubmit, null, null);
		TaskMapper mapper1 = new TaskMapper(taskData);
		connector.getTaskMapping(taskDataSubmit).merge(mapper1);

		RepositoryResponse reposonse = connector.getClient(actualFixture.repository()).postTaskData(taskDataSubmit,
				null);
		assertEquals(ResponseKind.TASK_CREATED, reposonse.getReposonseKind());
	}

	@Test
	public void testGetTaskData() throws Exception {
		final TaskMapping taskMappingInit = new TaskMapping() {
			@Override
			public String getSummary() {
				return "The Summary";
			}

			@Override
			public String getDescription() {
				return "The Description";
			}

			@Override
			public String getProduct() {
				return "ManualTest";
			}

			@Override
			public String getComponent() {
				return "ManualC1";
			}

			@Override
			public String getVersion() {
				return "R1";
			}
		};
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(actualFixture.repository());
		TaskData taskData = new TaskData(mapper, actualFixture.repository().getConnectorKind(),
				actualFixture.repository().getRepositoryUrl(), "");
		taskDataHandler.initializeTaskData(actualFixture.repository(), taskData, taskMappingInit, null);
		taskData.getRoot().getAttribute("cf_dropdown").setValue("one");
		taskData.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().TARGET_MILESTONE.getKey())
				.setValue("M2");
		RepositoryResponse reposonse = connector.getClient(actualFixture.repository()).postTaskData(taskData, null);
		assertEquals(ResponseKind.TASK_CREATED, reposonse.getReposonseKind());
		String taskId = reposonse.getTaskId();
		Set<String> taskIds = new HashSet<String>();
		taskIds.add(taskId);
		SingleTaskDataCollector singleTaskDataCollector = new SingleTaskDataCollector();
		connector.getClient(actualFixture.repository()).getTaskData(taskIds, actualFixture.repository(),
				singleTaskDataCollector, null);
		TaskData taskDataGet = singleTaskDataCollector.getTaskData();
		assertNotNull(taskDataGet);
		assertNotNull(taskDataGet.getRoot());

		// actual we read no comments and so we also can not get the description
		taskData.getRoot().removeAttribute("task.common.description");
		taskDataGet.getRoot().removeAttribute("task.common.description");

		// attributes we know that they can not be equal
		taskData.getRoot().removeAttribute("task.common.status");
		taskDataGet.getRoot().removeAttribute("task.common.status");
		taskData.getRoot().removeAttribute("task.common.user.assigned");
		taskDataGet.getRoot().removeAttribute("task.common.user.assigned");
		taskData.getRoot().removeAttribute("task.common.operation");
		taskDataGet.getRoot().removeAttribute("task.common.operation");

		// attributes only in new tasks
		taskData.getRoot().removeAttribute("description_is_private");

		// attributes only in old tasks
		taskDataGet.getRoot().removeAttribute("bug_id");
		taskDataGet.getRoot().removeAttribute("task.common.comment.new");

		// attributes for operations
		taskDataGet.getRoot().removeAttribute("task.common.operation-CONFIRMED");
		taskDataGet.getRoot().removeAttribute("task.common.operation-IN_PROGRESS");
		taskDataGet.getRoot().removeAttribute("task.common.operation-RESOLVED");
		taskDataGet.getRoot().removeAttribute("resolutionInput");
		taskDataGet.getRoot().removeAttribute("task.common.operation-duplicate");
		taskDataGet.getRoot().removeAttribute("dup_id");

		assertEquals(taskData.getRoot().toString(), taskDataGet.getRoot().toString());
	}

}
