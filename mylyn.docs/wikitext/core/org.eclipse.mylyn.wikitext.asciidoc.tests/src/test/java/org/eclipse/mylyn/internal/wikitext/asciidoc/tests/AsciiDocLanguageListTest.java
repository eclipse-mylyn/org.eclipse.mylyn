/*******************************************************************************
 * Copyright (c) 2015, 2024 Patrik Suzzi and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Patrik Suzzi - Bug 481670 - [asciidoc] support for lists
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.toolkit.RecordingDocumentBuilder.Event;
import org.junit.Test;

/**
 * Tests for the AsciiDoc ListBlock elements.
 *
 * @author Patrik Suzzi
 */
@SuppressWarnings("nls")
public class AsciiDocLanguageListTest extends AsciiDocLanguageTestBase {

	static final String BR = System.lineSeparator();

	@Test
	public void testUnorderedList() {
		String html = parseToHtml("" //
				+ "* level 1" + BR //
				+ "** level 2" + BR //
				+ "*** level 3" + BR //
				+ "**** level 4" + BR //
				+ "***** level 5" + BR //
				+ "* level 1" + BR);
		assertEquals("Unordered List parsing", "" //
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
				+ "</ul>".trim(), html.trim());
	}

	@Test
	public void testUnorderedListWithMultipleWhitespaces() {
		String html = parseToHtml("" //
				+ "- item 1" + BR //
				+ "-  item 2" + BR //
				+ "- _item_ 3" + BR);
		assertEquals("Unordered List with whitespaces and formatting", "" //
				+ "<ul>" //
				+ "<li>item 1</li>" //
				+ "<li>item 2</li>" //
				+ "<li><em>item</em> 3</li>" //
				+ "</ul>".trim(), html.trim());
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
		assertEquals("Unordered List with blank lines", "" //
				+ "<ul>" //
				+ "<li>item 1</li>" //
				+ "<li>item 2</li>" //
				+ "<li><strong>item3</strong></li>" //
				+ "</ul>" //
				+ "<p>other text</p>".trim(), html.trim());
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
		assertEquals("Ordered List parsing", "" //
				+ "<ol style=\"list-style-type:decimal;\">" //
				+ "<li>level 1<ol style=\"list-style-type:lower-alpha;\">" //
				+ "<li>level 2<ol style=\"list-style-type:lower-roman;\">" //
				+ "<li>level 3<ol style=\"list-style-type:upper-alpha;\">" //
				+ "<li>level 4<ol style=\"list-style-type:upper-roman;\">" //
				+ "<li>level 5</li>" //
				+ "</ol></li>" //
				+ "</ol></li>" //
				+ "</ol></li>" //
				+ "</ol></li>" //
				+ "<li>level 1</li>" //
				+ "</ol>".trim(), html.trim());
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
		assertEquals("Mixed List parsing", "" //
				+ "<ol style=\"list-style-type:decimal;\">" //
				+ "<li>level 1<ul>" //
				+ "<li>level 2<ol style=\"list-style-type:lower-alpha;\">" //
				+ "<li>level 3<ul>" //
				+ "<li>level 4<ol style=\"list-style-type:lower-roman;\">" //
				+ "<li>level 5</li>" //
				+ "</ol></li>" //
				+ "</ul></li>" //
				+ "</ol></li>" //
				+ "</ul></li>" //
				+ "<li>level 1</li></ol>".trim(), html.trim());
	}

	@Test
	public void testMixedList2() {
		// mixed ordered and unordered with starting element at 2nd level
		String html = parseToHtml("" //
				+ "** level2 (non-zero start)" + BR //
				+ "*** level 3" + BR //
				+ ".... level 4, 1st" + BR //
				+ ".... level 4, 2nd" + BR);
		assertEquals("Mixed List 2 parsing", "" //
				+ "<ul>" //
				+ "<li>level2 (non-zero start)<ul>" //
				+ "<li>level 3<ol style=\"list-style-type:decimal;\">" //
				+ "<li>level 4, 1st</li>" //
				+ "<li>level 4, 2nd</li>" //
				+ "</ol></li>" //
				+ "</ul></li>" //
				+ "</ul>".trim(), html.trim());
	}

