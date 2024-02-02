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

import org.junit.Test;

/**
 * Tests for Markdown overview and miscellaneous. Follows specification at
 * <a>http://daringfireball.net/projects/markdown/syntax#overview</a>. <a>http://daringfireball.net/projects/markdown/syntax#misc</a>.
 *
 * @author Stefan Seelmann
 */
public class MarkdownLanguageMiscellaneousTest extends MarkdownLanguageTestBase {
	@Test
	public void testEmptyLine() {
		parseAndAssert("    ", "");
	}

	/*
	 * Inline HTML. For any markup that is not covered by Markdown's syntax, you simply use HTML itself. There's no need
	 * to preface it or delimit it to indicate that youâ€™re switching from Markdown to HTML; you just use the tags. The
	 * only restrictions are that block-level HTML elements - e.g. div, table,pre, p, etc. - must be separated from
	 * surrounding content by blank lines, and the start and end tags of the block should not be indented with tabs or
	 * spaces. Markdown is smart enough not to add extra (unwanted) p tags around HTML block-level tags.
	 */
	@Test
	public void testInlineHtml() {
		String markup = "aaa\n\n<table>\n <tr>\n  <td>Foo</td>\n </tr>\n</table>\n\nbbb";
		String expectedHtml = "<p>aaa</p><table> <tr>  <td>Foo</td> </tr></table><p>bbb</p>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Note that Markdown formatting syntax is not processed within block-level HTML tags. E.g., you can't use
	 * Markdown-style *emphasis* inside an HTML block.
	 */
	@Test
	public void testNoProcessingWithinInlineHtmlBlockLevelTags() {
		String markup = "<div>*Foo*</div>";
		String expectedHtml = "<div>*Foo*</div>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Span-level HTML tags - e.g. span, cite, or del - can be used anywhere in a Markdown paragraph, list item, or
	 * header. If you want, you can even use HTML tags instead of Markdown formatting; e.g. if you'd prefer to use HTML
	 * a or img tags instead of Markdown's link or image syntax, go right ahead.
	 */
	@Test
	public void testSpanLevelTags() {
		String markup = "Image: <img src=\"image.jpg\">some nice image</img>.";
		String expectedHtml = "<p>Image: <img src=\"image.jpg\">some nice image</img>.</p>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Unlike block-level HTML tags, Markdown syntax is processed within span-level tags.
	 */
	@Test
	public void testProcessingInSpanLevelTags() {
		String markup = "Image: <img src=\"image.jpg\">some **nice** image</img>.";
		String expectedHtml = "<p>Image: <img src=\"image.jpg\">some <strong>nice</strong> image</img>.</p>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Automatic Escaping for Special Characters. Markdown allows you to use these characters naturally, taking care of
	 * all the necessary escaping for you. If you use an ampersand as part of an HTML entity, it remains unchanged;
	 * otherwise it will be translated into &amp;. So, if you want to include a copyright symbol in your article, you
	 * can write: &copy; and Markdown will leave it alone.
	 */
	@Test
	public void testPreserveHtmlEntities() {
		parseAndAssert("&copy; &amp;", "<p>&copy; &amp;</p>");
	}

	/*
	 * But if you write: AT&T Markdown will translate it to: AT&amp;T.
	 */
	@Test
	public void testAmpersandIsEscaped() {
		parseAndAssert("AT&T, a & b", "<p>AT&amp;T, a &amp; b</p>");
	}

	@Test
	public void testAmpersandIsEscapedWithinUrl() {
		String markup = "http://images.google.com/images?num=30&q=larry+bird";
		String expectedHtml = "<p>http://images.google.com/images?num=30&amp;q=larry+bird</p>";
		parseAndAssert(markup, expectedHtml);
	}

	/*
	 * Similarly, because Markdown supports inline HTML, if you use angle brackets as delimiters for HTML tags, Markdown
	 * will treat them as such. But if you write: 4 < 5 Markdown will translate it to: 4 &lt; 5
	 */
	@Test
	public void testLessThanAngleBracketIsEscaped() {
		parseAndAssert("4 < 5", "<p>4 &lt; 5</p>");
	}

	@Test
	public void testGreaterThanAngleBracketIsEscaped() {
		parseAndAssert("6 > 5", "<p>6 &gt; 5</p>");
	}

	/*
	 * Backslash Escapes. Markdown allows you to use backslash escapes to generate literal characters which would otherwise have special
	 * meaning in Markdown's formatting syntax.
	 */
	@Test
	public void testEscapedBackslash() {
		parseAndAssert("\\\\", "<p>\\</p>");
	}

	@Test
	public void testEscapedBacktick() {
		parseAndAssert("\\`", "<p>`</p>");
	}

	@Test
	public void testEscapedAsterisk() {
		parseAndAssert("\\*", "<p>*</p>");
	}

	@Test
	public void testEscapedUnderscore() {
		parseAndAssert("\\_", "<p>_</p>");
	}

	@Test
	public void testEscapedOpeningCurlyBrace() {
		parseAndAssert("\\{", "<p>{</p>");
	}

	@Test
	public void testEscapedClosingCurlyBrace() {
		parseAndAssert("\\}", "<p>}</p>");
	}

	@Test
	public void testEscapedOpeningSquareBracket() {
		parseAndAssert("\\[", "<p>[</p>");
	}

	@Test
	public void testEscapedClosingSquareBracket() {
		parseAndAssert("\\]", "<p>]</p>");
	}

	@Test
	public void testEscapedOpeningParenthesis() {
		parseAndAssert("\\(", "<p>(</p>");
	}

	@Test
	public void testEscapedClosingParenthesis() {
		parseAndAssert("\\)", "<p>)</p>");
	}

	@Test
	public void testEscapedHashMark() {
		parseAndAssert("\\#", "<p>#</p>");
	}

	@Test
	public void testEscapedPlusSign() {
		parseAndAssert("\\+", "<p>+</p>");
	}

	@Test
	public void testEscapedMinusSign() {
		parseAndAssert("\\-", "<p>-</p>");
	}

	@Test
	public void testEscapedDot() {
		parseAndAssert("\\.", "<p>.</p>");
	}

	@Test
	public void testEscapedExclamationMark() {
		parseAndAssert("\\!", "<p>!</p>");
	}

	/*
	 * Markdown supports a shortcut style for creating "automatic" links for URLs and email addresses: simply surround
	 * the URL or email address with angle brackets. What this means is that if you want to show the actual text of a
	 * URL or email address, and also have it be a clickable link, you can do this:
	 */
	@Test
	public void testAutomaticLinksAtBeginOfLine() {
		String markup = "<http://example.com/>";
		String expectedHtml = "<p><a href=\"http://example.com/\">http://example.com/</a></p>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testAutomaticLinksWithinText() {
		String markup = "This <http://example.com/> is an automatic link.";
		String expectedHtml = "<p>This <a href=\"http://example.com/\">http://example.com/</a> is an automatic link.</p>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testNoSpanWithinAutomaticLinks() {
		String markup = "This <http://www.google.de/?q=t_es_t> is an automatic link.";
		String expectedHtml = "<p>This <a href=\"http://www.google.de/?q=t_es_t\">http://www.google.de/?q=t_es_t</a> is an automatic link.</p>";
		parseAndAssert(markup, expectedHtml);
	}

	@Test
	public void testHtmlEntity() {
		parseAndAssert("&copy; more text", "<p>&copy; more text</p>");
		parseAndAssert("more text &copy;", "<p>more text &copy;</p>");
		parseAndAssert("&copy;", "<p>&copy;</p>");
		parseAndAssert("start&copy;end", "<p>start&copy;end</p>");
		parseAndAssert("&#160;&nbsp;&#xa0;&#xA0;", "<p>&#160;&nbsp;&#xa0;&#xA0;</p>");
		parseAndAssert("&pound;", "<p>&pound;</p>");
	}
}
