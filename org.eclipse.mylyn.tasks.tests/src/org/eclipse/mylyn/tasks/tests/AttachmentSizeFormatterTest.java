/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentSizeFormatter;

/**
 * Tests attachment size value format for many situations
 * <p>
 * <strong>This test may fail if run in non-english locales because the decimal point is localized; the workaround is to
 * change your locale to en_US or append the following parameter to program arguments on JUnit plug-in test: "-nl en_US"</strong>
 * 
 * @author Willian Mitsuda
 */
public class AttachmentSizeFormatterTest extends TestCase {

	public void testInvalidString() {
		assertEquals(AttachmentSizeFormatter.UNKNOWN_SIZE, AttachmentSizeFormatter.format(null));
		assertEquals(AttachmentSizeFormatter.UNKNOWN_SIZE, AttachmentSizeFormatter.format("x"));
	}

	public void testNotAValidNumber() {
		assertEquals(AttachmentSizeFormatter.UNKNOWN_SIZE, AttachmentSizeFormatter.format("-5"));
		assertEquals(AttachmentSizeFormatter.UNKNOWN_SIZE, AttachmentSizeFormatter.format("1.0"));
	}

	public void testByteFormatter() {
		assertEquals("1 byte", AttachmentSizeFormatter.format("1"));
		assertEquals("2 bytes", AttachmentSizeFormatter.format("2"));
		assertEquals("999 bytes", AttachmentSizeFormatter.format("999"));
	}

	public void testKBFormatter() {
		assertEquals("1.00 kB", AttachmentSizeFormatter.format("1000"));
		assertEquals("999.99 kB", AttachmentSizeFormatter.format("999994"));
	}

	public void testMBFormatter() {
		assertEquals("1.00 MB", AttachmentSizeFormatter.format("999995"));
		assertEquals("1.50 MB", AttachmentSizeFormatter.format("1500000"));
		assertEquals("999.99 MB", AttachmentSizeFormatter.format("999994444"));
	}

	public void testGBFormatter() {
		assertEquals("1.00 GB", AttachmentSizeFormatter.format("999994445"));
	}

}
