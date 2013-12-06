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

package org.eclipse.mylyn.wikitext.html.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.internal.wikitext.html.core.HtmlSubsetLanguage;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
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
				.name("Test")
				.create();
		assertNotNull(language);
		assertEquals("Test", language.getName());
		assertTrue(language instanceof HtmlSubsetLanguage);

		HtmlSubsetLanguage subsetLanguage = (HtmlSubsetLanguage) language;
		assertEquals(ImmutableSet.of(BlockType.PARAGRAPH, BlockType.CODE), subsetLanguage.getSupportedBlockTypes());
		assertEquals(ImmutableSet.of(SpanType.SUPERSCRIPT), subsetLanguage.getSupportedSpanTypes());
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
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Must provide support for at least one block type");
		builder.name("Test").create();
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

	protected void expectBlacklisted() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Name must not be equal to " + HtmlLanguage.NAME_HTML);
	}

}
