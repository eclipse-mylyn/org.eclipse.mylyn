/*******************************************************************************
 *  Copyright (c) 2011 Christian Trutz
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.IssueService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
	 * Get issue with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIssueNullUser() throws IOException {
		issueService.getIssue(null, "not null", "not null");
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
	 * Get issue with null id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIssueNullId() throws IOException {
		issueService.getIssue("not null", "not null", null);
	}

	/**
	 * Get issue with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void getIssueOK() throws IOException {
		issueService.getIssue("tu", "tr", 3);
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
		issueService.getComments(null, "not null", "not null");
	}

	/**
	 * Get issue comments with null name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getCommentsNullRepositoryName() throws IOException {
		issueService.getComments("not null", null, "not null");
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
	 * Get issue comments with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void getCommentsOK() throws IOException {
		issueService.getComments("tu", "tr", "4");
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
	 * Get issues with null repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getIssuesNullRepositoryName() throws IOException {
		issueService.getIssues("not null", null, null);
	}

	/**
	 * Get issues with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void getIssuesOK() throws IOException {
		issueService.getIssues("tu", "tr", null);
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
		issueService.createIssue(null, "not null", null);
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
	 * Edit issue with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void editIssueNullUser() throws IOException {
		issueService.editIssue(null, "not null", null);
	}

	/**
	 * Edit issue with null repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void editIssueNullRepositoryName() throws IOException {
		issueService.editIssue("not null", null, null);
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
	public void editIssueOK() throws IOException {
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
	 * Create issue comment with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCommentNullUser() throws IOException {
		issueService.createComment(null, "not null", "not null", "not null");
	}

	/**
	 * Create issue comment with null repository name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCommentNullRepositoryName() throws IOException {
		issueService.createComment("not null", null, "not null", "not null");
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
	 * Create issue comment with valid parameters
	 *
	 * @throws IOException
	 */
	@Test
	public void createCommentOK() throws IOException {
		issueService.createComment("test_user", "test_repository", "1",
				"test_comment");

		Map<String, String> params = new HashMap<String, String>();
		params.put(IssueService.FIELD_BODY, "test_comment");
		verify(gitHubClient).post(
				"/repos/test_user/test_repository/issues/1/comments", params,
				Comment.class);
	}
}
