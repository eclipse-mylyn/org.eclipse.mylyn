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
import static org.junit.Assert.assertNotNull;
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
		thrown.expect(IllegalArgumentException.class);
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
		assertNotNull(builder.pushBlockStrategy(BlockType.CODE));
	}

	@Test
	public void blockParagraphSupported() {
		assertSupportedBlock("<p>test</p>", BlockType.PARAGRAPH);
	}

	@Test
	public void blockCodeSupported() {
		assertSupportedBlock("<pre><code>test</code></pre>", BlockType.CODE);
	}

	@Test
	public void blockDivSupported() {
		assertSupportedBlock("<div>test</div>", BlockType.DIV);
	}

	@Test
	public void blockPreformattedSupported() {
		assertSupportedBlock("<pre>test</pre>", BlockType.PREFORMATTED);
	}

	@Test
	public void blockQuoteSupported() {
		assertSupportedBlock("<blockquote>test</blockquote>", BlockType.QUOTE);
	}

	@Test
	public void blockParagraphUnsuupported() {
		assertUnsupportedBlock("\ntest\n", BlockType.PARAGRAPH, BlockType.CODE);
	}

	@Test
	public void blockCodeUnsuupported() {
		assertUnsupportedBlock("<pre>test</pre>", BlockType.CODE, BlockType.PREFORMATTED);
	}

	@Test
	public void blockCodeUnsuupportedToPara() {
		assertUnsupportedBlock("<p>test</p>", BlockType.CODE, BlockType.PARAGRAPH);
	}

	@Test
	public void blockCodeUnsuupportedWithoutFallback() {
		assertUnsupportedBlock("\ntest\n", BlockType.CODE, BlockType.DIV);
	}

	@Test
	public void blockDivUnsuupported() {
		assertUnsupportedBlock("<p>test</p>", BlockType.DIV, BlockType.PARAGRAPH);
	}

	@Test
	public void blockPreformattedUnsuupported() {
		assertUnsupportedBlock("<p>test</p>", BlockType.PREFORMATTED, BlockType.PARAGRAPH);
	}

	@Test
	public void blockQuoteUnsuupported() {
		assertUnsupportedBlock("<p>test</p>", BlockType.QUOTE, BlockType.PARAGRAPH);
	}

	@Test
	public void blockQuoteUnsuupportedWithoutFallback() {
		assertUnsupportedBlock("\ntest\n", BlockType.QUOTE, BlockType.CODE);
	}

	@Test
	public void blockUnsupported() {
		builder.setSupportedBlockTypes(Sets.newHashSet(BlockType.PARAGRAPH));
		builder.beginBlock(BlockType.DIV, new Attributes());
		builder.characters("test");
		builder.endBlock();
		assertContent("<p>test</p>");
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
		assertContent("<p>test</p><p>test2</p>");
	}

	@Test
	public void blockBulletedListSupported() {
		builder.setSupportedBlockTypes(Sets.newHashSet(BlockType.BULLETED_LIST));
		buildList(BlockType.BULLETED_LIST);
		assertContent("<ul><li>test 0</li><li>test 1</li></ul>");
	}

	@Test
	public void blockBulletedListUnsupported() {
		builder.setSupportedBlockTypes(Sets.newHashSet(BlockType.PARAGRAPH));
		buildList(BlockType.BULLETED_LIST);
		assertContent("\n<p>test 0</p><p>test 1</p>\n");
	}

	@Test
	public void blockNumericListSupported() {
		builder.setSupportedBlockTypes(Sets.newHashSet(BlockType.NUMERIC_LIST));
		buildList(BlockType.NUMERIC_LIST);
		assertContent("<ol><li>test 0</li><li>test 1</li></ol>");
	}

	@Test
	public void blockNumericListUnsupported() {
		builder.setSupportedBlockTypes(Sets.newHashSet(BlockType.PARAGRAPH));
		buildList(BlockType.NUMERIC_LIST);
		assertContent("\n<p>test 0</p><p>test 1</p>\n");
	}

	@Test
	public void testTableSupported() {
		builder.setSupportedBlockTypes(Sets.newHashSet(BlockType.TABLE));
		buildTable();
		assertContent("<table><tr><td>test 0/0</td><td>test 1/0</td><td>test 2/0</td></tr><tr><td>test 0/1</td><td>test 1/1</td><td>test 2/1</td></tr></table>");
	}

	@Test
	public void testTableUnsupported() {
		builder.setSupportedBlockTypes(Sets.newHashSet(BlockType.PARAGRAPH));
		buildTable();
		assertContent("\n\n<p>test 0/0</p><p>test 1/0</p><p>test 2/0</p>\n\n<p>test 0/1</p><p>test 1/1</p><p>test 2/1</p>\n\n");
	}

	private void buildTable() {
		builder.beginBlock(BlockType.TABLE, new Attributes());
		for (int y = 0; y < 2; ++y) {
			builder.beginBlock(BlockType.TABLE_ROW, new Attributes());
			for (int x = 0; x < 3; ++x) {
				builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());
				builder.characters("test " + x + "/" + y);
				builder.endBlock();
			}
			builder.endBlock();
		}
		builder.endBlock();
	}

	protected void buildList(BlockType listType) {
		builder.beginBlock(listType, new Attributes());
		for (int x = 0; x < 2; ++x) {
			builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
			builder.characters("test " + x);
			builder.endBlock();
		}
		builder.endBlock();
	}

	private void assertSupportedBlock(String expected, BlockType blockType) {
		builder.setSupportedBlockTypes(Sets.newHashSet(blockType));
		builder.beginBlock(blockType, new Attributes());
		builder.characters("test");
		builder.endBlock();
		assertContent(expected);
	}

	private void assertUnsupportedBlock(String expected, BlockType unsupported, BlockType supported) {
		builder.setSupportedBlockTypes(Sets.newHashSet(supported));
		builder.beginBlock(unsupported, new Attributes());
		builder.characters("test");
		builder.endBlock();
		assertContent(expected);
	}

	private void assertContent(String expectedContent) {
		assertEquals(expectedContent, writer.toString());
	}
}
