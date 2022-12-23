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

package org.eclipse.mylyn.tasks.tests.data;

import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Benjamin Muskalla
 * @author Miles Parker
 */
public class TaskAttributeTest extends TestCase {

	private TaskAttribute attribute;

	@Override
	protected void setUp() throws Exception {
		TaskRepository taskRepository = new TaskRepository("kind", "repository");
		TaskAttributeMapper mapper = new TaskAttributeMapper(taskRepository);
		TaskData data = new TaskData(mapper, "kind", "repository", "id");
		attribute = new TaskAttribute(data.getRoot(), "test");
	}

	public void testGetValue() {
		attribute.setValue("baz");
		assertEquals("baz", attribute.getValue());
		attribute.setValue("bee");
		assertEquals("bee", attribute.getValue());
	}

	public void testRegularValue() throws Exception {
		attribute.setValue("foo");
		assertEquals("foo", attribute.getValue());
	}

	public void testRegularValues() throws Exception {
		attribute.setValues(Collections.singletonList("foo"));
		assertEquals("foo", attribute.getValue());
	}

	public void testHasValue() {
		assertFalse(attribute.hasValue());

		attribute.setValue("description");
		assertTrue(attribute.hasValue());

		attribute.clearValues();
		assertFalse(attribute.hasValue());

		attribute.setValue("description");
		assertTrue(attribute.hasValue());
	}

	public void testNullValue() throws Exception {
		try {
			attribute.setValue(null);
			fail("Should refuse null");
		} catch (Exception e) {
			// expected
		}
	}

	public void testNullAsValues() throws Exception {
		try {
			attribute.setValues(null);
			fail("Should refuse null");
		} catch (Exception e) {
			// expected
		}
	}

	public void testNullAsList() throws Exception {
		try {
			attribute.setValues(Collections.<String> singletonList(null));
			fail("Should refuse null");
		} catch (Exception e) {
			// expected
		}
	}

	public void testNullInsideValuesList() throws Exception {
		try {
			attribute.setValues(Arrays.asList("foo", null, "bar"));
			fail("Should refuse null");
		} catch (Exception e) {
			// expected
		}
	}

}
