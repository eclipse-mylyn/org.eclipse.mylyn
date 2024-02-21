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
 *     Max Rydahl Andersen - copied from markdown to get base for asciidoc
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
public class AsciiDocLanguageLinksTest extends AsciiDocLanguageTestBase {

	@Test
	public void implicitLink() {
		String html = parseToHtml("http://eclipse.org");
		assertEquals("<p><a href=\"http://eclipse.org\">http://eclipse.org</a></p>\n", html);
	}

	@Test
	public void implicitLinkWithSymbols() {
		String[] problematicUrls = { "http://example.com/.", //
				"http://example.com/.a", "http://example.com/)a", "http://example.com/(", "http://example.com/(a",
				"http://example.com/,", "http://example.com/,a", "http://example.com/;", "http://example.com/;a",
				"http://example.com/#", "http://example.com/#a", "http://example.com/%20", "http://example.com/%20a", };

		for (String url : problematicUrls) {
			assertEquals("<p><a href=\"" + url + "\">" + url + "</a></p>\n", parseToHtml(url));
		}
	}

	@Test
	public void implicitLinkWithAmpersand() {
		assertEquals("<p><a href=\"http://example.com?a=b&amp;c=d+e\">http://example.com?a=b&amp;c=d+e</a></p>\n",
				parseToHtml("http://example.com?a=b&c=d+e"));
	}

	@Test
	public void implicitLinkWithText() {
		String html = parseToHtml("http://eclipse.org[Eclipse Rocks]");
		assertEquals("<p><a href=\"http://eclipse.org\">Eclipse Rocks</a></p>\n", html);
	}

	@Test
	public void implicitLinkWithTextAndBlankShortcut() {
		String html = parseToHtml("http://eclipse.org[Eclipse Rocks^]");
		assertEquals("<p><a href=\"http://eclipse.org\" target=\"_blank\">Eclipse Rocks</a></p>\n", html);
	}

	@Test
	public void implicitLinkWithMixedTextSymbolx() {
		String html = parseToHtml(
				"This is a link to http://eclipse.org#tag%20test[Eclipse Rocks] that could be nasty.");
		assertEquals(
				"<p>This is a link to <a href=\"http://eclipse.org#tag%20test\">Eclipse Rocks</a> that could be nasty.</p>\n",
				html);
	}

	@Test
	public void explicitInlineLink() {
		String html = parseToHtml("link:http://eclipse.org[Eclipse Rocks]");
		assertEquals("<p><a href=\"http://eclipse.org\">Eclipse Rocks</a></p>\n", html);

	}

	@Test
	public void explicitLinkWithMixedTextSymbolx() {
		String html = parseToHtml(
				"This is a link to link:http://eclipse.org#tag%20test[Eclipse Rocks] that could be nasty.");
		assertEquals(
				"<p>This is a link to <a href=\"http://eclipse.org#tag%20test\">Eclipse Rocks</a> that could be nasty.</p>\n",
				html);
	}

	@Test
	public void implicitEmailLink() {
		String html = parseToHtml("devel@discuss.arquillian.org");
		assertEquals("<p><a href=\"mailto:devel@discuss.arquillian.org\">devel@discuss.arquillian.org</a></p>\n", html);

	}

	@Test
	public void implicitIrcLink() {
		String html = parseToHtml("irc://irc.freenode.org/#asciidoctor");
		assertEquals("<p><a href=\"irc://irc.freenode.org/#asciidoctor\">irc://irc.freenode.org/#asciidoctor</a></p>\n",
				html);
	}

	@Test
	public void implicitCallToLinkWithText() {
		String html = parseToHtml("Call me at callto://eclipse.example ");
		assertEquals("<p>Call me at <a href=\"callto://eclipse.example\">callto://eclipse.example</a></p>\n", html);
	}

	@Test
	public void explicitWindowsPath() {
		String html = parseToHtml("link:\\\\server\\share\\whitepaper.pdf[Whitepaper]");
		assertEquals("<p><a href=\"\\\\server\\share\\whitepaper.pdf\">Whitepaper</a></p>\n", html);
	}

	@Test
	public void explicitLinkWithWhiteSpaces() {
		String html = parseToHtml("link:++http://example.org/?q=[a b]++[URL with special characters]");
		assertEquals("<p><a href=\"http://example.org/?q=[a b]\">URL with special characters</a></p>\n", html);

	}

	@Test
	public void explicitLinkWithSpecialCharacters() {
		String html = parseToHtml("link:++http://example.org/?q=%5Ba%20b%5D++[URL with special characters]");
		assertEquals("<p><a href=\"http://example.org/?q=%5Ba%20b%5D\">URL with special characters</a></p>\n", html);

	}

	@Test
	public void explicitRelativeLink() {
		String html = parseToHtml("link:index.html[Docs]");
		assertEquals("<p><a href=\"index.html\">Docs</a></p>\n", html);

	}

}
