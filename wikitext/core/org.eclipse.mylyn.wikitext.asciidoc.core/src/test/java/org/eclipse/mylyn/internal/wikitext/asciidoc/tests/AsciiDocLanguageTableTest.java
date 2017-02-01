/*******************************************************************************
 * Copyright (c) 2016 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

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
}
