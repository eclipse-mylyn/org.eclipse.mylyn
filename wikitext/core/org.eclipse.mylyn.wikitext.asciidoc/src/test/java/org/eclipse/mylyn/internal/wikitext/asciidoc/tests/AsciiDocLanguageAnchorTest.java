/*******************************************************************************
 * Copyright (c) 2016 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AsciiDocLanguageAnchorTest extends AsciiDocLanguageTestBase {

	@Test
	public void testInText() {
		String text = "Lorem [[xxx]] ipsum";

		String html = parseToHtml(text);

		assertEquals("<p>Lorem <a id=\"xxx\"></a> ipsum</p>\n", html);
	}

	@Test
	public void testBeginText() {
		String text = "[[xxx]] Lorem *ipsum*";

		String html = parseToHtml(text);

		assertEquals("<p><a id=\"xxx\"></a> Lorem <strong>ipsum</strong></p>\n", html);
	}

	@Test
	public void testEndText() {
		String text = "Lorem ipsum [[xxx]]";

		String html = parseToHtml(text);

		assertEquals("<p>Lorem ipsum <a id=\"xxx\"></a></p>\n", html);
	}

	@Test
	public void testTextWithXreflabel() {
		String text = "[[bookmark-c,last paragraph]] Lorem ipsum";

		String html = parseToHtml(text);

		assertEquals("<p><a id=\"bookmark-c\"></a> Lorem ipsum</p>\n", html);
	}

	@Test
	public void testMacro() {
		String text = "Lorem anchor:xxx[] ipsum";

		String html = parseToHtml(text);

		assertEquals("<p>Lorem <a id=\"xxx\"></a> ipsum</p>\n", html);
	}

	@Test
	public void testMacroWithXreflabel() {
		String text = "Lorem anchor:xxx[yyy] ipsum";

		String html = parseToHtml(text);

		assertEquals("<p>Lorem <a id=\"xxx\"></a> ipsum</p>\n", html);
	}

	@Test
	public void testInTitle() {
		String text = "=== Lorem [[xxx]] ipsum";

		String html = parseToHtml(text);

		assertEquals("<h3 id=\"_lorem_a_id_xxx_a_ipsum\">Lorem <a id=\"xxx\"></a> ipsum</h3>", html);
	}

	@Test
	public void testBeginTitle() {
		String text = "=== [[xxx]] Lorem ipsum";

		String html = parseToHtml(text);

		assertEquals("<h3 id=\"_a_id_xxx_a_lorem_ipsum\"><a id=\"xxx\"></a> Lorem ipsum</h3>", html);
	}

	@Test
	public void endBeginTitle() {
		String text = "=== Lorem ipsum [[xxx]]";

		String html = parseToHtml(text);

		assertEquals("<h3 id=\"_lorem_ipsum_a_id_xxx_a\">Lorem ipsum <a id=\"xxx\"></a></h3>", html);
	}
}
