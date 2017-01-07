/*******************************************************************************
 * Copyright (c) 2015, 2016 Patrik Suzzi and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Patrik Suzzi - Bug 481670 - [asciidoc] support for lists
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * Tests for the AsciiDoc ListBlock elements.
 *
 * @author Patrik Suzzi
 */
public class AsciiDocLanguageListTest extends AsciiDocLanguageTestBase {

	static final String BR = System.getProperty("line.separator");

	@Test
	public void testUnorderedList() {
		String html = parseToHtml("" //
				+ "* level 1" + BR //
				+ "** level 2" + BR //
				+ "*** level 3" + BR //
				+ "**** level 4" + BR //
				+ "***** level 5" + BR //
				+ "* level 1" + BR);
		assertEquals("Unordered List parsing",
				"" //
						+ "<ul>" //
						+ "<li>level 1<ul>" //
						+ "<li>level 2<ul>" //
						+ "<li>level 3<ul>" //
						+ "<li>level 4<ul>" //
						+ "<li>level 5</li>" //
						+ "</ul></li>" //
						+ "</ul></li>" //
						+ "</ul></li>" //
						+ "</ul></li>" //
						+ "<li>level 1</li>" //
						+ "</ul>".trim(),
				html.trim());
	}

	@Test
	public void testUnorderedListWithMultipleWhitespaces() {
		String html = parseToHtml("" //
				+ "- item 1" + BR //
				+ "-  item 2" + BR //
				+ "- _item_ 3" + BR);
		assertEquals("Unordered List with whitespaces and formatting",
				"" //
						+ "<ul>" //
						+ "<li>item 1</li>" //
						+ "<li>item 2</li>" //
						+ "<li><em>item</em> 3</li>" //
						+ "</ul>".trim(),
				html.trim());
	}

	@Test
	public void testUnorderedListWithBlankLines() {
		String html = parseToHtml("" //
				+ "* item 1" + BR //
				+ "" + BR //
				+ "* item 2" + BR //
				+ "" + BR //
				+ "" + BR //
				+ "* *item3*" + BR //
				+ "" + BR //
				+ "other text" + BR);
		assertEquals("Unordered List with blank lines",
				"" //
						+ "<ul>" //
						+ "<li>item 1</li>" //
						+ "<li>item 2</li>" //
						+ "<li><strong>item3</strong></li>" //
						+ "</ul>" //
						+ "<p>other text</p>".trim(),
				html.trim());
	}

	@Test
	public void testOrderedList() {
		String html = parseToHtml("" //
				+ ". level 1" + BR //
				+ ".. level 2" + BR //
				+ "... level 3" + BR //
				+ ".... level 4" + BR //
				+ "..... level 5" + BR //
				+ ". level 1" + BR);
		assertEquals("Ordered List parsing",
				"" //
						+ "<ol>" //
						+ "<li>level 1<ol>" //
						+ "<li>level 2<ol>" //
						+ "<li>level 3<ol>" //
						+ "<li>level 4<ol>" //
						+ "<li>level 5</li>" //
						+ "</ol></li>" //
						+ "</ol></li>" //
						+ "</ol></li>" //
						+ "</ol></li>" //
						+ "<li>level 1</li>" //
						+ "</ol>".trim(),
				html.trim());
	}

	@Test
	public void testMixedList() {
		String html = parseToHtml("" //
				+ ". level 1" + BR //
				+ "** level 2" + BR //
				+ "... level 3" + BR //
				+ "**** level 4" + BR //
				+ "..... level 5" + BR //
				+ ". level 1" + BR);
		assertEquals("Mixed List parsing",
				"" //
						+ "<ol>" //
						+ "<li>level 1<ul>" //
						+ "<li>level 2<ol>" //
						+ "<li>level 3<ul>" //
						+ "<li>level 4<ol>" //
						+ "<li>level 5</li>" //
						+ "</ol></li>" //
						+ "</ul></li>" //
						+ "</ol></li>" //
						+ "</ul></li>" //
						+ "<li>level 1</li></ol>".trim(),
				html.trim());
	}

	@Test
	public void testMixedList2() {
		// mixed ordered and unordered with starting element at 2nd level
		String html = parseToHtml("" //
				+ "** level2 (non-zero start)" + BR //
				+ "*** level 3" + BR //
				+ ".... level 4, 1st" + BR //
				+ ".... level 4, 2nd" + BR);
		assertEquals("Mixed List 2 parsing",
				"" //
						+ "<ul>" //
						+ "<li>level2 (non-zero start)<ul>" //
						+ "<li>level 3<ol>" //
						+ "<li>level 4, 1st</li>" //
						+ "<li>level 4, 2nd</li>" //
						+ "</ol></li>" //
						+ "</ul></li>" //
						+ "</ul>".trim(),
				html.trim());
	}

	@Test
	public void testMixedLevels() {
		String html = parseToHtml("- item level 1" + BR //
				+ "** first item level 2" + BR //
				+ "** second item level 2" + BR //
				+ "* item level 3" + BR //
				+ "- item level 1" + BR);
		assertEquals("testMixedLevels",
				"" //
						+ "<ul>"//
						+ "<li>item level 1<ul>" //
						+ "<li>first item level 2</li>" //
						+ "<li>second item level 2<ul>" //
						+ "<li>item level 3</li>" //
						+ "</ul></li></ul></li>" //
						+ "<li>item level 1</li>" //
						+ "</ul>",
				html);
	}

