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

package org.eclipse.mylyn.ide.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.team.ui.actions.OpenCorrespondingTaskAction;

/**
 * @author Mik Kersten
 */
public class OpenCorrespondingTaskActionTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		// ignore
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		// ignore
		super.tearDown();
	}

	public void test07LegacyMatching() {
		String label = "Progress on: 123: foo \nhttps://bugs.eclipse.org";
		String id = OpenCorrespondingTaskAction.getTaskIdFromLegacy07Label(label);
		assertEquals("123", id);
	}
	
	public void testUrlMatching() {
		String label = "bla bla\nhttp://foo.bar-123 bla bla";
		String id = OpenCorrespondingTaskAction.getUrlFromComment(label);
		assertEquals("http://foo.bar-123", id);
	}
	
}
