/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.core;

import junit.framework.TestCase;

import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema;
import org.eclipse.mylyn.tasks.core.data.DefaultTaskSchema;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Steffen Pingel
 */
public class DefaultTaskSchemaTest extends TestCase {

	private class TestSchema extends AbstractTaskSchema {

		private final DefaultTaskSchema parent = DefaultTaskSchema.getInstance();

		public final Field SUMMARY = inheritFrom(parent.SUMMARY).create();

		public final Field SUMMARY_READ_ONLY = inheritFrom(parent.SUMMARY).addFlags(Flag.READ_ONLY).create();

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

}
