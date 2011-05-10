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
package org.eclipse.mylyn.github.internal;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.AssertionFailedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.gson.reflect.TypeToken;

/**
 * Unit tests for {@link GistService}
 */
@SuppressWarnings("restriction")
@RunWith(MockitoJUnitRunner.class)
public class GistServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	@Mock
	private GitHubResponse response;

	private GistService gistService;

	@Before
	public void before() throws IOException {
		doReturn(response).when(gitHubClient).get(any(GitHubRequest.class));
		gistService = new GistService(gitHubClient);
	}

	@Test(expected = AssertionFailedException.class)
	public void constructor_NullArgument() {
		new GistService(null);
	}

	@Test(expected = AssertionFailedException.class)
	public void getGist_NullId() throws IOException {
		gistService.getGist(null);
	}

	@Test
	public void getGist_OK() throws IOException {
		gistService.getGist("1");
		verify(gitHubClient).get(any(GitHubRequest.class));
	}

	@Test(expected = AssertionFailedException.class)
	public void getGists_NullUser() throws IOException {
		gistService.getGists(null);
	}

	@Test
	public void getGists_OK() throws IOException {
		gistService.getGists("test_user");
		verify(gitHubClient).get(any(GitHubRequest.class));
	}

	@Test(expected = AssertionFailedException.class)
	public void createGist_NullGist() throws IOException {
		gistService.createGist(null);
	}

	@Test
	public void createGist_NullUser() throws IOException {
		Gist gist = new Gist();
		gist.setUser(null);
		gistService.createGist(gist);
		verify(gitHubClient).post("/gists.json", gist, Gist.class);
	}

	@Test
	public void createGist_NonNullUser() throws IOException {
		Gist gist = new Gist();
		User user = new User();
		user.setLogin("test_user");
		gist.setUser(user);
		gistService.createGist(gist);
		verify(gitHubClient).post("/users/test_user/gists.json", gist,
				Gist.class);
	}

	@Test(expected = AssertionFailedException.class)
	public void createGist_NonNullUser_NullLogin() throws IOException {
		Gist gist = new Gist();
		User user = new User();
		user.setLogin(null);
		gist.setUser(user);
		gistService.createGist(gist);
	}

	@Test(expected = AssertionFailedException.class)
	public void updateGist_NullGist() throws IOException {
		gistService.updateGist(null);
	}

	@Test(expected = AssertionFailedException.class)
	public void updateGist_NullId() throws IOException {
		Gist gist = new Gist();
		gist.setId(null);
		gistService.updateGist(gist);
	}

	@Test
	public void updateGist_OK() throws IOException {
		Gist gist = new Gist();
		gist.setId("123");
		gistService.updateGist(gist);
		verify(gitHubClient).put("/gists/123.json", gist, Gist.class);
	}

	@Test(expected = AssertionFailedException.class)
	public void createComment_NullGistId() throws IOException {
		gistService.createComment(null, "not null");
	}

	@Test(expected = AssertionFailedException.class)
	public void createComment_NullComment() throws IOException {
		gistService.createComment("not null", null);
	}

	@Test
	public void createComment_OK() throws IOException {
		gistService.createComment("1", "test_comment");

		Map<String, String> params = new HashMap<String, String>(1, 1);
		params.put(IssueService.FIELD_BODY, "test_comment");
		verify(gitHubClient).post("/gists/1/comments.json", params,
				Comment.class);
	}

	@Test(expected = AssertionFailedException.class)
	public void getComments_NullGistId() throws IOException {
		gistService.getComments(null);
	}

	@Test
	public void getComments_OK() throws IOException {
		gistService.getComments("1");

		TypeToken<List<Comment>> commentsToken = new TypeToken<List<Comment>>() {
		};
		verify(gitHubClient).get(any(GitHubRequest.class));
	}
}
