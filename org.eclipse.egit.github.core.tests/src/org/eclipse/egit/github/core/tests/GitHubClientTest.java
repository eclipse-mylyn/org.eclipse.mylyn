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

import org.apache.http.HttpHost;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.junit.Test;

/**
 * Unit tests of {@link GitHubClient}
 */
public class GitHubClientTest {

	/**
	 * Create client with null host
	 */
	@Test(expected = IllegalArgumentException.class)
	public void constructorNullArgument() {
		new GitHubClient((HttpHost) null);
	}
}
