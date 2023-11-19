/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.wikitext.parser.Locator;
import org.junit.Test;

import com.google.common.base.Strings;

public class LineTest {

	@Test(expected = NullPointerException.class)
	public void requiresText() {
		assertNotNull(new Line(0, 0, null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void requiresNonNegativeLineOffset() {
		assertNotNull(new Line(1, -1, "test"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void requiresNonNegativeLineNumber() {
		assertNotNull(new Line(-1, 1, "test"));
	}

	@Test
	public void isEmpty() {
		assertEmpty("");
		assertEmpty("\t");
		assertEmpty("   ");
		assertNotEmpty("a");
		assertNotEmpty(" a");
		assertNotEmpty("a ");
	}

	@Test
	public void getText() {
		assertEquals("abc", new Line(1, 0, "abc").getText());
	}

	@Test
	public void getLineNumber() {
		assertEquals(0, new Line(0, 1, "abc").getLineNumber());
		assertEquals(1, new Line(1, 1, "abc").getLineNumber());
	}

	@Test
	public void getOffset() {
		assertEquals(0, new Line(1, 0, "abc").getOffset());
		assertEquals(1, new Line(1, 1, "abc").getOffset());
	}

	@Test
	public void toStringTest() {
		assertEquals("Line{lineNumber=1, offset=15, text=1}", new Line(1, 15, "1").toString());
		assertEquals("Line{lineNumber=2, offset=0, text=\\t\\r\\nabc}", new Line(2, 0, "\t\r\nabc").toString());
		assertEquals("Line{lineNumber=0, offset=0, text=aaaaaaaaaaaaaaaaaaaa...}",
				new Line(0, 0, Strings.repeat("a", 100)).toString());
	}

	@Test
	public void segment() {
		Line segment = new Line(2, 15, "0123456789").segment(3, 5);
		assertNotNull(segment);
		assertEquals(2, segment.getLineNumber());
		assertEquals(15 + 3, segment.getOffset());
		assertEquals("34567", segment.getText());
	}

	@Test
	public void toLocator() {
		Line line = new Line(2, 15, "0123456789");
		Locator locator = line.toLocator();
		assertNotNull(locator);
		assertEquals(3, locator.getLineNumber());
		assertEquals(15, locator.getLineDocumentOffset());
	}

	private void assertNotEmpty(String string) {
		assertFalse(string, new Line(0, 0, string).isEmpty());
	}

	private void assertEmpty(String string) {
		assertTrue(string, new Line(0, 0, string).isEmpty());
	}
}
