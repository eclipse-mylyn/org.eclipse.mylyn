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

import java.util.ArrayList;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.CommitUser;
import org.eclipse.egit.github.core.Tree;
import org.junit.Test;

/**
 * Unit tests of {@link Commit}
 */
public class CommitTest {

	/**
	 * Test default state of commit
	 */
	@Test
	public void defaultState() {
		Commit commit = new Commit();
		assertNull(commit.getAuthor());
		assertNull(commit.getCommitter());
		assertNull(commit.getMessage());
		assertNull(commit.getParents());
		assertNull(commit.getSha());
		assertNull(commit.getTree());
		assertNull(commit.getUrl());
		assertEquals(0, commit.getCommentCount());
	}

	/**
	 * Test updating commit fields
	 */
	@Test
	public void updateFields() {
		Commit commit = new Commit();
		CommitUser author = new CommitUser().setName("Art Thor");
		assertEquals(author, commit.setAuthor(author).getAuthor());
		CommitUser committer = new CommitUser().setName("Comb Mitter");
		assertEquals(committer, commit.setCommitter(committer).getCommitter());
		assertEquals("commit message", commit.setMessage("commit message")
				.getMessage());
		assertEquals(new ArrayList<Commit>(),
				commit.setParents(new ArrayList<Commit>()).getParents());
		assertEquals("abcdef", commit.setSha("abcdef").getSha());
		Tree tree = new Tree();
		tree.setSha("12345");
		assertEquals(tree, commit.setTree(tree).getTree());
		assertEquals("url", commit.setUrl("url").getUrl());
		assertEquals(32, commit.setCommentCount(32).getCommentCount());
	}
}
