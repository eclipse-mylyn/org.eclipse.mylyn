/*******************************************************************************
 * Copyright (c) 2015, 2016 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
	public void blockCommentWithWhiteSpace() {
		String html = parseToHtml("//// \n" //
				+ "ignore this\n" //
				+ "ignore that\n" //
				+ "////");
		assertEquals("", html);
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
