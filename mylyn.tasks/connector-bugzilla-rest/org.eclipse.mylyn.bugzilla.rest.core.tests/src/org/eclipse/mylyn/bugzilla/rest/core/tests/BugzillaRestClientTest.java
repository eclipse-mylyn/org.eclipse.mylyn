/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
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

package org.eclipse.mylyn.bugzilla.rest.core.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.bugzilla.rest.test.support.BugzillaRestHarness;
import org.eclipse.mylyn.bugzilla.rest.test.support.BugzillaRestTestFixture;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.AbstractTestFixture;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.ConditionalIgnoreRule;
import org.eclipse.mylyn.commons.sdk.util.IFixtureJUnitClass;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner;
import org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.FixtureDefinition;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestAttachmentMapper;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestClient;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConfiguration;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestCreateTaskSchema;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestException;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestTaskAttachmentHandler;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestTaskSchema;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestVersion;
import org.eclipse.mylyn.internal.bugzilla.rest.core.IBugzillaRestConstants;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Field;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Parameters;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Product;
import org.eclipse.mylyn.internal.commons.core.operations.NullOperationMonitor;
import org.eclipse.mylyn.internal.commons.repositories.core.InMemoryCredentialsStore;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataState;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
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
public class BugzillaRestClientTest implements IFixtureJUnitClass {
	private final BugzillaRestTestFixture actualFixture;

	@Rule
	public ConditionalIgnoreRule rule = new ConditionalIgnoreRule(this);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private BugzillaRestConnector connector;

	private BugzillaRestHarness harness;

	public BugzillaRestClientTest(BugzillaRestTestFixture fixture) {
		this.actualFixture = fixture;
	}

	@Before
	public void setUp() {
		connector = actualFixture.connector();
		harness = actualFixture.createHarness();
	}

	public AbstractTestFixture getActualFixture() {
		return actualFixture;
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
		BugzillaRestVersion version = client.getVersion(new NullOperationMonitor());
		assertEquals("expeccted: " + actualFixture.getVersion() + " actual: " + version.toString(),
				actualFixture.getVersion(), version.toString());
	}

	@Test
	public void testValidate() throws Exception {
		BugzillaRestClient client = new BugzillaRestClient(actualFixture.location(), connector);
		assertNotNull(client.getClient());
		assertTrue(client.validate(new NullOperationMonitor()));
	}

	@Test
	public void testInvalidUserValidate() throws BugzillaRestException {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl(actualFixture.getRepositoryUrl());
		location.setProxy(null);
		location.setCredentialsStore(new InMemoryCredentialsStore());
		location.setCredentials(AuthenticationType.REPOSITORY, new UserCredentials("wrong", "wrong"));
		BugzillaRestClient client;
		client = new BugzillaRestClient(location, connector);
		assertNotNull(client.getClient());
		thrown.expect(BugzillaRestException.class);
		thrown.expectMessage("Unauthorized");
		client.validate(new NullOperationMonitor());
	}

	@Test
	public void testNoUserValidate() throws BugzillaRestException {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl(actualFixture.getRepositoryUrl());
		location.setProxy(null);
		location.setCredentialsStore(new InMemoryCredentialsStore());
		location.setCredentials(AuthenticationType.REPOSITORY, null);
		BugzillaRestClient client;
		client = new BugzillaRestClient(location, connector);
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Authentication requested without valid credentials");
		client.validate(new NullOperationMonitor());
	}

	@Test
	public void testInvalidPasswordValidate() throws BugzillaRestException {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl(actualFixture.getRepositoryUrl());
		location.setProxy(null);
		location.setCredentialsStore(new InMemoryCredentialsStore());
		location.setCredentials(AuthenticationType.REPOSITORY, new UserCredentials("tests@mylyn.eclipse.org", "wrong"));
		BugzillaRestClient client;
		client = new BugzillaRestClient(location, connector);
		assertNotNull(client.getClient());
		thrown.expect(BugzillaRestException.class);
		thrown.expectMessage("Unauthorized");
		client.validate(new NullOperationMonitor());
	}

	@Test
	@ConditionalIgnoreRule.ConditionalIgnore(condition = MustRunOnApikeyRule.class)
	public void testApikeyValidate() throws BugzillaRestException {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl(actualFixture.getRepositoryUrl());
		location.setProxy(null);
		location.setCredentialsStore(new InMemoryCredentialsStore());
		location.setCredentials(AuthenticationType.REPOSITORY, new UserCredentials("tests@mylyn.eclipse.org", ""));
		location.setProperty(IBugzillaRestConstants.REPOSITORY_USE_API_KEY, Boolean.toString(true));
		location.setProperty(IBugzillaRestConstants.REPOSITORY_API_KEY, "wvkz2SoBMBQEKv6ishp1j7NY1R9l711g5w2afXc6");
		BugzillaRestClient client;
		client = new BugzillaRestClient(location, connector);
		assertNotNull(client.getClient());
		assertTrue(client.validate(new NullOperationMonitor()));
	}

	@Test
	@ConditionalIgnoreRule.ConditionalIgnore(condition = MustRunOnApikeyRule.class)
	public void testInvalidApikeyValidate() throws BugzillaRestException {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl(actualFixture.getRepositoryUrl());
		location.setProxy(null);
		location.setCredentialsStore(new InMemoryCredentialsStore());
		location.setCredentials(AuthenticationType.REPOSITORY, new UserCredentials("tests@mylyn.eclipse.org", ""));
		location.setProperty(IBugzillaRestConstants.REPOSITORY_USE_API_KEY, Boolean.toString(true));
		location.setProperty(IBugzillaRestConstants.REPOSITORY_API_KEY, "wvkz2SoBMBQEKv6ishp1j7NY1R9l711g5w2afXc8");
		BugzillaRestClient client;
		client = new BugzillaRestClient(location, connector);
		assertNotNull(client.getClient());
		thrown.expect(BugzillaRestException.class);
		thrown.expectMessage("The API key you specified is invalid");
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
				new Gson().toJson(parameter)
						.replaceAll(repository.getRepositoryUrl(), "http://dummy.url")
						.replaceAll(repository.getRepositoryUrl().replaceFirst("https://", "http://"),
								"http://dummy.url"));
		assertEquals(
				IOUtils.toString(
						CommonTestUtil.getResource(this, actualFixture.getTestDataFolder() + "/configuration.json")),
				new Gson().toJson(configuration)
						.replaceAll(repository.getRepositoryUrl(), "http://dummy.url")
						.replaceAll(repository.getRepositoryUrl().replaceFirst("https://", "http://"),
								"http://dummy.url"));
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
			connector.getClient(actualFixture.repository()).postTaskData(taskData, null, null);
			fail("never reach this!");
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
			connector.getClient(actualFixture.repository()).postTaskData(taskData, null, null);
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
		RepositoryResponse reposonse = connector.getClient(actualFixture.repository())
				.postTaskData(taskData, null, null);
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

		RepositoryResponse reposonse = connector.getClient(actualFixture.repository())
				.postTaskData(taskDataSubmit, null, null);
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
		TaskData taskData = harness.createTaskData(taskMappingInit, null, null);

