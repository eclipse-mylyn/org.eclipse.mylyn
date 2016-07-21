/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
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

import org.apache.http.HttpStatus;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationException;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;
import org.eclipse.mylyn.commons.repositories.http.core.HttpRequestProcessor;
import org.eclipse.mylyn.commons.sdk.util.MockServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class CommonHttpClientPreemptiveAuthTest {

	private MockServer server;

	public CommonHttpClientPreemptiveAuthTest() {
	}

	@Before
	public void setUp() throws Exception {
		server = new MockServer();
		server.startAndWait();
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	@Test
	public void testExecuteGetNoPreemptiveAuth() throws Exception {
		RepositoryLocation location = new RepositoryLocation(server.getUrl());
		location.setCredentials(AuthenticationType.HTTP, new UserCredentials("user", "pass"));
		CommonHttpClient client = new CommonHttpClient(location);

		server.addResponse(MockServer.OK);
		CommonHttpResponse response = client.executeGet("/", null, HttpRequestProcessor.DEFAULT);
		assertEquals(HttpStatus.SC_OK, response.getStatusCode());
		assertEquals(null, server.getRequest().getHeader("Authorization"));
	}

	@Test(expected = AuthenticationException.class)
	public void testExecuteGetAuthChallengeNoCredentials() throws Exception {
		RepositoryLocation location = new RepositoryLocation(server.getUrl());
		CommonHttpClient client = new CommonHttpClient(location);

		server.addResponse(MockServer.UNAUTHORIZED);
		client.executeGet("/", null, HttpRequestProcessor.DEFAULT);
	}

	@Test
	public void testExecuteGetAuthChallenge() throws Exception {
		RepositoryLocation location = new RepositoryLocation(server.getUrl());
		location.setCredentials(AuthenticationType.HTTP, new UserCredentials("user", "pass"));
		CommonHttpClient client = new CommonHttpClient(location);

		server.addResponse(MockServer.UNAUTHORIZED);
		server.addResponse(MockServer.OK);
		CommonHttpResponse response = client.executeGet("/", null, HttpRequestProcessor.DEFAULT);
		assertEquals(HttpStatus.SC_OK, response.getStatusCode());
		assertEquals("Did not expect preemptive credentails", null, server.getRequest().getHeader("Authorization"));
		assertEquals("Expect credentails on challenge", "Authorization: Basic dXNlcjpwYXNz",
				server.getRequest().getHeader("Authorization"));
	}

	@Test
	public void testExecuteGetPreemptiveAuth() throws Exception {
		RepositoryLocation location = new RepositoryLocation(server.getUrl());
		location.setCredentials(AuthenticationType.HTTP, new UserCredentials("user", "pass"));
		CommonHttpClient client = new CommonHttpClient(location);

		client.setPreemptiveAuthenticationEnabled(true);
		server.addResponse(MockServer.OK);
		CommonHttpResponse response = client.executeGet("/", null, HttpRequestProcessor.DEFAULT);
		assertEquals("Authorization: Basic dXNlcjpwYXNz", server.getRequest().getHeader("Authorization"));
		assertEquals(HttpStatus.SC_OK, response.getStatusCode());

		// subsequent requests will have cached credentials
		client.setPreemptiveAuthenticationEnabled(false);
		server.addResponse(MockServer.OK);
		response = client.executeGet("/", null, HttpRequestProcessor.DEFAULT);
		assertEquals("Authorization: Basic dXNlcjpwYXNz", server.getRequest().getHeader("Authorization"));
		assertEquals(HttpStatus.SC_OK, response.getStatusCode());
	}

}
