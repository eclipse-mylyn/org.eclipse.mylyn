/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.util.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.eclipse.mylyn.wikitext.util.LocationTrackingReader;
import org.junit.Test;

/**
 * @author David Green
 */
@SuppressWarnings("resource")
public class LocationTrackingReaderTest {
	@Test
	public void testCharOffset() throws IOException {
		String content = "aaflkjsdf \nas;dfj asl;fj\r\naslfkjasd";
		int count = 0;
		LocationTrackingReader reader = new LocationTrackingReader(new StringReader(content), 16);
		int c;
		while ((c = reader.read()) != -1) {
			++count;
			assertEquals(content.charAt(count - 1), (char) c);
			assertEquals(count - 1, reader.getOffset());
		}
		assertEquals(content.length(), count);
	}

	@Test
	public void testReadLine() throws IOException {
		String content = "\rabc\ndef\r\rfoo bar";
		int bufSize = 3;
		int[] lineOffsets = { 0, 1, 5, 9, 10, 17 };

		doTest(content, lineOffsets, bufSize);
	}

	private void doTest(String content, int[] lineOffsets, int bufSize) throws IOException {
		LocationTrackingReader reader = new LocationTrackingReader(new StringReader(content), bufSize);
		BufferedReader refReader = new BufferedReader(new StringReader(content));

		int lineNumber = 0;
		String testLine = null;
		String refLine = null;
		do {
			int expectedOffset = lineOffsets[lineNumber++];
			testLine = reader.readLine();
			refLine = refReader.readLine();

			assertEquals(refLine, testLine);
			assertEquals(expectedOffset, reader.getLineOffset());
			assertEquals(lineNumber - 1, reader.getLineNumber());
		} while (testLine != null && refLine != null);

		assertNull(refLine);
		assertNull(testLine);
	}

	@Test
	public void testReadLineWithWindowsNewlines() throws IOException {
		String content = "abc\r\ndef\r\n\r\nfoo bar";
		int bufSize = 3;
		int[] lineOffsets = { 0, 5, 10, 12, 19 };

		doTest(content, lineOffsets, bufSize);
	}

	@Test
	public void testReadLineWithTerminatingEOLs() throws IOException {
		String content = "abc\n";
		int bufSize = 3;
		int[] lineOffsets = { 0, 4 };

		doTest(content, lineOffsets, bufSize);

		content = "abc\r\n";
		bufSize = 3;
		lineOffsets = new int[] { 0, 5 };

		doTest(content, lineOffsets, bufSize);

		content = "abc\r";
		bufSize = 3;
		lineOffsets = new int[] { 0, 4 };

		doTest(content, lineOffsets, bufSize);
	}
}
