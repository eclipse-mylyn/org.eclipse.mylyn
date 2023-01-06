/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.User;
import org.junit.Test;

/**
 * Unit tests of {@link CommitComment}
 */
public class CommitCommentTest {

	/**
	 * Test default state of commit comment
	 */
	@Test
	public void defaultState() {
		CommitComment comment = new CommitComment();
		assertNull(comment.getBody());
		assertNull(comment.getCommitId());
		assertNull(comment.getCreatedAt());
		assertEquals(0, comment.getId());
		assertEquals(0, comment.getLine());
		assertNull(comment.getPath());
		assertEquals(0, comment.getPosition());
		assertNull(comment.getUpdatedAt());
		assertNull(comment.getUrl());
		assertNull(comment.getUser());
	}

	/**
	 * Test updating commit comment fields
	 */
	@Test
	public void updateFields() {
		CommitComment comment = new CommitComment();
		assertEquals("a body", comment.setBody("a body").getBody());
		assertEquals("123abc", comment.setCommitId("123abc").getCommitId());
		assertEquals(new Date(8000), comment.setCreatedAt(new Date(8000))
				.getCreatedAt());
		assertEquals(20, comment.setId(20).getId());
		assertEquals(12, comment.setLine(12).getLine());
		assertEquals("/a/path", comment.setPath("/a/path").getPath());
		assertEquals(4, comment.setPosition(4).getPosition());
		assertEquals(new Date(10000), comment.setUpdatedAt(new Date(10000))
				.getUpdatedAt());
		assertEquals("http://url", comment.setUrl("http://url").getUrl());
		User user = new User().setLogin("theuser");
		assertEquals(user, comment.setUser(user).getUser());
	}
}
