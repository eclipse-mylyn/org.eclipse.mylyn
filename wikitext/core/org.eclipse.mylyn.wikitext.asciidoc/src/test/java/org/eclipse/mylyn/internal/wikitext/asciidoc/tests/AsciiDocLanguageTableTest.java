/*******************************************************************************
 * Copyright (c) 2016 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.toolkit.RecordingDocumentBuilder.Event;
import org.junit.Test;

/**
 * Tests for the AsciiDoc TableBlock elements.
 */
public class AsciiDocLanguageTableTest extends AsciiDocLanguageTestBase {

	@Test
	public void testSimpleTable() {
		String html = parseToHtml("" //
				+ "|===\n" //
				+ "\n" //
				+ "| first | second | third\n" //
				+ "\n" //
				+ "| lorem | ipsum | dolor\n" //
				+ "\n" //
				+ "|===\n" //
				+ "Some Text");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>lorem</td>" //
				+ "<td>ipsum</td>" //
				+ "<td>dolor</td>" //
				+ "</tr>" //
				+ "</table>" //
				+ "<p>Some Text</p>\n", html);
	}

	@Test
	public void testSimpleTableOneCol() {
		String html = parseToHtml("" //
				+ "|===\n" //
				+ "\n" //
				+ "| first\n" //
				+ "\n" //
				+ "| lorem\n" //
				+ "\n" //
				+ "|===\n" //
				+ "Some Text");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>lorem</td>" //
				+ "</tr>" //
				+ "</table>" //
				+ "<p>Some Text</p>\n", html);
	}

	@Test
	public void testJustOpenedTable() {
		String html = parseToHtml("" //
				+ "|===\n");
		assertEquals("<table>" //
				+ "</table>", html);
	}

	@Test
	public void testTableWithContentAdjacentToThePipe() {
		String html = parseToHtml("" //
				+ "|===   \n" //
				+ "\n" //
				+ "|lorem|ipsum\n" //
				+ "\n" //
				+ "|===\n");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>lorem</td>" //
				+ "<td>ipsum</td>" //
				+ "</tr>" //
				+ "</table>", html);
	}

	@Test
	public void testTableWithLeadingAndTrailingSpaces() {
		String html = parseToHtml("" //
				+ "|===\n" //
				+ "\n" //
				+ "| lorem         |     _ipsum_     \n" //
				+ "\n" //
				+ "|===   \n");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>lorem</td>" //
				+ "<td><em>ipsum</em></td>" //
				+ "</tr>" //
				+ "</table>", html);
	}

	@Test
	public void testTableWithEscapedPipe() {
		String html = parseToHtml("" //
				+ "|===\n" //
				+ "\n" //
				+ "| lorem \\| ipsum | other\n" //
				+ "\n" //
				+ "|===\n");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>lorem | ipsum</td>" //
				+ "<td>other</td>" //
				+ "</tr>" //
				+ "</table>", html);
	}

	@Test
	public void testTableFirstRowDefinesNumberOfColumns() {
		String html = parseToHtml("" //
				+ "|===\n" //
				+ "\n" //
				+ "| lorem | ipsum\n" //
				+ "\n" //
				+ "| first\n" //
				+ "| second\n" //
				+ "\n" //
				+ "| alice\n" //
				+ "| bob\n" //
				+ "|===\n");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>lorem</td>" //
				+ "<td>ipsum</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>alice</td>" //
				+ "<td>bob</td>" //
				+ "</tr>" //
				+ "</table>", html);
	}

	@Test
	public void testTableEmptyCellInFirstRow() {
		String html = parseToHtml("" //
				+ "|===\n" //
				+ "\n" //
				+ "| lorem | | ipsum\n" //
				+ "\n" //
				+ "| one | two | three\n" //
				+ "\n" //
				+ "|===\n");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>lorem</td>" //
				+ "<td></td>" //
				+ "<td>ipsum</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>one</td>" //
				+ "<td>two</td>" //
				+ "<td>three</td>" //
				+ "</tr>" //
				+ "</table>", html);
	}

	@Test
	public void testTableEmptyCellInSecondRow() {
		String html = parseToHtml("" //
				+ "|===\n" //
				+ "|one|two|three\n" //
				+ "|lorem||ipsum\n" //
				+ "|===\n");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>one</td>" //
				+ "<td>two</td>" //
				+ "<td>three</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>lorem</td>" //
				+ "<td></td>" //
				+ "<td>ipsum</td>" //
				+ "</tr>" //
				+ "</table>", html);
	}

	@Test
	public void testTableColsAttribute() {
		String html = parseToHtml("" //
				+ "[cols=\"3*\"]\n" //
				+ "|===\n" //
				+ "\n" //
				+ "| first\n"//
				+ "| second\n"//
				+ "| third\n" //
				+ "\n" //
				+ "| lorem\n"//
				+ "| ipsum\n"//
				+ "| dolor\n" //
				+ "\n" //
				+ "|===\n" //
				+ "Some Text");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>lorem</td>" //
				+ "<td>ipsum</td>" //
				+ "<td>dolor</td>" //
				+ "</tr>" //
				+ "</table>" //
				+ "<p>Some Text</p>\n", html);
	}

	@Test
	public void testTableColsAttributeCellMissingInLastRow() {
		String html = parseToHtml("" //
				+ "[cols=\"2*\"]\n" //
				+ "|===\n" //
				+ "\n" //
				+ "| first\n"//
				+ "| second\n"//
				+ "\n" //
				+ "| lorem\n"//
				+ "\n" //
				+ "|===\n" //
				+ "Some Text");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>lorem</td>" //
				+ "</tr>" //
				+ "</table>" //
				+ "<p>Some Text</p>\n", html);
	}

	@Test
	public void testTableColsAttributeAndAlignment() {
		String html = parseToHtml("" //
				+ "[cols=\"<.^,>.>\"]\n" //
				+ "|===\n" //
				+ "| first\n"//
				+ "| second\n"//
				+ "| lorem\n"//
				+ "| ipsum\n"//
				+ "|===\n" //
				+ "Some Text");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td align=\"left\" valign=\"middle\">first</td>" //
				+ "<td align=\"right\" valign=\"bottom\">second</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td align=\"left\" valign=\"middle\">lorem</td>" //
				+ "<td align=\"right\" valign=\"bottom\">ipsum</td>" //
				+ "</tr>" //
				+ "</table>" //
				+ "<p>Some Text</p>\n", html);
	}

	@Test
	public void testColsTableEmptyCellInFirstRow() {
		String html = parseToHtml("" //
				+ "[cols=\"3*\"]\n" //
				+ "|===\n" //
				+ "\n" //
				+ "| lorem \n" //
				+ "| \n" //
				+ "| ipsum\n" //
				+ "\n" //
				+ "| one \n" //
				+ "| two \n" //
				+ "| three\n" //
				+ "\n" //
				+ "|===\n");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>lorem</td>" //
				+ "<td></td>" //
				+ "<td>ipsum</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>one</td>" //
				+ "<td>two</td>" //
				+ "<td>three</td>" //
				+ "</tr>" //
				+ "</table>", html);
	}

	@Test
	public void testColsTableEmptyCellInSecondRow() {
		String html = parseToHtml("" //
				+ "[cols=\"3*\"]\n" //
				+ "|===\n" //
				+ "|one\n" //
				+ "|two\n" //
				+ "|three\n" //
				+ "|lorem\n" //
				+ "|\n" //
				+ "|ipsum\n" //
				+ "|===\n");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>one</td>" //
				+ "<td>two</td>" //
				+ "<td>three</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>lorem</td>" //
				+ "<td></td>" //
				+ "<td>ipsum</td>" //
				+ "</tr>" //
				+ "</table>", html);
	}

	@Test
	public void testColsTableTooManyCellsInSecondRow() {
		String html = parseToHtml("" //
				+ "[cols=\"1,1\"]\n" //
				+ "|===\n" //
				+ "|one|two\n" //
				+ "|three|four|five|six\n" //
				+ "|===\n");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>one</td>" //
				+ "<td>two</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>three</td>" //
				+ "<td>four</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>five</td>" //
				+ "<td>six</td>" //
				+ "</tr>" //
				+ "</table>", html);
	}

	@Test
	public void testMultilineCell() {
		String html = parseToHtml("" //
				+ "[cols=\"2*\"]\n" //
				+ "|===\n" //
				+ "|aaa\n" //
				+ "bbb\n" //
				+ "|xxx\n" //
				+ "| first | second\n" //
				+ "|===\n");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>aaa bbb</td>" //
				+ "<td>xxx</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "</tr>" //
				+ "</table>", html);
	}

	@Test
	public void testMultilineCellOneCol() {
		String html = parseToHtml("" //
				+ "|===\n" //
				+ "| aaa\n" //
				+ "bbb\n" //
				+ "ccc\n" //
				+ "| xxx\n" //
				+ "yyy\n" //
				+ "| 000\n" //
				+ "|===\n");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>aaa bbb ccc</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>xxx yyy</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>000</td>" //
				+ "</tr>" //
				+ "</table>", html);
	}

	@Test
	public void testMultilineCellTwoCols() {
		String html = parseToHtml("" //
				+ "|===\n" //
				+ "| aaa\n" //
				+ "bbb | xxx\n" //
				+ "| 000\n" //
				+ "| 111\n" //
				+ "|===\n");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>aaa bbb</td>" //
				+ "<td>xxx</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>000</td>" //
				+ "<td>111</td>" //
				+ "</tr>" //
				+ "</table>", html);
	}

	@Test
	public void testBasicTableAttributes() {
		String html = parseToHtml("[width=\"80%\",options=\"header\"]\n" //
				+ "|===\n" //
				+ "|one|two|three\n" //
				+ "|four|five|six\n" //
				+ "|===");
		assertEquals("<table width=\"80%\">" //
				+ "<tr><th>one</th><th>two</th><th>three</th></tr>" //
				+ "<tr><td>four</td><td>five</td><td>six</td></tr>" //
				+ "</table>", html);
	}

	@Test
	public void testTableAttributesWithCols() {
		String html = parseToHtml("[width=\"70%\",options=\"header\",cols=\"2*\"]\n" //
				+ "|===\n" //
				+ "|one\n" //
				+ "|two\n" //
				+ "|three\n" //
				+ "|four\n" //
				+ "|===");
		assertEquals("<table width=\"70%\">" //
				+ "<tr><th>one</th><th>two</th></tr>" //
				+ "<tr><td>three</td><td>four</td></tr>" //
				+ "</table>", html);
	}

	@Test
	public void testTableCSV() {
		String html = parseToHtml("" //
				+ "[format=\"csv\", options=\"header\"]\n" //
				+ "|===\n" //
				+ "first,second,third\n" //
				+ "lorem,ipsum,dolor\n" //
				+ "|===\n" //
				+ "Some Text");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<th>first</th>" //
				+ "<th>second</th>" //
				+ "<th>third</th>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>lorem</td>" //
				+ "<td>ipsum</td>" //
				+ "<td>dolor</td>" //
				+ "</tr>" //
				+ "</table>" //
				+ "<p>Some Text</p>\n", html);
	}

	@Test
	public void testTableCSVShorthandNotation() {
		String html = parseToHtml("" //
				+ ",===\n" //
				+ "first,second\n" //
				+ "lorem,ipsum\n" //
				+ ",===\n" //
				+ "Some Text");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<th>first</th>" //
				+ "<th>second</th>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>lorem</td>" //
				+ "<td>ipsum</td>" //
				+ "</tr>" //
				+ "</table>" //
				+ "<p>Some Text</p>\n", html);
	}

	@Test
	public void testTableCSVEscaped() {
		String html = parseToHtml("" //
				+ "[cols=\"2*\", format=\"csv\"]\n" //
				+ "|===\n" //
				+ "lo \\| rem\\,ips | um\n" //
				+ "alice\n" //
				+ "bob\n" //
				+ "first,second\n" //
				+ "|===\n");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>lo \\| rem\\</td>" //
				+ "<td>ips | um</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>alice</td>" //
				+ "<td>bob</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "</tr>" //
				+ "</table>", html);
	}

	@Test
	public void testTableCSVWithQuotes() {
		String html = parseToHtml("" //
				+ "[format=\"csv\"]\n" //
				+ "|===\n" //
				+ "\"first\",second,third\n" //
				+ "first,\"second\",third\n" //
				+ "first,second,\"third\"\n" //
				+ "\"foo,bar\",second,third\n" //
				+ "first,\"foo,bar\",third\n" //
				+ "first,second,\"foo,bar\"\n" //
				+ "|===\n" //
				+ "Some Text");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>foo,bar</td>" //
				+ "<td>second</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>foo,bar</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "<td>foo,bar</td>" //
				+ "</tr>" //
				+ "</table>" //
				+ "<p>Some Text</p>\n", html);
	}

	@Test
	public void testTableCSVWithWrongQuotes() {
		String html = parseToHtml("" //
				+ "[format=\"csv\"]\n" //
				+ "|===\n" //
				+ "xxx\"first\"xxx,second,third\n" //
				+ "first,xxx\"second\"xxx,third\n" //
				+ "first,second,xxx\"third\"xxx\n" //
				+ "\n" //
				+ "xxx \" fi rst \" xxx,second,third\n" //
				+ "first,xxx \"sec ond\" xxx,third\n" //
				+ "first,second,xxx \" third \" xxx\n" //
				+ "|===\n" //
				+ "Some Text");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>xxx\"first\"xxx</td>" //
				+ "<td>second</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>xxx\"second\"xxx</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "<td>xxx\"third\"xxx</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>xxx \" fi rst \" xxx</td>" //
				+ "<td>second</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>xxx \"sec ond\" xxx</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "<td>xxx \" third \" xxx</td>" //
				+ "</tr>" //
				+ "</table>" //
				+ "<p>Some Text</p>\n", html);
	}

	@Test
	public void testTableCSVWithQuotesAndEscaped() {
		String html = parseToHtml("" //
				+ "[format=\"csv\"]\n" //
				+ "|===\n" //
				+ "\"lorem \"\"a\"\" ipsum\",second,third\n" //
				+ "first,\"lorem \"\"a\"\" ipsum\",third\n" //
				+ "first,second,\"lorem \"\"a\"\" ipsum\"\n" //
				+ "\n" //
				+ "\"\"\"a\"\" ipsum\",second,third\n" //
				+ "first,\"\"\"a\"\" ipsum\",third\n" //
				+ "first,second,\"\"\"a\"\" ipsum\"\n" //
				+ "\n" //
				+ "\"lorem \"\"a\"\"\",second,third\n" //
				+ "first,\"lorem \"\"a\"\"\",third\n" //
				+ "first,second,\"lorem \"\"a\"\"\"\n" //
				+ "|===\n" //
				+ "Some Text");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>lorem \"a\" ipsum</td>" //
				+ "<td>second</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>lorem \"a\" ipsum</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "<td>lorem \"a\" ipsum</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>\"a\" ipsum</td>" //
				+ "<td>second</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>\"a\" ipsum</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "<td>\"a\" ipsum</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>lorem \"a\"</td>" //
				+ "<td>second</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>lorem \"a\"</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "<td>lorem \"a\"</td>" //
				+ "</tr>" //
				+ "</table>" //
				+ "<p>Some Text</p>\n", html);
	}

	@Test
	public void testTableDSV() {
		String html = parseToHtml("" //
				+ "[format=\"dsv\", options=\"header\"]\n" //
				+ "|===\n" //
				+ "first:second:third\n" //
				+ "\n" //
				+ "lorem:ipsum:dolor\n" //
				+ "|===\n" //
				+ "Some Text");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<th>first</th>" //
				+ "<th>second</th>" //
				+ "<th>third</th>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>lorem</td>" //
				+ "<td>ipsum</td>" //
				+ "<td>dolor</td>" //
				+ "</tr>" //
				+ "</table>" //
				+ "<p>Some Text</p>\n", html);
	}

	@Test
	public void testTableDSVShorthandNotation() {
		String html = parseToHtml("" //
				+ ":===\n" //
				+ "first:second\n" //
				+ "lorem:ipsum\n" //
				+ ":===\n" //
				+ "Some Text");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<th>first</th>" //
				+ "<th>second</th>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>lorem</td>" //
				+ "<td>ipsum</td>" //
				+ "</tr>" //
				+ "</table>" //
				+ "<p>Some Text</p>\n", html);
	}

	@Test
	public void testTableDSVEscaped() {
		String html = parseToHtml("" //
				+ "[cols=\"2*\", format=\"dsv\"]\n" //
				+ "|===\n" //
				+ "lo \\| rem\\:ips | um\n" //
				+ "dolor\n" //
				+ "\n" //
				+ "first:second\n" //
				+ "|===\n");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>lo \\| rem:ips | um</td>" //
				+ "<td>dolor</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "</tr>" //
				+ "</table>", html);
	}

	@Test
	public void testTableFormattedText() {
		String html = parseToHtml("" //
				+ "|===\n" //
				+ "| first | second\n" //
				+ "| *bold* _italic_ | plain\n" //
				+ "| a^super^ | a~sub~\n" //
				+ "|===\n");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>first</td><td>second</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td><strong>bold</strong> <em>italic</em></td>" //
				+ "<td>plain</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>a<sup>super</sup></td>" //
				+ "<td>a<sub>sub</sub></td>" //
				+ "</tr>" //
				+ "</table>", html);
	}

	@Test
	public void testTableFormattedTextRanges() {
		List<Event> events = parseToEvents("" //
				+ "|===\n" //
				+ "| first | second\n" //
				+ "| *bold* _italic_ | plain\n" //
				+ "| a^super^ | a~sub~\n" //
				+ "|===\n");

		boolean emphasisFound = false;
		boolean boldFound = false;
		boolean superFound = false;
		boolean subFound = false;
		for (Event event : events) {
			if (event.spanType == SpanType.EMPHASIS) {
				assertEquals(9, event.locator.getLineCharacterOffset());
				assertEquals(17, event.locator.getLineSegmentEndOffset());
				emphasisFound = true;
			} else if (event.spanType == SpanType.STRONG) {
				assertEquals(2, event.locator.getLineCharacterOffset());
				assertEquals(8, event.locator.getLineSegmentEndOffset());
				boldFound = true;
			} else if (event.spanType == SpanType.SUPERSCRIPT) {
				assertEquals(3, event.locator.getLineCharacterOffset());
				assertEquals(10, event.locator.getLineSegmentEndOffset());
				superFound = true;
			} else if (event.spanType == SpanType.SUBSCRIPT) {
				assertEquals(14, event.locator.getLineCharacterOffset());
				assertEquals(19, event.locator.getLineSegmentEndOffset());
				subFound = true;
			}
		}
		assertTrue("expected to find emphasis span", emphasisFound);
		assertTrue("expected to find strong span", boldFound);
		assertTrue("expected to find superscript span", superFound);
		assertTrue("expected to find subscript span", subFound);
	}

	@Test
	public void testHorizontalSpan() {
		String html = parseToHtml("" //
				+ "|===\n" //
				+ "| first | second | third\n" //
				+ "3+| lorem\n" //
				+ "2+| lorem | ipsum\n" //
				+ "| lorem 2+| ipsum\n" //
				+ "| lorem | ipsum | dolor\n" //
				+ "|===\n" //
				+ "Some Text");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td colspan=\"3\">lorem</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td colspan=\"2\">lorem</td>" //
				+ "<td>ipsum</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>lorem</td>" //
				+ "<td colspan=\"2\">ipsum</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td>lorem</td>" //
				+ "<td>ipsum</td>" //
				+ "<td>dolor</td>" //
				+ "</tr>" //
				+ "</table>" //
				+ "<p>Some Text</p>\n", html);
	}

	@Test
	public void testCellHorizontalAlign() {
		String html = parseToHtml("" //
				+ "|===\n" //
				+ "| first | second | third\n" //
				+ ">| lorem ^| ipsum <| dolor\n" //
				+ "|===\n" //
				+ "Some Text");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td align=\"right\">lorem</td>" //
				+ "<td align=\"center\">ipsum</td>" //
				+ "<td align=\"left\">dolor</td>" //
				+ "</tr>" //
				+ "</table>" //
				+ "<p>Some Text</p>\n", html);
	}

	@Test
	public void testCellHorizontalAlignAndSpan() {
		String html = parseToHtml("" //
				+ "|===\n" //
				+ "| first | second | third\n" //
				+ "3+>| lorem\n" //
				+ ">| lorem 2+^| ipsum\n" //
				+ "|===\n" //
				+ "Some Text");
		assertEquals("<table>" //
				+ "<tr>" //
				+ "<td>first</td>" //
				+ "<td>second</td>" //
				+ "<td>third</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td align=\"right\" colspan=\"3\">lorem</td>" //
				+ "</tr>" //
				+ "<tr>" //
				+ "<td align=\"right\">lorem</td>" //
				+ "<td align=\"center\" colspan=\"2\">ipsum</td>" //
				+ "</tr>" //
				+ "</table>" //
				+ "<p>Some Text</p>\n", html);
	}
}
