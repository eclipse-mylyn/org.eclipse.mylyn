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
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.ui;

import junit.framework.TestCase;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.mylyn.internal.tasks.core.TaskAttachment;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;

/**
 * @author Steffen Pingel
 * @author Frank Becker
 */
public class AttachmentUtilTest extends TestCase {

	TaskAttachment attachment = TaskTestUtil.createMockTaskAttachment("id");

	public void testGetAttachmentFilenameNull() {
		try {
			AttachmentUtil.getAttachmentFilename(null);
			fail("Expected AssertionFailedException");
		} catch (AssertionFailedException expected) {

		}
	}

	public void testGetAttachmentFilename() {
		attachment.setFileName("file.bmp");
		assertEquals("file.bmp", AttachmentUtil.getAttachmentFilename(attachment));
	}

	public void testGetAttachmentFilenameNoExtension() {
		attachment.setFileName("file");
		assertEquals("file", AttachmentUtil.getAttachmentFilename(attachment));
	}

	public void testGetAttachmentFilenameEmpty() {
		attachment.setFileName("");
		assertEquals("attachment", AttachmentUtil.getAttachmentFilename(attachment));
	}

	public void testGetAttachmentFilenameEmptyContentTypeHtml() {
		attachment.setFileName("");
		attachment.setContentType("html");
		assertEquals("attachment.html", AttachmentUtil.getAttachmentFilename(attachment));
	}

	public void testGetAttachmentFilenameEmptyContentTypeText() {
		attachment.setFileName("");
		attachment.setContentType("text");
		assertEquals("attachment.txt", AttachmentUtil.getAttachmentFilename(attachment));
	}

	public void testGetAttachmentFilenameEmptyContentTypeZip() {
		attachment.setFileName("");
		attachment.setContentType("zip");
		assertEquals("attachment.zip", AttachmentUtil.getAttachmentFilename(attachment));
	}

	public void testGetAttachmentFilenameEmptyContentTypeOctetStream() {
		attachment.setFileName("");
		attachment.setContentType("octet-stream");
		assertEquals("attachment", AttachmentUtil.getAttachmentFilename(attachment));
	}

	public void testGetAttachmentFilenameEmptyContentTypeApplicationOctetStream() {
		attachment.setFileName("");
		attachment.setContentType("application/octet-stream");
		assertEquals("attachment.octet-stream", AttachmentUtil.getAttachmentFilename(attachment));
	}

	public void testGetAttachmentFilenameEmptyContentTypeImagePng() {
		attachment.setFileName("");
		attachment.setContentType("image/png");
		assertEquals("attachment.png", AttachmentUtil.getAttachmentFilename(attachment));
	}

	public void testGetAttachmentFilenameEmptyContentTypeImageJpeg() {
		attachment.setFileName("");
		attachment.setContentType("image/jpeg");
		assertEquals("attachment.jpeg", AttachmentUtil.getAttachmentFilename(attachment));
	}

	public void testGetAttachmentFilenameEmptyContentTypeImageGif() {
		attachment.setFileName("");
		attachment.setContentType("image/gif");
		assertEquals("attachment.gif", AttachmentUtil.getAttachmentFilename(attachment));
	}

	public void testGetAttachmentFilenameEmptyContentTypeTextPlain() {
		attachment.setFileName("");
		attachment.setContentType("text/plain");
		assertEquals("attachment.txt", AttachmentUtil.getAttachmentFilename(attachment));
	}

	public void testGetAttachmentFilenameEmptyContentTypeTextHtml() {
		attachment.setFileName("");
		attachment.setContentType("text/html");
		assertEquals("attachment.html", AttachmentUtil.getAttachmentFilename(attachment));
	}

	public void testGetAttachmentFilenameEmptyContentTypeApplicationXml() {
		attachment.setFileName("");
		attachment.setContentType("application/xml");
		assertEquals("attachment.xml", AttachmentUtil.getAttachmentFilename(attachment));
	}

	public void testGetAttachmentFilenameTestHtmlContentTypeOctetStream() {
		attachment.setFileName("Test.html");
		attachment.setContentType("octet-stream");
		assertEquals("Test.html", AttachmentUtil.getAttachmentFilename(attachment));
	}

	public void testGetAttachmentIlegalFilename() {
		attachment.setFileName("Ilegal:File:Name");
		attachment.setContentType("octet-stream");
		assertEquals("Ilegal%3AFile%3AName", AttachmentUtil.getAttachmentFilename(attachment));
	}

}
