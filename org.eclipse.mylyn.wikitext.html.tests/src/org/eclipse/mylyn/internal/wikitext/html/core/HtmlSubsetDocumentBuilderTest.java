/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.html.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Sets;

public class HtmlSubsetDocumentBuilderTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private StringWriter writer;

	private HtmlSubsetDocumentBuilder builder;

	private HtmlDocumentBuilder delegate;

	@Before
	public void before() {
		writer = new StringWriter();
		delegate = new HtmlDocumentBuilder(writer);
		delegate.setEmitAsDocument(false);
		builder = new HtmlSubsetDocumentBuilder(delegate);
		builder.setSupportedBlockTypes(Sets.newHashSet(BlockType.PARAGRAPH));
	}

	@Test
	public void createNullWriter() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide a writer");
		new HtmlSubsetDocumentBuilder(null, false);
	}

	@Test
	public void createNoDelegate() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide a delegate");
		new HtmlSubsetDocumentBuilder(null);
	}

	@Test
	public void create() {
		writer = new StringWriter();
		builder = new HtmlSubsetDocumentBuilder(writer, false);
		builder.characters("test");
		assertContent("test");
	}

	@Test
	public void lineBreak() {
		builder.beginDocument();
		builder.lineBreak();
		builder.endDocument();
		assertContent("<br/>");
	}

	@Test
	public void charactersUnescaped() {
		builder.charactersUnescaped("<specialTag/>");
		assertContent("<specialTag/>");
	}

	@Test
	public void characters() {
		builder.characters("<specialTag/>");
		assertContent("&lt;specialTag/&gt;");
	}

	@Test
	public void entityReference() {
		builder.entityReference("copy");
		assertContent("&copy;");
	}

	@Test
	public void acronym() {
		builder.acronym("ABC", "Always Be Cold");
		assertContent("<acronym title=\"Always Be Cold\">ABC</acronym>");
	}

	@Test
	public void imageLink() {
		builder.imageLink("target", "image.png");
		assertContent("<a href=\"target\"><img border=\"0\" src=\"image.png\"/></a>");
	}

	@Test
	public void image() {
		builder.beginDocument();
		builder.image(new Attributes(), "image.png");
		builder.endDocument();
		assertContent("<img border=\"0\" src=\"image.png\"/>");
	}

	@Test
	public void link() {
		builder.link("target", "text");
		assertContent("<a href=\"target\">text</a>");
	}

	@Test
	public void heading() {
		builder.beginHeading(1, new Attributes());
		builder.characters("test");
		builder.endHeading();
		assertContent("<h1>test</h1>");
	}

	@Test
	public void block() {
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("test");
		builder.endBlock();
		assertContent("<p>test</p>");
	}

	@Test
	public void span() {
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("test");
		builder.endSpan();
		assertContent("<b>test</b>");
	}

	@Test
	public void setSupportedBlockTypesNull() {
		thrown.expect(NullPointerException.class);
		builder.setSupportedBlockTypes(null);
	}

	@Test
	public void setSupportedBlockTypesEmpty() {
		builder.setSupportedBlockTypes(Sets.<BlockType> newHashSet());
	}

	@Test
	public void supportedBlockTypes() {
		builder.setSupportedBlockTypes(Sets.newHashSet(BlockType.PARAGRAPH));
		assertSame(SupportedBlockStrategy.instance, builder.pushBlockStrategy(BlockType.PARAGRAPH));
	}

	@Test
	public void unsupportedBlockTypes() {
		builder.setSupportedBlockTypes(Sets.newHashSet(BlockType.PARAGRAPH));
		assertSame(UnsupportedBlockStrategy.instance, builder.pushBlockStrategy(BlockType.CODE));
	}

	@Test
	public void blockSupported() {
		builder.setSupportedBlockTypes(Sets.newHashSet(BlockType.PARAGRAPH));
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("test");
		builder.endBlock();
		assertContent("<p>test</p>");
	}

	@Test
	public void blockUnsupported() {
		builder.setSupportedBlockTypes(Sets.newHashSet(BlockType.PARAGRAPH));
		builder.beginBlock(BlockType.DIV, new Attributes());
		builder.characters("test");
		builder.endBlock();
		assertContent("\ntest\n");
	}

	@Test
	public void blockSupportedUnsupportedCombined() {
		builder.setSupportedBlockTypes(Sets.newHashSet(BlockType.PARAGRAPH));
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("test");
		builder.endBlock();
		builder.beginBlock(BlockType.DIV, new Attributes());
		builder.characters("test2");
		builder.endBlock();
		assertContent("<p>test</p>\ntest2\n");
	}

	private void assertContent(String expectedContent) {
		assertEquals(expectedContent, writer.toString());
	}
}