	@Test
	public void testMixedLevels() {
		String html = parseToHtml("- item level 1" + BR //
				+ "** first item level 2" + BR //
				+ "** second item level 2" + BR //
				+ "* item level 3" + BR //
				+ "- item level 1" + BR);
		assertEquals("testMixedLevels", """
				<ul>\
				<li>item level 1<ul>\
				<li>first item level 2</li>\
				<li>second item level 2<ul>\
				<li>item level 3</li>\
				</ul></li></ul></li>\
				<li>item level 1</li>\
				</ul>""", html);
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
		assertEquals("""
				<ul>\
				<li>item 1</li>\
				<li>item 2</li></ul>\
				<p>first list done</p>
				<ul><li>item 3</li></ul>""", html);
	}

	@Test
	public void testUnorderedLeadingWhitespaces() {
		String html = parseToHtml("  * item 1" + BR //
				+ "  * item 2" + BR + BR //
				+ "end of first list" + BR + BR //
				+ "    - item a" + BR //
				+ "    - item b" + BR //
				);
		assertEquals("""
				<ul>\
				<li>item 1</li>\
				<li>item 2</li>\
				</ul>\
				<p>end of first list</p>
				<ul>\
				<li>item a</li>\
				<li>item b</li>\
				</ul>""", html);
	}

	@Test
	public void testListWithSecondAndThirdLine() {
		String html = parseToHtml("* lorem" + BR //
				+ "ipsum" + BR //
				+ "dolor" + BR //
				+ "* other" + BR); //
		assertEquals("""
				<ul>\
				<li>lorem ipsum dolor</li>\
				<li>other</li>\
				</ul>""", html);
	}

	@Test
	public void testListWithSecondLineAndLeadingWhitespaces() {
		String html = parseToHtml("* lorem" + BR //
				+ "  ipsum" + BR //
				+ "* other" + BR); //
		assertEquals("""
				<ul>\
				<li>lorem ipsum</li>\
				<li>other</li>\
				</ul>""", html);
	}

	@Test
	public void testTwoDashes() {
		String html = parseToHtml("- lorem" + BR //
				+ "-- ipsum" + BR //this is not a valid list item
				+ "- other" + BR); //
		assertEquals("""
				<ul>\
				<li>lorem -- ipsum</li>\
				<li>other</li>\
				</ul>""", html);
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
		assertEquals("testListBreak", """
				<ol style="list-style-type:decimal;">\
				<li>lorem ...... ipsum</li>\
				<li>other</li>\
				</ol>""", html);
	}

	@Test
	public void testListWithContinuationAndParagraph() {
		String html = parseToHtml("* lorem" + BR //
				+ "+" + BR //
				+ "ipsum" + BR //
				+ "* other" + BR); //
		assertEquals("testListBreak", """
				<ul>\
				<li>lorem<p>ipsum</p>
				</li>\
				<li>other</li>\
				</ul>""", html);
	}

	@Test
	public void testListWithContinuationAndClosingPlus() {
		String html = parseToHtml("* lorem" + BR //
				+ "+" + BR //
				+ "ipsum" + BR //
				+ "+" + BR //
				+ "* other" + BR); //
		assertEquals("testListBreak", """
				<ul>\
				<li>lorem<p>ipsum</p>
				</li>\
				<li>other</li>\
				</ul>""", html);
	}

	@Test
	public void testListWithContinuationAndClosingWhitespace() {
		String html = parseToHtml("* lorem" + BR //
				+ "+" + BR //
				+ "ipsum" + BR //
				+ "" + BR //
				+ "* other" + BR); //
		assertEquals("testListBreak", """
				<ul>\
				<li>lorem<p>ipsum</p>
				</li>\
				<li>other</li>\
				</ul>""", html);
	}

	@Test
	public void testListWithContinuationAndCodeBlock() {
		String html = parseToHtml("* lorem" + BR //
				+ "+" + BR //
				+ "----" + BR //
				+ "some code" + BR //
				+ "----" + BR //
				+ "* other" + BR + "+" + BR //
				+ "----" + BR //
				+ "other code" + BR //
				+ "----" + BR //
				); //
		assertEquals("testListBreak", """
				<ul>\
				<li>lorem\
				<div class="listingblock"><div class="content"><pre class="nowrap"><code class="nowrap">\
				some code<br/>\
				</code></pre></div></div>\
				</li>\
				<li>other\
				<div class="listingblock"><div class="content"><pre class="nowrap"><code class="nowrap">\
				other code<br/>\
				</code></pre></div></div>\
				</li>\
				</ul>""", html);
	}

