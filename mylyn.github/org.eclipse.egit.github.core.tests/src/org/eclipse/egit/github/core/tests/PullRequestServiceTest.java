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
package org.eclipse.egit.github.core.tests;

import java.io.IOException;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests of {@link PullRequestService}
 */
@RunWith(MockitoJUnitRunner.class)
public class PullRequestServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	@Mock
	private SearchRepository repository;

	private PullRequestService pullRequestService;

	/**
	 * Test case set up
	 */
	@Before
	public void before() {
		pullRequestService = new PullRequestService(gitHubClient);
	}

	/**
	 * Create pull request service with null client
	 */
	@Test(expected = IllegalArgumentException.class)
	public void constructorNullArgument() {
		new PullRequestService(null);
	}

	/**
	 * Get pull request with null repository
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getPullRequestNullRepository() throws IOException {
		pullRequestService.getPullRequest(null, 3);
	}

	/**
	 * Get pull requests with null repository
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getPullRequestsNullRepository() throws IOException {
		pullRequestService.getPullRequests(null, "not null");
	}

	/**
	 * Get pull requests with null state
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getPullRequestsNullState() throws IOException {
		pullRequestService.getPullRequests(repository, null);
	}

	/**
	 * Get pull request with repository that generates a null id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getPullRequestsNullRepositoryId() throws IOException {
		pullRequestService.getPullRequests(new IRepositoryIdProvider() {

			public String generateId() {
				return null;
			}
		}, "test_state");
	}
}
