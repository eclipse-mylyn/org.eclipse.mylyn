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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.eclipse.egit.github.core.DownloadResource;
import org.junit.Test;

/**
 * Unit tests of {@link DownloadResource}
 */
public class DownloadResourceTest {

	/**
	 * Test default state of download resource
	 */
	@Test
	public void defaultState() {
		DownloadResource resource = new DownloadResource();
		assertFalse(resource.isRedirect());
		assertNull(resource.getAccesskeyid());
		assertNull(resource.getAcl());
		assertNull(resource.getDescription());
		assertEquals(0, resource.getDownloadCount());
		assertNull(resource.getExpirationdate());
		assertNull(resource.getHtmlUrl());
		assertEquals(0, resource.getId());
		assertNull(resource.getMimeType());
		assertNull(resource.getName());
		assertNull(resource.getPath());
		assertNull(resource.getPolicy());
		assertNull(resource.getPrefix());
		assertNull(resource.getS3Url());
		assertNull(resource.getSignature());
		assertEquals(0, resource.getSize());
		assertNull(resource.getUrl());
	}

	/**
	 * Test updating download resource fields
	 */
	@Test
	public void updateFields() {
		DownloadResource resource = new DownloadResource();
		assertEquals("abc", resource.setAccesskeyid("abc").getAccesskeyid());
		assertEquals("group", resource.setAcl("group").getAcl());
		assertEquals("a download", resource.setDescription("a download")
				.getDescription());
		assertEquals(5, resource.setDownloadCount(5).getDownloadCount());
		assertEquals(new Date(2500), resource.setExpirationdate(new Date(2500))
				.getExpirationdate());
		assertEquals("download.html", resource.setHtmlUrl("download.html")
				.getHtmlUrl());
		assertEquals(58, resource.setId(58).getId());
		assertEquals("text/plain", resource.setMimeType("text/plain")
				.getMimeType());
		assertEquals("download.jar", resource.setName("download.jar").getName());
		assertEquals("/a/b", resource.setPath("/a/b").getPath());
		assertEquals("ro", resource.setPolicy("ro").getPolicy());
		assertEquals("s3", resource.setPrefix("s3").getPrefix());
		assertEquals("/s/3", resource.setS3Url("/s/3").getS3Url());
		assertEquals("1a2b", resource.setSignature("1a2b").getSignature());
		assertEquals(12345, resource.setSize(12345).getSize());
		assertEquals("/path/dl", resource.setUrl("/path/dl").getUrl());
		assertTrue(resource.setRedirect(true).isRedirect());
	}
}