	@Test
	public void testListWithContinuationAndNestedBlocks() {
		String html = parseToHtml("""
				* item 1
				+
				----
				code block 1
				----
				+
				|===
				|cell 1|cell 2
				|cell 3|cell 4
				|===
				* item 2
				+
				----
				code block 2
				----
				"""
				); //
		assertEquals("testListBreak", """
				<ul>\
				<li>item 1\
				<div class="listingblock"><div class="content"><pre class="nowrap"><code class="nowrap">\
				code block 1<br/>\
				</code></pre></div></div>\
				<table><tr>\
				<td>cell 1</td><td>cell 2</td>\
				</tr><tr>\
				<td>cell 3</td><td>cell 4</td>\
				</tr></table>\
				</li>\
				<li>item 2\
				<div class="listingblock"><div class="content"><pre class="nowrap"><code class="nowrap">\
				code block 2<br/>\
				</code></pre></div></div>\
				</li>\
				</ul>""", html);
	}

	@Test
	public void testListWithNestedCodeBlockContainingEmptyLine() {
		String html = parseToHtml("""
				* item 1
				+
				----
				code block with empty line

				----

				end of list"""
				); //
		assertEquals("testListBreak", """
				<ul>\
				<li>item 1\
				<div class="listingblock"><div class="content"><pre class="nowrap"><code class="nowrap">\
				code block with empty line<br/><br/>\
				</code></pre></div></div>\
				</li>\
				</ul>\
				<p>end of list</p>
				""", html);
	}

	@Test
	public void testListWithNestedCodeBlockContainingContinuation() {
		String html = parseToHtml("""
				* item 1
				+
				----
				code block with continuation
				+
				----

				end of list"""
				); //
		assertEquals("testListBreak", """
				<ul>\
				<li>item 1\
				<div class="listingblock"><div class="content"><pre class="nowrap"><code class="nowrap">\
				code block with continuation<br/>\
				+<br/>\
				</code></pre></div></div>\
				</li>\
				</ul>\
				<p>end of list</p>
				""", html);
	}

	@Test
	public void testListWithNestedTableContainingEmptyLine() {
		String html = parseToHtml("""
				* item 1
				+
				|===
				|one | two

				|===

				end of list"""
				); //
		assertEquals("testListBreak", """
				<ul>\
				<li>item 1\
				<table><tr><td>one</td><td>two</td></tr></table>\
				</li>\
				</ul>\
				<p>end of list</p>
				""", html);
	}

	@Test
	public void testListWithStart() {
		String html = parseToHtml("""
				[start="5"]
				. item 5

				another
				. item""");
		assertEquals("""
				<ol style="list-style-type:decimal;" start="5">\
				<li>item 5</li>\
				</ol><p>another</p>
				<ol style="list-style-type:decimal;">\
				<li>item</li></ol>""", //
				html);
	}

	@Test
	public void testListWithStyleLoweralpha() {
		String html = parseToHtml("""
				[style="loweralpha"]
				. item
				.. item

				another
				. item""");
		assertEquals("""
				<ol style="list-style-type:lower-alpha;">\
				<li>item<ol style="list-style-type:lower-alpha;">\
				<li>item</li>\
				</ol></li></ol><p>another</p>
				<ol style="list-style-type:decimal;">\
				<li>item</li></ol>""", //
				html);
	}

	@Test
	public void testListWithStyleUpperalpha() {
		String html = parseToHtml("""
				[style="upperalpha"]
				. item
				.. item

				another
				. item""");
		assertEquals("""
				<ol style="list-style-type:upper-alpha;">\
				<li>item<ol style="list-style-type:lower-alpha;">\
				<li>item</li>\
				</ol></li></ol><p>another</p>
				<ol style="list-style-type:decimal;">\
				<li>item</li></ol>""", //
				html);
	}

	@Test
	public void testListWithStyleLowerroman() {
		String html = parseToHtml("""
				[style="lowerroman"]
				. item
				.. item

				another
				. item""");
		assertEquals("""
				<ol style="list-style-type:lower-roman;">\
				<li>item<ol style="list-style-type:lower-alpha;">\
				<li>item</li>\
				</ol></li></ol><p>another</p>
				<ol style="list-style-type:decimal;">\
				<li>item</li></ol>""", //
				html);
	}

