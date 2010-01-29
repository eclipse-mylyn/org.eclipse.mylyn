/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tests.util.TestUtil.PrivilegeLevel;

/**
 * Tests should be run against Bugzilla 3.2.4 or greater
 * 
 * @author Frank Becker
 * @author Robert Elves
 */
public class BugzillaCustomFieldsTest extends TestCase {

	private BugzillaClient client;

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		repository = BugzillaFixture.current().repository();
		client = BugzillaFixture.current().client();
	}

	public void testCustomAttributes() throws Exception {

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

		TaskData fruitTaskData = BugzillaFixture.current().getTask(taskNumber, client);
		assertNotNull(fruitTaskData);

		if (fruitTaskData.getRoot().getAttribute("cf_multiselect").getValue().equals("---")) {
			setFruitValueTo(fruitTaskData, "apple");
			setFruitValueTo(fruitTaskData, "orange");
			setFruitValueTo(fruitTaskData, "---");
		} else if (fruitTaskData.getRoot().getAttribute("cf_multiselect").getValue().equals("apple")) {
			setFruitValueTo(fruitTaskData, "orange");
			setFruitValueTo(fruitTaskData, "apple");
			setFruitValueTo(fruitTaskData, "---");
		} else if (fruitTaskData.getRoot().getAttribute("cf_multiselect").getValue().equals("orange")) {
			setFruitValueTo(fruitTaskData, "apple");
			setFruitValueTo(fruitTaskData, "orange");
			setFruitValueTo(fruitTaskData, "---");
		}
		if (fruitTaskData != null) {
			fruitTaskData = null;
		}
	}

	private void setFruitValueTo(TaskData fruitTaskData, String newValue) throws Exception {
		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		TaskAttribute cf_fruit = fruitTaskData.getRoot().getAttribute("cf_fruit");
		cf_fruit.setValue(newValue);
		assertEquals(newValue, fruitTaskData.getRoot().getAttribute("cf_fruit").getValue());
		changed.add(cf_fruit);
		BugzillaFixture.current().submitTask(fruitTaskData, client);
		fruitTaskData = BugzillaFixture.current().getTask(fruitTaskData.getTaskId(), client);
		assertEquals(newValue, fruitTaskData.getRoot().getAttribute("cf_fruit").getValue());
	}

}
