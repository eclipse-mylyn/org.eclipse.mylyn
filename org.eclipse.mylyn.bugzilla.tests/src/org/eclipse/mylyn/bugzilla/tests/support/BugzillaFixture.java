/*******************************************************************************
 * Copyright (c) 2009, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.AssertionFailedError;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.commons.sdk.util.FixtureConfiguration;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClientManager;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tests.util.TestFixture;

/**
 * @author Steffen Pingel
 * @author Thomas Ehrnhoefer
 * @author Frank Becker
 */
public class BugzillaFixture extends TestFixture {

	public static final String CUSTOM_WF = "Custom Workflow";

	public static final String CUSTOM_WF_AND_STATUS = "Custom Workflow and Status";

	private static BugzillaFixture current;

	private final String version;

	private final BugzillaVersion bugzillaVersion;

	private final Map<String, String> properties;

	private final Map<String, String> defaultproperties = new HashMap<String, String>() {
		private static final long serialVersionUID = 6995247925138693239L;

		{
			put("xmlrpc_enabled", "true");
		}
	};

	private BugzillaRepositoryConnector connector;

	public BugzillaFixture(FixtureConfiguration config) {
		super(BugzillaCorePlugin.CONNECTOR_KIND, config.getUrl());
		this.version = config.getVersion();
		this.bugzillaVersion = new BugzillaVersion(version);
		setInfo("Bugzilla", version, config.getInfo());
		this.properties = config.getProperties();
	}

	public BugzillaFixture(String url, String version, String info) {
		super(BugzillaCorePlugin.CONNECTOR_KIND, url);
		this.version = version;
		this.bugzillaVersion = new BugzillaVersion(version);
		setInfo("Bugzilla", version, info);
		this.properties = null;
	}

	public BugzillaVersion getBugzillaVersion() {
		return bugzillaVersion;
	}

	public static void cleanup010() throws Exception {
	}

	public static BugzillaFixture current() {
		if (current == null) {
			current = TestConfiguration.getDefault().discoverDefault(BugzillaFixture.class, "bugzilla");
			current.activate();
		}
		return current;
	}

	@Override
	public BugzillaFixture activate() {
		current = this;
		setUpFramework();
		return this;
	}

	@Override
	protected TestFixture getDefault() {
		return TestConfiguration.getDefault().discoverDefault(BugzillaFixture.class, "bugzilla");
	}

	public String getVersion() {
		return version;
	}

	public BugzillaClient client() throws CoreException, IOException {
		UserCredentials credentials = CommonTestUtil.getCredentials(PrivilegeLevel.USER);
		return client(getRepositoryUrl(), credentials.getUserName(), credentials.getPassword(), "", "", "UTF-8");
	}

	public BugzillaClient client(String hostUrl, String username, String password, String htAuthUser,
			String htAuthPass, String encoding) throws CoreException, IOException {
		WebLocation location = new WebLocation(hostUrl);
		location.setCredentials(AuthenticationType.REPOSITORY, username, password);
		location.setCredentials(AuthenticationType.HTTP, htAuthUser, htAuthPass);
		return client(location, encoding);

	}

	public BugzillaClient client(AbstractWebLocation location, String encoding) throws CoreException {

		TaskRepository taskRepository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, location.getUrl());
		if (getDsciptorfile() != null) {
			String filepath = "testdata/repository/" + getDsciptorfile();
			try {
				File file = BugzillaFixture.getFile(filepath);
				if (file != null) {
					taskRepository.setProperty(IBugzillaConstants.BUGZILLA_DESCRIPTOR_FILE, file.getCanonicalPath());
				}
			} catch (AssertionFailedError a) {
				// ignore the Exception. The BUGZILLA_DESCRIPTOR_FILE does not exist so the property is null
			} catch (IOException e) {
				// ignore the Exception. The BUGZILLA_DESCRIPTOR_FILE does not exist so the property is null
			}
		}

		taskRepository.setCredentials(AuthenticationType.REPOSITORY,
				location.getCredentials(AuthenticationType.REPOSITORY), false);

		taskRepository.setCredentials(AuthenticationType.HTTP, location.getCredentials(AuthenticationType.HTTP), false);
		taskRepository.setCharacterEncoding(encoding);
		taskRepository.setProperty(IBugzillaConstants.BUGZILLA_USE_XMLRPC, getProperty("xmlrpc_enabled"));

		connector = new BugzillaRepositoryConnector();
		BugzillaClientManager bugzillaClientManager = connector.getClientManager();
		BugzillaClient client = bugzillaClientManager.getClient(taskRepository, null);

