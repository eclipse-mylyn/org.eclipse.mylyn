/*******************************************************************************
 * Copyright (c) 2026 George Lindholm
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.mylyn.internal.bugzilla.core.SaxMultiBugReportContentHandler;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
public class BugzillaAttachmentNameTest {
	@Test
	public void testUnicodeAttachmentName() {
		String mojibakedFilename = "test-Ã§Ã±Â¥â¬Â£Â½Â¼Î²Î¸å°åãã¡ï½³ã.txt";
		String encodedName = SaxMultiBugReportContentHandler
				.recoverMojibakeFilename(mojibakedFilename);
		assertEquals("test-çñ¥€£½¼βθ台北ゖァｳ゗.txt", encodedName);
	}

	@Test
	public void testLatin1AttachmentName() {
		String filename = "test-screenshot.txt";
		String encodedName = SaxMultiBugReportContentHandler.recoverMojibakeFilename(filename);
		assertEquals(filename, encodedName);
	}

	@Test
	public void testNonMojibakedLatin1AttachmentName() {
		// A filename with genuine Latin-1 characters (not Mojibake): é (U+00E9) encodes as
		// 0xE9 in Latin-1, which is an invalid UTF-8 start byte — conversion fails and the
		// original is returned unchanged.
		String original = "résumé.txt";
		String result = SaxMultiBugReportContentHandler.recoverMojibakeFilename(original);
		assertEquals(original, result);
	}

}