		taskData.getRoot().getAttribute("cf_dropdown").setValue("one");
		taskData.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().TARGET_MILESTONE.getKey())
				.setValue("M2");
		String taskId = harness.submitNewTask(taskData);
		TaskData taskDataGet = harness.getTaskFromServer(taskId);

		// description is only for old tasks readonly and has the two sub attributes
		// COMMENT_NUMBER and COMMENT_ISPRIVATE
		TaskAttribute getDesc = taskDataGet.getRoot()
				.getAttribute(BugzillaRestTaskSchema.getDefault().DESCRIPTION.getKey());
		getDesc.getMetaData().setReadOnly(false);
		getDesc.removeAttribute(TaskAttribute.COMMENT_ISPRIVATE);
		getDesc.removeAttribute(TaskAttribute.COMMENT_NUMBER);

		// resolution is only for new tasks readonly
		taskData.getRoot()
				.getAttribute(BugzillaRestTaskSchema.getDefault().RESOLUTION.getKey())
				.getMetaData()
				.setReadOnly(false);

		// attributes we know that they can not be equal
		taskData.getRoot().removeAttribute(BugzillaRestTaskSchema.getDefault().STATUS.getKey());
		taskDataGet.getRoot().removeAttribute(BugzillaRestTaskSchema.getDefault().STATUS.getKey());
		taskData.getRoot().removeAttribute(BugzillaRestTaskSchema.getDefault().ASSIGNED_TO.getKey());
		taskDataGet.getRoot().removeAttribute(BugzillaRestTaskSchema.getDefault().ASSIGNED_TO.getKey());
		taskData.getRoot().removeAttribute(TaskAttribute.OPERATION);
		taskDataGet.getRoot().removeAttribute(TaskAttribute.OPERATION);
		taskDataGet.getRoot().removeAttribute(BugzillaRestTaskSchema.getDefault().DATE_MODIFICATION.getKey());
		taskData.getRoot().removeAttribute(BugzillaRestTaskSchema.getDefault().DATE_MODIFICATION.getKey());
		// CC attribute has diverences in the meta data between create and update
		taskData.getRoot().removeAttribute(TaskAttribute.USER_CC);
		taskDataGet.getRoot().removeAttribute(TaskAttribute.USER_CC);

		// attributes only in old tasks
		taskData.getRoot().removeAttribute("description_is_private");

		// attributes only in new tasks
		taskDataGet.getRoot().removeAttribute("bug_id");
		taskDataGet.getRoot().removeAttribute(TaskAttribute.COMMENT_NEW);
		taskDataGet.getRoot().removeAttribute("addCC");
		taskDataGet.getRoot().removeAttribute("removeCC");
		taskDataGet.getRoot().removeAttribute(BugzillaRestTaskSchema.getDefault().RESET_QA_CONTACT.getKey());
		taskDataGet.getRoot().removeAttribute(BugzillaRestTaskSchema.getDefault().RESET_ASSIGNED_TO.getKey());
		taskDataGet.getRoot().removeAttribute(BugzillaRestTaskSchema.getDefault().ADD_SELF_CC.getKey());
		ArrayList<TaskAttribute> flags = new ArrayList<>();
		for (TaskAttribute attribute : taskDataGet.getRoot().getAttributes().values()) {
			if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG_TYPE)) {
				flags.add(attribute);
			}
		}
		for (TaskAttribute taskAttribute : flags) {
			taskDataGet.getRoot().removeAttribute(taskAttribute.getId());
		}

		// attributes for operations
		taskDataGet.getRoot().removeAttribute("task.common.operation-CONFIRMED");
		taskDataGet.getRoot().removeAttribute("task.common.operation-IN_PROGRESS");
		taskDataGet.getRoot().removeAttribute("task.common.operation-RESOLVED");
		taskDataGet.getRoot().removeAttribute("resolutionInput");
		taskDataGet.getRoot().removeAttribute("task.common.operation-duplicate");
		taskDataGet.getRoot().removeAttribute(BugzillaRestTaskSchema.getDefault().DUPE_OF.getKey());

		assertEquals(taskData.getRoot().toString(), taskDataGet.getRoot().toString());
		assertEquals(
				IOUtils.toString(
						CommonTestUtil.getResource(this, actualFixture.getTestDataFolder() + "/taskDataFlags.txt")),
				flags.toString());
	}

	@Test
	public void testUpdateTaskData() throws Exception {
		String taskId = harness.getNewTaksId4TestProduct();
		TaskData taskDataGet = harness.getTaskFromServer(taskId);

		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();

		TaskAttribute attribute = taskDataGet.getRoot()
				.getMappedAttribute(BugzillaRestCreateTaskSchema.getDefault().PRODUCT.getKey());
		attribute.setValue("Product with Spaces");
		changed.add(attribute);
		attribute = taskDataGet.getRoot()
				.getMappedAttribute(BugzillaRestCreateTaskSchema.getDefault().COMPONENT.getKey());
		attribute.setValue("Component 1");
		changed.add(attribute);
		attribute = taskDataGet.getRoot()
				.getMappedAttribute(BugzillaRestCreateTaskSchema.getDefault().VERSION.getKey());
		attribute.setValue("b");
		changed.add(attribute);
		attribute = taskDataGet.getRoot()
				.getMappedAttribute(BugzillaRestCreateTaskSchema.getDefault().TARGET_MILESTONE.getKey());
		attribute.setValue("M3.0");
		changed.add(attribute);

		attribute = taskDataGet.getRoot().getAttribute("cf_dropdown");
		attribute.setValue("two");
		changed.add(attribute);
		attribute = taskDataGet.getRoot().getAttribute("cf_multiselect");
		attribute.setValues(Arrays.asList("Red", "Yellow"));
		changed.add(attribute);

		//Act
		RepositoryResponse reposonse = connector.getClient(actualFixture.repository())
				.postTaskData(taskDataGet, changed, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_UPDATED));
		//Assert
		TaskData taskDataUpdate = harness.getTaskFromServer(taskId);

		attribute = taskDataUpdate.getRoot()
				.getMappedAttribute(BugzillaRestCreateTaskSchema.getDefault().PRODUCT.getKey());
		assertThat(attribute.getValue(), is("Product with Spaces"));
		attribute = taskDataUpdate.getRoot()
				.getMappedAttribute(BugzillaRestCreateTaskSchema.getDefault().COMPONENT.getKey());
		assertThat(attribute.getValue(), is("Component 1"));
		attribute = taskDataUpdate.getRoot()
				.getMappedAttribute(BugzillaRestCreateTaskSchema.getDefault().VERSION.getKey());
		assertThat(attribute.getValue(), is("b"));
		attribute = taskDataUpdate.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().TARGET_MILESTONE.getKey());
		assertThat(attribute.getValue(), is("M3.0"));
		attribute = taskDataUpdate.getRoot().getAttribute("cf_dropdown");
		assertThat(attribute.getValue(), is("two"));
		attribute = taskDataUpdate.getRoot().getAttribute("cf_multiselect");
		assertThat(attribute.getValues(), is(Arrays.asList("Red", "Yellow")));
	}

	@Test
	public void testAddComment() throws Exception {
		String taskId = harness.getNewTaksId4TestProduct();
		TaskData taskDataGet = harness.getTaskFromServer(taskId);

		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();

		TaskAttribute attribute = taskDataGet.getRoot()
				.getAttribute(BugzillaRestTaskSchema.getDefault().NEW_COMMENT.getKey());
		attribute.setValue("The Comment");
		changed.add(attribute);

		//Act
		RepositoryResponse reposonse = connector.getClient(actualFixture.repository())
				.postTaskData(taskDataGet, changed, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_UPDATED));
		//Assert
		TaskData taskDataUpdate = harness.getTaskFromServer(taskId);

		attribute = taskDataUpdate.getRoot().getMappedAttribute(TaskAttribute.PREFIX_COMMENT + "1");
		assertNotNull(attribute);
		TaskAttribute commentAttribute = attribute.getMappedAttribute(TaskAttribute.COMMENT_TEXT);
		assertNotNull(commentAttribute);
		assertThat(commentAttribute.getValue(), is("The Comment"));
		commentAttribute = attribute.getMappedAttribute(TaskAttribute.COMMENT_NUMBER);
		assertNotNull(commentAttribute);
		assertThat(commentAttribute.getValue(), is("1"));
		commentAttribute = attribute.getMappedAttribute(TaskAttribute.COMMENT_ISPRIVATE);
		assertNotNull(commentAttribute);
		assertThat(commentAttribute.getValue(), is("false"));
	}

	@Test
	public void testGifAttachment() throws Exception {
		TaskAttribute attachmentAttribute = null;
		TaskRepository repository = actualFixture.repository();

		final TaskMapping taskMappingInit = new TaskMapping() {
			@Override
			public String getSummary() {
				return "Bug for Gif Attachment";
			}

			@Override
			public String getDescription() {
				return "The bug is used to test that gif attachments can be put and get correctly!";
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
		String taskId = harness.getNewTaksIdFromInitMapping(taskMappingInit, harness.taskInitializationData);
		TaskData taskData = harness.getTaskFromServer(taskId);
		assertNotNull(taskData);
		for (Entry<String, TaskAttribute> entry : taskData.getRoot().getAttributes().entrySet()) {
			if (TaskAttribute.TYPE_ATTACHMENT.equals(entry.getValue().getMetaData().getType())) {
				attachmentAttribute = entry.getValue();
			}
		}
		assertNull(attachmentAttribute);
		BugzillaRestTaskAttachmentHandler attachmentHandler = new BugzillaRestTaskAttachmentHandler(connector);
		ITask task = new TaskTask(actualFixture.repository().getConnectorKind(),
				actualFixture.repository().getRepositoryUrl(), taskId);
		File file = File.createTempFile("attachment", null);
		file.deleteOnExit();

		try (OutputStream out = new FileOutputStream(file);
				InputStream in = CommonTestUtil.getResource(this, "testdata/icons/bugzilla-logo.gif")) {
			IOUtils.copy(in, out);
		}
		FileTaskAttachmentSource attachment = new FileTaskAttachmentSource(file);
		attachment.setContentType("image/gif");
		attachment.setDescription("My Attachment 2");
		attachment.setName("Attachment 2.gif");
		attachmentHandler.postContent(repository, task, attachment, "comment", null, null);
		taskData = getTaskData(taskId);
		assertNotNull(taskData);
		for (Entry<String, TaskAttribute> entry : taskData.getRoot().getAttributes().entrySet()) {
			if (TaskAttribute.TYPE_ATTACHMENT.equals(entry.getValue().getMetaData().getType())) {
				attachmentAttribute = entry.getValue();
			}
		}
		assertNotNull(attachmentAttribute);
		InputStream instream = attachmentHandler.getContent(actualFixture.repository(), task, attachmentAttribute,
				null);
		InputStream instream2 = CommonTestUtil.getResource(this, "testdata/icons/bugzilla-logo.gif");
		assertTrue(IOUtils.contentEquals(instream, instream2));
	}

	@Test
	public void testTextAttachment() throws Exception {
		TaskAttribute attachmentAttribute = null;
		TaskRepository repository = actualFixture.repository();

		final TaskMapping taskMappingInit = new TaskMapping() {
			@Override
			public String getSummary() {
				return "Bug for Text Attachment";
			}

			@Override
			public String getDescription() {
				return "The bug is used to test that text attachments can be put and get correctly!";
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
		String taskId = harness.getNewTaksIdFromInitMapping(taskMappingInit, harness.taskInitializationData);
		TaskData taskData = harness.getTaskFromServer(taskId);
		assertNotNull(taskData);
		for (Entry<String, TaskAttribute> entry : taskData.getRoot().getAttributes().entrySet()) {
			if (TaskAttribute.TYPE_ATTACHMENT.equals(entry.getValue().getMetaData().getType())) {
				attachmentAttribute = entry.getValue();
			}
		}
		assertNull(attachmentAttribute);
		BugzillaRestTaskAttachmentHandler attachmentHandler = new BugzillaRestTaskAttachmentHandler(connector);
		ITask task = new TaskTask(actualFixture.repository().getConnectorKind(),
				actualFixture.repository().getRepositoryUrl(), taskId);
		File file = File.createTempFile("attachment", null);
		file.deleteOnExit();
		try (OutputStream out = new FileOutputStream(file);
				InputStream in = CommonTestUtil.getResource(this, "testdata/AttachmentTest.txt")) {
			IOUtils.copy(in, out);
		}

		FileTaskAttachmentSource attachment = new FileTaskAttachmentSource(file);
		attachment.setContentType("text/plain");
		attachment.setDescription("My Attachment 2");
		attachment.setName("Attachment 2.txt");
		attachmentHandler.postContent(repository, task, attachment, "comment", null, null);
		taskData = getTaskData(taskId);
		assertNotNull(taskData);
		for (Entry<String, TaskAttribute> entry : taskData.getRoot().getAttributes().entrySet()) {
			if (TaskAttribute.TYPE_ATTACHMENT.equals(entry.getValue().getMetaData().getType())) {
				attachmentAttribute = entry.getValue();
			}
		}
		assertNotNull(attachmentAttribute);
		InputStream instream = attachmentHandler.getContent(actualFixture.repository(), task, attachmentAttribute,
				null);
		InputStream instream2 = CommonTestUtil.getResource(this, "testdata/AttachmentTest.txt");
		assertTrue(IOUtils.contentEquals(instream, instream2));
	}

	private TaskData getTaskData(final String taskId) throws CoreException, BugzillaRestException {
		BugzillaRestClient client = connector.getClient(actualFixture.repository());
		final Map<String, TaskData> results = new HashMap<String, TaskData>();
		client.getTaskData(new HashSet<String>() {
			private static final long serialVersionUID = 1L;

			{
				add(taskId);
			}
		}, actualFixture.repository(), new TaskDataCollector() {

			@Override
			public void accept(TaskData taskData) {
				results.put(taskData.getTaskId(), taskData);
			}
		}, null);
		return results.get(taskId);
	}

	@Test
	public void testCreateCCAttribute() throws Exception {
		final TaskMapping taskMappingInit = new TaskMapping() {
			@Override
			public String getSummary() {
				return "Test CC Attribute";
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
		taskData.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().CC.getKey())
				.setValue("admin@mylyn.eclipse.org, tests@mylyn.eclipse.org");
		RepositoryResponse reposonse = connector.getClient(actualFixture.repository())
				.postTaskData(taskData, null, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_CREATED));
		TaskData taskDataUpdate = harness.getTaskFromServer(reposonse.getTaskId());
		TaskAttribute ccAttrib = taskDataUpdate.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().CC.getKey());
		assertEquals(2, ccAttrib.getValues().size());
		assertEquals("admin@mylyn.eclipse.org", ccAttrib.getValues().get(0));
		assertEquals("tests@mylyn.eclipse.org", ccAttrib.getValues().get(1));
	}

	@Test
	public void testCreateBlocksAttribute() throws Exception {
		final TaskMapping taskMappingInit = new TaskMapping() {
			@Override
			public String getSummary() {
				return "Test blocks Attribute";
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
		String[] taskIdRel = harness.getRelationTasks();
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(actualFixture.repository());
		TaskData taskData = new TaskData(mapper, actualFixture.repository().getConnectorKind(),
				actualFixture.repository().getRepositoryUrl(), "");
		taskDataHandler.initializeTaskData(actualFixture.repository(), taskData, taskMappingInit, null);
		taskData.getRoot().getAttribute("cf_dropdown").setValue("one");
		taskData.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().TARGET_MILESTONE.getKey())
				.setValue("M2");
		taskData.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().BLOCKS.getKey())
				.setValue(taskIdRel[0] + ", " + taskIdRel[1]);
		RepositoryResponse reposonse = connector.getClient(actualFixture.repository())
				.postTaskData(taskData, null, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_CREATED));
		TaskData taskDataUpdate = harness.getTaskFromServer(reposonse.getTaskId());
		TaskAttribute blocksAttrib = taskDataUpdate.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().BLOCKS.getKey());
		assertEquals(2, blocksAttrib.getValues().size());
		assertEquals(taskIdRel[0], blocksAttrib.getValues().get(0));
		assertEquals(taskIdRel[1], blocksAttrib.getValues().get(1));
	}

	@Test
	public void testCreateDependsOnAttribute() throws Exception {
		final TaskMapping taskMappingInit = new TaskMapping() {
			@Override
			public String getSummary() {
				return "Test depends_on Attribute";
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
		String[] taskIdRel = harness.getRelationTasks();
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(actualFixture.repository());
		TaskData taskData = new TaskData(mapper, actualFixture.repository().getConnectorKind(),
				actualFixture.repository().getRepositoryUrl(), "");
		taskDataHandler.initializeTaskData(actualFixture.repository(), taskData, taskMappingInit, null);
		taskData.getRoot().getAttribute("cf_dropdown").setValue("one");
		taskData.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().TARGET_MILESTONE.getKey())
				.setValue("M2");
		taskData.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().DEPENDS_ON.getKey())
				.setValue(taskIdRel[0] + ", " + taskIdRel[1]);
		RepositoryResponse reposonse = connector.getClient(actualFixture.repository())
				.postTaskData(taskData, null, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_CREATED));
		TaskData taskDataUpdate = harness.getTaskFromServer(reposonse.getTaskId());
		TaskAttribute dependsOnAttrib = taskDataUpdate.getRoot()
				.getAttribute(BugzillaRestTaskSchema.getDefault().DEPENDS_ON.getKey());
		assertEquals(2, dependsOnAttrib.getValues().size());
		assertEquals(taskIdRel[0], dependsOnAttrib.getValues().get(0));
		assertEquals(taskIdRel[1], dependsOnAttrib.getValues().get(1));
	}

	@Test
	public void testCCAttribute() throws Exception {
		String taskId = harness.getNewTaksId4TestProduct();
		TaskData taskDataGet = harness.getTaskFromServer(taskId);

		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();

		TaskAttribute attribute = taskDataGet.getRoot()
				.getAttribute(BugzillaRestTaskSchema.getDefault().ADD_CC.getKey());
		attribute.setValue("tests@mylyn.eclipse.org");
		changed.add(attribute);

		//Act
		RepositoryResponse reposonse = connector.getClient(actualFixture.repository())
				.postTaskData(taskDataGet, changed, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_UPDATED));
		//Assert
		TaskData taskDataUpdate = harness.getTaskFromServer(taskId);
		TaskAttribute ccAttrib = taskDataUpdate.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().CC.getKey());
		assertEquals(1, ccAttrib.getValues().size());
		assertEquals("tests@mylyn.eclipse.org", ccAttrib.getValues().get(0));

		TaskAttribute ccAddAttrib = taskDataUpdate.getRoot()
				.getAttribute(BugzillaRestTaskSchema.getDefault().ADD_CC.getKey());
		ccAddAttrib.setValue("admin@mylyn.eclipse.org");
		changed.add(ccAddAttrib);

		TaskAttribute ccRemoveAttrib = taskDataUpdate.getRoot()
				.getAttribute(BugzillaRestTaskSchema.getDefault().REMOVE_CC.getKey());
		ccRemoveAttrib.setValue("tests@mylyn.eclipse.org");
		changed.add(ccRemoveAttrib);

		//Act
		reposonse = connector.getClient(actualFixture.repository()).postTaskData(taskDataUpdate, changed, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_UPDATED));
		//Assert
		taskDataUpdate = harness.getTaskFromServer(taskId);
		ccAttrib = taskDataUpdate.getRoot().getAttribute(BugzillaRestCreateTaskSchema.getDefault().CC.getKey());
		assertEquals(1, ccAttrib.getValues().size());
		assertEquals("admin@mylyn.eclipse.org", ccAttrib.getValues().get(0));
	}

	@Test
	public void testBlocksAttributeV1() throws Exception {
		String taskId = harness.getNewTaksId4TestProduct();
		String[] taskIdRel = harness.getRelationTasks();

		TaskData taskDataGet = harness.getTaskFromServer(taskId);

		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		TaskData taskDataOld = TaskDataState.createCopy(taskDataGet);

		TaskAttribute attribute = taskDataGet.getRoot()
				.getAttribute(BugzillaRestTaskSchema.getDefault().BLOCKS.getKey());
		attribute.setValue(taskIdRel[0] + ", " + taskIdRel[1]);
		changed.add(taskDataOld.getRoot().getAttribute(BugzillaRestTaskSchema.getDefault().BLOCKS.getKey()));

		//Act
		RepositoryResponse reposonse = connector.getClient(actualFixture.repository())
				.postTaskData(taskDataGet, changed, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_UPDATED));
		//Assert
		TaskData taskDataUpdate = harness.getTaskFromServer(taskId);
		TaskAttribute blocksAttrib = taskDataUpdate.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().BLOCKS.getKey());
		assertEquals(2, blocksAttrib.getValues().size());
		assertEquals(taskIdRel[0], blocksAttrib.getValues().get(0));
		assertEquals(taskIdRel[1], blocksAttrib.getValues().get(1));
		changed.clear();
		changed.add(taskDataGet.getRoot().getAttribute(BugzillaRestTaskSchema.getDefault().BLOCKS.getKey()));

		blocksAttrib.setValue(taskIdRel[0] + ", " + taskIdRel[1] + ", " + taskIdRel[2]);

		//Act
		reposonse = connector.getClient(actualFixture.repository()).postTaskData(taskDataUpdate, changed, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_UPDATED));
		//Assert
		taskDataUpdate = harness.getTaskFromServer(taskId);
		blocksAttrib = taskDataUpdate.getRoot().getAttribute(BugzillaRestCreateTaskSchema.getDefault().BLOCKS.getKey());
		assertEquals(3, blocksAttrib.getValues().size());
		assertEquals(taskIdRel[0], blocksAttrib.getValues().get(0));
		assertEquals(taskIdRel[1], blocksAttrib.getValues().get(1));
		assertEquals(taskIdRel[2], blocksAttrib.getValues().get(2));
	}

	@Test
	public void testBlocksAttributeV2() throws Exception {
		String taskId = harness.getNewTaksId4TestProduct();
		String[] taskIdRel = harness.getRelationTasks();

		TaskData taskDataGet = harness.getTaskFromServer(taskId);

		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		TaskData taskDataOld = TaskDataState.createCopy(taskDataGet);

		TaskAttribute attribute = taskDataGet.getRoot()
				.getAttribute(BugzillaRestTaskSchema.getDefault().BLOCKS.getKey());
		attribute.setValue(taskIdRel[0]);
		attribute.addValue(taskIdRel[1]);
		changed.add(taskDataOld.getRoot().getAttribute(BugzillaRestTaskSchema.getDefault().BLOCKS.getKey()));

		//Act
		RepositoryResponse reposonse = connector.getClient(actualFixture.repository())
				.postTaskData(taskDataGet, changed, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_UPDATED));
		//Assert
		TaskData taskDataUpdate = harness.getTaskFromServer(taskId);
		TaskAttribute blocksAttrib = taskDataUpdate.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().BLOCKS.getKey());
		assertEquals(2, blocksAttrib.getValues().size());
		assertEquals(taskIdRel[0], blocksAttrib.getValues().get(0));
		assertEquals(taskIdRel[1], blocksAttrib.getValues().get(1));
		changed.clear();
		changed.add(taskDataGet.getRoot().getAttribute(BugzillaRestTaskSchema.getDefault().BLOCKS.getKey()));

		blocksAttrib.setValue(taskIdRel[0]);
		blocksAttrib.addValue(taskIdRel[1]);
		blocksAttrib.addValue(taskIdRel[2]);

		//Act
		reposonse = connector.getClient(actualFixture.repository()).postTaskData(taskDataUpdate, changed, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_UPDATED));
		//Assert
		taskDataUpdate = harness.getTaskFromServer(taskId);
		blocksAttrib = taskDataUpdate.getRoot().getAttribute(BugzillaRestCreateTaskSchema.getDefault().BLOCKS.getKey());
		assertEquals(3, blocksAttrib.getValues().size());
		assertEquals(taskIdRel[0], blocksAttrib.getValues().get(0));
		assertEquals(taskIdRel[1], blocksAttrib.getValues().get(1));
		assertEquals(taskIdRel[2], blocksAttrib.getValues().get(2));
	}

	@Test
	public void testDependsOnAttributeV1() throws Exception {
		String taskId = harness.getNewTaksId4TestProduct();
		String[] taskIdRel = harness.getRelationTasks();

		TaskData taskDataGet = harness.getTaskFromServer(taskId);

		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		TaskData taskDataOld = TaskDataState.createCopy(taskDataGet);

		TaskAttribute attribute = taskDataGet.getRoot()
				.getAttribute(BugzillaRestTaskSchema.getDefault().DEPENDS_ON.getKey());
		attribute.setValue(taskIdRel[0] + ", " + taskIdRel[1]);
		changed.add(taskDataOld.getRoot().getAttribute(BugzillaRestTaskSchema.getDefault().DEPENDS_ON.getKey()));

		//Act
		RepositoryResponse reposonse = connector.getClient(actualFixture.repository())
				.postTaskData(taskDataGet, changed, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_UPDATED));
		//Assert
		TaskData taskDataUpdate = harness.getTaskFromServer(taskId);
		TaskAttribute dependsOnAttrib = taskDataUpdate.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().DEPENDS_ON.getKey());
		assertEquals(2, dependsOnAttrib.getValues().size());
		assertEquals(taskIdRel[0], dependsOnAttrib.getValues().get(0));
		assertEquals(taskIdRel[1], dependsOnAttrib.getValues().get(1));
		changed.clear();
		changed.add(taskDataGet.getRoot().getAttribute(BugzillaRestTaskSchema.getDefault().DEPENDS_ON.getKey()));

		dependsOnAttrib.setValue(taskIdRel[0] + ", " + taskIdRel[1] + ", " + taskIdRel[2]);

		//Act
		reposonse = connector.getClient(actualFixture.repository()).postTaskData(taskDataUpdate, changed, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_UPDATED));
		//Assert
		taskDataUpdate = harness.getTaskFromServer(taskId);
		dependsOnAttrib = taskDataUpdate.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().DEPENDS_ON.getKey());
		assertEquals(3, dependsOnAttrib.getValues().size());
		assertEquals(taskIdRel[0], dependsOnAttrib.getValues().get(0));
		assertEquals(taskIdRel[1], dependsOnAttrib.getValues().get(1));
		assertEquals(taskIdRel[2], dependsOnAttrib.getValues().get(2));
	}

	@Test
	public void testDependsOnAttributeV2() throws Exception {
		String taskId = harness.getNewTaksId4TestProduct();
		String[] taskIdRel = harness.getRelationTasks();

		TaskData taskDataGet = harness.getTaskFromServer(taskId);

		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		TaskData taskDataOld = TaskDataState.createCopy(taskDataGet);

		TaskAttribute attribute = taskDataGet.getRoot()
				.getAttribute(BugzillaRestTaskSchema.getDefault().DEPENDS_ON.getKey());
		attribute.setValue(taskIdRel[0]);
		attribute.addValue(taskIdRel[1]);
		changed.add(taskDataOld.getRoot().getAttribute(BugzillaRestTaskSchema.getDefault().DEPENDS_ON.getKey()));

		//Act
		RepositoryResponse reposonse = connector.getClient(actualFixture.repository())
				.postTaskData(taskDataGet, changed, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_UPDATED));
		//Assert
		TaskData taskDataUpdate = harness.getTaskFromServer(taskId);
		TaskAttribute dependsOnAttrib = taskDataUpdate.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().DEPENDS_ON.getKey());
		assertEquals(2, dependsOnAttrib.getValues().size());
		assertEquals(taskIdRel[0], dependsOnAttrib.getValues().get(0));
		assertEquals(taskIdRel[1], dependsOnAttrib.getValues().get(1));
		changed.clear();
		changed.add(taskDataGet.getRoot().getAttribute(BugzillaRestTaskSchema.getDefault().DEPENDS_ON.getKey()));

		dependsOnAttrib.setValue(taskIdRel[0]);
		dependsOnAttrib.addValue(taskIdRel[1]);
		dependsOnAttrib.addValue(taskIdRel[2]);

		//Act
		reposonse = connector.getClient(actualFixture.repository()).postTaskData(taskDataUpdate, changed, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_UPDATED));
		//Assert
		taskDataUpdate = harness.getTaskFromServer(taskId);
		dependsOnAttrib = taskDataUpdate.getRoot()
				.getAttribute(BugzillaRestCreateTaskSchema.getDefault().DEPENDS_ON.getKey());
		assertEquals(3, dependsOnAttrib.getValues().size());
		assertEquals(taskIdRel[0], dependsOnAttrib.getValues().get(0));
		assertEquals(taskIdRel[1], dependsOnAttrib.getValues().get(1));
		assertEquals(taskIdRel[2], dependsOnAttrib.getValues().get(2));
	}

	@Test
	public void testFlagsSet() throws Exception {
		String taskId = harness.getNewTaksId4TestProduct();

		TaskData taskDataGet = harness.getTaskFromServer(taskId);

		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		TaskData taskDataOld = TaskDataState.createCopy(taskDataGet);
		for (TaskAttribute attribute : taskDataGet.getRoot().getAttributes().values()) {
			if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG_TYPE)) {
				boolean found;
				TaskAttribute state = attribute.getTaskData().getAttributeMapper().getAssoctiatedAttribute(attribute);
				if (state != null) {
					switch (state.getMetaData().getLabel()) {
					case "BugFlag1":
						state.setValue("-");
						found = true;
						break;
					case "BugFlag2":
						state.setValue("?");
						found = true;
						break;
					case "BugFlag3":
						state.setValue("+");
						found = true;
						break;
					case "BugFlag4":
						state.setValue("?");
						attribute.getAttribute("requestee").setValue("admin@mylyn.eclipse.org");
						found = true;
						break;
					default:
						found = false;
						break;
					}
					if (found) {
						changed.add(taskDataOld.getRoot().getAttribute(attribute.getId()));
					}
				}
			}
		}

		//Act
		RepositoryResponse reposonse = connector.getClient(actualFixture.repository())
				.postTaskData(taskDataGet, changed, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_UPDATED));
		//Assert
		TaskData taskDataUpdate = harness.getTaskFromServer(taskId);
		int flagcount = 0;
		for (TaskAttribute attribute : taskDataUpdate.getRoot().getAttributes().values()) {
			if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG)) {
				flagcount++;
				TaskAttribute state = attribute.getTaskData().getAttributeMapper().getAssoctiatedAttribute(attribute);
				if (state != null) {
					switch (state.getMetaData().getLabel()) {
					case "BugFlag1":
						assertEquals("-", state.getValue());
						assertEquals("[, -, +]", state.getOptions().values().toString());
						assertEquals("1", attribute.getAttribute("typeId").getValue());
						assertEquals("", attribute.getAttribute("requestee").getValue());
						assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
						assertEquals(attribute.getAttribute("creationDate").getValue(),
								attribute.getAttribute("modificationDate").getValue());
						break;
					case "BugFlag2":
						assertEquals("?", state.getValue());
						assertEquals("[, ?, -, +]", state.getOptions().values().toString());
						assertEquals("2", attribute.getAttribute("typeId").getValue());
						assertEquals("", attribute.getAttribute("requestee").getValue());
						assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
						assertEquals(attribute.getAttribute("creationDate").getValue(),
								attribute.getAttribute("modificationDate").getValue());
						break;
					case "BugFlag3":
						if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG_TYPE)) {
							assertEquals(" ", state.getValue());
							assertEquals("", attribute.getAttribute("setter").getValue());
							assertNull(attribute.getAttribute("creationDate"));
							assertNull(attribute.getAttribute("modificationDate"));
						} else {
							assertEquals("+", state.getValue());
							assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
							assertEquals(attribute.getAttribute("creationDate").getValue(),
									attribute.getAttribute("modificationDate").getValue());
						}
						assertEquals("[, ?, -, +]", state.getOptions().values().toString());
						assertEquals("5", attribute.getAttribute("typeId").getValue());
						assertEquals("", attribute.getAttribute("requestee").getValue());
						break;
					case "BugFlag4":
						if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG_TYPE)) {
							assertEquals(" ", state.getValue());
							assertEquals("", attribute.getAttribute("setter").getValue());
							assertEquals("", attribute.getAttribute("requestee").getValue());
							assertNull(attribute.getAttribute("creationDate"));
							assertNull(attribute.getAttribute("modificationDate"));
						} else {
							assertEquals("?", state.getValue());
							assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
							assertEquals("admin@mylyn.eclipse.org", attribute.getAttribute("requestee").getValue());
							assertEquals(attribute.getAttribute("creationDate").getValue(),
									attribute.getAttribute("modificationDate").getValue());
						}
						assertEquals("[, ?, -, +]", state.getOptions().values().toString());
						assertEquals("6", attribute.getAttribute("typeId").getValue());
						break;
					default:
						fail("No flag with name " + state.getMetaData().getLabel());
						break;
					}
				}
			}
		}
		assertEquals(6, flagcount);
	}

	@Test
	public void testFlagsReset() throws Exception {
		String taskId = harness.getNewTaksId4TestProduct();

		TaskData taskDataGet = harness.getTaskFromServer(taskId);

		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		TaskData taskDataOld = TaskDataState.createCopy(taskDataGet);
		for (TaskAttribute attribute : taskDataGet.getRoot().getAttributes().values()) {
			if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG_TYPE)) {
				boolean found;
				TaskAttribute state = attribute.getTaskData().getAttributeMapper().getAssoctiatedAttribute(attribute);
				if (state != null) {
					switch (state.getMetaData().getLabel()) {
					case "BugFlag1":
						state.setValue("-");
						found = true;
						break;
					case "BugFlag2":
						state.setValue("?");
						found = true;
						break;
					case "BugFlag3":
						state.setValue("+");
						found = true;
						break;
					case "BugFlag4":
						state.setValue("?");
						attribute.getAttribute("requestee").setValue("admin@mylyn.eclipse.org");
						found = true;
						break;
					default:
						found = false;
						break;
					}
					if (found) {
						changed.add(taskDataOld.getRoot().getAttribute(attribute.getId()));
					}
				}
			}
		}

		RepositoryResponse reposonse = connector.getClient(actualFixture.repository())
				.postTaskData(taskDataGet, changed, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_UPDATED));
		TaskData taskDataUpdate = harness.getTaskFromServer(taskId);
		Set<TaskAttribute> changedUpdate = new HashSet<TaskAttribute>();
		TaskData taskDataOldUpdate = TaskDataState.createCopy(taskDataUpdate);
		for (TaskAttribute attribute : taskDataUpdate.getRoot().getAttributes().values()) {
			if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG)) {
				TaskAttribute state = attribute.getTaskData().getAttributeMapper().getAssoctiatedAttribute(attribute);
				if (state != null) {
					boolean found;
					switch (state.getMetaData().getLabel()) {
					case "BugFlag1":
						assertEquals("-", state.getValue());
						assertEquals("[, -, +]", state.getOptions().values().toString());
						assertEquals("1", attribute.getAttribute("typeId").getValue());
						assertEquals("", attribute.getAttribute("requestee").getValue());
						assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
						assertEquals(attribute.getAttribute("creationDate").getValue(),
								attribute.getAttribute("modificationDate").getValue());
						state.setValue(" ");
						found = true;
						break;
					case "BugFlag2":
						assertEquals("?", state.getValue());
						assertEquals("[, ?, -, +]", state.getOptions().values().toString());
						assertEquals("2", attribute.getAttribute("typeId").getValue());
						assertEquals("", attribute.getAttribute("requestee").getValue());
						assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
						assertEquals(attribute.getAttribute("creationDate").getValue(),
								attribute.getAttribute("modificationDate").getValue());
						state.setValue(" ");
						found = true;
						break;
					case "BugFlag3":
						if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG_TYPE)) {
							assertEquals(" ", state.getValue());
							assertEquals("", attribute.getAttribute("setter").getValue());
							assertNull(attribute.getAttribute("creationDate"));
							assertNull(attribute.getAttribute("modificationDate"));
						} else {
							assertEquals("+", state.getValue());
							assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
							assertEquals(attribute.getAttribute("creationDate").getValue(),
									attribute.getAttribute("modificationDate").getValue());
							state.setValue(" ");
						}
						assertEquals("[, ?, -, +]", state.getOptions().values().toString());
						assertEquals("5", attribute.getAttribute("typeId").getValue());
						found = true;
						assertEquals("", attribute.getAttribute("requestee").getValue());
						break;
					case "BugFlag4":
						if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG_TYPE)) {
							assertEquals(" ", state.getValue());
							assertEquals("", attribute.getAttribute("setter").getValue());
							assertEquals("", attribute.getAttribute("requestee").getValue());
							assertNull(attribute.getAttribute("creationDate"));
							assertNull(attribute.getAttribute("modificationDate"));
						} else {
							assertEquals("?", state.getValue());
							assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
							assertEquals("admin@mylyn.eclipse.org", attribute.getAttribute("requestee").getValue());
							assertEquals(attribute.getAttribute("creationDate").getValue(),
									attribute.getAttribute("modificationDate").getValue());
							state.setValue(" ");
						}
						assertEquals("[, ?, -, +]", state.getOptions().values().toString());
						assertEquals("6", attribute.getAttribute("typeId").getValue());
						found = true;
						break;
					default:
						fail("No flag with name " + state.getMetaData().getLabel());
						found = false;
						break;
					}
					if (found) {
						changedUpdate.add(taskDataOldUpdate.getRoot().getAttribute(attribute.getId()));
					}
				}
			}
		}
		//Act
		reposonse = connector.getClient(actualFixture.repository()).postTaskData(taskDataUpdate, changedUpdate, null);
		//Assert
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_UPDATED));
		taskDataUpdate = harness.getTaskFromServer(taskId);
		int flagcount = 0;
		for (TaskAttribute attribute : taskDataUpdate.getRoot().getAttributes().values()) {
			if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG)) {
				flagcount++;
				TaskAttribute state = attribute.getTaskData().getAttributeMapper().getAssoctiatedAttribute(attribute);
				if (state != null) {
					switch (state.getMetaData().getLabel()) {
					case "BugFlag1":
						assertEquals(IBugzillaRestConstants.KIND_FLAG_TYPE + "1", attribute.getId());
						assertEquals(" ", state.getValue());
						assertEquals("[, -, +]", state.getOptions().values().toString());
						assertEquals("1", attribute.getAttribute("typeId").getValue());
						assertEquals("", attribute.getAttribute("requestee").getValue());
						assertEquals("", attribute.getAttribute("setter").getValue());
						assertNull(attribute.getAttribute("creationDate"));
						assertNull(attribute.getAttribute("modificationDate"));
						break;
					case "BugFlag2":
						assertEquals(IBugzillaRestConstants.KIND_FLAG_TYPE + "2", attribute.getId());
						assertEquals(" ", state.getValue());
						assertEquals("[, ?, -, +]", state.getOptions().values().toString());
						assertEquals("2", attribute.getAttribute("typeId").getValue());
						assertEquals("", attribute.getAttribute("requestee").getValue());
						assertEquals("", attribute.getAttribute("setter").getValue());
						assertNull(attribute.getAttribute("creationDate"));
						assertNull(attribute.getAttribute("modificationDate"));
						break;
					case "BugFlag3":
						assertEquals(IBugzillaRestConstants.KIND_FLAG_TYPE + "5", attribute.getId());
						assertEquals(" ", state.getValue());
						assertEquals("", attribute.getAttribute("setter").getValue());
						assertNull(attribute.getAttribute("creationDate"));
						assertNull(attribute.getAttribute("modificationDate"));
						assertEquals("[, ?, -, +]", state.getOptions().values().toString());
						assertEquals("5", attribute.getAttribute("typeId").getValue());
						assertEquals("", attribute.getAttribute("requestee").getValue());
						break;
					case "BugFlag4":
						assertEquals(IBugzillaRestConstants.KIND_FLAG_TYPE + "6", attribute.getId());
						assertEquals(" ", state.getValue());
						assertEquals("", attribute.getAttribute("setter").getValue());
						assertEquals("", attribute.getAttribute("requestee").getValue());
						assertNull(attribute.getAttribute("creationDate"));
						assertNull(attribute.getAttribute("modificationDate"));
						assertEquals("[, ?, -, +]", state.getOptions().values().toString());
						assertEquals("6", attribute.getAttribute("typeId").getValue());
						break;
					default:
						fail("No flag with name " + state.getMetaData().getLabel());
						break;
					}
				}
			}
		}
		assertEquals(4, flagcount);
	}

	@Test
	public void testFlagsChange() throws Exception {
		String taskId = harness.getNewTaksId4TestProduct();

		TaskData taskDataGet = harness.getTaskFromServer(taskId);

		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		TaskData taskDataOld = TaskDataState.createCopy(taskDataGet);
		for (TaskAttribute attribute : taskDataGet.getRoot().getAttributes().values()) {
			if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG_TYPE)) {
				boolean found;
				TaskAttribute state = attribute.getTaskData().getAttributeMapper().getAssoctiatedAttribute(attribute);
				if (state != null) {
					switch (state.getMetaData().getLabel()) {
					case "BugFlag1":
						state.setValue("-");
						found = true;
						break;
					case "BugFlag2":
						state.setValue("?");
						found = true;
						break;
					case "BugFlag3":
						state.setValue("+");
						found = true;
						break;
					case "BugFlag4":
						state.setValue("?");
						attribute.getAttribute("requestee").setValue("admin@mylyn.eclipse.org");
						found = true;
						break;
					default:
						found = false;
						break;
					}
					if (found) {
						changed.add(taskDataOld.getRoot().getAttribute(attribute.getId()));
					}
				}
			}
		}

		RepositoryResponse reposonse = connector.getClient(actualFixture.repository())
				.postTaskData(taskDataGet, changed, null);
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_UPDATED));
		TaskData taskDataUpdate = harness.getTaskFromServer(taskId);
		Set<TaskAttribute> changedUpdate = new HashSet<TaskAttribute>();
		TaskData taskDataOldUpdate = TaskDataState.createCopy(taskDataUpdate);
		for (TaskAttribute attribute : taskDataUpdate.getRoot().getAttributes().values()) {
			if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG)) {
				TaskAttribute state = attribute.getTaskData().getAttributeMapper().getAssoctiatedAttribute(attribute);
				if (state != null) {
					boolean found;
					switch (state.getMetaData().getLabel()) {
					case "BugFlag1":
						assertEquals("-", state.getValue());
						assertEquals("[, -, +]", state.getOptions().values().toString());
						assertEquals("1", attribute.getAttribute("typeId").getValue());
						assertEquals("", attribute.getAttribute("requestee").getValue());
						assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
						assertEquals(attribute.getAttribute("creationDate").getValue(),
								attribute.getAttribute("modificationDate").getValue());
						state.setValue("+");
						found = true;
						break;
					case "BugFlag2":
						assertEquals("?", state.getValue());
						assertEquals("[, ?, -, +]", state.getOptions().values().toString());
						assertEquals("2", attribute.getAttribute("typeId").getValue());
						assertEquals("", attribute.getAttribute("requestee").getValue());
						assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
						assertEquals(attribute.getAttribute("creationDate").getValue(),
								attribute.getAttribute("modificationDate").getValue());
						state.setValue("-");
						found = true;
						break;
					case "BugFlag3":
						if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG_TYPE)) {
							assertEquals(" ", state.getValue());
							assertEquals("", attribute.getAttribute("setter").getValue());
							assertNull(attribute.getAttribute("creationDate"));
							assertNull(attribute.getAttribute("modificationDate"));
						} else {
							assertEquals("+", state.getValue());
							assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
							assertEquals(attribute.getAttribute("creationDate").getValue(),
									attribute.getAttribute("modificationDate").getValue());
							state.setValue("-");
						}
						assertEquals("[, ?, -, +]", state.getOptions().values().toString());
						assertEquals("5", attribute.getAttribute("typeId").getValue());
						found = true;
						assertEquals("", attribute.getAttribute("requestee").getValue());
						break;
					case "BugFlag4":
						if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG_TYPE)) {
							assertEquals(" ", state.getValue());
							assertEquals("", attribute.getAttribute("setter").getValue());
							assertEquals("", attribute.getAttribute("requestee").getValue());
							assertNull(attribute.getAttribute("creationDate"));
							assertNull(attribute.getAttribute("modificationDate"));
						} else {
							assertEquals("?", state.getValue());
							assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
							assertEquals("admin@mylyn.eclipse.org", attribute.getAttribute("requestee").getValue());
							assertEquals(attribute.getAttribute("creationDate").getValue(),
									attribute.getAttribute("modificationDate").getValue());
							state.setValue("+");
						}
						assertEquals("[, ?, -, +]", state.getOptions().values().toString());
						assertEquals("6", attribute.getAttribute("typeId").getValue());
						found = true;
						break;
					default:
						fail("No flag with name " + state.getMetaData().getLabel());
						found = false;
						break;
					}
					if (found) {
						changedUpdate.add(taskDataOldUpdate.getRoot().getAttribute(attribute.getId()));
					}
				}
			}
		}
		//Act
		reposonse = connector.getClient(actualFixture.repository()).postTaskData(taskDataUpdate, changedUpdate, null);
		//Assert
		assertNotNull(reposonse);
		assertNotNull(reposonse.getReposonseKind());
		assertThat(reposonse.getReposonseKind(), is(ResponseKind.TASK_UPDATED));
		taskDataUpdate = harness.getTaskFromServer(taskId);
		int flagcount = 0;
		for (TaskAttribute attribute : taskDataUpdate.getRoot().getAttributes().values()) {
			if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG)) {
				flagcount++;
				TaskAttribute state = attribute.getTaskData().getAttributeMapper().getAssoctiatedAttribute(attribute);
				if (state != null) {
					switch (state.getMetaData().getLabel()) {
					case "BugFlag1":
						assertEquals("+", state.getValue());
						assertEquals("[, -, +]", state.getOptions().values().toString());
						assertEquals("1", attribute.getAttribute("typeId").getValue());
						assertEquals("", attribute.getAttribute("requestee").getValue());
						assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
						assertThat(attribute.getAttribute("modificationDate").getValue(),
								greaterThan(attribute.getAttribute("creationDate").getValue()));
						break;
					case "BugFlag2":
						assertEquals("-", state.getValue());
						assertEquals("[, ?, -, +]", state.getOptions().values().toString());
						assertEquals("2", attribute.getAttribute("typeId").getValue());
						assertEquals("", attribute.getAttribute("requestee").getValue());
						assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
						assertThat(attribute.getAttribute("modificationDate").getValue(),
								greaterThan(attribute.getAttribute("creationDate").getValue()));
						break;
					case "BugFlag3":
						if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG_TYPE)) {
							assertEquals(" ", state.getValue());
							assertEquals("", attribute.getAttribute("setter").getValue());
							assertNull(attribute.getAttribute("creationDate"));
							assertNull(attribute.getAttribute("modificationDate"));
						} else {
							assertEquals("-", state.getValue());
							assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
							assertThat(attribute.getAttribute("modificationDate").getValue(),
									greaterThan(attribute.getAttribute("creationDate").getValue()));
						}
						assertEquals("[, ?, -, +]", state.getOptions().values().toString());
						assertEquals("5", attribute.getAttribute("typeId").getValue());
						assertEquals("", attribute.getAttribute("requestee").getValue());
						break;
					case "BugFlag4":
						if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG_TYPE)) {
							assertEquals(" ", state.getValue());
							assertEquals("", attribute.getAttribute("setter").getValue());
							assertEquals("", attribute.getAttribute("requestee").getValue());
							assertNull(attribute.getAttribute("creationDate"));
							assertNull(attribute.getAttribute("modificationDate"));
						} else {
							assertEquals("+", state.getValue());
							assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
							assertEquals("", attribute.getAttribute("requestee").getValue());
							assertThat(attribute.getAttribute("modificationDate").getValue(),
									greaterThan(attribute.getAttribute("creationDate").getValue()));
						}
						assertEquals("[, ?, -, +]", state.getOptions().values().toString());
						assertEquals("6", attribute.getAttribute("typeId").getValue());
						break;
					default:
						fail("No flag with name " + state.getMetaData().getLabel());
						break;
					}
				}
			}
		}
		assertEquals(6, flagcount);
	}

	@Test
	public void testTextAttachmentWithFlags() throws Exception {
		TaskAttribute attachmentAttribute = null;
		TaskRepository repository = actualFixture.repository();

		final TaskMapping taskMappingInit = new TaskMapping() {
			@Override
			public String getSummary() {
				return "Bug for Text Attachment with Flags";
			}

			@Override
			public String getDescription() {
				return "The bug is used to test that text attachments with flags can be put and get correctly!";
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

		String taskId = harness.getNewTaksIdFromInitMapping(taskMappingInit, harness.taskInitializationData);
		TaskData taskData = harness.getTaskFromServer(taskId);
		assertNotNull(taskData);

		for (Entry<String, TaskAttribute> entry : taskData.getRoot().getAttributes().entrySet()) {
			if (TaskAttribute.TYPE_ATTACHMENT.equals(entry.getValue().getMetaData().getType())) {
				attachmentAttribute = entry.getValue();
			}
		}
		assertNull(attachmentAttribute);

		BugzillaRestTaskAttachmentHandler attachmentHandler = new BugzillaRestTaskAttachmentHandler(connector);
		ITask task = new TaskTask(actualFixture.repository().getConnectorKind(),
				actualFixture.repository().getRepositoryUrl(), taskId);

		File file = File.createTempFile("attachment", null);
		file.deleteOnExit();
		try (OutputStream out = new FileOutputStream(file);
				InputStream in = CommonTestUtil.getResource(this, "testdata/AttachmentTest.txt")) {
			IOUtils.copy(in, out);
		}

		FileTaskAttachmentSource attachment = new FileTaskAttachmentSource(file);
		attachment.setContentType("text/plain");
		attachment.setDescription("My Attachment 2");
		attachment.setName("Attachment 2.txt");

		attachmentAttribute = taskData.getRoot().createMappedAttribute(TaskAttribute.NEW_ATTACHMENT);

		BugzillaRestAttachmentMapper attachmentMapper = BugzillaRestAttachmentMapper.createFrom(attachmentAttribute);
		BugzillaRestAttachmentMapper.createFrom(attachmentAttribute);
		attachmentMapper.setContentType("text/plain");
		attachmentMapper.setDescription("My Attachment 2");
		attachmentMapper.setFileName("Attachment 2.txt");
		attachmentMapper.applyTo(attachmentAttribute);
		attachmentMapper.addMissingFlags(attachmentAttribute);
		TaskAttribute flag3 = attachmentAttribute.getAttribute(IBugzillaRestConstants.KIND_FLAG_TYPE + "3");
		TaskAttribute state3 = attachmentAttribute.getTaskData().getAttributeMapper().getAssoctiatedAttribute(flag3);
		state3.setValue("+");
		TaskAttribute flag4 = attachmentAttribute.getAttribute(IBugzillaRestConstants.KIND_FLAG_TYPE + "4");
		TaskAttribute state4 = attachmentAttribute.getTaskData().getAttributeMapper().getAssoctiatedAttribute(flag4);
		state4.setValue("-");

		attachmentHandler.postContent(repository, task, attachment, "comment", attachmentAttribute, null);
		taskData = getTaskData(taskId);
		assertNotNull(taskData);
		for (Entry<String, TaskAttribute> entry : taskData.getRoot().getAttributes().entrySet()) {
			if (TaskAttribute.TYPE_ATTACHMENT.equals(entry.getValue().getMetaData().getType())) {
				attachmentAttribute = entry.getValue();
			}
		}
		assertNotNull(attachmentAttribute);
		int flagcount = 0;
		for (TaskAttribute attribute : attachmentAttribute.getAttributes().values()) {
			if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG)) {
				flagcount++;
				TaskAttribute state = attribute.getTaskData().getAttributeMapper().getAssoctiatedAttribute(attribute);
				if (state != null) {
					switch (state.getMetaData().getLabel()) {
					case "AttachmentFlag1":
						if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG_TYPE)) {
							assertEquals(" ", state.getValue());
							assertEquals("[, ?, -, +]", state.getOptions().values().toString());
							assertEquals("3", attribute.getAttribute("typeId").getValue());
							assertEquals("", attribute.getAttribute("requestee").getValue());
						} else {
							assertEquals("+", state.getValue());
							assertEquals("[, ?, -, +]", state.getOptions().values().toString());
							assertEquals("3", attribute.getAttribute("typeId").getValue());
							assertEquals("", attribute.getAttribute("requestee").getValue());
							assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
						}
						break;
					case "AttachmentFlag2":
						if (attribute.getId().startsWith(IBugzillaRestConstants.KIND_FLAG_TYPE)) {
							assertEquals(" ", state.getValue());
							assertEquals("[, ?, -, +]", state.getOptions().values().toString());
							assertEquals("4", attribute.getAttribute("typeId").getValue());
							assertEquals("", attribute.getAttribute("requestee").getValue());
						} else {
							assertEquals("-", state.getValue());
							assertEquals("[, ?, -, +]", state.getOptions().values().toString());
							assertEquals("4", attribute.getAttribute("typeId").getValue());
							assertEquals("", attribute.getAttribute("requestee").getValue());
							assertEquals("tests@mylyn.eclipse.org", attribute.getAttribute("setter").getValue());
						}
						break;
					default:
						fail("No flag with name " + state.getMetaData().getLabel());
						break;
					}
				}
			}
		}
		assertEquals(4, flagcount);
		InputStream instream = attachmentHandler.getContent(actualFixture.repository(), task, attachmentAttribute,
				null);
		InputStream instream2 = CommonTestUtil.getResource(this, "testdata/AttachmentTest.txt");
		assertTrue(IOUtils.contentEquals(instream, instream2));
	}
}
