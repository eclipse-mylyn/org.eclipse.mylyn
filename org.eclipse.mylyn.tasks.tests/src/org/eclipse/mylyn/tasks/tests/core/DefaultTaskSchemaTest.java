/*******************************************************************************
 * Copyright (c) 2011, 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Field;
import org.eclipse.mylyn.tasks.core.data.DefaultTaskSchema;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 * @author Miles Parker
 */
public class DefaultTaskSchemaTest extends TestCase {

	private class TestSchema extends AbstractTaskSchema {

		private final DefaultTaskSchema parent = DefaultTaskSchema.getInstance();

		public final Field SUMMARY = inheritFrom(parent.SUMMARY).create();

		public final Field SUMMARY_READ_ONLY = inheritFrom(parent.SUMMARY).addFlags(Flag.READ_ONLY).create();

		public final Field PRODUCT_REQUIRED = inheritFrom(parent.PRODUCT).addFlags(Flag.REQUIRED).create();

		public final Field REPORTER = inheritFrom(parent.USER_REPORTER).create();

		public final Field REPORTER_MODIFIED = inheritFrom(parent.USER_REPORTER).addFlags(Flag.READ_ONLY)
				.removeFlags(Flag.READ_ONLY, Flag.PEOPLE)
				.create();

	}

	public void testInheritFlags() {
		TestSchema schema = new TestSchema();
		assertEquals(TaskAttribute.SUMMARY, schema.SUMMARY.getKey());
		assertEquals(TaskAttribute.SUMMARY, schema.SUMMARY_READ_ONLY.getKey());
		assertEquals(null, schema.SUMMARY_READ_ONLY.getKind());
		assertTrue(schema.SUMMARY_READ_ONLY.isReadOnly());
	}

	public void testInheritFlagsAddRemoveFlag() {
		TestSchema schema = new TestSchema();
		assertEquals(TaskAttribute.KIND_PEOPLE, schema.REPORTER.getKind());
		assertTrue(schema.REPORTER.isReadOnly());
		assertEquals(TaskAttribute.USER_REPORTER, schema.REPORTER_MODIFIED.getKey());
		assertEquals(null, schema.REPORTER_MODIFIED.getKind());
		assertFalse(schema.REPORTER_MODIFIED.isReadOnly());
	}

	public void testIterator() {
		AbstractTaskSchema schema = new DefaultTaskSchema();
		Iterator<Field> fields = schema.getFields().iterator();
		int i = 0;
		Set<String> attributeIds = new HashSet<String>();
		while (fields.hasNext()) {
			Field next = fields.next();
			attributeIds.add(next.getKey());
			i++;
		}
		//Let's allow for adding fields to default schema without breaking test, but assume that no-existing attributes will be removed
		assertTrue("Actual Attribute Count: " + i, i >= 40);
		assertTrue(attributeIds.contains(TaskAttribute.ADD_SELF_CC));
		assertTrue(attributeIds.contains(TaskAttribute.ATTACHMENT_AUTHOR));
		assertTrue(attributeIds.contains(TaskAttribute.SUMMARY));
	}

	public void testInitializeTaskData() {
		AbstractTaskSchema schema = new DefaultTaskSchema();
		TaskData testData = new TaskData(new TaskAttributeMapper(new TaskRepository("mock", "http://mock")), "mock",
				"http://mock", "-1");
		schema.initialize(testData);
		int size = testData.getRoot().getAttributes().values().size();
		//Let's allow for adding fields to default schema without breaking test, but assume that no-existing attributes will be removed
		assertTrue("Actual Attribute Count: " + size, size >= 40);
		assertNotNull(testData.getRoot().getAttribute(TaskAttribute.ADD_SELF_CC));
		assertNotNull(testData.getRoot().getAttribute(TaskAttribute.ATTACHMENT_ID));
		assertNotNull(testData.getRoot().getAttribute(TaskAttribute.SUMMARY));
	}

	public void testDescriptionHasKind() {
		assertEquals(TaskAttribute.KIND_DESCRIPTION, DefaultTaskSchema.getInstance().DESCRIPTION.getKind());
	}

	public void testRequiredAttribute() {
		TestSchema schema = new TestSchema();
		assertEquals(TaskAttribute.PRODUCT, schema.PRODUCT_REQUIRED.getKey());
		TaskData testData = new TaskData(new TaskAttributeMapper(new TaskRepository("mock", "http://mock")), "mock",
				"http://mock", "-1");
		schema.initialize(testData);
		assertNotNull(testData.getRoot().getAttribute(TaskAttribute.USER_REPORTER));
		assertFalse("USER_REPORTER should be not required", testData.getRoot()
				.getAttribute(TaskAttribute.USER_REPORTER)
				.getMetaData()
				.isRequired());
		assertNotNull(testData.getRoot().getAttribute(TaskAttribute.PRODUCT));
		assertTrue("PRODUCT should be required", testData.getRoot()
				.getAttribute(TaskAttribute.PRODUCT)
				.getMetaData()
				.isRequired());
	}

}
