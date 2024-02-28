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

package org.eclipse.mylyn.wikitext.html;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.Set;

import org.eclipse.mylyn.wikitext.html.internal.HtmlSubsetLanguage;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({ "nls", "restriction" })
public class HtmlLanguageBuilderTest {

	private HtmlLanguageBuilder builder;

	@Before
	public void before() {
		builder = new HtmlLanguageBuilder();
	}

	@Test
	public void nameNull() {
		NullPointerException npe = assertThrows(NullPointerException.class, () -> builder.name(null));
		assertTrue(npe.getMessage().contains("Must provide a name"));
	}

	@Test
	public void nameEmpty() {
		IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> builder.name(""));
		assertTrue(iae.getMessage().contains("Name must not be empty"));
	}

	@Test
	public void nameLeadingWhitespace() {
		IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> builder.name(" Name"));
		assertTrue(iae.getMessage().contains("Name must not have leading or trailing whitespace"));
	}

	@Test
	public void nameBlacklisted() {
		IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
				() -> builder.name(HtmlLanguage.NAME_HTML));
		assertTrue(iae.getMessage().contains("Name must not be equal to " + HtmlLanguage.NAME_HTML));
	}

	@Test
	public void nameBlacklisted2() {
		IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
				() -> builder.name(HtmlLanguage.NAME_HTML.toLowerCase()));
		assertTrue(iae.getMessage().contains("Name must not be equal to " + HtmlLanguage.NAME_HTML));
	}

	@Test
	public void nameBlacklisted3() {
		IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
				() -> builder.name(HtmlLanguage.NAME_HTML.toUpperCase()));
		assertTrue(iae.getMessage().contains("Name must not be equal to " + HtmlLanguage.NAME_HTML));
	}

	@Test
	public void name() {
		assertNotNull(builder.name("Test"));
		assertSame(builder, builder.name("Test"));
	}

	@Test
	public void create() {
		HtmlLanguage language = builder.add(BlockType.PARAGRAPH)
				.add(BlockType.CODE)
				.add(SpanType.SUPERSCRIPT)
				.add(SpanType.BOLD)
				.addSubstitution(SpanType.BOLD, "bold")
				.name("Test")
				.create();
		assertNotNull(language);
		assertEquals("Test", language.getName());
		assertTrue(language instanceof HtmlSubsetLanguage);

		HtmlSubsetLanguage subsetLanguage = (HtmlSubsetLanguage) language;
		assertEquals(Set.of(BlockType.PARAGRAPH, BlockType.CODE), subsetLanguage.getSupportedBlockTypes());
		assertEquals(Set.of(SpanType.SUPERSCRIPT, SpanType.BOLD), subsetLanguage.getSupportedSpanTypes());
		assertEquals(0, subsetLanguage.getSupportedHeadingLevel());
	}

	@Test
	public void createWithoutName() {
		IllegalStateException ise = assertThrows(IllegalStateException.class,
				() -> builder.add(BlockType.PARAGRAPH).create());
		assertTrue(ise.getMessage().contains("Name must be provided to create an HtmlLanguage"));
	}

	@Test
	public void createWithoutBlockType() {
		HtmlLanguage language = builder.document("", "").name("Test").create();
		requireNonNull(language);
		requireNonNull(language.createDocumentBuilder(new StringWriter()));
	}

	@Test
	public void addBlockTypeNull() {
		NullPointerException npe = assertThrows(NullPointerException.class, () -> builder.add((BlockType) null));
		assertTrue(npe.getMessage().contains("Must provide a blockType"));
	}

	@Test
	public void addBlockType() {
		assertNotNull(builder.add(BlockType.PARAGRAPH));
		assertSame(builder, builder.add(BlockType.PARAGRAPH));
	}

	@Test
	public void addSpanTypeNull() {
		NullPointerException npe = assertThrows(NullPointerException.class, () -> builder.add((SpanType) null));
		assertTrue(npe.getMessage().contains("Must provide a spanType"));
	}

	@Test
	public void addSpanType() {
		assertNotNull(builder.add(SpanType.BOLD));
		assertSame(builder, builder.add(SpanType.BOLD));
	}

	@Test
	public void addSpanSubstitutionNullAlternative() {
		NullPointerException npe = assertThrows(NullPointerException.class,
				() -> builder.addSubstitution(SpanType.BOLD, (String) null));
		assertTrue(npe.getMessage().contains("Must provide an alternativeTagName"));
	}

	@Test
	public void addSpanSubstitutionNullSpanType() {
		NullPointerException npe = assertThrows(NullPointerException.class,
				() -> builder.addSubstitution((SpanType) null, "bold"));
		assertTrue(npe.getMessage().contains("Must provide a spanType"));
	}

	@Test
	public void addSpanSubstitution() {
		assertNotNull(builder.addSubstitution(SpanType.BOLD, "bold"));
		assertSame(builder, builder.addSubstitution(SpanType.BOLD, "bold"));
	}

	@Test
	public void addHeadings() {
		assertNotNull(builder.addHeadings(1));
		assertSame(builder, builder.addHeadings(1));
	}

	@Test
	public void addHeadingsCreatesExpectedSupportLevel() {
		HtmlSubsetLanguage subsetLanguage = (HtmlSubsetLanguage) builder.add(BlockType.PARAGRAPH)
				.addHeadings(3)
				.name("Test")
				.create();
		assertEquals(3, subsetLanguage.getSupportedHeadingLevel());
	}

	@Test
	public void addHeadingsLowerBounds() {
		builder.addHeadings(1);
		IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> builder.addHeadings(0));
		assertTrue(iae.getMessage().contains("Heading level must be between 1 and 6"));
	}

	@Test
	public void addHeadingsUpperBounds() {
		builder.addHeadings(6);
		IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> builder.addHeadings(7));
		assertTrue(iae.getMessage().contains("Heading level must be between 1 and 6"));
	}

	@Test
	public void document() {
		HtmlSubsetLanguage subsetLanguage = (HtmlSubsetLanguage) builder.add(BlockType.PARAGRAPH)
				.document("<div>", "</div>")
				.name("Test")
				.create();
		StringWriter writer = new StringWriter();
		DocumentBuilder documentBuilder = subsetLanguage.createDocumentBuilder(writer);
		documentBuilder.beginDocument();
		documentBuilder.characters("test");
		documentBuilder.endDocument();
		assertEquals("<div>test</div>", writer.toString());
	}

	@Test
	public void documentNullPrefix() {
		NullPointerException npe = assertThrows(NullPointerException.class, () -> builder.document(null, "ouch"));
		assertTrue(npe.getMessage().contains("Must provide a prefix"));
	}

	@Test
	public void documentNullSuffix() {
		NullPointerException npe = assertThrows(NullPointerException.class, () -> builder.document("ouch", null));
		assertTrue(npe.getMessage().contains("Must provide a suffix"));
	}

	@Test
	public void addSpanFont() {
		StringWriter writer = new StringWriter();
		HtmlLanguage language = builder.addSpanFont().add(BlockType.PARAGRAPH).document("", "").name("Test").create();
		DocumentBuilder builder = language.createDocumentBuilder(writer);
		builder.beginDocument();
		builder.beginSpan(SpanType.SPAN, new Attributes());
		builder.characters("test");
		builder.endSpan();
		builder.characters(" ");
		addSpanWithCssColor(builder);
		builder.endDocument();

		assertEquals("test <font color=\"purple\">inside font</font>", writer.toString());
	}

	@Test
	public void addSpanFontWithSpanSupport() {
		StringWriter writer = new StringWriter();
		HtmlLanguage language = builder.addSpanFont()
				.add(BlockType.PARAGRAPH)
				.add(SpanType.SPAN)
				.document("", "")
				.name("Test")
				.create();
		DocumentBuilder builder = language.createDocumentBuilder(writer);
		builder.beginDocument();
		builder.beginSpan(SpanType.SPAN, new Attributes());
		builder.characters("test");
		builder.endSpan();
		builder.characters(" ");
		addSpanWithCssColor(builder);
		builder.endDocument();

		assertEquals("<span>test</span> <font color=\"purple\">inside font</font>", writer.toString());
	}

	@Test
	public void spanSubstitution() {
		StringWriter writer = new StringWriter();
		HtmlLanguage language = builder.addSpanFont()
				.add(SpanType.DELETED)
				.addSubstitution(SpanType.DELETED, "strike")
				.document("", "")
				.name("Test")
				.create();

		DocumentBuilder builder = language.createDocumentBuilder(writer);
		builder.beginDocument();
		builder.beginSpan(SpanType.DELETED, new Attributes());
		builder.characters("test");
		builder.endSpan();

		assertEquals("<strike>test</strike>", writer.toString());
	}

	@Test
	public void spanToBoldTransformation() {
		StringWriter writer = new StringWriter();
		HtmlLanguage language = builder.add(BlockType.PARAGRAPH)
				.add(SpanType.BOLD)
				.document("", "")
				.name("Test")
				.create();

		DocumentBuilder builder = language.createDocumentBuilder(writer);
		builder.beginDocument();
		addSpanWithCssFontWeightBold(builder);
		builder.characters(" ");
		addSpanWithCssColor(builder);
		builder.endDocument();

		assertEquals("<b>test</b> inside font", writer.toString());
	}

	@Test
	public void spanToStrongTransformation() {
		StringWriter writer = new StringWriter();
		HtmlLanguage language = builder.add(BlockType.PARAGRAPH)
				.add(SpanType.STRONG)
				.document("", "")
				.name("Test")
				.create();

		DocumentBuilder builder = language.createDocumentBuilder(writer);
		builder.beginDocument();
		addSpanWithCssFontWeightBold(builder);
		builder.characters(" ");
		addSpanWithCssColor(builder);
		builder.endDocument();

		assertEquals("<strong>test</strong> inside font", writer.toString());
	}

	@Test
	public void spanToCompositeTransformation() {
		StringWriter writer = new StringWriter();
		HtmlLanguage language = builder.add(BlockType.PARAGRAPH)
				.add(SpanType.BOLD)
				.add(SpanType.MONOSPACE)
				.add(SpanType.EMPHASIS)
				.document("", "")
				.name("Test")
				.create();

		DocumentBuilder builder = language.createDocumentBuilder(writer);
		builder.beginDocument();
		builder.beginSpan(SpanType.SPAN, new Attributes(null, null,
				"font-weight:bold; font-family: courrier, monospace;font-style:italic;unknown: rule", null));
		builder.characters("test");
		builder.endSpan();
		builder.endDocument();

		assertEquals("<b><tt><em>test</em></tt></b>", writer.toString());
	}

	@Test
	public void setXhtmlStrict() {
		assertXhtmlStrict(true);
		assertXhtmlStrict(false);
	}

	@Test
	public void setXhtmlStrictFalseCharsEmittedDirectlyToTheDocumentBodyTwice() {
		assertEmittedCharsEqual(false, "foobar", "foo", "bar");
	}

	@Test
	public void setXhtmlStrictTrueCharsEmittedDirectlyToTheDocumentBodyTwice() {
		assertEmittedCharsEqual(true, "<p>foobar</p>", "foo", "bar");
	}

	@Test
	public void setSupportsImages() {
		assertSupportsImages(true);
		assertSupportsImages(false);
	}

	private void addSpanWithCssFontWeightBold(DocumentBuilder builder) {
		builder.beginSpan(SpanType.SPAN, new Attributes(null, null, "font-weight:bold", null));
		builder.characters("test");
		builder.endSpan();
	}

	private void assertXhtmlStrict(boolean xhtmlStrict) {
		builder.name("Test").add(BlockType.PARAGRAPH);
		assertSame(builder, builder.setXhtmlStrict(xhtmlStrict));

		HtmlSubsetLanguage language = (HtmlSubsetLanguage) builder.create();
		assertEquals(xhtmlStrict, language.isXhtmlStrict());
	}

	private void assertSupportsImages(boolean supportsImages) {
		builder.name("Test").add(BlockType.PARAGRAPH);
		assertSame(builder, builder.setSupportsImages(supportsImages));

		HtmlSubsetLanguage language = (HtmlSubsetLanguage) builder.create();
		assertEquals(supportsImages, language.getSupportsImages());
	}

	private void addSpanWithCssColor(DocumentBuilder builder) {
		builder.beginSpan(SpanType.SPAN, new Attributes(null, null, "color: purple", null));
		builder.characters("inside font");
		builder.endSpan();
	}

	private void assertEmittedCharsEqual(boolean xhtmlStrict, String expected, String... chars) {
		StringWriter writer = new StringWriter();
		DocumentBuilder builder = newDocumentBuilder(writer, xhtmlStrict);

		builder.beginDocument();
		for (String characters : chars) {
			builder.characters(characters);
		}
		builder.endDocument();

		assertEquals(expected, writer.toString());
	}

	private DocumentBuilder newDocumentBuilder(StringWriter writer, boolean xhtmlStrict) {
		return builder.add(BlockType.PARAGRAPH)
				.setXhtmlStrict(xhtmlStrict)
				.document("", "")
				.name("Test")
				.create()
				.createDocumentBuilder(writer);
	}

}
