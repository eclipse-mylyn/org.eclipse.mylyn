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
 * Unit tests for {@link GistService}
 */
@SuppressWarnings("restriction")
@RunWith(MockitoJUnitRunner.class)
public class GistServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	private GistService gistService;

	@Before
	public void before() {
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
		verify(gitHubClient).get("/gists/1.json", Gist.class);
	}

	@Test(expected = AssertionFailedException.class)
	public void getGists_NullUser() throws IOException {
		gistService.getGists(null);
	}

	@Test
	public void getGists_OK() throws IOException {
		gistService.getGists("test_user");
		TypeToken<List<Gist>> gistsToken = new TypeToken<List<Gist>>() {
		};
		verify(gitHubClient).get("/users/test_user/gists.json",
				gistsToken.getType());
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
}
