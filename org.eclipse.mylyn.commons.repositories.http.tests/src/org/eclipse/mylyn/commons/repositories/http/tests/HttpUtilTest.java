/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.http.tests;

import static org.junit.Assert.assertEquals;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpProtocolParams;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.repositories.http.core.HttpUtil;
import org.eclipse.mylyn.commons.sdk.util.MockServer;
import org.eclipse.mylyn.commons.sdk.util.MockServer.Message;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class HttpUtilTest {

	private static final int /*NetUtil.*/MAX_HTTP_HOST_CONNECTIONS_DEFAULT = 100;

	private static final int /*NetUtil.*/MAX_HTTP_TOTAL_CONNECTIONS_DEFAULT = 1000;

	private MockServer server;

	private DefaultHttpClient client;

	private ThreadSafeClientConnManager connectionManager;

	public HttpUtilTest() {
	}

	@Before
	public void setUp() throws Exception {
		server = new MockServer();
		server.startAndWait();
		connectionManager = new ThreadSafeClientConnManager();
		client = new DefaultHttpClient() {
			@Override
			protected ClientConnectionManager createClientConnectionManager() {
				return connectionManager;
			}
		};
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	@Test
	public void testGetRequestPoolConnections() throws Exception {
		server.addResponse(MockServer.SERVICE_UNVAILABLE);
		HttpRequestBase request = new HttpGet(server.getUrl());

		HttpUtil.configureClient(client, null);
		assertEquals(0, connectionManager.getConnectionsInPool());

		HttpResponse response = HttpUtil.execute(client, null, request, null);
		assertEquals(HttpStatus.SC_SERVICE_UNAVAILABLE, response.getStatusLine().getStatusCode());
		assertEquals(1, connectionManager.getConnectionsInPool());
	}

	@Test(expected = AssertionFailedException.class)
	public void testConfigureAuthenticationNullUrl() {
		HttpUtil.configureAuthentication(client, new RepositoryLocation((String) null), new UserCredentials("", ""));
	}

	@Test(expected = AssertionFailedException.class)
	public void testConfigureAuthenticationNullClient() {
		HttpUtil.configureAuthentication(null, new RepositoryLocation("url"), new UserCredentials("", ""));
	}

	@Test(expected = AssertionFailedException.class)
	public void testConfigureAuthenticationNullCredentials() {
		HttpUtil.configureAuthentication(client, new RepositoryLocation("url"), null);
	}

	@Test
	public void testConfigureAuthentication() {
		HttpUtil.configureAuthentication(client, new RepositoryLocation("url"), new UserCredentials("", ""));
	}

	@Test
	public void testConfigureProxy() {
		HttpUtil.configureProxy(client, new RepositoryLocation("url"));
	}

	@Test(expected = AssertionFailedException.class)
	public void testConfigureProxyNullClient() {
		HttpUtil.configureProxy(null, new RepositoryLocation("url"));
	}

	@Test(expected = AssertionFailedException.class)
	public void testConfigureProxyNullLocation() {
		HttpUtil.configureProxy(client, null);
	}

	@Test
	public void testGetEmptyGzipResponse() throws Exception {
		client = new ContentEncodingHttpClient() {
			@Override
			protected ClientConnectionManager createClientConnectionManager() {
				return connectionManager;
			}
		};

		Message message = new Message("HTTP/1.1 200 OK");
		message.headers.add("Content-Length: 0");
		message.headers.add("Content-Encoding: gzip");
		message.headers.add("Connection: close");

		server.addResponse(message);
		HttpRequestBase request = new HttpGet(server.getUrl());

		HttpUtil.configureClient(client, null);
		HttpResponse response = HttpUtil.execute(client, null, request, null);
		assertEquals(1, connectionManager.getConnectionsInPool());
		HttpUtil.release(request, response, null);
		assertEquals(0, connectionManager.getConnectionsInPool());
	}

	@Test
	public void testConfigureClient() {
		HttpUtil.configureClient(client, "Agent 007");
		assertEquals("Agent 007", HttpProtocolParams.getUserAgent(client.getParams()));

		HttpUtil.configureClient(client, "Special Agent Fox Mulder");
		assertEquals("Special Agent Fox Mulder", HttpProtocolParams.getUserAgent(client.getParams()));

		HttpUtil.configureClient(client, null);
		assertEquals("Special Agent Fox Mulder", HttpProtocolParams.getUserAgent(client.getParams()));
	}

	@Test
	public void testConfigureConnectionManager() {
		ThreadSafeClientConnManager connManager = HttpUtil.getConnectionManager();

		assertEquals(CoreUtil.TEST_MODE ? 2 : MAX_HTTP_HOST_CONNECTIONS_DEFAULT, connManager.getDefaultMaxPerRoute());
		assertEquals(CoreUtil.TEST_MODE ? 20 : MAX_HTTP_TOTAL_CONNECTIONS_DEFAULT, connManager.getMaxTotal());
	}
}
