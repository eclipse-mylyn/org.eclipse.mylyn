/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.WatcherService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests of {@link WatcherService}
 */
@RunWith(MockitoJUnitRunner.class)
public class WatcherServiceTest {

	@Mock
	private GitHubClient client;

	@Mock
	private GitHubResponse response;

	private WatcherService service;

	private RepositoryId repo;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@Before
	public void before() throws IOException {
		doReturn(response).when(client).get(any(GitHubRequest.class));
		service = new WatcherService(client);
		repo = new RepositoryId("o", "n");
	}

	/**
	 * Create service using default constructor
	 */
	@Test
	public void constructor() {
		assertNotNull(new WatcherService().getClient());
	}

	/**
	 * Get watchers
	 *
	 * @throws IOException
	 */
	@Test
	public void getWatchers() throws IOException {
		service.getWatchers(repo);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/o/n/watchers"));
		verify(client).get(request);
	}

	/**
	 * Get watched
	 *
	 * @throws IOException
	 */
	@Test
	public void getCurrentWatched() throws IOException {
		service.getWatched();
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/user/watched"));
		verify(client).get(request);
	}

	/**
	 * Get watched with null name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getWatchedNullName() throws IOException {
		service.getWatched(null);
	}

	/**
	 * Get watched with empty name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getWatchedEmptyName() throws IOException {
		service.getWatched("");
	}

	/**
	 * Get watched
	 *
	 * @throws IOException
	 */
	@Test
	public void getWatched() throws IOException {
		service.getWatched("auser");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/users/auser/watched"));
		verify(client).get(request);
	}

	/**
	 * Is watching
	 *
	 * @throws IOException
	 */
	@Test
	public void isWatching() throws IOException {
		service.isWatching(repo);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/user/watched/o/n");
		verify(client).get(request);
	}

	/**
	 * Watch repository
	 *
	 * @throws IOException
	 */
	@Test
	public void watch() throws IOException {
		service.watch(repo);
		verify(client).put("/user/watched/o/n");
	}

	/**
	 * Unwatch repository
	 *
	 * @throws IOException
	 */
	@Test
	public void unwatch() throws IOException {
		service.unwatch(repo);
		verify(client).delete("/user/watched/o/n");
	}

	/**
	 * Page watchers
	 *
	 * @throws IOException
	 */
	@Test
	public void pageWatchers() throws IOException {
		PageIterator<User> iter = service.pageWatchers(repo);
		assertNotNull(iter);
		assertTrue(iter.hasNext());
		assertEquals(Utils.page("/repos/o/n/watchers"), iter.getRequest()
				.generateUri());
	}

	/**
	 * Page watched
	 *
	 * @throws IOException
	 */
	@Test
	public void pageCurrentWatched() throws IOException {
		PageIterator<Repository> iter = service.pageWatched();
		assertNotNull(iter);
		assertTrue(iter.hasNext());
		assertEquals(Utils.page("/user/watched"), iter.getRequest()
				.generateUri());
	}

	/**
	 * Page watched
	 *
	 * @throws IOException
	 */
	@Test
	public void pageWatched() throws IOException {
		PageIterator<Repository> iter = service.pageWatched("auser");
		assertNotNull(iter);
		assertTrue(iter.hasNext());
		assertEquals(Utils.page("/users/auser/watched"), iter.getRequest()
				.generateUri());
	}
}
