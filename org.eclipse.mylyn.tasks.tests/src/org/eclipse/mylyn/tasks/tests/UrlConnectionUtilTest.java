/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.net.InetSocketAddress;
import java.net.Proxy.Type;

import junit.framework.TestCase;

import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.eclipse.mylyn.web.core.AuthenticatedProxy;
import org.eclipse.mylyn.web.core.WebClientUtil;

public class UrlConnectionUtilTest extends TestCase {

	public void testUrlParsers() {
		String url = "https://example.com:444/folder/file.txt";
		assertEquals(444, WebClientUtil.getPort(url));
		assertEquals("example.com", WebClientUtil.getDomain(url));
		assertEquals("/folder/file.txt", WebClientUtil.getRequestPath(url));

		url = "http://example.com/";
		assertEquals(80, WebClientUtil.getPort(url));
		assertEquals("example.com", WebClientUtil.getDomain(url));
		assertEquals("/", WebClientUtil.getRequestPath(url));

		url = "http://example.com";
		assertEquals(80, WebClientUtil.getPort(url));
		assertEquals("example.com", WebClientUtil.getDomain(url));
		assertEquals("", WebClientUtil.getRequestPath(url));

		url = "https://example.com:321";
		assertEquals(321, WebClientUtil.getPort(url));
		assertEquals("example.com", WebClientUtil.getDomain(url));
		assertEquals("", WebClientUtil.getRequestPath(url));

		url = "example.com:321";
		assertEquals(321, WebClientUtil.getPort(url));
		assertEquals("example.com", WebClientUtil.getDomain(url));
		assertEquals("", WebClientUtil.getRequestPath(url));

		url = "https://example.com:444/folder/file.txt?search=https://example.com:812/folder/file.txt";
		assertEquals(444, WebClientUtil.getPort(url));
		assertEquals("example.com", WebClientUtil.getDomain(url));
		assertEquals("/folder/file.txt?search=https://example.com:812/folder/file.txt",
				WebClientUtil.getRequestPath(url));

		url = "https://example.com/folder/file.txt?search=https://example.com:812/folder/file.txt";
		assertEquals(443, WebClientUtil.getPort(url));
		assertEquals("example.com", WebClientUtil.getDomain(url));
		assertEquals("/folder/file.txt?search=https://example.com:812/folder/file.txt",
				WebClientUtil.getRequestPath(url));

		url = "https://jira.codehaus.org/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?&pid=11093&resolution=-1&sorter/field=updated&sorter/order=DESC&tempMax=1000";
		assertEquals(443, WebClientUtil.getPort(url));
		assertEquals("jira.codehaus.org", WebClientUtil.getDomain(url));
		assertEquals(
				"/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?&pid=11093&resolution=-1&sorter/field=updated&sorter/order=DESC&tempMax=1000",
				WebClientUtil.getRequestPath(url));
	}

	public void testCredentials() {
		AuthenticatedProxy proxy = new AuthenticatedProxy(Type.HTTP, new InetSocketAddress(4567), "user", "password");
		UsernamePasswordCredentials credentials = (UsernamePasswordCredentials) WebClientUtil.getCredentials(proxy,
				new InetSocketAddress(1234));
		assertEquals("user", credentials.getUserName());
		assertEquals("password", credentials.getPassword());

		proxy = new AuthenticatedProxy(Type.HTTP, new InetSocketAddress(4567), "domain\\user", "password");
		InetSocketAddress testAddress = new InetSocketAddress("mylyn.eclipse.org", 1234);
		NTCredentials ntCredentials = (NTCredentials) WebClientUtil.getCredentials(proxy, testAddress);
		assertEquals("user", ntCredentials.getUserName());
		assertEquals("password", ntCredentials.getPassword());
		assertEquals("domain", ntCredentials.getDomain());
		assertEquals("mylyn.eclipse.org", ntCredentials.getHost());
	}

}
