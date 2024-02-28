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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.wikitext.html.HtmlLanguage;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentHandler;
import org.eclipse.mylyn.wikitext.util.XmlStreamWriter;
import org.junit.Test;

@SuppressWarnings("nls")
public class HtmlSubsetLanguageTest {

	@Test(expected = NullPointerException.class)
	public void createNullName() {
		new HtmlSubsetLanguage(null, null, 6, Set.of(BlockType.PARAGRAPH), Set.of(SpanType.BOLD), Map.of(),
				Collections.emptyList(), false, true);
	}

	@Test(expected = NullPointerException.class)
	public void createNullBlockTypes() {
		new HtmlSubsetLanguage("Test", null, 6, null, Set.of(SpanType.BOLD), Map.of(), Collections.emptyList(), false,
				true);
	}

	@Test(expected = NullPointerException.class)
	public void createNullSpanTypes() {
		new HtmlSubsetLanguage("Test", null, 6, Set.of(BlockType.PARAGRAPH), null, Map.of(), Collections.emptyList(),
				false, true);
	}

	@Test(expected = NullPointerException.class)
	public void createNullTagNameSubstitutions() {
		new HtmlSubsetLanguage("Test", null, 6, Set.of(BlockType.PARAGRAPH), Set.of(SpanType.BOLD), null,
				Collections.emptyList(), false, true);
	}

