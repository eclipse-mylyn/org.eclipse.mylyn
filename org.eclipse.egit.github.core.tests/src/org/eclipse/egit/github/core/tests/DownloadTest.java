/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.egit.github.core.Download;
import org.junit.Test;

/**
 * Unit tests of {@link Download}
 */
public class DownloadTest {

	/**
	 * Test default state of download resource
	 */
	@Test
	public void defaultState() {
		Download dl = new Download();
		assertNull(dl.getDescription());
		assertEquals(0, dl.getDownloadCount());
		assertNull(dl.getHtmlUrl());
		assertEquals(0, dl.getId());
		assertNull(dl.getName());
		assertEquals(0, dl.getSize());
		assertNull(dl.getUrl());
		assertNull(dl.getContentType());
	}

	/**
	 * Test updating download resource fields
	 */
	@Test
	public void updateFields() {
		Download dl = new Download();
		assertEquals("a download", dl.setDescription("a download")
				.getDescription());
		assertEquals(5, dl.setDownloadCount(5).getDownloadCount());
		assertEquals("download.html", dl.setHtmlUrl("download.html")
				.getHtmlUrl());
		assertEquals(58, dl.setId(58).getId());
		assertEquals("download.jar", dl.setName("download.jar").getName());
		assertEquals(12345, dl.setSize(12345).getSize());
		assertEquals("/path/dl", dl.setUrl("/path/dl").getUrl());
		assertEquals("text/plain", dl.setContentType("text/plain").getContentType());
	}

}
