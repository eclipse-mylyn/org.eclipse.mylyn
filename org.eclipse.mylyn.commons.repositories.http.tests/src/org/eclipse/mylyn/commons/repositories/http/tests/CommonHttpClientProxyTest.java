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
import org.eclipse.mylyn.commons.sdk.util.TestProxy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class CommonHttpClientProxyTest {

	private TestProxy testProxy;

	public CommonHttpClientProxyTest() {
	}

	@Before
	public void setUp() throws Exception {
		testProxy = new TestProxy();
		testProxy.startAndWait();
	}

	@After
	public void tearDown() throws Exception {
		testProxy.stop();
	}

	@Test
	public void testExecuteGetNoPreemptiveAuth() throws Exception {
		RepositoryLocation location = new RepositoryLocation(testProxy.getUrl());
		location.setCredentials(AuthenticationType.HTTP, new UserCredentials("user", "pass"));
		CommonHttpClient client = new CommonHttpClient(location);

		testProxy.addResponse(TestProxy.OK);
		CommonHttpResponse response = client.executeGet("/", null, HttpRequestProcessor.DEFAULT);
		assertEquals(HttpStatus.SC_OK, response.getStatusCode());
		assertEquals(null, testProxy.getRequest().getHeader("Authorization"));
	}

	@Test(expected = AuthenticationException.class)
	public void testExecuteGetAuthChallengeNoCredentials() throws Exception {
		RepositoryLocation location = new RepositoryLocation(testProxy.getUrl());
		CommonHttpClient client = new CommonHttpClient(location);

		testProxy.addResponse(TestProxy.UNAUTHORIZED);
		client.executeGet("/", null, HttpRequestProcessor.DEFAULT);
	}

	@Test
	public void testExecuteGetAuthChallenge() throws Exception {
		RepositoryLocation location = new RepositoryLocation(testProxy.getUrl());
		location.setCredentials(AuthenticationType.HTTP, new UserCredentials("user", "pass"));
		CommonHttpClient client = new CommonHttpClient(location);

		testProxy.addResponse(TestProxy.UNAUTHORIZED);
		testProxy.addResponse(TestProxy.OK);
		CommonHttpResponse response = client.executeGet("/", null, HttpRequestProcessor.DEFAULT);
		assertEquals(HttpStatus.SC_OK, response.getStatusCode());
		assertEquals("Did not expect preemptive credentails", null, testProxy.getRequest().getHeader("Authorization"));
		assertEquals("Expect credentails on challenge", "Authorization: Basic dXNlcjpwYXNz", testProxy.getRequest()
				.getHeader("Authorization"));
	}

	@Test
	public void testExecuteGetPreemptiveAuth() throws Exception {
		RepositoryLocation location = new RepositoryLocation(testProxy.getUrl());
		location.setCredentials(AuthenticationType.HTTP, new UserCredentials("user", "pass"));
		CommonHttpClient client = new CommonHttpClient(location);

		client.setPreemptiveAuthenticationEnabled(true);
		testProxy.addResponse(TestProxy.OK);
		CommonHttpResponse response = client.executeGet("/", null, HttpRequestProcessor.DEFAULT);
		assertEquals("Authorization: Basic dXNlcjpwYXNz", testProxy.getRequest().getHeader("Authorization"));
		assertEquals(HttpStatus.SC_OK, response.getStatusCode());

		// subsequent requests will have cached credentials
		client.setPreemptiveAuthenticationEnabled(false);
		testProxy.addResponse(TestProxy.OK);
		response = client.executeGet("/", null, HttpRequestProcessor.DEFAULT);
		assertEquals("Authorization: Basic dXNlcjpwYXNz", testProxy.getRequest().getHeader("Authorization"));
		assertEquals(HttpStatus.SC_OK, response.getStatusCode());
	}

}