	@Test
	public void createInvalidHeadingLevel() {
		IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
				() -> new HtmlSubsetLanguage("Test", null, -1, Set.of(BlockType.PARAGRAPH), Set.of(SpanType.BOLD),
						Map.of(), Collections.emptyList(), false, true));
		assertTrue(iae.getMessage().contains("headingLevel must be between 0 and 6"));
	}

	@Test
	public void createWithUnsupportedSubstituted() {
		IllegalStateException ise = assertThrows(IllegalStateException.class,
				() -> new HtmlSubsetLanguage("Test", null, 6, Set.of(BlockType.PARAGRAPH), Set.of(SpanType.BOLD),
						Map.of(SpanType.ITALIC, "italic"), Collections.emptyList(), false, true));
		assertTrue(ise.getMessage()
				.contains("SpanType [ITALIC] is unsupported. Cannot add substitution to unsupported span types."));
	}

	@Test
	public void create() {
		HtmlSubsetLanguage language = newHtmlSubsetLanguage(BlockType.PARAGRAPH);
		assertEquals("Test", language.getName());
	}

	@Test
	public void supportedBlockTypes() {
		assertEquals(new HashSet<>(Arrays.asList(BlockType.PARAGRAPH, BlockType.CODE)),
				newHtmlSubsetLanguage(BlockType.PARAGRAPH, BlockType.CODE).getSupportedBlockTypes());
	}

	@Test
	public void supportedSpanTypes() {
		assertEquals(new HashSet<>(Arrays.asList(SpanType.BOLD, SpanType.EMPHASIS)),
				newHtmlSubsetLanguage(SpanType.BOLD, SpanType.EMPHASIS).getSupportedSpanTypes());
	}

	@Test
	public void tagNameSubstitutions() {
		assertEquals(Map.of(SpanType.EMPHASIS, "new-em"),
				newHtmlSubsetLanguage(SpanType.EMPHASIS).getTagNameSubstitutions());
	}

	@Test
	public void supportedHeadingLevel() {
		assertSupportedHeadingLevel(0);
		assertSupportedHeadingLevel(1);
		assertSupportedHeadingLevel(5);
		assertSupportedHeadingLevel(6);
		assertEquals(0, newHtmlSubsetLanguageWithHeadingLevel(0).getSupportedHeadingLevel());
	}

	@Test
	public void cloneSupported() {
		HtmlDocumentHandler documentHandler = new HtmlDocumentHandler() {

			@Override
			public void endDocument(HtmlDocumentBuilder builder, XmlStreamWriter writer) {
				// ignore
			}

			@Override
			public void beginDocument(HtmlDocumentBuilder builder, XmlStreamWriter writer) {
				// ignore
			}
		};
		HtmlSubsetLanguage language = new HtmlSubsetLanguage("Test", documentHandler, 6,
				new HashSet<>(Arrays.asList(BlockType.PARAGRAPH, BlockType.DIV, BlockType.QUOTE)),
				new HashSet<>(Arrays.asList(SpanType.CITATION, SpanType.EMPHASIS)), Map.of(SpanType.EMPHASIS, "new-em"),
				Collections.emptyList(), false, true);
		HtmlSubsetLanguage cloned = language.clone();

		assertEquals(language.getName(), cloned.getName());
		assertEquals(language.getSupportedBlockTypes(), cloned.getSupportedBlockTypes());
		assertEquals(language.getSupportedHeadingLevel(), cloned.getSupportedHeadingLevel());
		assertEquals(language.getSupportedSpanTypes(), cloned.getSupportedSpanTypes());
		assertEquals(language.getTagNameSubstitutions(), cloned.getTagNameSubstitutions());

	}

	@Test
	public void parseCleansHtmlSetOnClone() {
		HtmlLanguage htmlLanguage = newHtmlSubsetLanguage(BlockType.PARAGRAPH);
		htmlLanguage.setParseCleansHtml(true);
		assertEquals(htmlLanguage.isParseCleansHtml(), htmlLanguage.clone().isParseCleansHtml());
		htmlLanguage.setParseCleansHtml(false);
		assertEquals(htmlLanguage.isParseCleansHtml(), htmlLanguage.clone().isParseCleansHtml());
	}

	@Test
	public void createDocumentBuilder() {
		StringWriter out = new StringWriter();
		HtmlSubsetDocumentBuilder builder = newHtmlSubsetLanguage(BlockType.PARAGRAPH).createDocumentBuilder(out,
				false);
		assertNotNull(builder);
		assertTrue(builder instanceof HtmlSubsetDocumentBuilder);

		builder = newHtmlSubsetLanguage(SpanType.EMPHASIS).createDocumentBuilder(out, false);
		builder.beginSpan(SpanType.EMPHASIS, new Attributes());
		builder.characters("text");
		builder.endSpan();
		assertEquals("<new-em>text</new-em>", out.toString());
	}

	@Test
	public void isXhtmlStrict() {
		assertXhtmlStrict(true);
		assertXhtmlStrict(false);
	}

	@Test
	public void supportsImages() {
		assertSupportsImages(true);
		assertSupportsImages(false);
	}

	private void assertXhtmlStrict(boolean xhtmlStrict) {
		HtmlSubsetLanguage language = createHtmlSubsetLanguage(xhtmlStrict);
		assertEquals(xhtmlStrict, language.isXhtmlStrict());
		assertEquals(xhtmlStrict, language.clone().isXhtmlStrict());
		HtmlSubsetDocumentBuilder documentBuilder = language.createDocumentBuilder(new StringWriter(), false);
		assertEquals(xhtmlStrict, documentBuilder.getDelegate().isXhtmlStrict());
	}

	private void assertSupportsImages(boolean supportsImages) {
		HtmlSubsetLanguage language = createHtmlSubsetLanguage(false, supportsImages);
		assertEquals(supportsImages, language.getSupportsImages());
		assertEquals(supportsImages, language.clone().getSupportsImages());
		HtmlSubsetDocumentBuilder documentBuilder = language.createDocumentBuilder(new StringWriter(), false);
		assertEquals(supportsImages, documentBuilder.getSupportsImages());
	}

	private HtmlSubsetLanguage createHtmlSubsetLanguage(boolean xhtmlStrict) {
		return new HtmlSubsetLanguage("Test", null, 6, Set.of(BlockType.PARAGRAPH), Set.of(), Map.of(),
				Collections.emptyList(), xhtmlStrict, true);
	}

	private HtmlSubsetLanguage createHtmlSubsetLanguage(boolean xhtmlStrict, boolean supportsImages) {
		return new HtmlSubsetLanguage("Test", null, 6, Set.of(BlockType.PARAGRAPH), Set.of(), Map.of(),
				Collections.emptyList(), xhtmlStrict, supportsImages);
	}

	private void assertSupportedHeadingLevel(int level) {
		assertEquals(level, newHtmlSubsetLanguageWithHeadingLevel(level).getSupportedHeadingLevel());
	}

	private HtmlSubsetLanguage newHtmlSubsetLanguageWithHeadingLevel(int level) {
		return new HtmlSubsetLanguage("Test", null, level, new HashSet<>(Arrays.asList(BlockType.PARAGRAPH)), Set.of(),
				Map.of(), Collections.emptyList(), false, true);
	}

	protected HtmlSubsetLanguage newHtmlSubsetLanguage(SpanType... spans) {
		return new HtmlSubsetLanguage("Test", null, 6, new HashSet<>(Arrays.asList(BlockType.PARAGRAPH)),
				new HashSet<>(Arrays.asList(spans)), Map.of(SpanType.EMPHASIS, "new-em"), Collections.emptyList(),
				false, true);
	}

	protected HtmlSubsetLanguage newHtmlSubsetLanguage(BlockType... blocks) {
		return new HtmlSubsetLanguage("Test", null, 6, new HashSet<>(Arrays.asList(blocks)), Set.of(), Map.of(),
				Collections.emptyList(), false, true);
	}

}
