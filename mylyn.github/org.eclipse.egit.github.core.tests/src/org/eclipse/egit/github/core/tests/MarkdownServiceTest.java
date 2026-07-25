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
 *    See git history
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.eclipse.egit.github.core.client.IGitHubConstants.CHARSET_UTF8;
import static org.eclipse.egit.github.core.service.MarkdownService.MODE_MARKDOWN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.MarkdownService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Unit tests of {@link MarkdownService}
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("nls")
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
	@BeforeEach
	public void before() throws IOException {
		content = "<p>content</p>";
		ByteArrayInputStream stream = new ByteArrayInputStream(
				content.getBytes(CHARSET_UTF8));
		doReturn(stream).when(client).postStream(any(String.class), any(Object.class));
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
	@Test
	public void getRepositoryHtmlNullRepository() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> service.getRepositoryHtml(null, "input"));
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
