/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.tasks.core.WebClientUtil;

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
	
		url = "https://example.com:321";
		assertEquals(321, WebClientUtil.getPort(url));
		assertEquals("example.com", WebClientUtil.getDomain(url));
		assertEquals("", WebClientUtil.getRequestPath(url));
		
		url = "example.com:321";
		assertEquals(321, WebClientUtil.getPort(url));
		assertEquals("example.com", WebClientUtil.getDomain(url));
		assertEquals("", WebClientUtil.getRequestPath(url));
	}

}
