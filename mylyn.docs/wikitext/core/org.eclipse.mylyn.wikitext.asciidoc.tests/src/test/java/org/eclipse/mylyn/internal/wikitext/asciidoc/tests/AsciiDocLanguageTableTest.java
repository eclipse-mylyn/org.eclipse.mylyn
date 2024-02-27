/*******************************************************************************
 * Copyright (c) 2016, 2024 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *     ArSysOp - ongoing support
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
@SuppressWarnings({ "nls", "restriction" })
public class AsciiDocLanguageTableTest extends AsciiDocLanguageTestBase {

	@Test
	public void testSimpleTable() {
		String html = parseToHtml("""
				|===

				| first | second | third

				| lorem | ipsum | dolor

				|===
				Some Text""");
		assertEquals("""
				<table>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td>lorem</td>\
				<td>ipsum</td>\
				<td>dolor</td>\
				</tr>\
				</table>\
				<p>Some Text</p>
				""", html);
	}

	@Test
	public void testSimpleTableOneCol() {
		String html = parseToHtml("""
				|===

				| first

				| lorem

				|===
				Some Text""");
		assertEquals("""
				<table>\
				<tr>\
				<td>first</td>\
				</tr>\
				<tr>\
				<td>lorem</td>\
				</tr>\
				</table>\
				<p>Some Text</p>
				""", html);
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
		String html = parseToHtml("""
				|===\s\s\s

				|lorem|ipsum

				|===
				""");
		assertEquals("""
				<table>\
				<tr>\
				<td>lorem</td>\
				<td>ipsum</td>\
				</tr>\
				</table>""", html);
	}

	@Test
	public void testTableWithLeadingAndTrailingSpaces() {
		String html = parseToHtml("""
				|===

				| lorem         |     _ipsum_\s\s\s\s\s

				|===\s\s\s
				""");
		assertEquals("""
				<table>\
				<tr>\
				<td>lorem</td>\
				<td><em>ipsum</em></td>\
				</tr>\
				</table>""", html);
	}

	@Test
	public void testTableWithEscapedPipe() {
		String html = parseToHtml("""
				|===

				| lorem \\| ipsum | other

				|===
				""");
		assertEquals("""
				<table>\
				<tr>\
				<td>lorem | ipsum</td>\
				<td>other</td>\
				</tr>\
				</table>""", html);
	}

	@Test
	public void testTableFirstRowDefinesNumberOfColumns() {
		String html = parseToHtml("""
				|===

				| lorem | ipsum

				| first
				| second

				| alice
				| bob
				|===
				""");
		assertEquals("""
				<table>\
				<tr>\
				<td>lorem</td>\
				<td>ipsum</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				</tr>\
				<tr>\
				<td>alice</td>\
				<td>bob</td>\
				</tr>\
				</table>""", html);
	}

	@Test
	public void testTableEmptyCellInFirstRow() {
		String html = parseToHtml("""
				|===

				| lorem | | ipsum

				| one | two | three

				|===
				""");
		assertEquals("""
				<table>\
				<tr>\
				<td>lorem</td>\
				<td></td>\
				<td>ipsum</td>\
				</tr>\
				<tr>\
				<td>one</td>\
				<td>two</td>\
				<td>three</td>\
				</tr>\
				</table>""", html);
	}

	@Test
	public void testTableEmptyCellInSecondRow() {
		String html = parseToHtml("""
				|===
				|one|two|three
				|lorem||ipsum
				|===
				""");
		assertEquals("""
				<table>\
				<tr>\
				<td>one</td>\
				<td>two</td>\
				<td>three</td>\
				</tr>\
				<tr>\
				<td>lorem</td>\
				<td></td>\
				<td>ipsum</td>\
				</tr>\
				</table>""", html);
	}

	@Test
	public void testTableColsAttribute() {
		String html = parseToHtml("""
				[cols="3*"]
				|===

				| first
				| second
				| third

				| lorem
				| ipsum
				| dolor

				|===
				Some Text""");
		assertEquals("""
				<table>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td>lorem</td>\
				<td>ipsum</td>\
				<td>dolor</td>\
				</tr>\
				</table>\
				<p>Some Text</p>
				""", html);
	}

	@Test
	public void testTableColsAttributeCellMissingInLastRow() {
		String html = parseToHtml("""
				[cols="2*"]
				|===

				| first
				| second

				| lorem

				|===
				Some Text""");
		assertEquals("""
				<table>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				</tr>\
				<tr>\
				<td>lorem</td>\
				</tr>\
				</table>\
				<p>Some Text</p>
				""", html);
	}

	@Test
	public void testTableColsAttributeAndAlignment() {
		String html = parseToHtml("""
				[cols="<.^,>.>"]
				|===
				| first
				| second
				| lorem
				| ipsum
				|===
				Some Text""");
		assertEquals("""
				<table>\
				<tr>\
				<td align="left" valign="middle">first</td>\
				<td align="right" valign="bottom">second</td>\
				</tr>\
				<tr>\
				<td align="left" valign="middle">lorem</td>\
				<td align="right" valign="bottom">ipsum</td>\
				</tr>\
				</table>\
				<p>Some Text</p>
				""", html);
	}

	@Test
	public void testColsTableEmptyCellInFirstRow() {
		String html = parseToHtml("""
				[cols="3*"]
				|===

				| lorem\s
				|\s
				| ipsum

				| one\s
				| two\s
				| three

				|===
				""");
		assertEquals("""
				<table>\
				<tr>\
				<td>lorem</td>\
				<td></td>\
				<td>ipsum</td>\
				</tr>\
				<tr>\
				<td>one</td>\
				<td>two</td>\
				<td>three</td>\
				</tr>\
				</table>""", html);
	}

	@Test
	public void testColsTableEmptyCellInSecondRow() {
		String html = parseToHtml("""
				[cols="3*"]
				|===
				|one
				|two
				|three
				|lorem
				|
				|ipsum
				|===
				""");
		assertEquals("""
				<table>\
				<tr>\
				<td>one</td>\
				<td>two</td>\
				<td>three</td>\
				</tr>\
				<tr>\
				<td>lorem</td>\
				<td></td>\
				<td>ipsum</td>\
				</tr>\
				</table>""", html);
	}

	@Test
	public void testColsTableTooManyCellsInSecondRow() {
		String html = parseToHtml("""
				[cols="1,1"]
				|===
				|one|two
				|three|four|five|six
				|===
				""");
		assertEquals("""
				<table>\
				<tr>\
				<td>one</td>\
				<td>two</td>\
				</tr>\
				<tr>\
				<td>three</td>\
				<td>four</td>\
				</tr>\
				<tr>\
				<td>five</td>\
				<td>six</td>\
				</tr>\
				</table>""", html);
	}

	@Test
	public void testMultilineCell() {
		String html = parseToHtml("""
				[cols="2*"]
				|===
				|aaa
				bbb
				|xxx
				| first | second
				|===
				""");
		assertEquals("""
				<table>\
				<tr>\
				<td>aaa bbb</td>\
				<td>xxx</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				</tr>\
				</table>""", html);
	}

	@Test
	public void testMultilineCellOneCol() {
		String html = parseToHtml("""
				|===
				| aaa
				bbb
				ccc
				| xxx
				yyy
				| 000
				|===
				""");
		assertEquals("""
				<table>\
				<tr>\
				<td>aaa bbb ccc</td>\
				</tr>\
				<tr>\
				<td>xxx yyy</td>\
				</tr>\
				<tr>\
				<td>000</td>\
				</tr>\
				</table>""", html);
	}

	@Test
	public void testMultilineCellTwoCols() {
		String html = parseToHtml("""
				|===
				| aaa
				bbb | xxx
				| 000
				| 111
				|===
				""");
		assertEquals("""
				<table>\
				<tr>\
				<td>aaa bbb</td>\
				<td>xxx</td>\
				</tr>\
				<tr>\
				<td>000</td>\
				<td>111</td>\
				</tr>\
				</table>""", html);
	}

	@Test
	public void testBasicTableAttributes() {
		String html = parseToHtml("""
				[width="80%",options="header"]
				|===
				|one|two|three
				|four|five|six
				|===""");
		assertEquals("""
				<table width="80%">\
				<tr><th>one</th><th>two</th><th>three</th></tr>\
				<tr><td>four</td><td>five</td><td>six</td></tr>\
				</table>""", html);
	}

	@Test
	public void testTableAttributesWithCols() {
		String html = parseToHtml("""
				[width="70%",options="header",cols="2*"]
				|===
				|one
				|two
				|three
				|four
				|===""");
		assertEquals("""
				<table width="70%">\
				<tr><th>one</th><th>two</th></tr>\
				<tr><td>three</td><td>four</td></tr>\
				</table>""", html);
	}

	@Test
	public void testTableCSV() {
		String html = parseToHtml("""
				[format="csv", options="header"]
				|===
				first,second,third
				lorem,ipsum,dolor
				|===
				Some Text""");
		assertEquals("""
				<table>\
				<tr>\
				<th>first</th>\
				<th>second</th>\
				<th>third</th>\
				</tr>\
				<tr>\
				<td>lorem</td>\
				<td>ipsum</td>\
				<td>dolor</td>\
				</tr>\
				</table>\
				<p>Some Text</p>
				""", html);
	}

	@Test
	public void testTableCSVShorthandNotation() {
		String html = parseToHtml("""
				,===
				first,second
				lorem,ipsum
				,===
				Some Text""");
		assertEquals("""
				<table>\
				<tr>\
				<th>first</th>\
				<th>second</th>\
				</tr>\
				<tr>\
				<td>lorem</td>\
				<td>ipsum</td>\
				</tr>\
				</table>\
				<p>Some Text</p>
				""", html);
	}

	@Test
	public void testTableCSVEscaped() {
		String html = parseToHtml("""
				[cols="2*", format="csv"]
				|===
				lo \\| rem\\,ips | um
				alice
				bob
				first,second
				|===
				""");
		assertEquals("""
				<table>\
				<tr>\
				<td>lo \\| rem\\</td>\
				<td>ips | um</td>\
				</tr>\
				<tr>\
				<td>alice</td>\
				<td>bob</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				</tr>\
				</table>""", html);
	}

	@Test
	public void testTableCSVWithQuotes() {
		String html = parseToHtml("""
				[format="csv"]
				|===
				"first",second,third
				first,"second",third
				first,second,"third"
				"foo,bar",second,third
				first,"foo,bar",third
				first,second,"foo,bar"
				|===
				Some Text""");
		assertEquals("""
				<table>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td>foo,bar</td>\
				<td>second</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>foo,bar</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				<td>foo,bar</td>\
				</tr>\
				</table>\
				<p>Some Text</p>
				""", html);
	}

	@Test
	public void testTableCSVWithWrongQuotes() {
		String html = parseToHtml("""
				[format="csv"]
				|===
				xxx"first"xxx,second,third
				first,xxx"second"xxx,third
				first,second,xxx"third"xxx

				xxx " fi rst " xxx,second,third
				first,xxx "sec ond" xxx,third
				first,second,xxx " third " xxx
				|===
				Some Text""");
		assertEquals("""
				<table>\
				<tr>\
				<td>xxx"first"xxx</td>\
				<td>second</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>xxx"second"xxx</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				<td>xxx"third"xxx</td>\
				</tr>\
				<tr>\
				<td>xxx " fi rst " xxx</td>\
				<td>second</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>xxx "sec ond" xxx</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				<td>xxx " third " xxx</td>\
				</tr>\
				</table>\
				<p>Some Text</p>
				""", html);
	}

	@Test
	public void testTableCSVWithQuotesAndEscaped() {
		String html = parseToHtml("""
						[format="csv"]
						|===
						"lorem ""a"" ipsum",second,third
						first,"lorem ""a"" ipsum",third
						first,second,"lorem ""a"" ipsum"

						\"""a"" ipsum",second,third
						first,\"""a"" ipsum",third
						first,second,\"""a"" ipsum"

						"lorem ""a\""",second,third
						first,"lorem ""a\""",third
						first,second,"lorem ""a\"""
		|===
						Some Text""");
		assertEquals("""
				<table>\
				<tr>\
				<td>lorem "a" ipsum</td>\
				<td>second</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>lorem "a" ipsum</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				<td>lorem "a" ipsum</td>\
				</tr>\
				<tr>\
				<td>"a" ipsum</td>\
				<td>second</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>"a" ipsum</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				<td>"a" ipsum</td>\
				</tr>\
				<tr>\
				<td>lorem "a"</td>\
				<td>second</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>lorem "a"</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				<td>lorem "a"</td>\
				</tr>\
				</table>\
				<p>Some Text</p>
				""", html);
	}

	@Test
	public void testTableDSV() {
		String html = parseToHtml("""
				[format="dsv", options="header"]
				|===
				first:second:third

				lorem:ipsum:dolor
				|===
				Some Text""");
		assertEquals("""
				<table>\
				<tr>\
				<th>first</th>\
				<th>second</th>\
				<th>third</th>\
				</tr>\
				<tr>\
				<td>lorem</td>\
				<td>ipsum</td>\
				<td>dolor</td>\
				</tr>\
				</table>\
				<p>Some Text</p>
				""", html);
	}

	@Test
	public void testTableDSVShorthandNotation() {
		String html = parseToHtml("""
				:===
				first:second
				lorem:ipsum
				:===
				Some Text""");
		assertEquals("""
				<table>\
				<tr>\
				<th>first</th>\
				<th>second</th>\
				</tr>\
				<tr>\
				<td>lorem</td>\
				<td>ipsum</td>\
				</tr>\
				</table>\
				<p>Some Text</p>
				""", html);
	}

	@Test
	public void testTableDSVEscaped() {
		String html = parseToHtml("""
				[cols="2*", format="dsv"]
				|===
				lo \\| rem\\:ips | um
				dolor

				first:second
				|===
				""");
		assertEquals("""
				<table>\
				<tr>\
				<td>lo \\| rem:ips | um</td>\
				<td>dolor</td>\
				</tr>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				</tr>\
				</table>""", html);
	}

	@Test
	public void testTableFormattedText() {
		String html = parseToHtml("""
				|===
				| first | second
				| *bold* _italic_ | plain
				| a^super^ | a~sub~
				|===
				""");
		assertEquals("""
				<table>\
				<tr>\
				<td>first</td><td>second</td>\
				</tr>\
				<tr>\
				<td><strong>bold</strong> <em>italic</em></td>\
				<td>plain</td>\
				</tr>\
				<tr>\
				<td>a<sup>super</sup></td>\
				<td>a<sub>sub</sub></td>\
				</tr>\
				</table>""", html);
	}

	@Test
	public void testTableFormattedTextRanges() {
		List<Event> events = parseToEvents("""
				|===
				| first | second
				| *bold* _italic_ | plain
				| a^super^ | a~sub~
				|===
				""");

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
		String html = parseToHtml("""
				|===
				| first | second | third
				3+| lorem
				2+| lorem | ipsum
				| lorem 2+| ipsum
				| lorem | ipsum | dolor
				|===
				Some Text""");
		assertEquals("""
				<table>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td colspan="3">lorem</td>\
				</tr>\
				<tr>\
				<td colspan="2">lorem</td>\
				<td>ipsum</td>\
				</tr>\
				<tr>\
				<td>lorem</td>\
				<td colspan="2">ipsum</td>\
				</tr>\
				<tr>\
				<td>lorem</td>\
				<td>ipsum</td>\
				<td>dolor</td>\
				</tr>\
				</table>\
				<p>Some Text</p>
				""", html);
	}

	@Test
	public void testCellHorizontalAlign() {
		String html = parseToHtml("""
				|===
				| first | second | third
				>| lorem ^| ipsum <| dolor
				|===
				Some Text""");
		assertEquals("""
				<table>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td align="right">lorem</td>\
				<td align="center">ipsum</td>\
				<td align="left">dolor</td>\
				</tr>\
				</table>\
				<p>Some Text</p>
				""", html);
	}

	@Test
	public void testCellHorizontalAlignAndSpan() {
		String html = parseToHtml("""
				|===
				| first | second | third
				3+>| lorem
				>| lorem 2+^| ipsum
				|===
				Some Text""");
		assertEquals("""
				<table>\
				<tr>\
				<td>first</td>\
				<td>second</td>\
				<td>third</td>\
				</tr>\
				<tr>\
				<td align="right" colspan="3">lorem</td>\
				</tr>\
				<tr>\
				<td align="right">lorem</td>\
				<td align="center" colspan="2">ipsum</td>\
				</tr>\
				</table>\
				<p>Some Text</p>
				""", html);
	}

	@Test
	public void testHorizontalSpanInFirstRow() {
		String html = parseToHtml("""
				|===
				3+| one
				| lorem | ipsum | dolor
				|===
				Some Text""");
		assertEquals("""
				<table>\
				<tr>\
				<td colspan="3">one</td>\
				</tr>\
				<tr>\
				<td>lorem</td>\
				<td>ipsum</td>\
				<td>dolor</td>\
				</tr>\
				</table>\
				<p>Some Text</p>
				""", html);
	}

}
