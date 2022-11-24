/*******************************************************************************
 * Copyright (c) 2011, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.core.net.NetUtil;

/**
 * @author Steffen Pingel
 */
public class NetUtilTest extends TestCase {

	static final int /*NetUtil.*/MAX_HTTP_HOST_CONNECTIONS_DEFAULT = 100;

	static final int /*NetUtil.*/MAX_HTTP_TOTAL_CONNECTIONS_DEFAULT = 1000;

	private static final String /*NetUtil.*/PROPERTY_MAX_HTTP_HOST_CONNECTIONS = "org.eclipse.mylyn.http.connections.per.host";

	private static final String /*NetUtil.*/PROPERTY_MAX_HTTP_TOTAL_CONNECTIONS = "org.eclipse.mylyn.http.total.connections";

	public void testGetHostDefault() {
		String url = "https://example.com/";
		assertEquals("example.com", NetUtil.getHost(url));
	}

	public void testGetHostEmptyPath() {
		String url = "https://example.com";
		assertEquals("example.com", NetUtil.getHost(url));
	}

	public void testGetHostEmptyPathPort() {
		String url = "https://example.com";
		assertEquals("example.com", NetUtil.getHost(url));
	}

	public void testGetHostNestedUrl() {
		String url = "https://example.com:444/folder/file.txt?search=https://example.com:812/folder/file.txt";
		assertEquals("example.com", NetUtil.getHost(url));
	}

	public void testGetHostNestedUrlNoPort() {
		String url = "https://example.com/folder/file.txt?search=https://example.com:812/folder/file.txt";
		assertEquals("example.com", NetUtil.getHost(url));
	}

	public void testGetHostPortNumber() {
		String url = "https://example.com:444/folder/file.txt";
		assertEquals("example.com", NetUtil.getHost(url));
	}

	public void testGetPortDefault() {
		String url = "https://example.com/";
		assertEquals(80, NetUtil.getPort(url));
	}

	public void testGetPortEmptyPath() {
		String url = "https://example.com";
		assertEquals(80, NetUtil.getPort(url));
	}

	public void testGetPortEmptyPathPort() {
		String url = "https://example.com:321";
		assertEquals(321, NetUtil.getPort(url));
	}

	public void testGetPortNestedUrl() {
		String url = "https://example.com:444/folder/file.txt?search=https://example.com:812/folder/file.txt";
		assertEquals(444, NetUtil.getPort(url));
	}

	public void testGetPortNestedUrlDefault() {
		String url = "https://example.com/folder/file.txt?search=https://example.com:812/folder/file.txt";
		assertEquals(443, NetUtil.getPort(url));
	}

	public void testGetPortNoProtocol() {
		String url = "example.com:321";
		assertEquals(321, NetUtil.getPort(url));
	}

	public void testGetPortNoProtocolDefault() {
		String url = "example.com";
		assertEquals(80, NetUtil.getPort(url));
	}

	public void testGetPortNumber() {
		String url = "https://example.com:444/folder/file.txt";
		assertEquals(444, NetUtil.getPort(url));
	}

	public void testGetRequestParameters() {
		String url = "https://jira.codehaus.org/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?&pid=11093&resolution=-1&sorter/field=updated&sorter/order=DESC&tempMax=1000";
		assertEquals(
				"/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?&pid=11093&resolution=-1&sorter/field=updated&sorter/order=DESC&tempMax=1000",
				NetUtil.getRequestPath(url));
	}

	public void testGetRequestPathEmpty() {
		String url = "https://example.com";
		assertEquals("", NetUtil.getRequestPath(url));
	}

	public void testGetRequestPathEmptyPort() {
		String url = "https://example.com:321";
		assertEquals("", NetUtil.getRequestPath(url));
	}

	public void testGetRequestPathNestedUrl() {
		String url = "https://example.com:444/folder/file.txt?search=https://example.com:812/folder/file.txt";
		assertEquals("/folder/file.txt?search=https://example.com:812/folder/file.txt", NetUtil.getRequestPath(url));
	}

	public void testGetRequestPathNestedUrlNoPort() {
		String url = "https://example.com/folder/file.txt?search=https://example.com:812/folder/file.txt";
		assertEquals("/folder/file.txt?search=https://example.com:812/folder/file.txt", NetUtil.getRequestPath(url));
	}

	public void testGetRequestPathNoProtocol() {
		String url = "example.com/folder/file";
		assertEquals("/folder/file", NetUtil.getRequestPath(url));
	}

	public void testGetRequestPathNoProtocolEmtpyPath() {
		String url = "example.com";
		assertEquals("", NetUtil.getRequestPath(url));
	}

