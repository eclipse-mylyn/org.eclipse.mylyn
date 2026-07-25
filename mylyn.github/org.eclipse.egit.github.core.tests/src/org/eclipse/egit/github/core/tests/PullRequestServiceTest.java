/*******************************************************************************
 *  Copyright (c) 2011 Christian Trutz
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *    See git history
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.MergeStatus;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.PullRequestMarker;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Unit tests of {@link PullRequestService}
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("nls")
public class PullRequestServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	@Mock
	private GitHubResponse response;

	private RepositoryId repo;

	private PullRequestService pullRequestService;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@BeforeEach
	public void before() throws IOException {
		pullRequestService = new PullRequestService(gitHubClient);
		doReturn(response).when(gitHubClient).get(any(GitHubRequest.class));
		repo = new RepositoryId("o", "n");
	}

	/**
	 * Create service using default constructor
	 */
	@Test
	public void constructor() {
		assertNotNull(new PullRequestService().getClient());
	}

	/**
	 * Create pull request service with null client
	 */
	@Test
	public void constructorNullArgument() {
		assertThrows(IllegalArgumentException.class, () -> new PullRequestService(null));
	}

	/**
	 * Get pull request with null repository
	 *
	 * @throws IOException
	 */
	@Test
	public void getPullRequestNullRepository() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> pullRequestService.getPullRequest(null, 3));
	}

	/**
	 * Get pull request
	 *
	 * @throws IOException
	 */
	@Test
	public void getPullRequest() throws IOException {
		pullRequestService.getPullRequest(repo, 5);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/o/n/pulls/5");
		verify(gitHubClient).get(request);
	}

	/**
	 * Get pull requests with null repository
	 *
	 * @throws IOException
	 */
	@Test
	public void getPullRequestsNullRepository() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> pullRequestService.getPullRequests(null, "not null"));
	}

	/**
	 * Get pull request with repository that generates a null id
	 *
	 * @throws IOException
	 */
	@Test
	public void getPullRequestsNullRepositoryId() throws IOException {
		assertThrows(IllegalArgumentException.class,
				() -> pullRequestService.getPullRequests(() -> null, "test_state"));
	}

	/**
	 * Get pull requests
	 *
	 * @throws IOException
	 */
	@Test
	public void getPullRequests() throws IOException {
		pullRequestService.getPullRequests(repo, "open");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/o/n/pulls?state=open"));
		verify(gitHubClient).get(request);
	}

	/**
	 * Get pull request comment
	 *
	 * @throws IOException
	 */
	@Test
	public void getComment() throws IOException {
		pullRequestService.getComment(repo, 65);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/o/n/pulls/comments/65");
		verify(gitHubClient).get(request);
	}

	/**
	 * Get pull request comment
	 *
	 * @throws IOException
	 */
	@Test
	public void getComments() throws IOException {
		pullRequestService.getComments(repo, 6);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/o/n/pulls/6/comments"));
		verify(gitHubClient).get(request);
	}

	/**
	 * Get pull request files
	 *
	 * @throws IOException
	 */
	@Test
	public void getFiles() throws IOException {
		pullRequestService.getFiles(repo, 5);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/o/n/pulls/5/files"));
		verify(gitHubClient).get(request);
	}

	/**
	 * Get pull request commits
	 *
	 * @throws IOException
	 */
	@Test
	public void getCommits() throws IOException {
		pullRequestService.getCommits(repo, 5);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/o/n/pulls/5/commits"));
		verify(gitHubClient).get(request);
	}

	/**
	 * Is merged
	 *
	 * @throws IOException
	 */
	@Test
	public void isMerged() throws IOException {
		pullRequestService.isMerged(repo, 8);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/o/n/pulls/8/merge");
		verify(gitHubClient).get(request);
	}

	/**
	 * Merge pull request
	 *
	 * @throws IOException
	 */
	@Test
	public void merge() throws IOException {
		pullRequestService.merge(repo, 8, "merge");
		verify(gitHubClient).put(eq("/repos/o/n/pulls/8/merge"), any(), eq(MergeStatus.class));
	}

	/**
	 * Create pull request comment
	 *
	 * @throws IOException
	 */
	@Test
	public void createComment() throws IOException {
		CommitComment comment = new CommitComment();
		comment.setBody("looks good");
		pullRequestService.createComment(repo, 3, comment);
		verify(gitHubClient).post("/repos/o/n/pulls/3/comments", comment, CommitComment.class);
	}

	/**
	 * Reply to pull request comment
	 *
	 * @throws IOException
	 */
	@Test
	public void replyToComment() throws IOException {
		pullRequestService.replyToComment(repo, 5, 10, "reply");
		verify(gitHubClient).post(eq("/repos/o/n/pulls/5/comments"), notNull(), eq(CommitComment.class));
	}

	/**
	 * Create pull request
	 *
	 * @throws IOException
	 */
	@Test
	public void createPullRequest() throws IOException {
		PullRequest request = new PullRequest();
		request.setBody("a fix");
		request.setTitle("this is a fix");
		request.setHead(new PullRequestMarker().setRef("master"));
		request.setBase(new PullRequestMarker().setRef("b1"));
		pullRequestService.createPullRequest(repo, request);
		verify(gitHubClient).post(eq("/repos/o/n/pulls"), notNull(), eq(PullRequest.class));
	}

	/**
	 * Create pull request from issue
	 *
	 * @throws IOException
	 */
	@Test
	public void createPullRequestFromIssue() throws IOException {
		pullRequestService.createPullRequest(repo, 49, "master", "v1");
		verify(gitHubClient).post(eq("/repos/o/n/pulls"), notNull(), eq(PullRequest.class));
	}

	/**
	 * Edit pull request with null request
	 *
	 * @throws IOException
	 */
	@Test
	public void editPullRequestNullRequest() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> pullRequestService.editPullRequest(repo, null));
	}

	/**
	 * Edit pull request
	 *
	 * @throws IOException
	 */
	@Test
	public void editPullRequest() throws IOException {
		PullRequest request = new PullRequest().setNumber(33);
		request.setBody("a body");
		request.setTitle("new title");
		request.setState("merged");
		pullRequestService.editPullRequest(repo, request);
		verify(gitHubClient).post(eq("/repos/o/n/pulls/33"), notNull(), eq(PullRequest.class));
	}

	/**
	 * Delete pull request comment
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteComment() throws IOException {
		pullRequestService.deleteComment(repo, 589);
		verify(gitHubClient).delete("/repos/o/n/pulls/comments/589");
	}

	/**
	 * Edit comment with null comment
	 *
	 * @throws IOException
	 */
	@Test
	public void editCommentNullComment() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> pullRequestService.editComment(repo, null));
	}

	/**
	 * Edit comment
	 *
	 * @throws IOException
	 */
	@Test
	public void editComment() throws IOException {
		CommitComment comment = new CommitComment();
		comment.setId(78).setBody("a newer body");
		pullRequestService.editComment(repo, comment);
		verify(gitHubClient).post("/repos/o/n/pulls/comments/78", comment, CommitComment.class);
	}
}
