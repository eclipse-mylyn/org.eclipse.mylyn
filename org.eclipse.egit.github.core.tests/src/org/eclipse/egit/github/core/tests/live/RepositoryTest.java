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

import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.eclipse.egit.github.core.service.RepositoryService;

/**
 * Live repository test
 */
public class RepositoryTest extends LiveTest {

	/**
	 * Test fetching a repository
	 * 
	 * @throws IOException
	 */
	public void testFetch() throws IOException {
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

}
