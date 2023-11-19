/*******************************************************************************
 * Copyright (c) 2015, 2016 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Max Rydahl Andersen - Bug 474084: initial API and implementation
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
		String html = parseToHtml("////\n" //
				+ "ignore this\n" //
				+ "ignore that\n" //
				+ "////");
		assertEquals("", html);
	}

	@Test
	public void blockCommentAndContent() {
		String html = parseToHtml("////\n" //
				+ "ignore this\n" //
				+ "ignore that\n" //
				+ "////\n" //
				+ "some content");
		assertEquals("<p>some content</p>\n", html);
	}

	@Test
	public void blockCommentWithWhiteSpace() {
		String html = parseToHtml("//// \n" //
				+ "ignore this\n" //
				+ "ignore that\n" //
				+ "////");
		assertEquals("", html);
	}

	@Test
	public void blockCommentWithWhiteSpaceAndContent() {
		String html = parseToHtml("//// \n" //
				+ "ignore this\n" //
				+ "ignore that\n" //
				+ "////\n" //
				+ "some content");
		assertEquals("<p>some content</p>\n", html);
	}

	@Test
	public void blockCommentWithWhiteSpacesAtEndAndContent() {
		String html = parseToHtml("////\n" //
				+ "ignore this\n" //
				+ "ignore that\n" //
				+ "////    \n" //
				+ "some content");
		assertEquals("<p>some content</p>\n", html);
	}

	@Test
	public void blockCommentWithContent() {
		String html = parseToHtml("This is\n" //
				+ "////\n" //
				+ "ignore this\n" //
				+ "ignore that\n" //
				+ "////\n" //
				+ "two lines");
		assertEquals("<p>This is</p>\n<p>two lines</p>\n", html);
	}

}
