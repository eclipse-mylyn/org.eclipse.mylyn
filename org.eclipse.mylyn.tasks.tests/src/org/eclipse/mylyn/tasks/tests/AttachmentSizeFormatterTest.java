/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.tasks.tests;

import java.util.Locale;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.ui.editors.AttachmentSizeFormatter;

/**
 * Tests attachment size value format for many situations
 * <p>
 * <strong>This test may fail if run in non-english locales because the decimal point is localized; the workaround is to
 * change your locale to en_US or append the following parameter to program arguments on JUnit plug-in test: "-nl en_US"</strong>
 * 
 * @author Willian Mitsuda
 * @author Frank Becker
 */
public class AttachmentSizeFormatterTest extends TestCase {

	public void testInvalidString() {
		AttachmentSizeFormatter formatter = AttachmentSizeFormatter.getInstance();
		assertEquals(AttachmentSizeFormatter.UNKNOWN_SIZE, formatter.format(null));
		assertEquals(AttachmentSizeFormatter.UNKNOWN_SIZE, formatter.format("x"));
	}

	public void testNotAValidNumber() {
		AttachmentSizeFormatter formatter = AttachmentSizeFormatter.getInstance();
		assertEquals(AttachmentSizeFormatter.UNKNOWN_SIZE, formatter.format("-5"));
		assertEquals(AttachmentSizeFormatter.UNKNOWN_SIZE, formatter.format("1.0"));
	}

	public void testByteFormatter() {
		AttachmentSizeFormatter formatter = new AttachmentSizeFormatter(Locale.ENGLISH);
		assertEquals("1 byte", formatter.format("1"));
		assertEquals("2 bytes", formatter.format("2"));
		assertEquals("1023 bytes", formatter.format("1023"));
	}

	public void testKBFormatter() {
		AttachmentSizeFormatter formatterEnglish = new AttachmentSizeFormatter(Locale.ENGLISH);
		assertEquals("1.00 KB", formatterEnglish.format("1024"));
		assertEquals("1024.00 KB", formatterEnglish.format("1048575"));

		AttachmentSizeFormatter formatterGerman = new AttachmentSizeFormatter(Locale.GERMAN);
		assertEquals("1,00 KB", formatterGerman.format("1024"));
		assertEquals("1024,00 KB", formatterGerman.format("1048575"));
	}

	public void testMBFormatter() {
		AttachmentSizeFormatter formatterEnglish = new AttachmentSizeFormatter(Locale.ENGLISH);
		assertEquals("1.00 MB", formatterEnglish.format("1048576"));
		assertEquals("1024.00 MB", formatterEnglish.format("1073741823"));

		AttachmentSizeFormatter formatterGerman = new AttachmentSizeFormatter(Locale.GERMAN);
		assertEquals("1,00 MB", formatterGerman.format("1048576"));
		assertEquals("1024,00 MB", formatterGerman.format("1073741823"));
	}

	public void testGBFormatter() {
		AttachmentSizeFormatter formatterEnglish = new AttachmentSizeFormatter(Locale.ENGLISH);
		assertEquals("1.00 GB", formatterEnglish.format("1073741824"));

		AttachmentSizeFormatter formatterGerman = new AttachmentSizeFormatter(Locale.GERMAN);
		assertEquals("1,00 GB", formatterGerman.format("1073741824"));
	}

}
