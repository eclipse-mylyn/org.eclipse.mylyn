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
 * Tests for Markdown span elements. Follows specification at
 * <a>http://daringfireball.net/projects/markdown/syntax#span</a>.
 * 
 * @author Stefan Seelmann
 */
public class MarkdownLanguageSpanElementsTest extends MarkdownLanguageTestBase {

	/*
	 * Emphasis. Markdown treats asterisks * and underscores _ as indicators of emphasis. Text wrapped with one * or _ will be
	 * wrapped with an HTML em tag; double **'s or __ will be wrapped with an HTML strong tag.
	 */
	public void testEmphasisWithAsterisks() {
		String html = parseToHtml("*foo bar*");
		TestUtil.println("HTML: " + html);
		assertEquals("<p><em>foo bar</em></p>\n", html);
	}

	public void testEmphasisWithUnderscore() {
		String html = parseToHtml("_foo bar_");
		TestUtil.println("HTML: " + html);
		assertEquals("<p><em>foo bar</em></p>\n", html);
	}

	public void testStrongWithAsterisks() {
		String html = parseToHtml("**foo bar**");
		TestUtil.println("HTML: " + html);
		assertEquals("<p><strong>foo bar</strong></p>\n", html);
	}

	public void testStrongWithUnderscore() {
		String html = parseToHtml("__foo bar__");
		TestUtil.println("HTML: " + html);
		assertEquals("<p><strong>foo bar</strong></p>\n", html);
	}

	/*
	 * Emphasis can be used in the middle of a word.
	 */
	public void testEmphasisWithinWord() {
		String html = parseToHtml("un*frigging*believable");
		TestUtil.println("HTML: " + html);
		assertEquals("<p>un<em>frigging</em>believable</p>\n", html);
	}

	/*
	 * But if you surround an * or _ with spaces, it'll be treated as a literal asterisk or underscore.
	 */
	public void testLiteralAsteriskAndUnderscore() {
		String html = parseToHtml("asterisk * underscore _");
		TestUtil.println("HTML: " + html);
		assertEquals("<p>asterisk * underscore _</p>\n", html);
	}

	/*
	 * To produce a literal asterisk or underscore at a position where it would otherwise be used as an emphasis
	 * delimiter, you can backslash escape it.
	 */
	public void testEscapesAsterisk() {
		String html = parseToHtml("\\*foo bar\\*");
		TestUtil.println("HTML: " + html);
		assertEquals("<p>*foo bar*</p>\n", html);
	}

	public void testEscapesUnderscore() {
		String html = parseToHtml("\\_foo bar\\_");
		TestUtil.println("HTML: " + html);
		assertEquals("<p>_foo bar_</p>\n", html);
	}

	public void testEscapesDoubleAsterisk() {
		String html = parseToHtml("\\**foo bar\\**");
		TestUtil.println("HTML: " + html);
		assertEquals("<p>**foo bar**</p>\n", html);
	}

	public void testEscapesDoubleUnderscore() {
		String html = parseToHtml("\\__foo bar\\__");
		TestUtil.println("HTML: " + html);
		assertEquals("<p>__foo bar__</p>\n", html);
	}

	/*
	 * Code. To indicate a span of code, wrap it with backtick quotes.
	 */
	public void testCodeSpan() {
		String html = parseToHtml("Use the `printf()` function.");
		TestUtil.println("HTML: " + html);
		assertEquals("<p>Use the <code>printf()</code> function.</p>\n", html);
	}

	/*
	 * To include a literal backtick character within a code span, you can use multiple backticks as the opening and
	 * closing delimiters.
	 */
	public void testLiteralBacktickInCodeSpan() {
		String html = parseToHtml("``There is a literal backtick (`) here.``");
		TestUtil.println("HTML: " + html);
		assertEquals("<p><code>There is a literal backtick (`) here.</code></p>\n", html);
	}

	/*
	 * The backtick delimiters surrounding a code span may include spaces - one after the opening, one before the
	 * closing. This allows you to place literal backtick characters at the beginning or end of a code span:
	 */
	public void testLiteralBacktickAtBeginnionOrIndOfCodeSpan() {
		String html = parseToHtml("`` `foo` ``");
		TestUtil.println("HTML: " + html);
		assertEquals("<p><code>`foo`</code></p>\n", html);
	}

	/*
	 * With a code span, ampersands and angle brackets are encoded as HTML entities automatically
	 */
	public void testCodeSpanEncodesAmpersandsAndAngleBrackets() {
		String html = parseToHtml("`Encode tags <p> and enties &code;`");
		TestUtil.println("HTML: " + html);
		assertEquals("<p><code>Encode tags &lt;p&gt; and enties &code;</code></p>\n", html);
	}

	public void testNoProcessingInCodeSpan() {
		String html = parseToHtml("`Preserve *asterisk*.`");
		TestUtil.println("HTML: " + html);
		assertEquals("<p><code>Preserve *asterisk*.</code></p>\n", html);
	}

}
