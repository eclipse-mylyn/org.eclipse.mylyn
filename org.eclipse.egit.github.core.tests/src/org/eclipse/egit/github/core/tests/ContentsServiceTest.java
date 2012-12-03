/******************************************************************************
 *  Copyright (c) 2012 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.ContentsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests of {@link ContentsService}
 */
@RunWith(MockitoJUnitRunner.class)
public class ContentsServiceTest {

	@Mock
	private GitHubClient client;

	@Mock
	private GitHubResponse response;

	private ContentsService service;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@Before
	public void before() throws IOException {
		doReturn(response).when(client).get(any(GitHubRequest.class));
		service = new ContentsService(client);
	}

	/**
	 * Get readme for null repository
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getReadmeNullRepository() throws Exception {
		service.getReadme(null);
	}

	/**
	 * Get readme for repository
	 *
	 * @throws Exception
	 */
	@Test
	public void getReadme() throws Exception {
		RepositoryId repo = new RepositoryId("o", "n");
		service.getReadme(repo);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/o/n/readme");
		verify(client).get(request);
	}

	/**
	 * Get contents for null repository
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getContentsNullRepository() throws Exception {
		service.getContents(null);
	}

	/**
	 * Get contents at root in repository
	 *
	 * @throws Exception
	 */
	@Test
	public void getRootContents() throws Exception {
		RepositoryId repo = new RepositoryId("o", "n");
		service.getContents(repo);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/o/n/contents");
		verify(client).get(request);
	}

	/**
	 * Get contents at path in repository
	 *
	 * @throws Exception
	 */
	@Test
	public void getContents() throws Exception {
		RepositoryId repo = new RepositoryId("o", "n");
		service.getContents(repo, "a/b");
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/o/n/contents/a/b");
		verify(client).get(request);
	}
}
