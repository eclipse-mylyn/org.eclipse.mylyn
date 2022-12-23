/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.discovery.tests.core.util;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.discovery.core.util.WebUtil;

public class WebUtilTest extends TestCase {

	public void testGetFileNameForJar() throws Exception {
		assertEquals("org.eclipse.mylyn.discovery_3.10.jar",
				WebUtil.getFileNameFor("org.eclipse.mylyn.discovery-3.10.jar"));
	}

	public void testGetFileNameForUrl() throws Exception {
		assertEquals(
				"org.eclipse.mylyn.discovery_3.10.jar",
				WebUtil.getFileNameFor("http://www.eclipse.org/downloads/download.php?file=/mylyn/discovery/org.eclipse.mylyn.discovery-3.10.jar"));
	}

	public void testGetFileNameForUrlWithQuery() throws Exception {
		assertEquals(
				"org.eclipse.mylyn.discovery_3.10.jar_r_1_protocol_http",
				WebUtil.getFileNameFor("http://www.eclipse.org/downloads/download.php?file=/mylyn/discovery/org.eclipse.mylyn.discovery-3.10.jar&r=1&protocol=http"));
	}

	public void testGetFileNameForUrlEndingWithSlash() throws Exception {
		assertEquals("a.jar", WebUtil.getFileNameFor("a.jar/"));
	}

	public void testGetFileNameForUrlWithFilesystemReservedCharacters() throws Exception {
		assertEquals("1_2_3_4_5_6_7_8_9_", WebUtil.getFileNameFor("1<2>3:4\"5\\6|7?8*9+"));
	}
}
