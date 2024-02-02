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
public class AsciiDocLanguageAttributeTest extends AsciiDocLanguageTestBase {

	static final String MARKUP_FOR_DEFAULT = "Some default values\n\n" + //
			" - idprefix: {idprefix}\n" + //
			" - idseparator: {idseparator}\n" + //
			" - imagesdir: {imagesdir}\n" + //
			" - leveloffset: {leveloffset}\n" + //
			"";

	@Test
	public void testDefault() {
		String html = parseToHtml(MARKUP_FOR_DEFAULT);
		ensureDefaultValues(html);
	}

	static void ensureDefaultValues(String html) {
		String expected = """
				<p>Some default values</p>
				<ul>\
				<li>idprefix: _</li>\
				<li>idseparator: _</li>\
				<li>imagesdir: </li>\
				<li>leveloffset: {leveloffset}</li>\
				</ul>""";
		assertEquals(expected, html);
	}

	@Test
	public void testNotExist() {
		String markup = "Lorem {foo} ipsum {bar}";
		String html = parseToHtml(markup);

		String expected = "<p>Lorem {foo} ipsum {bar}</p>\n";
		assertEquals(expected, html);
	}

	@Test
	public void basicKeyValueAttribute() {
		String html = parseToHtml(":attr: 42\nThe answer is: {attr}");
		assertEquals("<p>The answer is: 42</p>\n", html);
	}

	@Test
	public void basicKeyValueAttributeTextStart() {
		String html = parseToHtml(":attr: 42\n{attr} is the answer");
		assertEquals("<p>42 is the answer</p>\n", html);
	}

	@Test
	public void basicKeyValueAttributeInText() {
		String html = parseToHtml(":attr: 42\nxxx{attr}zzzz");
		assertEquals("<p>xxx42zzzz</p>\n", html);
	}

	@Test
	public void basicKeyValueAttributeWithSpaces() {
		String html = parseToHtml(":attr  : 42\n\nThe answer is: {attr}");
		assertEquals("<p>The answer is: 42</p>\n", html);
	}

	@Test
	public void ignoreEscapedReference() {
		String html = parseToHtml(":attr: 42\nThe answer is: \\{attr} or {attr}");
		assertEquals("<p>The answer is: {attr} or 42</p>\n", html);
	}

	@Test
	public void attributeWithFormatting() {
		String html = parseToHtml(":boldy: *Stronged*\nIs this {boldy}");
		assertEquals("<p>Is this <strong>Stronged</strong></p>\n", html);
	}

	@Test
	public void attributeMidSentence() {
		String html = parseToHtml(":number: three\nIs {number} higher ?");
		assertEquals("<p>Is three higher ?</p>\n", html);
	}

	@Test
	public void multipleAttributeDefinition() {
		String markup = ":one: lorem\n" + //
				":two: {one} ipsum\n" + //
				"Test {one} and {two}.\n\n" + //
				":one: LOREM\n" + //
				"Repeat: Test {one} and {two}.\n\n" + //
				"";
		String html = parseToHtml(markup);

		String expected = "<p>Test lorem and lorem ipsum.</p>\n" + //
				"<p>Repeat: Test LOREM and LOREM ipsum.</p>\n";
		assertEquals(expected, html);
	}

	@Test
	public void attributeEmpty() {
		String html = parseToHtml(":attr:\nThis is {attr}");
		assertEquals("<p>This is </p>\n", html);
	}

	@Test
	public void attributeUnassign() {
		String markup = ":number: three\n" + //
				"Is {number} higher ?\n\n" + //
				":number!: three\n\n" + //
				"Repeat: is {number} higher ?\n\n" + //
				"";
		String html = parseToHtml(markup);

		String expected = "<p>Is three higher ?</p>\n" + //
				"<p>Repeat: is {number} higher ?</p>\n";
		assertEquals(expected, html);
	}

	@Test
	public void attributeUnassignWithSpace() {
		String markup = ":number   : three\n" + //
				"Is {number} higher ?\n\n" + //
				":number  !:\n\n" + //
				"Repeat: is {number} higher ?" + //
				"";
		String html = parseToHtml(markup);

		String expected = "<p>Is three higher ?</p>\n" + //
				"<p>Repeat: is {number} higher ?</p>\n";
		assertEquals(expected, html);
	}

	@Test
	public void inlineAttributeAssign() {
		String html = parseToHtml("Lorem {set:number:42} ipsum. {number} ?");
		assertEquals("<p>Lorem  ipsum. 42 ?</p>\n", html);
	}

	@Test
	public void inlineAttributeUnassign() {
		String html = parseToHtml(":number: three\nIs {number} higher? {set:number!}Because {number} is");
		assertEquals("<p>Is three higher? Because {number} is</p>\n", html);
	}
}
