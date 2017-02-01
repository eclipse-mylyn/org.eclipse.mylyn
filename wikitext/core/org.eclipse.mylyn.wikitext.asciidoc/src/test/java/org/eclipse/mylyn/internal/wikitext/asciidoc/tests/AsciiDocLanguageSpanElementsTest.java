/*******************************************************************************
 * Copyright (c) 2015 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Max Rydahl Andersen - copied from markdown to get base for asciidoc
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for asciidoc span elements.
 *
 * @author Max Rydahl Andersen
 */
public class AsciiDocLanguageSpanElementsTest extends AsciiDocLanguageTestBase {

	@Test
	public void strong() {
		String html = parseToHtml("*foo bar*");
		assertEquals("<p><strong>foo bar</strong></p>\n", html);
	}

	@Test
	public void strongSurrounded() {
		String html = parseToHtml("before *foo bar* after");
		assertEquals("<p>before <strong>foo bar</strong> after</p>\n", html);
	}

	@Test
	public void strongNoSpaceSurrounded() {
		String html = parseToHtml("before*foo bar*after");
		assertEquals("<p>before*foo bar*after</p>\n", html);
	}

	@Test
	public void strongTwiceInLine() {
		String html = parseToHtml("before *foo bar* after a *second time*");
		assertEquals("<p>before <strong>foo bar</strong> after a <strong>second time</strong></p>\n", html);
	}

	@Test
	public void strongTwiceInLineNoSpaceSurrounded() {
		String html = parseToHtml("before *foo bar*after a *second time*");
		assertEquals("<p>before <strong>foo bar*after a *second time</strong></p>\n", html);
	}

	@Test
	public void strongNoWordBoundaryNoSpaceSurrounded() {
		String html = parseToHtml("before**foo bar**after");
		assertEquals("<p>before<strong>foo bar</strong>after</p>\n", html);
	}

	@Test
	public void escapedStrong() {
		String html = parseToHtml("\\*foo bar*");
		assertEquals("<p>*foo bar*</p>\n", html);
	}

	@Test
	public void escapedStrongNoWordBoundary() {
		String html = parseToHtml("before\\\\**foo bar**");
		assertEquals("<p>before**foo bar**</p>\n", html);
	}

	@Test
	public void emphasis() {
		String html = parseToHtml("_foo bar_");
		assertEquals("<p><em>foo bar</em></p>\n", html);
	}

	@Test
	public void emphasisSurrounded() {
		String html = parseToHtml("before _foo bar_ after");
		assertEquals("<p>before <em>foo bar</em> after</p>\n", html);
	}

	@Test
	public void emphasisNoSpaceSurrounded() {
		String html = parseToHtml("before_foo bar_after");
		assertEquals("<p>before_foo bar_after</p>\n", html);
	}

	@Test
	public void emphasisTwiceInLine() {
		String html = parseToHtml("before _foo bar_ after a _second time_");
		assertEquals("<p>before <em>foo bar</em> after a <em>second time</em></p>\n", html);
	}

	@Test
	public void emphasisTwiceInLineNoSpaceSurrounded() {
		String html = parseToHtml("before _foo bar_after a _second time_");
		assertEquals("<p>before <em>foo bar_after a _second time</em></p>\n", html);
	}

	@Test
	public void emphasisNoWordBoundaryNoSpaceSurrounded() {
		String html = parseToHtml("before__foo bar__after");
		assertEquals("<p>before<em>foo bar</em>after</p>\n", html);
	}

	@Test
	public void escapedEmphasis() {
		String html = parseToHtml("\\_foo bar_");
		assertEquals("<p>_foo bar_</p>\n", html);
	}

	@Test
	public void escapedEmphasisNoWordBoundary() {
		String html = parseToHtml("before\\\\__foo bar__");
		assertEquals("<p>before__foo bar__</p>\n", html);
	}

	@Test
	public void code() {
		String html = parseToHtml("+foo bar+");
		assertEquals("<p><code>foo bar</code></p>\n", html);
	}

	@Test
	public void codeSurrounded() {
		String html = parseToHtml("before +foo bar+ after");
		assertEquals("<p>before <code>foo bar</code> after</p>\n", html);
	}

	@Test
	public void codeNoSpaceSurrounded() {
		String html = parseToHtml("before+foo bar+after");
		assertEquals("<p>before+foo bar+after</p>\n", html);
	}

	@Test
	public void codeTwiceInLine() {
		String html = parseToHtml("before +foo bar+ after a +second time+");
		assertEquals("<p>before <code>foo bar</code> after a <code>second time</code></p>\n", html);
	}

	@Test
	public void codeTwiceInLineNoSpaceSurrounded() {
		String html = parseToHtml("before +foo bar+after a +second time+");
		assertEquals("<p>before <code>foo bar+after a +second time</code></p>\n", html);
	}

	@Test
	public void codeNoWordBoundaryNoSpaceSurrounded() {
		String html = parseToHtml("before++foo bar++after");
		assertEquals("<p>before<code>foo bar</code>after</p>\n", html);
	}

	@Test
	public void escapedCode() {
		String html = parseToHtml("\\+foo bar+");
		assertEquals("<p>+foo bar+</p>\n", html);
	}

	@Test
	public void escapedCodeNoWordBoundary() {
		String html = parseToHtml("before\\\\++foo bar++");
		assertEquals("<p>before++foo bar++</p>\n", html);
	}

	@Test
	public void codeBacktick() {
		String html = parseToHtml("`foo bar`");
		assertEquals("<p><code>foo bar</code></p>\n", html);
	}

	@Test
	public void codeBacktickSurrounded() {
		String html = parseToHtml("before `foo bar` after");
		assertEquals("<p>before <code>foo bar</code> after</p>\n", html);
	}

	@Test
	public void codeBacktickNoSpaceSurrounded() {
		String html = parseToHtml("before`foo bar`after");
		assertEquals("<p>before`foo bar`after</p>\n", html);
	}

	@Test
	public void codeBacktickTwiceInLine() {
		String html = parseToHtml("before `foo bar` after a `second time`");
		assertEquals("<p>before <code>foo bar</code> after a <code>second time</code></p>\n", html);
	}

	@Test
	public void codeBacktickTwiceInLineNoSpaceSurrounded() {
		String html = parseToHtml("before `foo bar`after a `second time`");
		assertEquals("<p>before <code>foo bar`after a `second time</code></p>\n", html);
	}

	@Test
	public void codeBacktickNoWordBoundaryNoSpaceSurrounded() {
		String html = parseToHtml("before``foo bar``after");
		assertEquals("<p>before<code>foo bar</code>after</p>\n", html);
	}

	@Test
	public void mixed() {
		String html = parseToHtml("Here *we* _go_ *again*");
		assertEquals("<p>Here <strong>we</strong> <em>go</em> <strong>again</strong></p>\n", html);
	}

}
