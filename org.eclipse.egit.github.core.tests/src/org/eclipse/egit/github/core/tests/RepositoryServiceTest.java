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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryHook;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.RepositoryMerging;
import org.eclipse.egit.github.core.RepositoryMergingResponse;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests of {@link RepositoryService}
 */
@RunWith(MockitoJUnitRunner.class)
public class RepositoryServiceTest {

	@Mock
	private GitHubClient client;

	@Mock
	private GitHubResponse response;

	private RepositoryId repo;

	private RepositoryService service;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@Before
	public void before() throws IOException {
		service = new RepositoryService(client);
		doReturn(response).when(client).get(any(GitHubRequest.class));
		repo = new RepositoryId("o", "n");
	}

	/**
	 * Create service using default constructor
	 */
	@Test
	public void constructor() {
		assertNotNull(new RepositoryService().getClient());
	}

	/**
	 * Create repository with null repository
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createRepositoryNullRepository() throws IOException {
		service.createRepository(null);
	}

	/**
	 * Create repository
	 *
	 * @throws IOException
	 */
	@Test
	public void createRepository() throws IOException {
		Repository repo = new Repository();
		repo.setName("n");
		repo.setOwner(new User().setLogin("o"));
		service.createRepository(repo);
		verify(client).post("/user/repos", repo, Repository.class);
	}

	/**
	 * Edit repository with null repository
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void editRepositoryNullRepository() throws IOException {
		service.editRepository(null);
	}

	/**
	 * Edit repository
	 *
	 * @throws IOException
	 */
	@Test
	public void editRepository() throws IOException {
		Repository repo = new Repository();
		repo.setName("n");
		repo.setOwner(new User().setLogin("o"));
		service.editRepository(repo);
		verify(client).post("/repos/o/n", repo, Repository.class);
	}

	/**
	 * Edit repository with fields
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void editRepositoryWithNullFields() throws IOException {
		service.editRepository("o", "n", null);
	}

	/**
	 * Edit repository with fields
	 *
	 * @throws IOException
	 */
	@Test
	public void editRepositoryWithFields() throws IOException {
		Map<String, Object> fields = new HashMap<String, Object>();
		fields.put("has_issues", true);
		fields.put("homepage", "test://address");
		service.editRepository("o", "n", fields);
		verify(client).post("/repos/o/n", fields, Repository.class);
	}

	/**
	 * Edit repository with fields
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void editRepositoryProviderWithNullFields() throws IOException {
		service.editRepository(repo, null);
	}

	/**
	 * Edit repository with fields
	 *
	 * @throws IOException
	 */
	@Test
	public void editRepositoryProviderWithFields() throws IOException {
		Map<String, Object> fields = new HashMap<String, Object>();
		fields.put("has_issues", true);
		fields.put("homepage", "test://address");
		service.editRepository(repo, fields);
		verify(client).post("/repos/o/n", fields, Repository.class);
	}

	/**
	 * Create repository with null organization name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createOrgRepositoryNullOrg() throws IOException {
		service.createRepository(null, new Repository());
	}

	/**
	 * Create repository with empty organizaton name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createOrgRepositoryEmptyOrg() throws IOException {
		service.createRepository("", new Repository());
	}

	/**
	 * Create organization repository with null repository
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createOrgRepositoryNullRepository() throws IOException {
		service.createRepository("anorg", null);
	}

	/**
	 * Create organization repository
	 *
	 * @throws IOException
	 */
	@Test
	public void createOrgRepository() throws IOException {
		Repository repo = new Repository();
		repo.setName("n");
		repo.setOwner(new User().setLogin("o"));
		service.createRepository("abc", repo);
		verify(client).post("/orgs/abc/repos", repo, Repository.class);
	}

	/**
	 * Get repository using an repository provider
	 *
	 * @throws IOException
	 */
	@Test
	public void getRepositoryProvider() throws IOException {
		service.getRepository(repo);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/o/n");
		verify(client).get(request);
	}

