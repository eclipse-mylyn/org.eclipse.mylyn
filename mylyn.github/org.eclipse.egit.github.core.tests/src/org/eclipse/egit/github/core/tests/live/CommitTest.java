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
 *    See git history
 *****************************************************************************/
package org.eclipse.egit.github.core.tests.live;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.CommitService;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link CommitService}
 */
@SuppressWarnings("nls")
public class CommitTest extends LiveTest {

	/**
	 * Test paging commits
	 *
	 * @throws Exception
	 */
	@Test
	public void pageCommits() throws Exception {
		CommitService service = new CommitService(client);
		RepositoryId repo = RepositoryId.create("defunkt", "mustache");
		PageIterator<RepositoryCommit> commits = service.pageCommits(repo, 2);
		Set<String> shas = new HashSet<>();
		int pages = 0;
		for (Collection<RepositoryCommit> page : commits) {
			assertNotNull(page);
			assertEquals(2, page.size());
			for (RepositoryCommit commit : page) {
				assertNotNull(commit);
				assertNotNull(commit.getSha());
				assertFalse(shas.contains(commit.getSha()));
				shas.add(commit.getSha());
				assertNotNull(commit.getCommit());
			}
			pages++;
			if (pages == 3) {
				break;
			}
		}
	}

	/**
	 * Test getting comments for a commit
	 *
	 * @throws Exception
	 */
	@Test
	public void getComments() throws Exception {
		CommitService service = new CommitService(client);
		RepositoryId repo = RepositoryId.create("defunkt", "mustache");
		String commit = "8118091cbad66d7a4d504f65964c62629a4fd064";
		List<CommitComment> comments = service.getComments(repo, commit);
		assertNotNull(comments);
		assertFalse(comments.isEmpty());
		for (CommitComment comment : comments) {
			assertNotNull(comment);
			assertNotNull(comment.getBody());
			assertEquals(commit, comment.getCommitId());
			assertNotNull(comment.getCreatedAt());
			assertNotNull(comment.getUpdatedAt());
			assertNotNull(comment.getUrl());
			assertNotNull(comment.getUser());
			CommitComment fetched = service.getComment(repo, comment.getId());
			assertEquals(comment.getId(), fetched.getId());
			assertEquals(comment.getUrl(), fetched.getUrl());
			assertEquals(comment.getBody(), fetched.getBody());
		}
	}

	/**
	 * Test getting commit by SHA-1
	 *
	 * @throws Exception
	 */
	@Test
	public void getCommit() throws Exception {
		CommitService service = new CommitService(client);
		RepositoryId repo = RepositoryId.create("defunkt", "mustache");
		String id = "8118091cbad66d7a4d504f65964c62629a4fd064";
		RepositoryCommit commit = service.getCommit(repo, id);
		assertNotNull(commit);
		assertEquals(id, commit.getSha());
		assertNotNull(commit.getAuthor());
		assertNotNull(commit.getCommitter());
		assertNotNull(commit.getCommit());
		assertNotNull(commit.getUrl());
		assertNotNull(commit.getParents());
		assertFalse(commit.getParents().isEmpty());
	}

	/**
	 * Test generating single-commit diff
	 *
	 * @throws IOException
	 */
	@Test
	public void singleCommitDiff() throws IOException {
		CommitService service = new CommitService(client);
		RepositoryId repo = RepositoryId.create("defunkt", "mustache");
		String sha = "d8214ac6aef0759e112ff9ce8d2ef851b36969eb";
		String firstLine = readLine(service.getCommitDiff(repo, sha), 0);
		assertTrue(firstLine.startsWith("diff --git"), "response is a diff");
	}

	/**
	 * Test generating single-commit patch
	 *
	 * @throws IOException
	 */
	@Test
	public void singleCommitPatch() throws IOException {
		CommitService service = new CommitService(client);
		RepositoryId repo = RepositoryId.create("defunkt", "mustache");
		String sha = "d8214ac6aef0759e112ff9ce8d2ef851b36969eb";
		String fourthLine = readLine(service.getCommitPatch(repo, sha), 3);
		assertTrue(fourthLine.contains("[PATCH "), "response is a patch");
	}

	/**
	 * Test generating multi-commit diff
	 *
	 * @throws IOException
	 */
	@Test
	public void multiCommitDiff() throws IOException {
		CommitService service = new CommitService(client);
		RepositoryId repo = RepositoryId.create("defunkt", "mustache");
		String base = "d8214ac6aef0759e112ff9ce8d2ef851b36969eb";
		String head = "7dd0a3773e7c65351cf3d75f17e9e91919bafa33";
		String firstLine = readLine(service.compareDiff(repo, base, head), 0);
		assertTrue(firstLine.startsWith("diff --git"), "response is a diff");
	}

	/**
	 * Test generating multi-commit patch
	 *
	 * @throws IOException
	 */
	@Test
	public void multiCommitPatch() throws IOException {
		CommitService service = new CommitService(client);
		RepositoryId repo = RepositoryId.create("defunkt", "mustache");
		String base = "d8214ac6aef0759e112ff9ce8d2ef851b36969eb";
		String head = "7dd0a3773e7c65351cf3d75f17e9e91919bafa33";
		String fourthLine = readLine(service.comparePatch(repo, base, head), 3);
		assertTrue(fourthLine.contains("[PATCH "), "response is a patch");
	}

	private String readLine(InputStream inStream, int i) {
		try (final Scanner sc = new Scanner(inStream)) {
			sc.useDelimiter("\\n");
			for (; i > 0; i--) {
				sc.next();
			}
			return sc.next();
		}
	}
}
