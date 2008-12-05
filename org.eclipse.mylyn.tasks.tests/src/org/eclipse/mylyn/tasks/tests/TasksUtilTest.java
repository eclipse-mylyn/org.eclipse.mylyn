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

import org.eclipse.mylyn.internal.provisional.tasks.core.TasksUtil;

public class TasksUtilTest extends TestCase {

	public void testDecode() {
		assertEquals("abc", TasksUtil.encode("abc"));
		assertEquals("%2D_", TasksUtil.encode("-"));
		assertEquals("abc%2D_123", TasksUtil.encode("abc-123"));
		assertEquals("", TasksUtil.encode(""));
	}

	public void testEncode() {
		assertEquals("abc", TasksUtil.decode("abc"));
		assertEquals("-", TasksUtil.decode("%2D_"));
		assertEquals("abc-123", TasksUtil.decode("abc%2D_123"));
	}

}