	/**
	 * Get repository using owner owner and name
	 *
	 * @throws IOException
	 */
	@Test
	public void getRepository() throws IOException {
		service.getRepository("o", "n");
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/o/n");
		verify(client).get(request);
	}

	/**
	 * Get forks
	 *
	 * @throws IOException
	 */
	@Test
	public void getForks() throws IOException {
		service.getForks(repo);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/o/n/forks"));
		verify(client).get(request);
	}

	/**
	 * Fork repository
	 *
	 * @throws IOException
	 */
	@Test
	public void forkRepository() throws IOException {
		service.forkRepository(repo);
		verify(client).post("/repos/o/n/forks", null, Repository.class);
	}

	/**
	 * Fork repository to organization
	 *
	 * @throws IOException
	 */
	@Test
	public void forkRepositoryToOrg() throws IOException {
		service.forkRepository(repo, "abc");
		verify(client).post("/repos/o/n/forks?org=abc", null, Repository.class);
	}

	/**
	 * Get repositories
	 *
	 * @throws IOException
	 */
	@Test
	public void getRepositories() throws IOException {
		service.getRepositories();
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/user/repos"));
		verify(client).get(request);
	}

	/**
	 * Page all repositories
	 *
	 * @throws IOException
	 */
	@Test
	public void pageAllRepositories() throws IOException {
		service.pageAllRepositories().next();
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repositories"));
		verify(client).get(request);
	}

	/**
	 * Page all repositories
	 *
	 * @throws IOException
	 */
	@Test
	public void pageAllRepositoriesSince() throws IOException {
		service.pageAllRepositories(1234).next();
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repositories?since=1234"));
		verify(client).get(request);
	}

	/**
	 * Get repositories with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getRepositoriesNullUser() throws IOException {
		service.getRepositories((String) null);
	}

	/**
	 * Get repositories with empty user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getRepositoriesEmptyUser() throws IOException {
		service.getRepositories("");
	}

	/**
	 * Get user repositories
	 *
	 * @throws IOException
	 */
	@Test
	public void getUserRepositories() throws IOException {
		service.getRepositories("u1");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/users/u1/repos"));
		verify(client).get(request);
	}

	/**
	 * Get repositories with null organization
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getRepositoriesNullOrg() throws IOException {
		service.getOrgRepositories(null);
	}

	/**
	 * Get repositories with empty organization
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getRepositoriesEmptyOrg() throws IOException {
		service.getOrgRepositories("");
	}

	/**
	 * Get organization repositories
	 *
	 * @throws IOException
	 */
	@Test
	public void getOrgRepositories() throws IOException {
		service.getOrgRepositories("o1");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/orgs/o1/repos"));
		verify(client).get(request);
	}

	/**
	 * Search repositories with null query
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void searchRepositoriesNullQuery() throws IOException {
		service.searchRepositories((String) null);
	}

	/**
	 * Search repository with empty query
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void searchRepositoriesEmptyQuery() throws IOException {
		service.searchRepositories("");
	}

	/**
	 * Search repositories
	 *
	 * @throws IOException
	 */
	@Test
	public void searchRepositories() throws IOException {
		service.searchRepositories("test");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/legacy/repos/search/test"));
		verify(client).get(request);
	}

	/**
	 * Search repositories matching language
	 *
	 * @throws IOException
	 */
	@Test
	public void searchRepositoriesMatchingLanguage() throws IOException {
		service.searchRepositories("buffers", "c");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/legacy/repos/search/buffers?language=c"));
		verify(client).get(request);
	}

	/**
	 * Search repositories starting at page
	 *
	 * @throws IOException
	 */
	@Test
	public void searchRepositoriesStartingAtPage() throws IOException {
		service.searchRepositories("buffers", 50);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/legacy/repos/search/buffers?start_page=50"));
		verify(client).get(request);
	}

	/**
	 * Search repositories with query that needs escaping
	 *
	 * @throws IOException
	 */
	@Test
	public void searchEscaped() throws IOException {
		service.searchRepositories("a and a.");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/legacy/repos/search/a%20and%20a%2E"));
		verify(client).get(request);
	}

