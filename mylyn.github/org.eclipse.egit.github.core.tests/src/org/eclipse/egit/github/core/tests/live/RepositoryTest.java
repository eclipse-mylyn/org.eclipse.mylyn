/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests.live;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Live repository test
 */
public class RepositoryTest extends LiveTest {

	/**
	 * Test fetching a repository
	 * 
	 * @throws IOException
	 */
	@Test
	public void fetchRepositories() throws IOException {
		RepositoryService service = new RepositoryService(
				createClient(IGitHubConstants.URL_API_V2));
		List<Repository> repos = service.getRepositories("defunkt");
		assertNotNull(repos);
		assertFalse(repos.isEmpty());
		for (Repository repo : repos) {
			assertNotNull(repo);
			assertNotNull(repo.getName());
			assertNotNull(repo.getOwner());
			assertNotNull(repo.getId());
			assertNotNull(repo.getUrl());
			assertNotNull(repo.getCreatedAt());
			assertTrue(repo.getSize() >= 0);
			assertTrue(repo.getForks() >= 0);
			assertTrue(repo.getOpenIssues() >= 0);
			assertTrue(repo.getWatchers() >= 0);
		}
	}

	/**
	 * Test creating a repository
	 * 
	 * @throws IOException
	 */
	@Test
	@Ignore
	public void createRepository() throws IOException {
		assertNotNull("Client user is required", client.getUser());
		RepositoryService service = new RepositoryService(
				createClient(IGitHubConstants.URL_API_V2));
		Repository repository = new Repository(client.getUser(), "test-create-"
				+ System.currentTimeMillis());
		repository.setPrivate(true);
		Repository created = service.createRepository(repository);
		assertNotNull(created);
		assertNotSame(repository, created);
		assertTrue(created.isPrivate());
		assertEquals(repository.getOwner(), created.getOwner());
		assertEquals(repository.getName(), created.getName());
	}

}
