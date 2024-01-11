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

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.eclipse.mylyn.wikitext.commonmark.internal.TextSegment;
import org.junit.Test;

public class CursorTest {

	@Test(expected = NullPointerException.class)
	public void requiresTextSegment() {
		assertNotNull(new Cursor(null));
	}

	@Test
	public void empty() {
		Cursor cursor = new Cursor(createTextSegment(""));
		assertFalse(cursor.hasChar());
		assertEquals(0, cursor.getOffset());
		cursor.advance();
		assertFalse(cursor.hasChar());
		assertEquals(0, cursor.getOffset());
	}

	@Test
	public void withContent() {
		Cursor cursor = new Cursor(createTextSegment("1\n23"));
		assertTrue(cursor.hasChar());
		assertEquals(0, cursor.getOffset());
		assertEquals('1', cursor.getChar());
		assertEquals('1', cursor.getChar(0));
		cursor.advance();
		assertTrue(cursor.hasChar());
		assertEquals(1, cursor.getOffset());
		assertEquals('\n', cursor.getChar());
		assertEquals('1', cursor.getChar(0));
		cursor.advance();
		assertTrue(cursor.hasChar());
		assertEquals(2, cursor.getOffset());
		assertEquals('2', cursor.getChar());
		cursor.advance();
		assertTrue(cursor.hasChar());
		assertEquals(3, cursor.getOffset());
		assertEquals('3', cursor.getChar());
		assertEquals('3', cursor.getChar(3));
		cursor.advance();
		assertFalse(cursor.hasChar());
		assertEquals(4, cursor.getOffset());

	}

	@Test
	public void advance() {
		Cursor cursor = new Cursor(createTextSegment("1\n23"));
		cursor.advance(2);
		assertTrue(cursor.hasChar());
		assertEquals(2, cursor.getOffset());
		assertEquals('2', cursor.getChar());
	}

	@Test
	public void rewind() {
		Cursor cursor = new Cursor(createTextSegment("1\n23"));
		cursor.advance(2);
		assertEquals('2', cursor.getChar());
		cursor.rewind();
		assertEquals('\n', cursor.getChar());
		cursor.rewind();
		assertEquals('1', cursor.getChar());
		cursor.advance(3);
		assertEquals('3', cursor.getChar());
		cursor.rewind(3);
		assertEquals('1', cursor.getChar());
	}

	@Test
	public void getTextAtOffset() {
		Cursor cursor = new Cursor(createTextSegment("1\n23"));
		assertEquals("1\n23", cursor.getTextAtOffset());
		assertEquals("1\n2", cursor.getTextAtOffset(3));
		cursor.advance(2);
		assertEquals("23", cursor.getTextAtOffset());
		assertEquals("23", cursor.getTextAtOffset(2));
		assertEquals("2", cursor.getTextAtOffset(1));
	}

	@Test
	public void getNext() {
		Cursor cursor = new Cursor(createTextSegment("123"));
		assertTrue(cursor.hasNext());
		assertTrue(cursor.hasNext(1));
		assertEquals('2', cursor.getNext());
		assertTrue(cursor.hasNext(2));
		assertEquals('3', cursor.getNext(2));
		assertFalse(cursor.hasNext(3));
		cursor.advance();
		assertTrue(cursor.hasNext());
		assertEquals('3', cursor.getNext());
		assertFalse(cursor.hasNext(2));
		cursor.advance();
		assertFalse(cursor.hasNext());
		assertFalse(cursor.hasNext(1));
	}

	@Test
	public void getPrevious() {
		Cursor cursor = new Cursor(createTextSegment("123"));
		assertFalse(cursor.hasPrevious());
		assertFalse(cursor.hasPrevious(2));
		cursor.advance();
		assertTrue(cursor.hasPrevious());
		assertTrue(cursor.hasPrevious(1));
		assertFalse(cursor.hasPrevious(2));
		assertEquals('1', cursor.getPrevious());
		assertEquals('1', cursor.getPrevious(1));
		cursor.advance();
		assertTrue(cursor.hasPrevious());
		assertTrue(cursor.hasPrevious(1));
		assertTrue(cursor.hasPrevious(2));
		assertFalse(cursor.hasPrevious(3));
		assertEquals('2', cursor.getPrevious());
		assertEquals('2', cursor.getPrevious(1));
		assertEquals('1', cursor.getPrevious(2));
		cursor.advance();
		assertEquals('3', cursor.getPrevious());
		cursor.advance();
		assertEquals('3', cursor.getPrevious());
	}

	@Test
	public void matcher() {
		Cursor cursor = new Cursor(createTextSegment("123"));
		Matcher matcher = cursor.matcher(Pattern.compile("123"));
		assertNotNull(matcher);
		assertTrue(matcher.matches());

		matcher = cursor.matcher(2, Pattern.compile("3"));
		assertNotNull(matcher);
		assertTrue(matcher.matches());
	}

	@Test
	public void getOffset() {
		Cursor cursor = new Cursor(createTextSegment("one\r\ntwo"));
		assertEquals(0, cursor.getOffset(0));
		assertEquals(0, cursor.getOffset());
		assertEquals(1, cursor.getOffset(1));
		assertEquals(5, cursor.getOffset(4));
		cursor.advance(4);
		assertEquals(5, cursor.getOffset());

		cursor = new Cursor(new TextSegment(List.of(new Line(1, 10, "abc"))));
		assertEquals(12, cursor.getOffset(2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void toCursorOffset() {
		Cursor cursor = new Cursor(new TextSegment(List.of(new Line(1, 10, "abc"))));
		assertEquals(0, cursor.toCursorOffset(10));
		assertEquals(2, cursor.toCursorOffset(12));
		cursor.toCursorOffset(9);
	}

	@Test
	public void getText() {
		assertGetText("1\n23", "01\n23", 1, 5);
		assertGetText("\n2", "01\n23", 2, 4);
		assertGetText("1\n2", "01\n23", 1, 4);
		assertGetText("1\n2", "01\r\n23", 1, 5);
	}

	private void assertGetText(String expected, String document, int documentStart, int documentEnd) {
		Cursor cursor = new Cursor(createTextSegment(document));
		int start = cursor.toCursorOffset(documentStart);
		int end = cursor.toCursorOffset(documentEnd);
		assertEquals(expected, cursor.getText(start, end));
	}

	private TextSegment createTextSegment(String content) {
		return new TextSegment(LineSequence.create(content).with(x -> true));
	}
}
