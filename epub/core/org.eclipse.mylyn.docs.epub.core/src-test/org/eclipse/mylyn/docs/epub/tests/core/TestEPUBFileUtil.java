/*******************************************************************************
 * Copyright (c) 2014, 2017 Torkild U. Resheim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.docs.epub.tests.core;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.eclipse.mylyn.docs.epub.internal.EPUBFileUtil;
import org.junit.Test;

@SuppressWarnings({ "nls" })
public class TestEPUBFileUtil {

	/*
	 * A list of core media types is specified in
	 * http://idpf.org/epub/20/spec/OPS_2.0.1_draft.htm#Section1.3.7
	 *
	 * Currently the following types cannot be detected:
	 *
	 * "text/x-oeb1-document"
	 * "text/x-oeb1-css"
	 * "application/x-dtbncx+xml"
	 * "application/x-dtbook+xml"
	 */

	@Test
	public void testGetMimeTypeGIF() {
		File file = new File("testdata/content-detection/image.gif");
		assertEquals("image/gif", EPUBFileUtil.getMimeType(file));
	}

	@Test
	public void testGetMimeTypeJPEG() {
		File file = new File("testdata/content-detection/picture.jpeg");
		assertEquals("image/jpeg", EPUBFileUtil.getMimeType(file));
		// Should work even when the file name cannot be used
		File file2 = new File("testdata/content-detection/picture.xxx");
		assertEquals("image/jpeg", EPUBFileUtil.getMimeType(file2));
	}

	@Test
	public void testGetMimeTypePNG() {
		File file = new File("testdata/content-detection/image.png");
		assertEquals("image/png", EPUBFileUtil.getMimeType(file));
	}

	@Test
	public void testGetMimeTypeSVG() {
		File file = new File("testdata/content-detection/drawing.svg");
		assertEquals("image/svg+xml", EPUBFileUtil.getMimeType(file));
	}

	@Test
	public void testGetMimeTypeXHTML() {
		File file = new File("testdata/content-detection/content.xhtml");
		assertEquals("application/xhtml+xml", EPUBFileUtil.getMimeType(file));
		// Should work even when the file name cannot be used
		File file2 = new File("testdata/content-detection/content.xxx");
		assertEquals("application/xhtml+xml", EPUBFileUtil.getMimeType(file2));
		// Handle situations where we have file name that indicates we have
		// plain HTML, but the contents say XML. Hence we are probably
		// looking at XHTML (see bug 360701).
		File file3 = new File("testdata/content-detection/content.html");
		assertEquals("application/xhtml+xml", EPUBFileUtil.getMimeType(file3));
	}

	@Test
	public void testGetMimeTypeXML() {
		File file = new File("testdata/content-detection/xml.xml");
		assertEquals("application/xml", EPUBFileUtil.getMimeType(file));
	}

	@Test
	public void testGetMimeTypeCSS() {
		File file = new File("testdata/content-detection/style.css");
		assertEquals("text/css", EPUBFileUtil.getMimeType(file));
	}

	@Test
	public void testGetMimeTypeEPUB() {
		File file = new File("testdata/content-detection/basic_2.epub");
		assertEquals("application/epub+zip", EPUBFileUtil.getMimeType(file));
	}
}
