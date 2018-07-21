/*******************************************************************************
 * Copyright (c) 2012, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.core;

import junit.framework.TestCase;

import org.eclipse.mylyn.tasks.core.TaskInitializationData;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Benjamin Muskalla
 */
public class TaskInitializationDataTest extends TestCase {

	private TaskInitializationData data;

	@Override
	protected void setUp() throws Exception {
		data = new TaskInitializationData();
	}

	public void testNotSupported() {
		try {
			data.merge(null);
			fail("Should not be supported");
		} catch (UnsupportedOperationException e) {
			// expected
		}
		assertNull(data.getTaskStatus());
		assertNull(data.getTaskData());
		assertNull(data.getPriorityLevel());
		assertNull(data.getModificationDate());
		assertNull(data.getKeywords());
		assertNull(data.getDueDate());
		assertNull(data.getCreationDate());
		assertNull(data.getCompletionDate());
		assertNull(data.getCc());
	}

	public void testTaskKind() throws Exception {
		data.setTaskKind("foo");
		assertEquals("foo", data.getTaskKind());
		assertEquals("foo", data.getAttribute(TaskAttribute.TASK_KIND));
	}

	public void testProduct() throws Exception {
		data.setProduct("product");
		assertEquals("product", data.getProduct());
		assertEquals("product", data.getAttribute(TaskAttribute.PRODUCT));
	}

	public void testComponent() throws Exception {
		data.setComponent("component");
		assertEquals("component", data.getComponent());
		assertEquals("component", data.getAttribute(TaskAttribute.COMPONENT));
	}

	public void testSetAttributeComponent() throws Exception {
		data.setAttribute(TaskAttribute.COMPONENT, "component");
		assertEquals("component", data.getComponent());
		assertEquals("component", data.getAttribute(TaskAttribute.COMPONENT));
	}

	public void testGetAttribute() throws Exception {
		assertNull(data.getAttribute("custom"));
		data.setAttribute("custom", "value");
		assertEquals("value", data.getAttribute("custom"));
		data.setAttribute("custom", null);
		assertNull(data.getAttribute("custom"));
	}

}
