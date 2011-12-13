/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Jason Tsay (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.event.CommitCommentPayload;
import org.junit.Test;

/**
 * Unit tests of {@link CommitCommentPayload}
 */
public class CommitCommentPayloadTest {

	/**
	 * Test default state of CommitCommentPayload
	 */
	@Test
	public void defaultState() {
		CommitCommentPayload payload = new CommitCommentPayload();
		assertNull(payload.getComment());
	}

	/**
	 * Test updating CommitCommentPayload fields
	 */
	@Test
	public void updateFields() {
		CommitCommentPayload payload = new CommitCommentPayload();
		Comment comment = new Comment().setBody("comment");
		assertEquals(comment, payload.setComment(comment).getComment());
	}
}
