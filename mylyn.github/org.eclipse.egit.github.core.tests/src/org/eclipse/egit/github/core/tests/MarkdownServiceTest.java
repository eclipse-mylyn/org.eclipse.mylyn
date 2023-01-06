/******************************************************************************
 *  Copyright (c) 2012 GitHub Inc.
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

import static org.eclipse.egit.github.core.client.IGitHubConstants.CHARSET_UTF8;
import static org.eclipse.egit.github.core.service.MarkdownService.MODE_MARKDOWN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.MarkdownService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests of {@link MarkdownService}
 */
@RunWith(MockitoJUnitRunner.class)
public class MarkdownServiceTest {

	@Mock
	private GitHubClient client;

	private MarkdownService service;

	private RepositoryId repo;

	private String content;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@Before
	public void before() throws IOException {
		content = "<p>content</p>";
		ByteArrayInputStream stream = new ByteArrayInputStream(
				content.getBytes(CHARSET_UTF8));
		doReturn(stream).when(client).postStream(any(String.class),
				any(Object.class));
		service = new MarkdownService(client);
		repo = new RepositoryId("o", "n");
	}

	/**
	 * Get repository HTML
	 *
	 * @throws Exception
	 */
	@Test
	public void getRepositoryHtml() throws Exception {
		assertEquals(content, service.getRepositoryHtml(repo, "input"));
	}

	/**
	 * Get repository HTML
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getRepositoryHtmlNullRepository() throws Exception {
		service.getRepositoryHtml(null, "input");
	}

	/**
	 * Get HTML
	 *
	 * @throws Exception
	 */
	@Test
	public void getHtml() throws Exception {
		assertEquals(content, service.getHtml("input", MODE_MARKDOWN));
	}
}
