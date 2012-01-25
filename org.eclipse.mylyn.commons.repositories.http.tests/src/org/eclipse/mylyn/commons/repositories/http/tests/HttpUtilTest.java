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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.eclipse.mylyn.commons.repositories.http.core.HttpUtil;
import org.eclipse.mylyn.commons.sdk.util.TestProxy;
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

}
