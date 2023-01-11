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
import java.util.Arrays;
import java.util.List;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.CommitStats;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.User;
import org.junit.Test;

/**
 * Unit tests of {@link RepositoryCommit}
 */
public class RepositoryCommitTest {

	/**
	 * Test default state of repository commit
	 */
	@Test
	public void defaultState() {
		RepositoryCommit commit = new RepositoryCommit();
		assertNull(commit.getAuthor());
		assertNull(commit.getCommit());
		assertNull(commit.getCommitter());
		assertNull(commit.getFiles());
		assertNull(commit.getParents());
		assertNull(commit.getSha());
		assertNull(commit.getStats());
		assertNull(commit.getUrl());
	}

	/**
	 * Test updating repository commit fields
	 */
	@Test
	public void updateFields() {
		RepositoryCommit commit = new RepositoryCommit();
		User author = new User().setLogin("author");
		assertEquals(author, commit.setAuthor(author).getAuthor());
		Commit gitCommit = new Commit().setSha("abc");
		assertEquals(gitCommit, commit.setCommit(gitCommit).getCommit());
		User committer = new User().setLogin("committer");
		assertEquals(committer, commit.setCommitter(committer).getCommitter());
		assertEquals(new ArrayList<Commit>(),
				commit.setParents(new ArrayList<Commit>()).getParents());
		assertEquals("0a0", commit.setSha("0a0").getSha());
		assertEquals("url", commit.setUrl("url").getUrl());
		CommitStats stats = new CommitStats();
		assertEquals(stats, commit.setStats(stats).getStats());
		List<CommitFile> files = Arrays.asList(new CommitFile()
				.setFilename("test.txt"));
		assertEquals(files, commit.setFiles(files).getFiles());
	}
}
