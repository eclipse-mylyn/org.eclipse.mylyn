/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.core;

import junit.framework.TestCase;

import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tests.util.TestUtil.PrivilegeLevel;

public class BugzillaPriorityTest extends TestCase {

	private TaskRepository repository;

	private BugzillaRepositoryConnector connector;

	@Override
	public void setUp() throws Exception {
		BugzillaFixture.current().client(PrivilegeLevel.USER);
		repository = BugzillaFixture.current().repository();
		connector = BugzillaFixture.current().connector();
	}

	public void testPriority() throws Exception {
		BugzillaVersion version = new BugzillaVersion(BugzillaFixture.current().getVersion());
		boolean useOldWay = version.isSmaller(BugzillaVersion.BUGZILLA_3_6);

		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(repository);
		TaskData taskData = new TaskData(mapper, repository.getConnectorKind(), repository.getRepositoryUrl(), "");
		ITaskMapping mapping = connector.getTaskMapping(taskData);
		taskDataHandler.initializeTaskData(repository, taskData, null, null);
		String value;
		PriorityLevel level;

		level = PriorityLevel.P1;
		value = useOldWay ? level.toString() : "Highest";
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue(value);
		assertEquals(value, taskData.getRoot().getMappedAttribute(TaskAttribute.PRIORITY).getValue());
		assertEquals(level, mapping.getPriorityLevel());
		level = PriorityLevel.P2;
		value = useOldWay ? level.toString() : "High";
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue(value);
		assertEquals(value, taskData.getRoot().getMappedAttribute(TaskAttribute.PRIORITY).getValue());
		assertEquals(level, mapping.getPriorityLevel());
		level = PriorityLevel.P3;
		value = useOldWay ? level.toString() : "Normal";
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue(value);
		assertEquals(value, taskData.getRoot().getMappedAttribute(TaskAttribute.PRIORITY).getValue());
		assertEquals(level, mapping.getPriorityLevel());
		level = PriorityLevel.P4;
		value = useOldWay ? level.toString() : "Low";
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue(value);
		assertEquals(value, taskData.getRoot().getMappedAttribute(TaskAttribute.PRIORITY).getValue());
		assertEquals(level, mapping.getPriorityLevel());
		level = PriorityLevel.P5;
		value = useOldWay ? level.toString() : "Lowest";
		taskData.getRoot().createMappedAttribute(BugzillaAttribute.PRIORITY.getKey()).setValue(value);
		assertEquals(value, taskData.getRoot().getMappedAttribute(TaskAttribute.PRIORITY).getValue());
		assertEquals(level, mapping.getPriorityLevel());
	}
}
