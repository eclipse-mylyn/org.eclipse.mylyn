/*******************************************************************************
 * Copyright (c) 2004, 2024 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.net;

import static org.eclipse.mylyn.commons.tests.net.NetUtilTest.MAX_HTTP_HOST_CONNECTIONS_DEFAULT;
import static org.eclipse.mylyn.commons.tests.net.NetUtilTest.MAX_HTTP_TOTAL_CONNECTIONS_DEFAULT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.MockServer;
import org.eclipse.mylyn.commons.sdk.util.MockServer.Message;
import org.eclipse.mylyn.commons.sdk.util.TestUrl;
import org.eclipse.mylyn.internal.commons.net.AuthenticatedProxy;
import org.eclipse.mylyn.internal.commons.net.CommonsNetPlugin;
import org.eclipse.mylyn.internal.commons.net.PollingInputStream;
import org.eclipse.mylyn.internal.commons.net.PollingSslProtocolSocketFactory;
import org.eclipse.mylyn.internal.commons.net.TimeoutInputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class WebUtilTest {

	static class StubProgressMonitor implements IProgressMonitor {

		private volatile boolean canceled;

		@Override
		public void beginTask(String name, int totalWork) {
		}

		@Override
		public void done() {
		}

		@Override
		public void internalWorked(double work) {
		}

		@Override
		public boolean isCanceled() {
			return canceled;
		}

		@Override
		public void setCanceled(boolean value) {
			canceled = value;
		}

		@Override
		public void setTaskName(String name) {
		}

		@Override
		public void subTask(String name) {
		}

		@Override
		public void worked(int work) {
		}

	}

	private MockServer server;

	private HttpClient client;

	private InetSocketAddress proxyAddress;

	public WebUtilTest() {
	}

	@Before
	public void setUp() throws Exception {
		server = new MockServer();
		int proxyPort = server.startAndWait();
		assert proxyPort > 0;
		proxyAddress = new InetSocketAddress("localhost", proxyPort);

		client = new HttpClient();
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	@Test
	public void testConnectCancelStalledConnect() throws Exception {
		final StubProgressMonitor monitor = new StubProgressMonitor();
		String host = "google.com";
		int port = 9999;

		try {
			Runnable runner = () -> {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				monitor.canceled = true;
			};
			new Thread(runner).start();
			WebUtil.connect(new Socket(), new InetSocketAddress(host, port), 5000, monitor);
			fail("Expected OperationCanceledException");
		} catch (OperationCanceledException expected) {
			assertTrue(monitor.isCanceled());
		} catch (ConnectException ignored) {
			System.err.println("Skipping testConnectCancelStalledConnect() due to blocking firewall");
		}
	}

	@Test
	public void testConfigureClient() throws Exception {
		WebLocation location = new WebLocation(TestUrl.DEFAULT.getHttpOk().toString());

		WebUtil.createHostConfiguration(client, location, null /*monitor*/);

		HttpConnectionManagerParams params = client.getHttpConnectionManager().getParams();
		assertEquals(CoreUtil.TEST_MODE ? 2 : MAX_HTTP_HOST_CONNECTIONS_DEFAULT,
				params.getMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION));
		assertEquals(CoreUtil.TEST_MODE ? 20 : MAX_HTTP_TOTAL_CONNECTIONS_DEFAULT, params.getMaxTotalConnections());
	}

	@Ignore("No CI Server")
	@Test
	public void testExecute() throws Exception {
		StubProgressMonitor monitor = new StubProgressMonitor();
		HttpClient client = new HttpClient();
		WebLocation location = new WebLocation(TestUrl.DEFAULT.getHttpOk().toString());
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, monitor);

		GetMethod method = new GetMethod(location.getUrl());
		try {
			int result = WebUtil.execute(client, hostConfiguration, method, monitor);
			assertEquals(HttpStatus.SC_OK, result);
		} finally {
			WebUtil.releaseConnection(method, monitor);
		}
	}

	@Test
	public void testExecuteCancelStalledConnect() throws Exception {
		final StubProgressMonitor monitor = new StubProgressMonitor();
		HttpClient client = new HttpClient();
		WebLocation location = new WebLocation(TestUrl.DEFAULT.getConnectionTimeout().toString());
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, monitor);

		GetMethod method = new GetMethod(location.getUrl());
		try {
			Runnable runner = () -> {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				monitor.canceled = true;
			};
			new Thread(runner).start();
			WebUtil.execute(client, hostConfiguration, method, monitor);
			client.executeMethod(method);
			fail("Expected OperationCanceledException");
		} catch (OperationCanceledException expected) {
			assertTrue(monitor.isCanceled());
		} catch (ConnectException ignored) {
			System.err.println("Skipping testExecuteCancelStalledConnect() due to blocking firewall");
		} finally {
			WebUtil.releaseConnection(method, monitor);
		}
	}

	@Test
	public void testExecuteAlreadyCancelled() throws Exception {
		StubProgressMonitor monitor = new StubProgressMonitor();
		HttpClient client = new HttpClient();
		WebLocation location = new WebLocation(TestUrl.DEFAULT.getHttpOk().toString());
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, monitor);

		GetMethod method = new GetMethod(location.getUrl());
		try {
			monitor.canceled = true;
			WebUtil.execute(client, hostConfiguration, method, monitor);
			fail("Expected InterruptedIOException");
		} catch (OperationCanceledException expected) {
		} finally {
			WebUtil.releaseConnection(method, monitor);
		}
	}

	@Test
	public void testConfigureHttpClient() {
		HttpClient client = new HttpClient();

		WebUtil.configureHttpClient(client, "");
		assertEquals(WebUtil.getUserAgent(""), client.getParams().getParameter(HttpMethodParams.USER_AGENT));

		WebUtil.configureHttpClient(client, null);
		assertEquals(WebUtil.getUserAgent(""), client.getParams().getParameter(HttpMethodParams.USER_AGENT));

		WebUtil.configureHttpClient(client, "myagent");
		assertTrue(-1 != client.getParams().getParameter(HttpMethodParams.USER_AGENT).toString().indexOf("myagent"));

		// TODO test timeouts
	}

	@Test
	public void testCreateHostConfigurationProxy() throws Exception {
		StubProgressMonitor monitor = new StubProgressMonitor();
		HttpClient client = new HttpClient();
		WebUtil.createHostConfiguration(client,
				new WebLocation(TestUrl.DEFAULT.getHttpOk().toString(), null, null, (host, proxyType) -> {
					assertEquals(IProxyData.HTTP_PROXY_TYPE, proxyType);
					return null;
				}), monitor);
		WebUtil.createHostConfiguration(client,
				new WebLocation(TestUrl.DEFAULT.getHttpsOk().toString(), null, null, (host, proxyType) -> {
					assertEquals(IProxyData.HTTPS_PROXY_TYPE, proxyType);
					return null;
				}), monitor);
	}

	@Ignore("No CI Server")
	@Test
	public void testReadTimeout() throws Exception {
		// wait 5 seconds for thread pool to be idle
		for (int i = 0; i < 10; i++) {
			if (((ThreadPoolExecutor) CommonsNetPlugin.getExecutorService()).getActiveCount() == 0) {
				break;
			}
			Thread.sleep(500);
		}
		assertEquals(0, ((ThreadPoolExecutor) CommonsNetPlugin.getExecutorService()).getActiveCount());

		String url = "http://" + proxyAddress.getHostName() + ":" + proxyAddress.getPort() + "/";
		AbstractWebLocation location = new WebLocation(url, null, null, null);
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, null);

		server.addResponse(MockServer.TIMEOUT);

		GetMethod method = new GetMethod("/");
		method.getParams().setSoTimeout(100);
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(200, statusCode);

		try (PollingInputStream in = new PollingInputStream(
				new TimeoutInputStream(method.getResponseBodyAsStream(), 8192, 500L, -1), 1,
				new NullProgressMonitor())) {
			in.read();
			fail("expected InterruptedIOException");
		} catch (InterruptedIOException e) {
			// expected
		}
		Thread.sleep(500);
		assertEquals(0, ((ThreadPoolExecutor) CommonsNetPlugin.getExecutorService()).getActiveCount());
	}

	@Test
	public void testLocationConnect() throws Exception {
		String url = "http://" + proxyAddress.getHostName() + ":" + proxyAddress.getPort() + "/";
		AbstractWebLocation location = new WebLocation(url, null, null, null);
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, null);

		server.addResponse(MockServer.OK);

		GetMethod method = new GetMethod("/");
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(200, statusCode);

		Message request = server.getRequest();
		assertEquals("GET / HTTP/1.1", request.request);
	}

	@Test
	public void testLocationConnectSsl() throws Exception {
		String url = "https://" + proxyAddress.getHostName() + ":" + proxyAddress.getPort() + "/";
		AbstractWebLocation location = new WebLocation(url, null, null, null);
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, null);

		server.setCloseOnConnect(true);

		GetMethod method = new GetMethod("/");
		try {
			int statusCode = client.executeMethod(hostConfiguration, method);
			fail("Expected SSLHandshakeException or connection reset, got status: " + statusCode);
		} catch (SSLHandshakeException e) {
		} catch (SocketException e) {
			// FIXME local environment not set up locally.
			if ("An established connection was aborted by the software in your host machine".equals(e.getMessage())) {
				System.err.println(
						"WebUtilTest.testLocationConnectSsl() test ignored: " + e.getMessage() + ". " + proxyAddress);
				return;
			} else {
				assertEquals("Connection reset", e.getMessage());
			}
		}

		assertFalse(server.hasRequest());
	}

	@Test
	public void testLocationConnectProxy() throws Exception {
		String url = "http://foo/bar";
		final Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		AbstractWebLocation location = new WebLocation(url, null, null, (host, proxyType) -> proxy);
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, null);

		server.addResponse(MockServer.OK);

		GetMethod method = new GetMethod(WebUtil.getRequestPath(url));
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(200, statusCode);

		Message request = server.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);
	}

	@Test
	public void testLocationConnectProxyHttpAuth() throws Exception {
		String url = "http://foo/bar";
		final Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		WebLocation location = new WebLocation(url, "", "", (host, proxyType) -> proxy);
		location.setCredentials(AuthenticationType.HTTP, "user", "pass");
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, null);
		client.getParams().setAuthenticationPreemptive(true);

		Message response = new Message("HTTP/1.1 401 Authentication required");
		response.headers.add("WWW-Authenticate: Basic realm=\"Foo\"");
		server.addResponse(response);
		server.addResponse(MockServer.OK);

		GetMethod method = new GetMethod(url);
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(401, statusCode);

		Message request = server.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);
		assertEquals("Basic dXNlcjpwYXNz", request.getHeaderValue("Authorization"));
	}

	@Test
	public void testLocationConnectProxyNoProxyCredentials() throws Exception {
		String url = "http://foo/bar";
		final Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		AbstractWebLocation location = new WebLocation(url, "user", "pass", (host, proxyType) -> proxy);
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, null);

		Message response = new Message("HTTP/1.1 407 Proxy authentication required");
		response.headers.add("Proxy-Authenticate: Basic realm=\"Foo\"");
		server.addResponse(response);
		server.addResponse(MockServer.OK);

		GetMethod method = new GetMethod(url);
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(407, statusCode);

		Message request = server.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);

		assertFalse("Expected HttpClient to close connection", server.hasRequest());
	}

	@Test
	public void testLocationConnectProxyProxyCredentials() throws Exception {
		String url = "http://foo/bar";
		final Proxy proxy = new AuthenticatedProxy(Type.HTTP, proxyAddress, "proxyUser", "proxyPass");
		AbstractWebLocation location = new WebLocation(url, "user", "pass", (host, proxyType) -> proxy);
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, null);
		client.getParams().setAuthenticationPreemptive(true);

		Message response = new Message("HTTP/1.1 407 Proxy authentication required");
		response.headers.add("Proxy-Authenticate: Basic realm=\"Foo\"");
		server.addResponse(response);
		server.addResponse(MockServer.OK);

		GetMethod method = new GetMethod(url);
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(407, statusCode);

		Message request = server.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);
		assertEquals("Basic cHJveHlVc2VyOnByb3h5UGFzcw==", request.getHeaderValue("Proxy-Authorization"));
	}

	@Test
	public void testLocationConnectProxyProxyCredentialsHttpAuth() throws Exception {
		String url = "http://foo/bar";
		final Proxy proxy = new AuthenticatedProxy(Type.HTTP, proxyAddress, "proxyUser", "proxyPass");
		WebLocation location = new WebLocation(url, "", "", (host, proxyType) -> proxy);
		location.setCredentials(AuthenticationType.HTTP, "user", "pass");

		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, null);
		client.getParams().setAuthenticationPreemptive(true);

		server.addResponse(MockServer.OK);

		GetMethod method = new GetMethod(url);
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(200, statusCode);

		Message request = server.getRequest();
		assertEquals("GET http://foo/bar HTTP/1.1", request.request);
		assertEquals("Basic cHJveHlVc2VyOnByb3h5UGFzcw==", request.getHeaderValue("Proxy-Authorization"));
		assertEquals("Basic dXNlcjpwYXNz", request.getHeaderValue("Authorization"));
	}

	@Test
	public void testLocationSslConnectProxy() throws Exception {
		String url = "https://foo/bar";
		final Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		AbstractWebLocation location = new WebLocation(url, null, null, (host, proxyType) -> proxy);
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, null);

		server.addResponse(MockServer.SERVICE_UNVAILABLE);

		GetMethod method = new GetMethod("/");
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(503, statusCode);

		Message request = server.getRequest();
		assertEquals("CONNECT foo:443 HTTP/1.1", request.request);
	}

	@Test
	public void testLocationSslConnectProxyProxyCredentials() throws Exception {
		String url = "https://foo/bar";
		final Proxy proxy = new AuthenticatedProxy(Type.HTTP, proxyAddress, "proxyUser", "proxyPass");
		AbstractWebLocation location = new WebLocation(url, null, null, (host, proxyType) -> proxy);
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, null);

		client.getParams().setAuthenticationPreemptive(true);

		server.addResponse(MockServer.SERVICE_UNVAILABLE);

		GetMethod method = new GetMethod("/");
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(503, statusCode);

		Message request = server.getRequest();
		assertEquals("CONNECT foo:443 HTTP/1.1", request.request);
		assertEquals("Basic cHJveHlVc2VyOnByb3h5UGFzcw==", request.getHeaderValue("Proxy-Authorization"));
	}

	@Test
	public void testLocationSslConnectProxyNoProxyCredentials() throws Exception {
		String url = "https://foo/bar";
		final Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		AbstractWebLocation location = new WebLocation(url, null, null, (host, proxyType) -> proxy);
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, null);

		Message response = new Message("HTTP/1.1 407 Proxy authentication required");
		response.headers.add("Proxy-Authenticate: Basic realm=\"Foo\"");
		server.addResponse(response);
		server.addResponse(MockServer.SERVICE_UNVAILABLE);

		GetMethod method = new GetMethod("/");
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(407, statusCode);

		Message request = server.getRequest();
		assertEquals("CONNECT foo:443 HTTP/1.1", request.request);

		assertFalse("Expected HttpClient to close connection", server.hasRequest());
	}

	@Test
	public void testLocationSslConnectProxyTimeout() throws Exception {
		String url = "https://foo/bar";
		final Proxy proxy = new Proxy(Type.HTTP, proxyAddress);
		AbstractWebLocation location = new WebLocation(url, null, null, (host, proxyType) -> proxy);
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, null);

		server.addResponse(MockServer.OK);

		GetMethod method = new GetMethod("/");
		// avoid second attempt to connect to proxy to get exception right away
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, (HttpMethodRetryHandler) (method1, exception, executionCount) -> false);
		try {
			int statusCode = client.executeMethod(hostConfiguration, method);
			fail("Expected SSLHandshakeException, got status: " + statusCode);
		} catch (SSLHandshakeException | SocketException e) {
			// connection reset, happens in some environments instead of SSLHandshakeExecption depending on how much data has been written before the socket is closed
		}

		Message request = server.getRequest();
		assertEquals("CONNECT foo:443 HTTP/1.1", request.request);
	}

	@Test
	public void testLocationConnectSslClientCert() throws Exception {
		if (CommonTestUtil.isCertificateAuthBroken()) {
			return; // skip test
		}

		String url = "https://mylyn.org/secure/";
		AbstractWebLocation location = new WebLocation(url);
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, null);

		if (!((PollingSslProtocolSocketFactory) hostConfiguration.getProtocol().getSocketFactory()).hasKeyManager()) {
			return; // skip test if keystore property is not set
		}

		GetMethod method = new GetMethod(WebUtil.getRequestPath(url));
		int statusCode = client.executeMethod(hostConfiguration, method);
		assertEquals(200, statusCode);
	}

	@Test
	public void testGetUserAgent() {
		String userAgent = WebUtil.getUserAgent(null);
		assertEquals(userAgent, WebUtil.getUserAgent(""));
		assertEquals(-1, userAgent.indexOf("null"));
		assertEquals(-1, userAgent.indexOf("  "));
		assertEquals(0, userAgent.indexOf("Mylyn"));

		userAgent = WebUtil.getUserAgent("abc");
		assertEquals(-1, userAgent.indexOf("null"));
		assertEquals(-1, userAgent.indexOf("  "));
		assertEquals(0, userAgent.indexOf("Mylyn"));
		assertTrue(userAgent.contains(" abc "));
	}

	@Test
	public void testUrlParsers() {
		String url = "https://example.com:444/folder/file.txt";
		assertEquals(444, WebUtil.getPort(url));
		assertEquals("example.com", WebUtil.getHost(url));
		assertEquals("/folder/file.txt", WebUtil.getRequestPath(url));

		url = "http://example.com/";
		assertEquals(80, WebUtil.getPort(url));
		assertEquals("example.com", WebUtil.getHost(url));
		assertEquals("/", WebUtil.getRequestPath(url));

		url = "http://example.com";
		assertEquals(80, WebUtil.getPort(url));
		assertEquals("example.com", WebUtil.getHost(url));
		assertEquals("", WebUtil.getRequestPath(url));

		url = "https://example.com:321";
		assertEquals(321, WebUtil.getPort(url));
		assertEquals("example.com", WebUtil.getHost(url));
		assertEquals("", WebUtil.getRequestPath(url));

		url = "example.com:321";
		assertEquals(321, WebUtil.getPort(url));
		assertEquals("example.com", WebUtil.getHost(url));
		assertEquals("", WebUtil.getRequestPath(url));

		url = "https://example.com:444/folder/file.txt?search=https://example.com:812/folder/file.txt";
		assertEquals(444, WebUtil.getPort(url));
		assertEquals("example.com", WebUtil.getHost(url));
		assertEquals("/folder/file.txt?search=https://example.com:812/folder/file.txt", WebUtil.getRequestPath(url));

		url = "https://example.com/folder/file.txt?search=https://example.com:812/folder/file.txt";
		assertEquals(443, WebUtil.getPort(url));
		assertEquals("example.com", WebUtil.getHost(url));
		assertEquals("/folder/file.txt?search=https://example.com:812/folder/file.txt", WebUtil.getRequestPath(url));

		url = "https://jira.codehaus.org/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?&pid=11093&resolution=-1&sorter/field=updated&sorter/order=DESC&tempMax=1000";
		assertEquals(443, WebUtil.getPort(url));
		assertEquals("jira.codehaus.org", WebUtil.getHost(url));
		assertEquals(
				"/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?&pid=11093&resolution=-1&sorter/field=updated&sorter/order=DESC&tempMax=1000",
				WebUtil.getRequestPath(url));
	}

	@Ignore("No CI Server")
	@Test
	public void testGetTitleFromUrl() throws Exception {
		assertEquals("Eclipse Mylyn Open Source Project",
				WebUtil.getTitleFromUrl(new WebLocation(TestUrl.DEFAULT.getHttpOk().toString()), null));
		// disabled: fails in environments where the DNS resolver redirects for unknown hosts
		//		try {
//			String title = WebUtil.getTitleFromUrl(new WebLocation("http://invalidurl"), null);
//			fail("Expected UnknownHostException, got: " + title);
//		} catch (UnknownHostException e) {
//		}
		String url = "http://" + proxyAddress.getHostName() + ":" + proxyAddress.getPort() + "/";
		server.addResponse(MockServer.OK);
		assertNull(WebUtil.getTitleFromUrl(new WebLocation(url), null));
	}

	/**
	 * Default encoding needs to be set to non-UTF8 encoding for this test to be meaningful, e.g. <code>-Dfile.encoding=ISO-8859-1</code>.
	 */
	@Test
	public void testGetTitleFromUrlUtf8() throws Exception {
		String message = """
				HTTP/1.1 200 OK
				Date: Sat, 03 Jan 2009 14:40:23 GMT
				Connection: close
				Content-Type: text/html; charset=UTF-8
				Content-Length: 30

				<html><title>\u00C3\u00BC</title></html>""";
		server.addResponse(message);
		String url = "http://" + proxyAddress.getHostName() + ":" + proxyAddress.getPort() + "/";
		assertEquals("\u00FC", WebUtil.getTitleFromUrl(new WebLocation(url) {
			@Override
			public Proxy getProxyForHost(String host, String proxyType) {
				return null;// ensure that we do not try to connect to localhost through a proxy server
			}
		}, null));
	}

	// FIXME
	@Test
	@Ignore
	public void testGetPlatformProxyDefault() {
//		assertNull(WebUtil.getProxy("mylyn.eclipse.org", Type.HTTP));
//		assertNull(WebUtil.getProxy("mylyn.eclipse.org", Type.DIRECT));
//		assertNull(WebUtil.getProxy("mylyn.eclipse.org", Type.SOCKS));
	}

	@Test
	@Ignore
	public void testGetPlatformProxy() {
//		IProxyService defaultProxyService = WebUtil.getProxyService();
//		try {
//			StubProxyService proxyService = new StubProxyService();
//			WebUtil.setProxyService(proxyService);
//			proxyService.setProxy(IProxyData.HTTP_PROXY_TYPE, "proxy", 8080, false);
//			Proxy proxy = WebUtil.getProxy("mylyn.eclipse.org", Type.HTTP);
//			assertNotNull(proxy);
//			assertEquals(Proxy.Type.HTTP, proxy.type());
//			proxy = WebUtil.getProxy("mylyn.eclipse.org", Type.SOCKS);
//			assertNull(proxy);
//		} finally {
//			WebUtil.setProxyService(defaultProxyService);
//		}
	}
}
