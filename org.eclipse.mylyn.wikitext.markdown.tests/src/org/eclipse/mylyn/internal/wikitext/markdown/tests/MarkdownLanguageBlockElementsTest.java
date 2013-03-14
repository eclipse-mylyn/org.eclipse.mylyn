/*******************************************************************************
 * Copyright (c) 2012 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.tests;

import org.eclipse.mylyn.wikitext.tests.TestUtil;

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
		String html = parseToHtml("a paragraph");
		TestUtil.println("HTML: " + html);
		assertEquals("<p>a paragraph</p>\n", html);
	}

	public void testParagraphWithMulitpleLines() {
		String html = parseToHtml("a paragraph\nwith multiple\nlines");
		TestUtil.println("HTML: " + html);
		assertEquals("<p>a paragraph\nwith multiple\nlines</p>\n", html);
	}

	public void testParagraphsSeparatedBySingleBlankLine() {
		String html = parseToHtml("a paragraph\n\nanother paragraph\n\n");
		TestUtil.println("HTML: " + html);
		assertEquals("<p>a paragraph</p>\n<p>another paragraph</p>\n", html);
	}

	public void testParagraphsSeparatedByMulitpleBlankLines() {
		String html = parseToHtml("a paragraph\n\n\nanother paragraph\n\n\n");
		TestUtil.println("HTML: " + html);
		assertEquals("<p>a paragraph</p>\n<p>another paragraph</p>\n", html);
	}

	public void testParagraphsSeparatedByMulitpleBlankLinesWithSpacesAndTabs() {
		String html = parseToHtml("a paragraph\n \n\t\nanother paragraph");
		TestUtil.println("HTML: " + html);
		assertEquals("<p>a paragraph</p>\n<p>another paragraph</p>\n", html);
	}

	/*
	 * When you do want to insert a <br />
	 * break tag using Markdown, you end a line with two or more spaces, then type return.
	 */
	public void testLineBreakInParagraph() {
		String html = parseToHtml("line  1  \nline  2    \nline  3");
		TestUtil.println("HTML: " + html);
		assertEquals("<p>line  1<br/>\nline  2<br/>\nline  3</p>\n", html);
	}

	/*
	 * Headers. Atx-style headers use 1-6 hash characters at the start of the line, corresponding to header levels 1-6.
	 */
	public void testAtxStyleHeaderH1() {
		String h1 = parseToHtml("# This is an H1");
		TestUtil.println("HTML: " + h1);
		assertEquals("<h1>This is an H1</h1>", h1);
	}

	public void testAtxStyleHeaderH2() {
		String h2 = parseToHtml("## This is an H2");
		TestUtil.println("HTML: " + h2);
		assertEquals("<h2>This is an H2</h2>", h2);
	}

	public void testAtxStyleHeaderH3() {
		String h3 = parseToHtml("### This is an H3");
		TestUtil.println("HTML: " + h3);
		assertEquals("<h3>This is an H3</h3>", h3);
	}

	public void testAtxStyleHeaderH4() {
		String h4 = parseToHtml("#### This is an H4");
		TestUtil.println("HTML: " + h4);
		assertEquals("<h4>This is an H4</h4>", h4);
	}

	public void testAtxStyleHeaderH5() {
		String h5 = parseToHtml("##### This is an H5");
		TestUtil.println("HTML: " + h5);
		assertEquals("<h5>This is an H5</h5>", h5);
	}

	public void testAtxStyleHeaderH6() {
		String h6 = parseToHtml("###### This is an H6");
		TestUtil.println("HTML: " + h6);
		assertEquals("<h6>This is an H6</h6>", h6);
	}

	/*
	 * Optionally, you may "close" atx-style headers. This is purely cosmetic - you can use this if you think it looks
	 * better. The closing hashes don't even need to match the number of hashes used to open the header. (The number of
	 * opening hashes determines the header level.)
	 */
	public void testClosedAtxStyleHeaderH1() {
		String h1 = parseToHtml("# This is an H1 #");
		TestUtil.println("HTML: " + h1);
		assertEquals("<h1>This is an H1</h1>", h1);
	}

	public void testClosedAtxStyleHeaderH2() {
		String h2 = parseToHtml("## This is an H2 ##");
		TestUtil.println("HTML: " + h2);
		assertEquals("<h2>This is an H2</h2>", h2);
	}

	public void testClosedAtxStyleHeaderH3() {
		String h3 = parseToHtml("### This is an H3 ###");
		TestUtil.println("HTML: " + h3);
		assertEquals("<h3>This is an H3</h3>", h3);
	}

	public void testClosedAtxStyleHeaderH4() {
		String h4 = parseToHtml("#### This is an H4 ####");
		TestUtil.println("HTML: " + h4);
		assertEquals("<h4>This is an H4</h4>", h4);
	}

	public void testClosedAtxStyleHeaderH5() {
		String h5 = parseToHtml("##### This is an H5 #####");
		TestUtil.println("HTML: " + h5);
		assertEquals("<h5>This is an H5</h5>", h5);
	}

	public void testClosedAtxStyleHeaderH6() {
		String h6 = parseToHtml("###### This is an H6 ######");
		TestUtil.println("HTML: " + h6);
		assertEquals("<h6>This is an H6</h6>", h6);
	}

	public void testClosedAtxStyleHeaderWithMoreClosingHashes() {
		String h1 = parseToHtml("# This is an H1 ################################");
		TestUtil.println("HTML: " + h1);
		assertEquals("<h1>This is an H1</h1>", h1);
	}

	public void testClosedAtxStyleHeaderWithLessCosingHashes() {
		String h6 = parseToHtml("###### This is an H6 #");
		TestUtil.println("HTML: " + h6);
		assertEquals("<h6>This is an H6</h6>", h6);
	}

	/*
	 * Setext-style headers are "underlined" using equal signs (for first-level headers) and dashes (for second-level
	 * headers). Any number of underlining ='s or -'s will work.
	 */
	public void testUnderlinedHeaderH1() {
		String h1 = parseToHtml("This is an H1\n============");
		TestUtil.println("HTML: " + h1);
		assertEquals("<h1>This is an H1</h1>", h1);
	}

	public void testUnderlinedHeaderH2() {
		String h2 = parseToHtml("This is an H2\n------------");
		TestUtil.println("HTML: " + h2);
		assertEquals("<h2>This is an H2</h2>", h2);
	}

	public void testSingleCharUnderlinedHeaderH1() {
		String h1 = parseToHtml("This is an H1\n= ");
		TestUtil.println("HTML: " + h1);
		assertEquals("<h1>This is an H1</h1>", h1);
	}

	public void testSingleCharUnderlinedHeaderH2() {
		String h2 = parseToHtml("This is an H2\n- ");
		TestUtil.println("HTML: " + h2);
		assertEquals("<h2>This is an H2</h2>", h2);
	}

	/*
	 * Blockquotes. Markdown uses email-style > characters for blockquoting. It looks best if you hard wrap the text and
	 * put a > before every line.
	 */
	public void testBlockquoteWithQuoteCharInEachLine() {
		String h1 = parseToHtml("> Lorem ipsum dolor sit amet, \n>  consetetur adipisici elit.\n");
		TestUtil.println("HTML: " + h1);
		assertEquals("<blockquote><p>Lorem ipsum dolor sit amet, \nconsetetur adipisici elit.</p>\n</blockquote>", h1);
	}

	/*
	 * Markdown allows you to be lazy and only put the > before the first line of a hard-wrapped paragraph.
	 */
	public void testBlockquoteWithSingleQuoteChar() {
		String h1 = parseToHtml("> Lorem ipsum dolor sit amet, \nconsetetur adipisici elit.\n");
		TestUtil.println("HTML: " + h1);
		assertEquals("<blockquote><p>Lorem ipsum dolor sit amet, \nconsetetur adipisici elit.</p>\n</blockquote>", h1);
	}

	/*
	 * Blockquotes can be nested (i.e. a blockquote-in-a-blockquote) by adding additional levels of >.
	 */
	public void testNestedBlockquotes() {
		String h1 = parseToHtml(">A1\n>>B1\n>A2\n");
		TestUtil.println("HTML: " + h1);
		assertEquals("<blockquote><p>A1</p>\n<blockquote><p>B1</p>\n</blockquote><p>A2</p>\n</blockquote>", h1);
	}

	/*
	 * Blockquotes can contain other Markdown elements, including headers, lists, and code blocks.
	 */
	public void testBlockquotesWithOtherElements() {
		String h1 = parseToHtml(">#H1");
		TestUtil.println("HTML: " + h1);
		assertEquals("<blockquote><h1>H1</h1></blockquote>", h1);
	}

	/*
	 * Markdown wraps a code block in both pre and code tags. To produce a code block in Markdown, simply indent every
	 * line of the block by at least 4 spaces or 1 tab.
	 */
	public void testCodeBlockIndentedByFourSpaces() {
		String html = parseToHtml("    This is a code block.");
		TestUtil.println("HTML: " + html);
		assertEquals("<pre><code>This is a code block.</code></pre>", html);
	}

	public void testCodeBlockIndentedByOneTab() {
		String html = parseToHtml("\tThis is a code block.");
		TestUtil.println("HTML: " + html);
		assertEquals("<pre><code>This is a code block.</code></pre>", html);
	}

	/*
	 * One level of indentation - 4 spaces or 1 tab - is removed from each line of the code block.
	 */
	public void testCodeBlockMultiLineIndentedByFourSpaces() {
		String html = parseToHtml("    aaa\n        bbb\n            ccc\n    \n    continue after empty line");
		TestUtil.println("HTML: " + html);
		String expectedHtml = "<pre><code>aaa\n    bbb\n        ccc\n\ncontinue after empty line</code></pre>";
		assertEquals(expectedHtml, html);
	}

	public void testCodeBlockMultiLineIndentedByOneTab() {
		String html = parseToHtml("\taaa\n\t\tbbb\n\t\t\tccc\n\t\n\tcontinue after empty line");
		TestUtil.println("HTML: " + html);
		String expectedHtml = "<pre><code>aaa\n    bbb\n        ccc\n\ncontinue after empty line</code></pre>";
		assertEquals(expectedHtml, html);
	}

	/*
	 * Within a code block, ampersands (&) and angle brackets (< and >) are automatically converted into HTML entities.
	 */
	public void testSpecialCharactersAreConvertedInCodeBlock() {
		String html = parseToHtml("    <div class=\"footer\">\n    &copy; 2004 Foo Bar\n    </div>");
		TestUtil.println("HTML: " + html);
		String exptectedHtml = "<pre><code>&lt;div class=\"footer\"&gt;\n&amp;copy; 2004 Foo Bar\n&lt;/div&gt;</code></pre>";
		assertEquals(exptectedHtml, html);
	}

	/*
	 * Regular Markdown syntax is not processed within code blocks.
	 */
	public void testNoProcessingInCodeBlock() {
		String html = parseToHtml("    ### Header 3\n    Lorem *ipsum*");
		TestUtil.println("HTML: " + html);
		assertEquals("<pre><code>### Header 3\nLorem *ipsum*</code></pre>", html);
	}

	/*
	 * Horizontal Rules. You can produce a horizontal rule tag ( hr/ ) by placing three or more hyphens, asterisks, or
	 * underscores on a line by themselves. If you wish, you may use spaces between the hyphens or asterisks.
	 */
	public void testHorizontalRulesWithAsterisksAndSpaces() {
		String html = parseToHtml("* * *");
		TestUtil.println("HTML: " + html);
		assertEquals("<hr/>", html);
	}

	public void testHorizontalRulesWithAsterisks() {
		String html = parseToHtml("***");
		TestUtil.println("HTML: " + html);
		assertEquals("<hr/>", html);
	}

	public void testHorizontalRulesWithMoreAsterisks() {
		String html = parseToHtml("*****");
		TestUtil.println("HTML: " + html);
		assertEquals("<hr/>", html);
	}

	public void testHorizontalRulesWithHyphensAndSpaces() {
		String html = parseToHtml("- - -");
		TestUtil.println("HTML: " + html);
		assertEquals("<hr/>", html);
	}

	public void testHorizontalRulesWithHyphens() {
		String html = parseToHtml("---------------------------------------");
		TestUtil.println("HTML: " + html);
		assertEquals("<hr/>", html);
	}

	public void testHorizontalRulesWithUnderscores() {
		String html = parseToHtml("___");
		TestUtil.println("HTML: " + html);
		assertEquals("<hr/>", html);
	}
}