		connector.getRepositoryConfiguration(taskRepository, false, new NullProgressMonitor());
		connector.writeRepositoryConfigFile();
		return client;
	}

	public BugzillaClient client(PrivilegeLevel level) throws Exception {
		AbstractWebLocation location = location(level);
		return client(location, "UTF-8");
	}

	public static File getFile(String filename) throws IOException {
		return CommonTestUtil.getFile(BugzillaFixture.class, filename);
	}

	public static InputStream getResource(String filename) throws IOException {
		return CommonTestUtil.getResource(BugzillaFixture.class, filename);
	}

	/**
	 * Create and returns a minimal task.
	 * 
	 * @param summary
	 *            may be <code>null</code>
	 * @param description
	 *            may be <code>null</code>
	 * @return The taskData retrieved from updating the task
	 */
	public TaskData createTask(PrivilegeLevel level, String summary, String description) throws Exception {
		final String summaryNotNull = summary != null ? summary : "summary";
		final String descriptionNotNull = description != null ? description : "description";
		return createTask(level, new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put(TaskAttribute.SUMMARY, summaryNotNull);
				put(TaskAttribute.DESCRIPTION, descriptionNotNull);
				put(BugzillaAttribute.OP_SYS.getKey(), "All");
				put(BugzillaAttribute.REP_PLATFORM.getKey(), "All");
				put(BugzillaAttribute.VERSION.getKey(), "unspecified");
			}
		});
	}

	public TaskData createTask(PrivilegeLevel level, Map<String, String> additionalAttributeValues) throws Exception {
		Assert.isLegal(additionalAttributeValues.containsKey(TaskAttribute.SUMMARY), "need value for Summary");
		Assert.isLegal(additionalAttributeValues.containsKey(TaskAttribute.DESCRIPTION), "need value for Description");
		ITaskMapping initializationData = new TaskMapping() {

			@Override
			public String getProduct() {
				return "ManualTest";
			}

			@Override
			public String getComponent() {
				// ignore
				return "ManualC1";
			}

		};
		BugzillaClient client = client(level);
		AbstractTaskDataHandler taskDataHandler = connector().getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(repository());
		TaskData taskData = new TaskData(mapper, repository().getConnectorKind(), repository().getRepositoryUrl(), "");
		taskDataHandler.initializeTaskData(repository(), taskData, initializationData, null);
		for (String attributeKey : additionalAttributeValues.keySet()) {
			taskData.getRoot()
					.createMappedAttribute(attributeKey)
					.setValue(additionalAttributeValues.get(attributeKey));
		}
		RepositoryResponse response = submitTask(taskData, client);
		String bugId = response.getTaskId();
		return getTask(bugId, client);
	}

	/**
	 * Retrieve task data for given task id
	 * 
	 * @param id
	 * @param client
	 * @return The taskData retrieved
	 * @throws Exception
	 */
	public TaskData getTask(String id, BugzillaClient client) throws Exception {
		final AbstractTaskDataHandler taskDataHandler = connector().getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(repository());
		final TaskData[] newData = new TaskData[1];
		client.getTaskData(Collections.singleton(id), new TaskDataCollector() {
			@Override
			public void accept(TaskData data) {
				newData[0] = data;
			}
		}, mapper, null);
		taskDataHandler.initializeTaskData(repository(), newData[0], null, null);
		return newData[0];
	}

	public RepositoryResponse submitTask(TaskData taskData, BugzillaClient client) throws IOException, CoreException {
		RepositoryResponse result = client.postTaskData(taskData, null);
		return result;
	}

	@Override
	public BugzillaRepositoryConnector connector() {
		return connector;
	}

	public String getProperty(String key) {
		String result = null;
		if (properties != null) {
			result = properties.get(key);
			if (result == null) {
				result = defaultproperties.get(key);
			}
		} else {
			result = defaultproperties.get(key);
		}
		return result;
	}

	public boolean isCustomWorkflowAndStatus() {
		return Boolean.parseBoolean(getProperty("custom_wf_and_status"));
	}

	public boolean isCustomWorkflow() {
		return Boolean.parseBoolean(getProperty("custom_wf"));
	}

	public boolean isXmlRpcEnabled() {
		return Boolean.parseBoolean(getProperty("xmlrpc_enabled"));
	}

	public String getDsciptorfile() {
		return getProperty("desciptorfile");
	}

	public BugzillaHarness createHarness() {
		return new BugzillaHarness(this);
	}

}
