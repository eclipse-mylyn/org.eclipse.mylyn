/*******************************************************************************
 * Copyright © 2004, 2013, 2024 Nathan Hapke and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Nathan Hapke - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Frank Becker - improvements
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.core;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

import junit.framework.TestCase;

/**
 * @author Nathan Hapke
 * @author Rob Elves
 * @author Thomas Ehrnhoefer
 * @author Steffen Pingel
 * @author Frank Becker
 */
@SuppressWarnings("nls")
public class BugzillaRepositoryConnectorStandaloneTest extends TestCase {

	private TaskRepository repository;

	private BugzillaRepositoryConnector connector;

	private BugzillaClient client;

	@Override
	public void setUp() throws Exception {
		client = BugzillaFixture.current().client(PrivilegeLevel.USER);
		repository = BugzillaFixture.current().repository();
		connector = BugzillaFixture.current().connector();
	}

	public void testHasTaskChanged() {
		AbstractTask task = new TaskTask(repository.getConnectorKind(), repository.getRepositoryUrl(), "1");
		task.setAttribute(BugzillaAttribute.DELTA_TS.getKey(), "2008-02-02 12:01:12");
		TaskData data = new TaskData(connector.getTaskDataHandler().getAttributeMapper(repository),
				repository.getConnectorKind(), repository.getRepositoryUrl(), "1");
		TaskAttribute attribute = data.getRoot().createAttribute(BugzillaAttribute.DELTA_TS.getKey());

		// Testing State 1

		// Offline no timezone, repository no timezone, same time
		attribute.setValue("2008-02-02 12:01:12");
		assertFalse(connector.hasTaskChanged(repository, task, data));

		// Offline no timezone, repository no timezone, different date
		attribute.setValue("2008-02-03 12:01:12");
		assertTrue(connector.hasTaskChanged(repository, task, data));

		// Offline no timezone, repository no timezone, different time
		attribute.setValue("2008-02-02 12:03:12");
		assertTrue(connector.hasTaskChanged(repository, task, data));

		// Offline no timezone, repository no timezone, different (older) time
		attribute.setValue("2008-02-02 12:03:00");
		assertTrue(connector.hasTaskChanged(repository, task, data));

		// Testing Fuzzy States 2 & 3

		// Offline have timezone, repository no timezone, same time
		task.setAttribute(BugzillaAttribute.DELTA_TS.getKey(), "2008-02-02 12:01:12 -0700");
		attribute.setValue("2008-02-02 12:01:12");
		assertFalse(connector.hasTaskChanged(repository, task, data));

		// Offline have timezone, repository no timezone, different time
		task.setAttribute(BugzillaAttribute.DELTA_TS.getKey(), "2008-02-02 12:01:12 -0700");
		attribute.setValue("2008-02-02 12:01:13");
		assertTrue(connector.hasTaskChanged(repository, task, data));

		// Offline no timezone, repository has timezone
		task.setAttribute(BugzillaAttribute.DELTA_TS.getKey(), "2008-02-02 12:01:12");
		attribute.setValue("2008-02-02 12:01:12 -0700");
		assertFalse(connector.hasTaskChanged(repository, task, data));

		// Offline no timezone, repository has timezone and different time (fuzzy check doesn't pass)
		attribute.setValue("2008-02-02 12:01:13 -0700");
		assertTrue(connector.hasTaskChanged(repository, task, data));

		// Test backwards in time
		task.setAttribute(BugzillaAttribute.DELTA_TS.getKey(), "2008-02-02 12:01:12");
		attribute.setValue("2008-02-02 12:01:03 -0700");
		assertTrue(connector.hasTaskChanged(repository, task, data));

		// Testing State 4

		// Same world time, reported wrt different time zones
		task.setAttribute(BugzillaAttribute.DELTA_TS.getKey(), "2009-09-04 00:00:00 PDT");
		attribute.setValue("2009-09-04 03:00:00 EDT");
		assertFalse(connector.hasTaskChanged(repository, task, data));

		// Different times, same time zone
		task.setAttribute(BugzillaAttribute.DELTA_TS.getKey(), "2009-09-04 12:00:00 PDT");
		attribute.setValue("2009-09-04 12:00:01 PDT");
		assertTrue(connector.hasTaskChanged(repository, task, data));

		// Same times, bogus format (string compare)
		task.setAttribute(BugzillaAttribute.DELTA_TS.getKey(), "2009-09-04 12:00:::01 PDT");
		attribute.setValue("2009-09-04 12:00:::01 PDT");
		assertFalse(connector.hasTaskChanged(repository, task, data));

		// Different times, bogus format (string compare)
		task.setAttribute(BugzillaAttribute.DELTA_TS.getKey(), "2009-09X-04X12:00:::01 PDT");
		attribute.setValue("2009-X-03 12:00:::01 PDT");
		assertTrue(connector.hasTaskChanged(repository, task, data));

	}

