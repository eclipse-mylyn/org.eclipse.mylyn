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
package org.eclipse.mylyn.github.internal;

import static org.mockito.Mockito.when;

import java.io.IOException;

import org.eclipse.core.runtime.AssertionFailedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for {@link PullRequestServiceTest}
 */
@SuppressWarnings("restriction")
@RunWith(MockitoJUnitRunner.class)
public class PullRequestServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	@Mock
	private Repository repository;

	private PullRequestService pullRequestService;

	@Before
	public void before() {
		pullRequestService = new PullRequestService(gitHubClient);
	}

	@Test(expected = AssertionFailedException.class)
	public void constructor_NullArgument() {
		new PullRequestService(null);
	}

	@Test(expected = AssertionFailedException.class)
	public void getPullRequest_NullRepository() throws IOException {
		pullRequestService.getPullRequest(null, "not null");
	}

	@Test(expected = AssertionFailedException.class)
	public void getPullRequest_NullId() throws IOException {
		pullRequestService.getPullRequest(repository, null);
	}

	@Test(expected = AssertionFailedException.class)
	public void getPullRequest_NullRepositoryId() throws IOException {
		when(repository.getId()).thenReturn(null);
		pullRequestService.getPullRequest(repository, "test_id");
	}

	@Test
	public void getPullRequest_OK() throws IOException {
		// the OK unit test is not possible with Mockito, but with JMOckit
	}

}
