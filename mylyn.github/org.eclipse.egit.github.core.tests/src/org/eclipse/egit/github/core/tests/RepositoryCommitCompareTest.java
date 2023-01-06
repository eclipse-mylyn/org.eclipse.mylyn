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

import java.util.Arrays;
import java.util.List;

import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryCommitCompare;
import org.junit.Test;

/**
 * Unit tests of {@link RepositoryCommitCompare}
 */
public class RepositoryCommitCompareTest {

	/**
	 * Test default state of repository commit compare
	 */
	@Test
	public void defaultState() {
		RepositoryCommitCompare compare = new RepositoryCommitCompare();
		assertEquals(0, compare.getAheadBy());
		assertNull(compare.getBaseCommit());
		assertEquals(0, compare.getBehindBy());
		assertNull(compare.getCommits());
		assertNull(compare.getDiffUrl());
		assertNull(compare.getFiles());
		assertNull(compare.getHtmlUrl());
		assertNull(compare.getPatchUrl());
		assertNull(compare.getPermalinkUrl());
		assertNull(compare.getStatus());
		assertEquals(0, compare.getTotalCommits());
		assertNull(compare.getUrl());
	}

	/**
	 * Test updating repository commit compare fields
	 */
	@Test
	public void updateFields() {
		RepositoryCommitCompare compare = new RepositoryCommitCompare();
		assertEquals(5, compare.setAheadBy(5).getAheadBy());
		RepositoryCommit base = new RepositoryCommit();
		assertEquals(base, compare.setBaseCommit(base).getBaseCommit());
		assertEquals(12, compare.setBehindBy(12).getBehindBy());
		List<RepositoryCommit> commits = Arrays.asList(base);
		assertEquals(commits, compare.setCommits(commits).getCommits());
		assertEquals("diff-url", compare.setDiffUrl("diff-url").getDiffUrl());
		List<CommitFile> files = Arrays.asList(new CommitFile());
		assertEquals(files, compare.setFiles(files).getFiles());
		assertEquals("html-url", compare.setHtmlUrl("html-url").getHtmlUrl());
		assertEquals("patch-url", compare.setPatchUrl("patch-url")
				.getPatchUrl());
		assertEquals("link-url", compare.setPermalinkUrl("link-url")
				.getPermalinkUrl());
		assertEquals("behind", compare.setStatus("behind").getStatus());
		assertEquals(400, compare.setTotalCommits(400).getTotalCommits());
		assertEquals("url", compare.setUrl("url").getUrl());
	}
}
