/*******************************************************************************
 * Copyright (c) 2012, 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.tests;

import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.builder.RecordingDocumentBuilder.Event;

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
	 */
	public void testParagraphWithOneLine() {
		String markup = "a paragraph";
		String expectedHtml = "<p>a paragraph</p>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testParagraphWithMulitpleLines() {
		String markup = "a paragraph\nwith multiple\nlines";
		String expectedHtml = "<p>a paragraph\nwith multiple\nlines</p>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testParagraphsSeparatedBySingleBlankLine() {
		String markup = "a paragraph\n\nanother paragraph\n";
		String expectedHtml = "<p>a paragraph</p><p>another paragraph</p>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testParagraphsSeparatedByMulitpleBlankLines() {
		String markup = "a paragraph\n\n\nanother paragraph\n\n";
		String expectedHtml = "<p>a paragraph</p><p>another paragraph</p>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testParagraphsSeparatedByMulitpleBlankLinesWithSpacesAndTabs() {
		String markup = "a paragraph\n \n\t\nanother paragraph";
		String expectedHtml = "<p>a paragraph</p><p>another paragraph</p>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * When you do want to insert a <br />
	 * break tag using Markdown, you end a line with two or more spaces, then type return.
	 */
	public void testLineBreakInParagraph() {
		String markup = "line  1  \nline  2    \nline  3";
		String expectedHtml = "<p>line  1<br/>\nline  2<br/>\nline  3</p>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Blockquotes. Markdown uses email-style > characters for blockquoting. It looks best if you hard wrap the text and
	 * put a > before every line.
	 */
	public void testBlockquoteWithQuoteCharInEachLine() {
		String markup = "> Lorem ipsum dolor sit amet, \n> consetetur adipisici elit.";
		String expectedHtml = "<blockquote><p>Lorem ipsum dolor sit amet, \nconsetetur adipisici elit.</p></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Markdown allows you to be lazy and only put the > before the first line of a hard-wrapped paragraph.
	 */
	public void testBlockquoteWithSingleQuoteChar() {
		String markup = "> Lorem ipsum dolor sit amet, \nconsetetur adipisici elit.";
		String expectedHtml = "<blockquote><p>Lorem ipsum dolor sit amet, \nconsetetur adipisici elit.</p></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Blockquotes can be nested (i.e. a blockquote-in-a-blockquote) by adding additional levels of >.
	 */
	public void testNestedBlockquotesTwoLevels() {
		String markup = "> A1\n>\n> > B1\n> > B2\n>\n> A2";
		String expectedHtml = "<blockquote><p>A1</p><blockquote><p>B1\nB2</p></blockquote><p>A2</p></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testNestedBlockquotesThreeLevels() {
		String markup = "> A1\n>\n> > B1\n> >\n> > > C1\n>\n> A2";
		String expectedHtml = "<blockquote><p>A1</p><blockquote><p>B1</p><blockquote><p>C1</p></blockquote></blockquote><p>A2</p></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Blockquotes can contain other Markdown elements, including headers, lists, and code blocks.
	 */
	public void testBlockquotesContainingParagraphs() {
		String markup = ">a\n>b\n>\n>c";
		String expectedHtml = "<blockquote><p>a\nb</p><p>c</p></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testBlockquotesContainingHeader() {
		String markup = ">#H1";
		String expectedHtml = "<blockquote><h1 id=\"h1\">H1</h1></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testBlockquotesContainingUnderlinedHeader1() {
		String markup = ">H1\n>===";
		String expectedHtml = "<blockquote><h1 id=\"h1\">H1</h1></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testBlockquotesContainingUnderlinedHeader2() {
		String markup = ">H2\n>---";
		String expectedHtml = "<blockquote><h2 id=\"h2\">H2</h2></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testBlockquotesContainingInlineLink() {
		String markup = ">[Link](http://www.example.com)";
		String expectedHtml = "<blockquote><p><a href=\"http://www.example.com\">Link</a></p></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testBlockquotesContainingReferenceLink() {
		String markup = ">[Link][link]\n>\n>[link]: http://www.example.com";
		String expectedHtml = "<blockquote><p><a href=\"http://www.example.com\">Link</a></p></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testBlockquotesContainingHorizontalRule() {
		String markup = ">---";
		String expectedHtml = "<blockquote><hr/></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testBlockquotesContainingHorizontalRuleIsNotInterpretedAsUnderlinedHeader() {
		String markup = ">No H2.\n>\n>---";
		String expectedHtml = "<blockquote><p>No H2.</p><hr/></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testBlockquotesContainingCodeBlock() {
		String markup = ">     code\n>     block";
		String expectedHtml = "<blockquote><pre><code>code\nblock</code></pre></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testBlockquotesContainingInlineHTML() {
		String markup = "> <input type=\"button\" value=\"Click\"/>";
		String expectedHtml = "<blockquote><input type=\"button\" value=\"Click\"/></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testBlockquotesContainingList() {
		String markup = "> * Black\n> * White";
		String expectedHtml = "<blockquote><ul><li>Black</li><li>White</li></ul></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testBlockquotesContainingListWithWrappedItem() {
		String markup = "> * Wrapped\n    line\n> * Next\nitem";
		String expectedHtml = "<blockquote><ul><li>Wrapped\n    line</li><li>Next\nitem</li></ul></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testBlockquoteSimple() {
		String markup = "> a\n> b";
		String expectedHtml = "<blockquote><p>a\nb</p></blockquote>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Unordered lists use asterisks, pluses, and hyphens - interchangably - as list markers.
	 */
	public void testUnorderedListUsingAsteriskMarker() {
		String markup = "*   Red\n*   Green\n*   Blue";
		String expectedHtml = "<ul><li>Red</li><li>Green</li><li>Blue</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testUnorderedListUsingPlusMarkers() {
		String markup = "+   Red\n+   Green\n+   Blue";
		String expectedHtml = "<ul><li>Red</li><li>Green</li><li>Blue</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testUnorderedListUsingHyphenMarkers() {
		String markup = "-   Red\n-   Green\n-   Blue";
		String expectedHtml = "<ul><li>Red</li><li>Green</li><li>Blue</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testUnorderedListUsingMixedMarkers() {
		String markup = "*   Red\n-   Green\n+   Blue";
		String expectedHtml = "<ul><li>Red</li><li>Green</li><li>Blue</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Ordered lists use numbers followed by periods.
	 */
	public void testOrderedListUsingSequentialNumbers() {
		String markup = "1.  Bird\n2.  McHale\n3.  Parish";
		String expectedHtml = "<ol><li>Bird</li><li>McHale</li><li>Parish</li></ol>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * It's important to note that the actual numbers you use to mark the list
	 * have no effect on the HTML output Markdown produces.
	 */
	public void testOrderedListUsingSameNumbers() {
		String markup = "1.  Bird\n1.  McHale\n1.  Parish";
		String expectedHtml = "<ol><li>Bird</li><li>McHale</li><li>Parish</li></ol>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * List markers typically start at the left margin, but may be indented by up to three spaces.
	 */
	public void testListMarkersIndentedBySpaces() {
		String markup = " * Red\n  * Green\n   * Blue";
		String expectedHtml = "<ul><li>Red</li><li>Green</li><li>Blue</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testListMarkersIndentedByMoreThanThreeSpacesIsNotRecognizedAsList() {
		String markup = "    * Red\n     * Green\n      * Blue";
		String expectedHtml = "<pre><code>* Red\n * Green\n  * Blue</code></pre>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * List markers must be followed by one or more spaces or a tab.
	 */
	public void testListMarkersFollowedBySpaces() {
		String markup = "* Red\n*   Green\n*     Blue";
		String expectedHtml = "<ul><li>Red</li><li>Green</li><li>Blue</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testListMarkersFollowedByTab() {
		String markup = "1.\tBird\n1.\tMcHale\n1.\tParish";
		String expectedHtml = "<ol><li>Bird</li><li>McHale</li><li>Parish</li></ol>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testListMarkersNotFollowedBySpaceOrTabIsNotRecognizedAsList() {
		String markup = "*Red\n*Green\n*Blue";
		String expectedHtml = "<p>*Red\n*Green\n*Blue</p>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * To make lists look nice, you can wrap items with hanging indents.
	 */
	public void testListWithWrappedItemAndHangingIndents() {
		String markup = "* Lorem ipsum\n  sit amet.\n* Donec sit\n  amet nisl.";
		String expectedHtml = "<ul><li>Lorem ipsum\n  sit amet.</li><li>Donec sit\n  amet nisl.</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * But if you want to be lazy, you don't have to.
	 */
	public void testListWithWrappedItemAndNoHangingIndents() {
		String markup = "* Lorem ipsum\nsit amet.\n* Donec sit\namet nisl.";
		String expectedHtml = "<ul><li>Lorem ipsum\nsit amet.</li><li>Donec sit\namet nisl.</li></ul>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testListContentOffsets() {
		String markup = "  * Lorem ipsum *sit* amet.";
		List<Event> events = parseToEvents(markup);

		Event spanEvent = findEvent(events, SpanType.EMPHASIS);
		assertEquals(1, spanEvent.locator.getLineNumber());
		assertEquals(16, spanEvent.locator.getLineCharacterOffset());
		assertEquals(21, spanEvent.locator.getLineSegmentEndOffset());
	}

	/*
	 * Markdown wraps a code block in both pre and code tags. To produce a code block in Markdown, simply indent every
	 * line of the block by at least 4 spaces or 1 tab.
	 */
	public void testCodeBlockIndentedByFourSpaces() {
		String markup = "    This is a code block.";
		String expectedHtml = "<pre><code>This is a code block.</code></pre>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testCodeBlockIndentedByOneTab() {
		String markup = "\tThis is a code block.";
		String expectedHtml = "<pre><code>This is a code block.</code></pre>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * One level of indentation - 4 spaces or 1 tab - is removed from each line of the code block.
	 */
	public void testCodeBlockMultiLineIndentedByFourSpaces() {
		String markup = "    aaa\n        bbb\n            ccc\n    \n    continue after empty line";
		String expectedHtml = "<pre><code>aaa\n    bbb\n        ccc\n\ncontinue after empty line</code></pre>";
		parseAndAssert(markup, expectedHtml);
	}

	public void testCodeBlockMultiLineIndentedByOneTab() {
		String markup = "\taaa\n\t\tbbb\n\t\t\tccc\n\t\n\tcontinue after empty line";
		String expectedHtml = "<pre><code>aaa\n    bbb\n        ccc\n\ncontinue after empty line</code></pre>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Within a code block, ampersands (&) and angle brackets (< and >) are automatically converted into HTML entities.
	 */
	public void testSpecialCharactersAreConvertedInCodeBlock() {
		String markup = "    <div class=\"footer\">\n    &copy; 2004 Foo Bar\n    </div>";
		String expectedHtml = "<pre><code>&lt;div class=\"footer\"&gt;\n&amp;copy; 2004 Foo Bar\n&lt;/div&gt;</code></pre>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Regular Markdown syntax is not processed within code blocks.
	 */
	public void testNoProcessingInCodeBlock() {
		String markup = "    ### Header 3\n    Lorem *ipsum*";
		String expectedHtml = "<pre><code>### Header 3\nLorem *ipsum*</code></pre>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Horizontal Rules. You can produce a horizontal rule tag ( hr/ ) by placing three or more hyphens, asterisks, or
	 * underscores on a line by themselves. If you wish, you may use spaces between the hyphens or asterisks.
	 */
	public void testHorizontalRulesWithAsterisksAndSpaces() {
		parseAndAssert("* * *", "<hr/>");
	}

	public void testHorizontalRulesWithAsterisks() {
		parseAndAssert("***", "<hr/>");
	}

	public void testHorizontalRulesWithMoreAsterisks() {
		parseAndAssert("*****", "<hr/>");
	}

	public void testHorizontalRulesWithHyphensAndSpaces() {
		parseAndAssert("- - -", "<hr/>");
	}

	public void testHorizontalRulesWithHyphens() {
		parseAndAssert("---------------------------------------", "<hr/>");
	}

	public void testHorizontalRulesWithUnderscores() {
		parseAndAssert("___", "<hr/>");
	}
}
