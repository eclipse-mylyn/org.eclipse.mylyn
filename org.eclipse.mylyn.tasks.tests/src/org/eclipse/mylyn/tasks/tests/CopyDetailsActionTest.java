/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.ui.actions.CopyTaskDetailsAction;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryTask;

/**
 * @author Mik Kersten
 */
public class CopyDetailsActionTest extends TestCase {

	public void testIdLabelIncluded() {
		MockRepositoryTask task = new MockRepositoryTask("123");		
		String text = CopyTaskDetailsAction.getTextForTask(task);
		assertTrue(text.startsWith(task.getTaskKey()));
	}
	
}
