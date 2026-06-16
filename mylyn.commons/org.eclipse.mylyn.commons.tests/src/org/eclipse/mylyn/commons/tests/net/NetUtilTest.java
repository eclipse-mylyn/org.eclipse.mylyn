/*******************************************************************************
 * Copyright (c) 2011, 2024 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.eclipse.mylyn.commons.core.net.NetUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.util.SetSystemProperty;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class NetUtilTest {

	private static final String MAX_HTTP_CONNECTIONS_PER_HOST_VALUE = "7";

	static final int /*NetUtil.*/ MAX_HTTP_HOST_CONNECTIONS_DEFAULT = 100;

	static final int /*NetUtil.*/ MAX_HTTP_TOTAL_CONNECTIONS_DEFAULT = 1000;

	private static final String /*NetUtil.*/ PROPERTY_MAX_HTTP_HOST_CONNECTIONS = "org.eclipse.mylyn.http.connections.per.host";

	private static final String /*NetUtil.*/ PROPERTY_MAX_HTTP_TOTAL_CONNECTIONS = "org.eclipse.mylyn.http.total.connections";

	@Test
	public void testGetHostDefault() {
		String url = "http://example.com/";
		assertEquals("example.com", NetUtil.getHost(url));
	}

	@Test
	public void testGetHostEmptyPath() {
		String url = "http://example.com";
		assertEquals("example.com", NetUtil.getHost(url));
	}

	@Test
	public void testGetHostEmptyPathPort() {
		String url = "http://example.com";
		assertEquals("example.com", NetUtil.getHost(url));
	}

	@Test
	public void testGetHostNestedUrl() {
		String url = "https://example.com:444/folder/file.txt?search=https://example.com:812/folder/file.txt";
		assertEquals("example.com", NetUtil.getHost(url));
	}

	@Test
	public void testGetHostNestedUrlNoPort() {
		String url = "https://example.com/folder/file.txt?search=https://example.com:812/folder/file.txt";
		assertEquals("example.com", NetUtil.getHost(url));
	}

	@Test
	public void testGetHostPortNumber() {
		String url = "https://example.com:444/folder/file.txt";
		assertEquals("example.com", NetUtil.getHost(url));
	}

	@Test
	public void testGetPortDefault() {
		String url = "http://example.com/";
		assertEquals(80, NetUtil.getPort(url));
	}

	@Test
	public void testGetPortEmptyPath() {
		String url = "http://example.com";
		assertEquals(80, NetUtil.getPort(url));
	}

	@Test
	public void testGetPortEmptyPathPort() {
		String url = "http://example.com:321";
		assertEquals(321, NetUtil.getPort(url));
	}

	@Test
	public void testGetPortNestedUrl() {
		String url = "https://example.com:444/folder/file.txt?search=https://example.com:812/folder/file.txt";
		assertEquals(444, NetUtil.getPort(url));
	}

	@Test
	public void testGetPortNestedUrlDefault() {
		String url = "https://example.com/folder/file.txt?search=https://example.com:812/folder/file.txt";
		assertEquals(443, NetUtil.getPort(url));
	}

	@Test
	public void testGetPortNoProtocol() {
		String url = "example.com:321";
		assertEquals(321, NetUtil.getPort(url));
	}

	@Test
	public void testGetPortNoProtocolDefault() {
		String url = "example.com";
		assertEquals(80, NetUtil.getPort(url));
	}

	@Test
	public void testGetPortNumber() {
		String url = "https://example.com:444/folder/file.txt";
		assertEquals(444, NetUtil.getPort(url));
	}

	@Test
	public void testGetRequestParameters() {
		String url = "https://jira.codehaus.org/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?&pid=11093&resolution=-1&sorter/field=updated&sorter/order=DESC&tempMax=1000";
		assertEquals(
				"/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?&pid=11093&resolution=-1&sorter/field=updated&sorter/order=DESC&tempMax=1000",
				NetUtil.getRequestPath(url));
	}

	@Test
	public void testGetRequestPathEmpty() {
		String url = "http://example.com";
		assertEquals("", NetUtil.getRequestPath(url));
	}

	@Test
	public void testGetRequestPathEmptyPort() {
		String url = "http://example.com:321";
		assertEquals("", NetUtil.getRequestPath(url));
	}

	@Test
	public void testGetRequestPathNestedUrl() {
		String url = "https://example.com:444/folder/file.txt?search=https://example.com:812/folder/file.txt";
		assertEquals("/folder/file.txt?search=https://example.com:812/folder/file.txt", NetUtil.getRequestPath(url));
	}

	@Test
	public void testGetRequestPathNestedUrlNoPort() {
		String url = "https://example.com/folder/file.txt?search=https://example.com:812/folder/file.txt";
		assertEquals("/folder/file.txt?search=https://example.com:812/folder/file.txt", NetUtil.getRequestPath(url));
	}

	@Test
	public void testGetRequestPathNoProtocol() {
		String url = "example.com/folder/file";
		assertEquals("/folder/file", NetUtil.getRequestPath(url));
	}

	@Test
	public void testGetRequestPathNoProtocolEmtpyPath() {
		String url = "example.com";
		assertEquals("", NetUtil.getRequestPath(url));
	}

	@Test
	public void testGetRequestPathPortNumber() {
		String url = "https://example.com:444/folder/file.txt";
		assertEquals("/folder/file.txt", NetUtil.getRequestPath(url));
	}

	@Test
	public void testGetRequestPathSlash() {
		String url = "http://example.com/";
		assertEquals("/", NetUtil.getRequestPath(url));
	}

	@Test
	public void testIsUrlHttpsValid() {
		assertTrue(NetUtil.isUrlHttps("https://example.com"));
	}

	@Test
	public void testIsUrlHttpsHttpUrl() {
		assertFalse(NetUtil.isUrlHttps("http://"));
	}

	@Test
	public void testIsUrlHttpsInvalid() {
		assertFalse(NetUtil.isUrlHttps("abc"));
	}

	@Test
	public void testIsUrlHttpsNoHost() {
		assertTrue(NetUtil.isUrlHttps("https://"));
	}

	@Test
	public void testIsUrlHttpsNoUrl() {
		assertTrue(NetUtil.isUrlHttps("httpsabc"));
	}

	@Test
	public void testIsUrlHttpsTelnets() {
		assertFalse(NetUtil.isUrlHttps("telnets://"));
	}

	@Test
	public void testGetMaxHttpConnectionsPerHostDefault() throws IOException {
		PrintStream oldErr = System.err;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			System.setErr(new PrintStream(baos));
			try {
				assertEquals(MAX_HTTP_HOST_CONNECTIONS_DEFAULT, NetUtil.getMaxHttpConnectionsPerHost());
				assertEquals(0, baos.size()); //  no error dumped to console
			} finally {
				System.setErr(oldErr);
			}
		}
	}

	@Test
	public void testGetMaxHttpConnectionsDefault() throws IOException {
		PrintStream oldErr = System.err;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			System.setErr(new PrintStream(baos));
			try {
				assertEquals(MAX_HTTP_TOTAL_CONNECTIONS_DEFAULT, NetUtil.getMaxHttpConnections());
				assertEquals(0, baos.size()); //  no error dumped to console
			} finally {
				System.setErr(oldErr);
			}
		}
	}

	@Test
	@SetSystemProperty(key = PROPERTY_MAX_HTTP_HOST_CONNECTIONS, value = MAX_HTTP_CONNECTIONS_PER_HOST_VALUE)
	public void testGetMaxHttpConnectionsPerHost() {
		assertEquals(Integer.parseInt(MAX_HTTP_CONNECTIONS_PER_HOST_VALUE), NetUtil.getMaxHttpConnectionsPerHost());
	}

	@Test
	@SetSystemProperty(key = PROPERTY_MAX_HTTP_TOTAL_CONNECTIONS, value = "NaN")
	public void testGetMaxHttpConnectionsPerHostInvalid() {
		assertEquals(MAX_HTTP_HOST_CONNECTIONS_DEFAULT, NetUtil.getMaxHttpConnectionsPerHost());
	}

	@Test
	@SetSystemProperty(key = PROPERTY_MAX_HTTP_TOTAL_CONNECTIONS, value = MAX_HTTP_CONNECTIONS_PER_HOST_VALUE)
	public void testGetMaxHttpConnections() {
		assertEquals(Integer.parseInt(MAX_HTTP_CONNECTIONS_PER_HOST_VALUE), NetUtil.getMaxHttpConnections());
	}

	@Test
	@SetSystemProperty(key = PROPERTY_MAX_HTTP_TOTAL_CONNECTIONS, value = "NaN")
	public void testGetMaxHttpConnectionsInvalid() {
		assertEquals(MAX_HTTP_TOTAL_CONNECTIONS_DEFAULT, NetUtil.getMaxHttpConnections());
	}

}
