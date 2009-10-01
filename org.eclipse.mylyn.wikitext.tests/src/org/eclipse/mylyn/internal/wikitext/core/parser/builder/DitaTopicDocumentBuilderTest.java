/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.parser.builder;

import java.io.StringWriter;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.util.DefaultXmlStreamWriter;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

public class DitaTopicDocumentBuilderTest extends TestCase {

	private MarkupParser parser;

	private StringWriter out;

	private DitaTopicDocumentBuilder builder;

	@Override
	public void setUp() {
		parser = new MarkupParser();
		parser.setMarkupLanguage(new TextileLanguage());
		out = new StringWriter();
		builder = new DitaTopicDocumentBuilder(out);
		parser.setBuilder(builder);
	}

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
		System.out.println("DITA: \n" + dita);

		assertTrue(Pattern.compile(".*?<topic>\\s*<title></title>\\s*<body>\\s*<p>foo</p>\\s*<p>bar</p>\\s*</body>.*",
				Pattern.DOTALL).matcher(dita).matches());
	}

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
		System.out.println("DITA: \n" + dita);

		assertTrue(dita.contains("<topic id=\"Title1\"><title>Title1</title><body><p>some content in a para</p></body></topic>"));
	}

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
		System.out.println("DITA: \n" + dita);

		assertTrue(Pattern.compile("<xref href=\"#test1234\">\\s*<i>link text</i>\\s*</xref>").matcher(dita).find());
	}
}
