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
import java.net.Socket;
import java.net.Proxy.Type;

import javax.net.ssl.SSLHandshakeException;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.tests.TestProxy.Message;
import org.eclipse.mylyn.web.core.AbstractWebLocation;
import org.eclipse.mylyn.web.core.AuthenticatedProxy;
import org.eclipse.mylyn.web.core.AuthenticationType;
import org.eclipse.mylyn.web.core.IProxyProvider;
import org.eclipse.mylyn.web.core.SslProtocolSocketFactory;
import org.eclipse.mylyn.web.core.WebClientUtil;
import org.eclipse.mylyn.web.core.WebLocation;

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

	public void testConnectCancelStalledConnect() throws Exception {
		final StubProgressMonitor monitor = new StubProgressMonitor();
		String host = "google.com";
		int port = 9999;

		try {
			Runnable runner = new Runnable() {
				public void run() {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
					monitor.canceled = true;
				}
			};
			new Thread(runner).start();
			WebClientUtil.connect(new Socket(), new InetSocketAddress(host, port), 5000, monitor);
			fail("Expected OperationCanceledException");
		} catch (OperationCanceledException expected) {
			assertTrue(monitor.isCanceled());
		}
	}

	public void testExecute() throws Exception {
		StubProgressMonitor monitor = new StubProgressMonitor();
		HttpClient client = new HttpClient();
		String url = "http://eclipse.org/";
		WebLocation location = new WebLocation(url);
		HostConfiguration hostConfiguration = WebClientUtil.createHostConfiguration(client, location, monitor);

		GetMethod method = new GetMethod(url);
		try {
			int result = WebClientUtil.execute(client, hostConfiguration, method, monitor);
			assertEquals(HttpStatus.SC_OK, result);
		} finally {
			method.releaseConnection();
		}
	}

	public void testExecuteCancelStalledConnect() throws Exception {
		final StubProgressMonitor monitor = new StubProgressMonitor();
		HttpClient client = new HttpClient();
		String url = "http://google.com:9999/";
		WebLocation location = new WebLocation(url);
		HostConfiguration hostConfiguration = WebClientUtil.createHostConfiguration(client, location, monitor);

		GetMethod method = new GetMethod(url);
		try {
			Runnable runner = new Runnable() {
				public void run() {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
					monitor.canceled = true;
				}
			};
			new Thread(runner).start();
			WebClientUtil.execute(client, hostConfiguration, method, monitor);
			client.executeMethod(method);
			fail("Expected OperationCanceledException");
		} catch (OperationCanceledException expected) {
			assertTrue(monitor.isCanceled());
		} finally {
			method.releaseConnection();
		}
	}

	public void testExecuteAlreadyCancelled() throws Exception {
		StubProgressMonitor monitor = new StubProgressMonitor();
		HttpClient client = new HttpClient();
		String url = "http://eclipse.org/";
		WebLocation location = new WebLocation(url);
		HostConfiguration hostConfiguration = WebClientUtil.createHostConfiguration(client, location, monitor);

		GetMethod method = new GetMethod(url);
		try {
			monitor.canceled = true;
			WebClientUtil.execute(client, hostConfiguration, method, monitor);
			fail("Expected InterruptedIOException");
		} catch (OperationCanceledException expected) {
		} finally {
			method.releaseConnection();
		}
	}

	public void testConfigureHttpClient() {
		HttpClient client = new HttpClient();

		WebClientUtil.configureHttpClient(client, "");
		assertEquals(WebClientUtil.USER_AGENT, client.getParams().getParameter(HttpMethodParams.USER_AGENT));

		WebClientUtil.configureHttpClient(client, null);
		assertEquals(WebClientUtil.USER_AGENT, client.getParams().getParameter(HttpMethodParams.USER_AGENT));

		WebClientUtil.configureHttpClient(client, "myagent");
		assertTrue(-1 != client.getParams().getParameter(HttpMethodParams.USER_AGENT).toString().indexOf("myagent"));

		// TODO test timeouts
	}

	public void testLocationConnect() throws Exception {
		String url = "http://" + proxyAddress.getHostName() + ":" + proxyAddress.getPort() + "/";
		AbstractWebLocation location = new WebLocation(url, null, null, null);
		HostConfiguration hostConfiguration = WebClientUtil.createHostConfiguration(client, location, null);

		testProxy.addResponse(TestProxy.OK);

		GetMethod method = new GetMethod("/");
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(200, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("GET / HTTP/1.1", request.request);
	}

	public void testLocationConnectSsl() throws Exception {
		String url = "https://" + proxyAddress.getHostName() + ":" + proxyAddress.getPort() + "/";
		AbstractWebLocation location = new WebLocation(url, null, null, null);
		HostConfiguration hostConfiguration = WebClientUtil.createHostConfiguration(client, location, null);

		GetMethod method = new GetMethod("/");
		try {
			int statusCode = client.executeMethod(hostConfiguration, method);
			fail("Expected SSLHandshakeException, got status: " + statusCode);
		} catch (SSLHandshakeException e) {
		}

		assertFalse(testProxy.hasRequest());
	}

	public void testLocationConnectProxy() throws Exception {
		String url = "http://foo/bar";
		final Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		AbstractWebLocation location = new WebLocation(url, null, null, new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return proxy;
			}
		});
		HostConfiguration hostConfiguration = WebClientUtil.createHostConfiguration(client, location, null);

		testProxy.addResponse(TestProxy.OK);

		GetMethod method = new GetMethod(WebClientUtil.getRequestPath(url));
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(200, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);
	}

	public void testLocationConnectProxyHttpAuth() throws Exception {
		String url = "http://foo/bar";
		final Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		WebLocation location = new WebLocation(url, "", "", new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return proxy;
			}
		});
		location.setCredentials(AuthenticationType.HTTP, "user", "pass");
		HostConfiguration hostConfiguration = WebClientUtil.createHostConfiguration(client, location, null);
		client.getParams().setAuthenticationPreemptive(true);

		Message response = new Message("HTTP/1.1 401 Authentication required");
		response.headers.add("WWW-Authenticate: Basic realm=\"Foo\"");
		testProxy.addResponse(response);
		testProxy.addResponse(TestProxy.OK);

		GetMethod method = new GetMethod(url);
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(401, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);
		assertEquals("Basic dXNlcjpwYXNz", request.getHeaderValue("Authorization"));
	}

	public void testLocationConnectProxyNoProxyCredentials() throws Exception {
		String url = "http://foo/bar";
		final Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		AbstractWebLocation location = new WebLocation(url, "user", "pass", new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return proxy;
			}
		});
		HostConfiguration hostConfiguration = WebClientUtil.createHostConfiguration(client, location, null);

		Message response = new Message("HTTP/1.1 407 Proxy authentication required");
		response.headers.add("Proxy-Authenticate: Basic realm=\"Foo\"");
		testProxy.addResponse(response);
		testProxy.addResponse(TestProxy.OK);

		GetMethod method = new GetMethod(url);
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(407, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);

		assertFalse("Expected HttpClient to close connection", testProxy.hasRequest());
	}

	public void testLocationConnectProxyProxyCredentials() throws Exception {
		String url = "http://foo/bar";
		final Proxy proxy = new AuthenticatedProxy(Type.HTTP, proxyAddress, "proxyUser", "proxyPass");
		AbstractWebLocation location = new WebLocation(url, "user", "pass", new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return proxy;
			}
		});
		HostConfiguration hostConfiguration = WebClientUtil.createHostConfiguration(client, location, null);
		client.getParams().setAuthenticationPreemptive(true);

		Message response = new Message("HTTP/1.1 407 Proxy authentication required");
		response.headers.add("Proxy-Authenticate: Basic realm=\"Foo\"");
		testProxy.addResponse(response);
		testProxy.addResponse(TestProxy.OK);

		GetMethod method = new GetMethod(url);
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(407, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);
		assertEquals("Basic cHJveHlVc2VyOnByb3h5UGFzcw==", request.getHeaderValue("Proxy-Authorization"));
	}

	public void testLocationConnectProxyProxyCredentialsHttpAuth() throws Exception {
		String url = "http://foo/bar";
		final Proxy proxy = new AuthenticatedProxy(Type.HTTP, proxyAddress, "proxyUser", "proxyPass");
		WebLocation location = new WebLocation(url, "", "", new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return proxy;
			}
		});
		location.setCredentials(AuthenticationType.HTTP, "user", "pass");

		HostConfiguration hostConfiguration = WebClientUtil.createHostConfiguration(client, location, null);
		client.getParams().setAuthenticationPreemptive(true);

		testProxy.addResponse(TestProxy.OK);

		GetMethod method = new GetMethod(url);
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(200, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);
		assertEquals("Basic cHJveHlVc2VyOnByb3h5UGFzcw==", request.getHeaderValue("Proxy-Authorization"));
		assertEquals("Basic dXNlcjpwYXNz", request.getHeaderValue("Authorization"));
	}

	public void testLocationSslConnectProxy() throws Exception {
		String url = "https://foo/bar";
		final Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		AbstractWebLocation location = new WebLocation(url, null, null, new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return proxy;
			}
		});
		HostConfiguration hostConfiguration = WebClientUtil.createHostConfiguration(client, location, null);
		;

		testProxy.addResponse(TestProxy.SERVICE_UNVAILABLE);

		GetMethod method = new GetMethod("/");
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(503, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("CONNECT foo:443 HTTP/1.1", request.request);
	}

	public void testLocationSslConnectProxyProxyCredentials() throws Exception {
		String url = "https://foo/bar";
		final Proxy proxy = new AuthenticatedProxy(Type.HTTP, proxyAddress, "proxyUser", "proxyPass");
		AbstractWebLocation location = new WebLocation(url, null, null, new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return proxy;
			}
		});
		HostConfiguration hostConfiguration = WebClientUtil.createHostConfiguration(client, location, null);
		;
		client.getParams().setAuthenticationPreemptive(true);

		testProxy.addResponse(TestProxy.SERVICE_UNVAILABLE);

		GetMethod method = new GetMethod("/");
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(503, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("CONNECT foo:443 HTTP/1.1", request.request);
		assertEquals("Basic cHJveHlVc2VyOnByb3h5UGFzcw==", request.getHeaderValue("Proxy-Authorization"));
	}

	public void testLocationSslConnectProxyNoProxyCredentials() throws Exception {
		String url = "https://foo/bar";
		final Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		AbstractWebLocation location = new WebLocation(url, null, null, new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return proxy;
			}
		});
		HostConfiguration hostConfiguration = WebClientUtil.createHostConfiguration(client, location, null);
		;

		Message response = new Message("HTTP/1.1 407 Proxy authentication required");
		response.headers.add("Proxy-Authenticate: Basic realm=\"Foo\"");
		testProxy.addResponse(response);
		testProxy.addResponse(TestProxy.SERVICE_UNVAILABLE);

		GetMethod method = new GetMethod("/");
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(407, statusCode);

		Message request = testProxy.getRequest();
		assertEquals("CONNECT foo:443 HTTP/1.1", request.request);

		assertFalse("Expected HttpClient to close connection", testProxy.hasRequest());
	}

	public void testLoationSslConnectProxyTimeout() throws Exception {
		String url = "https://foo/bar";
		final Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		AbstractWebLocation location = new WebLocation(url, null, null, new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return proxy;
			}
		});
		HostConfiguration hostConfiguration = WebClientUtil.createHostConfiguration(client, location, null);

		testProxy.addResponse(TestProxy.OK);

		GetMethod method = new GetMethod("/");
		try {
			int statusCode = client.executeMethod(hostConfiguration, method);
			fail("Expected SSLHandshakeException, got status: " + statusCode);
		} catch (SSLHandshakeException e) {
		}

		Message request = testProxy.getRequest();
		assertEquals("CONNECT foo:443 HTTP/1.1", request.request);
	}

	public void testLocationConnectSslClientCert() throws Exception {
		if (!SslProtocolSocketFactory.getInstance().hasKeyManager()) {
			// skip if keystore property is not set
			return;
		}

		String url = "https://mylyn.eclipse.org/secure/";
		AbstractWebLocation location = new WebLocation(url, null, null, null);
		HostConfiguration hostConfiguration = WebClientUtil.createHostConfiguration(client, location, null);

		GetMethod method = new GetMethod(WebClientUtil.getRequestPath(url));
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(200, statusCode);
	}

	public void testGetUserAgent() {
		String userAgent = WebClientUtil.getUserAgent(null);
		assertEquals(userAgent, WebClientUtil.getUserAgent(""));
		assertEquals(-1, userAgent.indexOf("null"));
		assertEquals(-1, userAgent.indexOf("  "));
		assertEquals(0, userAgent.indexOf("Mylyn"));

		userAgent = WebClientUtil.getUserAgent("abc");
		assertEquals(-1, userAgent.indexOf("null"));
		assertEquals(-1, userAgent.indexOf("  "));
		assertEquals(0, userAgent.indexOf("Mylyn"));
		assertTrue(userAgent.contains(" abc "));
	}

	private class StubProgressMonitor implements IProgressMonitor {

		private volatile boolean canceled;

		public void beginTask(String name, int totalWork) {
		}

		public void done() {
		}

		public void internalWorked(double work) {
		}

		public boolean isCanceled() {
			return canceled;
		}

		public void setCanceled(boolean value) {
			this.canceled = value;
		}

		public void setTaskName(String name) {
		}

		public void subTask(String name) {
		}

		public void worked(int work) {
		}

	}

}
