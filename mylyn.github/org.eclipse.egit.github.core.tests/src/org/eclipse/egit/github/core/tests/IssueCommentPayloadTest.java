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

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.event.IssueCommentPayload;
import org.junit.Test;

/**
 * Unit tests of {@link IssueCommentPayload}
 */
public class IssueCommentPayloadTest {

	/**
	 * Test default state of IssueCommentPayload
	 */
	@Test
	public void defaultState() {
		IssueCommentPayload payload = new IssueCommentPayload();
		assertNull(payload.getAction());
		assertNull(payload.getIssue());
		assertNull(payload.getComment());
	}

	/**
	 * Test updating IssueCommentPayload fields
	 */
	@Test
	public void updateFields() {
		IssueCommentPayload payload = new IssueCommentPayload();
		Issue issue = new Issue().setTitle("issue");
		Comment comment = new Comment().setBody("comment");
		assertEquals("create", payload.setAction("create").getAction());
		assertEquals(issue, payload.setIssue(issue).getIssue());
		assertEquals(comment, payload.setComment(comment).getComment());
	}
}
