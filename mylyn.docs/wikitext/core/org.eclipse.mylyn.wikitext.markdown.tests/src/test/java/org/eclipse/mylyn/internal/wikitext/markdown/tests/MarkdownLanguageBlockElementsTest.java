/*******************************************************************************
 * Copyright (c) 2012, 2024 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.toolkit.RecordingDocumentBuilder.Event;
import org.junit.Test;

/**
 * Tests for Markdown block elements. Follows specification at
 * <a>http://daringfireball.net/projects/markdown/syntax#block</a>.
 *
 * @author Stefan Seelmann
 */
public class MarkdownLanguageBlockElementsTest extends MarkdownLanguageTestBase {

	/*
	 * Paragraphs and Line Breaks. A paragraph is simply one or more consecutive lines of text, separated by one or more
	 * blank lines. (A blank line is any line that looks like a blank line â€” a line containing nothing but spaces or
	 * tabs is considered blank.) Normal paragraphs should not be indented with spaces or tabs.
	 */@Test
	public void testParagraphWithOneLine() {
		String markup = "a paragraph";
		String expectedHtml = "<p>a paragraph</p>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testParagraphWithMulitpleLines() {
		String markup = "a paragraph\nwith multiple\nlines";
		String expectedHtml = "<p>a paragraph\nwith multiple\nlines</p>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testParagraphsSeparatedBySingleBlankLine() {
		String markup = "a paragraph\n\nanother paragraph\n";
		String expectedHtml = "<p>a paragraph</p><p>another paragraph</p>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testParagraphsSeparatedByMulitpleBlankLines() {
		String markup = "a paragraph\n\n\nanother paragraph\n\n";
		String expectedHtml = "<p>a paragraph</p><p>another paragraph</p>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testParagraphsSeparatedByMulitpleBlankLinesWithSpacesAndTabs() {
		String markup = "a paragraph\n \n\t\nanother paragraph";
		String expectedHtml = "<p>a paragraph</p><p>another paragraph</p>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * When you do want to insert a <br />
	 * break tag using Markdown, you end a line with two or more spaces, then type return.
	 */@Test
	public void testLineBreakInParagraph() {
		String markup = "line  1  \nline  2    \nline  3";
		String expectedHtml = "<p>line  1<br/>\nline  2<br/>\nline  3</p>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Blockquotes. Markdown uses email-style > characters for blockquoting. It looks best if you hard wrap the text and
	 * put a > before every line.
	 */@Test
	public void testBlockquoteWithQuoteCharInEachLine() {
		String markup = "> Lorem ipsum dolor sit amet, \n> consetetur adipisici elit.";
		String expectedHtml = "<blockquote><p>Lorem ipsum dolor sit amet, \nconsetetur adipisici elit.</p></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Markdown allows you to be lazy and only put the > before the first line of a hard-wrapped paragraph.
	 */@Test
	public void testBlockquoteWithSingleQuoteChar() {
		String markup = "> Lorem ipsum dolor sit amet, \nconsetetur adipisici elit.";
		String expectedHtml = "<blockquote><p>Lorem ipsum dolor sit amet, \nconsetetur adipisici elit.</p></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Blockquotes can be nested (i.e. a blockquote-in-a-blockquote) by adding additional levels of >.
	 */@Test
	public void testNestedBlockquotesTwoLevels() {
		String markup = "> A1\n>\n> > B1\n> > B2\n>\n> A2";
		String expectedHtml = "<blockquote><p>A1</p><blockquote><p>B1\nB2</p></blockquote><p>A2</p></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testNestedBlockquotesThreeLevels() {
		String markup = "> A1\n>\n> > B1\n> >\n> > > C1\n>\n> A2";
		String expectedHtml = "<blockquote><p>A1</p><blockquote><p>B1</p><blockquote><p>C1</p></blockquote></blockquote><p>A2</p></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Blockquotes can contain other Markdown elements, including headers, lists, and code blocks.
	 */@Test
	public void testBlockquotesContainingParagraphs() {
		String markup = ">a\n>b\n>\n>c";
		String expectedHtml = "<blockquote><p>a\nb</p><p>c</p></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testBlockquotesContainingHeader() {
		String markup = ">#H1";
		String expectedHtml = "<blockquote><h1 id=\"h1\">H1</h1></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testBlockquotesContainingUnderlinedHeader1() {
		String markup = ">H1\n>===";
		String expectedHtml = "<blockquote><h1 id=\"h1\">H1</h1></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testBlockquotesContainingUnderlinedHeader2() {
		String markup = ">H2\n>---";
		String expectedHtml = "<blockquote><h2 id=\"h2\">H2</h2></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testBlockquotesContainingInlineLink() {
		String markup = ">[Link](http://www.example.com)";
		String expectedHtml = "<blockquote><p><a href=\"http://www.example.com\">Link</a></p></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testBlockquotesContainingReferenceLink() {
		String markup = ">[Link][link]\n>\n>[link]: http://www.example.com";
		String expectedHtml = "<blockquote><p><a href=\"http://www.example.com\">Link</a></p></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testBlockquotesContainingHorizontalRule() {
		String markup = ">---";
		String expectedHtml = "<blockquote><hr/></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testBlockquotesContainingHorizontalRuleIsNotInterpretedAsUnderlinedHeader() {
		String markup = ">No H2.\n>\n>---";
		String expectedHtml = "<blockquote><p>No H2.</p><hr/></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testBlockquotesContainingCodeBlock() {
		String markup = ">     code\n>     block";
		String expectedHtml = "<blockquote><pre><code>code\nblock</code></pre></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testBlockquotesContainingInlineHTML() {
		String markup = "> <input type=\"button\" value=\"Click\"/>";
		String expectedHtml = "<blockquote><input type=\"button\" value=\"Click\"/></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testBlockquotesContainingList() {
		String markup = "> * Black\n> * White";
		String expectedHtml = "<blockquote><ul><li>Black</li><li>White</li></ul></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testBlockquotesContainingListWithWrappedItem() {
		String markup = "> * Wrapped\n    line\n> * Next\nitem";
		String expectedHtml = "<blockquote><ul><li>Wrapped\n    line</li><li>Next\nitem</li></ul></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testBlockquoteSimple() {
		String markup = "> a\n> b";
		String expectedHtml = "<blockquote><p>a\nb</p></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Unordered lists use asterisks, pluses, and hyphens - interchangably - as list markers.
	 */@Test
	public void testUnorderedListUsingAsteriskMarker() {
		String markup = "*   Red\n*   Green\n*   Blue";
		String expectedHtml = "<ul><li>Red</li><li>Green</li><li>Blue</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testUnorderedListUsingPlusMarkers() {
		String markup = "+   Red\n+   Green\n+   Blue";
		String expectedHtml = "<ul><li>Red</li><li>Green</li><li>Blue</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testUnorderedListUsingHyphenMarkers() {
		String markup = "-   Red\n-   Green\n-   Blue";
		String expectedHtml = "<ul><li>Red</li><li>Green</li><li>Blue</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testUnorderedListUsingMixedMarkers() {
		String markup = "*   Red\n-   Green\n+   Blue";
		String expectedHtml = "<ul><li>Red</li><li>Green</li><li>Blue</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testUnorderedListWithCodeBlockAfterwards() {
		String markup = "*   Red\n\n\n    should be code\n";
		String expectedHtml = "<ul><li>Red</li></ul><pre><code>should be code</code></pre>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testUnorderedListWithParagraphAfterwards() {
		String markup = "*   first item\n\nThis is not a list anymore";
		String expectedHtml = "<ul><li>first item</li></ul><p>This is not a list anymore</p>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Unordered lists, that are nested
	 */@Test
	public void testUnorderedListWhichIsNested() {
		String markup = "*   Item 1\n    *   Item 1.1";
		String expectedHtml = "<ul><li>Item 1</li><ul><li>Item 1.1</li></ul></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testUnorderedListTabWhichIsNested() {
		String markup = "*   Item 1\n\t*   Item 1.1";
		String expectedHtml = "<ul><li>Item 1</li><ul><li>Item 1.1</li></ul></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testUnorderedListWhichIsNestedUsingMixedMarkers() {
		String markup = "*   Item 1\n    +   Item 1.1\n        -   Item 1.1.1\n    *   Item 1.2\n*   Item 2";
		String expectedHtml = "<ul><li>Item 1</li><ul><li>Item 1.1</li><ul><li>Item 1.1.1</li></ul>"
				+ "<li>Item 1.2</li></ul><li>Item 2</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testOrderedNestedListUsingSequentialNumbers() {
		String markup = "1.  Item 1\n    1. Item 1.1";
		String expectedHtml = "<ol><li>Item 1</li><ol><li>Item 1.1</li></ol></ol>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testOrderedNestedListUsingSameNumbers() {
		String markup = "1.  Item 1\n    1. Item 1.1\n        1. Item 1.1.1\n    1. Item 1.2\n1. Item 2";
		String expectedHtml = "<ol><li>Item 1</li><ol><li>Item 1.1</li><ol><li>Item 1.1.1</li></ol>"
				+ "<li>Item 1.2</li></ol><li>Item 2</li></ol>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testNestedListsMixingUnorderedAndOrdered() {
		String markup = "*   Item 1\n    1. Item 1.1\n        * Item 1.1.1";
		String expectedHtml = "<ul><li>Item 1</li><ol><li>Item 1.1</li><ul><li>Item 1.1.1</li></ul></ol></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testUnorderedListUsingMultipleParagraphs() {
		String markup = "*   This is a list item with two paragraphs.\n" + "\n"
				+ "    This is the second paragraph in the list item. You're\n"
				+ "only required to indent the first line. Lorem ipsum dolor\n"
				+ "sit amet, consectetuer adipiscing elit.";
		String expectedHtml = "<ul><li>This is a list item with two paragraphs.\n"
				+ "<p>This is the second paragraph in the list item. You're\n"
				+ "only required to indent the first line. Lorem ipsum dolor\n"
				+ "sit amet, consectetuer adipiscing elit.</p></li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testOrderListUsingMultipleParagraphs() {
		String markup = "1.  This is a list item with two paragraphs. Lorem ipsum dolor\n"
				+ "    sit amet, consectetuer adipiscing elit. Aliquam hendrerit\n" + "    mi posuere lectus.\n\n" // end of paragraph
				+ "    Vestibulum enim wisi, viverra nec, fringilla in, laoreet\n"
				+ "    vitae, risus. Donec sit amet nisl. Aliquam semper ipsum\n" + "    sit amet velit.\n\n" // end of paragraph and list item
				+ "2.  Suspendisse id sem consectetuer libero luctus adipiscing.\n\n"
				+ "3.  Third Item with an empty line separated.";
		String expectedHtml = "<ol><li>This is a list item with two paragraphs. Lorem ipsum dolor\n"
				+ "sit amet, consectetuer adipiscing elit. Aliquam hendrerit\n" + "mi posuere lectus.\n"
				+ "<p>Vestibulum enim wisi, viverra nec, fringilla in, laoreet\n"
				+ "vitae, risus. Donec sit amet nisl. Aliquam semper ipsum\n" + "sit amet velit.</p></li>"
				+ "<li>Suspendisse id sem consectetuer libero luctus adipiscing.</li>"
				+ "<li>Third Item with an empty line separated.</li></ol>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Ordered lists use numbers followed by periods.
	 */@Test
	public void testOrderedListUsingSequentialNumbers() {
		String markup = "1.  Bird\n2.  McHale\n3.  Parish";
		String expectedHtml = "<ol><li>Bird</li><li>McHale</li><li>Parish</li></ol>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * It's important to note that the actual numbers you use to mark the list
	 * have no effect on the HTML output Markdown produces.
	 */
	@Test
	public void testOrderedListUsingSameNumbers() {
		String markup = "1.  Bird\n1.  McHale\n1.  Parish";
		String expectedHtml = "<ol><li>Bird</li><li>McHale</li><li>Parish</li></ol>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * List markers typically start at the left margin, but may be indented by up to three spaces.
	 */
	@Test
	public void testListMarkersIndentedBySpaces() {
		String markup = " * Red\n  * Green\n   * Blue";
		String expectedHtml = "<ul><li>Red</li><li>Green</li><li>Blue</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testListMarkersIndentedByMoreThanThreeSpacesIsNotRecognizedAsList() {
		String markup = "    * Red\n     * Green\n      * Blue";
		String expectedHtml = "<pre><code>* Red\n * Green\n  * Blue</code></pre>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * List markers must be followed by one or more spaces or a tab.
	 */
	@Test
	public void testListMarkersFollowedBySpaces() {
		String markup = "* Red\n*   Green\n*     Blue";
		String expectedHtml = "<ul><li>Red</li><li>Green</li><li>Blue</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testListMarkersFollowedByTab() {
		String markup = "1.\tBird\n1.\tMcHale\n1.\tParish";
		String expectedHtml = "<ol><li>Bird</li><li>McHale</li><li>Parish</li></ol>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testListMarkersNotFollowedBySpaceOrTabIsNotRecognizedAsList() {
		String markup = "*Red\n*Green\n*Blue";
		String expectedHtml = "<p>*Red\n*Green\n*Blue</p>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * To make lists look nice, you can wrap items with hanging indents.
	 */
	@Test
	public void testListWithWrappedItemAndHangingIndents() {
		String markup = "* Lorem ipsum\n  sit amet.\n* Donec sit\n  amet nisl.";
		String expectedHtml = "<ul><li>Lorem ipsum\nsit amet.</li><li>Donec sit\namet nisl.</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * But if you want to be lazy, you don't have to.
	 */
	@Test
	public void testListWithWrappedItemAndNoHangingIndents() {
		String markup = "* Lorem ipsum\nsit amet.\n* Donec sit\namet nisl.";
		String expectedHtml = "<ul><li>Lorem ipsum\nsit amet.</li><li>Donec sit\namet nisl.</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testListContentOffsets() {
		String markup = "  * Lorem ipsum *sit* amet.";
		List<Event> events = parseToEvents(markup);

		Event spanEvent = findEvent(events, SpanType.EMPHASIS);
		assertEquals(1, spanEvent.locator.getLineNumber());
		assertEquals(16, spanEvent.locator.getLineCharacterOffset());
		assertEquals(21, spanEvent.locator.getLineSegmentEndOffset());
	}

	@Test
	public void testListIndentationWithMultipleLines() {
		String input = "1. one\n\n2. two\na";
		String expectedHtml = "<ol><li>one</li><li>two\na</li></ol>";
		parseAndAssert(input, expectedHtml);
	}

	/*
	 * Markdown wraps a code block in both pre and code tags. To produce a code block in Markdown, simply indent every
	 * line of the block by at least 4 spaces or 1 tab.
	 */
	@Test
	public void testCodeBlockIndentedByFourSpaces() {
		String markup = "    This is a code block.";
		String expectedHtml = "<pre><code>This is a code block.</code></pre>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testCodeBlockIndentedByOneTab() {
		String markup = "\tThis is a code block.";
		String expectedHtml = "<pre><code>This is a code block.</code></pre>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * One level of indentation - 4 spaces or 1 tab - is removed from each line of the code block.
	 */
	@Test
	public void testCodeBlockMultiLineIndentedByFourSpaces() {
		String markup = "    aaa\n        bbb\n            ccc\n    \n    continue after empty line";
		String expectedHtml = "<pre><code>aaa\n    bbb\n        ccc\n\ncontinue after empty line</code></pre>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testCodeBlockMultiLineIndentedByOneTab() {
		String markup = "\taaa\n\t\tbbb\n\t\t\tccc\n\t\n\tcontinue after empty line";
		String expectedHtml = "<pre><code>aaa\n    bbb\n        ccc\n\ncontinue after empty line</code></pre>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Within a code block, ampersands (&) and angle brackets (< and >) are automatically converted into HTML entities.
	 */
	@Test
	public void testSpecialCharactersAreConvertedInCodeBlock() {
		String markup = "    <div class=\"footer\">\n    &copy; 2004 Foo Bar\n    </div>";
		String expectedHtml = "<pre><code>&lt;div class=\"footer\"&gt;\n&amp;copy; 2004 Foo Bar\n&lt;/div&gt;</code></pre>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Regular Markdown syntax is not processed within code blocks.
	 */
	@Test
	public void testNoProcessingInCodeBlock() {
		String markup = "    ### Header 3\n    Lorem *ipsum*";
		String expectedHtml = "<pre><code>### Header 3\nLorem *ipsum*</code></pre>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Horizontal Rules. You can produce a horizontal rule tag ( hr/ ) by placing three or more hyphens, asterisks, or
	 * underscores on a line by themselves. If you wish, you may use spaces between the hyphens or asterisks.
	 */
	@Test
	public void testHorizontalRulesWithAsterisksAndSpaces() {
		parseAndAssert("* * *", "<hr/>");
	}

	@Test
	public void testHorizontalRulesWithAsterisks() {
		parseAndAssert("***", "<hr/>");
	}

	@Test
	public void testHorizontalRulesWithMoreAsterisks() {
		parseAndAssert("*****", "<hr/>");
	}

	@Test
	public void testHorizontalRulesWithHyphensAndSpaces() {
		parseAndAssert("- - -", "<hr/>");
	}

	@Test
	public void testHorizontalRulesWithHyphens() {
		parseAndAssert("---------------------------------------", "<hr/>");
	}

	@Test
	public void testHorizontalRulesWithUnderscores() {
		parseAndAssert("___", "<hr/>");
	}

	@Test
	public void testParagraphsBrokenByHorizontalRuleBlock() {
		String markup = "a paragraph\nfollowed by a horizontal rule\n---";
		String expectedHtml = "<p>a paragraph\nfollowed by a horizontal rule</p><hr/>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testParagraphsBrokenByHeadingBlock() {
		String markup = "a paragraph\n# A header";
		String expectedHtml = "<p>a paragraph</p><h1 id=\"a-header\">A header</h1>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testParagraphsBrokenByQuoteBlock() {
		String markup = "a paragraph\n> a quote block paragraph";
		String expectedHtml = "<p>a paragraph</p><blockquote><p>a quote block paragraph</p></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testParagraphsBrokenByUListBlock() {
		String markup = "a paragraph\n- a list item";
		String expectedHtml = "<p>a paragraph</p><ul><li>a list item</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testParagraphsBrokenByOListBlock() {
		String markup = "a paragraph\n1. a list item";
		String expectedHtml = "<p>a paragraph</p><ol><li>a list item</li></ol>";
		parseAndAssert(markup, expectedHtml);
	}

}
