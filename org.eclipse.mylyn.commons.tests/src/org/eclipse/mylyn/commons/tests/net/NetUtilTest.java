/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.net;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.core.net.NetUtil;

/**
 * @author Steffen Pingel
 */
public class NetUtilTest extends TestCase {

	public void testGetHostDefault() {
		String url = "http://example.com/";
		assertEquals("example.com", NetUtil.getHost(url));
	}

	public void testGetHostEmptyPath() {
		String url = "http://example.com";
		assertEquals("example.com", NetUtil.getHost(url));
	}

	public void testGetHostEmptyPathPort() {
		String url = "http://example.com";
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
		String url = "http://example.com/";
		assertEquals(80, NetUtil.getPort(url));
	}

	public void testGetPortEmptyPath() {
		String url = "http://example.com";
		assertEquals(80, NetUtil.getPort(url));
	}

	public void testGetPortEmptyPathPort() {
		String url = "http://example.com:321";
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
		String url = "http://example.com";
		assertEquals("", NetUtil.getRequestPath(url));
	}

	public void testGetRequestPathEmptyPort() {
		String url = "http://example.com:321";
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
		String url = "http://example.com/";
		assertEquals("/", NetUtil.getRequestPath(url));
	}

	public void testIsUrlHttpsValid() {
		assertTrue(NetUtil.isUrlHttps("https://example.com"));
	}

	public void testIUrlHttpsHttpUrl() {
		assertFalse(NetUtil.isUrlHttps("http://"));
	}

	public void testIUrlHttpsInvalid() {
		assertFalse(NetUtil.isUrlHttps("abc"));
	}

	public void testIUrlHttpsNoHost() {
		assertTrue(NetUtil.isUrlHttps("https://"));
	}

	public void testIUrlHttpsNoUrl() {
		assertTrue(NetUtil.isUrlHttps("httpsabc"));
	}

	public void testIUrlHttpsTelnets() {
		assertFalse(NetUtil.isUrlHttps("telnets://"));
	}

}
