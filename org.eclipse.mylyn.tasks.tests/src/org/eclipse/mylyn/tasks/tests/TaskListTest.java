/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.tasks.core.TaskCategory;
import org.eclipse.mylar.tasks.core.TaskList;


/**
 * @author Mik Kersten
 */
public class TaskListTest extends TestCase {

	public void testGetUserCategories() {
		TaskList taskList = new TaskList();
		taskList.addCategory(new TaskCategory("a", taskList));
		assertEquals(2, taskList.getUserCategories().size());
	}
	
}
