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
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.GistService;
import org.eclipse.egit.github.core.service.IssueService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests of {@link GistService}
 */
@RunWith(MockitoJUnitRunner.class)
public class GistServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	@Mock
	private GitHubResponse response;

	private GistService gistService;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@Before
	public void before() throws IOException {
		doReturn(response).when(gitHubClient).get(any(GitHubRequest.class));
		gistService = new GistService(gitHubClient);
	}

	/**
	 * Create service with null client
	 */
	@Test(expected = IllegalArgumentException.class)
	public void constructorNullArgument() {
		new GistService(null);
	}

	/**
	 * Get gist with null id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getGistNullId() throws IOException {
		gistService.getGist(null);
	}

	/**
	 * Get gist with valid id
	 *
	 * @throws IOException
	 */
	@Test
	public void getGistOK() throws IOException {
		gistService.getGist("1");
		verify(gitHubClient).get(any(GitHubRequest.class));
	}

	/**
	 * Get gists for null login name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getGistsNullUser() throws IOException {
		gistService.getGists(null);
	}

	/**
	 * Get gists for valid login name
	 *
	 * @throws IOException
	 */
	@Test
	public void getGistsOK() throws IOException {
		gistService.getGists("test_user");
		verify(gitHubClient).get(any(GitHubRequest.class));
	}

	/**
	 * Create gist with null model
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createGistNullGist() throws IOException {
		gistService.createGist(null);
	}

	/**
	 * Get gist with null user
	 *
	 * @throws IOException
	 */
	@Test
	public void createGistNullUser() throws IOException {
		Gist gist = new Gist();
		gist.setUser(null);
		gistService.createGist(gist);
		verify(gitHubClient).post("/gists", gist, Gist.class);
	}

	/**
	 * Update null gist
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void updateGistNullGist() throws IOException {
		gistService.updateGist(null);
	}

	/**
	 * Update gist with null id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void updateGistNullId() throws IOException {
		Gist gist = new Gist();
		gist.setId(null);
		gistService.updateGist(gist);
	}

	/**
	 * Update valid gist
	 *
	 * @throws IOException
	 */
	@Test
	public void updateGistOK() throws IOException {
		Gist gist = new Gist();
		gist.setId("123");
		gistService.updateGist(gist);
		verify(gitHubClient).post("/gists/123", gist, Gist.class);
	}

	/**
	 * Create comment for with null gist id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCommentNullGistId() throws IOException {
		gistService.createComment(null, "not null");
	}

	/**
	 * Create null comment
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCommentNullComment() throws IOException {
		gistService.createComment("not null", null);
	}

	/**
	 * Create valid comment
	 *
	 * @throws IOException
	 */
	@Test
	public void createCommentOK() throws IOException {
		gistService.createComment("1", "test_comment");

		Map<String, String> params = new HashMap<String, String>(1, 1);
		params.put(IssueService.FIELD_BODY, "test_comment");
		verify(gitHubClient).post("/gists/1/comments", params, Comment.class);
	}

	/**
	 * Get comments for null gist id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getCommentsNullGistId() throws IOException {
		gistService.getComments(null);
	}

	/**
	 * Get comment with valid gist id
	 *
	 * @throws IOException
	 */
	@Test
	public void getCommentsOK() throws IOException {
		gistService.getComments("1");
		verify(gitHubClient).get(any(GitHubRequest.class));
	}
}
