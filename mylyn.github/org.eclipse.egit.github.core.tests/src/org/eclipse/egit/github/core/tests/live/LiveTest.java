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
 *    See git history
 *******************************************************************************/
package org.eclipse.egit.github.core.tests.live;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base live test class.
 */
@SuppressWarnings("nls")
public abstract class LiveTest {

	private static final String TEST_USER = Optional.ofNullable(System.getenv("GITHUB_TEST_USER"))
			.orElse(System.getProperty("github.test.user"));

	private static final String PW = Optional.ofNullable(System.getenv("GITHUB_TEST_PASSWORD"))
			.orElse(System.getProperty("github.test.password"));

	private static final String REPOSITORY = Optional.ofNullable(System.getenv("GITHUB_TEST_REPOSITORY"))
			.orElse(System.getProperty("github.test.repository"));

	/**
	 * Configured client
	 */
	protected GitHubClient client;

	/**
	 * Writable repository
	 */
	protected String writableRepo;

	@BeforeAll
	static void beforeAll() {
		assumeTrue(TEST_USER != null && //
				PW != null && //
				REPOSITORY != null, "Missing repository information: user, pw, or repository");
	}
	/**
	 * Configure client
	 *
	 * @param client
	 * @return specified client
	 */
	protected GitHubClient configure(GitHubClient client) {
		String user = TEST_USER;
		String password = PW;
		writableRepo = REPOSITORY;
		client.setCredentials(user, password);
		return client;
	}

	/**
	 * Create client for url
	 *
	 * @param url
	 * @return client
	 * @throws IOException
	 */
	protected GitHubClient createClient(String url) throws IOException {
		final GitHubClient client;
		if (url != null) {
			URL parsed;
			try {
				parsed = new URI(url).toURL();
			} catch (MalformedURLException | URISyntaxException e) {
				throw new IOException(e);
			}
			client = new GitHubClient(parsed.getHost(), parsed.getPort(), parsed.getProtocol());
		} else {
			client = new GitHubClient();
		}
		return configure(client);
	}

	/**
	 * Set up live unit test
	 *
	 * @throws Exception
	 */
	@BeforeEach
	public void setUp() throws Exception {
		String testUrl = System.getProperty("github.test.url");
		client = createClient(testUrl);
	}

	/**
	 * Check authenticated user is present
	 */
	public void checkUser() {
		assertNotNull(client.getUser(), "Test requires authenticated user");
	}
}
