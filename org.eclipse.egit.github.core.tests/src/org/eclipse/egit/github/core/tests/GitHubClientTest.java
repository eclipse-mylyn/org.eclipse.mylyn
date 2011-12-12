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

import static org.junit.Assert.assertEquals;

import org.apache.http.HttpHost;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.IGitHubConstants;
import org.junit.Test;

/**
 * Unit tests of {@link GitHubClient}
 */
public class GitHubClientTest {

	private static class PrefixClient extends GitHubClient {

		public PrefixClient(String host) {
			super(host);
		}

		String uri(String uri) {
			return super.configureUri(uri);
		}
	}

	/**
	 * Verify prefix with API v2 host
	 */
	@Test
	public void prefixHostApiV2() {
		PrefixClient client = new PrefixClient(IGitHubConstants.HOST_API_V2);
		assertEquals("/api/v3/repos/o/n", client.uri("/api/v3/repos/o/n"));
		assertEquals("/repos/o/n", client.uri("/repos/o/n"));
		assertEquals("/api/v2/json/repos/search/test",
				client.uri("/api/v2/json/repos/search/test"));
	}

	/**
	 * Verify prefix with API v3 host
	 */
	@Test
	public void prefixHostApiV3() {
		PrefixClient client = new PrefixClient(IGitHubConstants.HOST_API);
		assertEquals("/api/v3/repos/o/n", client.uri("/api/v3/repos/o/n"));
		assertEquals("/repos/o/n", client.uri("/repos/o/n"));
		assertEquals("/api/v2/json/repos/search/test",
				client.uri("/api/v2/json/repos/search/test"));
	}

	/**
	 * Verify prefix with localhost
	 */
	@Test
	public void prefixLocalhost() {
		PrefixClient client = new PrefixClient("localhost");
		assertEquals("/api/v3/repos/o/n", client.uri("/repos/o/n"));
		assertEquals("/api/v3/repos/o/n", client.uri("/api/v3/repos/o/n"));
		assertEquals("/api/v2/json/repos/search/test",
				client.uri("/api/v2/json/repos/search/test"));
	}
}