	@Test
	public void testListWithStyleUpperroman() {
		String html = parseToHtml("""
				[style="upperroman"]
				. item
				.. item

				another
				. item""");
		assertEquals("""
				<ol style="list-style-type:upper-roman;">\
				<li>item<ol style="list-style-type:lower-alpha;">\
				<li>item</li>\
				</ol></li></ol><p>another</p>
				<ol style="list-style-type:decimal;">\
				<li>item</li></ol>""", //
				html);
	}

	@Test
	public void testListWithStyleArabicAsPositionalParam() {
		String html = parseToHtml("""
				[arabic]
				. item
				.. item

				another
				. item""");
		assertEquals("""
				<ol style="list-style-type:decimal;">\
				<li>item<ol style="list-style-type:lower-alpha;">\
				<li>item</li>\
				</ol></li></ol><p>another</p>
				<ol style="list-style-type:decimal;">\
				<li>item</li></ol>""", //
				html);
	}

	@Test
	public void testListWithStyleLoweralphaAsPositionalParam() {
		String html = parseToHtml("""
				[loweralpha]
				. item
				.. item

				another
				. item""");
		assertEquals("""
				<ol style="list-style-type:lower-alpha;">\
				<li>item<ol style="list-style-type:lower-alpha;">\
				<li>item</li>\
				</ol></li></ol><p>another</p>
				<ol style="list-style-type:decimal;">\
				<li>item</li></ol>""", //
				html);
	}

	@Test
	public void testListWithStyleUpperalphaAsPositionalParam() {
		String html = parseToHtml("""
				[upperalpha]
				. item
				.. item

				another
				. item""");
		assertEquals("""
				<ol style="list-style-type:upper-alpha;">\
				<li>item<ol style="list-style-type:lower-alpha;">\
				<li>item</li>\
				</ol></li></ol><p>another</p>
				<ol style="list-style-type:decimal;">\
				<li>item</li></ol>""", //
				html);
	}

	@Test
	public void testListWithStyleLowerromanAsPositionalParam() {
		String html = parseToHtml("""
				[lowerroman]
				. item
				.. item

				another
				. item""");
		assertEquals("""
				<ol style="list-style-type:lower-roman;">\
				<li>item<ol style="list-style-type:lower-alpha;">\
				<li>item</li>\
				</ol></li></ol><p>another</p>
				<ol style="list-style-type:decimal;">\
				<li>item</li></ol>""", //
				html);
	}

	@Test
	public void testListWithStyleUpperromanAsPositionalParam() {
		String html = parseToHtml("""
				[upperroman]
				. item
				.. item

				another
				. item""");
		assertEquals("""
				<ol style="list-style-type:upper-roman;">\
				<li>item<ol style="list-style-type:lower-alpha;">\
				<li>item</li>\
				</ol></li></ol><p>another</p>
				<ol style="list-style-type:decimal;">\
				<li>item</li></ol>""", //
				html);
	}

	@Test
	public void testListWithExplicitNumbering() {
		String html = parseToHtml("""

				MDCLXIV) level 1
				AZ. level 2
				13. level 3
				iv) level 4
				ab. level 5
				I) level 1
				"""
				);
		assertEquals("""
				<ol style="list-style-type:upper-roman;">\
				<li>level 1<ol style="list-style-type:upper-alpha;">\
				<li>level 2<ol style="list-style-type:decimal;">\
				<li>level 3<ol style="list-style-type:lower-roman;">\
				<li>level 4<ol style="list-style-type:lower-alpha;">\
				<li>level 5</li>\
				</ol></li>\
				</ol></li>\
				</ol></li>\
				</ol></li>\
				<li>level 1</li>\
				</ol>""", html);
	}

	@Test
	public void testDefinitionListSimple() {
		String html = parseToHtml("""
				First Item:: first description
				Second Item:: second description
				""");
		assertEquals("""
				<dl>\
				<dt class="hdlist1">First Item</dt>\
				<dd>first description</dd>\
				<dt class="hdlist1">Second Item</dt>\
				<dd>second description</dd>\
				</dl>""", html);
	}