	/**
	 * This is the first test so that the repository credentials are correctly set for the other tests
	 */
	public void testAddCredentials() {
		AuthenticationCredentials auth = repository.getCredentials(AuthenticationType.REPOSITORY);
		assertTrue(auth != null && auth.getPassword() != null && !auth.getPassword().equals("")
				&& auth.getUserName() != null && !auth.getUserName().equals(""));
	}

	public void testGetTaskData() throws Exception {
		TaskData taskData = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		Set<String> taskIds = new HashSet<>();
		taskIds.add(taskData.getTaskId());
		final Set<TaskData> results = new HashSet<>();
		TaskDataCollector collector = new TaskDataCollector() {
			@Override
			public void accept(TaskData taskData) {
				results.add(taskData);
			}
		};
		connector.getTaskDataHandler().getMultiTaskData(repository, taskIds, collector, new NullProgressMonitor());
		assertEquals(1, results.size());
		TaskData updatedTaskData = results.iterator().next();
		String taskId = updatedTaskData.getTaskId();
		assertEquals(taskId, updatedTaskData.getTaskId());
		assertEquals(taskData.getRoot().getAttribute(BugzillaAttribute.ASSIGNED_TO.getKey()).getValue(),
				updatedTaskData.getRoot().getAttribute(BugzillaAttribute.ASSIGNED_TO.getKey()).getValue());
		assertEquals(taskData.getRoot().getAttribute(BugzillaAttribute.LONG_DESC.getKey()).getValue(),
				updatedTaskData.getRoot().getAttribute(BugzillaAttribute.LONG_DESC.getKey()).getValue());
		assertEquals(taskData.getRoot().getAttribute(BugzillaAttribute.PRIORITY.getKey()).getValue(),
				updatedTaskData.getRoot().getAttribute(BugzillaAttribute.PRIORITY.getKey()).getValue());
	}

	public void testGetMultiTaskData() throws Exception {
		TaskData taskData = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		TaskData taskData2 = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		TaskData taskData3 = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		Set<String> taskIds = new HashSet<>();
		taskIds.add(taskData.getTaskId());
		taskIds.add(taskData2.getTaskId());
		taskIds.add(taskData3.getTaskId());
		final Map<String, TaskData> results = new HashMap<>();
		TaskDataCollector collector = new TaskDataCollector() {
			@Override
			public void accept(TaskData taskData) {
				results.put(taskData.getTaskId(), taskData);
			}
		};
		connector.getTaskDataHandler().getMultiTaskData(repository, taskIds, collector, new NullProgressMonitor());
		assertEquals(3, results.size());

		TaskData updatedTaskData = results.get(taskData.getTaskId());
		assertEquals(taskData.getRoot().getAttribute(BugzillaAttribute.ASSIGNED_TO.getKey()).getValue(),
				updatedTaskData.getRoot().getAttribute(BugzillaAttribute.ASSIGNED_TO.getKey()).getValue());
		assertEquals(taskData.getRoot().getAttribute(BugzillaAttribute.LONG_DESC.getKey()).getValue(),
				updatedTaskData.getRoot().getAttribute(BugzillaAttribute.LONG_DESC.getKey()).getValue());
		assertEquals(taskData.getRoot().getAttribute(BugzillaAttribute.PRIORITY.getKey()).getValue(),
				updatedTaskData.getRoot().getAttribute(BugzillaAttribute.PRIORITY.getKey()).getValue());

		updatedTaskData = results.get(taskData2.getTaskId());
		assertEquals(taskData2.getRoot().getAttribute(BugzillaAttribute.ASSIGNED_TO.getKey()).getValue(),
				updatedTaskData.getRoot().getAttribute(BugzillaAttribute.ASSIGNED_TO.getKey()).getValue());
		assertEquals(taskData2.getRoot().getAttribute(BugzillaAttribute.LONG_DESC.getKey()).getValue(),
				updatedTaskData.getRoot().getAttribute(BugzillaAttribute.LONG_DESC.getKey()).getValue());

		updatedTaskData = results.get(taskData3.getTaskId());
		assertEquals(taskData3.getRoot().getAttribute(BugzillaAttribute.ASSIGNED_TO.getKey()).getValue(),
				updatedTaskData.getRoot().getAttribute(BugzillaAttribute.REPORTER.getKey()).getValue());
		assertEquals(taskData3.getRoot().getAttribute(BugzillaAttribute.LONG_DESC.getKey()).getValue(),
				updatedTaskData.getRoot().getAttribute(BugzillaAttribute.LONG_DESC.getKey()).getValue());
	}

