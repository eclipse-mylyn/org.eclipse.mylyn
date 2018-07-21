/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
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

/**
 * Tests should be run against Bugzilla 3.2.4 or greater
 * 
 * @author Frank Becker
 * @author Robert Elves
 */
public class BugzillaCustomFieldsTest extends TestCase {

	private TaskData fruitTaskData;

	public void testCustomAttributes() throws Exception {
		String taskID = taskCustomFieldExists();
		if (taskID == null) {
			taskID = createCustomFieldTask();
		}
		String taskNumber = taskID;
		TaskData taskData = BugzillaFixture.current().getTask(taskNumber, BugzillaFixture.current().client());
		assertNotNull(taskData);
		TaskMapper mapper = new TaskMapper(taskData);
		assertEquals(taskNumber, taskData.getTaskId());

//		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//		assertEquals(format1.parse("2009-09-16 14:11"), mapper.getCreationDate());

		AuthenticationCredentials credentials = BugzillaFixture.current()
				.repository()
				.getCredentials(AuthenticationType.REPOSITORY);
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
		taskData = BugzillaFixture.current().getTask(taskNumber, BugzillaFixture.current().client());
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
			BugzillaFixture.current().submitTask(taskData, BugzillaFixture.current().client());
		} else if (red && green && !yellow && !blue) {
			List<String> newValue = new ArrayList<String>(2);
			newValue.add("Green");
			colorAttribute.setValues(newValue);
			Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
			changed.add(colorAttribute);
			// Submit changes
			BugzillaFixture.current().submitTask(taskData, BugzillaFixture.current().client());
		}
	}

	public void testCustomAttributesNewTask() throws Exception {

		BugzillaVersion version = new BugzillaVersion(BugzillaFixture.current().getVersion());
		if (version.isSmallerOrEquals(BugzillaVersion.BUGZILLA_3_2)) {
			return;
		}

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

	public void testCustomFields() throws Exception {

		String taskNumber = "1";

		fruitTaskData = BugzillaFixture.current().getTask(taskNumber, BugzillaFixture.current().client());
		assertNotNull(fruitTaskData);

		if (fruitTaskData.getRoot().getAttribute("cf_dropdown").getValue().equals("---")) {
			setFruitValueTo("one");
			setFruitValueTo("two");
			setFruitValueTo("---");
		} else if (fruitTaskData.getRoot().getAttribute("cf_dropdown").getValue().equals("one")) {
			setFruitValueTo("two");
			setFruitValueTo("one");
			setFruitValueTo("---");
		} else if (fruitTaskData.getRoot().getAttribute("cf_dropdown").getValue().equals("two")) {
			setFruitValueTo("one");
			setFruitValueTo("two");
			setFruitValueTo("---");
		}
		if (fruitTaskData != null) {
			fruitTaskData = null;
		}
	}

	private void setFruitValueTo(String newValue) throws Exception {
		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		TaskAttribute cf_fruit = fruitTaskData.getRoot().getAttribute("cf_dropdown");
		cf_fruit.setValue(newValue);
		assertEquals(newValue, fruitTaskData.getRoot().getAttribute("cf_dropdown").getValue());
		changed.add(cf_fruit);
		BugzillaFixture.current().submitTask(fruitTaskData, BugzillaFixture.current().client());
		fruitTaskData = BugzillaFixture.current()
				.getTask(fruitTaskData.getTaskId(), BugzillaFixture.current().client());
		assertEquals(newValue, fruitTaskData.getRoot().getAttribute("cf_dropdown").getValue());
	}

	private static TaskData createTaskData(TaskRepository taskRepository, ITaskMapping initializationData,
			ITaskMapping selectionData, IProgressMonitor monitor) throws CoreException {
		AbstractRepositoryConnector connector = BugzillaFixture.current().connector();
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(taskRepository);
		TaskData taskData = new TaskData(mapper, taskRepository.getConnectorKind(), taskRepository.getRepositoryUrl(),
				""); //$NON-NLS-1$
		boolean result = taskDataHandler.initializeTaskData(taskRepository, taskData, initializationData, monitor);
		if (!result) {
			throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Initialization of task failed. The provided data is insufficient.")); //$NON-NLS-1$
		}
		if (selectionData != null) {
			connector.getTaskMapping(taskData).merge(selectionData);
		}
		return taskData;
	}

	private String taskCustomFieldExists() {
		String taskID = null;
		String queryUrlString = BugzillaFixture.current().repository().getRepositoryUrl() + "/buglist.cgi?"
				+ "short_desc=test%20Bug%20with%20Custom%20Fields&resolution=---&query_format=advanced"
				+ "&short_desc_type=casesubstring&component=ManualC2&product=ManualTest";
		RepositoryQuery query = new RepositoryQuery(BugzillaFixture.current().repository().getConnectorKind(),
				"handle-testQueryViaConnector");
		query.setUrl(queryUrlString);
		final Map<Integer, TaskData> changedTaskData = new HashMap<Integer, TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {
			@Override
			public void accept(TaskData taskData) {
				changedTaskData.put(Integer.valueOf(taskData.getTaskId()), taskData);
			}
		};
		BugzillaFixture.current()
				.connector()
				.performQuery(BugzillaFixture.current().repository(), query, collector, null, new NullProgressMonitor());
		if (changedTaskData.size() > 0) {
			Set<Integer> ks = changedTaskData.keySet();
			SortedSet<Integer> sks = new TreeSet<Integer>(ks);
			taskID = sks.last().toString();
		}
		return taskID;
	}

	private String createCustomFieldTask() throws Exception {
		final TaskMapping taskMappingInit = new TaskMapping() {
			@Override
			public String getProduct() {
				return "ManualTest";
			}
		};
		final TaskMapping taskMappingSelect = new TaskMapping() {
			@Override
			public String getComponent() {
				return "ManualC2";
			}

			@Override
			public String getSummary() {
				return "test Bug with Custom Fields";
			}

			@Override
			public String getDescription() {
				return "The Description of the test with Custom Fields Bug";
			}
		};
		final TaskData[] taskDataNew = new TaskData[1];

		// create Task
		taskDataNew[0] = createTaskData(BugzillaFixture.current().repository(), taskMappingInit, taskMappingSelect,
				null);

		RepositoryResponse response = BugzillaFixture.current().submitTask(taskDataNew[0],
				BugzillaFixture.current().client());

		assertNotNull(response);
		assertEquals(ResponseKind.TASK_CREATED.toString(), response.getReposonseKind().toString());
		String taskId = response.getTaskId();

		TaskData taskData = BugzillaFixture.current().getTask(taskId, BugzillaFixture.current().client());
		assertNotNull(taskData);

		TaskMapper mapper = new TaskMapper(taskData);
		TaskAttribute cf_multiselect = mapper.getTaskData().getRoot().getAttribute("cf_multiselect");
		cf_multiselect.setValue("Green");
		response = BugzillaFixture.current().submitTask(taskData, BugzillaFixture.current().client());

		return taskId;
	}

}
