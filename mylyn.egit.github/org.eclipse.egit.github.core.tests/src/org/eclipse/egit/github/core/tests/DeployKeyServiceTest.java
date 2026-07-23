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
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.egit.github.core.Key;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.DeployKeyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests of {@link DeployKeyService}
 */
@RunWith(MockitoJUnitRunner.class)
public class DeployKeyServiceTest {

	@Mock
	private GitHubClient client;

	@Mock
	private GitHubResponse response;

	private DeployKeyService service;

	private RepositoryId repo;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@Before
	public void before() throws IOException {
		doReturn(response).when(client).get(any(GitHubRequest.class));
		service = new DeployKeyService(client);
		repo = new RepositoryId("o", "n");
	}

	/**
	 * Create service using default constructor
	 */
	@Test
	public void constructor() {
		assertNotNull(new DeployKeyService().getClient());
	}

	/**
	 * Get keys
	 *
	 * @throws IOException
	 */
	@Test
	public void getKeys() throws IOException {
		service.getKeys(repo);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/o/n/keys"));
		verify(client).get(request);
	}

	/**
	 * Get key
	 *
	 * @throws IOException
	 */
	@Test
	public void getKey() throws IOException {
		service.getKey(repo, 40);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/o/n/keys/40");
		verify(client).get(request);
	}

	/**
	 * Create key
	 *
	 * @throws IOException
	 */
	@Test
	public void createKey() throws IOException {
		Key key = new Key();
		service.createKey(repo, key);
		verify(client).post("/repos/o/n/keys", key, Key.class);
	}

	/**
	 * Edit key with null key
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void editKeyNullKey() throws IOException {
		service.editKey(repo, null);
	}

	/**
	 * Edit key
	 *
	 * @throws IOException
	 */
	@Test
	public void editKey() throws IOException {
		Key key = new Key().setId(8);
		service.editKey(repo, key);
		verify(client).post("/repos/o/n/keys/8", key, Key.class);
	}

	/**
	 * Delete key
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteKey() throws IOException {
		service.deleteKey(repo, 88);
		verify(client).delete("/repos/o/n/keys/88");
	}
}