	public void testPerformQuery() throws Exception {
		RepositoryConfiguration repositoryConfiguration = connector
				.getRepositoryConfiguration(repository.getRepositoryUrl());
		List<String> priorities = repositoryConfiguration.getOptionValues(BugzillaAttribute.PRIORITY);
		final String priority = priorities.get(0);
		final String severity = "trivial";
		String email = "tests%40mylyn.eclipse.org";

		final String summaryNotNull = "Summary for testPerformQuery " + new Date();
		final String descriptionNotNull = "Description for testPerformQuery " + new Date();
		// create a Task so we not have on for the query
		BugzillaFixture.current().createTask(PrivilegeLevel.USER, new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put(TaskAttribute.SUMMARY, summaryNotNull);
				put(TaskAttribute.DESCRIPTION, descriptionNotNull);
				put(BugzillaAttribute.OP_SYS.getKey(), "All");
				put(BugzillaAttribute.REP_PLATFORM.getKey(), "All");
				put(BugzillaAttribute.VERSION.getKey(), "unspecified");
				put(TaskAttribute.PRIORITY, priority);
				put(TaskAttribute.SEVERITY, severity);
			}
		});
		// create the test Task
		TaskData taskData = BugzillaFixture.current().createTask(PrivilegeLevel.USER, new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put(TaskAttribute.SUMMARY, summaryNotNull);
				put(TaskAttribute.DESCRIPTION, descriptionNotNull);
				put(BugzillaAttribute.OP_SYS.getKey(), "All");
				put(BugzillaAttribute.REP_PLATFORM.getKey(), "All");
				put(BugzillaAttribute.VERSION.getKey(), "unspecified");
			}
		});

		String bug_status = BugzillaFixture.current()
				.getBugzillaVersion()
				.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_4_0) < 0
				? "&bug_status=UNCONFIRMED&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED"
						: "&bug_status=UNCONFIRMED&bug_status=CONFIRMED";
		String queryUrlString = repository.getRepositoryUrl() + "/buglist.cgi?priority=" + priority
				+ "&emailassigned_to1=1&query_format=advanced&emailreporter1=1&field0-0-0=bug_status&bug_severity="
				+ severity + bug_status + "&type0-0-1=equals&value0-0-1=tests%40mylyn.eclipse.org&email1=" + email
				+ "&type0-0-0=notequals&field0-0-1=reporter&value0-0-0=UNCONFIRMED&emailtype1=exact&longdesc="
				+ descriptionNotNull + "&longdesc_type=casesubstring";

		// make sure initial task is not P1/trivial
		assertFalse(
				taskData.getRoot().getMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).getValue().equals(priority));
		assertFalse(taskData.getRoot()
				.getMappedAttribute(BugzillaAttribute.BUG_SEVERITY.getKey())
				.getValue()
				.equals(severity));

		// run query
		RepositoryQuery query = new RepositoryQuery(repository.getConnectorKind(), "handle-testQueryViaConnector");
		query.setUrl(queryUrlString);
		final Map<String, TaskData> changedTaskData = new HashMap<>();
		TaskDataCollector collector = new TaskDataCollector() {
			@Override
			public void accept(TaskData taskData) {
				changedTaskData.put(taskData.getTaskId(), taskData);
			}
		};
		connector.performQuery(repository, query, collector, null, new NullProgressMonitor());
		assertEquals(1, changedTaskData.size());

		// set priority and severity on task
		taskData.getRoot()
		.getMappedAttribute(BugzillaAttribute.SHORT_DESC.getKey())
		.setValue(System.currentTimeMillis() + "");
		taskData.getRoot().getMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue(priority);
		taskData.getRoot().getMappedAttribute(BugzillaAttribute.BUG_SEVERITY.getKey()).setValue(severity);
		taskData.getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION).setValue(descriptionNotNull);
		RepositoryResponse response = BugzillaFixture.current().submitTask(taskData, client);
		assertFalse(response.getTaskId().equals(""));
		TaskData taskDataNew = BugzillaFixture.current().getTask(response.getTaskId(), client);

		// run query again
		final Map<String, TaskData> changedTaskData2 = new HashMap<>();
		TaskDataCollector collector2 = new TaskDataCollector() {
			@Override
			public void accept(TaskData taskData) {
				changedTaskData2.put(taskData.getTaskId(), taskData);
			}
		};
		connector.performQuery(repository, query, collector2, null, new NullProgressMonitor());
		assertEquals(2, changedTaskData2.size());
		// compare query results
		changedTaskData2.keySet().removeAll(changedTaskData.keySet());
		assertEquals(1, changedTaskData2.size());
		taskData = changedTaskData2.get(taskData.getTaskId());
		assertNotNull(taskData);
		assertTrue(taskData.getRoot()
				.getAttribute(BugzillaAttribute.SHORT_DESC.getKey())
				.getValue()
				.equals(taskDataNew.getRoot().getAttribute(BugzillaAttribute.SHORT_DESC.getKey()).getValue()));
	}

	public void testGetTaskMappingPriority() throws Exception {
		BugzillaVersion version = new BugzillaVersion(BugzillaFixture.current().getVersion());

		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(repository);
		TaskData taskData = new TaskData(mapper, repository.getConnectorKind(), repository.getRepositoryUrl(), "");
		ITaskMapping mapping = connector.getTaskMapping(taskData);
		taskDataHandler.initializeTaskData(repository, taskData, null, null);

		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P1");
		assertEquals(PriorityLevel.P1, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P2");
		assertEquals(PriorityLevel.P2, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P3");
		assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P4");
		assertEquals(PriorityLevel.P4, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P5");
		assertEquals(PriorityLevel.P5, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("abc");
		assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
		if (!version.isSmaller(BugzillaVersion.BUGZILLA_3_6)) {
			// fresh bugzilla 3.6 databases have a new schema for priorities
			// the value "---" maybe present (as default) so we get P5 or P3 (if not present) as the level
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Highest");
			assertEquals(PriorityLevel.P1, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("High");
			assertEquals(PriorityLevel.P2, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Normal");
			assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Low");
			assertEquals(PriorityLevel.P4, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Lowest");
			assertEquals(PriorityLevel.P5, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("---");
			PriorityLevel pl = mapping.getPriorityLevel();
			assertTrue("P3 or P5 expected! but got " + pl.toString(), pl == PriorityLevel.P3 || pl == PriorityLevel.P5);
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("abc");
			assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
		}
	}

	public void testGetTaskMappingPriorityNoConfiguration() throws Exception {
		BugzillaVersion version = new BugzillaVersion(BugzillaFixture.current().getVersion());

		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(repository);
		TaskData taskData = new TaskData(mapper, repository.getConnectorKind(), repository.getRepositoryUrl(), "");
		ITaskMapping mapping = connector.getTaskMapping(taskData);
		taskDataHandler.initializeTaskData(repository, taskData, null, null);

		connector.removeConfiguration(connector.getRepositoryConfiguration(repository.getRepositoryUrl()));

		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P1");
		assertEquals(PriorityLevel.P1, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P2");
		assertEquals(PriorityLevel.P2, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P3");
		assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P4");
		assertEquals(PriorityLevel.P4, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P5");
		assertEquals(PriorityLevel.P5, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("abc");
		assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
		if (!version.isSmaller(BugzillaVersion.BUGZILLA_3_6)) {
			// fresh bugzilla 3.6 databases have a new schema for priorities
			// the value "---" maybe present (as default) so we get P5 or P3 (if not present) as the level
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Highest");
			assertEquals(PriorityLevel.P1, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("High");
			assertEquals(PriorityLevel.P2, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Normal");
			assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Low");
			assertEquals(PriorityLevel.P4, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Lowest");
			assertEquals(PriorityLevel.P5, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("---");
			PriorityLevel pl = mapping.getPriorityLevel();
			assertTrue("P3 or P5 expected! but got " + pl.toString(), pl == PriorityLevel.P3 || pl == PriorityLevel.P5);
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("abc");
			assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
		}
	}

	public void testGetTaskMappingPriorityCustom() throws Exception {
		BugzillaVersion version = new BugzillaVersion(BugzillaFixture.current().getVersion());

		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(repository);
		TaskData taskData = new TaskData(mapper, repository.getConnectorKind(), repository.getRepositoryUrl(), "");
		ITaskMapping mapping = connector.getTaskMapping(taskData);
		taskDataHandler.initializeTaskData(repository, taskData, null, null);

		RepositoryConfiguration config = connector.getRepositoryConfiguration(repository.getRepositoryUrl());
		connector.removeConfiguration(config);
		config = new RepositoryConfiguration();
		config.setRepositoryUrl(repository.getRepositoryUrl());
		config.addItem(BugzillaAttribute.PRIORITY, "MostHighest");
		config.addItem(BugzillaAttribute.PRIORITY, "Highest");
		config.addItem(BugzillaAttribute.PRIORITY, "Higher");
		config.addItem(BugzillaAttribute.PRIORITY, "High");
		config.addItem(BugzillaAttribute.PRIORITY, "Normal");
		config.addItem(BugzillaAttribute.PRIORITY, "Low");
		config.addItem(BugzillaAttribute.PRIORITY, "Lower");
		config.addItem(BugzillaAttribute.PRIORITY, "Lowest");
		config.addItem(BugzillaAttribute.PRIORITY, "MostLowest");
		connector.addRepositoryConfiguration(config);

		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P1");
		assertEquals(PriorityLevel.P1, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P2");
		assertEquals(PriorityLevel.P2, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P3");
		assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P4");
		assertEquals(PriorityLevel.P4, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P5");
		assertEquals(PriorityLevel.P5, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("abc");
		assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
		if (!version.isSmaller(BugzillaVersion.BUGZILLA_3_6)) {
			// fresh bugzilla 3.6 databases have a new schema for priorities
			// the value "---" maybe present (as default) so we get P5 or P3 (if not present) as the level
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("MostHighest");
			assertEquals(PriorityLevel.P1, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Highest");
			assertEquals(PriorityLevel.P1, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Higher");
			assertEquals(PriorityLevel.P2, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("High");
			assertEquals(PriorityLevel.P2, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Normal");
			assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Low");
			assertEquals(PriorityLevel.P4, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Lower");
			assertEquals(PriorityLevel.P4, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Lowest");
			assertEquals(PriorityLevel.P5, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("MostLowest");
			assertEquals(PriorityLevel.P5, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("---");
			assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("abc");
			assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
		}
	}

	public void testGetTaskMappingPriorityCustomWithNoConfig() throws Exception {
		BugzillaVersion version = new BugzillaVersion(BugzillaFixture.current().getVersion());

		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(repository);
		TaskData taskData = new TaskData(mapper, repository.getConnectorKind(), repository.getRepositoryUrl(), "");
		ITaskMapping mapping = connector.getTaskMapping(taskData);
		taskDataHandler.initializeTaskData(repository, taskData, null, null);

		RepositoryConfiguration config = connector.getRepositoryConfiguration(repository.getRepositoryUrl());
		connector.removeConfiguration(config);
		config = new RepositoryConfiguration();
		config.setRepositoryUrl(repository.getRepositoryUrl());
		config.addItem(BugzillaAttribute.PRIORITY, "MostHighest");
		config.addItem(BugzillaAttribute.PRIORITY, "Highest");
		config.addItem(BugzillaAttribute.PRIORITY, "Higher");
		config.addItem(BugzillaAttribute.PRIORITY, "High");
		config.addItem(BugzillaAttribute.PRIORITY, "Normal");
		config.addItem(BugzillaAttribute.PRIORITY, "Low");
		config.addItem(BugzillaAttribute.PRIORITY, "Lower");
		config.addItem(BugzillaAttribute.PRIORITY, "Lowest");
		config.addItem(BugzillaAttribute.PRIORITY, "MostLowest");

		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P1");
		assertEquals(PriorityLevel.P1, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P2");
		assertEquals(PriorityLevel.P2, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P3");
		assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P4");
		assertEquals(PriorityLevel.P4, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("P5");
		assertEquals(PriorityLevel.P5, mapping.getPriorityLevel());
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("abc");
		assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
		if (!version.isSmaller(BugzillaVersion.BUGZILLA_3_6)) {
			// fresh bugzilla 3.6 databases have a new schema for priorities
			// the value "---" maybe present (as default) so we get P5 or P3 (if not present) as the level
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("MostHighest");
			assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Highest");
			assertEquals(PriorityLevel.P1, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Higher");
			assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("High");
			assertEquals(PriorityLevel.P2, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Normal");
			assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Low");
			assertEquals(PriorityLevel.P4, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Lower");
			assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("Lowest");
			assertEquals(PriorityLevel.P5, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("MostLowest");
			assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("---");
			assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
			taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue("abc");
			assertEquals(PriorityLevel.P3, mapping.getPriorityLevel());
		}
	}
}
