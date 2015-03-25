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
	public void strongSourrounded() {
		String html = parseToHtml("before *foo bar* after");
		assertEquals("<p>before <strong>foo bar</strong> after</p>\n", html);
	}

	@Test
	public void strongNoSpaceSourrounded() {
		String html = parseToHtml("before*foo bar*after");
		assertEquals("<p>before<strong>foo bar</strong>after</p>\n", html);
	}

	@Test
	public void escapedStrong() {
		String html = parseToHtml("\\*foo bar*");
		assertEquals("<p>*foo bar*</p>\n", html);
	}

	@Test
	public void emphasis() {
		String html = parseToHtml("_foo bar_");
		assertEquals("<p><em>foo bar</em></p>\n", html);
	}

	@Test
	public void escapedEmphasis() {
		String html = parseToHtml("\\_foo bar_");
		assertEquals("<p>_foo bar_</p>\n", html);
	}

	@Test
	public void code() {
		String html = parseToHtml("+foo bar+");
		assertEquals("<p><code>foo bar</code></p>\n", html);
	}

	@Test
	public void escapedCode() {
		String html = parseToHtml("\\+foo bar+");
		assertEquals("<p>+foo bar+</p>\n", html);
	}

	@Test
	public void mixed() {
		String html = parseToHtml("Here *we* _go_ *again*");
		assertEquals("<p>Here <strong>we</strong> <em>go</em> <strong>again</strong></p>\n", html);
	}

}
