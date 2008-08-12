/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

/**
 * 
 * 
 * @author David Green
 */
public class LocationTrackingReaderTest extends TestCase {

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

	public void testReadLine() throws IOException {
		String content = "\rabc\ndef\r\rfoo bar";
		int bufSize = 3;
		int[] lineOffsets = new int[] { 0, 1, 5, 9, 10, 17 };

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

		assertTrue(refLine == null);
		assertTrue(testLine == null);
	}

	public void testReadLineWithWindowsNewlines() throws IOException {
		String content = "abc\r\ndef\r\n\r\nfoo bar";
		int bufSize = 3;
		int[] lineOffsets = new int[] { 0, 5, 10, 12, 19 };

		doTest(content, lineOffsets, bufSize);
	}

	public void testReadLineWithTerminatingEOLs() throws IOException {
		String content = "abc\n";
		int bufSize = 3;
		int[] lineOffsets = new int[] { 0, 4 };

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
