/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.bugzilla.tests.AbstractBugzillaFixtureTest;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests should be run against Bugzilla 3.2.4 or greater
 *
 * @author Frank Becker
 * @author Robert Elves
 */
@SuppressWarnings("nls")
public class BugzillaFlagsTest extends AbstractBugzillaFixtureTest {

	@BeforeEach
	void excludeCheck() {
		assumeFalse(fixture.isExcluded());
	}

	@BeforeEach
	void versionCheck() {
		assumeFalse(fixture.getBugzillaVersion().isSmallerOrEquals(BugzillaVersion.BUGZILLA_3_2));
	}

	private BugzillaClient client;

	private TaskAttribute flagA;

	private TaskAttribute flagB;

	private TaskAttribute flagC;

	private TaskAttribute flagC2;

	private TaskAttribute flagD;

	private TaskAttribute stateA;

	private TaskAttribute stateB;

	private TaskAttribute stateC;

	private TaskAttribute stateC2;

	private TaskAttribute stateD;

	@BeforeEach
	void checkVersion() {
		BugzillaVersion version = new BugzillaVersion(fixture.getVersion());
		assumeTrue(!version.isSmallerOrEquals(BugzillaVersion.BUGZILLA_3_2),
				"Custom fields not supported in this Bugzilla version");
	}

	@BeforeEach
	void setUp() throws Exception {
		client = fixture.client();
	}

	@Test
	public void testFlags() throws Exception {
		String taskNumber = "2";
		TaskData taskData = fixture.getTask(taskNumber, client);
		assertNotNull(taskData);

		if (flagTests(taskData, true)) {
			changeFromSpace(taskData);
			taskData = fixture.getTask(taskNumber, client);
			if (flagTests(taskData, false)) {
				changeToSpace(taskData);
			}
		} else {
			changeToSpace(taskData);
			taskData = fixture.getTask(taskNumber, client);
			if (flagTests(taskData, true)) {
				changeFromSpace(taskData);
			}

		}
	}

	private void changeFromSpace(TaskData taskData) throws IOException, CoreException {
		assertEquals(BugzillaAttribute.KIND_FLAG_TYPE + "1", flagA.getId());
		assertEquals(BugzillaAttribute.KIND_FLAG_TYPE + "2", flagB.getId());
		assertEquals(BugzillaAttribute.KIND_FLAG_TYPE + "5", flagC.getId());
		assertEquals(BugzillaAttribute.KIND_FLAG_TYPE + "6", flagD.getId());
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
		Set<TaskAttribute> changed = new HashSet<>();
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

		fixture.submitTask(taskData, client);
	}

	private void changeToSpace(TaskData taskData) throws IOException, CoreException {
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
		Set<TaskAttribute> changed = new HashSet<>();
		stateA.setValue(" ");
		stateB.setValue(" ");
		stateC.setValue(" ");
		stateD.setValue(" ");
		changed.add(flagA);
		changed.add(flagB);
		changed.add(flagC);
		changed.add(flagD);

		fixture.submitTask(taskData, client);
	}

	private boolean flagTests(TaskData taskData, boolean testSpace) {
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

		for (TaskAttribute taskAttribute : taskData.getRoot().getAttributes().values()) {
			if (taskAttribute.getId().startsWith(BugzillaAttribute.KIND_FLAG)) {
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
		assertNotNull(flagD);
		assertNotNull(stateA);
		assertNotNull(stateB);
		assertNotNull(stateC);
		assertNotNull(stateD);
		TaskAttribute requesteeD = flagD.getAttribute("requestee");

		if (testSpace) {
			return flagC2 == null && stateC2 == null && " ".equals(stateA.getValue()) && " ".equals(stateB.getValue())
					&& " ".equals(stateC.getValue()) && " ".equals(stateD.getValue());
		} else {
			return flagC2 != null && stateC2 != null && "+".equals(stateA.getValue()) && "?".equals(stateB.getValue())
					&& "?".equals(stateC.getValue()) && " ".equals(stateC2.getValue()) && "?".equals(stateD.getValue())
					&& "guest@mylyn.eclipse.org".equals(requesteeD.getValue());
		}
	}
}
