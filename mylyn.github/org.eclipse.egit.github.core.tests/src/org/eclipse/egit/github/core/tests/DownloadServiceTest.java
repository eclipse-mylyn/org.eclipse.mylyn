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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.egit.github.core.Download;
import org.eclipse.egit.github.core.DownloadResource;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.DownloadService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests of {@link DownloadService}
 */
@RunWith(MockitoJUnitRunner.class)
public class DownloadServiceTest {

	@Mock
	private GitHubClient client;

	@Mock
	private GitHubResponse response;

	private RepositoryId repo;

	private DownloadService service;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@Before
	public void before() throws IOException {
		service = new DownloadService(client);
		doReturn(response).when(client).get(any(GitHubRequest.class));
		repo = new RepositoryId("o", "n");
	}

	/**
	 * Create service using default constructor
	 */
	@Test
	public void constructor() {
		assertNotNull(new DownloadService().getClient());
	}

	/**
	 * Get download
	 *
	 * @throws IOException
	 */
	@Test
	public void getDownload() throws IOException {
		service.getDownload(repo, 3);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/repos/o/n/downloads/3");
		verify(client).get(request);
	}

	/**
	 * Get downloads
	 *
	 * @throws IOException
	 */
	@Test
	public void getDownloads() throws IOException {
		service.getDownloads(repo);
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/repos/o/n/downloads"));
		verify(client).get(request);
	}

	/**
	 * Create download resource
	 *
	 * @throws IOException
	 */
	@Test
	public void createResource() throws IOException {
		Download download = new Download().setName("dl.txt");
		service.createResource(repo, download);
		verify(client).post("/repos/o/n/downloads", download,
				DownloadResource.class);
	}

	/**
	 * Delete downloads
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteDownload() throws IOException {
		service.deleteDownload(repo, 49);
		verify(client).delete("/repos/o/n/downloads/49");
	}

	/**
	 * Upload resource with null resource
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void uploadResourceNullResource() throws IOException {
		service.uploadResource(null, new ByteArrayInputStream(new byte[0]), 1);
	}

	/**
	 * Upload resource with null stream
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void uploadResourceNullStream() throws IOException {
		service.uploadResource(new DownloadResource(), null, 1);
	}
}
