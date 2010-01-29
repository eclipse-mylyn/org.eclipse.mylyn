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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * Tests should be run against Bugzilla 3.2.4 or greater
 * 
 * @author Frank Becker
 * @author Robert Elves
 */
public class BugzillaFlagsTest extends TestCase {

	private BugzillaClient client;

	@Override
	protected void setUp() throws Exception {
		client = BugzillaFixture.current().client();
	}

	@SuppressWarnings("null")
	public void testFlags() throws Exception {
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

}
