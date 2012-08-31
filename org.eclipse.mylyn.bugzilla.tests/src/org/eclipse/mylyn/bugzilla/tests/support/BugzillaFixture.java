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

import junit.framework.AssertionFailedError;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClientManager;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 * @author Thomas Ehrnhoefer
 */
public class BugzillaFixture extends TestFixture {

	public static final String TEST_BUGZILLA_34_URL = getServerUrl("bugs34");

	public static final String TEST_BUGZILLA_36_URL = getServerUrl("bugs36");

	public static final String TEST_BUGZILLA_40_URL = getServerUrl("bugs40");

	public static final String TEST_BUGZILLA_42_URL = getServerUrl("bugs42");

	public static final String TEST_BUGZILLA_HEAD_URL = getServerUrl("bugshead");

	private static final String getServerUrl(String version) {
		return TestConfiguration.getRepositoryUrl(version);
	}

	public static final String CUSTOM_WF = "Custom Workflow";

	public static final String CUSTOM_WF_AND_STATUS = "Custom Workflow and Status";

	public static final String XML_RPC_DISABLED = "XML-RPC disabled";

	private static BugzillaFixture current;

	/**
	 * @deprecated not supported any more
	 */
	@Deprecated
	public static BugzillaFixture BUGS_3_4 = new BugzillaFixture(BugzillaFixture.TEST_BUGZILLA_34_URL, //
			"3.4.14", "");

	public static BugzillaFixture BUGS_3_6 = new BugzillaFixture(BugzillaFixture.TEST_BUGZILLA_36_URL, //
			"3.6.11", "");

	public static BugzillaFixture BUGS_3_6_CUSTOM_WF = new BugzillaFixture(BugzillaFixture.TEST_BUGZILLA_36_URL
			+ "-custom-wf", "3.6.11", CUSTOM_WF);

	public static BugzillaFixture BUGS_3_6_CUSTOM_WF_AND_STATUS = new BugzillaFixture(
			BugzillaFixture.TEST_BUGZILLA_36_URL + "-custom-wf-and-status", "3.6.11", CUSTOM_WF_AND_STATUS);

	public static BugzillaFixture BUGS_3_6_XML_RPC_DISABLED = new BugzillaFixture(BugzillaFixture.TEST_BUGZILLA_36_URL
			+ "-xml-rpc-disabled", "3.6.11", XML_RPC_DISABLED);

	public static BugzillaFixture BUGS_4_0 = new BugzillaFixture(BugzillaFixture.TEST_BUGZILLA_40_URL, //
			"4.0.8", "");

	public static BugzillaFixture BUGS_4_2 = new BugzillaFixture(BugzillaFixture.TEST_BUGZILLA_42_URL, //
			"4.2.3", "");

	public static BugzillaFixture BUGS_HEAD = new BugzillaFixture(BugzillaFixture.TEST_BUGZILLA_HEAD_URL, //
			"4.5", "");

	public static BugzillaFixture DEFAULT = BUGS_4_2;

	public static final BugzillaFixture[] ALL = new BugzillaFixture[] { BUGS_3_4, BUGS_3_6, BUGS_3_6_XML_RPC_DISABLED,
			BUGS_3_6_CUSTOM_WF, BUGS_3_6_CUSTOM_WF_AND_STATUS, BUGS_4_0, BUGS_4_2 /*, BUGS_HEAD*/};

	private final String version;

	private final BugzillaVersion bugzillaVersion;

	public BugzillaFixture(String url, String version, String info) {
		super(BugzillaCorePlugin.CONNECTOR_KIND, url);
		this.version = version;
		this.bugzillaVersion = new BugzillaVersion(version);
		setInfo("Bugzilla", version, info);
	}

	public BugzillaVersion getBugzillaVersion() {
		return bugzillaVersion;
	}

	public static void cleanup010() throws Exception {
	}

	public static BugzillaFixture current(BugzillaFixture fixture) {
		if (current == null) {
			fixture.activate();
		}
		return current;
	}

	public static BugzillaFixture current() {
		return current(DEFAULT);
	}

	@Override
	public BugzillaFixture activate() {
		current = this;
		setUpFramework();
		return this;
	}

	@Override
	protected TestFixture getDefault() {
		return DEFAULT;
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
		String repositoryURL = taskRepository.getUrl();
		String filepath = "testdata/repository/" + getRepositoryName(location.getUrl()) + "/DesciptorFile.txt";
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

		taskRepository.setCredentials(AuthenticationType.REPOSITORY,
				location.getCredentials(AuthenticationType.REPOSITORY), false);

		taskRepository.setCredentials(AuthenticationType.HTTP, location.getCredentials(AuthenticationType.HTTP), false);
		taskRepository.setCharacterEncoding(encoding);

		BugzillaRepositoryConnector connector = new BugzillaRepositoryConnector();
		super.connector = connector;
		BugzillaClientManager bugzillaClientManager = connector.getClientManager();
		BugzillaClient client = bugzillaClientManager.getClient(taskRepository, null);

		connector.getRepositoryConfiguration(taskRepository, false, new NullProgressMonitor());
		connector.writeRepositoryConfigFile();
		return client;
	}

	private String getRepositoryName(String url) {
		int i = url.lastIndexOf("/");
		if (i == -1) {
			throw new IllegalArgumentException(NLS.bind("Unable to determine repository name for {0}", url));
		}
		return url.substring(i + 1);
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
		if (summary == null) {
			summary = "summary";
		}
		if (description == null) {
			description = "description";
		}
		BugzillaClient client = client(level);
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(repository());
		TaskData taskData = new TaskData(mapper, repository().getConnectorKind(), repository().getRepositoryUrl(), "");
		taskDataHandler.initializeTaskData(repository(), taskData, null, null);
		taskData.getRoot().createMappedAttribute(TaskAttribute.SUMMARY).setValue(summary);
		taskData.getRoot().createMappedAttribute(TaskAttribute.DESCRIPTION).setValue(description);
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.OP_SYS.getKey()).setValue("All");
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.REP_PLATFORM.getKey()).setValue("All");
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.VERSION.getKey()).setValue("unspecified");
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
		final AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
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
		return (BugzillaRepositoryConnector) connector;
	}

}
