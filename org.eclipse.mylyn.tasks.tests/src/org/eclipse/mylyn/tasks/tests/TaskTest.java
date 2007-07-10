/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;

/**
 * @author Mik Kersten
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
		assertTrue(task.hasValidUrl());

		task.setUrl("http://");
		assertFalse(task.hasValidUrl());

		task.setUrl("https://");
		assertFalse(task.hasValidUrl());

		task.setUrl("");
		assertFalse(task.hasValidUrl());

		task.setUrl(null);
		assertFalse(task.hasValidUrl());
	}

	public void testPriorityNeverNull() {
		AbstractTask task = new LocalTask("handle", "label");
		assertNotNull(task.getPriority());

		PriorityLevel def = PriorityLevel.getDefault();
		assertNotNull(def);
		assertEquals(def, AbstractTask.PriorityLevel.fromDescription("garbage"));
		assertEquals(def, AbstractTask.PriorityLevel.fromString("garbage"));
	}
}
