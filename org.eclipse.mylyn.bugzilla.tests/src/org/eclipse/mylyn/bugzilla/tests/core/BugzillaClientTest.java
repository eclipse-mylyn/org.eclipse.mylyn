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

package org.eclipse.mylyn.bugzilla.tests.core;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttributeMapper;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryLocation;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tests.util.UrlBuilder;

/**
 * @author Robert Elves
 * @author Thomas Ehrnhoefer
 * @author Frank Becker
 * @author David Green
 */
public class BugzillaClientTest extends TestCase {

	private BugzillaClient client;

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		repository = BugzillaFixture.current().repository();
		client = BugzillaFixture.current().client();
	}

	public void testRDFProductConfig() throws Exception {
		RepositoryConfiguration config = client.getRepositoryConfiguration();
		assertNotNull(config);
		assertEquals(BugzillaFixture.current().getVersion().toUpperCase(), config.getInstallVersion()
				.toString()
				.toUpperCase());
		if (BugzillaFixture.current() == BugzillaFixture.BUGS_3_6_CUSTOM_WF_AND_STATUS) {
			assertEquals(10, config.getStatusValues().size());
		} else if (BugzillaFixture.current().getBugzillaVersion().compareMajorMinorOnly(BugzillaVersion.BUGZILLA_4_0) < 0) {
			assertEquals(7, config.getStatusValues().size());
		} else {
			assertEquals(5, config.getStatusValues().size());
		}
		if (config.getResolutions().contains("LATER")) {
			assertEquals(8, config.getResolutions().size());
			assertEquals(8, config.getPlatforms().size());
			assertEquals(36, config.getOSs().size());
			assertEquals(5, config.getPriorities().size());
		} else {
			assertEquals(6, config.getResolutions().size());
			assertEquals(4, config.getPlatforms().size());
			assertEquals(5, config.getOSs().size());
			assertEquals(6, config.getPriorities().size());
		}
		assertEquals(7, config.getSeverities().size());
		assertEquals(3, config.getProducts().size());
		if (BugzillaFixture.current() == BugzillaFixture.BUGS_3_6_CUSTOM_WF_AND_STATUS) {
			assertEquals(6, config.getOpenStatusValues().size());
			assertEquals(1, config.getClosedStatusValues().size());
		} else if (BugzillaFixture.current().getBugzillaVersion().compareMajorMinorOnly(BugzillaVersion.BUGZILLA_4_0) < 0) {
			assertEquals(4, config.getOpenStatusValues().size());
			assertEquals(3, config.getClosedStatusValues().size());
		} else {
			assertEquals(3, config.getOpenStatusValues().size());
			assertEquals(2, config.getClosedStatusValues().size());
		}
		assertEquals(2, config.getKeywords().size());
		assertEquals(2, config.getComponents("ManualTest").size());
		assertEquals(4, config.getVersions("ManualTest").size());
		assertEquals(4, config.getTargetMilestones("ManualTest").size());
		assertEquals(2, config.getComponents("TestProduct").size());
		assertEquals(4, config.getVersions("TestProduct").size());
		assertEquals(4, config.getTargetMilestones("TestProduct").size());
		assertEquals(2, config.getComponents("Product with Spaces").size());
		assertEquals(4, config.getVersions("Product with Spaces").size());
		assertEquals(4, config.getTargetMilestones("Product with Spaces").size());
	}

	public void testValidate() throws Exception {
		TaskRepository repository = BugzillaFixture.current().repository();
		AbstractWebLocation location = BugzillaFixture.current().location();
		client = new BugzillaClient(location, repository, BugzillaFixture.current().connector());
		client.validate(new NullProgressMonitor());
	}

	public void testValidateInvalidProxy() throws Exception {
		TaskRepository repository = BugzillaFixture.current().repository();
		AbstractWebLocation location = BugzillaFixture.current().location(PrivilegeLevel.USER,
				new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 12356)));

		client = new BugzillaClient(location, repository, BugzillaFixture.current().connector());
		try {
			client.validate(new NullProgressMonitor());
			fail("invalid proxy did not cause connection error");
		} catch (Exception e) {
			// ignore
		}
	}

	public void testValidateAnonymous() throws Exception {
		TaskRepository repository = BugzillaFixture.current().repository();
		AuthenticationCredentials anonymousCreds = new AuthenticationCredentials("", "");
		repository.setCredentials(AuthenticationType.REPOSITORY, anonymousCreds, false);
		TaskRepositoryLocation location = new TaskRepositoryLocation(repository);

		client = new BugzillaClient(location, repository, BugzillaFixture.current().connector());
		client.validate(new NullProgressMonitor());
	}

	public void testValidateAnonymousPlusHTTP() throws Exception {
		TaskRepository repository = BugzillaFixture.current().repository();
		AuthenticationCredentials anonymousCreds = new AuthenticationCredentials("", "");
		repository.setCredentials(AuthenticationType.REPOSITORY, anonymousCreds, false);
		repository.setCredentials(AuthenticationType.HTTP, new AuthenticationCredentials("YYYYYYYY", "XXXXXXXX"), false);
		TaskRepositoryLocation location = new TaskRepositoryLocation(repository);

		client = new BugzillaClient(location, repository, BugzillaFixture.current().connector());
		try {
			client.validate(new NullProgressMonitor());
		} catch (CoreException e) {
			assertEquals("Expected login error, got: " + e.getMessage(), RepositoryStatus.ERROR_REPOSITORY_LOGIN,
					e.getStatus().getCode());
		}
	}

	public void testValidateUser() throws Exception {
		TaskRepository repository = BugzillaFixture.current().repository();
		TaskRepositoryLocation location = new TaskRepositoryLocation(repository);

		client = new BugzillaClient(location, repository, BugzillaFixture.current().connector());
		client.validate(new NullProgressMonitor());
	}

	public void testValidateUserPlusHTTP() throws Exception {
		TaskRepository repository = BugzillaFixture.current().repository();
		repository.setCredentials(AuthenticationType.HTTP, new AuthenticationCredentials("YYYYYYYY", "XXXXXXXX"), false);
		TaskRepositoryLocation location = new TaskRepositoryLocation(repository);

		client = new BugzillaClient(location, repository, BugzillaFixture.current().connector());
		try {
			client.validate(new NullProgressMonitor());
		} catch (Exception e) {
			assertEquals("Unable to login to " + repository.getUrl()
					+ ".\n\n\n    The username or password you entered is not valid.\n\n"
					+ "Please validate credentials via Task Repositories view.", e.getMessage());
		}
	}

	public void testCommentQuery() throws Exception {
		BugzillaRepositoryConnector connector = BugzillaFixture.current().connector();
		BugzillaAttributeMapper mapper = new BugzillaAttributeMapper(repository, connector);
		TaskData newData = new TaskData(mapper, BugzillaFixture.current().getConnectorKind(), BugzillaFixture.current()
				.getRepositoryUrl(), "");

		assertTrue(connector.getTaskDataHandler().initializeTaskData(repository, newData, null,
				new NullProgressMonitor()));
		newData.getRoot().getMappedAttribute(TaskAttribute.SUMMARY).setValue("testCommentQuery()");
		newData.getRoot().getMappedAttribute(TaskAttribute.PRODUCT).setValue("TestProduct");
		newData.getRoot().getMappedAttribute(TaskAttribute.COMPONENT).setValue("TestComponent");
		newData.getRoot().getMappedAttribute(BugzillaAttribute.VERSION.getKey()).setValue("1");
		newData.getRoot().getMappedAttribute(BugzillaAttribute.OP_SYS.getKey()).setValue("All");
		long timestamp = System.currentTimeMillis();
		newData.getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION).setValue("" + timestamp);
		RepositoryResponse response = client.postTaskData(newData, new NullProgressMonitor());

		String bugid = response.getTaskId();
		RepositoryQuery query = new RepositoryQuery(BugzillaFixture.current().getConnectorKind(), "123");
		query.setRepositoryUrl(BugzillaFixture.current().getRepositoryUrl());
		if (BugzillaFixture.current().getBugzillaVersion().compareMajorMinorOnly(BugzillaVersion.BUGZILLA_4_0) < 0) {
			query.setUrl("?long_desc_type=allwordssubstr&long_desc=" + timestamp + "&bug_status=NEW&");
		} else {
			query.setUrl("?long_desc_type=allwordssubstr&long_desc=" + timestamp + "&bug_status=CONFIRMED&");
		}

		final Set<TaskData> returnedData = new HashSet<TaskData>();

		TaskDataCollector collector = new TaskDataCollector() {

			@Override
			public void accept(TaskData taskData) {
				returnedData.add(taskData);
			}
		};

		client.getSearchHits(query, collector, mapper, new NullProgressMonitor());
		assertEquals(1, returnedData.size());
		assertEquals(bugid, returnedData.iterator().next().getTaskId());
	}

	/**
	 * test for bug 335278: enhance search result handler to handle additional attributes
	 */
	public void testQueryRealName_Bug335278() throws Exception {
		IRepositoryQuery query = new RepositoryQuery(BugzillaFixture.current().getConnectorKind(), "query");
		UrlBuilder urlBuilder = UrlBuilder.build(BugzillaFixture.current().repository()).append("/buglist.cgi");

		urlBuilder.parameter(
				"columnlist",
				"bug_severity,priority,assigned_to,bug_status,resolution,short_desc,changeddate,reporter,assigned_to_realname,reporter_realname,product,component");
		query.setUrl(urlBuilder.toString());

		final Set<TaskData> returnedData = new HashSet<TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {

			@Override
			public void accept(TaskData taskData) {
				returnedData.add(taskData);
			}
		};
		TaskAttributeMapper mapper = BugzillaFixture.current()
				.connector()
				.getTaskDataHandler()
				.getAttributeMapper(BugzillaFixture.current().repository());

		client.getSearchHits(query, collector, mapper, new NullProgressMonitor());

		assertTrue(returnedData.size() > 0);
		for (TaskData taskData : returnedData) {
			TaskAttribute reporterName = taskData.getRoot().getAttribute(BugzillaAttribute.REPORTER_NAME.getKey());
			TaskAttribute assignedToName = taskData.getRoot().getAttribute(BugzillaAttribute.ASSIGNED_TO_NAME.getKey());
			assertHasValue(reporterName);
			assertHasValue(assignedToName);
		}
	}

	private void assertHasValue(TaskAttribute attribute) {
		assertNotNull(attribute);
		assertNotNull(attribute.getValue());
		assertTrue(attribute.getValue().trim().length() > 0);
	}

	public void testLeadingZeros() throws Exception {
		String taskNumber = "0002";
		TaskData taskData = BugzillaFixture.current().getTask(taskNumber, client);
		assertNotNull(taskData);
		assertNotNull(taskData);
		TaskAttribute idAttribute = taskData.getRoot().getAttribute(BugzillaAttribute.BUG_ID.getKey());
		assertNotNull(idAttribute);
		assertEquals("2", idAttribute.getValue());
	}

}