	public void testGetRequestPathPortNumber() {
		String url = "https://example.com:444/folder/file.txt";
		assertEquals("/folder/file.txt", NetUtil.getRequestPath(url));
	}

	public void testGetRequestPathSlash() {
		String url = "https://example.com/";
		assertEquals("/", NetUtil.getRequestPath(url));
	}

	public void testIsUrlHttpsValid() {
		assertTrue(NetUtil.isUrlHttps("https://example.com"));
	}

	public void testIsUrlHttpsHttpUrl() {
		assertFalse(NetUtil.isUrlHttps("https://"));
	}

	public void testIsUrlHttpsInvalid() {
		assertFalse(NetUtil.isUrlHttps("abc"));
	}

	public void testIsUrlHttpsNoHost() {
		assertTrue(NetUtil.isUrlHttps("https://"));
	}

	public void testIsUrlHttpsNoUrl() {
		assertTrue(NetUtil.isUrlHttps("httpsabc"));
	}

	public void testIsUrlHttpsTelnets() {
		assertFalse(NetUtil.isUrlHttps("telnets://"));
	}

	public void testGetMaxHttpConnectionsPerHostDefault() throws IOException {
		PrintStream oldErr = System.err;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.setErr(new PrintStream(baos));
		try {
			assertEquals(MAX_HTTP_HOST_CONNECTIONS_DEFAULT, NetUtil.getMaxHttpConnectionsPerHost());
			assertEquals(0, baos.size()); //  no error dumped to console
		} finally {
			baos.close();
			System.setErr(oldErr);
		}
	}

	public void testGetMaxHttpConnectionsDefault() throws IOException {
		PrintStream oldErr = System.err;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.setErr(new PrintStream(baos));
		try {
			assertEquals(MAX_HTTP_TOTAL_CONNECTIONS_DEFAULT, NetUtil.getMaxHttpConnections());
			assertEquals(0, baos.size()); //  no error dumped to console
		} finally {
			baos.close();
			System.setErr(oldErr);
		}
	}

	public void testGetMaxHttpConnectionsPerHost() {
		String oldValue = System.getProperty(PROPERTY_MAX_HTTP_HOST_CONNECTIONS);
		int newValue = 7;
		try {
			System.setProperty(PROPERTY_MAX_HTTP_HOST_CONNECTIONS, Integer.toString(newValue));
			assertEquals(newValue, NetUtil.getMaxHttpConnectionsPerHost());
		} finally {
			resetSystemProperty(PROPERTY_MAX_HTTP_HOST_CONNECTIONS, oldValue);
		}
	}

	public void testGetMaxHttpConnectionsPerHostInvalid() {
		String oldValue = System.getProperty(PROPERTY_MAX_HTTP_HOST_CONNECTIONS);
		try {
			System.setProperty(PROPERTY_MAX_HTTP_HOST_CONNECTIONS, "NaN");
			assertEquals(MAX_HTTP_HOST_CONNECTIONS_DEFAULT, NetUtil.getMaxHttpConnectionsPerHost());
		} finally {
			resetSystemProperty(PROPERTY_MAX_HTTP_HOST_CONNECTIONS, oldValue);
		}
	}

	public void testGetMaxHttpConnections() {
		String oldValue = System.getProperty(PROPERTY_MAX_HTTP_TOTAL_CONNECTIONS);
		int newValue = 7;
		try {
			System.setProperty(PROPERTY_MAX_HTTP_TOTAL_CONNECTIONS, Integer.toString(newValue));
			assertEquals(newValue, NetUtil.getMaxHttpConnections());
		} finally {
			resetSystemProperty(PROPERTY_MAX_HTTP_TOTAL_CONNECTIONS, oldValue);
		}
	}

	public void testGetMaxHttpConnectionsInvalid() {
		String oldValue = System.getProperty(PROPERTY_MAX_HTTP_TOTAL_CONNECTIONS);
		try {
			System.setProperty(PROPERTY_MAX_HTTP_TOTAL_CONNECTIONS, "NaN");
			assertEquals(MAX_HTTP_TOTAL_CONNECTIONS_DEFAULT, NetUtil.getMaxHttpConnections());
		} finally {
			resetSystemProperty(PROPERTY_MAX_HTTP_TOTAL_CONNECTIONS, oldValue);
		}
	}

	private static void resetSystemProperty(String key, String oldValue) {
		if (oldValue == null) {
			System.clearProperty(key);
		} else {
			System.setProperty(key, oldValue);
		}
	}

}
