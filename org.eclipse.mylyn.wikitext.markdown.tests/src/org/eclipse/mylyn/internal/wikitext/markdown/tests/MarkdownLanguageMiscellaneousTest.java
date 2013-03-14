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
 * Tests for Markdown overview and miscellaneous. Follows specification at
 * <a>http://daringfireball.net/projects/markdown/syntax#overview</a>.
 * <a>http://daringfireball.net/projects/markdown/syntax#misc</a>.
 * 
 * @author Stefan Seelmann
 */
public class MarkdownLanguageMiscellaneousTest extends MarkdownLanguageTestBase {

	public void testEmptyLine() {
		String html = parseToHtml("    ");
		TestUtil.println("HTML: " + html);
		assertEquals("", html);
	}

	/*
	 * Inline HTML. For any markup that is not covered by Markdown's syntax, you simply use HTML itself. There's no need
	 * to preface it or delimit it to indicate that youâ€™re switching from Markdown to HTML; you just use the tags. The
	 * only restrictions are that block-level HTML elements - e.g. div, table,pre, p, etc. - must be separated from
	 * surrounding content by blank lines, and the start and end tags of the block should not be indented with tabs or
	 * spaces. Markdown is smart enough not to add extra (unwanted) p tags around HTML block-level tags.
	 */
	public void testInlineHtml() throws Exception {
		String html = parseToHtml("aaa\n\n<table>\n <tr>\n  <td>Foo</td>\n </tr>\n</table>\n\nbbb");
		TestUtil.println("HTML: " + html);
		assertEquals("<p>aaa</p>\n<table>\n <tr>\n  <td>Foo</td>\n </tr>\n</table>\n<p>bbb</p>\n", html);
	}

	/*
	 * Note that Markdown formatting syntax is not processed within block-level HTML tags. E.g., you can't use
	 * Markdown-style *emphasis* inside an HTML block.
	 */
	public void testNoProcessingWithinInlineHtmlBlockLevelTags() throws Exception {
		String html = parseToHtml("<div>*Foo*</div>");
		TestUtil.println("HTML: " + html);
		assertEquals("<div>*Foo*</div>\n", html);
	}

	/*
	 * Span-level HTML tags - e.g. span, cite, or del - can be used anywhere in a Markdown paragraph, list item, or
	 * header. If you want, you can even use HTML tags instead of Markdown formatting; e.g. if you'd prefer to use HTML
	 * a or img tags instead of Markdown's link or image syntax, go right ahead.
	 */
	public void testSpanLevelTags() throws Exception {
		String html = parseToHtml("Image: <img src=\"image.jpg\">some nice image</img>.");
		TestUtil.println("HTML: " + html);
		assertEquals("<p>Image: <img src=\"image.jpg\">some nice image</img>.</p>\n", html);

	}

	/*
	 * Unlike block-level HTML tags, Markdown syntax is processed within span-level tags.
	 */
	public void testProcessingInSpanLevelTags() throws Exception {
		String html = parseToHtml("Image: <img src=\"image.jpg\">some **nice** image</img>.");
		TestUtil.println("HTML: " + html);
		assertEquals("<p>Image: <img src=\"image.jpg\">some <strong>nice</strong> image</img>.</p>\n", html);
	}

	/*
	 * Automatic Escaping for Special Characters. Markdown allows you to use these characters naturally, taking care of
	 * all the necessary escaping for you. If you use an ampersand as part of an HTML entity, it remains unchanged;
	 * otherwise it will be translated into &amp;. So, if you want to include a copyright symbol in your article, you
	 * can write: &copy; and Markdown will leave it alone.
	 */
	public void testPreserveHtmlEntities() {
		String copy = parseToHtml("&copy; &amp;");
		TestUtil.println("HTML: " + copy);
		assertEquals("<p>&copy; &amp;</p>\n", copy);
	}

	/*
	 * But if you write: AT&T Markdown will translate it to: AT&amp;T. 
	 */
	public void testAmpersandIsEscaped() {
		String amp = parseToHtml("AT&T, a & b");
		TestUtil.println("HTML: " + amp);
		assertEquals("<p>AT&amp;T, a &amp; b</p>\n", amp);

		String urlWithAmp = parseToHtml("http://images.google.com/images?num=30&q=larry+bird");
		TestUtil.println("HTML: " + urlWithAmp);
		assertEquals("<p>http://images.google.com/images?num=30&amp;q=larry+bird</p>\n", urlWithAmp);
	}

	/* 
	 * Similarly, because Markdown supports inline HTML, if you use angle brackets as delimiters for HTML tags, Markdown 
	 * will treat them as such. But if you write: 4 < 5 Markdown will translate it to: 4 &lt; 5
	 */
	public void testAngleBracketsAreEscaped() {
		String lt = parseToHtml("4 < 5");
		TestUtil.println("HTML: " + lt);
		assertEquals("<p>4 &lt; 5</p>\n", lt);

		String gt = parseToHtml("6 > 5");
		TestUtil.println("HTML: " + gt);
		assertEquals("<p>6 &gt; 5</p>\n", gt);
	}

	/*
	 * Backslash Escapes. Markdown allows you to use backslash escapes to generate literal characters which would otherwise have special
	 * meaning in Markdown's formatting syntax.
	 */
	public void testEscapedBackslash() {
		assertEquals("<p>\\</p>\n", parseToHtml("\\\\"));
	}

	public void testEscapedBacktick() {
		assertEquals("<p>`</p>\n", parseToHtml("\\`"));
	}

	public void testEscapedAsterisk() {
		assertEquals("<p>*</p>\n", parseToHtml("\\*"));
	}

	public void testEscapedUnderscore() {
		assertEquals("<p>_</p>\n", parseToHtml("\\_"));
	}

	public void testEscapedOpeningCurlyBrace() {
		assertEquals("<p>{</p>\n", parseToHtml("\\{"));
	}

	public void testEscapedClosingCurlyBrace() {
		assertEquals("<p>}</p>\n", parseToHtml("\\}"));
	}

	public void testEscapedOpeningSquareBracket() {
		assertEquals("<p>[</p>\n", parseToHtml("\\["));
	}

	public void testEscapedClosingSquareBracket() {
		assertEquals("<p>]</p>\n", parseToHtml("\\]"));
	}

	public void testEscapedOpeningParenthesis() {
		assertEquals("<p>(</p>\n", parseToHtml("\\("));
	}

	public void testEscapedClosingParenthesis() {
		assertEquals("<p>)</p>\n", parseToHtml("\\)"));
	}

	public void testEscapedHashMark() {
		assertEquals("<p>#</p>\n", parseToHtml("\\#"));
	}

	public void testEscapedPlusSign() {
		assertEquals("<p>+</p>\n", parseToHtml("\\+"));
	}

	public void testEscapedMinusSign() {
		assertEquals("<p>-</p>\n", parseToHtml("\\-"));
	}

	public void testEscapedDot() {
		assertEquals("<p>.</p>\n", parseToHtml("\\."));
	}

	public void testEscapedExclamationMark() {
		assertEquals("<p>!</p>\n", parseToHtml("\\!"));
	}

}
