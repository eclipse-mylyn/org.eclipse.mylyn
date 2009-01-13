/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class TaskTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testUrl() {
		AbstractTask task = new LocalTask("handle", "label");
		task.setUrl("http://eclipse.org/mylyn/doc");
		assertTrue(TasksUiInternal.isValidUrl(task.getUrl()));

		task.setUrl("http://");
		assertFalse(TasksUiInternal.isValidUrl(task.getUrl()));

		task.setUrl("https://");
		assertFalse(TasksUiInternal.isValidUrl(task.getUrl()));

		task.setUrl("");
		assertFalse(TasksUiInternal.isValidUrl(task.getUrl()));

		task.setUrl(null);
		assertFalse(TasksUiInternal.isValidUrl(task.getUrl()));
	}

	public void testPriorityNeverNull() {
		ITask task = new LocalTask("handle", "label");
		assertNotNull(task.getPriority());

		PriorityLevel def = PriorityLevel.getDefault();
		assertNotNull(def);
		assertEquals(def, PriorityLevel.fromDescription("garbage"));
		assertEquals(def, PriorityLevel.fromString("garbage"));
	}

	public void testPriorityLevelFromLevel() {
		assertEquals(PriorityLevel.P1, PriorityLevel.fromLevel(Integer.MIN_VALUE));
		assertEquals(PriorityLevel.P1, PriorityLevel.fromLevel(-1));
		assertEquals(PriorityLevel.P1, PriorityLevel.fromLevel(0));
		assertEquals(PriorityLevel.P1, PriorityLevel.fromLevel(1));
		assertEquals(PriorityLevel.P2, PriorityLevel.fromLevel(2));
		assertEquals(PriorityLevel.P3, PriorityLevel.fromLevel(3));
		assertEquals(PriorityLevel.P4, PriorityLevel.fromLevel(4));
		assertEquals(PriorityLevel.P5, PriorityLevel.fromLevel(5));
		assertEquals(PriorityLevel.P5, PriorityLevel.fromLevel(6));
		assertEquals(PriorityLevel.P5, PriorityLevel.fromLevel(Integer.MAX_VALUE));
	}
}
