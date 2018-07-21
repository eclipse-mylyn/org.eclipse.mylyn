/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
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

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttributeMapper;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class BugzillaAttributeMapperTest extends TestCase {

	private BugzillaAttributeMapper mapper;

	private TaskData oldTaskData;

	private TaskData newTaskData;

	@Override
	protected void setUp() throws Exception {
		TaskRepository repository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, "http://repository");
		mapper = new BugzillaAttributeMapper(repository, null);
		oldTaskData = new TaskData(mapper, BugzillaCorePlugin.CONNECTOR_KIND, "http://repository", "id");
		newTaskData = new TaskData(mapper, BugzillaCorePlugin.CONNECTOR_KIND, "http://repository", "id");
	}

	@Test
	public void testEqualsTaskAttributeResolution() {
		TaskAttribute oldAttribute = oldTaskData.getRoot().createAttribute(BugzillaAttribute.RESOLUTION.getKey());
		TaskAttribute newAttribute = newTaskData.getRoot().createAttribute(BugzillaAttribute.RESOLUTION.getKey());

		assertTrue(mapper.equals(newAttribute, oldAttribute));

		oldAttribute.setValue("");
		assertTrue(mapper.equals(newAttribute, oldAttribute));

		newAttribute.setValue("");
		assertTrue(mapper.equals(newAttribute, oldAttribute));
	}

	@Test
	public void testEqualsTaskAttributeDescription() {
		TaskAttribute oldAttribute = oldTaskData.getRoot().createAttribute(BugzillaAttribute.DESC.getKey());
		TaskAttribute newAttribute = newTaskData.getRoot().createAttribute(BugzillaAttribute.DESC.getKey());

		assertTrue(mapper.equals(newAttribute, oldAttribute));

		oldAttribute.setValue("");
		assertFalse(mapper.equals(newAttribute, oldAttribute));

		newAttribute.setValue("");
		assertTrue(mapper.equals(newAttribute, oldAttribute));
	}

	@Test
	public void testEqualsTaskAttributeBugFileLoc() {
		TaskAttribute oldAttribute = oldTaskData.getRoot().createAttribute(BugzillaAttribute.BUG_FILE_LOC.getKey());
		TaskAttribute newAttribute = newTaskData.getRoot().createAttribute(BugzillaAttribute.BUG_FILE_LOC.getKey());

		assertTrue(mapper.equals(newAttribute, oldAttribute));

		oldAttribute.setValue("");
		assertTrue(mapper.equals(newAttribute, oldAttribute));

		newAttribute.setValue("");
		assertTrue(mapper.equals(newAttribute, oldAttribute));
	}

	@Test
	public void testEqualsTaskAttributeStatusWhiteboard() {
		TaskAttribute oldAttribute = oldTaskData.getRoot()
				.createAttribute(BugzillaAttribute.STATUS_WHITEBOARD.getKey());
		TaskAttribute newAttribute = newTaskData.getRoot()
				.createAttribute(BugzillaAttribute.STATUS_WHITEBOARD.getKey());

		assertTrue(mapper.equals(newAttribute, oldAttribute));

		oldAttribute.setValue("");
		assertTrue(mapper.equals(newAttribute, oldAttribute));

		newAttribute.setValue("");
		assertTrue(mapper.equals(newAttribute, oldAttribute));
	}

	@Test
	public void testEqualsTaskAttributeKeywords() {
		TaskAttribute oldAttribute = oldTaskData.getRoot().createAttribute(BugzillaAttribute.KEYWORDS.getKey());
		TaskAttribute newAttribute = newTaskData.getRoot().createAttribute(BugzillaAttribute.KEYWORDS.getKey());

		assertTrue(mapper.equals(newAttribute, oldAttribute));

		oldAttribute.setValue("");
		assertTrue(mapper.equals(newAttribute, oldAttribute));

		newAttribute.setValue("");
		assertTrue(mapper.equals(newAttribute, oldAttribute));

		newAttribute.addValue("AA");
		assertFalse(mapper.equals(newAttribute, oldAttribute));
	}

}
