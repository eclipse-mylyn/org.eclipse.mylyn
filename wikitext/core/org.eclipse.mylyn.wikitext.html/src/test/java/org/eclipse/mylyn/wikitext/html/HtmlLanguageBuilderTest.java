/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.wikitext.html;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.html.internal.HtmlSubsetLanguage;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableSet;

public class HtmlLanguageBuilderTest {

	private HtmlLanguageBuilder builder;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void before() {
		builder = new HtmlLanguageBuilder();
	}

	@Test
	public void nameNull() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide a name");
		builder.name(null);
	}

	@Test
	public void nameEmpty() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Name must not be empty");
		builder.name("");
	}

	@Test
	public void nameLeadingWhitespace() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Name must not have leading or trailing whitespace");
		builder.name(" Name");
	}

	@Test
	public void nameBlacklisted() {
		expectBlacklisted();
		builder.name(HtmlLanguage.NAME_HTML);
	}

	@Test
	public void nameBlacklisted2() {
		expectBlacklisted();
		builder.name(HtmlLanguage.NAME_HTML.toLowerCase());
	}

	@Test
	public void nameBlacklisted3() {
		expectBlacklisted();
		builder.name(HtmlLanguage.NAME_HTML.toUpperCase());
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
		assertEquals(ImmutableSet.of(BlockType.PARAGRAPH, BlockType.CODE), subsetLanguage.getSupportedBlockTypes());
		assertEquals(ImmutableSet.of(SpanType.SUPERSCRIPT, SpanType.BOLD), subsetLanguage.getSupportedSpanTypes());
		assertEquals(0, subsetLanguage.getSupportedHeadingLevel());
	}

	@Test
	public void createWithoutName() {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Name must be provided to create an HtmlLanguage");
		builder.add(BlockType.PARAGRAPH).create();
	}

	@Test
	public void createWithoutBlockType() {
		HtmlLanguage language = builder.document("", "").name("Test").create();
		requireNonNull(language);
		requireNonNull(language.createDocumentBuilder(new StringWriter()));
	}

	@Test
	public void addBlockTypeNull() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide a blockType");
		builder.add((BlockType) null);
	}

	@Test
	public void addBlockType() {
		assertNotNull(builder.add(BlockType.PARAGRAPH));
		assertSame(builder, builder.add(BlockType.PARAGRAPH));
	}

	@Test
	public void addSpanTypeNull() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide a spanType");
		builder.add((SpanType) null);
	}

	@Test
	public void addSpanType() {
		assertNotNull(builder.add(SpanType.BOLD));
		assertSame(builder, builder.add(SpanType.BOLD));
	}

	@Test
	public void addSpanSubstitutionNullAlternative() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide an alternativeTagName");
		builder.addSubstitution(SpanType.BOLD, (String) null);
	}

	@Test
	public void addSpanSubstitutionNullSpanType() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide a spanType");
		builder.addSubstitution((SpanType) null, "bold");
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
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Heading level must be between 1 and 6");
		builder.addHeadings(0);
	}

	@Test
	public void addHeadingsUpperBounds() {
		builder.addHeadings(6);
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Heading level must be between 1 and 6");
		builder.addHeadings(7);
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
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide a prefix");
		builder.document(null, "ouch");
	}

	@Test
	public void documentNullSuffix() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Must provide a suffix");
		builder.document("ouch", null);
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

	protected void expectBlacklisted() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Name must not be equal to " + HtmlLanguage.NAME_HTML);
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
