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
 * Unit tests for {@link IssueService}
 */
@RunWith(MockitoJUnitRunner.class)
public class IssueServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	@Mock
	private GitHubResponse response;

	private IssueService issueService;

	@Before
	public void before() throws IOException {
		doReturn(response).when(gitHubClient).get(any(GitHubRequest.class));
		issueService = new IssueService(gitHubClient);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor_NullArgument() {
		new IssueService(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getIssue_NullUser() throws IOException {
		issueService.getIssue(null, "not null", "not null");
	}

	@Test(expected = IllegalArgumentException.class)
	public void getIssue_NullRepository() throws IOException {
		issueService.getIssue("not null", null, "not null");
	}

	@Test(expected = IllegalArgumentException.class)
	public void getIssue_NullId() throws IOException {
		issueService.getIssue("not null", "not null", null);
	}

	@Test
	public void getIssue_OK() throws IOException {
		issueService.getIssue("test_user", "test_repository", "test_id");
		verify(gitHubClient).get(any(GitHubRequest.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getComments_NullUser() throws IOException {
		issueService.getComments(null, "not null", "not null");
	}

	@Test(expected = IllegalArgumentException.class)
	public void getComments_NullRepository() throws IOException {
		issueService.getComments("not null", null, "not null");
	}

	@Test(expected = IllegalArgumentException.class)
	public void getComments_NullId() throws IOException {
		issueService.getComments("not null", "not null", null);
	}

	@Test
	public void getComments_OK() throws IOException {
		issueService.getComments("test_user", "test_repository", "test_id");
		verify(gitHubClient).get(any(GitHubRequest.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getIssues_NullUser() throws IOException {
		issueService.getIssues(null, "not null", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getIssues_NullRepository() throws IOException {
		issueService.getIssues("not null", null, null);
	}

	@Test
	public void getIssues_OK() throws IOException {
		issueService.getIssues("test_user", "test_repository", null);
		verify(gitHubClient).get(any(GitHubRequest.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createIssue_NullUser() throws IOException {
		issueService.createIssue(null, "not null", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createIssue_NullRepository() throws IOException {
		issueService.createIssue("not null", null, null);
	}

	@Test
	public void createIssue_NullIssue() throws IOException {
		issueService.createIssue("test_user", "test_repository", null);
		verify(gitHubClient).post("/repos/test_user/test_repository/issues",
				new HashMap<String, String>(), Issue.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void editIssue_NullUser() throws IOException {
		issueService.editIssue(null, "not null", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void editIssue_NullRepository() throws IOException {
		issueService.editIssue("not null", null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void editIssue_NullIssue() throws IOException {
		issueService.editIssue("not null", "not null", null);
	}

	@Test
	public void editIssue_OK() throws IOException {
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

	@Test(expected = IllegalArgumentException.class)
	public void createComment_NullUser() throws IOException {
		issueService.createComment(null, "not null", "not null", "not null");
	}

	@Test(expected = IllegalArgumentException.class)
	public void createComment_NullRepository() throws IOException {
		issueService.createComment("not null", null, "not null", "not null");
	}

	@Test(expected = IllegalArgumentException.class)
	public void createComment_NullIssueId() throws IOException {
		issueService.createComment("not null", "not null", null, "not null");
	}

	@Test
	public void createComment_OK() throws IOException {
		issueService.createComment("test_user", "test_repository", "1",
				"test_comment");

		Map<String, String> params = new HashMap<String, String>();
		params.put(IssueService.FIELD_BODY, "test_comment");
		verify(gitHubClient).post(
				"/repos/test_user/test_repository/issues/1/comments", params,
				Comment.class);
	}
}
