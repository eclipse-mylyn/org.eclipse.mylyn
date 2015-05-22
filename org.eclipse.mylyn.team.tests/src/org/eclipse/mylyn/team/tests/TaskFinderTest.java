/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.team.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.team.ui.actions.TaskFinder;

/**
 * @author Mik Kersten
 */
public class TaskFinderTest extends TestCase {

	public void test07LegacyMatching() {
		String label = "Progress on: 123: foo \nhttps://bugs.eclipse.org";
		String id = TaskFinder.getTaskIdFromLegacy07Label(label);
		assertEquals("123", id);
	}

	public void testUrlMatching() {
		String label = "bla bla\nhttp://foo.bar-123 bla bla";
		String id = TaskFinder.getUrlFromComment(label);
		assertEquals("http://foo.bar-123", id);
		label = "bla bla\nhttp://foo.bar-1234\n- bla bla";
		id = TaskFinder.getUrlFromComment(label);
		assertEquals("http://foo.bar-1234", id);
	}
}
