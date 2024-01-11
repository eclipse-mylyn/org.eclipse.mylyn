/*******************************************************************************
 * Copyright (c) 2015, 2024 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Max Rydahl Andersen - copied from markdown to get base for asciidoc
 *     ArSysOp - ongoing support
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
	public void strongNoWordBoundarySpaceSurrounded() {
		String html = parseToHtml("before **foo bar** after");
		assertEquals("<p>before <strong>foo bar</strong> after</p>\n", html);
	}

	@Test
	public void strongNoWordBoundarySpaceSurroundedBegin() {
		String html = parseToHtml("**foo bar** after");
		assertEquals("<p><strong>foo bar</strong> after</p>\n", html);
	}

	@Test
	public void strongNoWordBoundarySpaceSurroundedEnd() {
		String html = parseToHtml("before **foo bar**");
		assertEquals("<p>before <strong>foo bar</strong></p>\n", html);
	}

	@Test
	public void strongNoWordBoundaryNoSpaceSurrounded() {
		String html = parseToHtml("before**foo bar**after");
		assertEquals("<p>before<strong>foo bar</strong>after</p>\n", html);
	}

	@Test
	public void strongSpecialCharAfter() {
		String html = parseToHtml("This *lorem*! And *ipsum*.");
		assertEquals("<p>This <strong>lorem</strong>! And <strong>ipsum</strong>.</p>\n", html);
	}

	@Test
	public void strongSpecialCharBefore() {
		String html = parseToHtml("This %*foo bar* and -*bar foo*");
		assertEquals("<p>This %<strong>foo bar</strong> and -<strong>bar foo</strong></p>\n", html);
	}

	@Test
	public void notStrongUnderscoreBefore() {
		String html = parseToHtml("before _*foo* after");
		assertEquals("<p>before _*foo* after</p>\n", html);
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
	public void emphasisNoWordBoundarySpaceSurrounded() {
		String html = parseToHtml("before __foo bar__ after");
		assertEquals("<p>before <em>foo bar</em> after</p>\n", html);
	}

	@Test
	public void emphasisNoWordBoundarySpaceSurroundedBegin() {
		String html = parseToHtml("__foo bar__ after");
		assertEquals("<p><em>foo bar</em> after</p>\n", html);
	}

	@Test
	public void emphasisNoWordBoundarySpaceSurroundedEnd() {
		String html = parseToHtml("before __foo bar__");
		assertEquals("<p>before <em>foo bar</em></p>\n", html);
	}

	@Test
	public void emphasisNoWordBoundaryNoSpaceSurrounded() {
		String html = parseToHtml("before__foo bar__after");
		assertEquals("<p>before<em>foo bar</em>after</p>\n", html);
	}

	@Test
	public void emphasisSpecialCharAfter() {
		String html = parseToHtml("Try _lorem_{} and _ipsum_[]");
		assertEquals("<p>Try <em>lorem</em>{} and <em>ipsum</em>[]</p>\n", html);
	}

	@Test
	public void emphasisSpecialCharBefore() {
		String html = parseToHtml("Try ()_lorem_ and <>_ipsum_");
		assertEquals("<p>Try ()<em>lorem</em> and &lt;&gt;<em>ipsum</em></p>\n", html);
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
	public void plusSpan() {
		String html = parseToHtml("+foo bar+");
		assertEquals("<p><span>foo bar</span></p>\n", html);
	}

	@Test
	public void plusSpanSurrounded() {
		String html = parseToHtml("before +foo bar+ after");
		assertEquals("<p>before <span>foo bar</span> after</p>\n", html);
	}

	@Test
	public void plusSpanNoSpaceSurrounded() {
		String html = parseToHtml("before+foo bar+after");
		assertEquals("<p>before+foo bar+after</p>\n", html);
	}

	@Test
	public void plusSpanTwiceInLine() {
		String html = parseToHtml("before +foo bar+ after a +second time+");
		assertEquals("<p>before <span>foo bar</span> after a <span>second time</span></p>\n", html);
	}

	@Test
	public void plusSpanTwiceInLineNoSpaceSurrounded() {
		String html = parseToHtml("before +foo bar+after a +second time+");
		assertEquals("<p>before <span>foo bar+after a +second time</span></p>\n", html);
	}

	@Test
	public void plusSpanNoWordBoundarySpaceSurrounded() {
		String html = parseToHtml("before ++foo bar++ after");
		assertEquals("<p>before <span>foo bar</span> after</p>\n", html);
	}

	@Test
	public void plusSpanNoWordBoundarySpaceSurroundedBegin() {
		String html = parseToHtml("++foo bar++ after");
		assertEquals("<p><span>foo bar</span> after</p>\n", html);
	}

	@Test
	public void plusSpanNoWordBoundarySpaceSurroundedEnd() {
		String html = parseToHtml("before ++foo bar++");
		assertEquals("<p>before <span>foo bar</span></p>\n", html);
	}

	@Test
	public void plusSpanNoWordBoundaryNoSpaceSurrounded() {
		String html = parseToHtml("before++foo bar++after");
		assertEquals("<p>before<span>foo bar</span>after</p>\n", html);
	}

	@Test
	public void escapedPlusSpan() {
		String html = parseToHtml("\\+foo bar+");
		assertEquals("<p>+foo bar+</p>\n", html);
	}

	@Test
	public void escapedPlusSpanNoWordBoundary() {
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
	public void codeBacktickNoWordBoundarySpaceSurrounded() {
		String html = parseToHtml("before ``foo bar`` after");
		assertEquals("<p>before <code>foo bar</code> after</p>\n", html);
	}

	@Test
	public void codeBacktickNoWordBoundarySpaceSurroundedBegin() {
		String html = parseToHtml("``foo bar`` after");
		assertEquals("<p><code>foo bar</code> after</p>\n", html);
	}

	@Test
	public void codeBacktickNoWordBoundarySpaceSurroundedEnd() {
		String html = parseToHtml("before ``foo bar``");
		assertEquals("<p>before <code>foo bar</code></p>\n", html);
	}

	@Test
	public void codeBacktickNoWordBoundaryNoSpaceSurrounded() {
		String html = parseToHtml("before``foo bar``after");
		assertEquals("<p>before<code>foo bar</code>after</p>\n", html);
	}

	@Test
	public void codeBacktickSpecialCharAfter() {
		String html = parseToHtml("For `waitUntil` and `waitWhile`.");
		assertEquals("<p>For <code>waitUntil</code> and <code>waitWhile</code>.</p>\n", html);
	}

	@Test
	public void mixed() {
		String html = parseToHtml("Here *we* _go_ *again*");
		assertEquals("<p>Here <strong>we</strong> <em>go</em> <strong>again</strong></p>\n", html);
	}

	@Test
	public void boldAndItalicWordBoundary() {
		String html = parseToHtml("This should be *_italic bold_*");
		assertEquals("<p>This should be <strong><em>italic bold</em></strong></p>\n", html);
	}

	@Test
	public void italicAndBoldWordBoundary() {
		String html = parseToHtml("This should be _*italic bold*_");
		assertEquals("<p>This should be <em><strong>italic bold</strong></em></p>\n", html);
	}

	@Test
	public void codeAndBoldWordBoundary() {
		String html = parseToHtml("This should be `*italic bold*`");
		assertEquals("<p>This should be <code><strong>italic bold</strong></code></p>\n", html);
	}

	@Test
	public void boldAndCodeWordBoundary() {
		String html = parseToHtml("This should be *`italic bold`*");
		assertEquals("<p>This should be <strong><code>italic bold</code></strong></p>\n", html);
	}

	@Test
	public void italicAndCodeWordBoundary() {
		String html = parseToHtml("This should be _`italic bold`_");
		assertEquals("<p>This should be <em><code>italic bold</code></em></p>\n", html);
	}

	@Test
	public void codeAndItalicWordBoundary() {
		String html = parseToHtml("This should be `_italic bold_`");
		assertEquals("<p>This should be <code><em>italic bold</em></code></p>\n", html);
	}

	@Test
	public void passThroughWordBoundary() {
		String html = parseToHtml("This should be +++<strong>bold</strong>+++");
		assertEquals("<p>This should be <span><strong>bold</strong></span></p>\n", html);
	}

	@Test
	public void boldAndItalicNoWordBoundary() {
		String html = parseToHtml("This should be**_italic bold_**no space");
		assertEquals("<p>This should be<strong><em>italic bold</em></strong>no space</p>\n", html);
	}

	@Test
	public void italicAndBoldNoWordBoundary() {
		String html = parseToHtml("This should be__*italic bold*__no space");
		assertEquals("<p>This should be<em><strong>italic bold</strong></em>no space</p>\n", html);
	}

	@Test
	public void codeAndBoldNoWordBoundary() {
		String html = parseToHtml("This should be``*code bold*``no space");
		assertEquals("<p>This should be<code><strong>code bold</strong></code>no space</p>\n", html);
	}

	@Test
	public void boldAndCodeNoWordBoundary() {
		String html = parseToHtml("This should be**`code bold`**no space");
		assertEquals("<p>This should be<strong><code>code bold</code></strong>no space</p>\n", html);
	}

	@Test
	public void italicAndCodeNoWordBoundary() {
		String html = parseToHtml("This should be__`italic code`__no space");
		assertEquals("<p>This should be<em><code>italic code</code></em>no space</p>\n", html);
	}

	@Test
	public void codeAndItalicNoWordBoundary() {
		String html = parseToHtml("This should be``_italic code_``no space");
		assertEquals("<p>This should be<code><em>italic code</em></code>no space</p>\n", html);
	}

	@Test
	public void passThroughNoWordBoundary() {
		String html = parseToHtml("This should be+++<strong>bold</strong>+++no space");
		assertEquals("<p>This should be<span><strong>bold</strong></span>no space</p>\n", html);
	}

	@Test
	public void superscript() {
		String html = parseToHtml("Superscript: x^2**a**^ = *y*^_t_^z^*s*^");
		assertEquals(
				"<p>Superscript: x<sup>2<strong>a</strong></sup> = <strong>y</strong><sup><em>t</em></sup>z<sup><strong>s</strong></sup></p>\n",
				html);
	}

	@Test
	public void subscript() {
		String html = parseToHtml("Subscript: x~2**a**~ = *y*~_t_~z~*s*~");
		assertEquals(
				"<p>Subscript: x<sub>2<strong>a</strong></sub> = <strong>y</strong><sub><em>t</em></sub>z<sub><strong>s</strong></sub></p>\n",
				html);
	}

	@Test
	public void highlightWordBoundary() {
		String html = parseToHtml("#text#");
		assertEquals("<p><mark>text</mark></p>\n", html);
	}

	@Test
	public void highlightNoWordBoundary() {
		String html = parseToHtml("nospace#text#nospace");
		assertEquals("<p>nospace#text#nospace</p>\n", html);
	}

	@Test
	public void customClassWordBoundary() {
		String html = parseToHtml("[css-class]#text#");
		assertEquals("<p><span class=\"css-class\">text</span></p>\n", html);
	}

	@Test
	public void customClassLeadingDots() {
		String html = parseToHtml("[..css-class]#text#");
		assertEquals("<p><span class=\"css-class\">text</span></p>\n", html);
	}

	@Test
	public void customClassNoWordBoundary() {
		String html = parseToHtml("nospace[css-class]#text#nospace");
		assertEquals("<p>nospace[css-class]#text#nospace</p>\n", html);
	}

	@Test
	public void customClassNoWordBoundaryBefore() {
		String html = parseToHtml("nospace[css-class]#text#");
		assertEquals("<p>nospace[css-class]<mark>text</mark></p>\n", html);
	}
}
