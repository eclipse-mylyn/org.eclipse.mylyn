/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryTask.PriorityLevel;

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
		AbstractRepositoryTask task = new LocalTask("handle", "label");
		task.setTaskUrl("http://eclipse.org/mylar/doc.php");
		assertTrue(task.hasValidUrl());

		task.setTaskUrl("http://");
		assertFalse(task.hasValidUrl());

		task.setTaskUrl("https://");
		assertFalse(task.hasValidUrl());

		task.setTaskUrl("");
		assertFalse(task.hasValidUrl());

		task.setTaskUrl(null);
		assertFalse(task.hasValidUrl());
	}
	
	public void testPriorityNeverNull() {
		AbstractRepositoryTask task = new LocalTask("handle", "label");
		assertNotNull(task.getPriority());
		
		PriorityLevel def = PriorityLevel.getDefault();		
		assertNotNull(def);		
		assertEquals(def, AbstractRepositoryTask.PriorityLevel.fromDescription("garbage"));
		assertEquals(def, AbstractRepositoryTask.PriorityLevel.fromString("garbage"));		
	}
}