	@Test
	public void testUnorderedListLevel() {
		String html = parseToHtml("" //
				+ "* level 1" + BR //
				+ "- level 2" + BR //
				+ "** level 3" + BR //
				+ "** level 3b" + BR //
				+ "- level 2b" + BR //
				+ "* level 1b" + BR //
				+ "- level 2c" + BR);
		assertEquals("" //
				+ "<ul>" //
				+ "<li>level 1<ul>" //
				+ "<li>level 2<ul>" //
				+ "<li>level 3</li>" //
				+ "<li>level 3b</li>" //
				+ "</ul></li>" //
				+ "<li>level 2b</li>" //
				+ "</ul></li>" //
				+ "<li>level 1b<ul>" //
				+ "<li>level 2c</li>" //
				+ "</ul></li>" //
				+ "</ul>".trim(), html.trim());
	}

	@Test
	public void testMixedLevelsUnordered() {
		String html = parseToHtml("* item 1" + BR //
				+ "* item 2" + BR //
				+ "" + BR //
				+ "first list done" + BR //
				+ "- item 3" + BR);
		assertEquals("" //
				+ "<ul>" //
				+ "<li>item 1</li>" //
				+ "<li>item 2</li></ul>" //
				+ "<p>first list done</p>\n" //
				+ "<ul><li>item 3</li></ul>", html);
	}

	@Test
	public void testUnorderedLeadingWhitespaces() {
		String html = parseToHtml("  * item 1" + BR //
				+ "  * item 2" + BR + BR //
				+ "end of first list" + BR + BR //
				+ "    - item a" + BR //
				+ "    - item b" + BR //
		);
		assertEquals("" //
				+ "<ul>" //
				+ "<li>item 1</li>" //
				+ "<li>item 2</li>" //
				+ "</ul>" //
				+ "<p>end of first list</p>\n" //
				+ "<ul>" //
				+ "<li>item a</li>" //
				+ "<li>item b</li>" //
				+ "</ul>", html);
	}

	@Test
	public void testListWithSecondAndThirdLine() {
		String html = parseToHtml("* lorem" + BR //
				+ "ipsum" + BR //
				+ "dolor" + BR //
				+ "* other" + BR); //
		assertEquals(
				"" //
						+ "<ul>" //
						+ "<li>lorem ipsum dolor</li>" //
						+ "<li>other</li>" //
						+ "</ul>" //
				, html);
	}

	@Test
	public void testListWithSecondLineAndLeadingWhitespaces() {
		String html = parseToHtml("* lorem" + BR //
				+ "  ipsum" + BR //
				+ "* other" + BR); //
		assertEquals(
				"" //
						+ "<ul>" //
						+ "<li>lorem ipsum</li>" //
						+ "<li>other</li>" //
						+ "</ul>" //
				, html);
	}

	@Test
	public void testTwoDashes() {
		String html = parseToHtml("- lorem" + BR //
				+ "-- ipsum" + BR //this is not a valid list item
				+ "- other" + BR); //
		assertEquals(
				"" //
						+ "<ul>" //
						+ "<li>lorem -- ipsum</li>" //
						+ "<li>other</li>" //
						+ "</ul>" //
				, html);
	}

	@Test
	public void testSixStars() {
		String html = parseToHtml("** lorem" + BR //
				+ "****** ipsum" + BR //this is not a valid list item
				+ "** other" + BR); //
		assertFalse(html.contains("<li>ipsum</li>"));
	}

	@Test
	public void testSixDots() {
		String html = parseToHtml(".. lorem" + BR //
				+ "...... ipsum" + BR //this is not a valid list item
				+ ".. other" + BR); //
		assertEquals("testListBreak",
				"" //
						+ "<ol>" //
						+ "<li>lorem ...... ipsum</li>" //
						+ "<li>other</li>" //
						+ "</ol>" //
				, html);
	}

	@Test
	public void testListWithContinuationAndParagraph() {
		String html = parseToHtml("* lorem" + BR //
				+ "+" + BR //
				+ "ipsum" + BR //
				+ "* other" + BR); //
		assertEquals("testListBreak",
				"" //
						+ "<ul>" //
						+ "<li>lorem<p>ipsum</p>\n" //
						+ "</li>" //
						+ "<li>other</li>" //
						+ "</ul>" //
				, html);
	}

	@Test
	public void testListWithContinuationAndClosingPlus() {
		String html = parseToHtml("* lorem" + BR //
				+ "+" + BR //
				+ "ipsum" + BR //
				+ "+" + BR //
				+ "* other" + BR); //
		assertEquals("testListBreak",
				"" //
						+ "<ul>" //
						+ "<li>lorem<p>ipsum</p>\n" //
						+ "</li>" //
						+ "<li>other</li>" //
						+ "</ul>" //
				, html);
	}

	@Test
	public void testListWithContinuationAndClosingWhitespace() {
		String html = parseToHtml("* lorem" + BR //
				+ "+" + BR //
				+ "ipsum" + BR //
				+ "" + BR //
				+ "* other" + BR); //
		assertEquals("testListBreak",
				"" //
						+ "<ul>" //
						+ "<li>lorem<p>ipsum</p>\n" //
						+ "</li>" //
						+ "<li>other</li>" //
						+ "</ul>" //
				, html);
	}

	@Test
	public void testListWithContinuationAndCodeBlock() {
		String html = parseToHtml("* lorem" + BR //
				+ "+" + BR //
				+ "----" + BR //
				+ "some code" + BR //
				+ "----" + BR //
				+ "* other" + BR); //
		assertEquals("testListBreak", "" //
				+ "<ul>" //
				+ "<li>lorem"
				+ "<div class=\"listingblock\"><div class=\"content\"><pre class=\"nowrap\"><code class=\"nowrap\">" //
				+ "some code<br/>" //
				+ "</code></pre></div></div>" //
				+ "</li>" //
				+ "<li>other</li>" //
				+ "</ul>" //
				, html);
	}
}
