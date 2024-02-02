/*******************************************************************************
 * Copyright (c) 2016, 2024 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AsciiDocLanguageXrefTest extends AsciiDocLanguageTestBase {

	@Test
	public void testInText() {
		String text = "Lorem <<xxx>> ipsum";

		String html = parseToHtml(text);

		assertEquals("<p>Lorem <a href=\"#xxx\">[xxx]</a> ipsum</p>\n", html);
	}

	@Test
	public void testEndTextWithLinkText() {
		String text = "Lorem ipsum: <<xxx,abc>>";

		String html = parseToHtml(text);

		assertEquals("<p>Lorem ipsum: <a href=\"#xxx\">abc</a></p>\n", html);
	}

	@Test
	public void testBeginTextWithExternalFileWithAnchor() {
		String text = "<<file.adoc#xxx>> lorem ipsum";

		String html = parseToHtml(text);

		assertEquals("<p><a href=\"file.adoc#xxx\">[file#xxx]</a> lorem ipsum</p>\n", html);
	}

	@Test
	public void testInTextExternalFile() {
		String text = "Lorem <<file.adoc#>> ipsum";

		String html = parseToHtml(text);

		assertEquals("<p>Lorem <a href=\"file.adoc\">[file]</a> ipsum</p>\n", html);
	}

	@Test
	public void testInTextExternalFileWitoutExtension() {
		String text = "Lorem <<file#xxx>> ipsum";

		String html = parseToHtml(text);

		assertEquals("<p>Lorem <a href=\"file#xxx\">[file#xxx]</a> ipsum</p>\n", html);
	}

	@Test
	public void testInTextRefNotExistingTitle() {
		String text = "Lorem <<Some Title>> ipsum";

		String html = parseToHtml(text);

		assertEquals("<p>Lorem <a href=\"#Some Title\">[Some Title]</a> ipsum</p>\n", html);
	}

	@Test
	public void testStandalone() {
		String text = "<<abc>>";

		String html = parseToHtml(text);

		assertEquals("<p><a href=\"#abc\">[abc]</a></p>\n", html);
	}

	@Test
	public void testMacroInText() {
		String text = "Lorem xref:xxx[] ipsum";

		String html = parseToHtml(text);

		assertEquals("<p>Lorem <a href=\"#xxx\">[xxx]</a> ipsum</p>\n", html);
	}

	@Test
	public void testMacroEndTextWithLinkText() {
		String text = "Lorem ipsum: xref:xxx[abc]";

		String html = parseToHtml(text);

		assertEquals("<p>Lorem ipsum: <a href=\"#xxx\">abc</a></p>\n", html);
	}

	@Test
	public void testMacroBeginTextWithExternalFileWithAnchor() {
		String text = "xref:file.adoc#xxx[] lorem ipsum";

		String html = parseToHtml(text);

		assertEquals("<p><a href=\"file.adoc#xxx\">[file#xxx]</a> lorem ipsum</p>\n", html);
	}

	@Test
	public void testMacroInTextExternalFile() {
		String text = "Lorem xref:file.adoc#[] ipsum";

		String html = parseToHtml(text);

		assertEquals("<p>Lorem <a href=\"file.adoc\">[file]</a> ipsum</p>\n", html);
	}

	@Test
	public void testMacroInTextExternalFileWitoutExtension() {
		String text = "Lorem xref:file#xxx[] ipsum";

		String html = parseToHtml(text);

		assertEquals("<p>Lorem <a href=\"file#xxx\">[file#xxx]</a> ipsum</p>\n", html);
	}

	@Test
	public void testMacroInTextRefNotExistingTitle() {
		String text = "Lorem xref:Some Title[] ipsum";

		String html = parseToHtml(text);

		assertEquals("<p>Lorem <a href=\"#Some Title\">[Some Title]</a> ipsum</p>\n", html);
	}

	@Test
	public void testMacroStandalone() {
		String text = "xref:abc[]";

		String html = parseToHtml(text);

		assertEquals("<p><a href=\"#abc\">[abc]</a></p>\n", html);
	}

	@Test
	public void testInList() {
		String text = """
				* <<xxx>>
				* <<file.adoc#yyy>>
				* xref:yyy[My link]
				""";

		String html = parseToHtml(text);

		String expected = """
				<ul>\
				<li><a href="#xxx">[xxx]</a></li>\
				<li><a href="file.adoc#yyy">[file#yyy]</a></li>\
				<li><a href="#yyy">My link</a></li>\
				</ul>""";
		assertEquals(expected, html);
	}

	@Test
	public void testXrefAndAnchor() {
		String text = "Lorem <<xxx, link>> Ipsum [[xxx]].";

		String html = parseToHtml(text);

		String expected = "<p>Lorem <a href=\"#xxx\">link</a> Ipsum <a id=\"xxx\"></a>.</p>\n";
		assertEquals(expected, html);
	}

	@Test
	public void testXrefAndAnchorMacro() {
		String text = "Lorem xref:yyy[My link] Ipsum anchor:yyy[].";

		String html = parseToHtml(text);

		String expected = "<p>Lorem <a href=\"#yyy\">My link</a> Ipsum <a id=\"yyy\"></a>.</p>\n";
		assertEquals(expected, html);
	}
}
