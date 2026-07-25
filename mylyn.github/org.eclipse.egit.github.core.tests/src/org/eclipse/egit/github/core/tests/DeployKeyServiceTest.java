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
 *    See git history
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.egit.github.core.Key;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.DeployKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Unit tests of {@link DeployKeyService}
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("nls")
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
	@BeforeEach
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
	@Test
	public void editKeyNullKey() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> service.editKey(repo, null));
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
