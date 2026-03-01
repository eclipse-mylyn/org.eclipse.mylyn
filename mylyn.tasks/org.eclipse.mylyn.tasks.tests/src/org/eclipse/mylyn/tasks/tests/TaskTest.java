/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.junit.jupiter.api.Test;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class TaskTest {

	@Test
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

	@Test
	public void testPriorityNeverNull() {
		ITask task = new LocalTask("handle", "label");
		assertNotNull(task.getPriority());

		PriorityLevel def = PriorityLevel.getDefault();
		assertNotNull(def);
		assertEquals(def, PriorityLevel.fromDescription("garbage"));
		assertEquals(def, PriorityLevel.fromString("garbage"));
	}

	@Test
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
