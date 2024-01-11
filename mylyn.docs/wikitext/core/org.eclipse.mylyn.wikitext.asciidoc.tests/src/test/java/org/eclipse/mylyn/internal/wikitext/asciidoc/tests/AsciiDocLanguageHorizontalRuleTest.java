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

public class AsciiDocLanguageHorizontalRuleTest extends AsciiDocLanguageTestBase {

	@Test
	public void testHorizontalRule() {
		String text = "'''";

		String html = parseToHtml(text);

		assertEquals("<hr/>", html);
	}

	@Test
	public void testHorizontalRuleWithTrailingSpaces() {
		String text = "'''  ";

		String html = parseToHtml(text);

		assertEquals("<hr/>", html);
	}

	@Test
	public void testHorizontalRuleWithTrailingTab() {
		String text = "'''\t";

		String html = parseToHtml(text);

		assertEquals("<hr/>", html);
	}

	@Test
	public void testHorizontalRuleWithMoreChars() {
		String text = "''''";

		String html = parseToHtml(text);

		assertEquals("<hr/>", html);
	}

	@Test
	public void testNoHorizontalRuleTextBefore() {
		String text = "x '''";

		String html = parseToHtml(text);

		//the created html should not contain "<hr/>"
		assertEquals("<p>x '''</p>\n", html);
	}

	@Test
	public void testNoHorizontalRuleSpaceBefore() {
		String text = " '''";

		String html = parseToHtml(text);

		//the created html should not contain "<hr/>"
		assertEquals("<p> '''</p>\n", html);
	}

	@Test
	public void testNoHorizontalRuleTextAfter() {
		String text = "''' lorem";

		String html = parseToHtml(text);

		//the created html should not contain "<hr/>"
		assertEquals("<p>''' lorem</p>\n", html);
	}
}
