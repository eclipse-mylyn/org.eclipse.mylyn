/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tests;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import javax.net.ssl.SSLHandshakeException;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.mylyn.tests.TestProxy.Message;
import org.eclipse.mylyn.web.core.AuthenticatedProxy;
import org.eclipse.mylyn.web.core.SslProtocolSocketFactory;
import org.eclipse.mylyn.web.core.WebClientUtil;

/**
 * @author Steffen Pingel
 */
public class WebClientUtilTest extends TestCase {

	private TestProxy testProxy;

	private HttpClient client;

	private InetSocketAddress proxyAddress;

	public WebClientUtilTest() {
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		testProxy = new TestProxy();
		int proxyPort = testProxy.startAndWait();
		assert proxyPort > 0;
		proxyAddress = new InetSocketAddress("localhost", proxyPort);

		client = new HttpClient();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		testProxy.stop();
	}

	public void testConnect() throws Exception {
		String url = "http://" + proxyAddress.getHostName() + ":" + proxyAddress.getPort() + "/";
		WebClientUtil.setupHttpClient(client, null, url, "", "");

		testProxy.addResponse(TestProxy.OK);

		GetMethod method = new GetMethod("/");
		int statusCode = client.executeMethod(method);
		assertEquals(200, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("GET / HTTP/1.1", request.request);
	}

	public void testConnectSsl() throws Exception {
		String url = "https://" + proxyAddress.getHostName() + ":" + proxyAddress.getPort() + "/";
		WebClientUtil.setupHttpClient(client, null, url, "", "");

		GetMethod method = new GetMethod("/");
		try {
			int statusCode = client.executeMethod(method);
			fail("Expected SSLHandshakeException, got status: " + statusCode);
		} catch (SSLHandshakeException e) {
		}

		assertFalse(testProxy.hasRequest());
	}

	public void testConnectProxy() throws Exception {
		String url = "http://foo/bar";
		Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		WebClientUtil.setupHttpClient(client, proxy, url, "", "");

		testProxy.addResponse(TestProxy.OK);

		GetMethod method = new GetMethod(WebClientUtil.getRequestPath(url));
		int statusCode = client.executeMethod(method);
		assertEquals(200, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);
	}

	public void testConnectProxyHttpAuth() throws Exception {
		String url = "http://foo/bar";
		Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		WebClientUtil.setupHttpClient(client, proxy, url, "user", "pass");

		Message response = new Message("HTTP/1.1 401 Authentication required");
		response.headers.add("WWW-Authenticate: Basic realm=\"Foo\"");
		testProxy.addResponse(response);
		testProxy.addResponse(TestProxy.OK);

		GetMethod method = new GetMethod(url);
		int statusCode = client.executeMethod(method);
		assertEquals(200, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);

		request = testProxy.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);
		assertEquals("Basic dXNlcjpwYXNz", request.getHeaderValue("Authorization"));
	}

	public void testConnectProxyNoProxyCredentials() throws Exception {
		String url = "http://foo/bar";
		Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		WebClientUtil.setupHttpClient(client, proxy, url, "user", "pass");

		Message response = new Message("HTTP/1.1 407 Proxy authentication required");
		response.headers.add("Proxy-Authenticate: Basic realm=\"Foo\"");
		testProxy.addResponse(response);
		testProxy.addResponse(TestProxy.OK);

		GetMethod method = new GetMethod(url);
		int statusCode = client.executeMethod(method);
		assertEquals(407, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);

		assertFalse("Expected HttpClient to close connection", testProxy.hasRequest());
	}

