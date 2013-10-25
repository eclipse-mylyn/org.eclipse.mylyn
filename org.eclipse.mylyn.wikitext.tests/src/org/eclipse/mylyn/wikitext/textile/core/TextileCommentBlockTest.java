/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.textile.core;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.junit.Before;
import org.junit.Test;

public class TextileCommentBlockTest {

	private MarkupParser parser;

	private TextileLanguage markupLanguage;

	@Before
	public void initParser() throws IOException {
		parser = new MarkupParser();
		markupLanguage = new TextileLanguage();
		parser.setMarkupLanguage(markupLanguage);
	}

	@Test
	public void testSimpleComment() {
		assertTextile("<p>one two</p><p>three</p>", "one two\n\n###. comment\n\nthree");
	}

	@Test
	public void testMultiLineComment() {
		assertTextile("<p>one two</p><p>three</p>", "one two\n\n###. comment\nline two\nline three\n\nthree");
	}

	@Test
	public void testNotAComment() {
		assertTextile("<p>one two</p><p>###.nocomment</p><p>three</p>", "one two\n\n###.nocomment\n\nthree");
	}

	@Test
	public void testNotAComment2() {
		assertTextile("<p>one two<br/>###.nocomment</p><p>three</p>", "one two\n###.nocomment\n\nthree");
	}

	@Test
	public void testExtendedComment() {
		assertTextile("<p>para</p>", "###.. extended comment\n\n\nwith more\n\np. para");
	}

	private void assertTextile(String expectedHtml, String textile) {
		String html = toHtml(textile);
		assertEquals(expectedHtml, html);
	}

	private String toHtml(String textile) {
		Writer writer = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		builder.setEmitAsDocument(false);
		parser.setBuilder(builder);
		parser.parse(textile);
		return writer.toString();
	}
}
