/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttributeMapper;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tests.util.TestUtil.PrivilegeLevel;

/**
 * @author Robert Elves
 * @author Thomas Ehrnhoefer
 * @author Frank Becker
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
		assertEquals(BugzillaFixture.current().getVersion(), config.getInstallVersion().toString());
		assertEquals(7, config.getStatusValues().size());
		assertEquals(8, config.getResolutions().size());
		assertEquals(8, config.getPlatforms().size());
		assertEquals(36, config.getOSs().size());
		assertEquals(5, config.getPriorities().size());
		assertEquals(7, config.getSeverities().size());
		assertEquals(3, config.getProducts().size());
		assertEquals(4, config.getOpenStatusValues().size());
		assertEquals(3, config.getClosedStatusValues().size());
		assertEquals(2, config.getKeywords().size());
		assertEquals(2, config.getComponents("ManualTest").size());
		assertEquals(4, config.getVersions("ManualTest").size());
		assertEquals(4, config.getTargetMilestones("ManualTest").size());
		assertEquals(2, config.getComponents("TestProduct").size());
		assertEquals(4, config.getVersions("TestProduct").size());
		assertEquals(4, config.getTargetMilestones("TestProduct").size());
		assertEquals(2, config.getComponents("Scratch").size());
		assertEquals(4, config.getVersions("Scratch").size());
		assertEquals(4, config.getTargetMilestones("Scratch").size());
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

	public void testCommentQuery() throws Exception {
		BugzillaRepositoryConnector connector = BugzillaFixture.current().connector();
		BugzillaAttributeMapper mapper = new BugzillaAttributeMapper(repository, connector);
		TaskData newData = new TaskData(mapper, BugzillaFixture.current().getConnectorKind(), BugzillaFixture.current()
				.getRepositoryUrl(), "");

		connector.getTaskDataHandler().initializeTaskData(repository, newData, null, new NullProgressMonitor());
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
		query.setUrl("?long_desc_type=allwordssubstr&long_desc=" + timestamp + "&bug_status=NEW&");

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

	@SuppressWarnings("null")
	public void testFlags() throws Exception {
		BugzillaVersion version = new BugzillaVersion(BugzillaFixture.current().getVersion());
		if (version.isSmallerOrEquals(BugzillaVersion.BUGZILLA_3_2)) {
			return;
		}
		String taskNumber = "10";
		TaskData taskData = BugzillaFixture.current().getTask(taskNumber, client);
		assertNotNull(taskData);

		Collection<TaskAttribute> a = taskData.getRoot().getAttributes().values();
		TaskAttribute flagA = null;
		TaskAttribute flagB = null;
		TaskAttribute flagC = null;
		TaskAttribute flagD = null;
		TaskAttribute stateA = null;
		TaskAttribute stateB = null;
		TaskAttribute stateC = null;
		TaskAttribute stateD = null;
		for (TaskAttribute taskAttribute : a) {
			if (taskAttribute.getId().startsWith("task.common.kind.flag")) {
				TaskAttribute state = taskAttribute.getAttribute("state");
				if (state.getMetaData().getLabel().equals("BugFlag1")) {
					flagA = taskAttribute;
					stateA = state;
				} else if (state.getMetaData().getLabel().equals("BugFlag2")) {
					flagB = taskAttribute;
					stateB = state;
				} else if (state.getMetaData().getLabel().equals("BugFlag3")) {
					flagC = taskAttribute;
					stateC = state;
				} else if (state.getMetaData().getLabel().equals("BugFlag4")) {
					flagD = taskAttribute;
					stateD = state;
				}
			}
		}
		assertNotNull(flagA);
		assertNotNull(flagB);
		assertNotNull(flagC);
		assertNotNull(flagD);
		assertNotNull(stateA);
		assertNotNull(stateB);
		assertNotNull(stateC);
		assertNotNull(stateD);
		assertEquals("flagA is set(wrong precondidion)", " ", stateA.getValue());
		assertEquals("flagB is set(wrong precondidion)", " ", stateB.getValue());
		assertEquals("flagC is set(wrong precondidion)", " ", stateC.getValue());
		assertEquals("flagD is set(wrong precondidion)", " ", stateD.getValue());
		assertEquals("task.common.kind.flag_type1", flagA.getId());
		assertEquals("task.common.kind.flag_type2", flagB.getId());
		assertEquals("task.common.kind.flag_type5", flagC.getId());
		assertEquals("task.common.kind.flag_type6", flagD.getId());
		Map<String, String> optionA = stateA.getOptions();
		Map<String, String> optionB = stateB.getOptions();
		Map<String, String> optionC = stateC.getOptions();
		Map<String, String> optionD = stateD.getOptions();
		assertEquals(true, optionA.containsKey(""));
		assertEquals(false, optionA.containsKey("?"));
		assertEquals(true, optionA.containsKey("+"));
		assertEquals(true, optionA.containsKey("-"));
		assertEquals(true, optionB.containsKey(""));
		assertEquals(true, optionB.containsKey("?"));
		assertEquals(true, optionB.containsKey("+"));
		assertEquals(true, optionB.containsKey("-"));
		assertEquals(true, optionC.containsKey(""));
		assertEquals(true, optionC.containsKey("?"));
		assertEquals(true, optionC.containsKey("+"));
		assertEquals(true, optionC.containsKey("-"));
		assertEquals(true, optionD.containsKey(""));
		assertEquals(true, optionD.containsKey("?"));
		assertEquals(true, optionD.containsKey("+"));
		assertEquals(true, optionD.containsKey("-"));
		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		stateA.setValue("+");
		stateB.setValue("?");
		stateC.setValue("?");
		stateD.setValue("?");
		TaskAttribute requesteeD = flagD.getAttribute("requestee");
		requesteeD.setValue("guest@mylyn.eclipse.org");
		changed.add(flagA);
		changed.add(flagB);
		changed.add(flagC);
		changed.add(flagD);

		BugzillaFixture.current().submitTask(taskData, client);
		taskData = BugzillaFixture.current().getTask(taskNumber, client);
		assertNotNull(taskData);
		a = taskData.getRoot().getAttributes().values();
		flagA = null;
		flagB = null;
		flagC = null;
		TaskAttribute flagC2 = null;
		flagD = null;
		stateA = null;
		stateB = null;
		stateC = null;
		TaskAttribute stateC2 = null;
		stateD = null;
		for (TaskAttribute taskAttribute : a) {
			if (taskAttribute.getId().startsWith("task.common.kind.flag")) {
				TaskAttribute state = taskAttribute.getAttribute("state");
				if (state.getMetaData().getLabel().equals("BugFlag1")) {
					flagA = taskAttribute;
					stateA = state;
				} else if (state.getMetaData().getLabel().equals("BugFlag2")) {
					flagB = taskAttribute;
					stateB = state;
				} else if (state.getMetaData().getLabel().equals("BugFlag3")) {
					if (flagC == null) {
						flagC = taskAttribute;
						stateC = state;
					} else {
						flagC2 = taskAttribute;
						stateC2 = state;
					}
				} else if (state.getMetaData().getLabel().equals("BugFlag4")) {
					flagD = taskAttribute;
					stateD = state;
				}
			}
		}
		assertNotNull(flagA);
		assertNotNull(flagB);
		assertNotNull(flagC);
		assertNotNull(flagC2);
		assertNotNull(flagD);
		assertNotNull(stateA);
		assertNotNull(stateB);
		assertNotNull(stateC);
		assertNotNull(stateC2);
		assertNotNull(stateD);
		assertEquals("+", stateA.getValue());
		assertEquals("?", stateB.getValue());
		assertEquals("?", stateC.getValue());
		assertEquals(" ", stateC2.getValue());
		assertEquals("?", stateD.getValue());
		requesteeD = flagD.getAttribute("requestee");
		assertNotNull(requesteeD);
		assertEquals("guest@mylyn.eclipse.org", requesteeD.getValue());
		stateA.setValue(" ");
		stateB.setValue(" ");
		stateC.setValue(" ");
		stateD.setValue(" ");
		changed.add(flagA);
		changed.add(flagB);
		changed.add(flagC);
		changed.add(flagD);

		BugzillaFixture.current().submitTask(taskData, client);
		taskData = BugzillaFixture.current().getTask(taskNumber, client);
		assertNotNull(taskData);
		a = taskData.getRoot().getAttributes().values();
		flagA = null;
		flagB = null;
		flagC = null;
		flagC2 = null;
		flagD = null;
		stateA = null;
		stateB = null;
		stateC = null;
		stateC2 = null;
		stateD = null;
		for (TaskAttribute taskAttribute : a) {
			if (taskAttribute.getId().startsWith("task.common.kind.flag")) {
				TaskAttribute state = taskAttribute.getAttribute("state");
				if (state.getMetaData().getLabel().equals("BugFlag1")) {
					flagA = taskAttribute;
					stateA = state;
				} else if (state.getMetaData().getLabel().equals("BugFlag2")) {
					flagB = taskAttribute;
					stateB = state;
				} else if (state.getMetaData().getLabel().equals("BugFlag3")) {
					if (flagC == null) {
						flagC = taskAttribute;
						stateC = state;
					} else {
						flagC2 = taskAttribute;
						stateC2 = state;
					}
				} else if (state.getMetaData().getLabel().equals("BugFlag4")) {
					flagD = taskAttribute;
					stateD = state;
				}
			}
		}
		assertNotNull(flagA);
		assertNotNull(flagB);
		assertNotNull(flagC);
		assertNull(flagC2);
		assertNotNull(flagD);
		assertNotNull(stateA);
		assertNotNull(stateB);
		assertNotNull(stateC);
		assertNull(stateC2);
		assertNotNull(stateD);
		assertEquals(" ", stateA.getValue());
		assertEquals(" ", stateB.getValue());
		assertEquals(" ", stateC.getValue());
		assertEquals(" ", stateD.getValue());
		requesteeD = flagD.getAttribute("requestee");
		assertNotNull(requesteeD);
		assertEquals("", requesteeD.getValue());
	}

	public void testCustomAttributes() throws Exception {

		BugzillaVersion version = new BugzillaVersion(BugzillaFixture.current().getVersion());
		if (version.isSmallerOrEquals(BugzillaVersion.BUGZILLA_3_2)) {
			return;
		}

		String taskNumber = "1";
		TaskData taskData = BugzillaFixture.current().getTask(taskNumber, client);
		assertNotNull(taskData);
		TaskMapper mapper = new TaskMapper(taskData);
		assertEquals(taskNumber, taskData.getTaskId());

//		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//		assertEquals(format1.parse("2009-09-16 14:11"), mapper.getCreationDate());

		AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
		assertNotNull("credentials are null", credentials);
		assertNotNull("Repositor User not set", credentials.getUserName());
		assertNotNull("no password for Repository", credentials.getPassword());

		TaskAttribute colorAttribute = mapper.getTaskData().getRoot().getAttribute("cf_multiselect");
		assertNotNull("TaskAttribute Color did not exists", colorAttribute);
		List<String> theColors = colorAttribute.getValues();
		assertNotNull(theColors);
		assertFalse("no colors set", theColors.isEmpty());

		boolean red = false;
		boolean green = false;
		boolean yellow = false;
		boolean blue = false;

		for (Object element : theColors) {
			String string = (String) element;

			if (!red && string.compareTo("Red") == 0) {
				red = true;
			} else if (!green && string.compareTo("Green") == 0) {
				green = true;
			} else if (!yellow && string.compareTo("Yellow") == 0) {
				yellow = true;
			} else if (!blue && string.compareTo("Blue") == 0) {
				blue = true;
			}
		}
		changeCollorAndSubmit(taskData, colorAttribute, red, green, yellow, blue);
		taskData = BugzillaFixture.current().getTask(taskNumber, client);
		assertNotNull(taskData);
		mapper = new TaskMapper(taskData);

		colorAttribute = mapper.getTaskData().getRoot().getAttribute("cf_multiselect");
		assertNotNull("TaskAttribute Color did not exists", colorAttribute);
		theColors = colorAttribute.getValues();
		assertNotNull(theColors);
		assertFalse("no colors set", theColors.isEmpty());
		boolean red_new = false;
		boolean green_new = false;
		boolean yellow_new = false;
		boolean blue_new = false;

		for (Object element : theColors) {
			String string = (String) element;

			if (!red_new && string.compareTo("Red") == 0) {
				red_new = true;
			} else if (!green_new && string.compareTo("Green") == 0) {
				green_new = true;
			} else if (!yellow_new && string.compareTo("Yellow") == 0) {
				yellow_new = true;
			} else if (!blue_new && string.compareTo("Blue") == 0) {
				blue_new = true;
			}
		}
		assertTrue("wrong change",
				(!red && green && !yellow && !blue && red_new && green_new && !yellow_new && !blue_new)
						|| (red && green && !yellow && !blue && !red_new && green_new && !yellow_new && !blue_new));
		changeCollorAndSubmit(taskData, colorAttribute, red_new, green_new, yellow_new, blue_new);

	}

	private void changeCollorAndSubmit(TaskData taskData, TaskAttribute colorAttribute, boolean red, boolean green,
			boolean yellow, boolean blue) throws Exception {
		if (!red && green && !yellow && !blue) {
			List<String> newValue = new ArrayList<String>(2);
			newValue.add("Red");
			newValue.add("Green");
			colorAttribute.setValues(newValue);
			Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
			changed.add(colorAttribute);
			// Submit changes
			BugzillaFixture.current().submitTask(taskData, client);
		} else if (red && green && !yellow && !blue) {
			List<String> newValue = new ArrayList<String>(2);
			newValue.add("Green");
			colorAttribute.setValues(newValue);
			Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
			changed.add(colorAttribute);
			// Submit changes
			BugzillaFixture.current().submitTask(taskData, client);
		}
	}

	public void testCustomAttributesNewTask() throws Exception {
		TaskData taskData = BugzillaFixture.current().createTask(PrivilegeLevel.USER, null, null);
		assertNotNull(taskData);
		assertNotNull(taskData.getRoot().getAttribute("token"));
		TaskAttribute productAttribute = taskData.getRoot().getAttribute(BugzillaAttribute.PRODUCT.getKey());
		assertNotNull(productAttribute);
		assertEquals("ManualTest" + "", productAttribute.getValue());
		TaskAttribute cfAttribute1 = taskData.getRoot().getAttribute("cf_freetext");
		assertNotNull(cfAttribute1);
		TaskAttribute cfAttribute2 = taskData.getRoot().getAttribute("cf_dropdown");
		assertNotNull(cfAttribute2);
		TaskAttribute cfAttribute3 = taskData.getRoot().getAttribute("cf_largetextbox");
		assertNotNull(cfAttribute3);
		TaskAttribute cfAttribute4 = taskData.getRoot().getAttribute("cf_multiselect");
		assertNotNull(cfAttribute4);
		TaskAttribute cfAttribute5 = taskData.getRoot().getAttribute("cf_datetime");
		assertNotNull(cfAttribute5);
		TaskAttribute cfAttribute6 = taskData.getRoot().getAttribute("cf_bugid");
		assertNotNull(cfAttribute6);
	}

	public void testLeadingZeros() throws Exception {
		String taskNumber = "0010";
		TaskData taskData = BugzillaFixture.current().getTask(taskNumber, client);
		assertNotNull(taskData);
		assertNotNull(taskData);
		TaskAttribute idAttribute = taskData.getRoot().getAttribute(BugzillaAttribute.BUG_ID.getKey());
		assertNotNull(idAttribute);
		assertEquals("10", idAttribute.getValue());
	}

}
