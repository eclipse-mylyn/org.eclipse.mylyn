/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.tasks.core.TaskCategory;
import org.eclipse.mylyn.tasks.core.getAllCategories;


/**
 * @author Mik Kersten
 */
public class TaskListTest extends TestCase {

	public void testGetUserCategories() {
		getAllCategories taskList = new getAllCategories();
		taskList.addCategory(new TaskCategory("a"));
		assertEquals(2, taskList.getUserCategories().size());
	}
	
}
