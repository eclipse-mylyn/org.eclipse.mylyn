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

import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests of {@link CollaboratorService}
 */
@RunWith(MockitoJUnitRunner.class)
public class CollaboratorServiceTest {

	@Mock
	private GitHubClient client;

	@Mock
	private GitHubResponse response;

	private CollaboratorService service;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@Before
	public void before() throws IOException {
		doReturn(response).when(client).get(any(GitHubRequest.class));
		service = new CollaboratorService(client);
	}

	/**
	 * Create service using default constructor
	 */
	@Test
	public void constructor() {
		assertNotNull(new CollaboratorService().getClient());
	}

	/**
	 * Get all collaborators for repository
	 *
	 * @throws IOException
	 */
	@Test
	public void getCollaborators() throws IOException {
		RepositoryId repo = new RepositoryId("u", "p");
		service.getCollaborators(repo);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/u/p/collaborators"));
		verify(client).get(request);
	}

	/**
	 * Check collaborator with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void isCollaboratorNullUser() throws IOException {
		RepositoryId repo = new RepositoryId("u", "p");
		service.isCollaborator(repo, null);
	}

	/**
	 * Check collaborator with empty user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void isCollaboratorEmptyUser() throws IOException {
		RepositoryId repo = new RepositoryId("u", "p");
		service.isCollaborator(repo, "");
	}

	/**
	 * Check collaborator
	 *
	 * @throws IOException
	 */
	@Test
	public void isCollaborator() throws IOException {
		RepositoryId repo = new RepositoryId("u", "p");
		service.isCollaborator(repo, "collab");
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/u/p/collaborators/collab");
		verify(client).get(request);
	}

	/**
	 * Add collaborator
	 *
	 * @throws IOException
	 */
	@Test
	public void addCollaborator() throws IOException {
		RepositoryId repo = new RepositoryId("u", "p");
		service.addCollaborator(repo, "collab");
		verify(client).put("/repos/u/p/collaborators/collab");
	}

	/**
	 * Add collaborator with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void addCollaboratorNullUser() throws IOException {
		RepositoryId repo = new RepositoryId("u", "p");
		service.addCollaborator(repo, null);
	}

	/**
	 * Add collaborator with empty user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void addCollaboratorEmptyUser() throws IOException {
		RepositoryId repo = new RepositoryId("u", "p");
		service.addCollaborator(repo, "");
	}

	/**
	 * Remove collaborator
	 *
	 * @throws IOException
	 */
	@Test
	public void delete() throws IOException {
		RepositoryId repo = new RepositoryId("u", "p");
		service.removeCollaborator(repo, "collab");
		verify(client).delete("/repos/u/p/collaborators/collab");
	}

	/**
	 * Remove collaborator with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void removeCollaboratorNullUser() throws IOException {
		RepositoryId repo = new RepositoryId("u", "p");
		service.removeCollaborator(repo, null);
	}

	/**
	 * Remove collaborator with empty user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void removeCollaboratorEmptyUser() throws IOException {
		RepositoryId repo = new RepositoryId("u", "p");
		service.removeCollaborator(repo, "");
	}
}
