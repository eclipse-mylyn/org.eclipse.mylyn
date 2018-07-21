/*******************************************************************************
 * Copyright (c) 2004, 2013 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskInitializationData;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Frank Becker
 * @author Rob Elves
 */
public class BugzillaTaskDataHandlerTest extends TestCase {

	private TaskRepository repository;

	private BugzillaRepositoryConnector connector;

	@Override
	public void setUp() throws Exception {
		repository = BugzillaFixture.current().repository();
		connector = BugzillaFixture.current().connector();
	}

	public void testCloneTaskData() throws Exception {
		TaskData taskData = BugzillaFixture.current().createTask(PrivilegeLevel.USER, "test summary for clone",
				"test description for clone");
		taskData.getRoot().getMappedAttribute(TaskAttribute.PRIORITY).setValue("P5");
		ITaskMapping mapping = connector.getTaskMapping(taskData);
		TaskInitializationData taskSelection = new TaskInitializationData();
		taskSelection.setDescription("Test description");

		TaskAttribute attrDescription = mapping.getTaskData().getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION);
		if (attrDescription != null) {
			attrDescription.getMetaData().setReadOnly(false);
		}

		mapping.merge(taskSelection);
		assertEquals("test summary for clone", mapping.getSummary());
		assertEquals("Test description", mapping.getDescription());

	}

	public void testCharacterEscaping() throws Exception {
		TaskData taskData = BugzillaFixture.current().createTask(PrivilegeLevel.USER, "Testing! \"&@ $\" &amp;", null);
		assertEquals("Testing! \"&@ $\" &amp;", taskData.getRoot()
				.getAttribute(BugzillaAttribute.SHORT_DESC.getKey())
				.getValue());
	}

	public void testinitializeTaskData() throws Exception {
		final TaskMapping taskMappingInit = new TaskMapping() {
			@Override
			public String getSummary() {
				return "The Summary";
			}

			@Override
			public String getDescription() {
				return "The Description";
			}
		};
		final TaskMapping taskMappingSelect = new TaskMapping() {
			@Override
			public String getProduct() {
				return "TestProduct";
			}
		};

		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(repository);
		TaskData taskData = new TaskData(mapper, repository.getConnectorKind(), repository.getRepositoryUrl(), "");
		assertTrue(taskDataHandler.initializeTaskData(repository, taskData, null, null));
		assertTrue(taskDataHandler.initializeTaskData(repository, taskData, taskMappingInit, null));
		assertTrue(taskDataHandler.initializeTaskData(repository, taskData, taskMappingSelect, null));
	}

	public void testPropertyTargetMilestoneUndefined() throws Exception {
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		repository.removeProperty(IBugzillaConstants.BUGZILLA_PARAM_USETARGETMILESTONE);
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(repository);
		TaskData taskData = new TaskData(mapper, repository.getConnectorKind(), repository.getRepositoryUrl(), "");
		assertTrue(taskDataHandler.initializeTaskData(repository, taskData, null, null));
		assertNotNull(taskData.getRoot().getAttribute(BugzillaAttribute.TARGET_MILESTONE.getKey()));
	}

	public void testPropertyTargetMilestoneTrue() throws Exception {
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		repository.setProperty(IBugzillaConstants.BUGZILLA_PARAM_USETARGETMILESTONE, Boolean.TRUE.toString());
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(repository);
		TaskData taskData = new TaskData(mapper, repository.getConnectorKind(), repository.getRepositoryUrl(), "");
		assertTrue(taskDataHandler.initializeTaskData(repository, taskData, null, null));
		assertNotNull(taskData.getRoot().getAttribute(BugzillaAttribute.TARGET_MILESTONE.getKey()));
	}

	public void testPropertyTargetMilestoneFalse() throws Exception {
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		repository.setProperty(IBugzillaConstants.BUGZILLA_PARAM_USETARGETMILESTONE, Boolean.FALSE.toString());
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(repository);
		TaskData taskData = new TaskData(mapper, repository.getConnectorKind(), repository.getRepositoryUrl(), "");
		assertTrue(taskDataHandler.initializeTaskData(repository, taskData, null, null));
		assertNull(taskData.getRoot().getAttribute(BugzillaAttribute.TARGET_MILESTONE.getKey()));
	}
}
