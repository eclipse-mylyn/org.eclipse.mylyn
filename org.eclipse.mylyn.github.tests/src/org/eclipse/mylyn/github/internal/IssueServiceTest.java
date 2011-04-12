/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.github.internal;

import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.AssertionFailedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.gson.reflect.TypeToken;

/**
 * Unit tests for {@link IssueService}
 */
@SuppressWarnings("restriction")
@RunWith(MockitoJUnitRunner.class)
public class IssueServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	private IssueService issueService;

	@Before
	public void before() {
		issueService = new IssueService(gitHubClient);
	}

	@Test(expected = AssertionFailedException.class)
	public void constructor_NullArgument() {
		new IssueService(null);
	}

	@Test(expected = AssertionFailedException.class)
	public void getIssue_NullUser() throws IOException {
		issueService.getIssue(null, "not null", "not null");
	}

	@Test(expected = AssertionFailedException.class)
	public void getIssue_NullRepository() throws IOException {
		issueService.getIssue("not null", null, "not null");
	}

	@Test(expected = AssertionFailedException.class)
	public void getIssue_NullId() throws IOException {
		issueService.getIssue("not null", "not null", null);
	}

	@Test
	public void getIssue_OK() throws IOException {
		issueService.getIssue("test_user", "test_repository", "test_id");
		verify(gitHubClient).get(
				"/repos/test_user/test_repository/issues/test_id.json",
				Issue.class);
	}

	@Test(expected = AssertionFailedException.class)
	public void getComments_NullUser() throws IOException {
		issueService.getComments(null, "not null", "not null");
	}

	@Test(expected = AssertionFailedException.class)
	public void getComments_NullRepository() throws IOException {
		issueService.getComments("not null", null, "not null");
	}

	@Test(expected = AssertionFailedException.class)
	public void getComments_NullId() throws IOException {
		issueService.getComments("not null", "not null", null);
	}

	@Test
	public void getComments_OK() throws IOException {
		issueService.getComments("test_user", "test_repository", "test_id");
		TypeToken<List<Comment>> commentToken = new TypeToken<List<Comment>>() {
		};
		verify(gitHubClient)
				.get("/repos/test_user/test_repository/issues/test_id/comments.json",
						commentToken.getType());
	}

	@Test(expected = AssertionFailedException.class)
	public void getIssues_NullUser() throws IOException {
		issueService.getIssues(null, "not null", null);
	}

	@Test(expected = AssertionFailedException.class)
	public void getIssues_NullRepository() throws IOException {
		issueService.getIssues("not null", null, null);
	}

	@Test
	public void getIssues_OK() throws IOException {
		issueService.getIssues("test_user", "test_repository", null);
		TypeToken<List<Issue>> issuesToken = new TypeToken<List<Issue>>() {
		};
		verify(gitHubClient).get(
				"/repos/test_user/test_repository/issues.json", null,
				issuesToken.getType());
	}

	@Test(expected = AssertionFailedException.class)
	public void createIssue_NullUser() throws IOException {
		issueService.createIssue(null, "not null", null);
	}

	@Test(expected = AssertionFailedException.class)
	public void createIssue_NullRepository() throws IOException {
		issueService.createIssue("not null", null, null);
	}

	@Test
	public void createIssue_NullIssue() throws IOException {
		issueService.createIssue("test_user", "test_repository", null);
		verify(gitHubClient).post(
				"/repos/test_user/test_repository/issues.json", null,
				Issue.class);
	}
}
