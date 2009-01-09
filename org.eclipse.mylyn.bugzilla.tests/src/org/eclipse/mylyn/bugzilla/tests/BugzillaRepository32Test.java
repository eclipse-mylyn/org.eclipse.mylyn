/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;

public class BugzillaRepository32Test extends AbstractBugzillaTest {

	@SuppressWarnings("null")
	public void testFlags() throws Exception {
		init32();
		String taskNumber = "10";
		ITask task = generateLocalTaskAndDownload(taskNumber);
		assertNotNull(task);
		TaskData taskData = TasksUiPlugin.getTaskDataManager().getTaskData(task);
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
				if (state.getMetaData().getLabel().equals("FLAG_A")) {
					flagA = taskAttribute;
					stateA = state;
				} else if (state.getMetaData().getLabel().equals("FLAG_B")) {
					flagB = taskAttribute;
					stateB = state;
				} else if (state.getMetaData().getLabel().equals("FLAG_C")) {
					flagC = taskAttribute;
					stateC = state;
				} else if (state.getMetaData().getLabel().equals("FLAG_D")) {
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
		assertEquals("task.common.kind.flag_type3", flagC.getId());
		assertEquals("task.common.kind.flag_type4", flagD.getId());
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
		requesteeD.setValue("rob.elves@eclipse.org");
		changed.add(flagA);
		changed.add(flagB);
		changed.add(flagC);
		changed.add(flagD);

		submit(task, taskData, changed);
		task = generateLocalTaskAndDownload(taskNumber);
		assertNotNull(task);
		taskData = TasksUiPlugin.getTaskDataManager().getTaskData(task);
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
				if (state.getMetaData().getLabel().equals("FLAG_A")) {
					flagA = taskAttribute;
					stateA = state;
				} else if (state.getMetaData().getLabel().equals("FLAG_B")) {
					flagB = taskAttribute;
					stateB = state;
				} else if (state.getMetaData().getLabel().equals("FLAG_C")) {
					if (flagC == null) {
						flagC = taskAttribute;
						stateC = state;
					} else {
						flagC2 = taskAttribute;
						stateC2 = state;
					}
				} else if (state.getMetaData().getLabel().equals("FLAG_D")) {
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
		assertEquals("rob.elves@eclipse.org", requesteeD.getValue());
		stateA.setValue(" ");
		stateB.setValue(" ");
		stateC.setValue(" ");
		stateD.setValue(" ");
		changed.add(flagA);
		changed.add(flagB);
		changed.add(flagC);
		changed.add(flagD);

		submit(task, taskData, changed);
		task = generateLocalTaskAndDownload(taskNumber);
		assertNotNull(task);
		taskData = TasksUiPlugin.getTaskDataManager().getTaskData(task);
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
				if (state.getMetaData().getLabel().equals("FLAG_A")) {
					flagA = taskAttribute;
					stateA = state;
				} else if (state.getMetaData().getLabel().equals("FLAG_B")) {
					flagB = taskAttribute;
					stateB = state;
				} else if (state.getMetaData().getLabel().equals("FLAG_C")) {
					if (flagC == null) {
						flagC = taskAttribute;
						stateC = state;
					} else {
						flagC2 = taskAttribute;
						stateC2 = state;
					}
				} else if (state.getMetaData().getLabel().equals("FLAG_D")) {
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
		init32();
		String taskNumber = "1";
		ITask task = generateLocalTaskAndDownload(taskNumber);
		assertNotNull(task);
		TaskData taskData = TasksUiPlugin.getTaskDataManager().getTaskData(task);
		assertNotNull(taskData);
		TaskMapper mapper = new TaskMapper(taskData);
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
		assertEquals(taskNumber, taskData.getTaskId());

		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		assertEquals(format1.parse("2008-10-04 15:01"), mapper.getCreationDate());

		AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
		assertNotNull("credentials are null", credentials);
		assertNotNull("Repositor User not set", credentials.getUserName());
		assertNotNull("no password for Repository", credentials.getPassword());

		TaskAttribute colorAttribute = mapper.getTaskData().getRoot().getAttribute("cf_colors");
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
		changeCollorAndSubmit(task, taskData, colorAttribute, red, green, yellow, blue);
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());
		TasksUiInternal.synchronizeTask(connector, task, true, null);
		TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
		assertNotNull(task);
		taskData = TasksUiPlugin.getTaskDataManager().getTaskData(task);
		assertNotNull(taskData);
		mapper = new TaskMapper(taskData);
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());

		colorAttribute = mapper.getTaskData().getRoot().getAttribute("cf_colors");
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
		changeCollorAndSubmit(task, taskData, colorAttribute, red_new, green_new, yellow_new, blue_new);
		assertEquals(SynchronizationState.SYNCHRONIZED, task.getSynchronizationState());

	}

	private void changeCollorAndSubmit(ITask task, TaskData taskData, TaskAttribute colorAttribute, boolean red,
			boolean green, boolean yellow, boolean blue) throws CoreException {
		if (!red && green && !yellow && !blue) {
			List<String> newValue = new ArrayList<String>(2);
			newValue.add("Red");
			newValue.add("Green");
			colorAttribute.setValues(newValue);
			Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
			changed.add(colorAttribute);
			// Submit changes
			submit(task, taskData, changed);
		} else if (red && green && !yellow && !blue) {
			List<String> newValue = new ArrayList<String>(2);
			newValue.add("Green");
			colorAttribute.setValues(newValue);
			Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
			changed.add(colorAttribute);
			// Submit changes
			submit(task, taskData, changed);
		}

	}
}