	@Test
	public void testDefinitionListFormattedText() {
		String html = parseToHtml("""
				First Item:: *first* _description_
				Second Item:: `second` description
				""");
		assertEquals("""
				<dl>\
				<dt class="hdlist1">First Item</dt>\
				<dd><strong>first</strong> <em>description</em></dd>\
				<dt class="hdlist1">Second Item</dt>\
				<dd><code>second</code> description</dd>\
				</dl>""", html);
	}

	@Test
	public void testDefinitionListSplitLines() {
		String html = parseToHtml("""
				First Item::
				first description
				Second Item::
				second description
				""");
		assertEquals("""
				<dl>\
				<dt class="hdlist1">First Item</dt>\
				<dd>first description</dd>\
				<dt class="hdlist1">Second Item</dt>\
				<dd>second description</dd>\
				</dl>""", html);
	}

	@Test
	public void testDefinitionListWithBlankLines() {
		String html = parseToHtml("""
				First Item:: first description

				Second Item::

				second description
				""");
		assertEquals("""
				<dl>\
				<dt class="hdlist1">First Item</dt>\
				<dd>first description</dd>\
				<dt class="hdlist1">Second Item</dt>\
				<dd>second description</dd>\
				</dl>""", html);
	}

	@Test
	public void testDefinitionListNested() {
		String html = parseToHtml("""
				First:: description

				Sub First 1::: description
				Sub First 2::: description
				Second:: description
				""");
		assertEquals("""
				<dl>\
				<dt class="hdlist1">First</dt>\
				<dd>description\
				<dl>\
				<dt class="hdlist1">Sub First 1</dt>\
				<dd>description</dd>\
				<dt class="hdlist1">Sub First 2</dt>\
				<dd>description</dd>\
				</dl></dd>\
				<dt class="hdlist1">Second</dt>\
				<dd>description</dd>\
				</dl>""", html);
	}

	@Test
	public void testDefinitionListNestedStopped() {
		String html = parseToHtml("""
				First:: description

				Sub First 1::: description
				"""
				);
		assertEquals("""
				<dl>\
				<dt class="hdlist1">First</dt>\
				<dd>description\
				<dl>\
				<dt class="hdlist1">Sub First 1</dt>\
				<dd>description</dd>\
				</dl></dd>\
				</dl>""", html);
	}

	@Test
	public void testDefinitionListNestedIncomplete() {
		String html = parseToHtml("""
				First:: description

				Sub First 1::: description
				Second::""");
		assertEquals("""
				<dl>\
				<dt class="hdlist1">First</dt>\
				<dd>description\
				<dl>\
				<dt class="hdlist1">Sub First 1</dt>\
				<dd>description</dd>\
				</dl></dd>\
				<dt class="hdlist1">Second</dt>\
				</dl>""", html);
	}

	@Test
	public void testDefinitionListTextRanges() {
		List<Event> events = parseToEvents("""
				First:: description

				Sub First 1::: description
				Sub First 2::: description
				Second:: description
				""");

		assertEquals(18, events.size());
		assertBlockRange(events.get(0), BlockType.DEFINITION_LIST, 0, 0);

		assertBlockRange(events.get(1), BlockType.DEFINITION_TERM, 0, 5);
		assertBlockRange(events.get(2), "First", 0, 5);
		assertBlockRange(events.get(3), BlockType.DEFINITION_ITEM, 8, 19);
		assertBlockRange(events.get(4), "description", 8, 19);

		assertBlockRange(events.get(5), BlockType.DEFINITION_LIST, 0, 0);

		assertBlockRange(events.get(6), BlockType.DEFINITION_TERM, 0, 11);
		assertBlockRange(events.get(7), "Sub First 1", 0, 11);
		assertBlockRange(events.get(8), BlockType.DEFINITION_ITEM, 15, 26);
		assertBlockRange(events.get(9), "description", 15, 26);

		assertBlockRange(events.get(10), BlockType.DEFINITION_TERM, 0, 11);
		assertBlockRange(events.get(11), "Sub First 2", 0, 11);
		assertBlockRange(events.get(12), BlockType.DEFINITION_ITEM, 15, 26);
		assertBlockRange(events.get(13), "description", 15, 26);

		assertBlockRange(events.get(14), BlockType.DEFINITION_TERM, 0, 6);
		assertBlockRange(events.get(15), "Second", 0, 6);
		assertBlockRange(events.get(16), BlockType.DEFINITION_ITEM, 9, 20);
		assertBlockRange(events.get(17), "description", 9, 20);

	}
}
