/*******************************************************************************
 * Copyright (c) 2013, 2024 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.html.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.junit.Before;
import org.junit.Test;

public class HtmlSubsetDocumentBuilderTest {

	private StringWriter writer;

	private HtmlSubsetDocumentBuilder builder;

	private HtmlDocumentBuilder delegate;

	@Before
	public void before() {
		writer = new StringWriter();
		delegate = new HtmlDocumentBuilder(writer);
		delegate.setEmitAsDocument(false);
		builder = new HtmlSubsetDocumentBuilder(delegate);
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.PARAGRAPH)));
		builder.setSupportedSpanTypes(new HashSet<>(Arrays.asList(SpanType.BOLD)), Collections.emptyList());
		builder.setSupportedHeadingLevel(3);
		builder.beginDocument();
	}

	@Test
	public void createNullWriter() {
		NullPointerException npe = assertThrows(NullPointerException.class,
				() -> new HtmlSubsetDocumentBuilder(null, false));
		assertTrue(npe.getMessage().contains("Must provide a writer"));
	}

	@Test
	public void createNoDelegate() {
		NullPointerException npe = assertThrows(NullPointerException.class, () -> new HtmlSubsetDocumentBuilder(null));
		assertTrue(npe.getMessage().contains("Must provide a delegate"));
	}

	@Test
	public void create() {
		writer = new StringWriter();
		builder = new HtmlSubsetDocumentBuilder(writer, false);
		builder.characters("test");
		builder.flush();
		assertEquals("test", writer.toString());
	}

	@Test
	public void lineBreak() {
		builder.beginDocument();
		builder.lineBreak();
		builder.endDocument();
		assertContent("<br/>");
	}

	@Test
	public void horizontalRule() {
		builder.beginDocument();
		builder.horizontalRule();
		builder.endDocument();
		assertContent("<hr/>");
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
		builder.image(new Attributes(), "image.png");
		assertContent("<img border=\"0\" src=\"image.png\"/>");
	}

	@Test
	public void setDoesntSupportImages() {
		builder.setSupportsImages(false);
		builder.image(new Attributes(), "image.png");
		assertContent("");
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
	public void headingUnsupported() {
		builder.beginHeading(4, new Attributes());
		builder.characters("test");
		builder.endHeading();
		assertContent("<p><b>test</b></p>");
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

	@Test(expected = NullPointerException.class)
	public void setSupportedBlockTypesNull() {
		builder.setSupportedBlockTypes(null);
	}

	@Test
	public void setSupportedBlockTypesEmpty() {
		builder.setSupportedBlockTypes(new HashSet<>());
		assertSame(UnsupportedBlockStrategy.instance, builder.pushBlockStrategy(BlockType.PARAGRAPH, new Attributes()));
	}

	@Test
	public void supportedBlockTypes() {
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.PARAGRAPH)));
		assertSame(SupportedBlockStrategy.instance, builder.pushBlockStrategy(BlockType.PARAGRAPH, new Attributes()));
	}

	@Test
	public void unsupportedBlockTypes() {
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.PARAGRAPH)));
		assertNotNull(builder.pushBlockStrategy(BlockType.CODE, new Attributes()));
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
	public void blockParagraphUnsupported() {
		assertUnsupportedBlock("<div>test</div>", BlockType.PARAGRAPH, BlockType.DIV);
	}

	@Test
	public void blockParagraphUnsupportedWithoutFallback() {
		assertUnsupportedBlock("test", BlockType.PARAGRAPH, BlockType.CODE);
	}

	@Test
	public void blockCodeUnsupported() {
		assertUnsupportedBlock("<pre>test</pre>", BlockType.CODE, BlockType.PREFORMATTED);
	}

	@Test
	public void blockCodeUnsupportedToPara() {
		assertUnsupportedBlock("<p>test</p>", BlockType.CODE, BlockType.PARAGRAPH);
	}

	@Test
	public void blockCodeUnsupportedWithoutFallback() {
		assertUnsupportedBlock("test", BlockType.CODE, BlockType.LIST_ITEM);
	}

	@Test
	public void blockDivUnsupported() {
		assertUnsupportedBlock("<p>test</p>", BlockType.DIV, BlockType.PARAGRAPH);
	}

	@Test
	public void blockPreformattedUnsupported() {
		assertUnsupportedBlock("<p>test</p>", BlockType.PREFORMATTED, BlockType.PARAGRAPH);
	}

	@Test
	public void blockQuoteUnsupported() {
		assertUnsupportedBlock("<p>test</p>", BlockType.QUOTE, BlockType.PARAGRAPH);
	}

	@Test
	public void blockQuoteUnsupportedWithoutFallback() {
		assertUnsupportedBlock("test", BlockType.QUOTE, BlockType.CODE);
	}

	@Test
	public void blockUnsupported() {
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.PARAGRAPH)));
		builder.beginBlock(BlockType.DIV, new Attributes());
		builder.characters("test");
		builder.endBlock();
		assertContent("<p>test</p>");
	}

	@Test
	public void blockSupportedUnsupportedCombined() {
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.PARAGRAPH)));
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
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.BULLETED_LIST)));
		buildList(BlockType.BULLETED_LIST);
		assertContent("<ul><li>test 0</li><li>test 1</li></ul>");
	}

	@Test
	public void blockBulletedListUnsupported() {
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.PARAGRAPH)));
		buildList(BlockType.BULLETED_LIST);
		assertContent("<p>test 0</p><p>test 1</p>");
	}

	@Test
	public void blockNumericListSupported() {
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.NUMERIC_LIST)));
		buildList(BlockType.NUMERIC_LIST);
		assertContent("<ol><li>test 0</li><li>test 1</li></ol>");
	}

	@Test
	public void blockNumericListUnsupported() {
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.PARAGRAPH)));
		buildList(BlockType.NUMERIC_LIST);
		assertContent("<p>test 0</p><p>test 1</p>");
	}

	@Test
	public void testTableSupported() {
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.TABLE)));
		buildTable();
		assertContent(
				"<table><tr><td>test 0/0</td><td>test 1/0</td><td>test 2/0</td></tr><tr><td>test 0/1</td><td>test 1/1</td><td>test 2/1</td></tr></table>");
	}

	@Test
	public void testTableUnsupported() {
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.PARAGRAPH)));
		buildTable();
		assertContent(
				"<p>test 0/0</p><p>test 1/0</p><p>test 2/0</p><br/><br/><p>test 0/1</p><p>test 1/1</p><p>test 2/1</p>");
	}

	@Test
	public void testParagraphUnsupported() {
		builder.setSupportedBlockTypes(Collections.emptySet());
		assertUnsupportedBlock("test", BlockType.PARAGRAPH);
	}

	@Test
	public void flush() {
		builder.characters("test");
		builder.flush();
		assertContent("test");
	}

	@Test
	public void flushAfterEndDocument() {
		builder.characters("test");
		builder.endDocument();
		builder.flush();
		assertContent("test");
	}

	@Test
	public void testUnsupportedMultiple() {
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.CODE)));
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("test");
		builder.endBlock();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("test2");
		builder.endBlock();
		assertContent("test<br/><br/>test2");
	}

	@Test
	public void testSupportedUnsupportedCombination() {
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.CODE)));
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("test");
		builder.endBlock();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("test2");
		builder.endBlock();
		assertContent("test<br/><br/><pre><code>test2</code></pre>");
	}

	@Test
	public void testUnsuportedBeforeHeading() {
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.CODE)));
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("test");
		builder.endBlock();
		builder.beginHeading(2, new Attributes());
		builder.characters("heading text");
		builder.endHeading();
		assertContent("test<br/><br/><h2>heading text</h2>");
	}

	@Test
	public void testUnsuportedBeforeImplicitParagraph() {
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(BlockType.CODE)));
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("test");
		builder.endBlock();
		builder.characters("test2");
		assertContent("test<br/><br/>test2");
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
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(blockType)));
		assertUnsupportedBlock(expected, blockType);
	}

	private void assertUnsupportedBlock(String expected, BlockType unsupported, BlockType supported) {
		builder.setSupportedBlockTypes(new HashSet<>(Arrays.asList(supported)));
		assertUnsupportedBlock(expected, unsupported);
	}

	private void assertUnsupportedBlock(String expected, BlockType unsupported) {
		builder.beginBlock(unsupported, new Attributes());
		builder.characters("test");
		builder.endBlock();
		assertContent(expected);
	}

	@Test
	public void supportedSpanTypes() {
		builder.setSupportedSpanTypes(new HashSet<>(Arrays.asList(SpanType.BOLD)), Collections.emptyList());
		assertSame(SupportedSpanStrategy.instance, builder.pushSpanStrategy(SpanType.BOLD, new Attributes()));
	}

	@Test
	public void unsupportedSpanTypes() {
		builder.setSupportedSpanTypes(new HashSet<>(Arrays.asList(SpanType.BOLD)), Collections.emptyList());
		assertNotNull(builder.pushSpanStrategy(SpanType.EMPHASIS, new Attributes()));
	}

	@Test
	public void spanBoldSupported() {
		assertSupportedSpan("<b>test</b>", SpanType.BOLD);
	}

	@Test
	public void spanCitationSupported() {
		assertSupportedSpan("<cite>test</cite>", SpanType.CITATION);
	}

	@Test
	public void spanCodeSupported() {
		assertSupportedSpan("<code>test</code>", SpanType.CODE);
	}

	@Test
	public void spanDeletedSupported() {
		assertSupportedSpan("<del>test</del>", SpanType.DELETED);
	}

	@Test
	public void spanEmphasisSupported() {
		assertSupportedSpan("<em>test</em>", SpanType.EMPHASIS);
	}

	@Test
	public void spanInsertedSupported() {
		assertSupportedSpan("<ins>test</ins>", SpanType.INSERTED);
	}

	@Test
	public void spanItalicSupported() {
		assertSupportedSpan("<i>test</i>", SpanType.ITALIC);
	}

	@Test
	public void spanLinkSupported() {
		assertSupportedSpan("<a>test</a>", SpanType.LINK);
	}

	@Test
	public void spanMonospaceSupported() {
		assertSupportedSpan("<tt>test</tt>", SpanType.MONOSPACE);
	}

	@Test
	public void spanQuoteSupported() {
		assertSupportedSpan("<q>test</q>", SpanType.QUOTE);
	}

	@Test
	public void spanSpanSupported() {
		assertSupportedSpan("<span>test</span>", SpanType.SPAN);
	}

	@Test
	public void spanStrongSupported() {
		assertSupportedSpan("<strong>test</strong>", SpanType.STRONG);
	}

	@Test
	public void spanSubscriptSupported() {
		assertSupportedSpan("<sub>test</sub>", SpanType.SUBSCRIPT);
	}

	@Test
	public void spanSuperscriptSupported() {
		assertSupportedSpan("<sup>test</sup>", SpanType.SUPERSCRIPT);
	}

	@Test
	public void spanUnderlinedSupported() {
		assertSupportedSpan("<u>test</u>", SpanType.UNDERLINED);
	}

	@Test
	public void spanMarkSupported() {
		assertSupportedSpan("<mark>test</mark>", SpanType.MARK);
	}

	@Test
	public void spanBoldUnsupported() {
		assertUnsupportedSpan("<strong>test</strong>", SpanType.BOLD, SpanType.STRONG);
	}

	@Test
	public void spanBoldUnsupportedNoFallback() {
		assertUnsupportedSpan("test", SpanType.BOLD, SpanType.SPAN);
	}

	@Test
	public void spanCitationUnsupported() {
		assertUnsupportedSpan("test", SpanType.CITATION, SpanType.BOLD);
	}

	@Test
	public void spanCodeUnsupported() {
		assertUnsupportedSpan("<tt>test</tt>", SpanType.CODE, SpanType.MONOSPACE);
	}

	@Test
	public void spanCodeUnsupportedNoFallback() {
		assertUnsupportedSpan("test", SpanType.CODE, SpanType.SPAN);
	}

	@Test
	public void spanDeletedUnsupported() {
		assertUnsupportedSpan("test", SpanType.DELETED, SpanType.BOLD);
	}

	@Test
	public void spanEmphasisUnsupported() {
		assertUnsupportedSpan("<i>test</i>", SpanType.EMPHASIS, SpanType.ITALIC);
	}

	@Test
	public void spanEmphasisUnsupportedNoFallback() {
		assertUnsupportedSpan("test", SpanType.EMPHASIS, SpanType.BOLD);
	}

	@Test
	public void spanInsertedUnsupported() {
		assertUnsupportedSpan("test", SpanType.INSERTED, SpanType.BOLD);
	}

	@Test
	public void spanItalicUnsupported() {
		assertUnsupportedSpan("<em>test</em>", SpanType.ITALIC, SpanType.EMPHASIS);
	}

	@Test
	public void spanItalicUnsupportedNoFallback() {
		assertUnsupportedSpan("test", SpanType.ITALIC, SpanType.BOLD);
	}

	@Test
	public void spanLinkUnsupported() {
		assertUnsupportedSpan("test", SpanType.LINK, SpanType.BOLD);
	}

	@Test
	public void spanMonospaceUnsupported() {
		assertUnsupportedSpan("<code>test</code>", SpanType.MONOSPACE, SpanType.CODE);
	}

	@Test
	public void spanMonospaceUnsupportedNoFallback() {
		assertUnsupportedSpan("test", SpanType.MONOSPACE, SpanType.BOLD);
	}

	@Test
	public void spanQuoteUnsupported() {
		assertUnsupportedSpan("test", SpanType.QUOTE, SpanType.BOLD);
	}

	@Test
	public void spanSpanUnsupported() {
		assertUnsupportedSpan("test", SpanType.SPAN, SpanType.BOLD);
	}

	@Test
	public void spanStrongUnsupported() {
		assertUnsupportedSpan("<b>test</b>", SpanType.STRONG, SpanType.BOLD);
	}

	@Test
	public void spanStrongUnsupportedNoFallback() {
		assertUnsupportedSpan("test", SpanType.STRONG, SpanType.SPAN);
	}

	@Test
	public void spanSubscriptUnsupported() {
		assertUnsupportedSpan("test", SpanType.SUBSCRIPT, SpanType.BOLD);
	}

	@Test
	public void spanSuperscriptUnsupported() {
		assertUnsupportedSpan("test", SpanType.SUPERSCRIPT, SpanType.BOLD);
	}

	@Test
	public void spanUnderlinedUnsupported() {
		assertUnsupportedSpan("test", SpanType.UNDERLINED, SpanType.BOLD);
	}

	@Test
	public void spanFontTag() {
		builder.setSupportedSpanTypes(new HashSet<>(Arrays.asList(SpanType.BOLD)),
				new ArrayList<>(Arrays.asList(new FontElementStrategy())));

		builder.beginSpan(SpanType.SPAN, new Attributes(null, null, "color: blue", null));
		builder.characters("test");
		builder.endSpan();
		builder.characters(" ");
		builder.beginSpan(SpanType.SPAN, new Attributes(null, null, "font-size: 15pt", null));
		builder.characters("test2");
		builder.endSpan();
		builder.characters(" ");
		builder.beginSpan(SpanType.SPAN, new Attributes(null, null, "font-size: 16em; color: red", null));
		builder.characters("test2");
		builder.endSpan();

		assertContent(
				"<font color=\"blue\">test</font> <font size=\"15pt\">test2</font> <font color=\"red\" size=\"16em\">test2</font>");
	}

	@Test
	public void spanSubstitution() {
		builder.setElementNameOfSpanType(SpanType.BOLD, "new-bold");

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text");
		builder.endSpan();

		assertContent("<new-bold>text</new-bold>");
	}

	@Test
	public void setXhtmlStrict() {
		assertFalse(builder.getDelegate().isXhtmlStrict());
		builder.setXhtmlStrict(true);
		assertTrue(builder.getDelegate().isXhtmlStrict());
		builder.setXhtmlStrict(false);
		assertFalse(builder.getDelegate().isXhtmlStrict());
	}

	@Test
	public void xhtmlStrictImplicitBlock() {
		builder.setXhtmlStrict(true);
		builder.characters("test");
		builder.endDocument();
		assertContent("<p>test</p>");
	}

	@Test
	public void xhtmlStrictImplicitBlockFlush() {
		builder.setXhtmlStrict(true);
		builder.characters("test");
		builder.flush();
		assertContent("<p>test</p>");
	}

	private void assertSupportedSpan(String expected, SpanType spanType) {
		builder.setSupportedSpanTypes(new HashSet<>(Arrays.asList(spanType)), Collections.emptyList());
		builder.beginSpan(spanType, new Attributes());
		builder.characters("test");
		builder.endSpan();
		assertContent(expected);
	}

	private void assertUnsupportedSpan(String expected, SpanType unsupported, SpanType supported) {
		builder.setSupportedSpanTypes(new HashSet<>(Arrays.asList(supported)), Collections.emptyList());
		builder.beginSpan(unsupported, new Attributes());
		builder.characters("test");
		builder.endSpan();
		assertContent(expected);
	}

	private void assertContent(String expectedContent) {
		builder.endDocument();
		assertEquals(expectedContent, writer.toString());
	}
}
