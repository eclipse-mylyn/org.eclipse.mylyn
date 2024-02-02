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
 *     Max Rydahl Andersen - Bug 474084: initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for asciidoc comment block elements.
 *
 * @author Max Rydahl Andersen
 */
public class AsciiDocLanguageCommentBlockElementsTest extends AsciiDocLanguageTestBase {

	@Test
	public void blockComment() {
		String html = parseToHtml("""
				////
				ignore this
				ignore that
				////""");
		assertEquals("", html);
	}

	@Test
	public void blockCommentAndContent() {
		String html = parseToHtml("""
				////
				ignore this
				ignore that
				////
				some content""");
		assertEquals("<p>some content</p>\n", html);
	}

	@Test
	public void blockCommentWithWhiteSpace() {
		String html = parseToHtml("""
				////\s
				ignore this
				ignore that
				////""");
		assertEquals("", html);
	}

	@Test
	public void blockCommentWithWhiteSpaceAndContent() {
		String html = parseToHtml("""
				////\s
				ignore this
				ignore that
				////
				some content""");
		assertEquals("<p>some content</p>\n", html);
	}

	@Test
	public void blockCommentWithWhiteSpacesAtEndAndContent() {
		String html = parseToHtml("""
				////
				ignore this
				ignore that
				////\s\s\s\s
				some content""");
		assertEquals("<p>some content</p>\n", html);
	}

	@Test
	public void blockCommentWithContent() {
		String html = parseToHtml("""
				This is
				////
				ignore this
				ignore that
				////
				two lines""");
		assertEquals("<p>This is</p>\n<p>two lines</p>\n", html);
	}

}
