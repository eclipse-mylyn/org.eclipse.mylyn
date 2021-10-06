/*******************************************************************************
 * Copyright (c) 2007, 2010 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.parser.builder;

import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.mediawiki.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.DitaTopicDocumentBuilder;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.eclipse.mylyn.wikitext.util.DefaultXmlStreamWriter;
import org.junit.Before;
import org.junit.Test;

public class DitaTopicDocumentBuilderTest {

	private MarkupParser parser;

	private StringWriter out;

	private DitaTopicDocumentBuilder builder;

	@Before
	public void setUp() {
		parser = new MarkupParser();
		parser.setMarkupLanguage(new TextileLanguage());
		out = new StringWriter();
		builder = new DitaTopicDocumentBuilder(out);
		parser.setBuilder(builder);
	}

	@Test
	public void testDiv() {
		builder.beginDocument();
		builder.beginBlock(BlockType.DIV, new Attributes());

		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("foo");
		builder.endBlock(); // PARAGRAPH

		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("bar");
		builder.endBlock(); // PARAGRAPH

		builder.endBlock(); // DIV
		builder.endDocument();

		String dita = out.toString();

		assertTrue(Pattern.compile(".*?<topic>\\s*<title></title>\\s*<body>\\s*<p>foo</p>\\s*<p>bar</p>\\s*</body>.*",
				Pattern.DOTALL).matcher(dita).matches());
	}

	@Test
	public void testNoFormatting() {
		parser = new MarkupParser();
		parser.setMarkupLanguage(new TextileLanguage());
		out = new StringWriter();
		DefaultXmlStreamWriter xmlStreamWriter = new DefaultXmlStreamWriter(out);
		builder = new DitaTopicDocumentBuilder(xmlStreamWriter, false);
		parser.setBuilder(builder);

		parser.parse("h1. Title1\n\nsome content in a para");

		xmlStreamWriter.close();

		String dita = out.toString();

		assertTrue(dita.contains(
				"<topic id=\"Title1\"><title>Title1</title><body><p>some content in a para</p></body></topic>"));
	}

	@Test
	public void testSpanLink() {
		builder.beginDocument();
		builder.beginBlock(BlockType.DIV, new Attributes());

		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		LinkAttributes attributes = new LinkAttributes();
		attributes.setHref("#test1234");
		builder.beginSpan(SpanType.LINK, attributes);
		builder.beginSpan(SpanType.EMPHASIS, new Attributes());
		builder.characters("link text");
		builder.endSpan();
		builder.endSpan();
		builder.endBlock(); // PARAGRAPH

		builder.endBlock(); // DIV
		builder.endDocument();

		String dita = out.toString();

		assertTrue(Pattern.compile("<xref href=\"#test1234\">\\s*<i>link text</i>\\s*</xref>").matcher(dita).find());
	}

	@Test
	public void testImageWithCaption() {
		parser.setMarkupLanguage(new MediaWikiLanguage());

		parser.parse("[[Image:images/editor-assist-proposals.png|alt=Alternative text|Caption text.]]");

		String dita = out.toString();

		assertTrue(Pattern.compile(
				"<fig>\\s*<title>Caption text.</title>\\s*<image href=\"images/editor-assist-proposals.png\" alt=\"Alternative text\"/>\\s*</fig>",
				Pattern.DOTALL).matcher(dita).find());
	}
}