	/**
	 * Get languages in repository
	 *
	 * @throws IOException
	 */
	@Test
	public void getLanguages() throws IOException {
		service.getLanguages(repo);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/o/n/languages");
		verify(client).get(request);
	}

	/**
	 * Get branches in repository
	 *
	 * @throws IOException
	 */
	@Test
	public void getBranches() throws IOException {
		service.getBranches(repo);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/o/n/branches"));
		verify(client).get(request);
	}

	/**
	 * Get tags in repository
	 *
	 * @throws IOException
	 */
	@Test
	public void getTags() throws IOException {
		service.getTags(repo);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/o/n/tags"));
		verify(client).get(request);
	}

	/**
	 * Get public repositories for current user
	 *
	 * @throws IOException
	 */
	@Test
	public void getPublicRepositories() throws IOException {
		service.getRepositories(Collections.singletonMap(
				RepositoryService.FILTER_TYPE, RepositoryService.TYPE_PUBLIC));
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/user/repos?type=public"));
		verify(client).get(request);
	}

	/**
	 * Get public repositories for current user
	 *
	 * @throws IOException
	 */
	@Test
	public void getPrivateOrgRepositories() throws IOException {
		service.getOrgRepositories("org1", Collections.singletonMap(
				RepositoryService.FILTER_TYPE, RepositoryService.TYPE_PRIVATE));
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/orgs/org1/repos?type=private"));
		verify(client).get(request);
	}

	/**
	 * Get contributors to repository
	 *
	 * @throws IOException
	 */
	@Test
	public void getContributors() throws IOException {
		service.getContributors(repo, false);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/o/n/contributors"));
		verify(client).get(request);
	}

	/**
	 * Get contributors including anonymous ones to repository
	 *
	 * @throws IOException
	 */
	@Test
	public void getContributorsWithAnonymous() throws IOException {
		service.getContributors(repo, true);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/o/n/contributors?anon=1"));
		verify(client).get(request);
	}

	/**
	 * Get hooks in repository
	 *
	 * @throws IOException
	 */
	@Test
	public void getHooks() throws IOException {
		service.getHooks(repo);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/o/n/hooks"));
		verify(client).get(request);
	}

	/**
	 * Get hook in repository
	 *
	 * @throws IOException
	 */
	@Test
	public void getHook() throws IOException {
		service.getHook(repo, 43);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/o/n/hooks/43");
		verify(client).get(request);
	}

	/**
	 * Create hook in repository
	 *
	 * @throws IOException
	 */
	@Test
	public void createHook() throws IOException {
		RepositoryHook hook = new RepositoryHook();
		service.createHook(repo, hook);
		verify(client).post("/repos/o/n/hooks", hook, RepositoryHook.class);
	}

	/**
	 * Edit hook in repository with null hook;
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void editHookNullHook() throws IOException {
		service.editHook(repo, null);
	}

	/**
	 * Edit hook in repository
	 *
	 * @throws IOException
	 */
	@Test
	public void editHook() throws IOException {
		RepositoryHook hook = new RepositoryHook();
		hook.setId(5006);
		service.editHook(repo, hook);
		verify(client).post("/repos/o/n/hooks/5006", hook, RepositoryHook.class);
	}

	/**
	 * Delete hook in repository
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteHook() throws IOException {
		service.deleteHook(repo, 4949);
		verify(client).delete("/repos/o/n/hooks/4949");
	}

	/**
	 * Run hook in repository
	 *
	 * @throws IOException
	 */
	@Test
	public void runHook() throws IOException {
		service.testHook(repo, 5609);
		verify(client).post("/repos/o/n/hooks/5609/test");
	}

	/**
	 * Run merge in repository
	 *
	 * @throws IOException
	 */
	@Test
	public void runMerge() throws IOException {
		RepositoryMerging merge = new RepositoryMerging();
		merge.setBase("develop");
		merge.setHead("master");
		merge.setCommitMessage("Test Merge");
		service.mergingBranches(repo, merge);
		verify(client).post("/repos/o/n/merges", merge,
				RepositoryMergingResponse.class);
	}
}
