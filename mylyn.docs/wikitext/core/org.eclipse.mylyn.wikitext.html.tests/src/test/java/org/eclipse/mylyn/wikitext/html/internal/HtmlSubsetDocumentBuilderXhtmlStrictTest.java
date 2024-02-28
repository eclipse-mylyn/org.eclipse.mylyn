/*******************************************************************************
 * Copyright (c) 2014, 2024 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tomasz Zarna - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.html.internal;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class HtmlSubsetDocumentBuilderXhtmlStrictTest {

	private StringWriter writer;

	private HtmlSubsetDocumentBuilder builder;

	private HtmlDocumentBuilder delegate;

	@Before
	public void before() {
		writer = new StringWriter();
		delegate = new HtmlDocumentBuilder(writer);
		delegate.setEmitAsDocument(false);
		builder = new HtmlSubsetDocumentBuilder(delegate);
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.PARAGRAPH, BlockType.DIV,
				BlockType.BULLETED_LIST, BlockType.LIST_ITEM, BlockType.QUOTE, BlockType.PREFORMATTED)));
		builder.setSupportedSpanTypes(new HashSet<>(Arrays.asList(SpanType.BOLD)), Collections.emptyList());
		builder.setSupportedHeadingLevel(3);
		builder.setXhtmlStrict(true);
		builder.beginDocument();
	}

	@Test
	public void characters() {
		builder.characters("foo");
		assertContent("<p>foo</p>");
	}

	@Test
	public void charactersTwice() {
		builder.characters("foo");
		builder.characters("bar");
		assertContent("<p>foobar</p>");
	}

	@Test
	public void paragraphBlock() {
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("foo");
		builder.endBlock();
		assertContent("<p>foo</p>");
	}

	@Test
	public void boldSpan() {
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("foo");
		builder.endSpan();
		assertContent("<p><b>foo</b></p>");
	}

	@Test
	public void charactersAndBoldSpan() {
		builder.characters("foo");
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bar");
		builder.endSpan();
		assertContent("<p>foo<b>bar</b></p>");
	}

	@Test
	public void heading() {
		builder.beginHeading(1, new Attributes());
		builder.characters("foo");
		builder.endHeading();
		assertContent("<h1>foo</h1>");
	}

	@Test
	public void headingAndCharacters() {
		builder.beginHeading(1, new Attributes());
		builder.characters("foo");
		builder.endHeading();
		builder.characters("bar");
		assertContent("<h1>foo</h1><p>bar</p>");
	}

	@Test
	public void charactersAndHeading() {
		builder.characters("foo");
		builder.beginHeading(1, new Attributes());
		builder.characters("bar");
		builder.endHeading();
		assertContent("<p>foo</p><h1>bar</h1>");
	}

	@Test
	public void bulletedList() {
		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("foo");
		builder.endBlock();
		builder.endBlock();
		assertContent("<ul><li>foo</li></ul>");
	}

	@Test
	public void charactersAndDiv() {
		builder.characters("foo");
		builder.beginBlock(BlockType.DIV, new Attributes());
		builder.characters("bar");
		builder.endBlock();
		assertContent("<p>foo</p><div>bar</div>");
	}

	@Test
	public void paragraphInDiv() {
		builder.beginBlock(BlockType.DIV, new Attributes());
		builder.characters("foo");
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("bar");
		builder.endBlock();
		builder.characters("baz");
		builder.endBlock();
		assertContent("<div>foo<p>bar</p>baz</div>");
	}

	@Test
	public void paragraphAndCharacters() {
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("foo");
		builder.endBlock();
		builder.characters("bar");
		assertContent("<p>foo</p><p>bar</p>");
	}

	@Test
	public void paragraphInImplitParagraph() {
		builder.characters("foo");
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("bar");
		builder.endBlock();
		assertContent("<p>foo</p><p>bar</p>");
	}

	@Test
	public void implicitParagraphAndQuote() {
		builder.characters("foo");
		builder.beginBlock(BlockType.QUOTE, new Attributes());
		builder.characters("bar");
		builder.endBlock();
		assertContent("<p>foo</p><blockquote>bar</blockquote>");
	}

	@Test
	public void implicitParagraphAndPre() {
		builder.characters("foo");
		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		builder.characters("bar");
		builder.endBlock();
		assertContent("<p>foo</p><pre>bar</pre>");
	}

	@Test
	public void paragraphNotSupported() {
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.DIV)));
		builder.characters("foo");
		assertContent("<div>foo</div>");
	}

	@Test
	public void paragraphAndDivNotSupported() {
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.CODE)));
		builder.characters("foo");
		assertContent("foo");
	}

	@Test
	public void imageAndCharacters() {
		builder.image(new Attributes(), "foo.png");
		builder.characters("bar");
		assertContent("<p><img style=\"border-width: 0px;\" alt=\"\" src=\"foo.png\"/>bar</p>");
	}

	@Test
	public void emitImageWithoutImageSupport() {
		builder.setSupportsImages(false);
		builder.image(new Attributes(), "foo.png");
		builder.characters("bar");
		assertContent("<p>bar</p>");
	}

	@Test
	public void lineBreakAndCharacters() {
		builder.lineBreak();
		builder.characters("foo");
		assertContent("<p><br/>foo</p>");
	}

	@Test
	public void lineBreakAndParagraph() {
		builder.characters("foo");
		builder.lineBreak();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("bar");
		builder.endBlock();
		assertContent("<p>foo<br/></p><p>bar</p>");
	}

	@Test
	public void entityReference() {
		builder.entityReference("copy");
		assertContent("<p>&copy;</p>");
	}

	@Test
	public void acronym() {
		builder.acronym("ABC", "Always Be Cold");
		assertContent("<p><acronym title=\"Always Be Cold\">ABC</acronym></p>");
	}

	@Test
	public void link() {
		builder.link("foo", "bar");
		assertContent("<p><a href=\"foo\">bar</a></p>");
	}

	@Test
	public void imageLink() {
		builder.imageLink("foo", "bar.png");
		assertContent("<p><a href=\"foo\"><img style=\"border-width: 0px;\" alt=\"\" src=\"bar.png\"/></a></p>");
	}

	private void assertContent(String expectedContent) {
		builder.endDocument();
		assertEquals(expectedContent, writer.toString());
	}
}
