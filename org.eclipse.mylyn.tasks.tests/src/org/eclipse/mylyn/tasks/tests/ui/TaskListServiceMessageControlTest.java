/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.ui;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskListServiceMessageControl;

/**
 * @author Steffen Pingel
 */
public class TaskListServiceMessageControlTest extends TestCase {

	public void testGetAction() {
		assertEquals("abc", TaskListServiceMessageControl.getAction("ABC"));
		assertEquals("abc", TaskListServiceMessageControl.getAction("abc"));
		assertEquals("def", TaskListServiceMessageControl.getAction("http://eclipse.org?action=DEF"));
		assertEquals("defg", TaskListServiceMessageControl.getAction("http://eclipse.org?action=defg&foo=bar"));
		assertEquals(null, TaskListServiceMessageControl.getAction("http://eclipse.org?foo=bar&action=defg"));
	}

}