	public void testConnectProxyProxyCredentials() throws Exception {
		String url = "http://foo/bar";
		Proxy proxy = new AuthenticatedProxy(Type.HTTP, proxyAddress, "proxyUser", "proxyPass");
		WebClientUtil.setupHttpClient(client, proxy, url, "user", "pass");

		Message response = new Message("HTTP/1.1 407 Proxy authentication required");
		response.headers.add("Proxy-Authenticate: Basic realm=\"Foo\"");
		testProxy.addResponse(response);
		testProxy.addResponse(TestProxy.OK);

		GetMethod method = new GetMethod(url);
		int statusCode = client.executeMethod(method);
		assertEquals(200, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);

		request = testProxy.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);
		assertEquals("Basic cHJveHlVc2VyOnByb3h5UGFzcw==", request.getHeaderValue("Proxy-Authorization"));
	}

	public void testConnectProxyProxyCredentialsHttpAuth() throws Exception {
		String url = "http://foo/bar";
		Proxy proxy = new AuthenticatedProxy(Type.HTTP, proxyAddress, "proxyUser", "proxyPass");
		WebClientUtil.setupHttpClient(client, proxy, url, "user", "pass");

		Message response = new Message("HTTP/1.1 407 Proxy authentication required");
		response.headers.add("Proxy-Authenticate: Basic realm=\"Foo\"");
		testProxy.addResponse(response);
		response = new Message("HTTP/1.1 401 Authentication required");
		response.headers.add("WWW-Authenticate: Basic realm=\"Foo\"");
		testProxy.addResponse(response);
		testProxy.addResponse(TestProxy.OK);

		GetMethod method = new GetMethod(url);
		int statusCode = client.executeMethod(method);
		assertEquals(200, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);

		request = testProxy.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);
		assertEquals("Basic cHJveHlVc2VyOnByb3h5UGFzcw==", request.getHeaderValue("Proxy-Authorization"));

		request = testProxy.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);
		assertEquals("Basic dXNlcjpwYXNz", request.getHeaderValue("Authorization"));
	}

	public void testSslConnectProxy() throws Exception {
		String url = "https://foo/bar";
		Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		WebClientUtil.setupHttpClient(client, proxy, url, "", "");

		testProxy.addResponse(TestProxy.SERVICE_UNVAILABLE);

		GetMethod method = new GetMethod("/");
		int statusCode = client.executeMethod(method);
		assertEquals(503, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("CONNECT foo:443 HTTP/1.1", request.request);
	}

	public void testSslConnectProxyProxyCredentials() throws Exception {
		String url = "https://foo/bar";
		Proxy proxy = new AuthenticatedProxy(Type.HTTP, proxyAddress, "proxyUser", "proxyPass");
		WebClientUtil.setupHttpClient(client, proxy, url, "", "");

		Message response = new Message("HTTP/1.1 407 Proxy authentication required");
		response.headers.add("Proxy-Authenticate: Basic realm=\"Foo\"");
		testProxy.addResponse(response);
		testProxy.addResponse(TestProxy.SERVICE_UNVAILABLE);

		GetMethod method = new GetMethod("/");
		int statusCode = client.executeMethod(method);
		assertEquals(503, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("CONNECT foo:443 HTTP/1.1", request.request);

		request = testProxy.getRequest();
		assertEquals("CONNECT foo:443 HTTP/1.1", request.request);
		assertEquals("Basic cHJveHlVc2VyOnByb3h5UGFzcw==", request.getHeaderValue("Proxy-Authorization"));
	}

	public void testSslConnectProxyNoProxyCredentials() throws Exception {
		String url = "https://foo/bar";
		Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		WebClientUtil.setupHttpClient(client, proxy, url, "", "");

		Message response = new Message("HTTP/1.1 407 Proxy authentication required");
		response.headers.add("Proxy-Authenticate: Basic realm=\"Foo\"");
		testProxy.addResponse(response);
		testProxy.addResponse(TestProxy.SERVICE_UNVAILABLE);

		GetMethod method = new GetMethod("/");
		int statusCode = client.executeMethod(method);
		assertEquals(407, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("CONNECT foo:443 HTTP/1.1", request.request);

		assertFalse("Expected HttpClient to close connection", testProxy.hasRequest());
	}

	public void testSslConnectProxyTimeout() throws Exception {
		String url = "https://foo/bar";
		Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		WebClientUtil.setupHttpClient(client, proxy, url, "", "");

		testProxy.addResponse(TestProxy.OK);

		GetMethod method = new GetMethod("/");
		try {
			int statusCode = client.executeMethod(method);
			fail("Expected SSLHandshakeException, got status: " + statusCode);
		} catch (SSLHandshakeException e) {
		}

		Message request = testProxy.getRequest();
		assertEquals("CONNECT foo:443 HTTP/1.1", request.request);
	}

	public void testConnectSslClientCert() throws Exception {
		if (!SslProtocolSocketFactory.getInstance().hasKeyManager()) {
			// skip if keystore property is not set
			return;
		}

		String url = "https://mylyn.eclipse.org/secure/";
		WebClientUtil.setupHttpClient(client, null, url, "", "");

		GetMethod method = new GetMethod(WebClientUtil.getRequestPath(url));
		int statusCode = client.executeMethod(method);
		assertEquals(200, statusCode);
	}

}
