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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.junit.Before;

/**
 * Base live test class.
 */
public abstract class LiveTest {

	/**
	 * Configured client
	 */
	protected GitHubClient client;

	/**
	 * Writable repository
	 */
	protected String writableRepo;

	/**
	 * Configure client
	 *
	 * @param client
	 * @return specified client
	 */
	protected GitHubClient configure(GitHubClient client) {
		String user = System.getProperty("github.test.user");
		String password = System.getProperty("github.test.password");
		writableRepo = System.getProperty("github.test.repository");
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
		GitHubClient client = null;
		if (url != null) {
			URL parsed = new URL(url);
			client = new GitHubClient(parsed.getHost(), parsed.getPort(),
					parsed.getProtocol());
		} else
			client = new GitHubClient();
		return configure(client);
	}

	/**
	 * Set up live unit test
	 *
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		String testUrl = System.getProperty("github.test.url");
		this.client = createClient(testUrl);
	}

	/**
	 * Check authenticated user is present
	 */
	public void checkUser() {
		assertNotNull("Test requires authenticated user", client.getUser());
	}
}
