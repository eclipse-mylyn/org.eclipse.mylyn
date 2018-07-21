/*******************************************************************************
 * Copyright (c) 2009, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.core;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.bugzilla.tests.AbstractBugzillaTest;
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
public class BugzillaClientTest extends AbstractBugzillaTest {

	public void testRDFProductConfig() throws Exception {
		if (BugzillaVersion.BUGZILLA_4_4.compareTo(BugzillaFixture.current().getBugzillaVersion()) == 0
				|| BugzillaVersion.BUGZILLA_HEAD.compareTo(BugzillaFixture.current().getBugzillaVersion()) == 0) {
			// FIXME: need fix of bug#372600
			return;
		}
		RepositoryConfiguration config = client.getRepositoryConfiguration();
		assertNotNull(config);
		assertEquals(0, config.getInstallVersion()
				.compareMajorMinorOnly(new BugzillaVersion(BugzillaFixture.current().getVersion())));
		if (BugzillaFixture.current().isCustomWorkflowAndStatus()) {
			assertEquals(8, config.getOptionValues(BugzillaAttribute.BUG_STATUS).size());
		} else if (BugzillaFixture.current()
				.getBugzillaVersion()
				.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_4_0) < 0) {
			assertEquals(7, config.getOptionValues(BugzillaAttribute.BUG_STATUS).size());
		} else {
			assertEquals(5, config.getOptionValues(BugzillaAttribute.BUG_STATUS).size());
		}
		if (config.getOptionValues(BugzillaAttribute.RESOLUTION).contains("LATER")) {
			assertEquals(8, config.getOptionValues(BugzillaAttribute.RESOLUTION).size());
			assertEquals(8, config.getOptionValues(BugzillaAttribute.REP_PLATFORM).size());
			assertEquals(36, config.getOptionValues(BugzillaAttribute.OP_SYS).size());
			assertEquals(5, config.getOptionValues(BugzillaAttribute.PRIORITY).size());
		} else {
			if (BugzillaVersion.BUGZILLA_4_0
					.compareMajorMinorOnly(new BugzillaVersion(BugzillaFixture.current().getVersion())) > 0) {
				assertEquals(6, config.getOptionValues(BugzillaAttribute.RESOLUTION).size());
			} else {
				assertEquals(5, config.getOptionValues(BugzillaAttribute.RESOLUTION).size());
			}
			assertEquals(4, config.getOptionValues(BugzillaAttribute.REP_PLATFORM).size());
			assertEquals(5, config.getOptionValues(BugzillaAttribute.OP_SYS).size());
			assertEquals(6, config.getOptionValues(BugzillaAttribute.PRIORITY).size());
		}
		assertEquals(7, config.getOptionValues(BugzillaAttribute.BUG_SEVERITY).size());
		assertEquals(3, config.getOptionValues(BugzillaAttribute.PRODUCT).size());
		if (BugzillaFixture.current().isCustomWorkflowAndStatus()) {
			assertEquals(5, config.getOpenStatusValues().size());
			assertEquals(3, config.getClosedStatusValues().size());
		} else if (BugzillaFixture.current()
				.getBugzillaVersion()
				.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_4_0) < 0) {
			assertEquals(4, config.getOpenStatusValues().size());
			assertEquals(3, config.getClosedStatusValues().size());
		} else {
			assertEquals(3, config.getOpenStatusValues().size());
			assertEquals(2, config.getClosedStatusValues().size());
		}
		assertEquals(2, config.getOptionValues(BugzillaAttribute.KEYWORDS).size());
		assertEquals(2, config.getProductOptionValues(BugzillaAttribute.COMPONENT, "ManualTest").size());
		assertEquals(4, config.getProductOptionValues(BugzillaAttribute.VERSION, "ManualTest").size());
		assertEquals(4, config.getProductOptionValues(BugzillaAttribute.TARGET_MILESTONE, "ManualTest").size());
		assertEquals(1, config.getProductOptionValues(BugzillaAttribute.COMPONENT, "TestProduct").size());
		assertEquals(1, config.getProductOptionValues(BugzillaAttribute.VERSION, "TestProduct").size());
		assertEquals(1, config.getProductOptionValues(BugzillaAttribute.TARGET_MILESTONE, "TestProduct").size());
		assertEquals(2, config.getProductOptionValues(BugzillaAttribute.COMPONENT, "Product with Spaces").size());
		assertEquals(4, config.getProductOptionValues(BugzillaAttribute.VERSION, "Product with Spaces").size());
		assertEquals(4,
				config.getProductOptionValues(BugzillaAttribute.TARGET_MILESTONE, "Product with Spaces").size());
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
		repository.setCredentials(AuthenticationType.HTTP, new AuthenticationCredentials("YYYYYYYY", "XXXXXXXX"),
				false);
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
		repository.setCredentials(AuthenticationType.HTTP, new AuthenticationCredentials("YYYYYYYY", "XXXXXXXX"),
				false);
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
		TaskData newData = new TaskData(mapper, BugzillaFixture.current().getConnectorKind(),
				BugzillaFixture.current().getRepositoryUrl(), "");

		assertTrue(connector.getTaskDataHandler().initializeTaskData(repository, newData, null,
				new NullProgressMonitor()));
		newData.getRoot().getMappedAttribute(TaskAttribute.SUMMARY).setValue("testCommentQuery()");
		newData.getRoot().getMappedAttribute(TaskAttribute.PRODUCT).setValue("TestProduct");
		newData.getRoot().getMappedAttribute(TaskAttribute.COMPONENT).setValue("TestComponent");
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
		String taskId = harness.enhanceSearchTaskExists();
		if (taskId == null) {
			taskId = harness.createEnhanceSearchTask();
		}

		IRepositoryQuery query = new RepositoryQuery(BugzillaFixture.current().getConnectorKind(), "query");
		UrlBuilder urlBuilder = UrlBuilder.build(BugzillaFixture.current().repository()).append("/buglist.cgi");

		urlBuilder.parameter("short_desc=test%20EnhanceSearch&columnlist",
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
