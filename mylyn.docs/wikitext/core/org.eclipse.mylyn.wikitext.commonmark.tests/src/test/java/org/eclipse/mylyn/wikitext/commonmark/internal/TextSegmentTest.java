/*******************************************************************************
 * Copyright (c) 2015, 2024 David Green and others.
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

package org.eclipse.mylyn.wikitext.commonmark.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Strings;

@SuppressWarnings("nls")
public class TextSegmentTest {

	@Test(expected = NullPointerException.class)
	public void requiresLines() {
		assertNotNull(new TextSegment(null));
	}

	@Test
	public void getText() {
		assertEquals("", new TextSegment(List.of()).getText());
		assertEquals("one\ntwo", new TextSegment(createLines("one\r\ntwo")).getText());
		assertEquals("one\ntwo\nthree", new TextSegment(createLines("one\r\ntwo\nthree")).getText());
	}

	@Test
	public void offsetOf() {
		TextSegment segment = new TextSegment(createLines("one\r\ntwo\r\nthree four"));
		String text = segment.getText();
		assertEquals(0, segment.offsetOf(text.indexOf("one")));
		assertEquals(5, segment.offsetOf(text.indexOf("two")));
		assertEquals(3, segment.offsetOf(text.indexOf("two") - 1));
		assertEquals(10, segment.offsetOf(text.indexOf("three")));
		assertEquals(8, segment.offsetOf(text.indexOf("three") - 1));
		assertEquals(16, segment.offsetOf(text.indexOf("four")));
	}

	@Test
	public void toTextOffset() {
		TextSegment segment = new TextSegment(List.of(new Line(1, 10, "abc"), new Line(2, 15, "def")));
		assertEquals(0, segment.toTextOffset(10));
		assertEquals(2, segment.toTextOffset(12));
		assertEquals(4, segment.toTextOffset(15));
		assertEquals(6, segment.toTextOffset(17));
		try {
			segment.toTextOffset(19);
			fail("expected exception");
		} catch (IllegalArgumentException e) {
			// expected
		}
		try {
			segment.toTextOffset(9);
			fail("expected exception");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void getLineAtOffset() {
		List<Line> lines = List.of(new Line(1, 10, "abc"), new Line(2, 15, "def"));
		TextSegment segment = new TextSegment(lines);
		assertEquals(lines.get(0), segment.getLineAtOffset(0));
		assertEquals(lines.get(0), segment.getLineAtOffset(3));
		assertEquals(lines.get(1), segment.getLineAtOffset(4));
		assertEquals(lines.get(1), segment.getLineAtOffset(6));
	}

	@Test
	public void toStringTest() {
		assertEquals("TextSegment{text=one\\ntwo\\nthree four}",
				new TextSegment(createLines("one\ntwo\r\nthree four")).toString());
		assertEquals("TextSegment{text=01234567890123456789...}",
				new TextSegment(createLines(Strings.repeat("0123456789", 10))).toString());
	}

	private Iterable<Line> createLines(String content) {
		return LineSequence.create(content);
	}
}
