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

package org.eclipse.mylar.tasklist.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.tasks.core.Task;

/**
 * @author Mik Kersten
 */
public class TaskTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testUrl() {
		Task task = new Task("handle", "label", true);
		task.setUrl("http://eclipse.org/mylar/doc.php");
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
}
