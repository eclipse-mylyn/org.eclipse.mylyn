/*******************************************************************************
 * Copyright (c) 2012, 2024 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Max Rydahl Andersen - copied from markdown to get base for asciidoc, Bug 474084
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for asciidoc overview and miscellaneous.
 *
 * @author Max Rydahl Andersen
 */
@SuppressWarnings("nls")
public class AsciiDocLanguageMiscellaneousTest extends AsciiDocLanguageTestBase {

	@Test
	public void emptyLine() {
		String html = parseToHtml("    ");
		assertEquals("", html);
	}

	@Test
	public void preserveHtmlEntities() {
		String html = parseToHtml("&copy; &amp;");
		assertEquals("<p>&copy; &amp;</p>\n", html);
	}

	@Test
	public void ampersandIsEscaped() {
		String html = parseToHtml("AT&T, a & b");
		assertEquals("<p>AT&amp;T, a &amp; b</p>\n", html);
	}

	@Test
	public void angleBracketsAreEscaped() {
		// lower than:
		String html = parseToHtml("4 < 5");
		assertEquals("<p>4 &lt; 5</p>\n", html);

		// greater than:
		html = parseToHtml("6 > 5");
		assertEquals("<p>6 &gt; 5</p>\n", html);
	}

	@Test
	public void backslashBackslash() {
		// this is not an escaped backslash
		String html = parseToHtml("\\\\");
		assertEquals("<p>\\\\</p>\n", html);
	}

	@Test
	public void backslashBacktick() {
		// this is not an escaped backtick
		String html = parseToHtml("\\`");
		assertEquals("<p>\\`</p>\n", html);
	}

	@Test
	public void backslashOpeningCurlyBrace() {
		// this is not an escaped opening curly brace
		String html = parseToHtml("\\{");
		assertEquals("<p>\\{</p>\n", html);
	}

	@Test
	public void backslashOpeningAndClosingCurlyBrace() {
		// this is not an escaped opening curly brace
		String html = parseToHtml("\\{}");
		assertEquals("<p>\\{}</p>\n", html);
	}

	@Test
	public void backslashAttribute() {
		// this is an escaped opening curly brace, because it is an attribute
		String html = parseToHtml("\\{xxx}");
		assertEquals("<p>{xxx}</p>\n", html);
	}

	@Test
	public void backslashAttributeUnderscore() {
		// this is an escaped opening curly brace, because it is an attribute
		String html = parseToHtml("\\{_}");
		assertEquals("<p>{_}</p>\n", html);
	}

	@Test
	public void backslashClosingCurlyBrace() {
		// this is not an escaped closing curly brace
		String html = parseToHtml("\\}");
		assertEquals("<p>\\}</p>\n", html);
	}

	@Test
	public void backslashOpeningSquareBracket() {
		// this is not an escaped opening square bracket
		String html = parseToHtml("\\[");
		assertEquals("<p>\\[</p>\n", html);
	}

	@Test
	public void backslashClosingSquareBracket() {
		// this is not an escaped closing square bracket
		String html = parseToHtml("\\]");
		assertEquals("<p>\\]</p>\n", html);
	}

	@Test
	public void backslashOpeningParenthesis() {
		// this is not an escaped opening parenthesis
		String html = parseToHtml("\\(");
		assertEquals("<p>\\(</p>\n", html);
	}

	@Test
	public void backslashClosingParenthesis() {
		// this is not an escaped closing parenthesis
		String html = parseToHtml("\\)");
		assertEquals("<p>\\)</p>\n", html);
	}

	@Test
	public void backslashHashMark() {
		// this is not an escaped hash mark
		String html = parseToHtml("\\#");
		assertEquals("<p>\\#</p>\n", html);
	}

	@Test
	public void backslashMinusSign() {
		// this is not an escaped minus sign
		String html = parseToHtml("\\-");
		assertEquals("<p>\\-</p>\n", html);
	}

	@Test
	public void backslashDot() {
		// this is not an escaped dot
		String html = parseToHtml("\\.");
		assertEquals("<p>\\.</p>\n", html);
	}

	@Test
	public void backslashExclamationMark() {
		// this is not an escaped exclamation mark
		String html = parseToHtml("\\!");
		assertEquals("<p>\\!</p>\n", html);
	}

	@Test
	public void ignoreComments() {
		String html = parseToHtml("// ignore this\nInclude this");
		assertEquals("<p>\nInclude this</p>\n", html);
	}

	@Test
	public void ignoreEmptyComment() {
		String html = parseToHtml("//\nInclude this");
		assertEquals("<p>\nInclude this</p>\n", html);
	}

	@Test
	public void ignoreBooleanAttribute() {
		String html = parseToHtml(":attr:");
		assertEquals("", html);
	}

	@Test
	public void ignoreKeyValueAttribute() {
		String html = parseToHtml(":attr: 42");
		assertEquals("", html);
	}
}
