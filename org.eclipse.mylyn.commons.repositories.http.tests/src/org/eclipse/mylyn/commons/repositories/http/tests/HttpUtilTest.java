/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.repositories.http.core.HttpUtil;
import org.eclipse.mylyn.commons.sdk.util.TestProxy;
import org.eclipse.mylyn.commons.sdk.util.TestProxy.Message;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class HttpUtilTest {

	private TestProxy testProxy;

	private DefaultHttpClient client;

	private ThreadSafeClientConnManager connectionManager;

	public HttpUtilTest() {
	}

	@Before
	public void setUp() throws Exception {
		testProxy = new TestProxy();
		testProxy.startAndWait();
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
		testProxy.stop();
	}

	@Test
	public void testGetRequestPoolConnections() throws Exception {
		testProxy.addResponse(TestProxy.SERVICE_UNVAILABLE);
		HttpRequestBase request = new HttpGet(testProxy.getUrl());

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

		testProxy.addResponse(message);
		HttpRequestBase request = new HttpGet(testProxy.getUrl());

		HttpUtil.configureClient(client, null);
		HttpResponse response = HttpUtil.execute(client, null, request, null);
		assertEquals(1, connectionManager.getConnectionsInPool());
		HttpUtil.release(request, response, null);
		assertEquals(0, connectionManager.getConnectionsInPool());
	}
}
