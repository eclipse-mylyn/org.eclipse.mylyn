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
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.IssueEvent;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.RepositoryIssue;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.IssueService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests of {@link IssueService}
 */
@RunWith(MockitoJUnitRunner.class)
public class IssueServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	@Mock
	private GitHubResponse response;

	private IssueService issueService;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@Before
	public void before() throws IOException {
		doReturn(response).when(gitHubClient).get(any(GitHubRequest.class));
		issueService = new IssueService(gitHubClient);
	}

	/**
	 * Create issue service with null client
	 */
	@Test(expected = IllegalArgumentException.class)
	public void constructorNullArgument() {
		new IssueService(null);
	}

	/**
	 * Create issue service default constructor
	 */
	@Test
	public void defaultConstructor() {
		assertNotNull(new IssueService().getClient());
	}

	/**
	 * Get issue with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIssueNullUser() throws IOException {
		issueService.getIssue(null, "not null", "not null");
	}

	/**
	 * Get issue with null repository id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIssueNullRepositoryId() throws IOException {
		issueService.getIssue(null, 1);
	}

	/**
	 * Get issue with empty user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIssueEmptyUser() throws IOException {
		issueService.getIssue("", "repo", "not null");
	}

	/**
	 * Get issue with null name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIssueNullRepositoryName() throws IOException {
		issueService.getIssue("not null", null, "not null");
	}

	/**
	 * Get issue with empty name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIssueEmptyRepositoryName() throws IOException {
		issueService.getIssue("user", "", "not null");
	}

	/**
	 * Get issue with null id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIssueNullId() throws IOException {
		issueService.getIssue("not null", "not null", null);
	}

	/**
	 * Get issue with empty id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIssueEmptyId() throws IOException {
		issueService.getIssue("not null", "not null", "");
	}

	/**
	 * Get issue with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void getIssue() throws IOException {
		issueService.getIssue("tu", "tr", 3);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/tu/tr/issues/3");
		verify(gitHubClient).get(request);
	}

	/**
	 * Get issue with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void getIssueWithRepositoryId() throws IOException {
		RepositoryId id = new RepositoryId("tu", "tr");
		issueService.getIssue(id, 3);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/tu/tr/issues/3");
		verify(gitHubClient).get(request);
	}

	/**
	 * Get issue comments with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getCommentsNullUser() throws IOException {
		issueService.getComments(null, "not null", 1);
	}

	/**
	 * Get issue comments with empty user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getCommentsEmptyUser() throws IOException {
		issueService.getComments("", "not null", 2);
	}

	/**
	 * Get issue comments with null name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getCommentsNullRepositoryName() throws IOException {
		issueService.getComments("not null", null, 3);
	}

	/**
	 * Get issue comments with null repository id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getCommentsNullRepositoryId() throws IOException {
		issueService.getComments(null, 3);
	}

	/**
	 * Get issue comments with empty name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getCommentsEmptyRepositoryName() throws IOException {
		issueService.getComments("not null", "", 3);
	}

	/**
	 * Get issue comments with null issue id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getCommentsNullId() throws IOException {
		issueService.getComments("not null", "not null", null);
	}

	/**
	 * Get issue comments with empty issue id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getCommentsEmptyId() throws IOException {
		issueService.getComments("not null", "not null", "");
	}

	/**
	 * Get issue comments with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void getComments() throws IOException {
		issueService.getComments("tu", "tr", 4);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/tu/tr/issues/4/comments"));
		verify(gitHubClient).get(request);
	}

	/**
	 * Get issue comments with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void getCommentsWithRepositoryId() throws IOException {
		RepositoryId id = new RepositoryId("tu", "tr");
		issueService.getComments(id, 4);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/tu/tr/issues/4/comments"));
		verify(gitHubClient).get(request);
	}

	/**
	 * Get issues with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIssuesNullUser() throws IOException {
		issueService.getIssues(null, "not null", null);
	}

	/**
	 * Get issues with null repository id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIssuesNullRepositoryId() throws IOException {
		issueService.getIssues(null, null);
	}

	/**
	 * Get issues with empty user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIssuesEmptyUser() throws IOException {
		issueService.getIssues("", "not null", null);
	}

	/**
	 * Get issues with null repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIssuesNullRepositoryName() throws IOException {
		issueService.getIssues("not null", null, null);
	}

	/**
	 * Get issues with null repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIssuesEmptyRepositoryName() throws IOException {
		issueService.getIssues("not null", "", null);
	}

	/**
	 * Get issues with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void getIssues() throws IOException {
		issueService.getIssues("tu", "tr", null);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/tu/tr/issues"));
		verify(gitHubClient).get(request);
	}

	/**
	 * Get issues with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void getIssuesRepositoryId() throws IOException {
		RepositoryId id = new RepositoryId("tu", "tr");
		issueService.getIssues(id, null);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/tu/tr/issues"));
		verify(gitHubClient).get(request);
	}

	/**
	 * Create issue with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createIssueNullUser() throws IOException {
		issueService.createIssue(null, "not null", new Issue());
	}

	/**
	 * Create issue with empty user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createIssueEmptyUser() throws IOException {
		issueService.createIssue("", "not null", new Issue());
	}

	/**
	 * Create issue with null repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createIssueNullRepositoryName() throws IOException {
		issueService.createIssue("not null", null, null);
	}

	/**
	 * Create issue with empty repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createIssueEmptyRepositoryName() throws IOException {
		issueService.createIssue("not null", "", new Issue());
	}

	/**
	 * Create issue with null issue
	 *
	 * @throws IOException
	 */
	@Test
	public void createIssueNullIssue() throws IOException {
		issueService.createIssue("test_user", "test_repository", null);
		verify(gitHubClient).post("/repos/test_user/test_repository/issues",
				new HashMap<String, String>(), Issue.class);
	}

	/**
	 * Create issue with null issue
	 *
	 * @throws IOException
	 */
	@Test
	public void createIssueNullIssueWithRepositoryId() throws IOException {
		RepositoryId id = new RepositoryId("tu", "tr");
		issueService.createIssue(id, null);
		verify(gitHubClient).post("/repos/tu/tr/issues",
				new HashMap<String, String>(), Issue.class);
	}

	/**
	 * Edit issue with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void editIssueNullUser() throws IOException {
		issueService.editIssue(null, "not null", new Issue());
	}

	/**
	 * Edit issue with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void editIssueEmptyUser() throws IOException {
		issueService.editIssue("", "not null", new Issue());
	}

	/**
	 * Edit issue with null repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void editIssueNullRepositoryName() throws IOException {
		issueService.editIssue("not null", null, new Issue());
	}

	/**
	 * Edit issue with null repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void editIssueEmptyRepositoryName() throws IOException {
		issueService.editIssue("not null", "", new Issue());
	}

	/**
	 * Edit issue with null issue
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void editIssueNullIssue() throws IOException {
		issueService.editIssue("not null", "not null", null);
	}

	/**
	 * Edit issue with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void editIssue() throws IOException {
		Issue issue = new Issue();
		issue.setNumber(1);
		issue.setTitle("test_title");
		issue.setBody("test_body");
		issue.setState("test_state");
		issueService.editIssue("test_user", "test_repository", issue);

		Map<String, String> params = new HashMap<String, String>();
		params.put(IssueService.FIELD_TITLE, "test_title");
		params.put(IssueService.FIELD_BODY, "test_body");
		params.put(IssueService.FILTER_STATE, "test_state");
		verify(gitHubClient).post("/repos/test_user/test_repository/issues/1",
				params, Issue.class);
	}

	/**
	 * Edit issue with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void editIssueWithRepositoryId() throws IOException {
		Issue issue = new Issue();
		issue.setNumber(1);
		issue.setTitle("test_title");
		issue.setBody("test_body");
		issue.setState("test_state");
		RepositoryId id = new RepositoryId("tu", "tr");
		issueService.editIssue(id, issue);

		Map<String, String> params = new HashMap<String, String>();
		params.put(IssueService.FIELD_TITLE, "test_title");
		params.put(IssueService.FIELD_BODY, "test_body");
		params.put(IssueService.FILTER_STATE, "test_state");
		verify(gitHubClient).post("/repos/tu/tr/issues/1", params, Issue.class);
	}

	/**
	 * Create issue comment with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCommentNullUser() throws IOException {
		issueService.createComment(null, "not null", 1, "not null");
	}

	/**
	 * Create issue comment with empty user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCommentEmptyUser() throws IOException {
		issueService.createComment("", "not null", 2, "not null");
	}

	/**
	 * Create issue comment with null repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCommentNullRepositoryName() throws IOException {
		issueService.createComment("not null", null, 3, "not null");
	}

	/**
	 * Create issue comment with empty repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCommentEmptyRepositoryName() throws IOException {
		issueService.createComment("not null", "", 4, "not null");
	}

	/**
	 * Create issue comment with null issue id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCommentNullIssueId() throws IOException {
		issueService.createComment("not null", "not null", null, "not null");
	}

	/**
	 * Create issue comment with empty issue id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCommentEmptyIssueId() throws IOException {
		issueService.createComment("not null", "not null", "", "not null");
	}

	/**
	 * Create issue comment with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void createComment() throws IOException {
		issueService.createComment("test_user", "test_repository", 1,
				"test_comment");

		Map<String, String> params = new HashMap<String, String>();
		params.put(IssueService.FIELD_BODY, "test_comment");
		verify(gitHubClient).post(
				"/repos/test_user/test_repository/issues/1/comments", params,
				Comment.class);
	}

	/**
	 * Create issue comment with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void createCommentWithRepositoryId() throws IOException {
		RepositoryId id = new RepositoryId("tu", "tr");
		issueService.createComment(id, 1, "test_comment");

		Map<String, String> params = new HashMap<String, String>();
		params.put(IssueService.FIELD_BODY, "test_comment");
		verify(gitHubClient).post("/repos/tu/tr/issues/1/comments", params,
				Comment.class);
	}

	/**
	 * Delete comment with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void deleteCommentNullUser() throws IOException {
		issueService.deleteComment(null, "repo", 1);
	}

	/**
	 * Delete comment with empty user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void deleteCommentEmptyUser() throws IOException {
		issueService.deleteComment("", "repo", 1);
	}

	/**
	 * Delete comment with null repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void deleteCommentNullRepositoryName() throws IOException {
		issueService.deleteComment("user", null, 1);
	}

	/**
	 * Delete comment with empty repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void deleteCommentEmptyRepositoryName() throws IOException {
		issueService.deleteComment("user", "", 1);
	}

	/**
	 * Delete comment with null comment id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void deleteCommentNullId() throws IOException {
		issueService.deleteComment("user", "repo", null);
	}

	/**
	 * Delete comment with empty comment id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void deleteCommentEmptyId() throws IOException {
		issueService.deleteComment("user", "repo", "");
	}

	/**
	 * Delete issue comment
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteComment() throws IOException {
		issueService.deleteComment("user", "repo", 1);
		verify(gitHubClient).delete("/repos/user/repo/issues/comments/1");
	}

	/**
	 * Delete issue comment
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteCommentWithRepositoryId() throws IOException {
		RepositoryId id = new RepositoryId("user", "repo");
		issueService.deleteComment(id, 1);
		verify(gitHubClient).delete("/repos/user/repo/issues/comments/1");
	}

	/**
	 * Page issues for current user
	 *
	 * @throws IOException
	 */
	@Test
	public void pageIssues() throws IOException {
		PageIterator<RepositoryIssue> iterator = issueService.pageIssues();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(Utils.page("/issues"), iterator.getRequest().generateUri());
	}

	/**
	 * Page issues for repository
	 *
	 * @throws IOException
	 */
	@Test
	public void pageRepsitoryIssues() throws IOException {
		PageIterator<Issue> iterator = issueService.pageIssues("user", "repo");
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(Utils.page("/repos/user/repo/issues"), iterator
				.getRequest().generateUri());
	}

	/**
	 * Page issues for repository
	 *
	 * @throws IOException
	 */
	@Test
	public void pageRepsitoryIssuesWithRepositoryId() throws IOException {
		RepositoryId id = new RepositoryId("user", "repo");
		PageIterator<Issue> iterator = issueService.pageIssues(id);
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(Utils.page("/repos/user/repo/issues"), iterator
				.getRequest().generateUri());
	}

	/**
	 * Get issues for current user
	 *
	 * @throws IOException
	 */
	@Test
	public void getCurrentUserIssues() throws IOException {
		List<RepositoryIssue> issues = issueService.getIssues();
		assertNotNull(issues);
		assertTrue(issues.isEmpty());
	}

	/**
	 * Page all issue events for repository
	 *
	 * @throws IOException
	 */
	@Test
	public void pageEvents() throws IOException {
		PageIterator<IssueEvent> iter = issueService.pageEvents("user", "repo");
		assertNotNull(iter);
		assertTrue(iter.hasNext());
		assertEquals(Utils.page("/repos/user/repo/issues/events"), iter
				.getRequest().generateUri());
	}

	/**
	 * Page issue events for repository
	 *
	 * @throws IOException
	 */
	@Test
	public void pageIssueEvents() throws IOException {
		PageIterator<IssueEvent> iter = issueService.pageIssueEvents("user",
				"repo", 16);
		assertNotNull(iter);
		assertTrue(iter.hasNext());
		assertEquals(Utils.page("/repos/user/repo/issues/16/events"), iter
				.getRequest().generateUri());
	}

	/**
	 * Get issue event
	 *
	 * @throws IOException
	 */
	@Test
	public void getIssueEvent() throws IOException {
		issueService.getIssueEvent("user", "repo", 4399);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/user/repo/issues/events/4399");
		verify(gitHubClient).get(request);
	}

	/**
	 * Get issue comment
	 *
	 * @throws IOException
	 */
	@Test
	public void getIssueComment() throws IOException {
		issueService.getComment("user", "repo", 38);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/user/repo/issues/comments/38");
		verify(gitHubClient).get(request);
	}

	/**
	 * Edit issue comment with null comment
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void editIssueCommentNullComment() throws IOException {
		issueService.editComment("user", "repo", null);
	}

	/**
	 * Edit issue comment
	 *
	 * @throws IOException
	 */
	@Test
	public void editIssueComment() throws IOException {
		Comment comment = new Comment().setId(29).setBody("new body");
		issueService.editComment("user", "repo", comment);
		verify(gitHubClient).post("/repos/user/repo/issues/comments/29",
				comment, Comment.class);
	}

	/**
	 * Edit issue comment
	 *
	 * @throws IOException
	 */
	@Test
	public void editIssueCommentWithRepositoryId() throws IOException {
		RepositoryId id = new RepositoryId("user", "repo");
		Comment comment = new Comment().setId(44).setBody("new body");
		issueService.editComment(id, comment);
		verify(gitHubClient).post("/repos/user/repo/issues/comments/44",
				comment, Comment.class);
	}

	/**
	 * Search issues with null repository
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void searchIssuesNullRepository() throws IOException {
		issueService.searchIssues(null, "open", "test");
	}

	/**
	 * Search issues with null query
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void searchIssueNullQuery() throws IOException {
		RepositoryId id = new RepositoryId("user", "repo");
		issueService.searchIssues(id, "open", null);
	}

	/**
	 * Search issues with empty query
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void searchIssueEmptyQuery() throws IOException {
		RepositoryId id = new RepositoryId("user", "repo");
		issueService.searchIssues(id, "open", "");
	}

	/**
	 * Search issues with null state
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void searchIssueNullState() throws IOException {
		RepositoryId id = new RepositoryId("user", "repo");
		issueService.searchIssues(id, null, "test");
	}

	/**
	 * Search issues with empty state
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void searchIssueEmptyState() throws IOException {
		RepositoryId id = new RepositoryId("user", "repo");
		issueService.searchIssues(id, "", "test");
	}

	/**
	 * Search issues
	 *
	 * @throws IOException
	 */
	@Test
	public void searchIssues() throws IOException {
		RepositoryId id = new RepositoryId("user", "repo");
		issueService.searchIssues(id, "closed", "test");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils
				.page("/legacy/issues/search/user/repo/closed/test"));
		verify(gitHubClient).get(request);
	}

	/**
	 * Search issues with query that needs escaping
	 *
	 * @throws IOException
	 */
	@Test
	public void searchEscaped() throws IOException {
		RepositoryId id = new RepositoryId("user", "repo");
		issueService.searchIssues(id, "open", "a and a.");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils
				.page("/legacy/issues/search/user/repo/open/a%20and%20a%2E"));
		verify(gitHubClient).get(request);
	}
}
