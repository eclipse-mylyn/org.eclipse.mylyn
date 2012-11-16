/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
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
		try {
			data.getTaskStatus();
			fail("Should not be supported");
		} catch (UnsupportedOperationException e) {
			// expected
		}
		try {
			data.getTaskData();
			fail("Should not be supported");
		} catch (UnsupportedOperationException e) {
			// expected
		}
		try {
			data.getPriorityLevel();
			fail("Should not be supported");
		} catch (UnsupportedOperationException e) {
			// expected
		}
		try {
			data.getModificationDate();
			fail("Should not be supported");
		} catch (UnsupportedOperationException e) {
			// expected
		}
		try {
			data.getKeywords();
			fail("Should not be supported");
		} catch (UnsupportedOperationException e) {
			// expected
		}
		try {
			data.getDueDate();
			fail("Should not be supported");
		} catch (UnsupportedOperationException e) {
			// expected
		}
		try {
			data.getCreationDate();
			fail("Should not be supported");
		} catch (UnsupportedOperationException e) {
			// expected
		}
		try {
			data.getCompletionDate();
			fail("Should not be supported");
		} catch (UnsupportedOperationException e) {
			// expected
		}
		try {
			data.getCc();
			fail("Should not be supported");
		} catch (UnsupportedOperationException e) {
			// expected
		}
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
