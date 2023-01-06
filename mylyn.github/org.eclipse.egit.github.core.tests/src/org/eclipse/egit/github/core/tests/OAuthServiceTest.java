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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.egit.github.core.Authorization;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.OAuthService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests of {@link OAuthService}
 */
@RunWith(MockitoJUnitRunner.class)
public class OAuthServiceTest {

	@Mock
	private GitHubClient client;

	@Mock
	private GitHubResponse response;

	private OAuthService service;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@Before
	public void before() throws IOException {
		doReturn(response).when(client).get(any(GitHubRequest.class));
		service = new OAuthService(client);
	}

	/**
	 * Create service with null client
	 */
	@Test(expected = IllegalArgumentException.class)
	public void constructorNullArgument() {
		new OAuthService(null);
	}

	/**
	 * Create service using default constructor
	 */
	@Test
	public void defaultConstructor() {
		assertNotNull(new OAuthService().getClient());
	}

	/**
	 * Get authorizations
	 *
	 * @throws IOException
	 */
	@Test
	public void getAuthorizations() throws IOException {
		service.getAuthorizations();
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/authorizations"));
		verify(client).get(request);
	}

	/**
	 * Get authorization
	 *
	 * @throws IOException
	 */
	@Test
	public void getAuthorization() throws IOException {
		service.getAuthorization(400);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/authorizations/400");
		verify(client).get(request);
	}

	/**
	 * Delete authorization
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteAuthorization() throws IOException {
		service.deleteAuthorization(678);
		verify(client).delete("/authorizations/678");
	}

	/**
	 * Create authorization
	 *
	 * @throws IOException
	 */
	@Test
	public void createAuthorization() throws IOException {
		Authorization auth = new Authorization();
		service.createAuthorization(auth);
		verify(client).post("/authorizations", auth, Authorization.class);
	}

	/**
	 * Test adding scopes to authorization
	 *
	 * @throws IOException
	 */
	@Test
	public void addScopes() throws IOException {
		Collection<String> scopes = Arrays.asList("repo");
		service.addScopes(300, scopes);
		verify(client).post("/authorizations/300",
				Collections.singletonMap("add_scopes", scopes),
				Authorization.class);
	}

	/**
	 * Test removing scopes to authorization
	 *
	 * @throws IOException
	 */
	@Test
	public void removeScopes() throws IOException {
		Collection<String> scopes = Arrays.asList("user");
		service.removeScopes(400, scopes);
		verify(client).post("/authorizations/400",
				Collections.singletonMap("remove_scopes", scopes),
				Authorization.class);
	}

	/**
	 * Test setting scopes to authorization
	 *
	 * @throws IOException
	 */
	@Test
	public void setScopes() throws IOException {
		Collection<String> scopes = Arrays.asList("gist");
		service.setScopes(500, scopes);
		verify(client)
				.post("/authorizations/500",
						Collections.singletonMap("scopes", scopes),
						Authorization.class);
	}
}
