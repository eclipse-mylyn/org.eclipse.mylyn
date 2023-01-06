/*******************************************************************************
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
 *******************************************************************************/
package org.eclipse.egit.github.core.tests.live;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.User;
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
		RepositoryService service = new RepositoryService(client);
		List<Repository> repos = service.getRepositories("defunkt");
		assertNotNull(repos);
		assertFalse(repos.isEmpty());
		for (Repository repo : repos) {
			assertNotNull(repo);
			assertNotNull(repo.getName());
			assertNotNull(repo.getOwner());
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
		RepositoryService service = new RepositoryService(client);
		Repository repository = new Repository();
		repository.setOwner(new User().setLogin(client.getUser()));
		repository.setName("test-create-" + System.currentTimeMillis());
		repository.setPrivate(true);
		Repository created = service.createRepository(repository);
		assertNotNull(created);
		assertNotSame(repository, created);
		assertTrue(created.isPrivate());
		assertEquals(repository.getOwner(), created.getOwner());
		assertEquals(repository.getName(), created.getName());
	}

	/**
	 * Test forking a repository
	 *
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void forkRepository() throws Exception {
		assertNotNull("Client user is required", client.getUser());
		RepositoryService service = new RepositoryService(client);
		service.forkRepository(new RepositoryId(client.getUser(), "resque"));
	}

	/**
	 * Test fetching forks of a repository
	 *
	 * @throws IOException
	 */
	@Test
	public void fetchForks() throws IOException {
		RepositoryService service = new RepositoryService(client);
		List<Repository> repos = service.getForks(new RepositoryId("defunkt",
				"resque"));
		assertNotNull(repos);
		assertFalse(repos.isEmpty());
		for (Repository repo : repos) {
			assertNotNull(repo);
			assertTrue(repo.isFork());
		}
	}

	/**
	 * Get languages used in a repository
	 *
	 * @throws IOException
	 */
	@Test
	public void getLanguages() throws IOException {
		RepositoryService service = new RepositoryService(client);
		Map<String, Long> languages = service.getLanguages(new RepositoryId(
				"defunkt", "resque"));
		assertNotNull(languages);
		assertFalse(languages.isEmpty());
		for (Entry<String, Long> language : languages.entrySet()) {
			assertNotNull(language.getKey());
			assertFalse(language.getKey().length() == 0);
			assertTrue(language.getValue() > 0);
		}
	}
}
