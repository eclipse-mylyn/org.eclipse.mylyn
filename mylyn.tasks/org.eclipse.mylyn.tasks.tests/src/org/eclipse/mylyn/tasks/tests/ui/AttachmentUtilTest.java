/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.junit.jupiter.api.Test;

/**
 * @author Steffen Pingel
 * @author Frank Becker
 */
@SuppressWarnings("nls")
public class AttachmentUtilTest {

	TaskAttachment attachment = TaskTestUtil.createMockTaskAttachment("id");

	@Test
	public void testGetAttachmentFilenameNull() {
		try {
			AttachmentUtil.getAttachmentFilename(null);
			fail("Expected AssertionFailedException");
		} catch (AssertionFailedException expected) {

		}
	}

	@Test
	public void testGetAttachmentFilename() {
		attachment.setFileName("file.bmp");
		assertEquals("file.bmp", AttachmentUtil.getAttachmentFilename(attachment));
	}

	@Test
	public void testGetAttachmentFilenameNoExtension() {
		attachment.setFileName("file");
		assertEquals("file", AttachmentUtil.getAttachmentFilename(attachment));
	}

	@Test
	public void testGetAttachmentFilenameEmpty() {
		attachment.setFileName("");
		assertEquals("attachment", AttachmentUtil.getAttachmentFilename(attachment));
	}

	@Test
	public void testGetAttachmentFilenameEmptyContentTypeHtml() {
		attachment.setFileName("");
		attachment.setContentType("html");
		assertEquals("attachment.html", AttachmentUtil.getAttachmentFilename(attachment));
	}

	@Test
	public void testGetAttachmentFilenameEmptyContentTypeText() {
		attachment.setFileName("");
		attachment.setContentType("text");
		assertEquals("attachment.txt", AttachmentUtil.getAttachmentFilename(attachment));
	}

	@Test
	public void testGetAttachmentFilenameEmptyContentTypeZip() {
		attachment.setFileName("");
		attachment.setContentType("zip");
		assertEquals("attachment.zip", AttachmentUtil.getAttachmentFilename(attachment));
	}

	@Test
	public void testGetAttachmentFilenameEmptyContentTypeOctetStream() {
		attachment.setFileName("");
		attachment.setContentType("octet-stream");
		assertEquals("attachment", AttachmentUtil.getAttachmentFilename(attachment));
	}

	@Test
	public void testGetAttachmentFilenameEmptyContentTypeApplicationOctetStream() {
		attachment.setFileName("");
		attachment.setContentType("application/octet-stream");
		assertEquals("attachment.octet-stream", AttachmentUtil.getAttachmentFilename(attachment));
	}

	@Test
	public void testGetAttachmentFilenameEmptyContentTypeImagePng() {
		attachment.setFileName("");
		attachment.setContentType("image/png");
		assertEquals("attachment.png", AttachmentUtil.getAttachmentFilename(attachment));
	}

	@Test
	public void testGetAttachmentFilenameEmptyContentTypeImageJpeg() {
		attachment.setFileName("");
		attachment.setContentType("image/jpeg");
		assertEquals("attachment.jpeg", AttachmentUtil.getAttachmentFilename(attachment));
	}

	@Test
	public void testGetAttachmentFilenameEmptyContentTypeImageGif() {
		attachment.setFileName("");
		attachment.setContentType("image/gif");
		assertEquals("attachment.gif", AttachmentUtil.getAttachmentFilename(attachment));
	}

	@Test
	public void testGetAttachmentFilenameEmptyContentTypeTextPlain() {
		attachment.setFileName("");
		attachment.setContentType("text/plain");
		assertEquals("attachment.txt", AttachmentUtil.getAttachmentFilename(attachment));
	}

	@Test
	public void testGetAttachmentFilenameEmptyContentTypeTextHtml() {
		attachment.setFileName("");
		attachment.setContentType("text/html");
		assertEquals("attachment.html", AttachmentUtil.getAttachmentFilename(attachment));
	}

	@Test
	public void testGetAttachmentFilenameEmptyContentTypeApplicationXml() {
		attachment.setFileName("");
		attachment.setContentType("application/xml");
		assertEquals("attachment.xml", AttachmentUtil.getAttachmentFilename(attachment));
	}

	@Test
	public void testGetAttachmentFilenameTestHtmlContentTypeOctetStream() {
		attachment.setFileName("Test.html");
		attachment.setContentType("octet-stream");
		assertEquals("Test.html", AttachmentUtil.getAttachmentFilename(attachment));
	}

	@Test
	public void testGetAttachmentIlegalFilename() {
		attachment.setFileName("Ilegal:File:Name");
		attachment.setContentType("octet-stream");
		assertEquals("Ilegal%3AFile%3AName", AttachmentUtil.getAttachmentFilename(attachment));
	}

}
