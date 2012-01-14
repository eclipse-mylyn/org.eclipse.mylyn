/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.http.tests;

import static org.junit.Assert.assertEquals;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class CommonHttpClientTest {

	@Test
	public void testGetRequest() throws Exception {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl("http://eclipse.org/");

		HttpGet request = new HttpGet(location.getUrl());
		CommonHttpClient client = new CommonHttpClient(location);
		HttpResponse response = client.execute(request, null);
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
	}

	@Test
	public void testHttpAuthenticationTypeHttp() throws Exception {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl("http://eclipse.org/");
		location.setCredentials(AuthenticationType.HTTP, new UserCredentials("username", "password"));

		HttpGet request = new HttpGet(location.getUrl());
		CommonHttpClient client = new CommonHttpClient(location);
		client.execute(request, null);

		AuthScope authScope = new AuthScope("eclipse.org", 80, AuthScope.ANY_REALM);
		Credentials httpCredentials = client.getHttpClient().getCredentialsProvider().getCredentials(authScope);
		assertEquals(new UsernamePasswordCredentials("username", "password"), httpCredentials);
	}

	@Test
	public void testHttpAuthenticationTypeRepository() throws Exception {
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl("http://eclipse.org/");
		location.setCredentials(AuthenticationType.REPOSITORY, new UserCredentials("username", "password"));

		HttpGet request = new HttpGet(location.getUrl());
		CommonHttpClient client = new CommonHttpClient(location);
		AuthScope authScope = new AuthScope("eclipse.org", 80, AuthScope.ANY_REALM);

		// credentials should be ignored
		client.execute(request, null);
		Credentials httpCredentials = client.getHttpClient().getCredentialsProvider().getCredentials(authScope);
		assertEquals(null, httpCredentials);

		client.setHttpAuthenticationType(AuthenticationType.REPOSITORY);
		// credentials should now be used
		client.execute(request, null);
		httpCredentials = client.getHttpClient().getCredentialsProvider().getCredentials(authScope);
		assertEquals(new UsernamePasswordCredentials("username", "password"), httpCredentials);
	}

}
