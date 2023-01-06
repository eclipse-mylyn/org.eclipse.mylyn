/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Jason Tsay (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.event.IssuesPayload;
import org.junit.Test;

/**
 * Unit tests of {@link IssuesPayload}
 */
public class IssuesPayloadTest {

	/**
	 * Test default state of IssuesPayload
	 */
	@Test
	public void defaultState() {
		IssuesPayload payload = new IssuesPayload();
		assertNull(payload.getAction());
		assertNull(payload.getIssue());
	}

	/**
	 * Test updating IssuesPayload fields
	 */
	@Test
	public void updateFields() {
		IssuesPayload payload = new IssuesPayload();
		Issue issue = new Issue().setTitle("issue");
		assertEquals("create", payload.setAction("create").getAction());
		assertEquals(issue, payload.setIssue(issue).getIssue());
	}
}
